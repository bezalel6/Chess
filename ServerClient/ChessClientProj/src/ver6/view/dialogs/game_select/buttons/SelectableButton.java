package ver6.view.dialogs.game_select.buttons;

import ver6.SharedClasses.FontManager;
import ver6.SharedClasses.ui.MyJButton;
import ver6.view.List.ListItem;
import ver6.view.dialogs.Selectable;

import javax.swing.*;
import java.awt.*;

public class SelectableButton extends MyJButton implements ListItem {
    public final Selectable value;

    public SelectableButton(Selectable value) {
        setVerticalTextPosition(SwingConstants.TOP);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setText(value.getText());
        setIcon(value.getIcon());
        setFont(FontManager.defaultSelectableBtn);
        setOnClick(this::onClick);
        this.value = value;
    }

    @Override
    public void onClick() {
        ListItem.super.onClick();
    }

    @Override
    public Component comp() {
        return this;
    }
}
