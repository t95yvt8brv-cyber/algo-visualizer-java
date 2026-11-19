package visualizer.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import visualizer.algorithms.Algorithm;
import visualizer.algorithms.AlgorithmFactory;
import visualizer.model.ArrayModel;
import visualizer.playback.Player;
import visualizer.playback.Race;

/**
 * The window (the controller). It only talks to the Algorithm type, so it works with any algorithm
 * without knowing which one it is (polymorphism). Each algorithm's steps are recorded first and
 * played by a Player; AlgorithmViews observe the Players. Choosing a second algorithm in the
 * "vs" box races the two on the same array, driven by one shared clock.
 */
public class VisualizerFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String NO_RACE = "(no race)";

    private final transient ArrayModel model = new ArrayModel();
    private final JComboBox<String> algoBox = new JComboBox<>(AlgorithmFactory.names());
    private final JComboBox<String> raceBox = new JComboBox<>();
    private final JComboBox<ArrayModel.Kind> kindBox = new JComboBox<>(ArrayModel.Kind.values());
    private final JSlider sizeSlider = new JSlider(5, 100, 30);
    private final JSlider speedSlider = new JSlider(1, 100, 40);
    private final JTextField targetField = new JTextField(4);
    private final JButton startButton = new JButton("Start");
    private final JPanel viewsPanel = new JPanel(new GridLayout(0, 1, 0, 4));
    private final JLabel statusLabel = new JLabel(" ");
    private final Timer timer;

    private transient List<Player> players = new ArrayList<>();
    private final transient List<AlgorithmView> views = new ArrayList<>();
    private int time;                      // shared clock: every player shows frame min(time, its length)
    private int[] startData;
    private Integer target;                // null for sorts
    private boolean fillingRaceBox;

    public VisualizerFrame() {
        super("Algorithm Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        timer = new Timer(delay(), e -> tick());
        buildLayout();
        fillRaceBox();
        wireEvents();
        model.generate(ArrayModel.Kind.RANDOM, sizeSlider.getValue());
        reset();
        pack();
        setLocationRelativeTo(null);
    }

    // ---------- layout ----------
    private void buildLayout() {
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        row1.add(algoBox);
        row1.add(new JLabel("vs"));
        row1.add(raceBox);
        row1.add(kindBox);
        row1.add(new JLabel("Size"));
        row1.add(sizeSlider);
        row1.add(new JLabel("Speed"));
        row1.add(speedSlider);
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        row2.add(new JLabel("Target:"));
        row2.add(targetField);
        row2.add(startButton);
        row2.add(button("Prev", e -> step(-1)));
        row2.add(button("Next", e -> step(+1)));
        row2.add(button("Reset", e -> rewind()));
        row2.add(button("New array", e -> newArray()));
        JPanel controls = new JPanel(new GridLayout(2, 1));
        controls.add(row1);
        controls.add(row2);

        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 13f));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));

        add(controls, BorderLayout.NORTH);
        add(viewsPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private static JButton button(String text, ActionListener action) {
        JButton b = new JButton(text);
        b.addActionListener(action);
        return b;
    }

    private void wireEvents() {
        algoBox.addActionListener(e -> { fillRaceBox(); reset(); });
        raceBox.addActionListener(e -> { if (!fillingRaceBox) reset(); });
        kindBox.addActionListener(e -> newArray());
        sizeSlider.addChangeListener(e -> { if (!sizeSlider.getValueIsAdjusting()) newArray(); });
        speedSlider.addChangeListener(e -> timer.setDelay(delay()));
        startButton.addActionListener(e -> startPause());
        // Re-record as soon as the target changes, so Start always searches for what is typed.
        targetField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { SwingUtilities.invokeLater(VisualizerFrame.this::reset); }
            @Override public void removeUpdate(DocumentEvent e)  { SwingUtilities.invokeLater(VisualizerFrame.this::reset); }
            @Override public void changedUpdate(DocumentEvent e) { }
        });
    }

    /** The "vs" box only offers algorithms of the same kind (sort vs sort, search vs search). */
    private void fillRaceBox() {
        fillingRaceBox = true;
        Object previous = raceBox.getSelectedItem();
        String first = (String) algoBox.getSelectedItem();
        boolean search = AlgorithmFactory.create(first).needsTarget();
        raceBox.removeAllItems();
        raceBox.addItem(NO_RACE);
        for (String name : AlgorithmFactory.names()) {
            if (!name.equals(first) && AlgorithmFactory.create(name).needsTarget() == search) raceBox.addItem(name);
        }
        if (previous != null) raceBox.setSelectedItem(previous);   // kept only if still offered
        fillingRaceBox = false;
    }

    private int delay() {
        return Math.max(1, 600 / speedSlider.getValue());
    }

    // ---------- recording ----------
    void newArray() {
        model.generate((ArrayModel.Kind) kindBox.getSelectedItem(), sizeSlider.getValue());
        reset();
    }

    /** Records the chosen algorithm(s) on the current array and shows the starting frame. */
    void reset() {
        timer.stop();
        List<Algorithm> algorithms = new ArrayList<>();
        algorithms.add(AlgorithmFactory.create((String) algoBox.getSelectedItem()));
        String rival = (String) raceBox.getSelectedItem();
        if (rival != null && !rival.equals(NO_RACE)) algorithms.add(AlgorithmFactory.create(rival));

        startData = model.copy();
        if (algorithms.stream().anyMatch(Algorithm::needsSorted)) Arrays.sort(startData);   // same input for both
        boolean search = algorithms.get(0).needsTarget();
        targetField.setEnabled(search);
        target = search ? chooseTarget() : null;

        players = new ArrayList<>();
        for (Algorithm a : algorithms) {
            Player p = new Player(a, startData, target == null ? 0 : target);
            p.addListener(x -> updateStatus());
            players.add(p);
        }
        showViews(players.size());
        for (int i = 0; i < players.size(); i++) views.get(i).attach(players.get(i));
        time = 0;
        startButton.setText("Start");
        updateStatus();
    }

    private int chooseTarget() {
        try {
            return Integer.parseInt(targetField.getText().trim());
        } catch (NumberFormatException e) {
            return startData[new Random().nextInt(startData.length)];   // pick a value that is present
        }
    }

    /** One view per player; bars are shorter in race mode so both fit. */
    private void showViews(int count) {
        if (views.size() == count) return;
        views.clear();
        viewsPanel.removeAll();
        for (int i = 0; i < count; i++) {
            AlgorithmView v = new AlgorithmView(count == 1 ? 360 : 210);
            views.add(v);
            viewsPanel.add(v);
        }
        viewsPanel.revalidate();
        if (isDisplayable()) pack();
    }

    // ---------- playback (one clock for every player) ----------
    private int longest() {
        int max = 0;
        for (Player p : players) max = Math.max(max, p.length());
        return max;
    }

    private boolean finished() { return time >= longest(); }

    private void seekAll() {
        for (Player p : players) p.seek(time);
    }

    void startPause() {
        if (timer.isRunning()) {
            timer.stop();
            startButton.setText("Resume");
            return;
        }
        if (finished()) rewind();                    // finished: play again from the start
        startButton.setText("Pause");
        timer.start();
    }

    /** Called by the timer. */
    private void tick() {
        if (!finished()) {
            time++;
            seekAll();
        }
        if (finished()) {
            timer.stop();
            startButton.setText("Start");
        }
    }

    void step(int direction) {
        timer.stop();
        startButton.setText("Resume");
        time = Math.max(0, Math.min(longest(), time + direction));
        seekAll();
    }

    /** Replays the same recording from the start (same array, same target). */
    void rewind() {
        timer.stop();
        time = 0;
        seekAll();
        startButton.setText("Start");
    }

    private void updateStatus() {
        String text = target == null ? "" : "Target: " + target + "      ";
        text += players.size() > 1 ? Race.result(players) : "";
        statusLabel.setText(text.isEmpty() ? " " : text);
    }

    // ---------- used by tools that drive the window (e.g. screenshots) ----------
    void select(String algorithmName) { algoBox.setSelectedItem(algorithmName); }
    void race(String algorithmName)   { raceBox.setSelectedItem(algorithmName); }
}
