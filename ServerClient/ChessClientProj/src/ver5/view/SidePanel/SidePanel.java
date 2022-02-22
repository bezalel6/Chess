package ver5.view.SidePanel;

import ver5.Client;
import ver5.SharedClasses.*;
import ver5.SharedClasses.ui.MyJButton;
import ver5.SharedClasses.ui.MyLbl;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {
    private static final Font font = FontManager.sidePanel;
    public final AskPlayer askPlayerPnl;
    public final MoveLog moveLog;
    private final Client client;
    private final MyLbl[] timeLbls;
    private final JPanel white, black;
    private final MyJButton resignBtn, offerDrawBtn, addTimeBtn;


    public SidePanel(long millis, boolean isFlipped, Client client) {
        this.client = client;
        String timeControl = StrUtils.createTimeStr(millis);
        timeLbls = new MyLbl[2];
        for (int i = 0; i < timeLbls.length; i++) {
            timeLbls[i] = new MyLbl(timeControl);
            timeLbls[i].setFont(font);
        }
        resignBtn = new MyJButton("Resign", font, client::resignBtnClicked);
        offerDrawBtn = new MyJButton("Offer Draw", font, client::offerDrawBtnClicked);
        addTimeBtn = new MyJButton("Add Time", font, client::addTimeButtonClicked);

        setLayout(new GridBagLayout());

        white = createTimerPnl("White", timeLbls[PlayerColor.WHITE.asInt()]);
        black = createTimerPnl("Black", timeLbls[PlayerColor.BLACK.asInt()]);

        moveLog = new MoveLog();

        askPlayerPnl = new AskPlayer();

        addLayout(isFlipped);

    }

    public JPanel createTimerPnl(String str, MyLbl timerLbl) {
        return new JPanel() {{
            MyLbl lbl = new MyLbl(str);
            lbl.setFont(font);
            setLayout(new BorderLayout());
            add(lbl, BorderLayout.NORTH);
            add(timerLbl, BorderLayout.SOUTH);
        }};
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
        gbc.weighty = 0;
        gbc.gridheight = 2;
        add(white, gbc);

        gbc.gridx = 0;
        gbc.gridy = bottomY + 3;
        gbc.gridwidth = GridBagConstraints.NONE;
        add(askPlayerPnl, gbc);

    }

    public void setFlipped(boolean isFlipped) {
        addLayout(isFlipped);
    }

    public void reset(GameTime gameTime) {
        sync(gameTime);
        moveLog.reset();
        askPlayerPnl.showPnl(false);
    }

    public void sync(GameTime gameTime) {
        setBothPlayersClocks(gameTime);
    }

    public void setBothPlayersClocks(GameTime gameTime) {
        if (gameTime == null)
            return;
        
        for (PlayerColor player : PlayerColor.PLAYER_COLORS) {
            setTimerLabel(player, gameTime.getTimeLeft(player));
        }
    }

    public void setTimerLabel(PlayerColor player, long millis) {
        String str = StrUtils.createTimeStr(millis);
        timeLbls[player.asInt()].setText(str);
    }

    public class AskPlayer extends JPanel {
        private final MyJButton yesBtn, noBtn;
        private final MyLbl header;
        private Question question;

        public AskPlayer() {
            header = new MyLbl() {{
                setFont(font);
            }};
            yesBtn = new MyJButton("Yes", font);
            noBtn = new MyJButton("No", font);

            addLayout();

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

        public void ask(Question question, QuestionCallback callback) {
            yesBtn.setOnClick(answerCallback(Question.Answer.Yes, callback));
            noBtn.setOnClick(answerCallback(Question.Answer.No, callback));
            this.question = question;
            this.header.setText(question.questionStr);
            showPnl(true);
        }

        private Callback answerCallback(Question.Answer answer, QuestionCallback callback) {
            return () -> {
                callback.callback(answer);
                showPnl(false);
            };
        }
    }
}
