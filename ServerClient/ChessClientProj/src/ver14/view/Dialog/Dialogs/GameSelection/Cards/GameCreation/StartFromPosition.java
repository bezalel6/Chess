package ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver14.SharedClasses.Game.GameSetup.GameSettings;
import ver14.SharedClasses.UI.LinkLabel;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.Cards.CardHeader;
import ver14.view.Dialog.Dialog;
import ver14.view.Dialog.Dialogs.GameSelection.Components.FenField;
import ver14.view.IconManager.GameIconsGenerator;
import ver14.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Start from position - start from a custom position.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class StartFromPosition extends GameCreationCard {
    /**
     * The Icon lbl.
     */
    private final JLabel iconLbl;


    /**
     * Instantiates a new Start from position.
     *
     * @param parentDialog the parent dialog
     * @param gameSettings the game settings
     */
    public StartFromPosition(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Start From Position"), parentDialog, gameSettings);
        iconLbl = new JLabel();
        add(iconLbl);
        FenField fenField = new FenField(this, gameSettings);
        addDialogComponent(fenField);
        JPanel pnl = new JPanel(new GridLayout(0, 4, 10, 10));
        Arrays.stream(Position.values()).forEach(position -> {
            pnl.add(new LinkLabel(position.name, () -> {
                fenField.setFen(position.fen);
            }));
        });
        add(pnl);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Size(500);
    }

    protected void changeState(boolean state) {
        if (!state)
            gameSettings.setFen(null);
        checkbox.setState(state);
        checkbox.setEnabled(state);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        iconLbl.setIcon(GameIconsGenerator.generate(gameSettings.getFen(), gameSettings.getPlayerToMove(), new Size(250)));
    }

    /**
     * Position.
     *
     * @author Bezalel Avrahami (bezalel3250@gmail.com)
     */
    public enum Position {
        /**
         * The Starting position.
         */
        StartingPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
        /**
         * The Queen gambit.
         */
        QueenGambit("queen's gambit", "rnbqkbnr/ppp1pppp/8/3p4/2PP4/8/PP2PPPP/RNBQKBNR b KQkq - 0 2"),
        /**
         * The Queen vs pawn.
         */
        QueenVsPawn("8/5K1P/8/8/3q4/8/8/2k5 w - - 0 1"),
        /**
         * The Promotion.
         */
        Promotion("2nqkbnr/pPpppppp/2b5/p7/r7/8/PPPP1PPP/RNBQKBNR w KQk - 0 1"),
        /**
         * The M 1.
         */
        M1("mate in 1", "rnb1k1nr/pppppppp/5q2/2b5/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
        /**
         * The Name.
         */
        public final String name;
        /**
         * The Fen.
         */
        public final String fen;

        /**
         * Instantiates a new Position.
         *
         * @param fen the fen
         */
        Position(String fen) {
            this.name = StrUtils.format(name());
            this.fen = fen;
        }

        /**
         * Instantiates a new Position.
         *
         * @param name the name
         * @param fen  the fen
         */
        Position(String name, String fen) {
            this.name = StrUtils.format(name);
            this.fen = fen;
        }
    }
}
