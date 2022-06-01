package ver14.view.SidePanel;

import ver14.SharedClasses.Callbacks.AnswerCallback;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.WinPnl;
import ver14.view.IconManager.Size;

import java.awt.*;

/**
 * Question pnl - represents a single question panel.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class QuestionPnl extends WinPnl {
    /**
     * The Question.
     */
    final Question question;
    private final AskPlayer askPlayer;

    /**
     * Instantiates a new Question pnl.
     *
     * @param question the question
     * @param callback the callback
     */
    public QuestionPnl(AskPlayer askPlayer, Question question, AnswerCallback callback) {
        super(1, new Header(question.questionStr));
        this.askPlayer = askPlayer;
        this.question = question;
        for (Question.Answer answer : question.getPossibleAnswers()) {
            add(createBtn(answer, callback));
        }
    }

    /**
     * Create btn my j button.
     *
     * @param answer the answer
     * @param onAns  the on ans
     * @return the my j button
     */
    private MyJButton createBtn(Question.Answer answer, AnswerCallback onAns) {
        return new MyJButton(answer.answerStr(), SidePanel.font, () -> {
            askPlayer.removeQuestion(this);
            onAns.callback(answer);
//                    showPnl(false);
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return Size.add(new Size(askPlayer.getPreferredSize().width, super.getPreferredSize().height), -10);
    }

    /**
     * Sets replacement for this question's message.
     *
     * @param headerMsg the header msg
     */
    public void setReplacement(String headerMsg) {
        header.setText(headerMsg);
        removeContent();
        add(createBtn(Question.Answer.OK));
    }

    /**
     * Create button.
     *
     * @param answer the answer
     * @return the button
     */
    private MyJButton createBtn(Question.Answer answer) {
        return createBtn(answer, ans -> {
        });
    }
}
