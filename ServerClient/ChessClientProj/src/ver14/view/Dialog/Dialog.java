package ver14.view.Dialog;

import ver14.ClientMessagesHandler;
import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.messages.Message;
import ver14.SharedClasses.networking.AppSocket;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Cards.NavigationCard;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.DialogProperties.Properties;
import ver14.view.ErrorPnl;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Stack;

public abstract class Dialog extends JDialog implements Parent {
    protected final JPanel topPnl;
    protected final Properties properties;
    private final Component parentWin;
    private final Container pane;
    private final Stack<DialogCard> cardStack;
    private final AppSocket socketToServer;
    private final ErrorPnl errorPnl;
    private final JPanel cardsPnl;
    private DialogCard currentCard;
    private Callback<Dialog> onClose;
    private boolean isDisposing;

    public Dialog(Properties properties) {
        super((java.awt.Dialog) null);
        this.parentWin = properties.parentWin();
        this.isDisposing = false;
        this.pane = getContentPane();
        this.socketToServer = properties.socketToServer();

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
        setTitle(properties.details().title());
        setLocationRelativeTo(parentWin);
        dialogWideErr(properties.details().error());
    }

    public void dialogWideErr(String error) {
        if (errorPnl != null)
            errorPnl.setText(error);
    }

    public void start() {
        start(null);
    }

    public void start(Callback<Dialog> onClose) {
        this.onClose = onClose;
        pack();
        setVisible(true);

        dispose();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(!isDisposing && b);
    }

    @Override
    public void dispose() {
        this.isDisposing = true;
        super.dispose();
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
    public DialogCard currentCard() {
        return currentCard;
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
        if (!isDisposing) {
            dispose();
            notifyClosed();
        }
        System.gc();
    }

    protected void notifyClosed() {
        if (onClose != null) {
            onClose.callback(this);
        }
    }

    protected void navigationCardSetup(DialogCard... dialogCards) {
        cardsSetup(new NavigationCard(createHeader(), this, dialogCards), dialogCards);
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
        return new CardHeader(properties.details().header());
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
}
