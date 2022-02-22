package ver7.view.List;

import ver7.view.dialogs.SelectedCallBack;

import javax.swing.*;

public abstract class JMenuCompList extends ComponentsList {

    private final JMenu menu;
    private final Container container;

    public JMenuCompList(String header, SelectedCallBack onSelect) {
        super(ListType.MenuItems, onSelect, header);
        menu = new JMenu(header);
        container = new Container() {
            @Override
            public void removeAllListItems() {
                menu.removeAll();
            }

            @Override
            public void add(ListItem item, ComponentsList list) {
                menu.add(item.comp());
            }

            @Override
            public JComponent container() {
                return menu;
            }

            @Override
            public void updated() {
//                menu.repaint();
//                System.out.println("check jmenu for updates");
            }
        };
        updateNavText();
    }

    @Override
    public Container listContainer() {
        return container;
    }

    @Override
    public AbstractButton navigationComp() {
        return menu;
    }

//    @Override
//    public void updateNavText() {
//        menu.setText(getNavText());
//    }

    @Override
    public void setNavText(String str) {
        menu.setText(str);
    }
}
