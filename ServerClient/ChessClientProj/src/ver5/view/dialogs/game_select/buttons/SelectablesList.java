package ver5.view.dialogs.game_select.buttons;

import ver5.view.dialogs.*;

import java.awt.*;
import java.util.ArrayList;

public class SelectablesList extends WinPnl implements VerifiedComponent {
    private final static Color normalClr = Color.WHITE;
    private final static Color selectedClr = Color.orange;
    public final boolean isList;
    protected final MyDialog parent;
    private final ArrayList<SelectableButton> buttons;
    private final SelectedCallBack onSelect;
    private Selectable selected = null;

    public SelectablesList(Selectable[] selectables, String header, Selectable defaultValue, SelectedCallBack onSelect, MyDialog parent) {
        this(selectables, header, defaultValue, onSelect, parent, false);
    }

    public SelectablesList(Selectable[] selectables, String header, Selectable defaultValue, SelectedCallBack onSelect, MyDialog parent, boolean verticalList) {
        super(verticalList ? 1 : 3, new Header(header), true);
        this.parent = parent;
        this.isList = verticalList;
        this.onSelect = onSelect;
        this.buttons = new ArrayList<>();
        sync(selectables, defaultValue);
    }

    protected void sync(Selectable[] selectables, Selectable... _defaultValue) {
        buttons.clear();
        removeAll();
        Selectable defaultValue = (_defaultValue.length == 0 || _defaultValue[0] == null) ? selected : _defaultValue[0];
        SelectableButton defaultSelectableButton = null;
        for (Selectable selectable : selectables) {
            SelectableButton adding = new SelectableButton(selectable) {{
                setOnClick(() -> clicked(this));
            }};
            if (selectable.equals(defaultValue))
                defaultSelectableButton = adding;
            buttons.add(adding);
        }
        resetClr();

        clicked(defaultSelectableButton);

        buttons.forEach(this::add);
        repaint();
        parent.repackWin();
    }

    private void clicked(SelectableButton selectableButton) {
        if (selectableButton == null) return;
        if (selectableButton.getBackground().equals(selectedClr)) {
            resetClr();
            selected = null;
            return;
        }
        selected = selectableButton.value;
        highlight(selectableButton);
        onSelect.onSelect(selectableButton.value);
    }

    private void resetClr() {
        buttons.forEach(selectableButton -> selectableButton.setBackground(normalClr));
    }

    private void highlight(SelectableButton selectableButton) {
        resetClr();
        selectableButton.setBackground(selectedClr);
    }

    @Override
    public boolean verify() {
        return isAnySelected();
    }

    @Override
    public String getError() {
        return "not all are selected";
    }

    public boolean isAnySelected() {
        return selected != null;
    }
}
