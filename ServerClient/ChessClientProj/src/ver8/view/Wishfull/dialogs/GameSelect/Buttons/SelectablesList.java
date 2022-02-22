package ver8.view.Wishfull.dialogs.GameSelect.Buttons;

import ver8.view.Wishfull.List.ComponentsList;
import ver8.view.Wishfull.dialogs.*;
import ver8.view.dialogs.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;

public class SelectablesList extends ComponentsList implements DialogComponent {
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

    @Override
    public AbstractButton navigationComp() {
        return null;
    }

    @Override
    public void setNavText(String str) {

    }

    @Override
    public WinPnl navTo() {
        return (WinPnl) container.container();
    }

    @Override
    public Parent parent() {
        return parent;
    }

    @Override
    public boolean verify() {
        return selected != null;
    }

    @Override
    public Container listContainer() {
        return container;
    }

}
