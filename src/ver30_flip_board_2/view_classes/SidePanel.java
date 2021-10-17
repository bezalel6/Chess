package ver30_flip_board_2.view_classes;

import ver30_flip_board_2.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Dimension2D;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SidePanel extends JPanel {
    private static final Font font = new Font(null, Font.BOLD, 20);
    private final JLabel[] timeLbls;
    private final JPanel white, black;
    private final MyJButton resignBtn, offerDrawBtn, addTimeBtn;
    private JPanel moveLogPnl;
    private JScrollPane moveLogScroll;

    public SidePanel(long millis, boolean isFlipped) {
        String timeControl = createTimeStr(millis);
        timeLbls = new JLabel[2];
        for (int i = 0; i < timeLbls.length; i++) {
            timeLbls[i] = new JLabel(timeControl);
            timeLbls[i].setFont(font);
        }
        resignBtn = new MyJButton("Resign");
        resignBtn.setFont(font);
        offerDrawBtn = new MyJButton("Offer Draw");
        offerDrawBtn.setFont(font);
        addTimeBtn = new MyJButton("Add Time");
        addTimeBtn.setFont(font);
        setLayout(new GridBagLayout());

        white = createTimerPnl("White", timeLbls[Player.WHITE]);
        black = createTimerPnl("Black", timeLbls[Player.BLACK]);

        createMoveLogPnl();
        createAndAddLayout(isFlipped);
        IntStream.range(1, 10).forEach(i -> addMoveStr("si", i));
//        test.add(moveLogScroll);
//        test.setVisible(true);

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

    private void createMoveLogPnl() {
        moveLogPnl = new JPanel(new GridLayout(0, 3));
        moveLogPnl.add(new JLabel("test"));
        moveLogPnl.setPreferredSize(new Dimension(1000, 100));

        moveLogScroll = new JScrollPane(moveLogPnl, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        moveLogPnl.setAutoscrolls(true);
        moveLogScroll.setPreferredSize(new Dimension(1000, 300));
    }

    public void addMoveStr(String str, int moveNum) {
        JLabel move = new JLabel(str);
        if (moveNum != -1)
            moveLogPnl.add(new JLabel(moveNum + ""));
        moveLogPnl.add(move);
//        moveLogWin.pack();
    }

    public void setBothPlayersClocks(long[] clocks) {
        for (int i = 0; i < clocks.length; i++) {
            setTimerLabel(i, clocks[i]);

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
        int bottomY = 3;
        if (isFlipped) {
            wY = 0;
            bY = bottomY;
        } else {
            wY = bottomY;
            bY = 0;
        }

        gbc.gridx = 0;
        gbc.gridy = wY;
        add(white, gbc);

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
//        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(moveLogScroll, gbc);
//        add(moveLogScroll);
    }

    public void setTimerLabel(int player, long millis) {
        String str = createTimeStr(millis);
        timeLbls[player].setText(str);
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

}
