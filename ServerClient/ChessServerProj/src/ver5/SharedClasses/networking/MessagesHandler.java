package ver5.SharedClasses.networking;

import ver5.SharedClasses.messages.Message;

public abstract class MessagesHandler {

    public void receivedMessage(Message message) {
        switch (message.getMessageType()) {
            case LOGIN -> {
                onLogin(message);
            }
            case WELCOME_MESSAGE -> {
                onWelcomeMessage(message);
            }
            case GET_GAME_SETTINGS -> {
                onGetGameSettings(message);
            }
            case WAIT_FOR_MATCH -> {
                onWaitForMatch(message);
            }
            case INIT_GAME -> {
                onInitGame(message);
            }
            case WAIT_TURN -> {
                onWaitTurn(message);
            }
            case GET_MOVE -> {
                onGetMove(message);
            }
            case UPDATE_BY_MOVE -> {
                onUpdateByMove(message);
            }
            case GAME_OVER -> {
                onGameOver(message);
            }
            case ERROR -> {
                onError(message);
            }
            case QUESTION -> {
                onQuestion(message);
            }
            case BYE -> {
                onBye(message);
            }
            case USERNAME_AVAILABILITY -> {
                onUsernameAvailability(message);
            }
            case PLAYERS_STATISTICS -> {
                onPlayersStatistics(message);
            }
            case RESUMABLE_GAMES -> {
                onResumableGames(message);
            }
            case JOINABLE_GAMES -> {
                onJoinableGames(message);
            }
            case INTERRUPT -> {
                onInterrupt(message);
            }
            case STOP_READ -> {
                onStopRead(message);
            }
            case IS_ALIVE -> {
                onIsAlive(message);
            }
            case ALIVE -> {
                onAlive(message);
            }
            default -> {
                throw new Error("message type not implemented " + message.getMessageType());
            }
        }
    }

    public void onLogin(Message message) {

    }


    public void onWelcomeMessage(Message message) {

    }


    public void onGetGameSettings(Message message) {

    }


    public void onWaitForMatch(Message message) {

    }


    public void onInitGame(Message message) {

    }


    public void onWaitTurn(Message message) {

    }


    public void onGetMove(Message message) {

    }


    public void onUpdateByMove(Message message) {

    }


    public void onGameOver(Message message) {

    }


    public void onError(Message message) {

    }


    public void onQuestion(Message message) {

    }


    public void onBye(Message message) {

    }


    public void onUsernameAvailability(Message message) {

    }


    public void onPlayersStatistics(Message message) {

    }


    public void onResumableGames(Message message) {

    }


    public void onJoinableGames(Message message) {

    }


    public void onInterrupt(Message message) {

    }


    public void onStopRead(Message message) {

    }


    public void onIsAlive(Message message) {

    }


    public void onAlive(Message message) {

    }

}
