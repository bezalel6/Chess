package ver14;

import ver14.SharedClasses.Callbacks.MessageCallback;
import ver14.SharedClasses.Game.Evaluation.GameStatus;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Board;
import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.Networking.Messages.Message;
import ver14.SharedClasses.Networking.Messages.MessageType;
import ver14.SharedClasses.Networking.MessagesHandler;
import ver14.SharedClasses.Sync.SyncedItems;
import ver14.SharedClasses.Sync.SyncedListType;
import ver14.view.Dialog.Cards.MessageCard;
import ver14.view.Dialog.SyncableList;
import ver14.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class ClientMessagesHandler extends MessagesHandler {
    private final Client client;
    private final View view;
    private final HashMap<SyncedListType, SyncedItems<?>> syncedLists = new HashMap<>();
    private final HashMap<SyncedListType, ArrayList<SyncableList>> listeningLists = new HashMap<>();

    public ClientMessagesHandler(Client client, View view) {
        super(client.getClientSocket());
        this.client = client;
        this.view = view;
    }

    public void registerSyncableList(SyncableList list) {
        synchronized (syncedLists) {
            getListeningList(list.syncedListType()).add(list);
            trySyncing(list);
        }
    }

    private ArrayList<SyncableList> getListeningList(SyncedListType type) {
        if (!listeningLists.containsKey(type))
            listeningLists.put(type, new ArrayList<>());
        return listeningLists.get(type);
    }

    private void trySyncing(SyncableList list) {
        if (syncedLists.containsKey(list.syncedListType())) {
            list.sync(syncedLists.get(list.syncedListType()));
        }
    }

    @Override
    public MessageCallback onCancelQuestion() {
        return message -> {
            super.onCancelQuestion().callback(message);
            client.getView().getSidePanel().askPlayerPnl.replaceWithMsg(message.getQuestion(), message.getCancelingQuestionCause());
        };
    }

    @Override
    public void onAnyMsg(Message message) {
        super.onAnyMsg(message);
        MessageType messageType = message.getMessageType();

        //in case its added to a message
        if (messageType != MessageType.UPDATE_SYNCED_LIST && message.getSyncedLists() != null) {
            onUpdateSyncedList().callback(message);
        }

        if (message.isSubject())
            view.setStatusLbl(message.getSubject());

        client.updateGameTime(message);

    }

    @Override
    public void onUnplannedDisconnect() {
        client.disconnectedFromServer();
        super.onUnplannedDisconnect();
    }

    @Override
    public MessageCallback onLogin() {
        return message -> {
            super.onLogin().callback(message);
            String username = client.login(message);
            view.setUsername(username);
        };
    }

    @Override
    public MessageCallback onGetGameSettings() {
        return message -> {
            super.onGetGameSettings().callback(message);
            GameSettings gameSettings = client.showGameSettingsDialog();
            socket.writeMessage(Message.returnGameSettings(gameSettings, message));
        };
    }

    @Override
    public MessageCallback onInitGame() {
        return message -> {
            super.onInitGame().callback(message);
            client.soundManager.gameStart.play();
            synchronized (view.boardLock) {
                view.drawFocus();

                PlayerColor myColor = message.getPlayerColor();
                assert myColor != null;
                client.mapPlayers(myColor, message.getOtherPlayer());
                Stack<Move> moveStack = message.getMoveStack();
                Board board = message.getBoard();
                //if loading a prev game the board should start from the starting pos and make all moves
//                boolean isLoadingGame = moveStack != null && !moveStack.isEmpty();
//                if (isLoadingGame) {
//                    board = Board.startingPos();
//                }
                view.initGame(message.getGameTime(), board, myColor, message.getOtherPlayer());
//                if (isLoadingGame) {
//                    for (Move move : moveStack)
//                        client.updateByMove(move, false);
//                }
            }

        };
    }

    @Override
    public MessageCallback onWaitTurn() {
        return message -> {
            super.onWaitTurn().callback(message);
            client.startOpponentTime();
            client.enablePreMove();
        };
    }

    @Override
    public MessageCallback onGetMove() {
        return message -> {
            super.onGetMove().callback(message);

            client.stopPremoving();

            synchronized (view.boardLock) {
                client.setLatestGetMoveMsg(message);
                client.unlockMovableSquares(message);
                view.drawFocus();
                client.startMyTime();
            }
        };

    }

    @Override
    public MessageCallback onUpdateByMove() {
        return message -> {
            super.onUpdateByMove().callback(message);
            view.drawFocus();
            client.updateByMove(message.getMove());
            client.stopRunningTime();
        };
    }

    @Override
    public MessageCallback onGameOver() {
        return message -> {
            super.onGameOver().callback(message);
            client.stopRunningTime();
            client.soundManager.gameEnd.play();
            GameStatus gameStatus = message.getGameStatus();
            view.gameOver(gameStatus);

        };
    }

    @Override
    public MessageCallback onError() {
        return message -> {
            super.onError().callback(message);
            client.closeClient(message.getSubject(), "Error", MessageCard.MessageType.ServerError);
        };
    }

    @Override
    public MessageCallback onQuestion() {
        return message -> {
            super.onQuestion().callback(message);
            Question question = message.getQuestion();
            Boolean drawOfferBtn = null;
            if (question.questionType == Question.QuestionType.DRAW_OFFER) {
                drawOfferBtn = view.getSidePanel().getGameActions().enableDrawOfferBtn(false);
            }
            Boolean finalDrawOfferBtn = drawOfferBtn;
            view.askQuestion(question, answer -> {
                socket.writeMessage(Message.answerQuestion(answer, message));
                if (finalDrawOfferBtn != null && answer != Question.Answer.ACCEPT) {
                    view.getSidePanel().getGameActions().enableDrawOfferBtn(finalDrawOfferBtn);
                }
            });
        };
    }

    @Override
    public MessageCallback onBye() {
        return message -> {
            super.onBye().callback(message);
            client.closeClient(message.getSubject(), "Bye", MessageCard.MessageType.INFO);
        };
    }

    @Override
    public MessageCallback onUpdateSyncedList() {
        return message -> {
            super.onUpdateSyncedList().callback(message);
            synchronized (syncedLists) {
                for (SyncedItems<?> items : message.getSyncedLists()) {
                    syncedLists.put(items.syncedListType, items);
                    getListeningList(items.syncedListType).forEach(this::trySyncing);
                }
            }
        };
    }


}
