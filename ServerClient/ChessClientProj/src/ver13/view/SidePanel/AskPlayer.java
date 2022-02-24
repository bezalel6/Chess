package ver13.view.SidePanel;

import ver13.SharedClasses.Callbacks.QuestionCallback;
import ver13.SharedClasses.Question;
import ver13.SharedClasses.ui.MyJButton;
import ver13.SharedClasses.ui.MyLbl;

import javax.swing.*;
import java.awt.*;

public class AskPlayer extends JPanel {
    private final MyLbl header;

    public AskPlayer() {
        header = new MyLbl() {{
            setFont(SidePanel.font);
        }};
//        addLayout();

        showPnl(false);
    }

    public void showPnl(boolean show) {
        for (Component component : getComponents()) {
            component.setVisible(show);
        }
    }

    public void ask(Question question, QuestionCallback callback) {
        this.header.setText(question.questionStr);
        addLayout(question, callback);
        showPnl(true);
    }

    private void addLayout(Question question, QuestionCallback callback) {
        removeAll();
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

        for (Question.Answer answer : question.getPossibleAnswers()) {
            add(new MyJButton(answer.answerStr, SidePanel.font, () -> {
                callback.callback(answer);
                showPnl(false);
            }), gbc);
        }
    }
}
