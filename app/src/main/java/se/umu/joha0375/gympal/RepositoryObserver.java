package se.umu.joha0375.gympal;

/**
 * The observer interface, that an observer object implements.
 */
public interface RepositoryObserver {
    /**
     * This method will be executed by the observed object to notify the observer,
     * that the distance has changed.
     * @param distance the changed distance.
     */
    void onDistanceUpdate(float distance);
}
