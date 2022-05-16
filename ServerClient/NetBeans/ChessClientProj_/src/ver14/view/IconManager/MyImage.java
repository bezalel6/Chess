package ver14.view.IconManager;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * My implementation of an icon.
 * todo move scaling to this object
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MyImage extends ImageIcon {
    /**
     * Instantiates a new My icon.
     *
     * @param filename    the filename
     * @param description the description
     */
    public MyImage(String filename, String description) {
        super(filename, description);
    }

    /**
     * Instantiates a new My icon.
     *
     * @param location    the location
     * @param description the description
     */
    public MyImage(URL location, String description) {
        super(location, description);
    }

    /**
     * Instantiates a new My icon.
     *
     * @param location the location
     */
    public MyImage(URL location) {
        super(location);
    }

    /**
     * Instantiates a new My icon.
     *
     * @param image       the image
     * @param description the description
     */
    public MyImage(Image image, String description) {
        super(image, description);
    }

    /**
     * Instantiates a new My icon.
     *
     * @param image the image
     */
    public MyImage(ImageIcon image) {
        super(image.getImage(), image.getDescription());
    }

    /**
     * Paint this image in the middle of a {@link Component}.
     *
     * @param g the graphics
     * @param c the component to paint in the middle of
     */
    public void paintInMiddle(Graphics g, Component c) {
//            g.setColor(Color.BLACK);
        Size size = new Size(c.getWidth() / 2, c.getHeight() / 2);
        int x = (getIconWidth() + size.width) / 2;
        int y = (getIconHeight() + size.height) / 2;
        IconManager.scaleImage(this, size).paintIcon(c, g, x, y);
    }

}
