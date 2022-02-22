package ver6.view.dialogs.game_select;

import ver6.SharedClasses.GameSettings;
import ver6.SharedClasses.Sync.SyncedItems;
import ver6.SharedClasses.networking.AppSocket;
import ver6.view.List.Synced.SyncedGames;
import ver6.view.dialogs.MyDialog;
import ver6.view.dialogs.WinPnl;
import ver6.view.dialogs.game_select.Panels.JoinGame;
import ver6.view.dialogs.game_select.Panels.ResumeGames;

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
            addNav(new SyncedGames(e -> {
            }, "headd", GameSelect.this) {{
                sync(SyncedItems.exampleGames1);
                doneAdding();
            }});
            addNav(newGameCreatePnl);
            add(joinGamePnl.navigationComp());
            add(resumeGamesPnl.navigationComp());
        }};
    }


    public GameSettings getGameSettings() {
        return gameSettings;
    }

}

