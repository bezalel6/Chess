package ver14.SharedClasses.Game.GameSetup;

import java.io.Serializable;


/**
 * Ai parameters.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class AiParameters implements Serializable, TimeFormatComponent {
    /**
     * The constant EZ_MY_AI.
     */
    public static final AiParameters EZ_MY_AI = new AiParameters(AiType.MyAi, TimeFormat.ULTRA_BULLET);
    /**
     * The constant EZ_STOCKFISH.
     */
    public static final AiParameters EZ_STOCKFISH = new AiParameters(AiType.Stockfish, TimeFormat.ULTRA_BULLET);
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
    public AiParameters() {
    }

    /**
     * Instantiates a new Ai parameters.
     *
     * @param other the other
     */
    public AiParameters(AiParameters other) {
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
    public AiParameters(AiType aiType, TimeFormat moveSearchTimeout) {
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
        return "AiParameters{" +
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
    public void setTimeFormat(TimeFormat timeFormat) {
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
