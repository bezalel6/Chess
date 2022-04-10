package ver14.SharedClasses;

import ver14.SharedClasses.Utils.ArrUtils;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Locale;

public class Question implements Serializable {
    public static final Question Threefold = new Question("Would you like to claim a Threefold repetition?", Answer.YES, Answer.NO);
    public static final Question Rematch = new Question("rematch", Answer.YES, Answer.NO);

    public final String questionStr;
    private final Answer[] possibleAnswers;
    private Answer defaultAnswer;
    private Answer answer;

    public Question(String questionStr, Answer... possibleAnswers) {
        this.questionStr = StrUtils.format(questionStr);
        this.possibleAnswers = possibleAnswers;
        this.defaultAnswer = ArrUtils.exists(possibleAnswers);
    }

    public static Question drawOffer(String offeringPlayer) {
        return new Question(offeringPlayer + " offered a draw", Answer.ACCEPT, Answer.DO_NOT_ACCEPT);
    }

    public Answer getDefaultAnswer() {
        return defaultAnswer;
    }

    public void setDefaultAnswer(Answer defaultAnswer) {
        this.defaultAnswer = defaultAnswer;
    }

    public String getQuestionStr() {
        return questionStr;
    }

    public Answer[] getPossibleAnswers() {
        return possibleAnswers;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public enum Answer {
        YES, NO, ACCEPT, DO_NOT_ACCEPT, OK;
        public final String answerStr;

        Answer() {
            this.answerStr = StrUtils.format(name().replaceAll("_", " ").toLowerCase(Locale.ROOT));
        }

        Answer(String answerStr) {
            this.answerStr = answerStr;
        }
    }
}
