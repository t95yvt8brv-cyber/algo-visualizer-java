package visualizer.playback;

import java.util.List;

/**
 * Decides the result of a race between players run on the same input. Every frame takes the
 * same time on screen, so the player with fewer frames finishes first; ties are broken by
 * the total work (comparisons + writes).
 */
public final class Race {
    private Race() { }

    /** Text describing who is ahead or who won, e.g. "Merge Sort wins: 180 steps vs 560". */
    public static String result(List<Player> players) {
        if (players.size() < 2) return "";
        Player a = players.get(0), b = players.get(1);
        if (!a.isFinished() && !b.isFinished()) return "Race: " + a.getAlgorithm() + " vs " + b.getAlgorithm();
        if (a.isFinished() != b.isFinished()) {
            Player done = a.isFinished() ? a : b;
            return done.getAlgorithm() + " finished first, after " + done.length() + " steps";
        }
        int byLength = Integer.compare(a.length(), b.length());
        int byWork = Integer.compare(work(a), work(b));
        int order = byLength != 0 ? byLength : byWork;
        String detail = a.length() + " vs " + b.length() + " steps, "
                + a.last().getComparisons() + " vs " + b.last().getComparisons() + " comparisons, "
                + a.last().getWrites() + " vs " + b.last().getWrites() + " writes";
        if (order == 0) return "Tie (" + detail + ")";
        Player winner = order < 0 ? a : b;
        return winner.getAlgorithm() + " wins (" + detail + ")";
    }

    private static int work(Player p) {
        return p.last().getComparisons() + p.last().getWrites();
    }
}
