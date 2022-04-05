package ver14.Tests;

import org.testng.annotations.Test;
import ver14.Server;
import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.GameSetup.AiParameters;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.TimeFormat;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.game.Game;
import ver14.players.PlayerAI.MyAi;

public class MinimaxTests extends Tests {

    @Test(testName = "minimax vs minimax")
    private void minimaxVsMinimax() {
        AiParameters parms = new AiParameters(AiParameters.AiType.MyAi, new TimeFormat(2000));
        MyAi ai = new MyAi(parms) {

            @Override
            public GameSettings getGameSettings(SyncedItems<?> joinableGames, SyncedItems<?> resumableGames) {
                return new GameSettings(PlayerColor.WHITE, TimeFormat.BULLET, null, parms, GameSettings.GameType.CREATE_NEW);
            }
        };

        Game.showGameView = true;
        Server server = new Server();
        server.gameSetup(ai);
        server.runServer();
    }
    public static void main(String[] args) {
        new MinimaxTests().minimaxVsMinimax();
    }
    @Test(testName = "minimax vs stockfish")
    private void minimaxVsStockfish() {
        MyAi ai = new MyAi(new AiParameters(AiParameters.AiType.MyAi, new TimeFormat(1000))) {
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
}
