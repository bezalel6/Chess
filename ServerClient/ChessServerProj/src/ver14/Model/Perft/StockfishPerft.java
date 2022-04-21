package ver14.Model.Perft;

import ver14.Players.PlayerAI.Stockfish.Stockfish;
import ver14.SharedClasses.Game.Moves.BasicMove;

import javax.swing.*;

public class StockfishPerft extends Perft {
    private final Stockfish stockfish;
    protected long sum = 0;

    public StockfishPerft(Stockfish stockfish, int depth) {
        super(depth);
        this.stockfish = stockfish;
        if (depth >= 8) {
            int selected;
            do {
                selected = JOptionPane.showConfirmDialog(null, "are you sure you want ur pc to blow up? depth %d?!".formatted(depth));
            } while (selected == JOptionPane.OK_OPTION);
            return;
        }
        String cmd = "go perft " + depth;
        stockfish.sendCommand(cmd);
        String searching = "Nodes searched: ";
        String fullStr = "";
        do {
            fullStr += stockfish.getOutput(1);
        }
        while (!fullStr.contains(searching));
        fullStr = fullStr.split("\nNodes")[0];
//        String moves = fullStr.split("\nNodes")[0];
        for (String moveStr : fullStr.split("\n")) {
            try {
                BasicMove m = new BasicMove(moveStr);
                long l = Long.parseLong(moveStr.split(": ")[1]);
                sum += l;
                set(m, l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "Stockfish" + super.toString();
    }

    @Override
    public long getSum() {
        return sum;
    }

    @Override
    public String details() {
        return "StockfishPerft";
    }
}
