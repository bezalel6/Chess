package ver14.players;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.SharedClasses.Callbacks.QuestionCallback;
import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.evaluation.GameStatus;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Question;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Threads.ErrorHandling.MyError;
import ver14.game.Game;
import ver14.game.GameSession;

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

    public void askQuestion(Question question, QuestionCallback onAns) {
        onAns.callback(question.getDefaultAnswer());
    }

    public abstract void updateByMove(Move move);

    public abstract void cancelQuestion(Question question, String cause);

    public abstract void interrupt(MyError error);

    public abstract void disconnect(String cause);

    public abstract void waitForMatch();

    public abstract void drawOffered(Callback<Question.Answer> answerCallback);

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