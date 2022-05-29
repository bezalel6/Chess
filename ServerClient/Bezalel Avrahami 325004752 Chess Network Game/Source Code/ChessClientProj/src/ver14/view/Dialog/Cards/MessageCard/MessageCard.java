package ver14.view.Dialog.Cards.MessageCard;

import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.IconManager.Size;
import ver14.view.MyTextArea;

import java.awt.*;

/**
 * Message card - represents a dialog card used for displaying messages.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MessageCard extends DialogCard {
    /**
     * Instantiates a new Message card.
     *
     * @param parentDialog the parent dialog
     * @param header       the header
     * @param message      the message
     * @param messageType  the message type
     */
    public MessageCard(Dialog parentDialog, CardHeader header, String message, MessageType messageType) {
        super(header, parentDialog);
//        setMinimumSize(new Size(400));

        if (!StrUtils.isEmpty(message)) {
            MyTextArea pnl = createMsgPnl(message, messageType, new Size(400));
            pnl.getTextArea().setCaretPosition(0);
            pnl.setEditable(false);

            add(pnl, new GridBagConstraints() {{
                gridheight = gridwidth = GridBagConstraints.REMAINDER;
                fill = GridBagConstraints.BOTH;
            }});
        }
    }


    /**
     * Create msg pnl my text area.
     *
     * @param msg  the msg
     * @param type the type
     * @param size the size
     * @return the my text area
     */
    public static MyTextArea createMsgPnl(String msg, MessageType type, Size... size) {
        return new MyTextArea(StrUtils.format(msg)) {
            {
                type.style(this);
                setWrap();
                if (size.length > 0)
                    setPreferredSize(new Size(size[0]).padding(-5));
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

}
