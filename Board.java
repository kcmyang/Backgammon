import java.awt.*;
import java.io.Serializable;
import java.util.*;

/**
 * Class representing the board of the game.
 *
 * Mostly composed of numbers and flags and logic, but corresponds directly with the dimensions of BoardPanel.
 */
public class Board implements Serializable {

	public enum Color {
		BLACK, WHITE, NONE
	}

	public enum Event {
		 BLACK_DICE_ROLL, BLACK_MOVE, BLACK_END_TURN, WHITE_DICE_ROLL, WHITE_MOVE, WHITE_END_TURN
	}

	/*
	 * Variable Dictionary
	 *
	 * LOOP                  - list of game states
	 *
	 * currentGameState      - index within LOOP representing the current game state
	 * eventString           - string to display on board for current game state
	 * boardTextImage        - image key of the board's text (pip numbering)
	 *
	 * allPips               - instance of container class of all pips on the board
	 * dice                  - instance of dice object
	 *
	 * hitboxes              - list of all hitboxes in the board
	 * mousePos              - location of the mouse (point (X, Y) relative to the boardPanel, corresponds to the board directly)
	 * mouseClick            - flag indicating if the mouse is currently clicked
	 *
	 * hitboxHovered         - pointer to the hitbox currently hovered
	 * hitboxClicked         - pointer to the hitbox currently clicked
	 * targetPip             - pointer to the pip currently selected by mouse
	 * targetMoves           - list of moves to the targetPip
	 */

    public static final LinkedList<Event> LOOP = generateGameLoop();

    private int currentGameState;
	private String eventString = "";
	private Images.k boardTextImage;
	
	private AllPips allPips;
	private Dice dice;

	private ArrayList<Hitbox> hitboxes;
	private Point mousePos;
	private boolean mouseClick;

    private Hitbox hitboxHovered, hitboxClicked;
	private Pip targetPip;
	private LinkedList<Move> targetMoves;

	public Board() {
		super();
	}

    /**
     * Constructs a new board. (AI functionality currently not implemented)
     *
     * @param black whether black is player or AI
     * @param white whether black is player or AI
     */
	public Board(boolean black, boolean white) {
		allPips = new AllPips();

		dice = new Dice();
		dice.getHitbox().setHoverable(true);
		dice.getHitbox().setClickable(true);

        // Initialise hitbox list
		hitboxes = new ArrayList<>();
        hitboxes.add(dice.getHitbox());
        for (Pip p : allPips.getAsArrayList())
            hitboxes.add(p.getHitbox());

        targetPip = null;
        targetMoves = null;

        // Start the game
        currentGameState = 0;
        prepareGameState(currentGameState);
	}

    /**
     * Prepares the game loop. Game states are ordered in the list.
     *
     * @return the game loop
     */
	private static LinkedList<Event> generateGameLoop(){
		LinkedList<Event> out = new LinkedList<>();
		out.add(Event.BLACK_DICE_ROLL);
		out.add(Event.BLACK_MOVE);
		out.add(Event.BLACK_END_TURN);
		out.add(Event.WHITE_DICE_ROLL);
		out.add(Event.WHITE_MOVE);
		out.add(Event.WHITE_END_TURN);
		
		return out;
	}

    /**
     * Main game logic. Ensures all events of current game state are executed before proceeding to the next state.
     */
    public void updateGame() {
        boolean proceedToNextState = false;

        // Perform state-specific checks
        switch (LOOP.get(currentGameState)) {

            case BLACK_DICE_ROLL:
            case WHITE_DICE_ROLL:
                proceedToNextState = !dice.getHitbox().isClickable();
                break;

            case BLACK_MOVE:
                if (getWinner() == Color.BLACK) {
                    eventString = "BLACK WINS!!!";
                } else {
                    proceedToNextState = dice.size() == 0 || !allPips.hasPossibleMoves();
                }
                break;

            case WHITE_MOVE:
                if (getWinner() == Color.WHITE) {
                    eventString = "WHITE WINS!!!";
                } else {
                    proceedToNextState = dice.size() == 0 || !allPips.hasPossibleMoves();
                }
                break;

            case BLACK_END_TURN:
                if (getWinner() == Color.BLACK) {
                    eventString = "BLACK WINS!!!";
                } else {
                    proceedToNextState = true;
                }
                break;

            case WHITE_END_TURN:
                if (getWinner() == Color.WHITE) {
                    eventString = "WHITE WINS!!!";
                } else {
                    proceedToNextState = true;
                }
                break;

        }

        // Stay on current state until exit condition met
        prepareGameState(proceedToNextState ? (currentGameState = (currentGameState + 1) % LOOP.size()) : currentGameState);
    }

    /**
     * Do preparations for the specified game state.
     *
     * @param gameState game state
     */
	public void prepareGameState(int gameState) {
		switch (LOOP.get(gameState)) {

            case BLACK_DICE_ROLL:
                eventString = "ROLL DICE";
                boardTextImage = Images.k.BOARD_TEXT_BLACK;
                dice.getHitbox().setHoverable(true);
                dice.getHitbox().setClickable(true);
                break;

            case WHITE_DICE_ROLL:
                eventString = "ROLL DICE";
                boardTextImage = Images.k.BOARD_TEXT_WHITE;
                dice.getHitbox().setHoverable(true);
                dice.getHitbox().setClickable(true);
                break;

            case BLACK_MOVE:
                eventString = "BLACK'S TURN";
                dice.getHitbox().setHoverable(false);
                dice.getHitbox().setClickable(false);
                allPips.setAllHitboxes(true);
                allPips.calculatePossibleMoves(dice.getAllDiceCombinations(), Color.BLACK);
                break;

            case WHITE_MOVE:
                eventString = "WHITE'S TURN";
                dice.getHitbox().setHoverable(false);
                dice.getHitbox().setClickable(false);
                allPips.setAllHitboxes(true);
                allPips.calculatePossibleMoves(dice.getAllDiceCombinations(), Color.WHITE);
                break;

            case BLACK_END_TURN:
                setHitboxes(false);
                break;

            case WHITE_END_TURN:
                setHitboxes(false);
                break;
		}
	}

    /**
     * Draws the board. Assumes the component on which the board is being drawn is exactly the size of the board.
     *
     * @param g the graphics environment
     */
	public void draw(Graphics g) {
		// Board (background)
		g.drawImage(Images.getImage(Images.k.BOARD), 0, 0, Images.BOARD_WIDTH, Images.BOARD_HEIGHT, null);

		// Text (pip labels)
        g.drawImage(Images.getImage(boardTextImage), 0, 0, Images.BOARD_WIDTH, Images.BOARD_HEIGHT, null);

		// Pips
		allPips.draw(g);

		// Dice
		dice.draw(g);

        // Event String
        g.setFont(new Font("Franklin Gothic", Font.BOLD, 32));
        g.setColor(new java.awt.Color(76, 66, 32));
        g.drawString(eventString, 40, 326);
	}

    /**
     * Sets the position of the mouse.
     *
     * @param p position
     */
	public void setMousePos(Point p) {
		mousePos = p;
		handleMouse();
	}

    /**
     * Sets the click state of the mouse
     *
     * @param c click
     */
	public void setMouseClick(boolean c) {
		mouseClick = c;
	}

    /**
     * Called from BoardPanel when the mouse is moved or clicked. Updates flags and then handles special logic. (Custom handling of mouse "events")
     */
	public void handleMouse() {
		// Reset pointers
		hitboxHovered = null;
		hitboxClicked = null;

		// Check for mouse collisions and update hitboxes and pointers accordingly
		for (Hitbox h : hitboxes) {

		    // If hovered
			if (h.checkHover(mousePos)) {
				hitboxHovered = h;

				// Set click
				h.setClick(mouseClick && h.isHovered() && h.isClickable());

				// If clicked
				if (h.isClicked()) {
					hitboxClicked = h;
				}
			}
		}

		// Reset flag
        mouseClick = false;

		// Exit if game is over
		if (getWinner() != Color.NONE)
		    return;

        // Do the logic
		mouseLogic();

		// Check game state and update game loop
		this.updateGame();
	}

    /**
     * Called when the user wants to cycle through potential moves. (Currently mapped to right click)
     */
	public void handleMoveCycle() {
        cycleThroughPotentialMoves();
    }

    /**
     * Cycles through the potential moves.
     */
    private void cycleThroughPotentialMoves() {
        if (targetMoves == null || targetMoves.isEmpty()) {
            allPips.highlightPossibleEnds();
            return;
        }

        // Iterate
        targetMoves.addLast(targetMoves.pop());
        allPips.highlightMove(targetMoves.peek());
    }

    /**
     * Given mouse input, performs any logic which is not self-contained in a hitbox object.
     */
    public void mouseLogic() {
        if (hitboxHovered == null || hitboxHovered == dice.getHitbox())
            return;

        // Perform target move updates if currently hovered pip changes
        Pip targetPip2 = allPips.getAssociatedPip(hitboxHovered, true);

        if (targetPip2 != targetPip) {
            targetPip = targetPip2;

            // Get list of possible moves to the hovered pip
            HashSet<Move> temp = allPips.getPossibleMovesTo(targetPip);

            if (temp == null)
                targetMoves = new LinkedList<>();
            else
                targetMoves = new LinkedList<>(temp);

            cycleThroughPotentialMoves();
        }

        /* Process click */

        // Exit if no click or no target moves (invalid move)
        if (targetMoves.isEmpty() || hitboxClicked == null)
            return;

        // Move the stone
        allPips.executeMove(targetMoves.peek());
        targetPip = null;

        // Update dice object ("use up" associated dice)
        dice.removeDiceInMove(targetMoves.peek());

        // Reset all graphics in allPips
        allPips.highlightNothing();
    }

    /**
     * Gets the color of the winning player.
     *
     * @return color of the winner
     */
    public Board.Color getWinner() {
    	return allPips.checkWinner();
    }

    /**
     * Checks whether the game is over (by game logic).
     *
     * @return whether game is over
     */
    public boolean getGameOver() {
        return getWinner() != Color.NONE;
    }

    /**
     * Enables or disables all hitboxes.
     *
     * @param set hitbox state (true = enabled, false = disabled)
     */
    public void setHitboxes(boolean set) {
    	allPips.setAllHitboxes(set);
    }
}
