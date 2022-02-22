package ver11.view.Dialog.Components;

import ver11.SharedClasses.Callbacks.Callback;
import ver11.view.Dialog.DialogFields.DialogField;
import ver11.view.Dialog.Dialogs.Header;
import ver11.view.Dialog.Selectables.Button.NormalButton;
import ver11.view.Dialog.Selectables.Button.SelectableBtn;
import ver11.view.Dialog.Selectables.Selectable;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ListComponent extends DialogField<Selectable> {
    protected final ArrayList<SelectableBtn> btns;
    protected Selectable selected = null;

    public ListComponent(int cols, Header header, Parent parent) {
        super(cols, header, parent);
        btns = new ArrayList<>();
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
        return new NormalButton(item, onSelect());
    }

    private Callback<Selectable> onSelect() {
        return justSelected -> {
            //justSelected is null when user unselects button
            selected = justSelected;
            btns.forEach(btn -> btn.setSelected(selected == btn.getValue()));
            onSelected();
            onUpdate();
        };
    }

    protected abstract void onSelected();

    public int listSize() {
        return btns.size();
    }

    public void addComponents(Collection<Selectable> components) {
        for (Selectable comp : components) {
            addComponent(comp);
        }
    }

    @Override
    public String errorDetails() {
        return "";
    }

    @Override
    protected boolean verifyField() {
        return selected != null;
    }

    @Override
    protected Selectable getValue() {
        return selected;
    }

    @Override
    protected void setValue(Selectable value) {
        onSelect().callback(value);
    }
}
