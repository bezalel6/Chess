package ver3;

import ver3.model_classes.Model;
import ver3.server_info.players.Player;

/**
 * Game.
 * ---------------------------------------------------------------------------
 * by Ilan Peretz (ilanperets@gmail.com)  10/11/2021
 */
public class Game {
    private Player p1;
    private Player p2;
    private Model model;
    private Player currentPlayerTurn;

    public Game(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;

        model = new Model(this);
    }

    public void runGame() {
//        currentPlayerTurn = p1;
//        while (!model.isGameOver()) {
//            Move move = currentPlayerTurn.getMove();
//            model.makeMove(move);
//
//            if (model.isGameOver()) {
//                p1.gameOver();
//                p2.gameOver();
//            } else {
//                if (currentPlayerTurn == p1)
//                    currentPlayerTurn = p2;
//                else
//                    currentPlayerTurn = p1;
//            }
//        }
    }
}
