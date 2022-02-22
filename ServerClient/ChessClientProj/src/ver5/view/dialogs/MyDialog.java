package ver5.view.dialogs;

import ver5.SharedClasses.messages.Message;
import ver5.SharedClasses.messages.MessageCallback;
import ver5.SharedClasses.networking.AppSocket;
import ver5.SharedClasses.ui.ErrorPnl;
import ver5.view.dialogs.Navigation.BackOkPnl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public abstract class MyDialog {
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
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    repackWin();
                }

                @Override
                public void componentMoved(ComponentEvent e) {
                    repackWin();
                }

                @Override
                public void componentShown(ComponentEvent e) {
                    repackWin();
                }

                @Override
                public void componentHidden(ComponentEvent e) {
                    repackWin();
                }
            });
        }};
        errorPnl = new ErrorPnl();
        currentPnl = new WinPnl("", true);
        createStartingPnl();
    }

    public void repackWin() {
        win.pack();
    }

    protected abstract void createStartingPnl();

    public void askServer(Message message, MessageCallback onMsg) {
        socketToServer.getBackgroundSocket().writeMessage(message);
        socketToServer.getBackgroundSocket().readMessage(onMsg);
    }

    public void dialogWideErr(String err) {
        errorPnl.setText(err);
        repackWin();
    }

    protected void doneCreating() {
        switchTo(startingPnl);

        win.add(new JPanel() {{
            add(currentPnl);
        }});
        win.pack();
        win.setLocationRelativeTo(null);
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
//        while (!finish) cycle();
    }

    public void switchToStart() {
        switchTo(startingPnl);
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

    private void cycle() {
        while (isEditing) Thread.onSpinWait();

        Component _parent = currentPnl.getComponents()[contentIndex];

        if (_parent instanceof WinPnl parent) {

            BackOkPnl backOk = parent.getBackOkPnl();
            if (backOk != null)
                backOk.enableOk(enable(parent));
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
                return enable(winPnl);
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

    public void done() {
        finished = true;
    }
}
