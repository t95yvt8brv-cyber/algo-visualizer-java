package visualizer.playback;

/**
 * Observer pattern: anything that wants to know when a Player moves to another frame.
 * The bars, the counters and the pseudocode panel all listen; the Player doesn't know who they are.
 */
@FunctionalInterface
public interface PlaybackListener {
    void frameChanged(Player player);
}
