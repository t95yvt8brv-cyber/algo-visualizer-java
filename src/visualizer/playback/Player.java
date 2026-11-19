package visualizer.playback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import visualizer.algorithms.Algorithm;
import visualizer.model.Role;
import visualizer.model.Step;

/**
 * Plays back one algorithm's recorded steps: next, previous, rewind (the subject in the
 * Observer pattern). Plain Java with no Swing, so the desktop window, race mode and the
 * tests can all use it.
 */
public class Player {
    private final Algorithm algorithm;
    private final List<Step> steps;
    private final Step start;                       // the untouched array, shown before step 1
    private final List<PlaybackListener> listeners = new ArrayList<>();
    private int index = -1;                         // -1 = showing `start`

    public Player(Algorithm algorithm, int[] data, int target) {
        this.algorithm = algorithm;
        this.steps = algorithm.run(data, target);
        Role[] normal = new Role[data.length];
        Arrays.fill(normal, Role.NORMAL);
        this.start = new Step(data, normal, "", 0, 0, 0);
    }

    public void addListener(PlaybackListener listener) {
        listeners.add(listener);
        listener.frameChanged(this);                // show the current frame straight away
    }

    /** Moves one frame forward. Returns false if it was already at the end. */
    public boolean next() {
        if (isFinished()) return false;
        index++;
        notifyListeners();
        return true;
    }

    /** Moves one frame back. Returns false if it was already at the start. */
    public boolean previous() {
        if (index < 0) return false;
        index--;
        notifyListeners();
        return true;
    }

    public void rewind() {
        seek(0);
    }

    /** Jumps to a position (0 = the start frame). Past the end it stays on the last frame. */
    public void seek(int position) {
        int newIndex = Math.max(0, Math.min(position, steps.size())) - 1;
        if (newIndex == index) return;
        index = newIndex;
        notifyListeners();
    }

    private void notifyListeners() {
        for (PlaybackListener l : listeners) l.frameChanged(this);
    }

    public Algorithm getAlgorithm() { return algorithm; }
    public Step current()           { return index < 0 ? start : steps.get(index); }
    public Step last()              { return steps.get(steps.size() - 1); }
    public int position()           { return index + 1; }          // 0 = the start frame
    public int length()             { return steps.size(); }
    public boolean isAtStart()      { return index < 0; }
    public boolean isFinished()     { return index >= steps.size() - 1; }
}
