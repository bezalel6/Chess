package ver14.Model.minimax;

import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.Game.evaluation.Evaluation;
import ver14.SharedClasses.Game.moves.Move;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

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

    public MinimaxView(boolean show) {
        moveEvaluationLbl = emptyLbl("move evaluation");
        bestMoveSoFarLbl = emptyLbl("best move so far");
        chosenMoveLbl = emptyLbl("chosen move");
        timeLbl = emptyTimeLbl("Time Elapsed");
        currentDepthLbl = emptyLbl("current depth");
        reachedDepthTimeLbl = emptyTimeLbl("time to reach current depth");
        numOfThreadsLbl = emptyLbl("Number of threads");
        cpuUsageLbl = emptyLbl("cpu usage");

        setSize(800, 200);
        setLayout(new GridLayout(0, 1));

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

    public void setTime(long millis) {
        timeLbl.setText(StrUtils.createTimeStr(millis));
    }

    public void resetBestMoves() {
        setBestMoveSoFar(EMPTY);
        setChosenMove(EMPTY);
    }

    public void setBestMoveSoFar(String str) {
        bestMoveSoFarLbl.setText(str);
    }

    public void setChosenMove(String str) {
        chosenMoveLbl.setText(str);
    }

    public void setCpuUsage(double usage) {
        cpuUsageLbl.setText(formatValue(usage) + "%");
    }

    private static String formatValue(double value) {
        return format.format(value);
    }

    public void setBestMoveSoFar(Move move) {
        setBestMoveSoFar(move.getAnnotation());
    }

    public void setBestMoveSoFar() {
        setBestMoveSoFar(EMPTY);
    }

    public void setNumOfThreads(int num) {
        numOfThreadsLbl.setText(num + "");
    }

    public void setCurrentDepth(int depth, long reachedIn) {
        currentDepthLbl.setText(depth + "");
        reachedDepthTimeLbl.setText(StrUtils.createTimeStr(reachedIn));
    }

    public void setMoveEval(Evaluation eval) {
        moveEvaluationLbl.setText(formatValue(eval.getEval()));
    }

}
