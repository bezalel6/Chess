package ver8.view.Dialog;

import com.formdev.flatlaf.FlatLightLaf;
import ver8.DummySocket;
import ver8.ServerMessagesHandler;
import ver8.SharedClasses.Callbacks.MessageCallback;
import ver8.SharedClasses.Sync.SyncedItems;
import ver8.SharedClasses.messages.Message;
import ver8.SharedClasses.messages.MessageType;
import ver8.SharedClasses.networking.AppSocket;
import ver8.view.Dialog.Cards.CardHeader;
import ver8.view.Dialog.Cards.DialogCard;
import ver8.view.Dialog.Cards.NavigationDialogCard;
import ver8.view.ErrorPnl;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Stack;

public abstract class Dialog extends JDialog {
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

    public Dialog(AppSocket socketToServer, String title) {
        super((java.awt.Dialog) null);
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
    }


    public void closeDialog() {
        dispose();
    }

    public void registerSyncableList(SyncableList list) {
        if (socketToServer == null || socketToServer instanceof DummySocket) {
            list.sync(SyncedItems.exampleGames1);
            return;
        }
        ((ServerMessagesHandler) socketToServer.getMessagesHandler()).registerSyncableList(list);
    }

    protected void navigationCardSetup(CardHeader header, DialogCard... dialogCards) {
        cardsSetup(new NavigationDialogCard(header, this, dialogCards), dialogCards);
    }

    protected void cardsSetup(DialogCard startingCard, DialogCard... dialogCards) {
        assert dialogCards.length > 0;

//        //asserts all names are different. important for the show function
//        if (Arrays.stream(dialogCards).noneMatch(c -> Arrays.stream(dialogCards).anyMatch(c2 -> (c2 != c) && c2.getCardName().equals(c.getCardName())))) {
//            throw new Error("");
//        }

        for (DialogCard dialogCard : dialogCards) {
            addCard(dialogCard);
        }
        addCard(startingCard);

        pane.add(cardsPnl, BorderLayout.CENTER);

        showCard(startingCard, false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void addCard(DialogCard card) {
//        if (!isCardExists(card)) {
        cardsPnl.add(card, card.getCardName());
//        }
    }

    protected void showCard(DialogCard card, boolean updateCardStack) {
        if (updateCardStack)
            cardStack.push(currentCard);
        currentCard = card;
        if (!isCardExists(card))
            addCard(card);
        getCardsLayout().show(cardsPnl, card.getCardName());
        onInputUpdate();
    }

    private boolean isCardExists(DialogCard card) {
        return Arrays.asList(cardsPnl.getComponents()).contains(card);
    }

    private CardLayout getCardsLayout() {
        return (CardLayout) (cardsPnl.getLayout());
    }

    public void onInputUpdate() {
        currentCard.onInputUpdate();
        verifyCurrentCard();
        repackWin();
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

    public void dialogWideErr(String error) {
        if (errorPnl != null)
            errorPnl.setText(error);
    }

    public void switchTo(DialogCard card) {
        showCard(card);
    }

    protected void showCard(DialogCard card) {
        showCard(card, true);
    }

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

    public void popCard() {
        if (cardStack.isEmpty()) {
            return;
        }
        showCard(cardStack.pop(), false);
    }
}
