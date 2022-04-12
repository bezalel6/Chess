package ver14.view.SidePanel;

import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.moves.Move;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.ui.MyJButton;
import ver14.view.Board.BoardPanel;
import ver14.view.Board.ViewSavedBoard;
import ver14.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;


public class MoveLog extends JPanel {
    private final MyJButton forward, back, start, end;
    private final ArrayList<ViewSavedBoard> boardsList = new ArrayList<>();
    private final ArrayList<MyJButton> movesBtns;
    private final JPanel whiteMoves, blackMoves;
    private BoardPanel boardPanel;
    private JScrollPane moveLogScroll;
    private int currentMoveIndex = 0;
    private boolean justAddedMove = false;

    public MoveLog() {
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
    }


    private void forward() {
        if (currentMoveIndex > lastMoveIndex() - 1) {
        } else {
            currentMoveIndex++;
            switchToCurrentIndex();
        }
    }

    private void back() {
        if (currentMoveIndex < 1) {
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

        JPanel moves = new JPanel(new GridLayout(1, 0));
//        moves.setInsets(new Insets(0, 0, 0, 0));
        moves.add(whiteMoves);
        moves.add(blackMoves);
        JPanel container = new JPanel(new GridBagLayout()) {{
            setAutoscrolls(true);
//            setInsets(new Insets(0, 0, 0, 0));
        }};
        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.weightx = 10;
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.VERTICAL;
        container.add(moves, gbc);

        moveLogScroll = new JScrollPane(container) {{
            setPreferredSize(new Size(225, 104));
            setAutoscrolls(true);
            setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//            setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//            setPreferredSize(getPreferredSize());
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
    public synchronized void addMove(Move move) {
        Font font = FontManager.sidePanel;
        MyJButton moveBtn = new MyJButton(StrUtils.dontCapFull(move.getAnnotation()), font) {
            {
                setSize(getPreferredSize());
            }

//            @Override
//            public Dimension getPreferredSize() {
//                return new Size(50, 20);
//            }
        };
//        moveBtn.setPreferredSize();
        movesBtns.add(moveBtn);

        ViewSavedBoard board = new ViewSavedBoard(boardPanel);

        if (move.getMovingColor() == PlayerColor.WHITE) {
//            MyFakeBtn lbl = new MyFakeBtn(move.getPrevFullMoveClock() + "");
//            lbl.setFont(font);

            moveBtn.setText(move.getPrevFullMoveClock() + ". " + moveBtn.getText());
//            lbl.setSize(10, lbl.getHeight());
        }
        justAddedMove = true;
        (move.getMovingColor() == PlayerColor.WHITE ? whiteMoves : blackMoves).add(moveBtn);
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
    }

    private synchronized void switchTo(int index) {
        if (index != currentMoveIndex) {
            currentMoveIndex = index;
            switchToCurrentIndex();
        }
    }

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

    private void scroll() {
        int row = currentMoveIndex / 2;
        row = (row - 1) * movesBtns.get(0).getHeight();
        moveLogScroll.getVerticalScrollBar().setValue(row);
    }

    public synchronized void switchToCurrentIndex() {
        boardPanel.lock(!isCaughtUp());
        boardPanel.restoreBoardButtons(boardsList.get(currentMoveIndex));
        enableNavBtns();
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

    public synchronized void reset() {
        whiteMoves.removeAll();
        blackMoves.removeAll();

        currentMoveIndex = 0;
        movesBtns.clear();
        boardsList.clear();
        setBoardPanel(boardPanel);
        boardPanel.lock(false);
    }


    public void setBoardPanel(BoardPanel boardPanel) {
        boardsList.add(new ViewSavedBoard(boardPanel));
        this.boardPanel = boardPanel;
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
