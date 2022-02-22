package ver8.view.Wishfull.dialogs;

import ver8.DummySocket;
import ver8.ServerMessagesHandler;
import ver8.SharedClasses.Callbacks.MessageCallback;
import ver8.SharedClasses.messages.Message;
import ver8.SharedClasses.networking.AppSocket;
import ver8.view.ErrorPnl;
import ver8.view.Wishfull.List.ComponentsList;
import ver8.view.Wishfull.List.Synced.SyncableList;
import ver8.view.Wishfull.dialogs.Navigation.BackOkPnl;
import ver8.view.Wishfull.dialogs.Navigation.Navable;

import javax.swing.*;
import java.awt.*;

public abstract class MyDialog extends JDialog implements ComponentsList.Parent {
    private final static Dimension winSize = new Dimension(600, 600);
    private static int contentIndex = 0;
    protected final AppSocket socketToServer;
    protected final JPanel win;
    protected final WinPnl currentPnl;
    protected final ErrorPnl errorPnl;
    protected final WinPnl startingPnl;
    protected int dialogFieldWidth;
    private volatile boolean finished = false;
    private volatile boolean isEditing = false;
    private CardLayout layout;
//    private ArrayList<Navable> dialogComponents = new ArrayList<>();

    //todo take header as parm
    public MyDialog(AppSocket socketToServer) {
        this.socketToServer = socketToServer;
        layout = new CardLayout();
        win = new JPanel(layout);
        setContentPane(win);
        setModal(true);

        errorPnl = new ErrorPnl();
        startingPnl = new WinPnl("", true);
        currentPnl = new WinPnl("", true);

        createB4StartingPnl();
        createStartingPnl();
        add(startingPnl);
        layout.last(win);
    }

    protected void createB4StartingPnl() {

    }

    protected abstract void createStartingPnl();


    protected final void addDialogWin(DialogComponent comp) {
        startingPnl.addNav(comp);
        startingPnl.addToVerifiedList(comp);
    }


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


    private void switchTo(JPanel pnl) {
        isEditing = true;
//        win.repaint();
//        contentIndex = 0;

        win.add(pnl);
        layout.last(win);
//        if (pnl instanceof DialogComponent dialogComponent) {
//        }

//        pnl.addToTopPnl(errorPnl, true);
        win.setSize(win.getPreferredSize());
        win.repaint();
        onInput();
        setLocationRelativeTo(null);
        repackWin();
        isEditing = false;
    }

    public void repackWin() {
        pack();
    }
//    protected void addComponent(Component comp){
//        win.add(comp);
//    }

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
    public void navigateTo(Navable pnl) {
        navTo(pnl);
    }

    public void navTo(Navable navable) {
//        navable.onNav();
        switchTo(navable.navTo());
    }

    @Override
    public void listUpdated() {
        repackWin();
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

    @Override
    public void onInput() {
        System.out.println("inside on input");
        if (win.getComponents().length <= 0)
            return;
        WinPnl p = (WinPnl) win.getComponents()[0];
        BackOkPnl backOkPnl = p.getBackOkPnl();
        boolean verified = p.isVerified();

        if (backOkPnl != null) {
            System.out.println("enable ok = " + verified);
            backOkPnl.enableOk(verified);
        } else {
            System.out.println("backokpnl was null, but verified = " + verified);
        }
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
