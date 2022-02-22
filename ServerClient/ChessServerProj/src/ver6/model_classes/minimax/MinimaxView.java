package ver6.model_classes.minimax;

import ver6.SharedClasses.StrUtils;
import ver6.SharedClasses.evaluation.Evaluation;
import ver6.SharedClasses.moves.Move;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class MinimaxView extends JFrame {
    private static String EMPTY = "-";

    private final JLabel timeLbl;
    private final JLabel bestMoveSoFarLbl;
    private final JLabel chosenMoveLbl;
    private final JLabel currentDepthLbl;
    private final JLabel moveEvaluationLbl;

    public MinimaxView() {
        Font font = new Font(null, Font.BOLD, 30);

        timeLbl = new JLabel(StrUtils.createTimeStr(0)) {{
            setFont(font);
        }};
        bestMoveSoFarLbl = new JLabel(EMPTY) {{
            setFont(font);
        }};
        chosenMoveLbl = new JLabel(EMPTY) {{
            setFont(font);
        }};
        currentDepthLbl = new JLabel(EMPTY) {{
            setFont(font);
        }};
        moveEvaluationLbl = new JLabel(EMPTY) {{
            setFont(font);
        }};

        setSize(800, 200);
        setLayout(new GridLayout(0, 2));

        add(new JLabel("Time Elapsed: ") {{
            setFont(font);
        }});
        add(timeLbl);

        add(new JLabel("Current Depth: ") {{
            setFont(font);
        }});
        add(currentDepthLbl);

        add(new JLabel("Best Move So Far: ") {{
            setFont(font);
        }});
        add(bestMoveSoFarLbl);

        add(new JLabel("Chosen Move: ") {{
            setFont(font);
        }});
        add(chosenMoveLbl);

        add(new JLabel("Move Evaluation: ") {{
            setFont(font);
        }});
        add(moveEvaluationLbl);

        pack();

        setAlwaysOnTop(true);
        setVisible(true);
    }

    public void setTime(long time) {
        timeLbl.setText(StrUtils.createTimeStr(TimeUnit.SECONDS.toMillis(time)));
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

    public void setBestMoveSoFar(Move move) {
        setBestMoveSoFar(move.getAnnotation());
    }

    public void setBestMoveSoFar() {
        setBestMoveSoFar(EMPTY);
    }

    public void setCurrentDepth(int depth) {
        currentDepthLbl.setText(depth + "");
    }

    public void setMoveEval(Evaluation eval) {
        moveEvaluationLbl.setText(eval.getEval() + "");
    }

}
