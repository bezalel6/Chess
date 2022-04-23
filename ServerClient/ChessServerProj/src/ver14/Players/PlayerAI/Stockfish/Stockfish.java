package ver14.Players.PlayerAI.Stockfish;


import ver14.Model.FEN;
import ver14.Model.Perft.StockfishPerft;
import ver14.SharedClasses.Utils.StrUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/*
 * Stockfish
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * Stockfish -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * Stockfish -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

/**
 * <a href="https://github.com/rahular/chess-misc/blob/master/JavaStockfish/src/com/rahul/stockfish/Stockfish.java">modified version of</a>
 */
public class Stockfish {

    private static final String PATH = "src/assets/Stockfish/stockfish_14.1_win_x64_avx2/stockfish_14.1_win_x64_avx2.exe";
    private Process engineProcess;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;

    public Stockfish(String fen) {
        this();
        setFen(fen);
    }

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

    private void setFen(String fen) {
        FEN.assertFen(fen);
        String cmd;

        if (StrUtils.isEmpty(fen)) {
            cmd = "position startpos";
        } else {
            cmd = "position fen " + fen;
        }
        sendCommand(cmd);
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

    public StockfishPerft perft(int depth) {
        return new StockfishPerft(this, depth);
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
    public synchronized float getEvalScore(String fen, int waitTime) {
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