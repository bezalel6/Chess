package ver14.view.Dialog.Dialogs.SimpleDialogs;

import ver14.view.Dialog.BackOk.BackOkInterface;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.DialogProperties;

import java.awt.*;
import java.util.Arrays;

/**
 * represents a Simple dialog. that can be created with simple components.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class SimpleDialog extends Dialog {

    /**
     * Instantiates a new Simple dialog.
     *
     * @param dialogProperties the properties
     * @param components       the components
     */
    public SimpleDialog(DialogProperties dialogProperties, Component... components) {
        this(dialogProperties, (BackOkInterface) null);
        if (components.length != 0)
            delayedSetup(BackOkInterface.createSimpleInterface(this::closeDialog), components);
    }

    /**
     * Instantiates a new Simple dialog.
     *
     * @param dialogProperties the properties
     * @param backOk           the back ok
     * @param components       the components
     */
    public SimpleDialog(DialogProperties dialogProperties, BackOkInterface backOk, Component... components) {
        super(dialogProperties);
        if (components.length != 0)
            delayedSetup(backOk, components);
    }

    /**
     * Delayed setup.
     *
     * @param backOk     the back ok
     * @param components the components
     */
    protected void delayedSetup(BackOkInterface backOk, Component... components) {
        DialogCard card = new DialogCard(new CardHeader(dialogProperties.details().header()), this, backOk) {
            {
                Arrays.stream(components).forEach(this::add);
                setBackOk(backOk);
            }
        };
        cardsSetup(null, card);
    }


}
