package ver14.SharedClasses.Game.Evaluation;

import ver14.SharedClasses.Utils.StrUtils;

/**
 * represents a specific game status with all its details.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum SpecificStatus {
    /**
     * Checkmate.
     */
    Checkmate,
    /**
     * Timed out.
     */
    TimedOut {
        @Override
        public String toString() {
            return "Time Out";
        }
    },
    /**
     * Timed out vs insufficient material.
     */
    TimedOutVsInsufficientMaterial(GameStatusType.TIE) {
        @Override
        public String toString() {
            return "Time Out vs Insufficient Material";
        }
    },
    /**
     * Resignation.
     */
    Resignation,
    /**
     * Game goes on .
     */
    GameGoesOn(GameStatusType.GAME_GOES_ON),
    /**
     * Three fold repetition.
     */
    ThreeFoldRepetition(GameStatusType.TIE),
    /**
     * Stalemate.
     */
    Stalemate(GameStatusType.TIE),
    /**
     * Insufficient material.
     */
    InsufficientMaterial(GameStatusType.TIE),
    /**
     * Fifty move rule.
     */
    FiftyMoveRule(GameStatusType.TIE),
    /**
     * The Tie by agreement.
     */
    TieByAgreement(GameStatusType.TIE) {
        @Override
        public String toString() {
            return "Agreement";
        }
    },
    /**
     * The Player disconnected vs ai.
     */
    PlayerDisconnectedVsAi(GameStatusType.UNFINISHED) {
        @Override
        public String toString() {
            return "Player Disconnected";
        }
    },
    /**
     * The Player disconnected vs real.
     */
    PlayerDisconnectedVsReal {
        @Override
        public String toString() {
            return "Other Player Disconnected";
        }
    },
    /**
     * Server stopped game.
     */
    ServerStoppedGame(GameStatusType.TIE);
    /**
     * The Game status type.
     */
    public final GameStatusType gameStatusType;

    /**
     * Instantiates a new Specific status.
     */
    SpecificStatus() {
        this(GameStatusType.WIN_OR_LOSS);
    }

    /**
     * Instantiates a new Specific status.
     *
     * @param gameStatusType the game status type
     */
    SpecificStatus(GameStatusType gameStatusType) {
        this.gameStatusType = gameStatusType;
    }


    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return StrUtils.format(name());
    }
}
