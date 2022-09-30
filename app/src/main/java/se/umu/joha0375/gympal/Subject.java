package se.umu.joha0375.gympal;

/**
 * The subject interface in the observer pattern that will be implemented
 * by any object that is observable.
 */
public interface Subject {

    /**
     * Register an observer to the subject.
     * @param repositoryObserver - the observer that will be registered.
     */
    void registerObserver(RepositoryObserver repositoryObserver);

    /**
     * Removes an observer from the subject(not used right now).
     * @param repositoryObserver - the observer that is being removed,
     */
    void removeObserver(RepositoryObserver repositoryObserver);

    /**
     * Notifies observers that a changed has been made.
     */
    void notifyObservers();
}
