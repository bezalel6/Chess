package ver13.view.Dialog.Dialogs.SimpleDialogs;

import ver13.view.Dialog.Cards.CardHeader;
import ver13.view.Dialog.Cards.DialogCard;
import ver13.view.Dialog.Dialog;
import ver13.view.Dialog.DialogProperties;

import java.awt.*;
import java.util.Arrays;

public class SimpleDialog extends Dialog {
    public SimpleDialog(DialogProperties properties, Component parentWin, Component... components) {
        super(null, properties, parentWin);
        DialogCard card = new DialogCard(new CardHeader(properties.header()), this) {
            {
                Arrays.stream(components).forEach(this::add);
            }

            @Override
            public String getBackText() {
                return null;
            }
        };
        cardsSetup(null, card);
    }
}
