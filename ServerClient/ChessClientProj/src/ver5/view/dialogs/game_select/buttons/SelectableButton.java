package ver5.view.dialogs.game_select.buttons;

import ver5.SharedClasses.FontManager;
import ver5.SharedClasses.StrUtils;
import ver5.SharedClasses.ui.MyJButton;
import ver5.view.dialogs.Selectable;

public class SelectableButton extends MyJButton {
    public final Selectable value;

    public SelectableButton(Selectable value) {
        setText(StrUtils.htmlNewLines(value.getText()));

//        setText(value.getText());
        setIcon(value.getIcon());
        setFont(FontManager.defaultSelectableBtn);
        this.value = value;
    }
}
