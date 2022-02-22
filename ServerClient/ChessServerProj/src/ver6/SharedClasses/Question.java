package ver6.SharedClasses;

import java.io.Serializable;

public class Question implements Serializable {
    public static final Question Threefold = new Question("Would you like to claim a Threefold repetition?");
    public static final Question Rematch = new Question("rematch");

    public final String questionStr;
    private Answer answer;

    public Question(String questionStr) {
        this.questionStr = StrUtils.uppercaseFirstLetters(questionStr);
    }

    public boolean getAnswer() {
        return answer == Answer.Yes;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public enum Answer {
        Yes, No
    }
}
