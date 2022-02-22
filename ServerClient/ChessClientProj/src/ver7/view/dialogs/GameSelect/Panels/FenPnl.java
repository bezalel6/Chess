package ver7.view.dialogs.GameSelect.Panels;

import ver7.SharedClasses.GameSettings;
import ver7.SharedClasses.RegEx;
import ver7.SharedClasses.ui.MyJButton;
import ver7.view.dialogs.CombinedKeyAdapter;
import ver7.view.dialogs.DialogField;
import ver7.view.dialogs.Navigation.BackOkInterface;
import ver7.view.dialogs.Navigation.Navable;
import ver7.view.dialogs.GameSelect.GameCreatePnl;
import ver7.view.dialogs.GameSelect.GameSelect;

import javax.swing.*;

public class FenPnl extends DialogField implements Navable {
    private final JTextField textField;
    private final MyJButton navBtn;

    public FenPnl(GameSelect gameSelect, GameCreatePnl gameCreatePnl) {
        super("start from position", RegEx.Fen, true, gameSelect);

        this.navBtn = new MyJButton();

        textField = new JTextField() {{
            setPreferredSize(defaultTextFieldSize);
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

    @Override
    public AbstractButton navigationComp() {
        return navBtn;
    }

    @Override
    public String getNavText() {
        return "Start from position";
    }

    @Override
    public void setNavText(String str) {
        navBtn.setText(str);
    }
}
