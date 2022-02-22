package ver8.view.OldDialogs.GameSelect.Panels;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.RegEx;
import ver8.view.OldDialogs.CombinedKeyAdapter;
import ver8.view.OldDialogs.DialogField;
import ver8.view.OldDialogs.Navigation.BackOkInterface;
import ver8.view.OldDialogs.GameSelect.GameCreatePnl;
import ver8.view.OldDialogs.GameSelect.GameSelect;

import javax.swing.*;

public class FenPnl extends DialogField {
    private final JTextField textField;

    public FenPnl(GameSelect gameSelect, GameCreatePnl gameCreatePnl) {
        super("start from position", RegEx.Fen, true, gameSelect);

        textField = new JTextField() {{
            setPreferredSize(defaultTextFieldSize);
//            setMaximumSize(getPreferredSize());
//            setMinimumSize(getPreferredSize());
        }};
        textField.addKeyListener(CombinedKeyAdapter.combinedKeyAdapter(() -> {
            String fen = textField.getText().equals("") ? null : textField.getText();
            gameSelect.getGameSettings().setFen(fen);
        }));

        GameSettings gameSettings = gameSelect.getGameSettings();
        initBackOk(new BackOkInterface() {
            @Override
            public void onBack() {
                gameSettings.setFen(null);
                gameCreatePnl.fenOptionPnl.setCheckBox(false);
                gameSelect.switchTo(gameCreatePnl);
            }

            @Override
            public void onOk() {
                gameCreatePnl.fenOptionPnl.setCheckBox(gameSettings.getFen() != null);
                gameSelect.switchTo(gameCreatePnl);
            }
        });

        addMainComp(textField);

        doneCreating();
    }

    @Override
    public boolean verify() {
        boolean res = super.verifyRegEx();
        if (res) {
            err("");
        } else {
            err(getError());
        }
        return res;
    }

    @Override
    public String getText() {
        return textField.getText();
    }

    @Override
    public int getMainCompWidth() {
        return textField.getWidth();
    }
}
