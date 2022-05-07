package ver14.view.SidePanel;

import ver14.SharedClasses.Game.Moves.Move;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.UI.Buttons.MyJButton;
import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Board.BoardPanel;
import ver14.view.Board.ViewSavedBoard;
import ver14.view.Dialog.Scrollable;
import ver14.view.IconManager.Size;
import ver14.view.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;


/**
 * Move log - represents a scrollable list of moves annotations in pgn format that gets built as the game progresses.
 * at any point, the client can click on any of the previous moves in the game's history, and that position will be loaded.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MoveLog extends JPanel {
    /**
     * The Forward.
     */
    private final MyJButton forward, /**
     * The Back.
     */
    back, /**
     * The Start.
     */
    start, /**
     * The End.
     */
    end;
    /**
     * The Boards list.
     */
    private final ArrayList<ViewSavedBoard> boardsList = new ArrayList<>();
    /**
     * The Moves btns.
     */
    private final ArrayList<MyJButton> movesBtns;
    /**
     * The White moves.
     */
    private final JPanel whiteMoves, /**
     * The Black moves.
     */
    blackMoves;
    private final View view;
    /**
     * The Board panel.
     */
    private BoardPanel boardPanel;
    /**
     * The Move log scroll.
     */
    private Scrollable moveLogScroll;
    /**
     * The Current move index.
     */
    private int currentMoveIndex = 0;

    private View.IgnoreIfUnsafe<Integer> switching = new View.IgnoreIfUnsafe<>() {
        @Override
        protected void ifSafe(Integer parm) {
            view.syncAction(() -> {
//                ThreadsManager.createThread(() -> {
                boardPanel.lock(!isCaughtUp());
                if (!isCaughtUp())
                    boardPanel.loadSaved(boardsList.get(currentMoveIndex));
                else boardPanel.restoreActualPosition();
                enableNavBtns();
//                }, true);
            });
        }
    };

    /**
     * Instantiates a new Move log.
     */
    public MoveLog(View view) {
        this.view = view;
        movesBtns = new ArrayList<>();
        forward = new MyJButton(">", FontManager.sidePanel, this::forward);
        back = new MyJButton("<", FontManager.sidePanel, this::back);
        start = new MyJButton("|<", FontManager.sidePanel, this::start);
        end = new MyJButton(">|", FontManager.sidePanel, this::end);

        whiteMoves = new JPanel();
        whiteMoves.setLayout(new BoxLayout(whiteMoves, BoxLayout.Y_AXIS));
        blackMoves = new JPanel();
        blackMoves.setLayout(new BoxLayout(blackMoves, BoxLayout.Y_AXIS));

        addLayout();
        enableNavBtns();

        view.addUnsafeOperation(switching);
    }


    /**
     * go one move Forward if possible.
     */
    private synchronized void forward() {
        if (currentMoveIndex > lastMoveIndex() - 1) {
        } else {
            currentMoveIndex++;
            switchToCurrentIndex();
        }
    }

    /**
     * go one move Backwards if possible.
     */
    private synchronized void back() {
        if (currentMoveIndex < 1) {
        } else {
            currentMoveIndex--;
            switchToCurrentIndex();
        }
    }

    /**
     * Add layout.
     */
    private void addLayout() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        add(start, gbc);

        add(back, gbc);

        add(forward, gbc);

        add(end, gbc);

        createMoveLogPnl();
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 10;
        gbc.weighty = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 100;
        add(moveLogScroll, gbc);
    }

    /**
     * Create move log pnl.
     */
    private void createMoveLogPnl() {

        JPanel moves = new JPanel(new GridLayout(1, 0));
        moves.add(whiteMoves);
        moves.add(blackMoves);
        JPanel container = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.VERTICAL;
        container.add(moves, gbc);

        moveLogScroll = new Scrollable(container, new Size(225, 104));
    }

    /**
     * before adding applying a new move.
     */
    public void preAdding() {
//        if (!isCaughtUp())
//            end();
    }

    /**
     * Create nav adapter for navigating moves with the arrow keys.
     *
     * @return the key adapter
     */
    public KeyAdapter createNavAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> {
                        start();
                    }
                    case KeyEvent.VK_DOWN -> {
                        end();
                    }
                    case KeyEvent.VK_LEFT -> {
                        back();
                    }
                    case KeyEvent.VK_RIGHT -> {
                        forward();
                    }
                }
            }
        };
    }

    /**
     * add new move to the log. called after applying the move to the board
     *
     * @param move the move
     */
    public synchronized void addMove(Move move) {
//        if (!isCaughtUp())
//            end();
        Font font = FontManager.sidePanel;
//        System.out.println("annotations = " + move.getAnnotation());
        MyJButton moveBtn = new MyJButton((move.getAnnotation()), font) {
            @Override
            public void setText(String text) {
                super.setText(StrUtils.dontCapFull(text));
            }
        };
//        moveBtn.setPreferredSize();
        movesBtns.add(moveBtn);
        ViewSavedBoard board = boardPanel.getActualPosition();
        System.out.println("saving " + board);

        while (move.getMovingColor() != PlayerColor.WHITE && whiteMoves.getComponents().length <= blackMoves.getComponents().length) {
            MyJButton empty = new MyJButton("  ") {
            };
            empty.setEnabled(false);
            whiteMoves.add(empty);
        }

        if (move.getMovingColor() == PlayerColor.WHITE) {
            moveBtn.setText(move.getPrevFullMoveClock() + ". " + moveBtn.getText());
        }
        moveLogScroll.scrollToBottom();
        (move.getMovingColor() == PlayerColor.WHITE ? whiteMoves : blackMoves).add(moveBtn);
        new Exception().printStackTrace();

        boardsList.add(board);

        if (currentMoveIndex == lastMoveIndex() - 1) {
            currentMoveIndex++;
        }

        moveBtn.setActionCommand(lastMoveIndex() + "");
        moveBtn.addActionListener(e -> {
            switchTo(Integer.parseInt(e.getActionCommand()));
        });
        System.out.println("boards list = " + boardsList);
        if (numOfMoves() > 1)
            boardsList.get(lastMoveIndex() - 1).disableAll();
        enableNavBtns();
    }

    /**
     * Last move index int.
     *
     * @return the int
     */
    private int lastMoveIndex() {
        return numOfMoves() - 1;
    }

    /**
     * Switch to a certain move.
     *
     * @param index the index
     */
    private synchronized void switchTo(int index) {
        if (index != currentMoveIndex) {
            currentMoveIndex = index;
            switchToCurrentIndex();
        }
    }

    /**
     * Num of moves int.
     *
     * @return the int
     */
    private int numOfMoves() {
        return boardsList.size();
    }

    /**
     * Enable navigation buttons.
     */
    public void enableNavBtns() {
        enableForwardNav(!movesBtns.isEmpty() && !isCaughtUp());
        enableBackNav(currentMoveIndex > 0);
        if (movesBtns.size() > 0) {
            scroll();
            movesBtns.forEach(btn -> btn.setEnabled(true));
            if (currentMoveIndex - 1 >= 0) {
                movesBtns.get(currentMoveIndex - 1).setEnabled(false);
            }
        }
    }

    /**
     * Switch to current index.
     */
    public synchronized void switchToCurrentIndex() {
        switching.run(currentMoveIndex);
    }

    /**
     * Enable forward navigation.
     *
     * @param enable should enable buttons
     */
    private void enableForwardNav(boolean enable) {
        end.setEnabled(enable);
        forward.setEnabled(enable);
    }

    /**
     * Is caught up - is the move that this board is looking at the last one.
     *
     * @return the boolean
     */
    public boolean isCaughtUp() {
        return currentMoveIndex == lastMoveIndex();
    }

    /**
     * Enable back navigation.
     *
     * @param enable the enable
     */
    private void enableBackNav(boolean enable) {
        start.setEnabled(enable);
        back.setEnabled(enable);
    }

    /**
     * Scroll to the current move index.
     */
    private void scroll() {
        int row = currentMoveIndex / 2;
        row = (row - 1) * movesBtns.get(0).getHeight();
        moveLogScroll.getVerticalScrollBar().revalidate();
        moveLogScroll.getVerticalScrollBar().setValue(row);
//        moveLogScroll.scrollToBottom();
    }

    /**
     * go to Start.
     */
    private void start() {
        switchTo(0);
    }

    /**
     * go to End.
     */
    private void end() {
        switchTo(lastMoveIndex());
    }

    /**
     * Reset log.
     */
    public synchronized void reset() {
        whiteMoves.removeAll();
        blackMoves.removeAll();

        currentMoveIndex = 0;
        movesBtns.clear();
        boardsList.clear();
        setBoardPanel(boardPanel);
        boardPanel.lock(false);
    }

    /**
     * Sets board panel.
     *
     * @param boardPanel the board panel
     */
    public void setBoardPanel(BoardPanel boardPanel) {
//        new Exception().printStackTrace();
//        boardsList.add((boardPanel).getActualPosition());
        this.boardPanel = boardPanel;
    }

    /**
     * Reset current board.
     */
    public void resetCurrentBoard() {
        if (numOfMoves() > 0) {
            new Exception().printStackTrace();
            var b = (boardPanel.getActualPosition());
            System.out.println("b = " + b);
            boardsList.set(lastMoveIndex(), b);
        }
    }
}
