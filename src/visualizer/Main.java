package visualizer;

import javax.swing.SwingUtilities;
import visualizer.ui.VisualizerFrame;

/** Desktop entry point. Run with:  java -cp out visualizer.Main */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VisualizerFrame().setVisible(true));
    }
}
