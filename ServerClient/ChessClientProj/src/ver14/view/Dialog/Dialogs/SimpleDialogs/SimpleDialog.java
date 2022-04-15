package ver14.view.Dialog.Dialogs.SimpleDialogs;

import ver14.view.Dialog.BackOk.BackOkInterface;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.DialogProperties.Properties;

import java.awt.*;
import java.util.Arrays;

public class SimpleDialog extends Dialog {


    public SimpleDialog(Properties properties, Component... components) {
        this(properties, (BackOkInterface) null);
        delayedSetup(BackOkInterface.createSimpleInterface(this::closeDialog), components);
    }

    public SimpleDialog(Properties properties, BackOkInterface backOk, Component... components) {
        super(properties);
        if (components.length != 0)
            delayedSetup(backOk, components);
    }

    protected void delayedSetup(BackOkInterface backOk, Component... components) {
        DialogCard card = new DialogCard(new CardHeader(properties.details().header()), this, backOk) {
            {
                Arrays.stream(components).forEach(this::add);
                setBackOk(backOk);
            }
        };
        cardsSetup(null, card);
    }


}
