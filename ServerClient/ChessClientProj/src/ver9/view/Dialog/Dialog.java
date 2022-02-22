package ver9.view.Dialog;

import com.formdev.flatlaf.FlatLightLaf;
import ver9.ServerMessagesHandler;
import ver9.SharedClasses.Callbacks.MessageCallback;
import ver9.SharedClasses.Sync.SyncedItems;
import ver9.SharedClasses.messages.Message;
import ver9.SharedClasses.messages.MessageType;
import ver9.SharedClasses.networking.AppSocket;
import ver9.view.Dialog.Cards.CardHeader;
import ver9.view.Dialog.Cards.DialogCard;
import ver9.view.Dialog.Cards.NavigationDialogCard;
import ver9.view.Dialog.Components.Parent;
import ver9.view.Dialog.Dialogs.GameSelection.GameSelect;
import ver9.view.Dialog.Dialogs.LoginProcess.LoginProcess;
import ver9.view.ErrorPnl;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Stack;

public abstract class Dialog extends JDialog implements Parent {

    static {
        FlatLightLaf.setup();
    }

    protected final JPanel topPnl;
    private final Container pane;
    private final Stack<DialogCard> cardStack;
    private final AppSocket socketToServer;
    private ErrorPnl errorPnl;
    private JPanel cardsPnl;
    private DialogCard currentCard;

    public Dialog(AppSocket socketToServer, String title, String err) {
        super((java.awt.Dialog) null);
        setAlwaysOnTop(true);
        setModalityType(java.awt.Dialog.DEFAULT_MODALITY_TYPE);
        this.socketToServer = socketToServer;
        pane = getContentPane();
        setTitle(title);
        setLocationRelativeTo(null);

        cardStack = new Stack<>();
        cardsPnl = new JPanel(new CardLayout());

        topPnl = new JPanel(new GridLayout(0, 1));
        errorPnl = new ErrorPnl();
        topPnl.add(errorPnl);

        pane.add(topPnl, BorderLayout.PAGE_START);

        dialogWideErr(err);
    }

    public static Dialog createDialog(DialogType dialogType, AppSocket socketToServer, String err) {
        return switch (dialogType) {
            case LoginProcess -> new LoginProcess(socketToServer, err);
            case GameSelection -> new GameSelect(socketToServer, err);

            default -> throw new IllegalStateException("Unexpected value: " + dialogType);
        };
    }

    public void dialogWideErr(String error) {
        if (errorPnl != null)
            errorPnl.setText(error);
    }

    @Override
    public void registerSyncedList(SyncableList list) {
        if (socketToServer == null) {
            list.sync(SyncedItems.exampleGames1);
            return;
        }
        ((ServerMessagesHandler) socketToServer.getMessagesHandler()).registerSyncableList(list);
    }

    @Override
    public void askServer(Message msg, MessageCallback onRes) {
        System.out.println("asking server " + msg);
        if (socketToServer != null) {
            socketToServer.requestMessage(msg, onRes);
        } else {
            onRes.onMsg(new Message(MessageType.INTERRUPT) {{
                setAvailable(true);
            }});
        }
    }

    public void onUpdate() {
        if (currentCard != null) {
            verifyCurrentCard();
        }
        repackWin();
    }

    public void closeDialog() {
        dispose();
    }

    protected void navigationCardSetup(CardHeader header, DialogCard... dialogCards) {
        cardsSetup(new NavigationDialogCard(header, this, dialogCards), dialogCards);
    }

    protected void cardsSetup(DialogCard startingCard, DialogCard... dialogCards) {
        assert dialogCards.length > 0;

        for (DialogCard dialogCard : dialogCards) {
            addCard(dialogCard);
        }
        addCard(startingCard);

        pane.add(cardsPnl, BorderLayout.CENTER);

        showCard(startingCard, false);
        pack();
        setLocationRelativeTo(null);
    }

    public void addCard(DialogCard card) {
        if (!isCardExists(card)) {
            cardsPnl.add(card, card.getCardName());
        }
    }

    protected void showCard(DialogCard card, boolean updateCardStack) {
        if (updateCardStack)
            cardStack.push(currentCard);
        currentCard = card;
        if (!isCardExists(card))
            addCard(card);
        getCardsLayout().show(cardsPnl, card.getCardName());
        onUpdate();
    }

    private boolean isCardExists(DialogCard card) {
        return Arrays.asList(cardsPnl.getComponents()).contains(card);
    }

    private CardLayout getCardsLayout() {
        return (CardLayout) (cardsPnl.getLayout());
    }

    private void verifyCurrentCard() {
        if (currentCard == null)
            return;
        String err = currentCard.checkVerifiedComponents();
        if (currentCard.dialogWideErrors()) {
            dialogWideErr(err);
        }
    }

    public void repackWin() {
        SwingUtilities.invokeLater(this::pack);
    }

    public void start() {
        setVisible(true);
    }

    public void switchTo(DialogCard card) {
        showCard(card);
    }

    protected void showCard(DialogCard card) {
        showCard(card, true);
    }

    public void popCard() {
        if (cardStack.isEmpty()) {
            return;
        }
        showCard(cardStack.pop(), false);
    }

    public void closeNow() {
        dispose();
    }

    public enum DialogType {
        LoginProcess, GameSelection
    }
}
