package ver10.SharedClasses;

import java.awt.*;

public class FontManager {

    public static final Font statusLbl = Base.normal;
    public static final Font dbResponseTable = Base.normal;
    public static final Font dbResponseTableHeader = Base.normal;
    public static final Font sidePanel = Base.normal;
    public static final Font coordinates = Base.normal;
    public static final Font menuFont = Base.normal;
    public static final Font menuItemsFont = Base.small;
    public static final Font boardButtons = Base.normal;
    public static final Font loginProcess = Base.normal;
    public static final Font defaultSelectableBtn = Base.normal;
    public static final Font statistics = Base.normal;
    public static final Font infoMessages = Base.normal;
    public static final Font backOk = Base.normal;
    public static final Font xLarge = Base.xLarge;

    public static final Font normal = Base.normal;

    public static final Font large = Base.large;

    public static final Font small = Base.small;

    public static final Font error = Base.error;

    static class Base {
        public static final Font error = new Font(Font.MONOSPACED, Font.BOLD | Font.ITALIC, Nums.smallSize);
        private static final String fontName = null;
        private static final int type = Font.BOLD;
        public static final Font xLarge = new Font(fontName, type, Nums.xLargeSize);
        public static final Font normal = new Font(fontName, type, Nums.normalSize);
        public static final Font large = new Font(fontName, type, Nums.largeSize);
        public static final Font small = new Font(fontName, type, Nums.smallSize);

        static class Nums {
            private static final int inc = 3;

            private static final int smallSize = 14;

            private static final int normalSize = smallSize + inc;

            private static final int largeSize = normalSize + inc;

            private static final int xLargeSize = largeSize + inc;

        }

    }
}
