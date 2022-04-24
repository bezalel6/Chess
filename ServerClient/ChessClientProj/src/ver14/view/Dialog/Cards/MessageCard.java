package ver14.view.Dialog.Cards;

import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.Dialog;
import ver14.view.IconManager.IconManager;
import ver14.view.IconManager.Size;
import ver14.view.MyTextArea;

import javax.swing.*;
import java.awt.*;

public class MessageCard extends DialogCard {
    public MessageCard(Dialog parentDialog, CardHeader header, String message, MessageType messageType) {
        super(header, parentDialog);
        setPreferredSize(new Size(400));

        if (!StrUtils.isEmpty(message)) {
            MyTextArea pnl = createMsgPnl(message, messageType);
            pnl.getTextArea().setCaretPosition(0);
            pnl.setEditable(false);

            add(pnl, new GridBagConstraints() {{
                gridheight = gridwidth = GridBagConstraints.REMAINDER;
                fill = GridBagConstraints.BOTH;
            }});
        }
    }


    public static MyTextArea createMsgPnl(String msg, MessageType type, Size... size) {
        return new MyTextArea(StrUtils.format(msg)) {
            {
                type.style(this);
                setWrap();
                if (size.length > 0)
                    setPreferredSize(size[0]);
            }
        };
    }

    @Override
    public String checkVerifiedComponents() {
        return null;
    }

    @Override
    public void displayed() {
        super.displayed();
        backOkPnl().getOk().setEnabled(true);
    }

    @Override
    public String getBackText() {
        return null;
    }

    public enum MessageType {
        INFO(IconManager.infoIcon, FontManager.Dialogs.MessageDialogs.info, Color.BLACK),
        ERROR(IconManager.errorIcon, FontManager.Dialogs.MessageDialogs.error, Color.RED),
        ServerError(IconManager.serverError, FontManager.Dialogs.MessageDialogs.error, Color.RED);
        public final ImageIcon icon;
        public final Font font;
        public final Color clr;

        MessageType(ImageIcon icon, Font font, Color clr) {
            this.icon = icon;
            this.font = font;
            this.clr = clr;
        }

        public void style(MyTextArea pnl) {
            pnl.setFont(font);
            pnl.setForeground(clr);
        }

    }
}
