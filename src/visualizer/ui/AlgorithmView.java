package visualizer.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import visualizer.model.Step;
import visualizer.playback.PlaybackListener;
import visualizer.playback.Player;

/**
 * Everything shown for one algorithm: title, bars, counters, message and pseudocode.
 * It is an observer of a Player and redraws itself whenever the Player moves.
 * Normal mode shows one AlgorithmView, race mode shows two.
 */
public class AlgorithmView extends JPanel implements PlaybackListener {
    private static final long serialVersionUID = 1L;

    private final JLabel title = new JLabel(" ");
    private final ArrayPanel bars;
    private final PseudocodePanel code = new PseudocodePanel();
    private final JLabel stats = new JLabel(" ");
    private final JLabel note = new JLabel(" ");

    public AlgorithmView(int barHeight) {
        super(new BorderLayout(6, 2));
        bars = new ArrayPanel(barHeight);
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(stats);
        info.add(note);
        add(title, BorderLayout.NORTH);
        add(bars, BorderLayout.CENTER);
        add(code, BorderLayout.EAST);
        add(info, BorderLayout.SOUTH);
        setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
    }

    /** Starts showing this player. The view registers itself as the player's listener. */
    public void attach(Player player) {
        title.setText(player.getAlgorithm().getName() + ":  " + player.getAlgorithm().complexity());
        code.setCode(player.getAlgorithm().pseudocode());
        player.addListener(this);
    }

    @Override
    public void frameChanged(Player player) {
        Step s = player.current();
        bars.show(s);
        code.highlight(s.getLine());
        stats.setText("Comparisons: " + s.getComparisons() + "    Writes: " + s.getWrites()
                + "    Step " + player.position() + " / " + player.length()
                + (player.isFinished() ? "    (done)" : ""));
        note.setText(player.isAtStart() ? "Press Start" : s.getNote());
    }
}
