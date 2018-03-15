package fi.hamk.calmfulnessV2.azure;

import java.util.ArrayList;

/**
 * List for containing indexes of visited exercises
 */
public abstract class VisitedList {

    /**
     * ArrayList where the indexes of visited exercises are stored
     */
    private static ArrayList<Integer> visited;

    /**
     * Initialize the list
     */
    public static void initialize() {
        visited = new ArrayList<>();
    }

    /**
     * @return ArrayList containing indexes of visited exercises
     */
    public static ArrayList<Integer> getVisited() {
        return visited;
    }

    /**
     * Add the visited index to the ArrayList
     *
     * @param visited Index of the visited exercise
     */
    public static void addVisited(final int visited) {
        VisitedList.visited.add(visited);
    }

    /**
     * Clear the list of visited indexes
     */
    public static void clearVisited() {
        VisitedList.visited.clear();
    }

    /**
     * @return Return <tt>true</tt> if list is not initialized, otherwise return <tt>false</tt>
     */
    public static boolean isNull() {
        return visited == null;
    }
}
