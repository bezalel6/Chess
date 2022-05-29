package ver14.SharedClasses.Misc;

import ver14.SharedClasses.Utils.ArrUtils;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;


/**
 * represents a Question to ask a player.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Question implements Serializable {
    /**
     * The constant Threefold.
     */
    public static final Question Threefold = new Question("Would you like to claim a Threefold repetition?", QuestionType.THREEFOLD, Answer.YES, Answer.NO);
    /**
     * The constant Rematch.
     */
    public static final Question Rematch = new Question("rematch", QuestionType.REMATCH, Answer.YES, Answer.NO);
    /**
     * The Question str.
     */
    public final String questionStr;
    /**
     * The Question type.
     */
    public final QuestionType questionType;
    /**
     * The Possible answers.
     */
    private final Answer[] possibleAnswers;
    /**
     * The Default answer.
     */
    private Answer defaultAnswer;

    /**
     * Instantiates a new Question.
     *
     * @param questionStr     the question str
     * @param possibleAnswers the possible answers
     */
    public Question(String questionStr, Answer... possibleAnswers) {
        this(questionStr, QuestionType.NO_TYPE, possibleAnswers);
    }

    /**
     * Instantiates a new Question.
     *
     * @param questionStr     the question str
     * @param questionType    the question type
     * @param possibleAnswers the possible answers
     */
    public Question(String questionStr, QuestionType questionType, Answer... possibleAnswers) {
        this.questionStr = StrUtils.format(questionStr);
        this.questionType = questionType;
        this.possibleAnswers = possibleAnswers;
        this.defaultAnswer = ArrUtils.exists(possibleAnswers);
    }

    /**
     * Draw offer question.
     *
     * @param offeringPlayer the offering player
     * @return the question
     */
    public static Question drawOffer(String offeringPlayer) {
        return new Question(offeringPlayer + " offered a draw", QuestionType.DRAW_OFFER, Answer.ACCEPT, Answer.DO_NOT_ACCEPT);
    }

    /**
     * Gets default answer.
     *
     * @return the default answer
     */
    public Answer getDefaultAnswer() {
        return defaultAnswer;
    }

    /**
     * Sets default answer.
     *
     * @param defaultAnswer the default answer
     */
    public void setDefaultAnswer(Answer defaultAnswer) {
        this.defaultAnswer = defaultAnswer;
    }

    /**
     * Gets question str.
     *
     * @return the question str
     */
    public String getQuestionStr() {
        return questionStr;
    }

    /**
     * Get possible answers answer [ ].
     *
     * @return the answer [ ]
     */
    public Answer[] getPossibleAnswers() {
        return possibleAnswers;
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question question)) return false;
        return questionType == question.questionType && questionStr.equals(question.questionStr);
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return questionType + " " + questionStr;
    }

    /**
     * Question type.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public enum QuestionType {
        /**
         * Draw offer question type.
         */
        DRAW_OFFER,
        /**
         * Threefold question type.
         */
        THREEFOLD,
        /**
         * Rematch question type.
         */
        REMATCH,
        /**
         * No type question type.
         */
        NO_TYPE
    }

    /**
     * Answer.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public record Answer(String answerStr) implements Serializable {
        /**
         * The constant YES.
         */
        public static final Answer YES = new Answer("YES");
        /**
         * The constant NO.
         */
        public static final Answer NO = new Answer("NO");
        /**
         * The constant ACCEPT.
         */
        public static final Answer ACCEPT = new Answer("ACCEPT");
        /**
         * The constant DO_NOT_ACCEPT.
         */
        public static final Answer DO_NOT_ACCEPT = new Answer("DO NOT ACCEPT");
        /**
         * The constant OK.
         */
        public static final Answer OK = new Answer("OK");
    }
}
