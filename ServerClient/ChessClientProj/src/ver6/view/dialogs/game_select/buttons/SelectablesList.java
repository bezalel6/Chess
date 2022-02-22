package ver6.view.dialogs.game_select.buttons;

import ver6.view.List.ComponentsList;
import ver6.view.dialogs.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;

public class SelectablesList extends ComponentsList implements VerifiedComponent {
    private final static Color normalClr = Color.WHITE;
    private final static Color selectedClr = Color.orange;
    public final boolean isList;
    private final Container container;
    //    protected final MyDialog parent;
//    private final ArrayList<ListItem> listItems;
//    private final SelectedCallBack onSelect;
//    private Selectable selected = null;
    private ListType listType = ListType.Buttons;

    public SelectablesList(Selectable[] selectables, String header, Selectable defaultValue, SelectedCallBack onSelect, MyDialog parent) {
        this(List.of(selectables), header, defaultValue, onSelect, parent);
    }

    public SelectablesList(Collection<Selectable> selectables, String header, Selectable defaultValue, SelectedCallBack onSelect, MyDialog parent) {
        this(selectables, header, defaultValue, onSelect, parent, false);
    }

    public SelectablesList(Collection<Selectable> selectables, String header, Selectable defaultValue, SelectedCallBack onSelect, MyDialog parent, boolean isList) {
        super(ListType.Buttons, onSelect, header);
        this.container = new WinPnl(isList ? 1 : 3, new Header(header), true);
        this.parent = parent;
        this.isList = isList;
//        this.onSelect = onSelect;
//        this.listItems = new ArrayList<>();
        sync(selectables, defaultValue);
    }

//    public void sync(Collection<Selectable> selectables, Selectable... _defaultValue) {
//        //todo only remove whats not on the list
//        listItems.clear();
//        removeAll();
//        Selectable defaultValue = (_defaultValue.length == 0 || _defaultValue[0] == null) ? selected : _defaultValue[0];
//        ListItem defaultSelectableButton = null;
//        for (Selectable selectable : selectables) {
//            ListItem adding = null;
//            switch (listType) {
//                case Buttons -> {
//                    adding = new SelectableButton(selectable) {{
//                        setOnClick(() -> clicked(this));
//                    }};
//                }
//                case MenuItems -> {
//                    adding = new MenuItem(selectable);
//                }
//            }
//            if (selectable.equals(defaultValue))
//                defaultSelectableButton = adding;
//            listItems.add(adding);
//        }
//        resetClr();
//
//        clicked(defaultSelectableButton);
//
//        listItems.forEach(item -> this.add(item.comp()));
//        repaint();
//        if (parent == null) {
//            System.out.println("no parent dialog");
//        } else {
//            parent.repackWin();
//        }
//    }

    @Override
    public Container listContainer() {
        return container;
    }

//    @Override
//    public boolean verify() {
//        return isAnySelected();
//    }
//
//    @Override
//    public String getError() {
//        return "not all are selected";
//    }
//
//    public boolean isAnySelected() {
//        return selected != null;
//    }

//    public void setItemsType(ListType listType) {
//        this.listType = listType;
//    }

//    public void sync(SyncedItems synced, Selectable... _defaultValue) {
//        sync(Selectable.createSelectables(synced), _defaultValue);
//    }

//    private void clicked(ListItem item) {
//        if (!(item instanceof SelectableButton selectableButton))
//            return;
////        if (selectableButton == null) return;
//        if (selectableButton.getBackground().equals(selectedClr)) {
//            resetClr();
//            selected = null;
//            return;
//        }
//        selected = selectableButton.value;
//        highlight(selectableButton);
//        onSelect.onSelect(selectableButton.value);
//    }

//    private void resetClr() {
//        listItems.forEach(selectableButton -> {
//            if (selectableButton instanceof SelectableButton btn) {
//                btn.setBackground(normalClr);
//            }
//        });
//    }

//    private void highlight(SelectableButton selectableButton) {
//        resetClr();
//        selectableButton.setBackground(selectedClr);
//    }

    //    public int sizeOfList() {
//        return listItems.size();
//    }
//
    @Override
    public AbstractButton navigationComp() {
        return null;
    }

    @Override
    public void setNavText(String str) {

    }

    @Override
    public boolean verify() {
        return selected != null;
    }

//
//    public enum ListType {
//        Buttons, MenuItems;
//    }
}
