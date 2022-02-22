package ver8.view.OldDialogs.GameSelect;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.networking.AppSocket;
import ver8.view.OldDialogs.MyDialog;
import ver8.view.OldDialogs.WinPnl;
import ver8.view.OldDialogs.GameSelect.Panels.JoinGame;
import ver8.view.OldDialogs.GameSelect.Panels.ResumeGames;

public class GameSelect extends MyDialog {
    private GameSettings gameSettings;
    private JoinGame joinGamePnl;
    private ResumeGames resumeGamesPnl;
    private GameCreatePnl newGameCreatePnl;

    public GameSelect(AppSocket socketToServer) {

        super(socketToServer);

        doneCreating();
    }


    public static void main(String[] args) {
        System.out.println(showGameSelectionDialog(null));
    }

    public static GameSettings showGameSelectionDialog(AppSocket socketToServer) {
        GameSelect select = new GameSelect(socketToServer);
        select.start();
        return select.gameSettings;
    }

    public static GameSelect create(AppSocket socket) {
        return new GameSelect(socket);
    }

    @Override
    protected void createB4StartingPnl() {
        super.createB4StartingPnl();
        gameSettings = new GameSettings();

        newGameCreatePnl = new GameCreatePnl(this);
        joinGamePnl = new JoinGame(this);
        resumeGamesPnl = new ResumeGames(this);

    }

    @Override
    protected void createStartingPnl() {
        startingPnl = new WinPnl("Choose Game") {{
            addNav(newGameCreatePnl);
            add(joinGamePnl.navigationComp());
            add(resumeGamesPnl.navigationComp());
        }};
    }


    public GameSettings getGameSettings() {
        return gameSettings;
    }

}

