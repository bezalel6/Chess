package ver2;


import ver2.types.*;

import java.util.ArrayList;

public class Controller {
    public static final int ROWS = 8, COLS =8;
    public static void main(String[] args) {
        View.initGame();
        Model.initGame();

        //Model.addPiece(new Knight(new Location(4,4), master.colors.White));
    }
    public static ArrayList<Path> clicked(Location loc)
    {
        return Model.getMovableSquares(loc);

    }

    public static void move(Location loc) {
        Model.move(loc);
    }
}
