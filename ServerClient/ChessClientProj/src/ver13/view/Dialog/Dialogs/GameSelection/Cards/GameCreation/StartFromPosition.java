package ver13.view.Dialog.Dialogs.GameSelection.Cards.GameCreation;

import ver13.SharedClasses.GameSettings;
import ver13.SharedClasses.Utils.StrUtils;
import ver13.SharedClasses.ui.LinkLabel;
import ver13.view.Dialog.Cards.CardHeader;
import ver13.view.Dialog.Dialog;
import ver13.view.Dialog.Dialogs.GameSelection.Components.FenField;
import ver13.view.IconManager.GameIconsGenerator;
import ver13.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class StartFromPosition extends GameCreationCard {
    private final JLabel iconLbl;


    public StartFromPosition(Dialog parentDialog, GameSettings gameSettings) {
        super(new CardHeader("Start From Position"), parentDialog, gameSettings);
        iconLbl = new JLabel();
        add(iconLbl);
        FenField fenField = new FenField(this, gameSettings);
        addDialogComponent(fenField);
        JPanel pnl = new JPanel(new GridLayout(0, 4, 5, 5));
        Arrays.stream(Position.values()).forEach(position -> {
            pnl.add(new LinkLabel(position.name, () -> {
                fenField.setFen(position.fen);
            }));
        });
        add(pnl);
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
        Size size = new Size(getSize());
        if (!size.isValid()) {
            return;
        }
        size.multBy(0.7);
        size = Size.min(size);
        iconLbl.setIcon(GameIconsGenerator.generate(gameSettings.getFen(), gameSettings.getPlayerToMove(), size));
    }

    enum Position {
        StartingPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
        QueenGambit("queen's gambit", "rnbqkbnr/ppp1pppp/8/3p4/2PP4/8/PP2PPPP/RNBQKBNR b KQkq - 0 2"),
        QueenVsPawn("8/5K1P/8/8/3q4/8/8/2k5 w - - 0 1");
        public final String name;
        public final String fen;

        Position(String fen) {
            this.name = StrUtils.format(name());
            this.fen = fen;
        }

        Position(String name, String fen) {
            this.name = StrUtils.format(name);
            this.fen = fen;
        }
    }
}
