package ver6;

import ver6.SharedClasses.GameSettings;
import ver6.SharedClasses.PlayerColor;
import ver6.SharedClasses.Sync.SyncedItems;
import ver6.SharedClasses.Sync.SyncedListType;
import ver6.SharedClasses.board_setup.Board;
import ver6.SharedClasses.evaluation.GameStatus;
import ver6.SharedClasses.messages.Message;
import ver6.SharedClasses.messages.MessageType;
import ver6.SharedClasses.moves.Move;
import ver6.SharedClasses.networking.MessagesHandler;
import ver6.SharedClasses.networking.MyErrors;
import ver6.view.List.Synced.SyncableList;
import ver6.view.View;
import ver6.view.dialogs.game_select.GameSelect;

import java.util.HashMap;
import java.util.Stack;

public class ServerMessagesHandler extends MessagesHandler {
    private final Client client;
    private final View view;
    private final HashMap<SyncedListType, SyncableList> syncableLists = new HashMap<>();
    private final HashMap<SyncedListType, SyncedItems> storedItems = new HashMap<>();

    public ServerMessagesHandler(Client client, View view) {
        super(client.getClientSocket());
        this.client = client;
        this.view = view;
    }

    public void registerSyncedList(SyncableList list) {
        syncableLists.put(list.syncedListType(), list);
        if (storedItems.containsKey(list.syncedListType()))
            list.sync(storedItems.get(list.syncedListType()));
    }

    @Override
    public void onAnyMsg(Message message) {
        super.onAnyMsg(message);
        MessageType messageType = message.getMessageType();

        //in case its added to a message
        if (messageType != MessageType.UPDATE_SYNCED_LIST && message.getSyncedLists() != null) {
            onUpdateSyncedList(message);
        }
//        if (message.getMessageType() != MessageType.QUESTION)
        if (message.getSubject() != null)
            view.setStatusLbl(message.getSubject());
        if (messageType.isGameProgression()) {
            view.resetStatusLblClr();
        }
        if (messageType == MessageType.INTERRUPT)
            client.hideQuestionPnl();

        client.updateGameTime(message);

    }

    @Override
    public void onLogin(Message message) throws MyErrors {
        super.onLogin(message);
        String username = client.login(message);
        view.setUsername(username);
    }

    @Override
    public void onGetGameSettings(Message message) {
        super.onGetGameSettings(message);
        GameSettings gameSettings = client.showDialog(GameSelect.create(socket)).getGameSettings();
        socket.writeMessage(Message.returnGameSettings(gameSettings, message));
    }

    @Override
    public void onInitGame(Message message) {
        super.onInitGame(message);
        PlayerColor myColor = message.getPlayerColor();
        client.setMyColor(myColor);
        view.initGame(message.getGameTime(), Board.startingPos(), myColor, message.getOtherPlayer());
        Stack<Move> moveStack = message.getMoveStack();
        if (moveStack != null) {
            for (Move move : moveStack)
                client.updateByMove(move);
        }
    }

    @Override
    public void onGetMove(Message message) {
        super.onGetMove(message);
        client.setLatestGetMoveMsg(message);
        client.unlockMovableSquares(message);
        view.getWin().requestFocus();
    }

    @Override
    public void onUpdateByMove(Message message) {
        super.onUpdateByMove(message);
        client.updateByMove(message.getMove());
    }

    @Override
    public void onGameOver(Message message) {
        super.onGameOver(message);
        GameStatus gameStatus = message.getGameStatus();
        view.gameOver(gameStatus.getDetailedStr());
    }

    @Override
    public void onQuestion(Message message) {
        super.onQuestion(message);
        view.getSidePanel().askPlayerPnl.ask(message.getQuestion(), answer -> {
            message.getQuestion().setAnswer(answer);
            socket.writeMessage(Message.answerQuestion(message.getQuestion(), message));
        });
    }

    @Override
    public void onBye(Message message) {
        super.onBye(message);
        client.closeClient(message.getSubject());

    }

    @Override
    public void onUpdateSyncedList(Message message) {
        super.onUpdateSyncedList(message);
        for (SyncedItems items : message.getSyncedLists()) {
            if (syncableLists.containsKey(items.syncedListType))
                syncableLists.get(items.syncedListType).sync(items);
            else
                storedItems.put(items.syncedListType, items);
        }
    }

    @Override
    public void onDisconnected() {
        client.disconnectedFromServer();
        super.onDisconnected();
    }
}
