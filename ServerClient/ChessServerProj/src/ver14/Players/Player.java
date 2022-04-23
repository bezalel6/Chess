package ver14.Players;

import ver14.Game.Game;
import ver14.Game.GameSession;
import ver14.SharedClasses.Callbacks.AnswerCallback;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;

/*
 * Player
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * Player -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * Player -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

/**
 * Player.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com) 10/11/2021
 */
public abstract class Player {
    protected Game game;
    protected GameSession gameSession;
    protected PlayerColor playerColor;
    protected Player partner = null;
    private String username;

    public Player(String id) {
        this.username = id;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public Player getPartner() {
        return partner;
    }

    public void setPartner(Player partner) {
        this.partner = partner;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void initGame(Game game) {
        this.game = game;
        initGame();
    }

    protected abstract void initGame();

    public abstract void error(String error);

    public abstract Move getMove();

    public abstract void waitTurn();

    public abstract void gameOver(GameStatus gameStatus);

//    public abstract void cancelQuestion(Question question, String cancelMsg);


    public void askQuestion(Question question, AnswerCallback onAns) {
        onAns.callback(question.getDefaultAnswer());
    }

    public abstract void updateByMove(Move move);

    public abstract void cancelQuestion(Question question, String cause);

    public abstract void interrupt(MyError error);

    public abstract void disconnect(String cause);

    public abstract void waitForMatch();

    public abstract GameSettings getGameSettings(SyncedItems<?> joinableGames, SyncedItems<?> resumableGames);

    public void resigned() {
        assert gameSession != null;
        gameSession.resigned(this);
    }

    public Game getOnGoingGame() {
        return game;
    }

    @Override
    public String toString() {
        if (playerColor != null) {
            return username + " " + playerColor;
        }
        return username;
    }

    public boolean isSaveWorthy() {
        return !isGuest() && !isAi();
    }

    public abstract boolean isGuest();

    public abstract boolean isAi();

    public boolean isConnected() {
        return true;
    }
}
