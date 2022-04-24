package ver14.SharedClasses.Game.Evaluation;

import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.Serializable;
import java.util.Map;

/*
 * GameStatus
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * GameStatus -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * GameStatus -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class GameStatus implements Serializable {
    private final PlayerColor winningPlayerColor;
    private final SpecificStatus specificStatus;
    private int depth = -1;
    private Location checkedKingLoc = null;
    private GameStatusType gameStatusType;
    private String customStr = null;


    private GameStatus(SpecificStatus specificStatus) {
        this(null, specificStatus);
    }

    private GameStatus(PlayerColor winningPlayerColor, SpecificStatus specificStatus) {
        this.specificStatus = specificStatus;
        this.winningPlayerColor = winningPlayerColor;
        this.gameStatusType = specificStatus.gameStatusType;
    }

    public static GameStatus checkmate(PlayerColor winningPlayerColor, Location matedKing) {
        return new GameStatus(winningPlayerColor, SpecificStatus.Checkmate) {{
            setCheckedKingLoc(matedKing);
        }};
    }

    public static GameStatus gameGoesOn() {
        return new GameStatus(SpecificStatus.GameGoesOn);
    }

    public static GameStatus tieByAgreement() {
        return new GameStatus(SpecificStatus.TieByAgreement);
    }

    public static GameStatus stalemate() {
        return new GameStatus(SpecificStatus.Stalemate);
    }

    public static GameStatus fiftyMoveRule() {
        return new GameStatus(SpecificStatus.FiftyMoveRule);
    }

    public static GameStatus serverStoppedGame(String cause) {
        return new GameStatus(SpecificStatus.ServerStoppedGame) {{
            setCustomStr("Game Stopped By Server! " + cause);
        }};
    }

    public void setCustomStr(String customStr) {
        this.customStr = customStr;
    }

    public static GameStatus threeFoldRepetition() {
        return new GameStatus(SpecificStatus.ThreeFoldRepetition);
    }

    public static GameStatus insufficientMaterial() {
        return new GameStatus(SpecificStatus.InsufficientMaterial);
    }

    public static GameStatus playerDisconnected(PlayerColor disconnectedPlayer, boolean isVsAi) {
        return new GameStatus(disconnectedPlayer.getOpponent(), isVsAi ? SpecificStatus.PlayerDisconnectedVsAi : SpecificStatus.PlayerDisconnectedVsReal);
    }

    public static GameStatus timedOut(PlayerColor timedOutPlayer, boolean isSufficientMaterial) {
        return new GameStatus(timedOutPlayer.getOpponent(), isSufficientMaterial ? SpecificStatus.TimedOut : SpecificStatus.TimedOutVsInsufficientMaterial);
    }

    public static GameStatus playerResigned(PlayerColor resignedPlayer) {
        return new GameStatus(resignedPlayer.getOpponent(), SpecificStatus.Resignation);
    }

    public boolean isDisconnected() {
        return specificStatus == SpecificStatus.ServerStoppedGame || specificStatus == SpecificStatus.PlayerDisconnectedVsAi || specificStatus == SpecificStatus.PlayerDisconnectedVsReal;
    }

    public Location getCheckedKingLoc() {
        return checkedKingLoc;
    }

    void setCheckedKingLoc(Location checkedKingLoc) {
        this.checkedKingLoc = checkedKingLoc;
    }

    public PlayerColor getWinningColor() {
        return winningPlayerColor;
    }

    public GameStatusType getGameStatusType() {
        return gameStatusType;
    }

    public void setInCheck(Location checkedKingLoc) {
        this.gameStatusType = GameStatusType.CHECK;
        setCheckedKingLoc(checkedKingLoc);
    }

    public boolean isCheck() {
        return gameStatusType == GameStatusType.CHECK || checkedKingLoc != null;
    }

    public String getDetailedStr() {
        return getDetailedStr(null);
    }

    public String getDetailedStr(Map<PlayerColor, String> playerUsernamesMap) {
        if (!StrUtils.isEmpty(customStr))
            return customStr;
        String winning = "";
        if (winningPlayerColor != null) {
            winning = playerUsernamesMap == null ? winningPlayerColor.getName() : playerUsernamesMap.get(winningPlayerColor);
        }

        return winning + " " + gameStatusType.gameOverStr + " By " + specificStatus + (isGameOver() && depth != -1 ? " In " + depth : "");
    }

    public boolean isGameOver() {
        return gameStatusType.isGameOver();
    }

    @Override
    public String toString() {
        return gameStatusType.annotation;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public SpecificStatus getSpecificStatus() {
        return specificStatus;
    }

    public enum SpecificStatus {
        Checkmate,
        TimedOut {
            @Override
            public String toString() {
                return "Time Out";
            }
        },
        TimedOutVsInsufficientMaterial(GameStatusType.TIE) {
            @Override
            public String toString() {
                return "Time Out vs Insufficient Material";
            }
        },
        Resignation,
        GameGoesOn(GameStatusType.GAME_GOES_ON),
        ThreeFoldRepetition(GameStatusType.TIE),
        Stalemate(GameStatusType.TIE),
        InsufficientMaterial(GameStatusType.TIE),
        FiftyMoveRule(GameStatusType.TIE),
        TieByAgreement(GameStatusType.TIE) {
            @Override
            public String toString() {
                return "Agreement";
            }
        },
        PlayerDisconnectedVsAi(GameStatusType.UNFINISHED) {
            @Override
            public String toString() {
                return "Player Disconnected";
            }
        },
        PlayerDisconnectedVsReal {
            @Override
            public String toString() {
                return "Other Player Disconnected";
            }
        },
        ServerStoppedGame(GameStatusType.TIE);
        public final GameStatusType gameStatusType;

        SpecificStatus() {
            this(GameStatusType.WIN_OR_LOSS);
        }

        SpecificStatus(GameStatusType gameStatusType) {
            this.gameStatusType = gameStatusType;
        }


        @Override
        public String toString() {
            return StrUtils.format(name());
        }
    }

    public enum GameStatusType implements Serializable {
        TIE("½½", "Tie"), CHECK("+"), GAME_GOES_ON(""), WIN_OR_LOSS("#", "Won"), UNFINISHED("...");

        //        public static final GameStatusType[] GAME_OVERS = {TIE, WIN_OR_LOSS};
        public final String annotation, gameOverStr;

        GameStatusType(String annotation) {
            this(annotation, annotation);
        }

        GameStatusType(String annotation, String gameOverStr) {
            this.annotation = annotation;
            this.gameOverStr = gameOverStr;
        }


        public boolean isGameOver() {
            return this == TIE || this == WIN_OR_LOSS || this == UNFINISHED;
        }
    }
}