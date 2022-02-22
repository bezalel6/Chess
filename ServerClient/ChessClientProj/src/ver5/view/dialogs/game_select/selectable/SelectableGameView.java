package ver5.view.dialogs.game_select.selectable;

import ver5.view.dialogs.Selectable;

import javax.swing.*;

public class SelectableGameView implements Selectable {
    private final String str;

    public SelectableGameView(String str) {
        this.str = str;
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getText() {
        return str;
    }
}
