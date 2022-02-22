package ver8.view.OldDialogs;

import ver8.SharedClasses.Callbacks.MessageCallback;
import ver8.SharedClasses.messages.Message;
import ver8.SharedClasses.networking.AppSocket;
import ver8.view.ErrorPnl;
import ver8.view.List.OldComponentsList;
import ver8.view.List.Synced.OldSyncableList;
import ver8.view.OldDialogs.Navigation.BackOkPnl;
import ver8.view.OldDialogs.Navigation.Navable;

import javax.swing.*;
import java.awt.*;

public abstract class MyDialog implements OldComponentsList.Parent {
    private final static Dimension winSize = new Dimension(600, 600);
    private static int contentIndex = 0;
    protected final AppSocket socketToServer;
    protected final JFrame win;
    protected final WinPnl currentPnl;
    protected final ErrorPnl errorPnl;
    protected int dialogFieldWidth;
    protected WinPnl startingPnl;
    private volatile boolean finished = false;
    private volatile boolean isEditing = false;

    public MyDialog(AppSocket socketToServer) {
        this.socketToServer = socketToServer;
        win = new JFrame() {{
            setAlwaysOnTop(true);
            setSize(winSize);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }};
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

    public void registerSyncedList(OldSyncableList list) {
//        if (socketToServer == null || socketToServer instanceof DummySocket)
//            return;
//
//        ((ServerMessagesHandler) socketToServer.getMessagesHandler()).registerSyncableList(list);

    }

    public void dialogWideErr(String err) {
        errorPnl.setText(err);
        repackWin();
    }

    public void repackWin() {
        win.pack();
    }

    protected void doneCreating() {
        switchTo(startingPnl);

        win.add(new JPanel() {{
            add(currentPnl);
        }});
        win.pack();
        win.setLocationRelativeTo(null);
    }

    public void switchTo(WinPnl pnl) {
        isEditing = true;
        currentPnl.removeAll();
        win.repaint();
        currentPnl.add(pnl);
        contentIndex = 0;
//        currentPnl.add(errorPnl);

        pnl.addToTopPnl(errorPnl, true);
        win.setSize(win.getPreferredSize());
        win.repaint();
        repackWin();
        win.setLocationRelativeTo(null);
        isEditing = false;
    }

    public void switchTo(Navable navable) {
        navable.onNav();
    }

    public void start() {
        win.setVisible(true);

        new Timer(100, e -> {
            if (!finished)
                cycle();
            else {
                ((Timer) e.getSource()).stop();
                win.dispose();
            }
        }).start();

        while (!finished) Thread.onSpinWait();
    }

    public void closeNow() {
        done();
        win.dispose();
    }

    public void done() {
        finished = true;
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

    private void cycle() {
        while (isEditing) Thread.onSpinWait();

//        Component _parent = currentPnl.getComponents()[contentIndex];
        if (currentPnl.getComponents().length > 0)
            enablingRec(currentPnl.getComponents()[0]);

    }

    private void enablingRec(Component _parent) {
        if (_parent instanceof WinPnl parent) {
            BackOkPnl backOk = parent.getBackOkPnl();
            if (backOk != null)
                backOk.enableOk(enable(parent));
        }
        if (_parent instanceof Container container) {
            for (Component child : container.getComponents()) {
                enablingRec(child);
            }
        }
    }

    private boolean enable(WinPnl parent) {
        boolean enable = true;
        if (parent instanceof VerifiedComponent verifiedComponent) {
            return verifiedComponent.verify();
        }
        for (Component component : parent.getComponents()) {
            if (component instanceof DialogField dialogField && dialogField.getMainCompWidth() > dialogFieldWidth) {
                dialogFieldWidth = dialogField.getMainCompWidth();
            }
            if (component instanceof VerifiedComponent comp) {
                String err = "";
                if (!comp.verify()) {
                    err = comp.getError();
                    enable = false;
                }
                if (comp instanceof DialogField dialogField) {
                    dialogField.err(err);
                }
            }
            enablingRec(component);
            if (component instanceof JScrollPane scrollPane && scrollPane.getViewport().getView() instanceof WinPnl winPnl) {
                if (!enable(winPnl))
                    enable = false;
            }
        }
        return enable;
    }

    protected void startedEditing() {
        isEditing = true;
    }

    protected void doneEditing() {
        isEditing = false;
    }
}
