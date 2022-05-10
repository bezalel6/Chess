package ver14.SharedClasses.UI;

import java.awt.*;


/**
 * represents a Font manager.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class FontManager {

    /**
     * The constant statusLbl.
     */
    public static final Font statusLbl = Base.normal;
    /**
     * The constant dbResponseTable.
     */
    public static final Font dbResponseTable = Base.normal;

    /**
     * The constant dbResponseTableHeader.
     */
    public static final Font dbResponseTableHeader = Base.normal;

    /**
     * The constant sidePanel.
     */
    public static final Font sidePanel = Base.normal;
    /**
     * The constant coordinates.
     */
    public static final Font coordinates = Base.normal;

    /**
     * The constant boardButtons.
     */
    public static final Font boardButtons = Base.normal;

    /**
     * The constant statistics.
     */
    public static final Font statistics = Base.normal;
    /**
     * The constant backOk.
     */
    public static final Font backOk = Base.normal;
    /**
     * The constant xLarge.
     */
    public static final Font xLarge = Base.xLarge;
    /**
     * The constant normal.
     */
    public static final Font normal = Base.normal;
    /**
     * The constant large.
     */
    public static final Font large = Base.large;
    /**
     * The constant small.
     */
    public static final Font small = Base.small;
    /**
     * The constant error.
     */
    public static final Font error = Base.error;
    /**
     * The constant defaultLinkLbl.
     */
    public static final Font defaultLinkLbl = Base.normal;


    /**
     * J menus.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class JMenus {
        /**
         * The constant headers.
         */
        public static final Font headers = Base.normal;
        /**
         * The constant items.
         */
        public static final Font items = Base.small;

    }

    /**
     * Dialogs.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public static class Dialogs {
        /**
         * The constant selectableBtn.
         */
        public static final Base selectableBtn = Base.normal.get(Font.SANS_SERIF);
        /**
         * The constant dialogHeader.
         */
        public static final Base dialogHeader = Base.xLarge.get(Font.DIALOG);
        /**
         * The constant dialogInput.
         */
        public static final Base dialogInput = Base.normal.get(Font.DIALOG_INPUT);
        /**
         * The constant dialog.
         */
        public static final Base dialog = Base.normal.get(Font.DIALOG);
        /**
         * The constant fetchVerifyLbl.
         */
        public static final Base fetchVerifyLbl = Base.normal;
        /**
         * The constant errorLbl.
         */
        public static final Base errorLbl = Base.normal;


        /**
         * Message dialogs.
         *
         * @author Bezalel Avrahami (bezalel3250@gmail.com)
         */
//        public static class Login{
//
//        }
        public static class MessageDialogs {
            /**
             * The constant error.
             */
            public static final Font error = Base.error;
            /**
             * The constant info.
             */
            public static final Font info = Base.normal;
        }
    }

    /**
     * Base.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    static class Base extends Font {
        /**
         * The constant error.
         */
        public static final Base error = new Base(Font.MONOSPACED, Font.BOLD, Nums.normalSize);
        /**
         * The constant xLarge.
         */
        public static final Base xLarge = new Base(Nums.xLargeSize);
        /**
         * The constant normal.
         */
        public static final Base normal = new Base(Nums.normalSize);
        /**
         * The constant large.
         */
        public static final Base large = new Base(Nums.largeSize);
        /**
         * The constant small.
         */
        public static final Base small = new Base(Nums.smallSize);
        /**
         * The constant defaultFontName.
         */
        private static final String defaultFontName = null;
        /**
         * The constant type.
         */
//        private static final String defaultFontName = "Verdana";
//        private static final int type = 0;
        private static final int type = Font.BOLD;

        /**
         * Instantiates a new Base.
         *
         * @param size the size
         */
        Base(int size) {
            this(defaultFontName, size);
        }

        /**
         * Instantiates a new Base.
         *
         * @param fontName the font name
         * @param size     the size
         */
        Base(String fontName, int size) {
            this(fontName, type, size);
        }

        /**
         * Instantiates a new Base.
         *
         * @param fontName the font name
         * @param style    the style
         * @param size     the size
         */
        Base(String fontName, int style, int size) {
            super(fontName, style, size);
        }

        /**
         * Get base.
         *
         * @param fontName the font name
         * @return the base
         */
        public Base get(String fontName) {
            return new Base(fontName, getStyle(), getSize());
        }

        /**
         * Nums.
         *
         * @author Bezalel Avrahami (bezalel3250@gmail.com)
         */
        static class Nums {
            /**
             * The constant inc.
             */
            private static final int inc = 3;

            /**
             * The constant smallSize.
             */
            private static final int smallSize = 14;

            /**
             * The constant normalSize.
             */
            private static final int normalSize = smallSize + inc;

            /**
             * The constant largeSize.
             */
            private static final int largeSize = normalSize + inc;

            /**
             * The constant xLargeSize.
             */
            private static final int xLargeSize = largeSize + inc;

        }

    }
}
