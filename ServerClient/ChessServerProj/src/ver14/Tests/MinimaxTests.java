package ver14.Tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ver14.Game.Game;
import ver14.Model.Minimax.Minimax;
import ver14.Players.PlayerAI.MyAi;
import ver14.Server;
import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.GameSetup.TimeFormat;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.Moves.BasicMove;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Sync.SyncedItems;

import java.lang.reflect.Method;


public class MinimaxTests extends Tests {

    public static void main(String[] args) {
        new MinimaxTests().minimaxVsStockfish();
    }

    @Test(testName = "minimax vs stockfish")
    private void minimaxVsStockfish() {
        MyAi ai = new MyAi(new AiParameters(AiParameters.AiType.MyAi, new TimeFormat(3000))) {
            @Override
            public GameSettings getGameSettings(SyncedItems<?> joinableGames, SyncedItems<?> resumableGames) {
                return new GameSettings(PlayerColor.WHITE, TimeFormat.BULLET, null, new AiParameters(AiParameters.AiType.Stockfish, new TimeFormat(1000)), GameSettings.GameType.CREATE_NEW);
            }
        };

        Game.showGameView = true;
        Server server = new Server();
        server.gameSetup(ai);
        server.runServer();
    }

    @BeforeMethod
    @Override
    protected void BeforeMethod(Method method, Object[] testData) {
        super.BeforeMethod(method, testData);
        Minimax.SHOW_UI = true;
        Minimax.LOG = true;
    }

    @Test(testName = "minimax vs minimax")
    private void minimaxVsMinimax() {
        AiParameters parms = new AiParameters(AiParameters.AiType.MyAi, new TimeFormat(2000));
        MyAi ai = new MyAi(parms) {

            @Override
            public GameSettings getGameSettings(SyncedItems<?> joinableGames, SyncedItems<?> resumableGames) {
                return new GameSettings(PlayerColor.WHITE, TimeFormat.RAPID, null, parms, GameSettings.GameType.CREATE_NEW);
            }
        };

        Game.showGameView = true;
        Server server = new Server();
        server.gameSetup(ai);
        server.runServer();
    }

    @Test
    private void t() {
        model.setup(null);
        model.printBoard();
        Minimax minimax = new Minimax(model, 100000);
        System.out.println(minimax.getEvaluation(PlayerColor.BLACK));
//        System.out.println(minimax.getBestMove());
    }

    @Test
    private void testEval() {
//        should be mate in 12

        String fen = "8/5k2/4p3/7P/5KP1/8/8/8 w - - 0 1";
        model.setup(fen);

        Minimax.SHOW_UI = true;
        Minimax minimax = new Minimax(model, Long.MAX_VALUE);

        minimax.getEvaluation(PlayerColor.WHITE).print();
    }

    @Test
    private void testStarting() {
//        model.makeMove(model.findMove(new BasicMove(Location.E2, Location.E4)));
        Minimax minimax = new Minimax(model, 3000);
        System.out.println(minimax.getBestMove(PlayerColor.WHITE));
    }

    @Test
    private void testM1() {
//        model.applyMove(model.findMove(new BasicMove(Location.E2, Location.E4)));
        model.setup("r1b2knr/pp3pp1/2pPq3/5B2/2P5/2PR1QPp/P4P1P/R1B3K1 b - - 0 19");
        Minimax minimax = new Minimax(model, 3000);
        Assert.assertEquals(minimax.getBestMove(), new BasicMove(Location.E6, Location.E1));
    }
}
