package ver14.view.SidePanel;

import ver14.SharedClasses.Callbacks.QuestionCallback;
import ver14.SharedClasses.Question;
import ver14.SharedClasses.ui.MyJButton;
import ver14.SharedClasses.ui.MyLbl;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AskPlayer extends JPanel {
    //including empty flahses between
    private final static int numOfFlashes = 10;
    private final static int flashesDelay = 200;
    private final static int borderThickness = 3;
    private final MyLbl header;
    //null is the empty flash
    private final Color[] flashes = {Color.WHITE, null};

    private final AtomicInteger currentClrIndex = new AtomicInteger();
    private final AtomicInteger numOfFlashesDone = new AtomicInteger();
    private final Timer flashingTimer;

    public AskPlayer() {
        header = new MyLbl() {{
            setFont(SidePanel.font);
        }};
        assert flashes.length > 0;
        this.flashingTimer = new Timer(flashesDelay, l -> {
            Color clr = flashes[currentClrIndex.getAndIncrement()];
            flash(clr);
            currentClrIndex.set(currentClrIndex.intValue() % flashes.length);
            numOfFlashesDone.getAndIncrement();
            if (numOfFlashesDone.get() == numOfFlashes) {
                stopFlashing();
            }
        });
        showPnl(false);
    }

    private void flash(Color clr) {
        setBackground(clr);
        Border border = clr == null ? BorderFactory.createLineBorder(null, borderThickness) : BorderFactory.createLineBorder(clr.darker(), borderThickness);
        setBorder(border);
    }


    private void stopFlashing() {
        flash(null);
        setBorder(null);
        this.flashingTimer.stop();
    }

    public void showPnl(boolean show) {

        for (Component component : getComponents()) {
            component.setVisible(show);
        }
        revalidate();
        repaint();
        if (show) {
            flashingTimer.start();
        } else {
            stopFlashing();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AskPlayer ask = new AskPlayer();
        new JFrame() {{
            setSize(500, 500);
            add(ask);
            setVisible(true);
        }};
        Thread.sleep(1000);
        ask.ask(Question.Rematch, a -> {
        });
    }

    public void ask(Question question, QuestionCallback callback) {
        this.header.setText(question.questionStr);
        addLayout(question, callback);
        showPnl(true);
    }

    private void addLayout(Question question, QuestionCallback callback) {
        removeAll();
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.gridheight = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(header, gbc);

        gbc.gridheight = 1;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weightx = 1;

        for (Question.Answer answer : question.getPossibleAnswers()) {
            add(new MyJButton(answer.answerStr, SidePanel.font, () -> {
                callback.callback(answer);
                showPnl(false);
            }), gbc);
        }
    }
}
