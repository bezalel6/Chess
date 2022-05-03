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

/**
 * Ask player - represents a panel for asking the player questions. when a question is asked, it will show the question and flash to get the player's attention.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class AskPlayer extends Scrollable {

    /**
     * The constant size.
     */
    private final static Size size = new Size(200, 200);
    /**
     * The constant numOfFlashes.
     * including empty flahses between
     */
    private final static int numOfFlashes = 8;
    /**
     * The constant flashesDelay.
     */
    private final static int flashesDelay = 200;
    /**
     * The constant borderThickness.
     */
    private final static int borderThickness = 3;
    /**
     * The No border.
     */
    private final Border noBorder;
    /**
     * The Flashes.
     */
//null is the empty flash
    private final Color[] flashes = {Color.WHITE, null};
    /**
     * The Flashing timer.
     */
    private final Timer flashingTimer;
    /**
     * The Content.
     */
    private final WinPnl content;
    /**
     * The Shown questions.
     */
    private final ArrayList<QuestionPnl> shownQuestions = new ArrayList<>();
    /**
     * The Current clr index.
     */
    private int currentClrIndex = 0;
    /**
     * The Num of flashes done.
     */
    private int numOfFlashesDone = 0;
    /**
     * The Just added.
     */
    private boolean justAdded = false;

    /**
     * Instantiates a new Ask player.
     */
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

    /**
     * Flash.
     *
     * @param clr the clr
     */
    private void flash(Color clr) {
//        setBackground(clr);
        clr = clr == null ? null : clr.darker();
        Border border = BorderFactory.createLineBorder(clr, borderThickness);
        setBorder(border);
    }

    /**
     * Stop flashing.
     */
    private void stopFlashing() {
        flash(null);
        setBorder(noBorder);
        if (this.flashingTimer.isRunning())
            this.flashingTimer.stop();

    }

//    /**
//     * The entry point of application.
//     *
//     * @param args the input arguments
//     */
//    public static void main(String[] args) {
//        new JFrame() {{
//            setLayout(new GridBagLayout());
//            setSize(500, 500);
//            AskPlayer askPlayer = new AskPlayer();
////            askPlayer.setPreferredSize(askPlayer.getPreferredSize());
////            askPlayer.setPreferredSize(new Size(150));
////            var scrl = new JScrollPane(askPlayer) {
////
////            };
////            scrl.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
////            scrl.setPreferredSize(new Size(300, 100));
////            add(scrl);
//            add(askPlayer);
//            new Thread(() -> {
//                pack();
//
//                IntStream.range(0, 3).forEach(i -> {
//                    Question q = i % 2 == 0 ? Question.Rematch : Question.Threefold;
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    askPlayer.ask(q, System.out::println);
//                });
//                askPlayer.replaceWithMsg(Question.Rematch, "WTFFF");
//            }).start();
//            setVisible(true);
//        }};
//    }

    /**
     * Ask question.
     *
     * @param question the question to ask
     * @param callback the callback to call once the player clicked an answer
     */
    public void ask(Question question, AnswerCallback callback) {
        QuestionPnl pnl = new QuestionPnl(question, callback);
        shownQuestions.add(pnl);
        content.add(pnl);
        justAdded = true;
        numOfFlashesDone = (0);
        flashingTimer.start();
    }

    /**
     * Replace a question with a message.
     *
     * @param replacing the replacing
     * @param msg       the msg
     */
    public void replaceWithMsg(Question replacing, String msg) {
        shownQuestions.stream().filter(pnl -> pnl.question.equals(replacing)).findAny().ifPresent(pnl -> {
            pnl.setReplacement(msg);
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    /**
     * Remove question.
     *
     * @param questionType the question type to remove
     */
    public void removeQuestion(Question.QuestionType questionType) {
        shownQuestions.stream().filter(p -> p.question.questionType.equals(questionType)).findAny().ifPresent(this::removeQuestion);
    }

    /**
     * Remove question.
     *
     * @param pnl the pnl
     */
    public void removeQuestion(QuestionPnl pnl) {
        content.removeContentComponent(pnl);
        shownQuestions.remove(pnl);
        revalidate();
        repaint();

    }

    /**
     * Remove question.
     *
     * @param question the question
     */
    public void removeQuestion(Question question) {
        shownQuestions.stream().filter(p -> p.question.equals(question)).findAny().ifPresent(this::removeQuestion);
    }

    /**
     * Question pnl - represents a single question panel.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public class QuestionPnl extends WinPnl {
        /**
         * The Question.
         */
        private final Question question;

        /**
         * Instantiates a new Question pnl.
         *
         * @param question the question
         * @param callback the callback
         */
        public QuestionPnl(Question question, AnswerCallback callback) {
            super(1, new Header(question.questionStr));
            this.question = question;
            for (Question.Answer answer : question.getPossibleAnswers()) {
                add(createBtn(answer, callback));
            }
        }

        /**
         * Create btn my j button.
         *
         * @param answer the answer
         * @param onAns  the on ans
         * @return the my j button
         */
        private MyJButton createBtn(Question.Answer answer, AnswerCallback onAns) {
            return new MyJButton(answer.answerStr(), SidePanel.font, () -> {
                removeQuestion(this);
                onAns.callback(answer);
//                    showPnl(false);
            });
        }

        @Override
        public Dimension getPreferredSize() {
            return Size.add(new Size(AskPlayer.this.getPreferredSize().width, super.getPreferredSize().height), -10);
        }

        /**
         * Sets replacement for this question's message.
         *
         * @param headerMsg the header msg
         */
        public void setReplacement(String headerMsg) {
            header.setText(headerMsg);
            removeContent();
            add(createBtn(Question.Answer.OK));
        }

        /**
         * Create button.
         *
         * @param answer the answer
         * @return the button
         */
        private MyJButton createBtn(Question.Answer answer) {
            return createBtn(answer, ans -> {
            });
        }
    }
}
