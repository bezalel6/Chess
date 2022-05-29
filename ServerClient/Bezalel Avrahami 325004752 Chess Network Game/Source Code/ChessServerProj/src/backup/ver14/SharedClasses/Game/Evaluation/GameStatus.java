package ver14.SharedClasses.Game.Evaluation;

import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Map;


/**
 * represents a game status.
 * a game status has a {@link SpecificStatus} for all the details about the specific game status
 * and a {@link GameStatusType} for the general info about the game status. is it game over?
 * and if so, is it a tie.
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


    /**
     * Instantiates a new Game status.
     *
     * @param other the other
     */
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

    /**
     * Is check or mate boolean.
     *
     * @return the boolean
     */
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

}