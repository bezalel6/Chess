package ver8.view.List;

import ver8.SharedClasses.Sync.SyncedItems;
import ver8.view.OldDialogs.GameSelect.buttons.SelectableButton;
import ver8.view.OldDialogs.Navigation.Navable;
import ver8.view.OldDialogs.Selectable;
import ver8.view.OldDialogs.SelectedCallBack;
import ver8.view.OldDialogs.WinPnl;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public abstract class OldComponentsList implements Navable {
    private final static Color normalClr = Color.WHITE;
    private final static Color selectedClr = Color.orange;
    public final ListType listType;
    protected final String header;
    private final ArrayList<ListItem> listItems;
    private final SelectedCallBack onSelect;
    protected Parent parent;
    protected Selectable selected = null;


    public OldComponentsList(ListType listType, SelectedCallBack onSelect, String header) {
        this.listType = listType;
        this.onSelect = onSelect;
        this.header = header;
        this.listItems = new ArrayList<>();
        this.parent = new Parent() {

            @Override
            public void switchToStart() {

            }

            @Override
            public void navigateTo(WinPnl pnl) {

            }

            @Override
            public void listUpdated() {

            }
        };
    }

    public boolean isVertical() {
        return false;
    }

    public void sync(SyncedItems syncable, Selectable... _defaultValue) {
        sync(Selectable.createSelectables(syncable), _defaultValue);
    }

    public void sync(Collection<Selectable> selectables, Selectable... _defaultValue) {
        //todo only remove whats not on the list
        listItems.clear();
        Container container = listContainer();
        container.removeAllListItems();
        AtomicReference<ListItem> defaultSelectableButton = new AtomicReference<>(null);
        Selectable defaultValue = (_defaultValue.length == 0 || _defaultValue[0] == null) ? selected : _defaultValue[0];
        selectables.forEach(item -> {
            ListItem adding = null;
            switch (listType) {
                case Buttons -> {
                    adding = new SelectableButton(item) {
                        @Override
                        public void onClick() {
                            super.onClick();
                            clicked(this);
                        }
                    };
                }
                case MenuItems -> {
                    adding = new MenuItem(item);
                }
            }
            if (item.equals(defaultValue))
                defaultSelectableButton.set(adding);
            listItems.add(adding);
        });
        resetClr();

        clicked(defaultSelectableButton.get());

        listItems.forEach(item -> container.add(item, this));

        container.updated();
    }

    public abstract Container listContainer();

    private void clicked(ListItem item) {
        if (!(item instanceof SelectableButton selectableButton))
            return;
//        if (selectableButton == null) return;
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
        listItems.forEach(selectableButton -> {
            if (selectableButton instanceof SelectableButton btn) {
                btn.setBackground(normalClr);
            }
        });
    }

    private void highlight(SelectableButton selectableButton) {
        resetClr();
        selectableButton.setBackground(selectedClr);
    }

    @Override
    public String getNavText() {
        return header + " (" + listSize() + ")";
    }

    public int listSize() {
        return listItems.size();
    }

    protected void updated() {
        listContainer().updated();
        parent.listUpdated();
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

//    @Override
//    public boolean verify() {
//        return selected != null;
//    }
//
//    @Override
//    public String getError() {
//        return "err aight";
//    }

    public enum ListType {
        Buttons, MenuItems;


    }

    public interface Parent {
        void switchToStart();

        void navigateTo(WinPnl pnl);

        void listUpdated();

    }

    public interface Container {
        void removeAllListItems();

        void add(ListItem item, OldComponentsList list);

        JComponent container();

//        default

        //repaint, repack?
        void updated();
    }
}
