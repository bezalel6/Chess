package ver14.view.IconManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Dynamic icon - represents an icon that changes depending on hover.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class DynamicIcon {
    /**
     * The constant defaultNormalFilename.
     */
    private static final String defaultNormalFilename = "normal.png";
    /**
     * The constant defaultHoverFilename.
     */
    private static final String defaultHoverFilename = "hover.gif";
    /**
     * The Normal.
     */
    private final ImageIcon normal;
    /**
     * The Hover.
     */
    private final ImageIcon hover;

    /**
     * Instantiates a new Dynamic icon.
     *
     * @param pathToSource the path to source
     * @param size         the size
     */
    public DynamicIcon(String pathToSource, Size size) {
        this(IconManager.load(pathToSource + defaultNormalFilename, size), IconManager.load(pathToSource + defaultHoverFilename, size));
    }

    /**
     * Instantiates a new Dynamic icon.
     *
     * @param normal the normal
     * @param hover  the hover
     */
    public DynamicIcon(ImageIcon normal, ImageIcon hover) {
        this.normal = normal;
        this.hover = hover;
    }

    /**
     * Set a button's icon to be this icon.
     *
     * @param btn the button
     */
    public void set(AbstractButton btn) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                btn.setIcon(DynamicIcon.this.getHover());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                btn.setIcon(DynamicIcon.this.getNormal());
            }
        });

        btn.setIcon(this.getNormal());
    }

    /**
     * Gets hover.
     *
     * @return the hover
     */
    public ImageIcon getHover() {
        return hover;
    }

    /**
     * Gets normal.
     *
     * @return the normal
     */
    public ImageIcon getNormal() {
        return normal;
    }
}
