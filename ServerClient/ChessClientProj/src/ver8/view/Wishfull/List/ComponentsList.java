package ver8.view.Wishfull.List;

import ver8.SharedClasses.Sync.SyncedItems;
import ver8.view.Wishfull.List.Synced.SyncableList;
import ver8.view.Wishfull.dialogs.GameSelect.Buttons.SelectableButton;
import ver8.view.Wishfull.dialogs.Navigation.Navable;
import ver8.view.Wishfull.dialogs.Selectable;
import ver8.view.Wishfull.dialogs.SelectedCallBack;
import ver8.view.Wishfull.dialogs.VerifiedComponent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ComponentsList implements Navable, VerifiedComponent {
    private final static Color normalClr = Color.WHITE;
    private final static Color selectedClr = Color.orange;
    public final ListType listType;
    protected final String header;
    private final ArrayList<ListItem> listItems;
    protected Parent parent;
    protected Selectable selected = null;
    private SelectedCallBack onSelect;


    public ComponentsList(ListType listType, SelectedCallBack onSelect, String header) {
        this.listType = listType;
        this.header = header;
        this.listItems = new ArrayList<>();
        this.parent = new Parent() {
            @Override
            public void switchToStart() {

            }

            @Override
            public void navigateTo(Navable pnl) {

            }

            @Override
            public void listUpdated() {

            }

            @Override
            public boolean checkErr(ComponentsList list) {
                return true;
            }

            @Override
            public void registerSyncedList(SyncableList list) {

            }

            @Override
            public void onInput() {

            }
        };
        setOnSelect(onSelect);
        parent.onInput();
    }

    public void setOnSelect(SelectedCallBack onSelect) {
        this.onSelect = selected -> {

            if (selected != null)
                onSelect.onSelect(selected);

//            parent.enableOk(checkVerification());

            parent.onInput();
        };
    }

    @Override
    public boolean verify() {
        return true;
    }

    public boolean isVertical() {
        return false;
    }

    public boolean isAnySelected() {
        return selected != null;
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

//        container.updated();↓
//        updateNavText();↓
        updated();
    }

    public abstract Container listContainer();

    private void clicked(ListItem item) {
        if (!(item instanceof SelectableButton selectableButton))
            return;
//        if (selectableButton == null) return;
        if (selectableButton.getBackground().equals(selectedClr)) {
            resetClr();
            selected = null;
            onSelect.onSelect(null);
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

    protected void updated() {
        listContainer().updated();
        parent.listUpdated();
        updateNavText();
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

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }


    public enum ListType {
        Buttons, MenuItems;


    }

    public interface Parent {
        void switchToStart();

        void navigateTo(Navable navable);

        void listUpdated();

        boolean checkErr(ComponentsList list);

        void registerSyncedList(SyncableList list);

        void onInput();
    }

    public interface Container {
        void removeAllListItems();

        void add(ListItem item, ComponentsList list);

        JComponent container();


        //repaint, repack?
        void updated();
    }
}
