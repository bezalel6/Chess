package ver8.view.OldDialogs.GameSelect.buttons;

import ver8.SharedClasses.FontManager;
import ver8.SharedClasses.ui.MyJButton;
import ver8.view.List.ListItem;
import ver8.view.OldDialogs.Selectable;

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
