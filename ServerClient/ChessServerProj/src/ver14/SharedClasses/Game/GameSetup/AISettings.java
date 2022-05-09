package ver14.SharedClasses.Game.GameSetup;

import ver14.SharedClasses.Misc.ParentOf;

import java.io.Serializable;


/**
 * represents Ai Settings. the {@link AiType} and how much time can it think.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class AISettings implements Serializable, ParentOf<TimeFormat> {


    /**
     * The constant EZ_MY_AI.
     */
    public static final AISettings EZ_MY_AI = new AISettings(AiType.MyAi, TimeFormat.ULTRA_BULLET);
    /**
     * The constant EZ_STOCKFISH.
     */
    public static final AISettings EZ_STOCKFISH = new AISettings(AiType.Stockfish, TimeFormat.ULTRA_BULLET);
    /**
     * The Ai type.
     */
    private AiType aiType;
    /**
     * The Move search timeout.
     */
    private TimeFormat moveSearchTimeout;

    /**
     * Instantiates a new Ai parameters.
     */
    public AISettings() {
    }

    /**
     * Instantiates a new Ai parameters.
     *
     * @param other the other
     */
    public AISettings(AISettings other) {
        if (other == null || other.moveSearchTimeout == null || other.aiType == null) {
            return;
        }
        this.aiType = other.aiType;
        this.moveSearchTimeout = new TimeFormat(other.moveSearchTimeout);
    }

    /**
     * Instantiates a new Ai parameters.
     *
     * @param aiType            the ai type
     * @param moveSearchTimeout the move search timeout
     */
    public AISettings(AiType aiType, TimeFormat moveSearchTimeout) {
        this.aiType = aiType;
        this.moveSearchTimeout = moveSearchTimeout;
    }


    /**
     * Gets ai type.
     *
     * @return the ai type
     */
    public AiType getAiType() {
        return aiType;
    }

    /**
     * Sets ai type.
     *
     * @param aiType the ai type
     */
    public void setAiType(AiType aiType) {
        this.aiType = aiType;
    }

    /**
     * Gets move search timeout.
     *
     * @return the move search timeout
     */
    public TimeFormat getMoveSearchTimeout() {
        return moveSearchTimeout;
    }

    /**
     * Sets move search timeout.
     *
     * @param moveSearchTimeout the move search timeout
     */
    public void setMoveSearchTimeout(TimeFormat moveSearchTimeout) {
        this.moveSearchTimeout = moveSearchTimeout;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "AISettings{" +
                "aiType=" + aiType +
                ", moveSearchTimeout=" + moveSearchTimeout +
                '}';
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return aiType == null || moveSearchTimeout == null;
    }

    /**
     * Sets time format.
     *
     * @param timeFormat the time format
     */
    @Override
    public void set(TimeFormat timeFormat) {
        setMoveSearchTimeout(timeFormat);
    }

    /**
     * Ai type.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public enum AiType {
        /**
         * Stockfish ai type.
         */
        Stockfish,
        /**
         * My ai type.
         */
        MyAi;

    }
}
