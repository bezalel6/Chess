package ver6.view.dialogs.game_select.Panels;

import ver6.SharedClasses.GameSettings;
import ver6.SharedClasses.RegEx;
import ver6.view.dialogs.CombinedKeyAdapter;
import ver6.view.dialogs.DialogField;
import ver6.view.dialogs.Navigation.BackOkInterface;
import ver6.view.dialogs.game_select.GameCreatePnl;
import ver6.view.dialogs.game_select.GameSelect;

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
