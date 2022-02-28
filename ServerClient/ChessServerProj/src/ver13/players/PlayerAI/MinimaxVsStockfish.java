package ver13.players.PlayerAI;

import ver13.SharedClasses.GameSettings;
import ver13.SharedClasses.GameSetup.AiParameters;
import ver13.SharedClasses.PlayerColor;
import ver13.SharedClasses.Sync.SyncedItems;
import ver13.SharedClasses.TimeFormat;

import java.util.concurrent.TimeUnit;

public class MinimaxVsStockfish extends MyAi {
    private boolean isNew = true;

    public MinimaxVsStockfish(int searchTimeInSeconds) {
        super(new AiParameters(AiParameters.AiType.MyAi, new TimeFormat(TimeUnit.SECONDS.toMillis(searchTimeInSeconds))));
    }

    @Override
    public GameSettings getGameSettings(SyncedItems joinableGames, SyncedItems resumableGames) {
        if (!isNew) {
            return null;
        }
        isNew = false;
        return new GameSettings(PlayerColor.WHITE, TimeFormat.BULLET, AiParameters.EZ_STOCKFISH, GameSettings.GameType.CREATE_NEW);
    }
}
