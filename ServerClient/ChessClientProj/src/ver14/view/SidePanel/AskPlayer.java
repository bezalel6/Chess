package ver14.view.SidePanel;

import ver14.SharedClasses.Callbacks.AnswerCallback;
import ver14.SharedClasses.Misc.Question;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Scrollable;
import ver14.view.Dialog.WinPnl;
import ver14.view.IconManager.Size;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class AskPlayer extends Scrollable {
    //including empty flahses between

    private final static Size size = new Size(300, 120);
    private final static int numOfFlashes = 8;
    private final static int flashesDelay = 200;
    private final static int borderThickness = 3;
    private final Border noBorder;
    //null is the empty flash
    private final Color[] flashes = {Color.WHITE, null};
    private final Timer flashingTimer;
    private final WinPnl content;
    private final ArrayList<QuestionPnl> shownQuestions = new ArrayList<>();
    private int currentClrIndex = 0;
    private int numOfFlashesDone = 0;
    private boolean justAdded = false;

    public AskPlayer() {
        super(new WinPnl(), size);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.content = (WinPnl) getViewport().getView();
        this.flashingTimer = new Timer(flashesDelay, l -> {
            Color clr = flashes[currentClrIndex++];
            flash(clr);
            currentClrIndex = (currentClrIndex % flashes.length);
            numOfFlashesDone++;
            if (numOfFlashesDone >= numOfFlashes) {
                stopFlashing();
            }
        });
        stopFlashing();

        getVerticalScrollBar().addAdjustmentListener(e -> {
            if (justAdded) {
                SwingUtilities.invokeLater(() -> {
                    verticalScrollBar.revalidate();
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    justAdded = false;
                });
            }
        });

//        showPnl(false);
        noBorder = BorderFactory.createLineBorder(getBackground(), borderThickness);
    }

    private void flash(Color clr) {
//        setBackground(clr);
        clr = clr == null ? null : clr.darker();
        Border border = BorderFactory.createLineBorder(clr, borderThickness);
        setBorder(border);
    }

    private void stopFlashing() {
        flash(null);
        setBorder(noBorder);
        if (this.flashingTimer.isRunning())
            this.flashingTimer.stop();

    }

    public static void main(String[] args) {
        new JFrame() {{
            setLayout(new GridBagLayout());
            setSize(500, 500);
            AskPlayer askPlayer = new AskPlayer();
//            askPlayer.setPreferredSize(askPlayer.getPreferredSize());
//            askPlayer.setPreferredSize(new Size(150));
//            var scrl = new JScrollPane(askPlayer) {
//
//            };
//            scrl.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
//            scrl.setPreferredSize(new Size(300, 100));
//            add(scrl);
            add(askPlayer);
            new Thread(() -> {
                pack();

                IntStream.range(0, 3).forEach(i -> {
                    Question q = i % 2 == 0 ? Question.Rematch : Question.Threefold;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    askPlayer.ask(q, System.out::println);
                });
                askPlayer.replaceWithMsg(Question.Rematch, "WTFFF");
            }).start();
            setVisible(true);
        }};
    }

    public void ask(Question question, AnswerCallback callback) {
        QuestionPnl pnl = new QuestionPnl(question, callback);
        shownQuestions.add(pnl);
        content.add(pnl);
        justAdded = true;
        numOfFlashesDone = (0);
        flashingTimer.start();
    }

    public void replaceWithMsg(Question replacing, String msg) {
        shownQuestions.stream().filter(pnl -> pnl.question.equals(replacing)).findAny().ifPresent(pnl -> {
            pnl.setReplacement(msg);
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    public void removeQuestion(QuestionPnl pnl) {
        content.removeContentComponent(pnl);
        shownQuestions.remove(pnl);
        revalidate();
        repaint();

    }

    public class QuestionPnl extends WinPnl {
        private final Question question;

        public QuestionPnl(Question question, AnswerCallback callback) {
            super(WinPnl.ALL_IN_ONE_ROW, new Header(question.questionStr));
            this.question = question;
            for (Question.Answer answer : question.getPossibleAnswers()) {
                add(createBtn(answer, callback));
            }
        }

        private MyJButton createBtn(Question.Answer answer, AnswerCallback onAns) {
            return new MyJButton(answer.answerStr, SidePanel.font, () -> {
                removeQuestion(this);
                onAns.callback(answer);
//                    showPnl(false);
            });
        }

        @Override
        public Dimension getPreferredSize() {
            return Size.add(new Size(AskPlayer.this.getPreferredSize().width, super.getPreferredSize().height), -10);
        }

        public void setReplacement(String headerMsg) {
            header.setText(headerMsg);
            removeContent();
            add(createBtn(Question.Answer.OK));
        }

        private MyJButton createBtn(Question.Answer answer) {
            return createBtn(answer, ans -> {
            });
        }
    }
}
