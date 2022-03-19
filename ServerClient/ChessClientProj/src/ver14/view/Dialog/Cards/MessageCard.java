package ver14.view.Dialog.Cards;

import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.Dialog;
import ver14.view.IconManager.IconManager;
import ver14.view.TextWrapPnl;

import javax.swing.*;
import java.awt.*;

public class MessageCard extends DialogCard {
    public MessageCard(Dialog parentDialog, String message, MessageType messageType) {
        super(messageType.header, parentDialog);
        TextWrapPnl pnl = new TextWrapPnl(StrUtils.format(message));
        messageType.style(pnl);
        pnl.setEditable(false);
        add(pnl);
    }

    @Override
    public String getBackText() {
        return null;
    }

    public enum MessageType {
        INFO(IconManager.infoIcon, FontManager.infoMessages, Color.BLACK), ERROR(IconManager.errorIcon, FontManager.error, Color.RED);
        public final ImageIcon icon;
        public final Font font;
        public final CardHeader header;
        public final Color clr;

        MessageType(ImageIcon icon, Font font, Color clr) {
            this.icon = icon;
            this.header = new CardHeader(name(), icon, true, name());
            this.font = font;
            this.clr = clr;
        }

        public void style(TextWrapPnl pnl) {
            pnl.setFont(font);
            pnl.setForeground(clr);
        }

    }
}
