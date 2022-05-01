package ver14.SharedClasses.Game.Evaluation;

import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Map;


/**
 * Game status - represents a game status .
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class GameStatus implements Serializable {
    /**
     * The Winning player color.
     */
    private final PlayerColor winningPlayerColor;
    /**
     * The Specific game status.
     */
    private final SpecificStatus specificStatus;
    /**
     * The status's Depth.
     */
    private int depth = -1;
    /**
     * The Checked king loc.
     */
    private Location checkedKingLoc = null;
    /**
     * The Game status type.
     */
    private GameStatusType gameStatusType;
    /**
     * The Custom description str.
     */
    private String customStr = null;


    public GameStatus(GameStatus other) {
        this.winningPlayerColor = other.winningPlayerColor;
        this.specificStatus = other.specificStatus;
        this.depth = other.depth;
        this.checkedKingLoc = other.checkedKingLoc;
        this.gameStatusType = other.gameStatusType;
        this.customStr = other.customStr;
    }

    /**
     * Instantiates a new Game status.
     *
     * @param specificStatus the specific status
     */
    private GameStatus(SpecificStatus specificStatus) {
        this(null, specificStatus);
    }

    /**
     * Instantiates a new Game status.
     *
     * @param winningPlayerColor the winning player color
     * @param specificStatus     the specific status
     */
    private GameStatus(PlayerColor winningPlayerColor, SpecificStatus specificStatus) {
        this.specificStatus = specificStatus;
        this.winningPlayerColor = winningPlayerColor;
        this.gameStatusType = specificStatus.gameStatusType;
    }

    /**
     * Checkmate game status.
     *
     * @param winningPlayerColor the winning player color
     * @param matedKing          the mated king
     * @return the game status
     */
    public static GameStatus checkmate(PlayerColor winningPlayerColor, Location matedKing) {
        return new GameStatus(winningPlayerColor, SpecificStatus.Checkmate) {{
            setCheckedKingLoc(matedKing);
        }};
    }

    /**
     * Game goes on game status.
     *
     * @return the game status
     */
    public static GameStatus gameGoesOn() {
        return new GameStatus(SpecificStatus.GameGoesOn);
    }

    /**
     * Tie by agreement game status.
     *
     * @return the game status
     */
    public static GameStatus tieByAgreement() {
        return new GameStatus(SpecificStatus.TieByAgreement);
    }

    /**
     * Stalemate game status.
     *
     * @return the game status
     */
    public static GameStatus stalemate() {
        return new GameStatus(SpecificStatus.Stalemate);
    }

    /**
     * Fifty move rule game status.
     *
     * @return the game status
     */
    public static GameStatus fiftyMoveRule() {
        return new GameStatus(SpecificStatus.FiftyMoveRule);
    }

    /**
     * Server stopped game game status.
     *
     * @param cause the cause
     * @return the game status
     */
    public static GameStatus serverStoppedGame(String cause) {
        return new GameStatus(SpecificStatus.ServerStoppedGame) {{
            setCustomStr("Game Stopped By Server! " + cause);
        }};
    }

    /**
     * Sets custom str.
     *
     * @param customStr the custom str
     */
    public void setCustomStr(String customStr) {
        this.customStr = customStr;
    }

    /**
     * Three fold repetition game status.
     *
     * @return the game status
     */
    public static GameStatus threeFoldRepetition() {
        return new GameStatus(SpecificStatus.ThreeFoldRepetition);
    }

    /**
     * Insufficient material game status.
     *
     * @return the game status
     */
    public static GameStatus insufficientMaterial() {
        return new GameStatus(SpecificStatus.InsufficientMaterial);
    }

    /**
     * Player disconnected game status.
     *
     * @param disconnectedPlayer the disconnected player
     * @param isVsAi             the is vs ai
     * @return the game status
     */
    public static GameStatus playerDisconnected(PlayerColor disconnectedPlayer, boolean isVsAi) {
        return new GameStatus(disconnectedPlayer.getOpponent(), isVsAi ? SpecificStatus.PlayerDisconnectedVsAi : SpecificStatus.PlayerDisconnectedVsReal);
    }

    /**
     * Timed out game status.
     *
     * @param timedOutPlayer       the timed out player
     * @param isSufficientMaterial the is sufficient material
     * @return the game status
     */
    public static GameStatus timedOut(PlayerColor timedOutPlayer, boolean isSufficientMaterial) {
        return new GameStatus(timedOutPlayer.getOpponent(), isSufficientMaterial ? SpecificStatus.TimedOut : SpecificStatus.TimedOutVsInsufficientMaterial);
    }

    /**
     * Player resigned game status.
     *
     * @param resignedPlayer the resigned player
     * @return the game status
     */
    public static GameStatus playerResigned(PlayerColor resignedPlayer) {
        return new GameStatus(resignedPlayer.getOpponent(), SpecificStatus.Resignation);
    }

    /**
     * Is disconnected boolean.
     *
     * @return the boolean
     */
    public boolean isDisconnected() {
        return specificStatus == SpecificStatus.ServerStoppedGame || specificStatus == SpecificStatus.PlayerDisconnectedVsAi || specificStatus == SpecificStatus.PlayerDisconnectedVsReal;
    }

    /**
     * Gets checked king loc.
     *
     * @return the checked king loc
     */
    public Location getCheckedKingLoc() {
        return checkedKingLoc;
    }

    /**
     * Sets checked king loc.
     *
     * @param checkedKingLoc the checked king loc
     */
    void setCheckedKingLoc(Location checkedKingLoc) {
        this.checkedKingLoc = checkedKingLoc;
    }

    /**
     * Gets winning color.
     *
     * @return the winning color
     */
    public PlayerColor getWinningColor() {
        return winningPlayerColor;
    }

    /**
     * Gets game status type.
     *
     * @return the game status type
     */
    public GameStatusType getGameStatusType() {
        return gameStatusType;
    }

    /**
     * Sets in check.
     *
     * @param checkedKingLoc the checked king loc
     */
    public void setInCheck(Location checkedKingLoc) {
        this.gameStatusType = GameStatusType.CHECK;
        setCheckedKingLoc(checkedKingLoc);
    }

    public boolean isCheckOrMate() {
        return isCheck() || specificStatus == SpecificStatus.Checkmate;
    }

    /**
     * Is check boolean.
     *
     * @return the boolean
     */
    public boolean isCheck() {
        return gameStatusType == GameStatusType.CHECK;
    }

    /**
     * Gets detailed str.
     *
     * @return the detailed str
     */
    public String getDetailedStr() {
        return getDetailedStr(null);
    }

    /**
     * Gets detailed str.
     *
     * @param playerUsernamesMap the player usernames map
     * @return the detailed str
     */
    public String getDetailedStr(Map<PlayerColor, String> playerUsernamesMap) {
        if (!StrUtils.isEmpty(customStr))
            return customStr;
        String winning = "";
        if (winningPlayerColor != null) {
            winning = playerUsernamesMap == null ? winningPlayerColor.getName() : playerUsernamesMap.get(winningPlayerColor);
        }

        return winning + " " + gameStatusType.gameOverStr + " By " + specificStatus + (isGameOver() && depth != -1 ? " In " + depth : "");
    }

    /**
     * Is game over boolean.
     *
     * @return the boolean
     */
    public boolean isGameOver() {
        return gameStatusType.isGameOver();
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return gameStatusType.annotation;
    }

    /**
     * Sets depth.
     *
     * @param depth the depth
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Gets specific status.
     *
     * @return the specific status
     */
    public SpecificStatus getSpecificStatus() {
        return specificStatus;
    }

    /**
     * Specific status - specific game status .
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

    /**
     * Game status type .
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public enum GameStatusType implements Serializable {
        /**
         * Tie game status type.
         */
        TIE("½½", "Tie"),
        /**
         * Check game status type.
         */
        CHECK("+"),
        /**
         * Game goes on game status type.
         */
        GAME_GOES_ON(""),
        /**
         * Win or loss game status type.
         */
        WIN_OR_LOSS("#", "Won"),
        /**
         * Unfinished game status type.
         */
        UNFINISHED("...");

        /**
         * The game status annotation.
         */
        public final String annotation;
        /**
         * game over str
         */
        public final String gameOverStr;

        /**
         * Instantiates a new Game status type.
         *
         * @param annotation the annotation
         */
        GameStatusType(String annotation) {
            this(annotation, annotation);
        }

        /**
         * Instantiates a new Game status type.
         *
         * @param annotation  the annotation
         * @param gameOverStr the game over str
         */
        GameStatusType(String annotation, String gameOverStr) {
            this.annotation = annotation;
            this.gameOverStr = gameOverStr;
        }


        /**
         * Is game over.
         *
         * @return true if is game over. false otherwise
         */
        public boolean isGameOver() {
            return this == TIE || this == WIN_OR_LOSS || this == UNFINISHED;
        }
    }
}