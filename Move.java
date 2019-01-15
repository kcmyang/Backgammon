import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class representing a move.
 */
public class Move implements Serializable {
	private int startPip, endPip;
	private ArrayList<Die> diceToUse;

    /**
     * Constructs a new move.
     *
     * @param start number of starting pip
     * @param end number of ending pip
     * @param diceToUse list of dice to use in this move (can be more than 1 dice)
     */
	public Move(int start, int end, ArrayList<Die> diceToUse) {
		startPip = start;
		endPip = end;
		this.diceToUse = diceToUse;
	}

    /**
     * Gets the starting pip number.
     *
     * @return the number
     */
	public int getStartPip() {
	    return startPip;
    }

    /**
     * Gets the ending pip number.
     *
     * @return the number
     */
    public int getEndPip() {
	    return endPip;
    }

    /**
     * Gets the list of dice to use.
     *
     * @return the list of dice
     */
    public ArrayList<Die> getDiceToUse() {
	    return diceToUse;
    }
}
