package ver10.SharedClasses.GameSetup;

import ver10.SharedClasses.TimeFormat;

import java.io.Serializable;

public class AiParameters implements Serializable {
    public static final AiParameters EZ_MY_AI = new AiParameters(AiType.MyAi, TimeFormat.BULLET);
    public static final AiParameters EZ_STOCKFISH = new AiParameters(AiType.Stockfish, TimeFormat.BULLET);
    public static final int numOfFields = 1 + TimeFormat.numOfFields;
    private AiType aiType;
    private TimeFormat moveSearchTimeout;

    public AiParameters() {
    }

    public AiParameters(AiParameters other) {
        if (other == null || other.moveSearchTimeout == null || other.aiType == null) {
            return;
        }
        this.aiType = other.aiType;
        this.moveSearchTimeout = new TimeFormat(other.moveSearchTimeout);
    }

    public AiParameters(AiType aiType, TimeFormat moveSearchTimeout) {
        this.aiType = aiType;
        this.moveSearchTimeout = moveSearchTimeout;
    }


    public AiType getAiType() {
        return aiType;
    }

    public void setAiType(AiType aiType) {
        this.aiType = aiType;
    }

    public TimeFormat getMoveSearchTimeout() {
        return moveSearchTimeout;
    }

    public void setMoveSearchTimeout(TimeFormat moveSearchTimeout) {
        this.moveSearchTimeout = moveSearchTimeout;
    }

    @Override
    public String toString() {
        return "AiParameters{" +
                "aiType=" + aiType +
                ", moveSearchTimeout=" + moveSearchTimeout +
                '}';
    }

    public boolean isEmpty() {
        return aiType == null || moveSearchTimeout == null;
    }

    public enum AiType {
        Stockfish, MyAi;

    }
}
