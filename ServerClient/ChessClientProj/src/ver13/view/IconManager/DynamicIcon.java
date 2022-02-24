package ver13.view.IconManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DynamicIcon {
    private static final String defaultNormalFilename = "normal.png";
    private static final String defaultHoverFilename = "hover.gif";
    private final ImageIcon normal;
    private final ImageIcon hover;

    public DynamicIcon(String pathToSource, Size size) {
        this(IconManager.load(pathToSource + defaultNormalFilename, size), IconManager.load(pathToSource + defaultHoverFilename, size));
    }

    public DynamicIcon(ImageIcon normal, ImageIcon hover) {
        this.normal = normal;
        this.hover = hover;
    }

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

    public ImageIcon getHover() {
        return hover;
    }

    public ImageIcon getNormal() {
        return normal;
    }
}
