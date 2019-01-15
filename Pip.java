import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Class representing a pip.
 */
public class Pip implements Serializable {

    // Useful constants (unique codes)
    public static final int BLACK_HOME = 25, WHITE_HOME = 0, BLACK_BAR = -1, WHITE_BAR = 26;

    /*
     * Variable Dictionary
     *
     * number                - unique code of this pip (1-24) or one of the class constants
     * drawY                 - y from which to draw
     * drawHeight            - vector height
     * x                     - x
     * y                     - y
     * w                     - width
     * h                     - scalar height
     *
     * stones                - list of stones on this pip
     *
     * isPointingUp          - whether the pip is "oriented up" (default state)
     * highlightStart        - whether the pip is to be highlighted as a beginning
     * highlightIntermediate - whether the pip is to be highlighted as an intermediate
     * highlightEnd          - whether the pip is to be highlighted as an end
     * imageMain             - image key of the pip's main graphic (if any)
     * imageHighlightHard    - image key of the dark highlight
     * imageHighlightSoft    - image key of the light highlight
     * hitbox                - hitbox of this object
     */
    private int number, drawY, drawHeight;
    private int x, y, w = Images.PIP_WIDTH, h = Images.PIP_HEIGHT;

    private LinkedList<Stone> stones;

    private boolean isPointingUp, highlightStart, highlightIntermediate, highlightEnd;
    private Images.k imageMain, imageHighlightHard, imageHighlightSoft;
    private Hitbox hitbox;

    public Pip() {
        this(Integer.MIN_VALUE);
    }

    /**
     * Constructs a new pip with the given number.
     *
     * @param number number of pip
     */
    public Pip(int number) {
        this.number = number;

        this.stones = new LinkedList<>();
        this.highlightStart = false;
        this.highlightIntermediate = false;
        this.highlightEnd = false;

        // Update orientation
        if (BLACK_BAR <= number && number <= 12)
            isPointingUp = true;
        else if (13 <= number && number <= WHITE_BAR)
            isPointingUp = false;

        // Set image keys
        imageMain = null;
        imageHighlightHard = null;
        imageHighlightSoft = null;

        if (number == BLACK_HOME || number == WHITE_HOME) {
            imageHighlightHard = Images.k.HOME_HIGHLIGHT;
        }

        else if (1 <= number && number <= 24) {
            if (number % 2 == 1)
                imageMain = Images.k.PIP_BLACK;
            else
                imageMain = Images.k.PIP_WHITE;

            imageHighlightHard = Images.k.PIP_HIGHLIGHT_HARD;
            imageHighlightSoft = Images.k.PIP_HIGHLIGHT_SOFT;
        }

        // Update position
        updateHitbox();
    }

    /**
     * Adds a stone.
     *
     * @param stone the stone
     */
    public void addStone(Stone stone) {
        stones.add(stone);
    }

    /**
     * Removes the top stone.
     *
     * @return the top stone
     */
    public Stone popStone() {
        return stones.pop();
    }

    /**
     * Sets the start highlight.
     *
     * @param set state
     */
    public void setHighlightStart(boolean set) {
        highlightStart = set;
    }

    /**
     * Sets the intermediate highlight.
     *
     * @param set state
     */
    public void setHighlightIntermediate(boolean set) {
        highlightIntermediate = set;
    }

    /**
     * Sets the end highlight.
     *
     * @param set state
     */
    public void setHighlightEnd(boolean set) {
        highlightEnd = set;
    }

    /**
     * Checks if this pip is a possible starting point for a move by the specified player.
     *
     * @param color color of player
     * @return whether a move is possible (a move is possible if there are stones of the player's colour on the pip)
     */
    public boolean isPossibleStart(Board.Color color) {
    	return stones.size() >= 1 && stones.peek().getColor() == color;
    }

    /**
     * Checks if this pip is a possible ending point for a move by the specified player.
     *
     * @param color color of player
     * @return whether a move is possible (a move is possible if there are stones of the player's colour, or only one stone of the opposing colour)
     */
    public boolean isPossibleEnd(Board.Color color) {
        return stones.size() <= 1 || color == stones.peek().getColor();
    }

    /**
     * Sets the position of this pip according to the given number. (Should be called internally)
     */
    private void updateHitbox() {
        // Escape conditions
        if (number < BLACK_BAR || WHITE_BAR < number)
            return;

        // Set x
        if (number == BLACK_HOME || number == WHITE_HOME)
            x = 704;
        else if (number == BLACK_BAR || number == WHITE_BAR)
            x = 327;
        else if (number <= 6)
            x = 378 + 50 * (6 - number);
        else if (number <= 12)
            x = 26 + 50 * (12 - number);
        else if (number <= 18)
            x = 26 + 50 * (number - 13);
        else // 19-24
            x = 378 + 50 * (number - 19);

        // Set y and drawHeight
        if (isPointingUp) {
            y = 384 - (number == BLACK_BAR ? 25 : 0);
            drawY = y;
            drawHeight = h;
        } else {
            y = 26 + (number == WHITE_BAR ? 25 : 0);
            drawY = y + h;
            drawHeight = -h;
        }

        // Make the hitbox
        hitbox = new Hitbox(x, y, w, h);
    }

    /**
     * Gets the hitbox.
     *
     * @return the hitbox
     */
    public Hitbox getHitbox() {
        return hitbox;
    }

    /**
     * Gets the number of stones on the pip.
     *
     * @return number of stones
     */
    public int getStoneSize() {
        return stones.size();
    }

    /**
     * Gets the colour of the stones on the pip.
     *
     * @return colour of stones, or Board.Color.NONE if no stones
     */
    public Board.Color getColor() {
        if (stones.isEmpty())
            return Board.Color.NONE;
        return stones.peek().getColor();
    }

    /**
     * Draws this pip.
     *
     * @param g graphics environment
     */
    public void draw(Graphics g) { //draws relative to the board
        // Draw pip
        g.drawImage(Images.getImage(imageMain), x, drawY, w, drawHeight, null);

        // Draw hard highlight filter if hovered over or end pip
        if (hitbox.isHovered() || highlightEnd)
            g.drawImage(Images.getImage(imageHighlightHard), x, drawY, w, drawHeight, null);

        // Draw soft highlight filter if intermediate pip
        else if (highlightIntermediate)
            g.drawImage(Images.getImage(imageHighlightSoft), x, drawY, w, drawHeight, null);

        // Draw stones on pip, from base to tip
        int stoneY = drawY + drawHeight;

        for (int i = 0; i < stones.size(); i++) {
            stones.get(i).draw(g, x + (Images.PIP_WIDTH - Images.STONE_WIDTH) / 2, stoneY,
                    drawHeight > 0, i == stones.size() - 1 && highlightStart);
            stoneY -= (drawHeight - (stones.size() > 6 ? 1 : 0) * (drawHeight > 0 ? 1 : -1) * Images.STONE_HEIGHT) / (stones.size() <= 6 ? 6 : stones.size());
        }
    }
}

