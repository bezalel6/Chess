package ver8.view.Dialog.Components;

import ver8.SharedClasses.Callbacks.Callback;
import ver8.view.Dialog.Dialog;
import ver8.view.Dialog.DialogFields.DialogField;
import ver8.view.Dialog.Selectables.Selectable;
import ver8.view.Dialog.Selectables.SelectableButton;
import ver8.view.OldDialogs.Header;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ListComponent extends DialogField {
    protected final ArrayList<SelectableButton> btns;
    protected Selectable selected = null;

    public ListComponent(int cols, Header header, Dialog parent) {
        super(cols, header, parent);
        btns = new ArrayList<>();
    }

    public void addComponents(Selectable... components) {
        for (Selectable comp : components) {
            addComponent(comp);
        }
    }

    public void addComponent(Selectable item) {
        SelectableButton btn = new SelectableButton(item, onSelect());
        btns.add(btn);
        add(btn);
    }

    public final Callback<Selectable> onSelect() {
        return justSelected -> {
            //justSelected is null when user unselects button
            selected = justSelected;
            btns.forEach(btn -> btn.setSelected(selected == btn.getValue()));
            onSelected();
            onInputUpdate();
        };
    }

    protected abstract void onSelected();

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
}
