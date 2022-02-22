package ver8.view.List;

import ver8.view.OldDialogs.SelectedCallBack;

import javax.swing.*;

public abstract class JMenuCompListOld extends OldComponentsList {

    private final JMenu menu;

    public JMenuCompListOld(String header, SelectedCallBack onSelect) {
        super(ListType.MenuItems, onSelect, header);
        menu = new JMenu(header);
        updateNavText();
    }

    @Override
    public Container listContainer() {
        return new Container() {
            @Override
            public void removeAllListItems() {
                menu.removeAll();
            }

            @Override
            public void add(ListItem item, OldComponentsList list) {
                menu.add(item.comp());
            }

            @Override
            public JComponent container() {
                return menu;
            }

            @Override
            public void updated() {
                System.out.println("check jmenu for updates");
            }
        };
    }

    @Override
    public AbstractButton navigationComp() {
        return menu;
    }

    @Override
    public void updateNavText() {
        menu.setText(getNavText());
    }

    @Override
    public void setNavText(String str) {
        menu.setText(str);
    }
}
