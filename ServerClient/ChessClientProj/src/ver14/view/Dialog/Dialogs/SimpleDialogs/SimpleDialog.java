package ver14.view.Dialog.Dialogs.SimpleDialogs;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.DialogProperties.Properties;

import java.awt.*;
import java.util.Arrays;

public class SimpleDialog extends Dialog {
    public SimpleDialog(Properties properties, Component... components) {
        super(properties);
        if (components.length == 0)
            delayedSetup(components);
    }

    protected void delayedSetup(Component... components) {
        DialogCard card = new DialogCard(new CardHeader(properties.details().header()), this) {
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

    @Override
    public void start(Callback<Dialog> onClose) {
        pack();
        super.start(onClose);
    }
}
