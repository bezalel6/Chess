package ver14.Model;


import ver14.SharedClasses.Location;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.moves.BasicMove;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * <a href="https://github.com/rahular/chess-misc/blob/master/JavaStockfish/src/com/rahul/stockfish/Stockfish.java">Source</a>
 */
public class Stockfish {

    private static final String PATH = "src/assets/Stockfish/stockfish_14.1_win_x64_avx2/stockfish_14.1_win_x64_avx2.exe";
    private Process engineProcess;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;

    public Stockfish() {
        if (startEngine()) {
            startEngine();
            getOutput(100);
//            String f = Positions.allPositions.get(0).getFen();
//            System.out.println(getBestMove(f, 100));
//            System.out.println("Eval = " + getEvalScore(f, 100));
        } else {
            System.out.println("err");
        }
    }

    /**
     * Starts Stockfish engine as a process and initializes it
     *
     * @return True on success. False otherwise
     */
    public boolean startEngine() {
        try {
            engineProcess = Runtime.getRuntime().exec(PATH);
            processReader = new BufferedReader(new InputStreamReader(
                    engineProcess.getInputStream()));
            processWriter = new OutputStreamWriter(
                    engineProcess.getOutputStream());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * This is generally called right after 'sendCommand' for getting the raw
     * output from Stockfish
     *
     * @param waitTime Time in milliseconds for which the function waits before
     *                 reading the output. Useful when a long running command is
     *                 executed
     * @return Raw output from Stockfish
     */
    public String getOutput(int waitTime) {
        StringBuffer buffer = new StringBuffer();
        try {
            Thread.sleep(Math.max(100, waitTime));
            sendCommand("isready");
            while (true) {
                String text = processReader.readLine();
                if (text.equals("readyok"))
                    break;
                else
                    buffer.append(text + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * Takes in any valid UCI command and executes it
     *
     * @param command
     */
    public void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Stockfish stockfish = new Stockfish();
        System.out.println(stockfish.perft(2, new BasicMove(Location.E2, Location.E4)));
        stockfish.stopEngine();
    }

    /**
     * get nodes from move
     * <h1>make sure move is legal</h1>
     */
    public long perft(int depth, BasicMove move) {
        if (depth >= 8) {
            int selected;
            do {
                selected = JOptionPane.showConfirmDialog(null, "are you sure you want to blow your computer up? depth %d?!".formatted(depth));
            } while (selected == JOptionPane.OK_OPTION);
            return -1;
        }
        sendCommand("go perft " + depth);
        String searching = "%s: ".formatted(StrUtils.clean(move.getBasicMoveAnnotation()));
        String out;
        do {
            out = getOutput(0);
        }
        while (!out.contains(searching));
        return Long.parseLong(out.split(searching)[1].split("[ \n]")[0]);
    }

    /**
     * Stops Stockfish and cleans up before closing it
     */
    public void stopEngine() {
        try {
            sendCommand("quit");
            processReader.close();
            processWriter.close();
        } catch (IOException e) {
        }
    }

    public long perft(int depth) {
        if (depth >= 8) {
            int selected;
            do {
                selected = JOptionPane.showConfirmDialog(null, "are you sure you want to blow your computer up? depth %d?!".formatted(depth));
            } while (selected == JOptionPane.OK_OPTION);
            return -1;
        }
        sendCommand("go perft " + depth);
        String searching = "searched: ";
        String out;
        do {
            out = getOutput(0);
        }
        while (!out.contains(searching));
        return Long.parseLong(out.split(searching)[1].split("[ \n]")[0]);

    }

    /**
     * This function returns the best move for a given position after
     * calculating for 'waitTime' ms
     *
     * @param fen      Position string
     * @param waitTime in milliseconds
     * @return Best Move in PGN format
     */
    public String getBestMove(String fen, int waitTime) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);
        String out = getOutput(waitTime + 20);
//        System.out.println("output = " + out);
        try {
            out = out.split("bestmove ")[1].split(" ")[0];

        } catch (Exception e) {
            out = getOutput(waitTime + 20).split("bestmove ")[1].split(" ")[0];
        }
        return out;
    }

    /**
     * Get a list of all legal moves from the given position
     *
     * @param fen Position string
     * @return String of moves
     */
    public String getLegalMoves(String fen) {
        sendCommand("position fen " + fen);
        sendCommand("d");
        return getOutput(0).split("Legal moves: ")[1];
    }

    /**
     * Draws the current state of the chess board
     *
     * @param fen Position string
     */
    public void drawBoard(String fen) {
        sendCommand("position fen " + fen);
        sendCommand("d");

        String[] rows = getOutput(0).split("\n");

        for (int i = 1; i < 18; i++) {
            System.out.println(rows[i]);
        }
    }

    /**
     * Get the evaluation score of a given board position
     *
     * @param fen      Position string
     * @param waitTime in milliseconds
     * @return evalScore
     */
    public float getEvalScore(String fen, int waitTime) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);

        float evalScore = 0.0f;
        String[] dump = getOutput(waitTime + 20).split("\n");
        for (int i = dump.length - 1; i >= 0; i--) {
            if (dump[i].startsWith("info depth ")) {
                try {
                    evalScore = Float.parseFloat(dump[i].split("score cp ")[1]
                            .split(" nodes")[0]);
                } catch (Exception e) {
                    evalScore = Float.parseFloat(dump[i].split("score cp ")[1]
                            .split(" upperbound nodes")[0]);
                }
            }
        }
        return evalScore / 100;
    }
}