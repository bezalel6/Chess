package ver6.SharedClasses;

import java.awt.*;

public class FontManager {
    public static final Font errorLbl = new Font(Font.MONOSPACED, Font.BOLD | Font.ITALIC, 12);
    private static final int minSize = 16;
    private static final int type = Font.BOLD;
    private static final String fontName = null;
    public static final Font statusLbl = new Font(fontName, type, minSize);
    public static final Font sidePanel = new Font(fontName, type, minSize);
    public static final Font coordinates = new Font(fontName, type, minSize);
    public static final Font menuItems = new Font(fontName, type, minSize);
    public static final Font boardButtons = new Font(fontName, type, minSize);
    public static final Font loginProcess = new Font(fontName, type, minSize);
    public static final Font defaultSelectableBtn = new Font(fontName, type, minSize);
    public static final Font statistics = new Font(fontName, type, minSize);
    public static final Font infoMessages = new Font(fontName, type, minSize);
    public static final Font backOk = new Font(fontName, type, minSize);
}
