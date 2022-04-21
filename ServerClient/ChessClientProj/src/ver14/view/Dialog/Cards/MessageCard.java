package ver14.view.Dialog.Cards;

import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.Dialog;
import ver14.view.IconManager.IconManager;
import ver14.view.TextWrapPnl;

import javax.swing.*;
import java.awt.*;

public class MessageCard extends DialogCard {
    public MessageCard(Dialog parentDialog, CardHeader header, String message, MessageType messageType) {
        super(header, parentDialog);
        if (!StrUtils.isEmpty(message)) {
            TextWrapPnl pnl = createMsgPnl(message, messageType);
            pnl.getTextArea().setCaretPosition(0);
            pnl.setEditable(false);
            add(pnl);
        }
    }

    public static TextWrapPnl createMsgPnl(String msg, MessageType type) {
        return new TextWrapPnl(StrUtils.format(msg)) {{
            type.style(this);
        }};
    }

    @Override
    public String checkVerifiedComponents() {
        return null;
    }

    @Override
    public void shown() {
        super.shown();
        backOkPnl().getOk().setEnabled(true);
    }

    @Override
    public String getBackText() {
        return null;
    }

    public enum MessageType {
        INFO(IconManager.infoIcon, FontManager.Dialogs.MessageDialogs.info, Color.BLACK),
        ERROR(IconManager.errorIcon, FontManager.Dialogs.MessageDialogs.error, Color.RED);
        public final ImageIcon icon;
        public final Font font;
        public final Color clr;

        MessageType(ImageIcon icon, Font font, Color clr) {
            this.icon = icon;
            this.font = font;
            this.clr = clr;
        }

        public void style(TextWrapPnl pnl) {
            pnl.setFont(font);
            pnl.setForeground(clr);
        }

    }
}
