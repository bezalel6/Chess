package ver14.Model.Minimax;

import ver14.SharedClasses.Game.Evaluation.Evaluation;
import ver14.SharedClasses.Game.Moves.MinimaxMove;
import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.SharedClasses.Utils.StrUtils;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Minimax view - represents a debugging frame for watching the minimax 'think' in real time.
 * can be activating by passing {@value Minimax#DEBUG_MINIMAX} as an argument when running the server
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MinimaxView extends JFrame {
    private static final Font font = new Font(null, Font.BOLD, 30);
    private static final DecimalFormat format = new DecimalFormat("#.###");
    private static String EMPTY = "-";
    private final JLabel timeLbl;
    private final JLabel bestMoveSoFarLbl;
    private final JLabel chosenMoveLbl;
    private final JLabel currentDepthLbl;
    private final JLabel reachedDepthTimeLbl;
    private final JLabel moveEvaluationLbl;
    private final JLabel numOfThreadsLbl;
    private final JLabel cpuUsageLbl;
    private final Timer timer;

    /**
     * Instantiates a new Minimax view.
     *
     * @param show    the show
     * @param minimax the minimax
     */
    public MinimaxView(boolean show, Minimax minimax) {
        setLayout(new GridLayout(0, 1));
        moveEvaluationLbl = emptyLbl("move evaluation");
        bestMoveSoFarLbl = emptyLbl("best move so far");
        chosenMoveLbl = emptyLbl("chosen move");
        timeLbl = emptyTimeLbl("Time Elapsed");
        currentDepthLbl = emptyLbl("current depth");
        reachedDepthTimeLbl = emptyTimeLbl("time to reach current depth");
        numOfThreadsLbl = emptyLbl("Number of threads");
        cpuUsageLbl = emptyLbl("cpu usage");
        add(new MyJButton("Stop", minimax::quietInterrupt) {{
            setToolTipText("(theres like a 99/100 chance this will crash the minimax)");
        }});
        setSize(800, 200);
        timer = show ? new Timer(150, l -> {
            setTime(minimax.getElapsed());
        }) : null;
        pack();

        setVisible(show);
    }

    private JLabel emptyLbl(String desc) {
        JLabel lbl = new JLabel(EMPTY) {
            {
                setFont(font);
            }
        };
        add(desc, lbl);
        return lbl;
    }

    private JLabel emptyTimeLbl(String desc) {
        JLabel lbl = emptyLbl(desc);
        lbl.setText(StrUtils.createTimeStr(0));
        return lbl;
    }

    /**
     * Sets time.
     *
     * @param millis the millis
     */
    public void setTime(long millis) {
        timeLbl.setText(StrUtils.createTimeStr(millis));
    }

    private void add(String desc, JLabel comp) {
        JPanel pnl = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        pnl.add(new JLabel(StrUtils.format(desc) + ": ") {{
            setFont(font);
        }}, gbc);
        gbc.anchor = GridBagConstraints.LINE_END;
        pnl.add(comp, gbc);
        add(pnl);
    }

    /**
     * Stop.
     */
    public void stop() {
        stopTimer();
        dispose();
    }

    /**
     * Stop timer.
     */
    public void stopTimer() {
        if (timer != null)
            timer.stop();
    }

    /**
     * Start time.
     */
    public void startTime() {
        if (timer != null) {
            timer.start();
        }
    }

    /**
     * Reset best moves.
     */
    public void resetBestMoves() {
        setBestMoveSoFar(EMPTY);
        setChosenMove(EMPTY);
    }

    /**
     * Sets best move so far.
     *
     * @param str the str
     */
    public void setBestMoveSoFar(String str) {
        bestMoveSoFarLbl.setText(str);
    }

    /**
     * Sets chosen move.
     *
     * @param str the str
     */
    public void setChosenMove(String str) {
        chosenMoveLbl.setText(str);
    }

    /**
     * Sets cpu usage.
     *
     * @param usage the usage
     */
    public void setCpuUsage(double usage) {
        cpuUsageLbl.setText(formatValue(usage) + "%");
    }

    private static String formatValue(double value) {
        return format.format(value);
    }

    /**
     * Sets best move so far.
     */
    public void setBestMoveSoFar() {
        setBestMoveSoFar(EMPTY);
    }

    /**
     * Sets num of threads.
     *
     * @param num the num
     */
    public void setNumOfThreads(int num) {
        numOfThreadsLbl.setText(num + "");
    }

    /**
     * Sets current depth.
     *
     * @param depth     the depth
     * @param reachedIn the reached in
     */
    public void setCurrentDepth(int depth, long reachedIn) {
        currentDepthLbl.setText(depth + "");
        reachedDepthTimeLbl.setText(StrUtils.createTimeStr(reachedIn));
    }

    /**
     * Update.
     *
     * @param bestMoveSoFar the best move so far
     */
    public void update(MinimaxMove bestMoveSoFar) {
        setBestMoveSoFar(bestMoveSoFar.getMove());
        setMoveEval(bestMoveSoFar.getMoveEvaluation());
    }

    /**
     * Sets best move so far.
     *
     * @param move the move
     */
    public void setBestMoveSoFar(Move move) {
        setBestMoveSoFar(move.getAnnotation());
    }

    /**
     * Sets move eval.
     *
     * @param eval the eval
     */
    public void setMoveEval(Evaluation eval) {
        moveEvaluationLbl.setText(formatValue(eval.getEval()));
    }
}
