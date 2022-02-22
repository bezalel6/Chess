package ver6.server.game;

import ver6.SharedClasses.GameSettings;
import ver6.SharedClasses.PlayerColor;
import ver6.SharedClasses.SavedGames.ArchivedGameInfo;
import ver6.SharedClasses.SavedGames.EstablishedGameInfo;
import ver6.SharedClasses.SavedGames.GameInfo;
import ver6.SharedClasses.SavedGames.UnfinishedGame;
import ver6.SharedClasses.evaluation.GameStatus;
import ver6.SharedClasses.moves.Move;
import ver6.SharedClasses.networking.MyErrors;
import ver6.server.DB.DB;
import ver6.server.Server;
import ver6.server.players.Player;

import java.util.Stack;

public class GameSession extends Thread {
    public final String gameID;
    private final Server server;
    private final Game game;


    public GameSession(UnfinishedGame unfinishedGame, Player creator, Player otherPlayer, Server server) {
        this((EstablishedGameInfo) unfinishedGame, creator, otherPlayer, server);
        PlayerColor clr = unfinishedGame.playerColorToMove;
        game.setCreatorColor(unfinishedGame.isCreatorToMove() ? clr : clr.getOpponent());
    }

    public GameSession(EstablishedGameInfo gameInfo, Player creator, Player otherPlayer, Server server) {
        this((GameInfo) gameInfo, creator, otherPlayer, server);
        game.setMoveStack(gameInfo.getMoveStack());
    }

    public GameSession(GameInfo gameInfo, Player creator, Player otherPlayer, Server server) {
        this(gameInfo.gameId, creator, otherPlayer, gameInfo.gameSettings, server);
    }

    public GameSession(String gameID, Player creator, Player p2, GameSettings gameSettings, Server server) {
        this.gameID = gameID;
        this.server = server;
        game = new Game(creator, p2, gameSettings, this);
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void run() {
        do {
            try {

                GameStatus gameOver = game.startNewGame();
                saveGame(gameOver);
                game.forEachPlayer(player -> player.gameOver(gameOver));
            } catch (MyErrors.Disconnected disconnected) {

            } catch (MyErrors errors) {
                errors.printStackTrace();
            }
        } while (game.askForRematch());

        game.parallelForEachPlayer(p -> {
            try {
                server.gameSetup(p);
            } catch (MyErrors e) {
                e.printStackTrace();
            }
        });
    }

    public void saveGame(GameStatus gameResult) throws MyErrors {
        Player creator = game.getGameCreator();
        if (creator.notSaveWorthy() && creator.getPartner().notSaveWorthy()) {
            return;
        }
        GameSettings gameSettings = game.getGameSettings();
        String un = creator.getUsername();
        String opp = creator.getPartner().getUsername();
        Stack<Move> moveStack = game.getMoveStack();
        if (gameResult.getGameStatusType() == GameStatus.GameStatusType.UNFINISHED) {
            UnfinishedGame unfinishedGame = new UnfinishedGame(
                    gameID,
                    un,
                    gameSettings,
                    opp,
                    gameSettings.getPlayerToMove(),
                    game.getCurrentPlayer().getUsername(),
                    moveStack);
            DB.saveUnFinishedGame(unfinishedGame);
        } else {
            String winner = switch (gameResult.getGameStatusType()) {
                case TIE -> DB.TIE_STR;
                case WIN_OR_LOSS -> gameResult.getWinningColor().equals(creator.getPlayerColor()) ? creator.getUsername() : creator.getPartner().getUsername();

                default -> throw new IllegalStateException("Unexpected value: " + gameResult.getGameStatusType());
            };
            ArchivedGameInfo archivedGameInfo = new ArchivedGameInfo(
                    gameID,
                    un,
                    opp,
                    gameSettings,
                    winner,
                    moveStack
            );
            DB.saveGameResult(archivedGameInfo);
        }


    }

    public void log(String str) {
        server.log(str);
    }
}
