package ver14.Tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ver14.Model.minimax.Minimax;
import ver14.Server;
import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.TimeFormat;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.game.Game;
import ver14.players.PlayerAI.MyAi;

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
    private void testEval() {
//        should be mate in 12

        String fen = "8/5k2/4p3/7P/5KP1/8/8/8 w - - 0 1";
        model.setup(fen);

        Minimax.SHOW_UI = true;
        Minimax minimax = new Minimax(model, Long.MAX_VALUE);

        minimax.getEvaluation(PlayerColor.WHITE).print();
    }
}
