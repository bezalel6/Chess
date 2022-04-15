package ver14.view.Dialog;

import ver14.ClientMessagesHandler;
import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.messages.Message;
import ver14.SharedClasses.networking.AppSocket;
import ver14.SharedClasses.ui.windows.MyJFrame;
import ver14.view.Dialog.BackOk.BackOkInterface;
import ver14.view.Dialog.BackOk.BackOkPnl;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Cards.NavigationCard;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.DialogProperties.Properties;
import ver14.view.ErrorPnl;
import ver14.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public abstract class Dialog extends JDialog implements Parent {
    protected final static Size maximumSize = new Size(600);
    protected final JPanel topPnl;
    protected final JPanel bottomPnl;
    protected final Properties properties;
    private final Component parentWin;
    private final Container pane;
    private final Stack<DialogCard> cardStack;
    private final AppSocket socketToServer;
    private final ErrorPnl errorPnl;
    private final JPanel cardsPnl;
    private final ArrayList<VoidCallback> onCloseCallbacks = new ArrayList<>();
    private BackOkPnl backOkPnl = null;
    private Component focusOn;
    private DialogCard currentCard;
    private Callback<Dialog> onClose;
    private boolean isDisposing;
    private JScrollPane cardsScrollPane;

    public Dialog(Properties properties) {
//        super((java.awt.Dialog) null);
        super(properties.parentWin());
        this.parentWin = properties.parentWin();
        this.isDisposing = false;
        if (properties.getContentPane() != null)
            setContentPane(properties.getContentPane());
        this.pane = getContentPane();
        this.socketToServer = properties.socketToServer();

        cardStack = new Stack<>();
        cardsPnl = new JPanel(new CardLayout());

        topPnl = new JPanel(new GridLayout(0, 1));
        errorPnl = new ErrorPnl();
        topPnl.add(errorPnl);

        bottomPnl = new JPanel(new BorderLayout());

        MyJFrame.debugAdapter(this);

        pane.add(topPnl, BorderLayout.PAGE_START);
        pane.add(bottomPnl, BorderLayout.PAGE_END);

        this.properties = properties;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                onXClick();
            }
        });

        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setModalityType(java.awt.Dialog.DEFAULT_MODALITY_TYPE);
        setTitle(StrUtils.format(properties.details().title()));
        dialogWideErr(properties.details().error());
        recenter();
    }

    protected void onXClick() {
        if (!tryCancel())
            tryOk(true);
    }

    protected void recenter() {
        setLocationRelativeTo(parentWin);
    }

    public void setFocusOn(Component focusOn) {
        this.focusOn = focusOn;
    }

    public void start() {
        start(null);
    }

    public void start(Callback<Dialog> onClose) {
        this.onClose = onClose;
        repackWin();

        if (this.focusOn != null)
            SwingUtilities.invokeLater(() -> focusOn.requestFocus());

        setVisible(true);

        dispose();
    }

    public void repackWin() {
        SwingUtilities.invokeLater(this::pack);
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
    public Dimension getPreferredSize() {
        return maximumSize.createMinCombo(dialogSize());
    }

    protected Dimension dialogSize() {
        return super.getPreferredSize();
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
        recenter();
        repackWin();
    }

    public void dialogWideErr(String error) {
        if (errorPnl != null) {
            errorPnl.setText(error);
        } else {

        }
    }

    @Override
    public void done() {
        closeDialog();
    }

    @Override
    public void back() {
        popCard();
    }

    @Override
    public DialogCard currentCard() {
        return currentCard;
    }

    @Override
    public BackOkPnl backOkPnl() {
        return backOkPnl;
    }

    @Override
    public void addOnClose(VoidCallback callback) {
        this.onCloseCallbacks.add(callback);
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
        setBackOk(card);
        card.shown();
        System.out.println("showing " + card + " preferred size = " + card.getPreferredSize());
        cardsPnl.setPreferredSize(card.getPreferredSize());
        cardsScrollPane.setPreferredSize(Size.add(card.getPreferredSize(), 50));
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

    protected void setBackOk(BackOkInterface backOkInterface) {
        if (this.backOkPnl != null)
            bottomPnl.remove(this.backOkPnl);

        if (backOkInterface != null) {
            this.backOkPnl = new BackOkPnl(backOkInterface);
            bottomPnl.add(backOkPnl, BorderLayout.SOUTH);
        } else this.backOkPnl = null;


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

    public void closeDialog() {
        if (!isDisposing) {
            dispose();
            notifyClosed();
        }
        System.gc();
    }

    protected void notifyClosed() {
//        todo combine
        if (onClose != null) {
            onClose.callback(this);
        }
        onCloseCallbacks.forEach(VoidCallback::callback);
    }

//    protected void

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

        cardsScrollPane = new JScrollPane(cardsPnl) {
            {
                setPreferredSize(getPreferredSize());
                setMaximumSize(getPreferredSize());
                getVerticalScrollBar().setBlockIncrement(100);
                getVerticalScrollBar().setValue(0);
            }

        };
        pane.add(cardsScrollPane, BorderLayout.CENTER);

        showCard(startingCard, false);
        pack();
        recenter();
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
