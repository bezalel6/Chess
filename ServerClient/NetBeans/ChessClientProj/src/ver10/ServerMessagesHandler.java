package ver10;

import ver10.SharedClasses.Callbacks.MessageCallback;
import ver10.SharedClasses.GameSettings;
import ver10.SharedClasses.PlayerColor;
import ver10.SharedClasses.Sync.SyncedItems;
import ver10.SharedClasses.Sync.SyncedListType;
import ver10.SharedClasses.board_setup.Board;
import ver10.SharedClasses.evaluation.GameStatus;
import ver10.SharedClasses.messages.Message;
import ver10.SharedClasses.messages.MessageType;
import ver10.SharedClasses.moves.Move;
import ver10.SharedClasses.networking.MessagesHandler;
import ver10.view.Dialog.SyncableList;
import ver10.view.View;

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

    public void registerSyncableList(SyncableList list) {
        syncableLists.put(list.syncedListType(), list);
        if (storedItems.containsKey(list.syncedListType()))
            list.sync(storedItems.remove(list.syncedListType()));
    }

    @Override
    public void onDisconnected() {
        client.disconnectedFromServer();
        super.onDisconnected();
    }

    @Override
    public void onAnyMsg(Message message) {
        super.onAnyMsg(message);
        MessageType messageType = message.getMessageType();

        //in case its added to a message
        if (messageType != MessageType.UPDATE_SYNCED_LIST && message.getSyncedLists() != null) {
            onUpdateSyncedList().onMsg(message);
        }

//        if (message.getMessageType() != MessageType.QUESTION)
        if (message.isSubject())
            view.setStatusLbl(message.getSubject());

        if (message.hideQuestion())
            client.hideQuestionPnl();

        client.updateGameTime(message);

    }

    @Override
    public MessageCallback onLogin() {
        return message -> {
            super.onLogin().onMsg(message);
            String username = client.login(message);
            view.setUsername(username);
        };
    }

    @Override
    public MessageCallback onGetGameSettings() {
        return message -> {
            super.onGetGameSettings().onMsg(message);
            GameSettings gameSettings = client.showGameSettingsDialog();
            socket.writeMessage(Message.returnGameSettings(gameSettings, message));
        };
    }

    @Override
    public MessageCallback onInitGame() {
        return message -> {
            super.onInitGame().onMsg(message);
            PlayerColor myColor = message.getPlayerColor();
            client.setMyColor(myColor);
            Stack<Move> moveStack = message.getMoveStack();
            Board board = message.getBoard();
            //if loading a prev game the board should start from the starting pos and make all moves
            boolean isLoadingGame = moveStack != null && !moveStack.isEmpty();
            if (isLoadingGame) {
                board = Board.startingPos();
            }
            view.initGame(message.getGameTime(), board, myColor, message.getOtherPlayer());
            if (isLoadingGame) {
                for (Move move : moveStack)
                    client.updateByMove(move);
            }
            
        };
    }

    @Override
    public MessageCallback onWaitTurn() {
        return message -> {
            super.onWaitTurn().onMsg(message);
            client.startOpponentTime();
        };
    }

    @Override
    public MessageCallback onGetMove() {
        return message -> {
            super.onGetMove().onMsg(message);
            client.setLatestGetMoveMsg(message);
            client.unlockMovableSquares(message);
            view.getWin().requestFocus();
            client.startMyTime();
        };
    }

    @Override
    public MessageCallback onUpdateByMove() {
        return message -> {
            super.onUpdateByMove().onMsg(message);
            client.updateByMove(message.getMove());
            client.stopRunningTime();
        };
    }

    @Override
    public MessageCallback onGameOver() {
        return message -> {
            super.onGameOver().onMsg(message);
            client.stopRunningTime();
            GameStatus gameStatus = message.getGameStatus();
            view.gameOver(gameStatus.getDetailedStr());

        };
    }

    @Override
    public MessageCallback onError() {
        return message -> {
            super.onError().onMsg(message);
            client.closeClient(message.getSubject());
        };
    }

    @Override
    public MessageCallback onQuestion() {
        return message -> {
            super.onQuestion().onMsg(message);
            view.getSidePanel().askPlayerPnl.ask(message.getQuestion(), answer -> {
                message.getQuestion().setAnswer(answer);
                socket.writeMessage(Message.answerQuestion(message.getQuestion(), message));
            });
        };
    }

    @Override
    public MessageCallback onBye() {
        return message -> {
            super.onBye().onMsg(message);
            client.closeClient(message.getSubject());
        };
    }

    @Override
    public MessageCallback onUpdateSyncedList() {
        return message -> {
            super.onUpdateSyncedList().onMsg(message);
            for (SyncedItems items : message.getSyncedLists()) {
                if (syncableLists.containsKey(items.syncedListType))
                    syncableLists.get(items.syncedListType).sync(items);
                else
                    storedItems.put(items.syncedListType, items);
            }
        };
    }


}
