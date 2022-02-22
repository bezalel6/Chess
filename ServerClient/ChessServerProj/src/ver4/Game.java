package ver4;

import ver4.SharedClasses.PlayerColor;
import ver4.SharedClasses.evaluation.GameStatus;
import ver4.SharedClasses.messages.Message;
import ver4.SharedClasses.moves.Move;
import ver4.model_classes.Model;
import ver4.server.Server;
import ver4.server.players.Player;
import ver4.server.players.PlayerAI;
import ver4.server.players.PlayerNet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Game.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com)  10/11/2021
 */
public class Game {
    public static final int ROWS = 8;
    public static final int COLS = 8;
    private final Server server;
    private final Player[] players;
    private final Model model;
    private boolean rematch = false;

    public Game(Player[] players, Server server) {
        this.server = server;
        this.players = players;
//        String f = "6k1/2R5/8/1p2p2p/1P2Pn1P/5Pq1/4r3/7K w - - 0 1";
        this.model = new Model();
        setPartners();
        randomizeColors();
//        setAiColor(PlayerColor.WHITE);
    }

    public Game(Game other) {
        this(other.players, other.server);
    }

    public Model getModel() {
        return model;
    }

    private void setAiColor(PlayerColor color) {
        Arrays.stream(players).forEach(player -> {
            if (player instanceof PlayerAI) {
                player.setPlayerColor(color);
                player.getPartner().setPlayerColor(color.getOpponent());
            }
        });
    }

    private void setPartners() {
        players[0].setPartner(players[1]);
        players[1].setPartner(players[0]);
    }

    private void randomizeColors() {
        int white = new Random().nextInt(2);
        players[white].setPlayerColor(PlayerColor.WHITE);
        players[Math.abs((white - 1) * -1)].setPlayerColor(PlayerColor.BLACK);
    }

    public void runGame(AtomicBoolean keepRunning) {
        initPlayersGames();
        GameStatus gameOverStatus = GameStatus.serverStoppedGame();
        server.log("Starting game " + this);
        while (keepRunning.get()) {
            Player currentPlayer = getCurrentPlayer();

            currentPlayer.getPartner().waitTurn();

            Move move = currentPlayer.getMove();
            if (move == null) {
                gameOverStatus = GameStatus.playerDisconnected(currentPlayer.getPlayerColor().getOpponent());
                break;
            }
            if (move.getThreefoldStatus() == Move.ThreefoldStatus.CLAIMED) {
                gameOverStatus = GameStatus.threeFoldRepetition();
                break;
            }
            move.getMoveAnnotation().resetPromotion(move);
            move.setMovingColor(currentPlayer.getPlayerColor());

            makeMove(move);

            server.log("Move = " + move + " Made By = " + currentPlayer);

            GameStatus gameStatus = move.getMoveEvaluation().getGameStatus();
            if (gameStatus.isGameOver()) {
                gameOverStatus = gameStatus;
                break;
            }
        }
        gameOver(gameOverStatus);
    }

    private void gameOver(GameStatus gameStatus) {
        for (Player player : players) {
            player.gameOver(gameStatus);
        }
    }

    private Player getCurrentPlayer() {
        return Arrays.stream(players)
                .filter(player -> player.getPlayerColor().equals(model.getCurrentPlayer()))
                .findFirst()
                .orElseThrow();
    }

    private void makeMove(Move move) {
        model.makeMove(move);

        for (Player player : players) {
            if (player instanceof PlayerNet) {
                ((PlayerNet) player).getSocketToClient().writeMessage(Message.makeMove(move));
            }
        }
    }

    private void initPlayersGames() {
        for (Player player : players) {
            initGameForPlayer(player);
        }
    }

    private void initGameForPlayer(Player player) {
        player.setModel(model);
        if (player instanceof PlayerNet) {
            ArrayList<Move> possibleMoves = model.generateAllMoves(player.getPlayerColor());
            ((PlayerNet) player).getSocketToClient().writeMessage(Message.initGame(model.getLogicBoard(), possibleMoves, player.getPlayerColor()));
        }
    }

    public Game rematch() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        Arrays.stream(players).parallel().forEach(player -> {
            boolean res = player.askForRematch();
            if (atomicBoolean.get() && !res) {
                atomicBoolean.set(false);
                if (player.getPartner() instanceof PlayerNet partner) {
                    partner.getSocketToClient().sendIsAlive();
                }
            }
        });
        return atomicBoolean.get() ? new Game(this) : null;
    }

    public Player[] getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        return "Game{" +
                ", players=" + Arrays.toString(players) +
                '}';
    }
}
