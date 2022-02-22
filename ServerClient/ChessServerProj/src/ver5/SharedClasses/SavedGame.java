package ver5.SharedClasses;

import java.io.Serializable;

public class SavedGame implements Serializable {
    public final String creatorUsername;
    public final String opponentUsername;
    public final String gameId;
    public final GameSettings gameSettings;
    public final String winner;

    public SavedGame(String gameId, String creatorUsername, GameSettings gameSettings) {
        this(gameId, creatorUsername, null, gameSettings, null);
    }

    public SavedGame(String gameId, String creatorUsername, String opponentUsername, GameSettings gameSettings, String winner) {
        this.creatorUsername = creatorUsername;
        this.opponentUsername = opponentUsername;
        this.winner = winner;
        this.gameId = gameId;
        this.gameSettings = gameSettings;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavedGame savedGame = (SavedGame) o;
        return savedGame.gameId.equals(gameId);
    }

    @Override
    public String toString() {
        return "SavedGame{" +
                "creatorUsername='" + creatorUsername + '\'' +
                ", opponentUsername='" + opponentUsername + '\'' +
                ", gameId='" + gameId + '\'' +
                ", gameSettings=" + gameSettings +
                '}';
    }

//    public enum SavedGameType {
//        WAITING_FOR_MATCH,
//        GAME_OVER,
//        GAME_PAUSED
//    }
}
