package ver4.view;

import ver4.Client;
import ver4.SharedClasses.PlayerColor;
import ver4.SharedClasses.StrUtils;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {
    private static final Font font = new Font(null, Font.BOLD, 20);
    public final AskPlayer askPlayerPnl;
    private final Client client;
    private final JLabel[] timeLbls;
    private final JPanel white, black;
    private final MyJButton resignBtn, offerDrawBtn, addTimeBtn;
    private final MoveLog moveLog;


    public SidePanel(long millis, boolean isFlipped, Client client) {
        this.client = client;
        String timeControl = StrUtils.createTimeStr(millis);
        timeLbls = new JLabel[2];
        for (int i = 0; i < timeLbls.length; i++) {
            timeLbls[i] = new JLabel(timeControl);
            timeLbls[i].setFont(font);
        }
        resignBtn = new MyJButton("Resign", font);
        offerDrawBtn = new MyJButton("Offer Draw", font);
        addTimeBtn = new MyJButton("Add Time", font);

        setLayout(new GridBagLayout());

        white = createTimerPnl("White", timeLbls[PlayerColor.WHITE.asInt()]);
        black = createTimerPnl("Black", timeLbls[PlayerColor.BLACK.asInt()]);

        moveLog = new MoveLog();

        askPlayerPnl = new AskPlayer();

        addLayout(isFlipped);

    }

    public void setBothPlayersClocks(long[] clocks) {
        for (PlayerColor player : PlayerColor.PLAYER_COLORS) {
            setTimerLabel(player, clocks[player.asInt()]);
        }
    }

    public void setFlipped(boolean isFlipped) {
        addLayout(isFlipped);
    }

    private void addLayout(boolean isFlipped) {
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
        gbc.weightx = 10;
        gbc.weighty = 5;
        add(moveLog, gbc);

        gbc.gridx = 0;
        gbc.gridy = wY;
        gbc.weighty = 3;
        gbc.gridheight = 2;
        add(white, gbc);

        gbc.gridx = 0;
        gbc.gridy = bottomY + 3;
        gbc.gridwidth = GridBagConstraints.NONE;
        add(askPlayerPnl, gbc);

    }

    public void setTimerLabel(PlayerColor player, long millis) {
        String str = StrUtils.createTimeStr(millis);
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
        askPlayerPnl.showPnl(false);
    }

    static class MoveLog extends JPanel {
        private final MyJButton forward, back, start, end;
        private JPanel moveLogPnl;
        private JScrollPane moveLogScroll;
        private boolean justAddedMove = false;

        public MoveLog() {
            forward = new MyJButton(">", font);
            back = new MyJButton("<", font);
            start = new MyJButton("<|", font);
            end = new MyJButton(">|", font);

            addLayout();

        }

        private void addLayout() {
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
                getVerticalScrollBar().addAdjustmentListener(e -> {
                    if (justAddedMove) {
                        e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                        justAddedMove = false;
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
            justAddedMove = true;

        }

        public void reset() {
            moveLogPnl.removeAll();
        }
    }

    public class AskPlayer extends JPanel {
        private final MyJButton yesBtn, noBtn;
        private final JLabel header;
        private Question question;

        public AskPlayer() {
            header = new JLabel() {{
                setFont(font);
            }};
            yesBtn = new MyJButton("Yes", font, this::clickedYes);
            noBtn = new MyJButton("No", font, this::clickedNo);

            addLayout();

            showPnl(false);
        }

        public void ask(Question question) {
            this.question = question;
            this.header.setText(question.questionStr);
            showPnl(true);
        }

        private void clickedYes() {
            client.answeredQuestionYes(question);
            showPnl(false);
        }

        private void clickedNo() {
            client.answeredQuestionNo(question);
            showPnl(false);
        }

        private void addLayout() {
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 3, 3, 3);
            gbc.gridheight = 2;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            add(header, gbc);

            gbc.gridheight = 1;
            gbc.gridwidth = GridBagConstraints.RELATIVE;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridy = GridBagConstraints.RELATIVE;
            gbc.weightx = 1;

            add(yesBtn, gbc);

            add(noBtn, gbc);
        }

        public void showPnl(boolean show) {
            for (Component component : getComponents()) {
                component.setVisible(show);
            }
        }

        public enum Question {
            Threefold("Would you like to claim a Threefold repetition?"), REMATCH("rematch");
            public final String questionStr;

            Question(String questionStr) {
                this.questionStr = StrUtils.wrapInHtml(StrUtils.uppercaseFirstLetters(questionStr));
            }
        }

    }
}
