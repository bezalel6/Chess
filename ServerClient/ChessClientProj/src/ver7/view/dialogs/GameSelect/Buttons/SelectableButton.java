package ver7.view.dialogs.GameSelect.Buttons;

import ver7.SharedClasses.FontManager;
import ver7.SharedClasses.ui.MyJButton;
import ver7.view.List.ListItem;
import ver7.view.dialogs.Selectable;

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
