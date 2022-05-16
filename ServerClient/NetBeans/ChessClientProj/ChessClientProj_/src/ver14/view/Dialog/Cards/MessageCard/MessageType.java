package ver14.view.Dialog.Cards.MessageCard;

import ver14.SharedClasses.UI.FontManager;
import ver14.view.IconManager.IconManager;
import ver14.view.MyTextArea;

import javax.swing.*;
import java.awt.*;

/**
 * Message type.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum MessageType {
    /**
     * Info message type.
     */
    INFO(IconManager.infoIcon, FontManager.Dialogs.MessageDialogs.info, Color.BLACK),
    /**
     * Error message type.
     */
    ERROR(IconManager.errorIcon, FontManager.Dialogs.MessageDialogs.error, Color.RED),
    /**
     * Server error message type.
     */
    ServerError(IconManager.serverError, FontManager.Dialogs.MessageDialogs.error, Color.RED);
    /**
     * The Icon.
     */
    public final ImageIcon icon;
    /**
     * The Font.
     */
    public final Font font;
    /**
     * The Clr.
     */
    public final Color clr;

    /**
     * Instantiates a new Message type.
     *
     * @param icon the icon
     * @param font the font
     * @param clr  the clr
     */
    MessageType(ImageIcon icon, Font font, Color clr) {
        this.icon = icon;
        this.font = font;
        this.clr = clr;
    }

    /**
     * Style.
     *
     * @param pnl the pnl
     */
    public void style(MyTextArea pnl) {
        pnl.setFont(font);
        pnl.setForeground(clr);
    }

}
