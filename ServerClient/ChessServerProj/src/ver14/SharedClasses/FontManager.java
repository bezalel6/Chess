package ver14.SharedClasses;

import java.awt.*;

public class FontManager {

    public static final Font statusLbl = Base.normal;
    public static final Font dbResponseTable = Base.normal;
    public static final Font dbResponseTableHeader = Base.normal;
    public static final Font sidePanel = Base.normal;
    public static final Font coordinates = Base.normal;
    //    public static final Font menuFont = Base.normal;
//    public static final Font menuItemsFont = Base.small;
    public static final Font boardButtons = Base.normal;
    //    public static final Font loginProcess = Base.normal;
//    public static final Font defaultSelectableBtn = Base.normal;
    public static final Font statistics = Base.normal;
    public static final Font backOk = Base.normal;
    public static final Font xLarge = Base.xLarge;
    public static final Font normal = Base.normal;
    public static final Font large = Base.large;
    public static final Font small = Base.small;
    public static final Font error = Base.error;
    public static final Font defaultLinkLbl = Base.normal;

    public static class JMenus {
        public static final Font headers = Base.normal;
        public static final Font items = Base.small;

    }

    public static class Dialogs {
        public static final Base selectableBtn = Base.normal.get(Font.SANS_SERIF);
        public static final Base dialogHeader = Base.xLarge.get(Font.DIALOG);
        public static final Base dialogInput = Base.normal.get(Font.DIALOG_INPUT);
        public static final Base dialog = Base.normal.get(Font.DIALOG);
        public static final Base fetchVerifyLbl = Base.normal;
        public static final Base errorLbl = Base.normal;


        //        public static class Login{
//
//        }
        public static class MessageDialogs {
            public static final Font error = Base.error;
            public static final Font info = Base.normal;
        }
    }

    static class Base extends Font {
        public static final Base error = new Base(Font.MONOSPACED, Font.BOLD | Font.ITALIC, Nums.smallSize);
        public static final Base xLarge = new Base(Nums.xLargeSize);
        public static final Base normal = new Base(Nums.normalSize);
        public static final Base large = new Base(Nums.largeSize);
        public static final Base small = new Base(Nums.smallSize);
        private static final String defaultFontName = null;
        private static final int type = Font.BOLD;

        Base(int size) {
            this(defaultFontName, size);
        }

        Base(String fontName, int size) {
            this(fontName, type, size);
        }

        Base(String fontName, int style, int size) {
            super(fontName, style, size);
        }

        public Base get(String fontName) {
            return new Base(fontName, getStyle(), getSize());
        }

        static class Nums {
            private static final int inc = 3;

            private static final int smallSize = 14;

            private static final int normalSize = smallSize + inc;

            private static final int largeSize = normalSize + inc;

            private static final int xLargeSize = largeSize + inc;

        }

    }
}
