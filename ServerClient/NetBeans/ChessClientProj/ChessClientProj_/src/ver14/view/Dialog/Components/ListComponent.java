package ver14.view.Dialog.Components;

import ver14.SharedClasses.Callbacks.Callback;
import ver14.view.Dialog.DialogFields.DialogField;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Selectables.Button.NormalButton;
import ver14.view.Dialog.Selectables.Button.SelectableBtn;
import ver14.view.Dialog.Selectables.Selectable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * List component - represents a list component.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class ListComponent extends DialogField<Selectable> {
    /**
     * The Btns.
     */
    protected final ArrayList<SelectableBtn> btns = new ArrayList<>();
    /**
     * The Selected.
     */
    protected Selectable selected = null;
    /**
     * The On select.
     */
    protected final Callback<Selectable> onSelect =
            justSelected -> {
                //justSelected is null when user unselects button
                selected = justSelected;
                btns.forEach(btn -> btn.select(selected == btn.getValue()));
                onSelected();
                onUpdate();
            };

    /**
     * Instantiates a new List component.
     *
     * @param cols   the cols
     * @param header the header
     * @param parent the parent
     */
    public ListComponent(int cols, Header header, Parent parent) {
        super(cols, header, parent);
    }

    @Override
    public void removeAll() {
        btns.clear();
        super.removeAll();
    }

    /**
     * Add components.
     *
     * @param components the components
     */
    public void addComponents(Selectable... components) {
        for (Selectable comp : components) {
            addComponent(comp);
        }
    }

    /**
     * Add component.
     *
     * @param item the item
     */
    public void addComponent(Selectable item) {
        SelectableBtn btn = createButton(item);
        btns.add(btn);
        add(btn.comp());
    }

    /**
     * Create button selectable btn.
     *
     * @param item the item
     * @return the selectable btn
     */
    protected SelectableBtn createButton(Selectable item) {
        return new NormalButton(item, onSelect);
    }

    /**
     * Get random selectable.
     *
     * @return random value from the components. if components are empty null is returned
     */
    public Selectable getRandom() {
        return btns.isEmpty() ? null : btns.get(new Random().nextInt(btns.size())).getValue();
    }

    /**
     * On selected.
     */
    protected abstract void onSelected();

    /**
     * List size int.
     *
     * @return the int
     */
    public int listSize() {
        return btns == null ? 0 : btns.size();
    }

    /**
     * Add components.
     *
     * @param components the components
     */
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
    public void setValue(Selectable value) {
        onSelect.callback(value);
    }
}
