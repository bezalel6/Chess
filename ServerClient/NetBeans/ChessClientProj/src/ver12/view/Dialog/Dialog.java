package ver12.view.Dialog;

import ver12.ClientMessagesHandler;
import ver12.SharedClasses.Callbacks.Callback;
import ver12.SharedClasses.Callbacks.MessageCallback;
import ver12.SharedClasses.Sync.SyncedItems;
import ver12.SharedClasses.messages.Message;
import ver12.SharedClasses.networking.AppSocket;
import ver12.view.Dialog.Cards.CardHeader;
import ver12.view.Dialog.Cards.DialogCard;
import ver12.view.Dialog.Cards.NavigationDialogCard;
import ver12.view.Dialog.Components.Parent;
import ver12.view.Dialog.Dialogs.GameSelection.GameSelect;
import ver12.view.Dialog.Dialogs.LoginProcess.LoginProcess;
import ver12.view.ErrorPnl;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Stack;

public abstract class Dialog extends JDialog implements Parent {

    protected final JPanel topPnl;
    protected final DialogProperties properties;
    private final Component parentWin;
    private final Container pane;
    private final Stack<DialogCard> cardStack;
    private final AppSocket socketToServer;
    private final ErrorPnl errorPnl;
    private final JPanel cardsPnl;
    private DialogCard currentCard;
    private Callback<Dialog> onClose;
    private boolean isDisposing;

    public Dialog(AppSocket socketToServer, DialogProperties properties, Component parentWin) {
        super((java.awt.Dialog) null);
        this.parentWin = parentWin;
        this.isDisposing = false;
        this.pane = getContentPane();
        this.socketToServer = socketToServer;

        cardStack = new Stack<>();
        cardsPnl = new JPanel(new CardLayout());

        topPnl = new JPanel(new GridLayout(0, 1));
        errorPnl = new ErrorPnl();
        topPnl.add(errorPnl);

        pane.add(topPnl, BorderLayout.PAGE_START);

        this.properties = properties;

        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setModalityType(java.awt.Dialog.DEFAULT_MODALITY_TYPE);
        setTitle(properties.title());
        setLocationRelativeTo(parentWin);
        dialogWideErr(properties.err());
    }

    public void dialogWideErr(String error) {
        if (errorPnl != null)
            errorPnl.setText(error);
    }

    /**
     * @param dialogType
     * @param socketToServer
     * @return
     */
    public static Dialog createDialog(Component parentWin, DialogType dialogType, AppSocket socketToServer, DialogProperties properties) {
        return switch (dialogType) {
            case LoginProcess -> new LoginProcess(socketToServer, properties, parentWin);
            case GameSelection -> new GameSelect(socketToServer, properties, parentWin);
            default -> throw new IllegalStateException("Unexpected value: " + dialogType);
        };
    }


    public void start() {
        start(null);
    }

    public void start(Callback<Dialog> onClose) {
        this.onClose = onClose;

        setVisible(true);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(!isDisposing && b);
    }

    @Override
    public void registerSyncedList(SyncableList list) {
        if (socketToServer == null) {
            list.sync(SyncedItems.exampleGames1);
            return;
        }
        ((ClientMessagesHandler) socketToServer.getMessagesHandler()).registerSyncableList(list);
    }

    @Override
    public void askServer(Message msg, MessageCallback onRes) {
        System.out.println("asking server " + msg);
        if (socketToServer != null) {
            socketToServer.requestMessage(msg, onRes);
        }
    }

    public void onUpdate() {
        if (currentCard != null) {
            verifyCurrentCard();
        }
        setLocationRelativeTo(parentWin);
        repackWin();
    }

    @Override
    public void done() {
        closeDialog();
    }

    @Override
    public void back() {
        popCard();
    }

    public void popCard() {
        if (cardStack.isEmpty()) {
            return;
        }
        showCard(cardStack.pop(), false);
    }

    protected void showCard(DialogCard card, boolean updateCardStack) {
        if (updateCardStack)
            cardStack.push(currentCard);
        currentCard = card;
        if (!isCardExists(card))
            addCard(card);
        getCardsLayout().show(cardsPnl, card.getCardID());
        onUpdate();
    }

    private boolean isCardExists(DialogCard card) {
        return Arrays.asList(cardsPnl.getComponents()).contains(card);
    }

    public void addCard(DialogCard card) {
        if (!isCardExists(card)) {
            cardsPnl.add(card, card.getCardID());
        }
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

    public void closeDialog() {
        dispose();
        notifyClosed();
    }

    @Override
    public void dispose() {
        this.isDisposing = true;
        super.dispose();
    }

    protected void notifyClosed() {
        if (onClose != null) {
            onClose.callback(this);
        }
    }

    protected void navigationCardSetup(DialogCard... dialogCards) {
        cardsSetup(new NavigationDialogCard(createHeader(), this, dialogCards), dialogCards);
    }

    protected void cardsSetup(DialogCard startingCard, DialogCard... dialogCards) {
        assert dialogCards.length > 0;
        startingCard = startingCard == null ? dialogCards[0] : startingCard;
        for (DialogCard dialogCard : dialogCards) {
            addCard(dialogCard);
        }
        addCard(startingCard);

        pane.add(cardsPnl, BorderLayout.CENTER);

        showCard(startingCard, false);
        pack();
        setLocationRelativeTo(parentWin);
    }

    protected CardHeader createHeader() {
        return new CardHeader(properties.header());
    }

    public void switchTo(DialogCard card) {
        showCard(card);
    }

    protected void showCard(DialogCard card) {
        showCard(card, true);
    }

    public void closeNow() {
        dispose();
        notifyClosed();
    }

    public enum DialogType {
        LoginProcess, GameSelection, ChangePassword, Custom
    }

}
