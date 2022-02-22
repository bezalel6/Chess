package ver5.view.dialogs.game_select;

import ver5.SharedClasses.GameSettings;
import ver5.SharedClasses.networking.AppSocket;
import ver5.SharedClasses.networking.DummySocket;
import ver5.SharedClasses.ui.MyJButton;
import ver5.view.dialogs.MyDialog;
import ver5.view.dialogs.WinPnl;
import ver5.view.dialogs.game_select.Panels.JoinGame;
import ver5.view.dialogs.game_select.Panels.ResumeGames;

public class GameSelect extends MyDialog {
    private final GameSettings gameSettings;
    private final JoinGame joinGamePnl;
    private final ResumeGames resumeGamesPnl;
    private final GameCreatePnl newGameCreatePnl;

    public GameSelect(AppSocket socketToServer) {

        super(socketToServer);
        gameSettings = new GameSettings();

        newGameCreatePnl = new GameCreatePnl(this);
        joinGamePnl = new JoinGame(this);
        resumeGamesPnl = new ResumeGames(this);

        doneCreating();
    }


    public static void main(String[] args) {
        System.out.println(showGameSelectionDialog(new DummySocket()));
    }

    public static GameSettings showGameSelectionDialog(AppSocket socketToServer) {
        return new GameSelect(socketToServer).gameSettings;
//
//        GameSettings gameSettings = new GameSettings(PlayerColor.WHITE, TimeFormat.BULLET, AiParameters.EZ_STOCKFISH, GameSettings.GameType.CREATE_NEW);
//        gameSettings.setFen("1nbqkbnr/Pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQk - 0 1");
//        return gameSettings;
    }


    @Override
    protected void createStartingPnl() {
        startingPnl = new WinPnl("Choose Game") {{
            add(new MyJButton("Create New Game", () -> switchTo(newGameCreatePnl)));
            add(new MyJButton("Join Game", () -> switchTo(joinGamePnl)));
            add(new MyJButton("Resume Game", () -> switchTo(resumeGamesPnl)));
        }};
    }


    public GameSettings getGameSettings() {
        return gameSettings;
    }

}

