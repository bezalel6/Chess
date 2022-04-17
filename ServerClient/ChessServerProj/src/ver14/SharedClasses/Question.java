package ver14.SharedClasses;

import ver14.SharedClasses.Utils.ArrUtils;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Locale;

public class Question implements Serializable {
    public static final Question Threefold = new Question("Would you like to claim a Threefold repetition?", QuestionType.THREEFOLD, Answer.YES, Answer.NO);
    public static final Question Rematch = new Question("rematch", QuestionType.REMATCH, Answer.YES, Answer.NO);
    public final String questionStr;
    public final QuestionType questionType;
    private final Answer[] possibleAnswers;
    private Answer defaultAnswer;

    public Question(String questionStr, Answer... possibleAnswers) {
        this(questionStr, QuestionType.NO_TYPE, possibleAnswers);
    }

    public Question(String questionStr, QuestionType questionType, Answer... possibleAnswers) {
        this.questionStr = StrUtils.format(questionStr);
        this.questionType = questionType;
        this.possibleAnswers = possibleAnswers;
        this.defaultAnswer = ArrUtils.exists(possibleAnswers);
    }

    public static Question drawOffer(String offeringPlayer) {
        return new Question(offeringPlayer + " offered a draw", QuestionType.DRAW_OFFER, Answer.ACCEPT, Answer.DO_NOT_ACCEPT);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question question)) return false;
        return questionType == question.questionType;
    }

    @Override
    public String toString() {
        return questionType + " " + questionStr;
    }

    public enum QuestionType {
        DRAW_OFFER, THREEFOLD, REMATCH, NO_TYPE
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
