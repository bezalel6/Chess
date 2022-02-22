package ver7.view.dialogs;

import ver7.DummySocket;
import ver7.ServerMessagesHandler;
import ver7.SharedClasses.Callbacks.MessageCallback;
import ver7.SharedClasses.messages.Message;
import ver7.SharedClasses.networking.AppSocket;
import ver7.view.ErrorPnl;
import ver7.view.List.ComponentsList;
import ver7.view.List.Synced.SyncableList;
import ver7.view.dialogs.Navigation.Navable;

import javax.swing.*;
import java.awt.*;

public abstract class MyDialog extends JDialog implements ComponentsList.Parent {
    private final static Dimension winSize = new Dimension(600, 600);
    private static int contentIndex = 0;
    protected final AppSocket socketToServer;
    protected final JPanel win;
    protected final WinPnl currentPnl;
    protected final ErrorPnl errorPnl;
    protected int dialogFieldWidth;
    protected WinPnl startingPnl;
    private volatile boolean finished = false;
    private volatile boolean isEditing = false;
    private CardLayout layout;

    public MyDialog(AppSocket socketToServer) {
        this.socketToServer = socketToServer;
        layout = new CardLayout();
        win = new JPanel();
        win.setLayout(layout);
        setContentPane(win);
        setModal(true);
        errorPnl = new ErrorPnl();
        currentPnl = new WinPnl("", true);
        createB4StartingPnl();
        createStartingPnl();
    }

    protected void createB4StartingPnl() {

    }

    protected abstract void createStartingPnl();

    public WinPnl getCurrentPnl() {
        return currentPnl;
    }

    public void askServer(Message message, MessageCallback onMsg) {
        socketToServer.requestMessage(message, onMsg);
    }

    protected void doneCreating() {
        switchTo(startingPnl);
        setLocationRelativeTo(null);
    }

    public void switchTo(WinPnl pnl) {
        isEditing = true;
        win.repaint();
        contentIndex = 0;

        win.add(pnl);
        layout.last(win);
//        layout.show(win,pnl.getName());

        pnl.addToTopPnl(errorPnl, true);
        win.setSize(win.getPreferredSize());
        win.repaint();
        repackWin();
        isEditing = false;
    }
//    protected void addComponent(Component comp){
//        win.add(comp);
//    }

    public void repackWin() {
        pack();
    }

    public void switchTo(Navable navable) {
        navable.onNav();
    }

    public void start() {
        pack();
        setVisible(true);
//        System.exit(0);
    }

    public void closeNow() {
        done();
    }

    public void done() {
        dispose();
    }

    public void switchToStart() {
        switchTo(startingPnl);
    }

    @Override
    public void navigateTo(WinPnl pnl) {
        switchTo(pnl);
    }

    @Override
    public void listUpdated() {
        repackWin();
    }

    @Override
    public void enableOk(boolean enable) {
        if (currentPnl != null &&
                currentPnl.getComponents().length > 0 &&
                currentPnl.getComponents()[0] instanceof WinPnl pnl &&
                pnl.getBackOkPnl() != null)

            pnl.getBackOkPnl().enableOk(enable);
    }

    @Override
    public boolean checkErr(ComponentsList list) {
        return errorPnl.verify(list);
    }

    @Override
    public void registerSyncedList(SyncableList list) {
        if (socketToServer == null || socketToServer instanceof DummySocket)
            return;

        ((ServerMessagesHandler) socketToServer.getMessagesHandler()).registerSyncedList(list);
    }

    public void dialogWideErr(String err) {
        errorPnl.setText(err);
        repackWin();
    }

    protected void startedEditing() {
        isEditing = true;
    }

    protected void doneEditing() {
        isEditing = false;
    }
}
