package ver14.view.Dialog.Dialogs.SimpleDialogs;

import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.Cards.MessageCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.DialogProperties.Properties;
import ver14.view.IconManager.Size;

public class MessageDialog extends Dialog {
    public MessageDialog(Properties properties, String message, String title, MessageCard.MessageType messageType) {
        super(properties);
        setTitle(title);
        setIconImage(messageType.icon.getImage());
        MessageCard card = new MessageCard(this, message, messageType);
        cardsSetup(null, card);
        setMinimumSize(new Size(250));
        setFocusOn(card.getBackOkPnl().getOk());

    }


    public static void main(String[] args) {
        new MessageDialog(new Properties(null, null, new Properties.Details()), "fekplfe erfj ejifhiouf nifehjfoi fenfoes hffshjf soijf fsnoif oifjsi j ijifj oijjoifj sij" + StrUtils.repeat((i, isLast) -> i + "", 50), "title", MessageCard.MessageType.ERROR).start();
    }


}
