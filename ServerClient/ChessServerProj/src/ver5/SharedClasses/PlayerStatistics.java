package ver5.SharedClasses;

import java.io.Serializable;

public class PlayerStatistics implements Serializable {
    public final String username;
    public final int numOfWins;
    public final int numOfLosses;
    public final int numOfTies;
    public final int totalGamesPlayed;

    public PlayerStatistics(String username, int numOfWins, int numOfLosses, int numOfTies) {
        this.username = username;
        this.numOfWins = numOfWins;
        this.numOfLosses = numOfLosses;
        this.numOfTies = numOfTies;
        this.totalGamesPlayed = numOfLosses + numOfTies + numOfWins;
    }

    @Override
    public String toString() {
        return StrUtils.uppercaseFirstLetters("%s\n\tWins: %s\n\tLosses: %s\n\tTies: %s\n\ttotal games played: %s".formatted(username, numOfWins, numOfLosses, numOfTies, totalGamesPlayed));
    }
}
