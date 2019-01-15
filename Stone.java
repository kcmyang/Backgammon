import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Class representing a stone.
 */
public class Stone implements Serializable {
    private Board.Color color;
    private Images.k image;

    /**
     * Constructs a new stone of the specified colour.
     *
     * @param color the color
     */
    public Stone(Board.Color color) {
        this.color = color;

        // Set image
        if (color.equals(Board.Color.BLACK))
            image = Images.k.STONE_BLACK;
        else
            image = Images.k.STONE_WHITE;
    }

    /**
     * Gets the color of this stone.
     *
     * @return the color
     */
    public Board.Color getColor(){
    	return color;
    }

    /**
     * Draws this stone.
     *
     * @param g graphics environment
     * @param x left x
     * @param y "bottom" y
     * @param up whether the underlying pip is pointing up
     * @param highlighted whether this stone is highlighted
     */
    public void draw(Graphics g, int x, int y, boolean up, boolean highlighted) {
        if (highlighted)
            g.drawImage(Images.getImage(Images.k.STONE_RING_HIGHLIGHT), x - 3, y + (up ? 3 : -3),
                    Images.STONE_RING_HIGHLIGHT_WIDTH, (up ? -1 : 1) * Images.STONE_RING_HIGHLIGHT_HEIGHT, null);
        g.drawImage(Images.getImage(image), x, y, Images.STONE_WIDTH, (up ? -1 : 1) * Images.STONE_HEIGHT, null);
    }
}
