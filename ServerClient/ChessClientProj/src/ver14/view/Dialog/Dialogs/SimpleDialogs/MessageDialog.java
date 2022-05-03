package ver14.view.Dialog.Dialogs.SimpleDialogs;

import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.MessageCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.DialogProperties;

/**
 * represents a Message dialog, used for displaying messages.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MessageDialog extends Dialog {
    /**
     * The Message type.
     */
    private final MessageCard.MessageType messageType;

    /**
     * Instantiates a new Message dialog.
     *
     * @param dialogProperties the properties
     * @param message          the message
     * @param title            the title
     * @param messageType      the message type
     */
    public MessageDialog(DialogProperties dialogProperties, String message, String title, MessageCard.MessageType messageType) {
        super(dialogProperties);
        setTitle(title);
        setIconImage(messageType.icon.getImage());
        MessageCard card = new MessageCard(this, new CardHeader(title), message, messageType);
        cardsSetup(null, card);
        setFocusOn(card.backOkPnl().getOk());

        this.messageType = messageType;
    }

//    /**
//     * The entry point of application.
//     *
//     * @param args the input arguments
//     */
//    public static void main(String[] args) {
//        new MessageDialog(new Properties(new Properties.Details()), "fekplfe erfj ejifhiouf nifehjfoi fenfoes hffshjf soijf fsnoif oifjsi j ijifj oijjoifj sij" + StrUtils.repeat((i, isLast) -> i + " ", 500), "title", MessageCard.MessageType.ERROR).start();
//    }

    @Override
    protected void onXClick() {
        tryOk(false);
    }

    /**
     * Gets message type.
     *
     * @return the message type
     */
    public MessageCard.MessageType getMessageType() {
        return messageType;
    }


}
