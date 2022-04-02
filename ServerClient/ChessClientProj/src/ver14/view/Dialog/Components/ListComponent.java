package ver14.view.Dialog.Components;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.view.Dialog.DialogFields.DialogField;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.Button.NormalButton;
import ver14.view.Dialog.Selectables.Button.SelectableBtn;
import ver14.view.Dialog.Selectables.Selectable;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ListComponent extends DialogField<Selectable> {
    protected final ArrayList<SelectableBtn> btns = new ArrayList<>();
    protected Selectable selected = null;
    private final Callback<Selectable> onSelect =
            justSelected -> {
                //justSelected is null when user unselects button
                selected = justSelected;
                btns.forEach(btn -> btn.select(selected == btn.getValue()));
                onSelected();
                onUpdate();
            };

    public ListComponent(int cols, Header header, Parent parent) {
        super(cols, header, parent);
    }

    @Override
    public void removeAll() {
        btns.clear();
        super.removeAll();
    }

    public void addComponents(Selectable... components) {
        for (Selectable comp : components) {
            addComponent(comp);
        }
    }

    public void addComponent(Selectable item) {
        SelectableBtn btn = createButton(item);
        btns.add(btn);
        add(btn.comp());
    }

    protected SelectableBtn createButton(Selectable item) {
        return new NormalButton(item, onSelect);
    }

    protected abstract void onSelected();

    public int listSize() {
        return btns.size();
    }

    public void addComponents(Collection<? extends Selectable> components) {
        for (Selectable comp : components) {
            addComponent(comp);
        }
    }

    @Override
    public String errorDetails() {
        return "";
    }

    @Override
    protected Selectable getValue() {
        return selected;
    }

    @Override
    protected boolean verifyField() {
        return selected != null;
    }

    @Override
    protected void setValue(Selectable value) {
        onSelect.callback(value);
    }
}
