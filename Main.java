import javax.swing.SwingUtilities;

/** Run with:  java Main */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VisualizerFrame().setVisible(true));
    }
}
