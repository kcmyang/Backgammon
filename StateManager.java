import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Class allowing for state/view switching in the application.
 * Allows for better data encapsulation in JFrame- and JPanel-extending classes.
 * This class is essentially a smart list which behaves similarly to browser history.
 */
public class StateManager extends LinkedList<StateManager.ID> {

    /* IDs of the states */
    public enum ID { MAIN_MENU, GAME, INSTRUCTIONS }

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Constructs an empty state manager.
     */
    public StateManager() {
        super();
    }

    /**
     * Adds a property change listener.
     *
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a property change listener
     *
     * @param listener the listener to add
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Sets the desired state to switch to.
     *
     * @param state desired state
     */
    public void changeToState(ID state) {
        if (this.peek() == state)
            return;

        this.push(state);

        propertyChangeSupport.firePropertyChange("STATE_UPDATE", 0, 1); //arbitrary values but must be different
    }

    /**
     * Switches to the previous state.
     */
    public void returnToPreviousState() {
        this.pop();

        propertyChangeSupport.firePropertyChange("STATE_UPDATE", 0, 1); //arbitrary values but must be different
    }

}
