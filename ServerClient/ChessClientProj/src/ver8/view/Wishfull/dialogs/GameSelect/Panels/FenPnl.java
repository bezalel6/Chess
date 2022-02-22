package ver8.view.Wishfull.dialogs.GameSelect.Panels;

import ver8.SharedClasses.GameSettings;
import ver8.SharedClasses.RegEx;
import ver8.SharedClasses.ui.MyJButton;
import ver8.view.Wishfull.dialogs.CombinedKeyAdapter;
import ver8.view.Wishfull.dialogs.DialogField;
import ver8.view.Wishfull.dialogs.GameSelect.GameCreatePnl;
import ver8.view.Wishfull.dialogs.GameSelect.GameSelect;
import ver8.view.Wishfull.dialogs.Navigation.BackOkInterface;
import ver8.view.Wishfull.dialogs.Navigation.Navable;
import ver8.view.Wishfull.dialogs.WinPnl;

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
                gameSelect.navTo(gameCreatePnl);
            }

            @Override
            public void onOk() {
                gameCreatePnl.fenOptionPnl.setCheckBox(gameSettings.getFen() != null);
                gameSelect.navTo(gameCreatePnl);
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
    public String getNavText() {
        return "Start from position";
    }

    @Override
    public AbstractButton navigationComp() {
        return navBtn;
    }

    @Override
    public void setNavText(String str) {
        navBtn.setText(str);
    }

    @Override
    public WinPnl navTo() {
        return this;
    }
}
