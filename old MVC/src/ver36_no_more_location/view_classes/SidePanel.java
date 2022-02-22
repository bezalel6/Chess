package ver36_no_more_location.view_classes;

import ver36_no_more_location.Controller;
import ver36_no_more_location.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.concurrent.TimeUnit;

public class SidePanel extends JPanel {
    private static final Font font = new Font(null, Font.BOLD, 20);
    private final Controller controller;
    private final JLabel[] timeLbls;
    private final JPanel white, black;
    private final MyJButton resignBtn, offerDrawBtn, addTimeBtn;
    private final MoveLog moveLog;
    private Dimension d;
    private int currentMoveIndex;


    public SidePanel(long millis, boolean isFlipped, Controller controller) {
        this.controller = controller;
        String timeControl = createTimeStr(millis);
        timeLbls = new JLabel[2];
        for (int i = 0; i < timeLbls.length; i++) {
            timeLbls[i] = new JLabel(timeControl);
            timeLbls[i].setFont(font);
        }
        resignBtn = new MyJButton("Resign", font);
        offerDrawBtn = new MyJButton("Offer Draw", font);
        addTimeBtn = new MyJButton("Add Time", font);

        setLayout(new GridBagLayout());

        white = createTimerPnl("White", timeLbls[Player.WHITE.asInt()]);
        black = createTimerPnl("Black", timeLbls[Player.BLACK.asInt()]);

        moveLog = new MoveLog();

        createAndAddLayout(isFlipped);

    }

    public static String createTimeStr(long millis) {
        long h = TimeUnit.MILLISECONDS.toHours(millis);
        long m = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(h);
        long s = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        long xs = TimeUnit.MILLISECONDS.toMicros(millis) - TimeUnit.SECONDS.toMicros(TimeUnit.MILLISECONDS.toSeconds(millis));
        String divider = ":";
        String hours = h == 0 ? "" : h + divider;
        String minutes = m == 0 ? "" : m + divider;
        String seconds = s + divider;
        String micros = xs + "";
        int strEnd = Math.min(1, micros.length());
        micros = micros.substring(0, strEnd);

        return hours + minutes + seconds + micros;
    }

    public void setBothPlayersClocks(long[] clocks) {
        for (Player player : Player.PLAYERS) {
            setTimerLabel(player, clocks[player.asInt()]);
        }
    }

    public void setFlipped(boolean isFlipped) {
        createAndAddLayout(isFlipped);
    }

    private void createAndAddLayout(boolean isFlipped) {
        removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        int wY, bY;
        int bottomY = 6;
        if (isFlipped) {
            wY = 0;
            bY = bottomY;
        } else {
            wY = bottomY;
            bY = 0;
        }

        gbc.gridx = 0;
        gbc.gridy = bY;
        add(black, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(resignBtn, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(offerDrawBtn, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        add(addTimeBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
//        gbc.gridheight = GridBagConstraints.RELATIVE;
        gbc.weightx = 10;
        gbc.weighty = 5;
        add(moveLog, gbc);

        gbc.gridx = 0;
        gbc.gridy = wY;
        gbc.weighty = 3;
        gbc.gridheight = 2;
        add(white, gbc);

    }

    public void setTimerLabel(Player player, long millis) {
        String str = createTimeStr(millis);
        timeLbls[player.asInt()].setText(str);
    }

    public JPanel createTimerPnl(String str, JLabel timerLbl) {
        return new JPanel() {{
            JLabel lbl = new JLabel(str);
            lbl.setFont(font);
            setLayout(new BorderLayout());
            add(lbl, BorderLayout.NORTH);
            add(timerLbl, BorderLayout.SOUTH);
        }};
    }

    public MoveLog getMoveLog() {
        return moveLog;
    }

    public void reset(long[] clocks) {
        setBothPlayersClocks(clocks);
        moveLog.reset();
    }

    class MoveLog extends JPanel {
        private final MyJButton forward, back, start, end;
        private JPanel moveLogPnl;
        private JScrollPane moveLogScroll;
        private boolean justAddedMove = false;

        public MoveLog() {
            forward = new MyJButton(">", font);
            back = new MyJButton("<", font);
            start = new MyJButton("<|", font);
            end = new MyJButton(">|", font);

            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 3, 3, 3);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;
            add(start, gbc);

            add(back, gbc);

            add(forward, gbc);

            add(end, gbc);

            createMoveLogPnl();
            gbc.gridy = 2;
            gbc.gridx = 0;
            gbc.gridheight = GridBagConstraints.REMAINDER;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 10;
            gbc.weighty = 10;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.ipady = 100;
            add(moveLogScroll, gbc);
        }

        private void createMoveLogPnl() {
            moveLogPnl = new JPanel(new GridLayout(0, 3)) {{
                setAutoscrolls(true);

            }};
            moveLogScroll = new JScrollPane(moveLogPnl) {{
                setAutoscrolls(true);
                setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent e) {
                        if (justAddedMove) {
                            e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                            justAddedMove = false;
                        }
                    }
                });
            }};
        }

        public void addMoveStr(String str, int moveNum, int moveIndex) {
            MyJButton move = new MyJButton(str);
            move.setActionCommand(moveIndex + "");

            if (moveNum != -1)
                moveLogPnl.add(new JLabel(moveNum + ""));
            moveLogPnl.add(move);
            currentMoveIndex = moveIndex;
            justAddedMove = true;

        }

        public void reset() {
            moveLogPnl.removeAll();
        }
    }
}
