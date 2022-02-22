package ver6.view.dialogs;

import ver6.DummySocket;
import ver6.ServerMessagesHandler;
import ver6.SharedClasses.Callbacks.MessageCallback;
import ver6.SharedClasses.messages.Message;
import ver6.SharedClasses.networking.AppSocket;
import ver6.SharedClasses.ui.ErrorPnl;
import ver6.view.List.ComponentsList;
import ver6.view.List.Synced.SyncableList;
import ver6.view.dialogs.Navigation.BackOkPnl;
import ver6.view.dialogs.Navigation.Navable;

import javax.swing.*;
import java.awt.*;

public abstract class MyDialog implements ComponentsList.Parent {
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
//            addComponentListener(new ComponentAdapter() {
//                @Override
//                public void componentResized(ComponentEvent e) {
//                    repackWin();
//                }
//
//                @Override
//                public void componentMoved(ComponentEvent e) {
//                    repackWin();
//                }
//
//                @Override
//                public void componentShown(ComponentEvent e) {
//                    repackWin();
//                }
//
//                @Override
//                public void componentHidden(ComponentEvent e) {
//                    repackWin();
//                }
//            });
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

    public void registerSyncedList(SyncableList list) {
        if (socketToServer == null || socketToServer instanceof DummySocket)
            return;

        ((ServerMessagesHandler) socketToServer.getMessagesHandler()).registerSyncedList(list);
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
        contentIndex = 0;
//        currentPnl.add(errorPnl);

        currentPnl.add(pnl);
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

        Component _parent = currentPnl.getComponents()[contentIndex];
        enablingRec(_parent);

    }

    private void enablingRec(Component _parent) {
        if (_parent instanceof WinPnl parent) {
            BackOkPnl backOk = parent.getBackOkPnl();
            if (backOk != null)
                backOk.enableOk(enable(parent));
        } else if (_parent instanceof Container container) {
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
