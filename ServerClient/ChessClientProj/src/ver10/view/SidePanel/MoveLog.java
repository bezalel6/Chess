package ver10.view.SidePanel;

import ver10.SharedClasses.FontManager;
import ver10.SharedClasses.PlayerColor;
import ver10.SharedClasses.Utils.StrUtils;
import ver10.SharedClasses.moves.Move;
import ver10.SharedClasses.ui.MyJButton;
import ver10.SharedClasses.ui.MyLbl;
import ver10.view.Board.BoardPanel;
import ver10.view.Board.ViewSavedBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;


//fixme BUSY WAIT(and not a very good one at that) and no sync
public class MoveLog extends JPanel {
    private final MyJButton forward, back, start, end;
    private final ArrayList<ViewSavedBoard> boardsList = new ArrayList<>();
    private final ArrayList<MyJButton> movesBtns;
    private BoardPanel boardPanel;
    private JPanel moveLogPnl;
    private JScrollPane moveLogScroll;
    private int currentMoveIndex = 0;
    private boolean justAddedMove = false;
    private int currentRow = 0;
    private int currentCol = 0;
    private volatile boolean isEditing = false;

    public MoveLog() {
        movesBtns = new ArrayList<>();
        forward = new MyJButton(">", FontManager.sidePanel, this::forward);
        back = new MyJButton("<", FontManager.sidePanel, this::back);
        start = new MyJButton("|<", FontManager.sidePanel, this::start);
        end = new MyJButton(">|", FontManager.sidePanel, this::end);
        addLayout();
    }

    private void forward() {
        if (currentMoveIndex > lastMoveIndex() - 1) {
            System.out.println("cant go forward");
        } else {
            currentMoveIndex++;
            switchToCurrentIndex();
        }
    }

    private void back() {
        if (currentMoveIndex < 1) {
            System.out.println("cant go back");
        } else {
            currentMoveIndex--;
            switchToCurrentIndex();
        }
    }

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

    private void createMoveLogPnl() {
        moveLogPnl = new JPanel(new GridBagLayout()) {{
            setAutoscrolls(true);
        }};
        moveLogScroll = new JScrollPane(moveLogPnl) {{
            setAutoscrolls(true);
            setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            JScrollBar scrollBar = getVerticalScrollBar();
            scrollBar.setBlockIncrement(100);
            scrollBar.addAdjustmentListener(e -> {
                if (justAddedMove) {
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    justAddedMove = false;
                }
            });
        }};
    }

    public void preAdding() {
        if (!isCaughtUp())
            end();
    }

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
     * CALL AFTER UPDATING BOARD
     *
     * @param move
     */
    public void addMove(Move move) {
        enterCritical();
        MyJButton moveBtn = new MyJButton(StrUtils.dontCapFull(move.getAnnotation()));
        movesBtns.add(moveBtn);

        ViewSavedBoard board = new ViewSavedBoard(boardPanel);
        GridBagConstraints gbc = new GridBagConstraints();

        if (move.getMovingColor() == PlayerColor.WHITE) {
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridy = currentRow++;
            gbc.gridx = currentCol++;
            moveLogPnl.add(new MyLbl(move.getPrevFullMoveClock() + ""), gbc);
        }
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = currentCol++;
        if (currentCol == 3)
            currentCol = 0;
        justAddedMove = true;
        moveLogPnl.add(moveBtn, gbc);
        boardsList.add(board);

        if (currentMoveIndex == lastMoveIndex() - 1) {
            currentMoveIndex++;
        }

        moveBtn.setActionCommand(lastMoveIndex() + "");
        moveBtn.addActionListener(e -> {
            switchTo(Integer.parseInt(e.getActionCommand()));
        });

        if (numOfMoves() > 1)
            boardsList.get(lastMoveIndex() - 1).disableAll();
        enableNavBtns();

        exitCritical();
    }

    private void switchTo(int index) {
        if (index != currentMoveIndex) {
            currentMoveIndex = index;
            switchToCurrentIndex();
        }
    }

    public void enableNavBtns() {
        enableForwardNav(!isCaughtUp());
        enableBackNav(currentMoveIndex > 0);
        if (movesBtns.size() > 0) {
            scroll();
            movesBtns.forEach(btn -> btn.setEnabled(true));
            if (currentMoveIndex - 1 >= 0) {
                movesBtns.get(currentMoveIndex - 1).setEnabled(false);
            }
        }
    }

    private void scroll() {
        int row = currentMoveIndex / 2;
        row = (row - 1) * movesBtns.get(0).getHeight();
        moveLogScroll.getVerticalScrollBar().setValue(row);
    }

    public void switchToCurrentIndex() {
        enterCritical();
        boardPanel.lock(!isCaughtUp());
        boardPanel.restoreBoardButtons(boardsList.get(currentMoveIndex));
        enableNavBtns();
        exitCritical();
    }

    private void enableForwardNav(boolean enable) {
        end.setEnabled(enable);
        forward.setEnabled(enable);
    }

    public boolean isCaughtUp() {
        return currentMoveIndex == lastMoveIndex();
    }

    private void enableBackNav(boolean enable) {
        start.setEnabled(enable);
        back.setEnabled(enable);
    }

    private void start() {
        switchTo(0);
    }

    private void end() {
        switchTo(lastMoveIndex());
    }

    public void reset() {
        enterCritical();
        moveLogPnl.removeAll();
        currentMoveIndex = 0;
        currentCol = 0;
        currentRow = 0;
        movesBtns.clear();
        boardsList.clear();
        setBoardPanel(boardPanel);
        exitCritical();
    }

    private void enterCritical() {
        waitForCritical();
        isEditing = true;
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        boardsList.add(new ViewSavedBoard(boardPanel));
        this.boardPanel = boardPanel;
    }

    private void exitCritical() {
        isEditing = false;
    }

    private void waitForCritical() {
        while (isEditing) Thread.onSpinWait();
    }

    public void resetCurrentBoard() {
        if (numOfMoves() > 0) {
            boardsList.set(lastMoveIndex(), new ViewSavedBoard(boardPanel));
        }
    }

    private int numOfMoves() {
        return boardsList.size();
    }

    private int lastMoveIndex() {
        return numOfMoves() - 1;
    }
}