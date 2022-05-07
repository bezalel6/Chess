package ver14.view.Dialog;

import ver14.ClientMessagesHandler;
import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Callbacks.VoidCallback;
import ver14.SharedClasses.Networking.AppSocket;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.UI.MyJFrame;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.BackOk.BackOkInterface;
import ver14.view.Dialog.BackOk.BackOkPnl;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Cards.DialogCard;
import ver14.view.Dialog.Cards.NavigationCard;
import ver14.view.Dialog.Components.Parent;
import ver14.view.ErrorPnl;
import ver14.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 * my implementation of a Dialog. a dialog is made using dialog cards that are managed in a card layout.
 * a dialog can communicate with the server directly using the provided {@link AppSocket} in the {@link DialogProperties}
 * the communication ability is mainly used for verifying availability of unique items. like when a new user registers, and needs to select a unique username.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class Dialog extends JDialog implements Parent {
    /**
     * The constant MAX_DIALOG_SIZE.
     */
    protected final static Size MAX_DIALOG_SIZE = new Size(600);
    /**
     * The constant DEFAULT_DIALOG_SIZE.
     */
    protected final static Size DEFAULT_DIALOG_SIZE = new Size(450);
    /**
     * The Top pnl.
     */
    protected final JPanel topPnl;
    /**
     * The Bottom pnl.
     */
    protected final JPanel bottomPnl;
    /**
     * The Properties.
     */
    protected final DialogProperties dialogProperties;
    /**
     * The Parent win.
     */
    private final Component parentWin;
    /**
     * The Pane.
     */
    private final Container pane;
    /**
     * The Card stack.
     */
    private final Stack<DialogCard> cardStack;
    /**
     * The Socket to server.
     */
    private final AppSocket socketToServer;
    /**
     * The Error pnl.
     */
    private final ErrorPnl errorPnl;
    /**
     * The Cards pnl.
     */
    private final JPanel cardsPnl;
    /**
     * The On close callbacks.
     */
    private final ArrayList<VoidCallback> onCloseCallbacks = new ArrayList<>();
    /**
     * The Back ok pnl.
     */
    private BackOkPnl backOkPnl = null;
    /**
     * The Focus on.
     */
    private Component focusOn;
    /**
     * The Current card.
     */
    private DialogCard currentCard;
    /**
     * The On close.
     */
    private Callback<Dialog> onClose;
    /**
     * The Is disposing.
     */
    private boolean isDisposing;
    /**
     * The Cards scroll pane.
     */
    private Scrollable cardsScrollPane;
    /**
     * The My adapter.
     */
    private MyJFrame.MyAdapter myAdapter;

    /**
     * Instantiates a new Dialog.
     *
     * @param dialogProperties the dialog's properties
     */
    public Dialog(DialogProperties dialogProperties) {
//        super((java.awt.Dialog) null);
        super(dialogProperties.parentWin());
        this.parentWin = dialogProperties.parentWin();
        this.isDisposing = false;

        if (dialogProperties.getContentPane() != null)
            setContentPane(dialogProperties.getContentPane());

        this.pane = getContentPane();
        this.socketToServer = dialogProperties.socketToServer();

        setMinimumSize(new Size(300, 200));

        cardStack = new Stack<>();
        cardsPnl = new JPanel(new CardLayout());

        topPnl = new JPanel(new GridLayout(0, 1));
        errorPnl = new ErrorPnl();
        topPnl.add(errorPnl);

        bottomPnl = new JPanel(new BorderLayout());

        myAdapter = MyJFrame.debugAdapter(this);
//        MyJFrame.addResizeEvent(getRootPane(), this::onUpdate);
        pane.add(topPnl, BorderLayout.PAGE_START);
        pane.add(bottomPnl, BorderLayout.PAGE_END);

        this.dialogProperties = dialogProperties;

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
        setTitle(StrUtils.format(dialogProperties.details().title()));
        dialogWideErr(dialogProperties.details().error());
        recenter();
    }

    /**
     * On x click.
     */
    protected void onXClick() {
        if (!tryCancel() && !tryOk(true))
            if (dialogProperties.parentWin() != null)
                dialogProperties.parentWin().doXClick();

    }

    /**
     * Recenter dialog.
     */
    protected void recenter() {
        SwingUtilities.invokeLater(() -> {
            setLocationRelativeTo(parentWin);
        });
    }

    @Override
    public MyJFrame.MyAdapter keyAdapter() {
        return myAdapter;
    }

    @Override
    public void registerSyncedList(SyncableList list) {
        ((ClientMessagesHandler) socketToServer.getMessagesHandler()).registerSyncableList(list);
    }

    @Override
    public void askServer(Message msg, MessageCallback onRes) {
        if (socketToServer != null) {
            socketToServer.requestMessage(msg, onRes);
        }
    }

    public void onUpdate() {
        if (currentCard != null) {
            verifyCurrentCard();
        }
        SwingUtilities.invokeLater(() -> {

            repackWin();
            recenter();

        });
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
    public void scrollToTop() {
        Parent.super.scrollToTop();
        cardsScrollPane.scrollToTop();
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

    /**
     * Pop card.
     */
    public void popCard() {
        if (cardStack.isEmpty()) {
            return;
        }
        showCard(cardStack.pop(), false);
    }

    /**
     * Show card.
     *
     * @param card            the card
     * @param updateCardStack the update card stack
     */
    protected void showCard(DialogCard card, boolean updateCardStack) {
        if (updateCardStack)
            cardStack.push(currentCard);
        currentCard = card;
        if (!isCardExists(card))
            addCard(card);
        setBackOk(card);
        Size cardSize = new Size(card.getPreferredSize());
        Size dialogSize = new Size(cardSize).padding(-30);
        cardsScrollPane.mySetSize(dialogSize);
        cardsPnl.setPreferredSize(cardSize);
        dialogSize = Size.min(dialogSize, MAX_DIALOG_SIZE);
        if (card.isOverrideableSize()) {
            dialogSize = new Size(DEFAULT_DIALOG_SIZE);
        }
        setPreferredSize(dialogSize);
        getCardsLayout().show(cardsPnl, card.getCardID());
        card.displayed();
        onUpdate();
    }

    /**
     * Is card exists boolean.
     *
     * @param card the card
     * @return the boolean
     */
    private boolean isCardExists(DialogCard card) {
        return Arrays.asList(cardsPnl.getComponents()).contains(card);
    }

    /**
     * Add card to the layout.
     *
     * @param card the card
     */
    public void addCard(DialogCard card) {
        if (!isCardExists(card)) {
            cardsPnl.add(card, card.getCardID());
        }
    }

    /**
     * Sets the back ok interface for the current card.
     *
     * @param backOkInterface the back ok interface
     */
    protected void setBackOk(BackOkInterface backOkInterface) {
        if (this.backOkPnl != null)
            bottomPnl.remove(this.backOkPnl);

        if (backOkInterface != null && backOkInterface != BackOkInterface.noInterface) {
            this.backOkPnl = new BackOkPnl(backOkInterface);
            bottomPnl.add(backOkPnl, BorderLayout.SOUTH);
        } else this.backOkPnl = null;


    }

    /**
     * Gets cards layout.
     *
     * @return the cards layout
     */
    private CardLayout getCardsLayout() {
        return (CardLayout) (cardsPnl.getLayout());
    }

    /**
     * Verify the current card.
     * @see ver14.view.Dialog.Components.Verified
     */
    private void verifyCurrentCard() {
        if (currentCard == null)
            return;
        String err = currentCard.checkVerifiedComponents();
        if (currentCard.dialogWideErrors()) {
            dialogWideErr(err);
        }
    }

    /**
     * Repack win.
     */
    public void repackWin() {
        SwingUtilities.invokeLater(this::pack);
    }

    /**
     * Close dialog.
     */
    public void closeDialog() {
        if (!isDisposing) {
            dispose();
            notifyClosed();
        }
        System.gc();
    }

    @Override
    public void dispose() {
        this.isDisposing = true;
        super.dispose();
    }

    /**
     * Notify all close listeners.
     */
    protected void notifyClosed() {
//        todo combine
        if (onClose != null) {
            onClose.callback(this);
        }
        onCloseCallbacks.forEach(VoidCallback::callback);
    }

    /**
     * Sets focus on.
     *
     * @param focusOn the focus on
     */
    public void setFocusOn(Component focusOn) {
        this.focusOn = focusOn;
    }

    /**
     * Start showing the dialog.
     */
    public void start() {
        start(null);
    }

    /**
     * Start showing the dialog.
     *
     * @param onClose a callback to call after closing the dialog
     */
    public void start(Callback<Dialog> onClose) {
        this.onClose = onClose;

        SwingUtilities.invokeLater(() -> {
            if (focusOn != null) {
                focusOn.requestFocus();
            }
            onUpdate();

        });
        setVisible(true);
        dispose();


    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(!isDisposing && b);
    }

    /**
     * set the current card to as a navigation card for {@code dialogCards}.
     *
     * @param navCardSize the nav card size
     * @param dialogCards the dialog cards
     * @return the navigation card
     */
    protected NavigationCard navigationCardSetup(Size navCardSize, DialogCard... dialogCards) {
        NavigationCard nav = new NavigationCard(createHeader(), this, dialogCards);
        nav.setPreferredSize(navCardSize);
        cardsSetup(nav, dialogCards);
        return nav;
    }

    /**
     * Create header.
     *
     * @return the card header
     */
    protected CardHeader createHeader() {
        return new CardHeader(dialogProperties.details().header());
    }

    /**
     * setup dialog with all its cards.
     *
     * @param startingCard the starting card or null. if null, the first element in the array will replace it
     * @param dialogCards  the dialog cards
     */
    protected void cardsSetup(DialogCard startingCard, DialogCard... dialogCards) {
        assert dialogCards.length > 0;
        startingCard = startingCard == null ? dialogCards[0] : startingCard;
        for (DialogCard dialogCard : dialogCards) {
            addCard(dialogCard);
        }
        addCard(startingCard);

        cardsScrollPane = new Scrollable(cardsPnl);
        pane.add(cardsScrollPane, BorderLayout.CENTER);

        showCard(startingCard, false);
        repackWin();
        recenter();
    }

    /**
     * Switch to a certain card.
     *
     * @param card the card to switch to
     */
    public void switchTo(DialogCard card) {
        showCard(card);
    }

    /**
     * Show card.
     *
     * @param card the card
     */
    protected void showCard(DialogCard card) {
        showCard(card, true);
    }

    /**
     * Close now.
     */
    public void closeNow() {
        dispose();
        notifyClosed();
    }
}
