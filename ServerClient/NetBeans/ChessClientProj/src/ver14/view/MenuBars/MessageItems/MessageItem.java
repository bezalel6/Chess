package ver14.view.MenuBars.MessageItems;

import ver14.Client;
import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.Cards.MessageCard;

import javax.swing.*;

public abstract class MessageItem extends JMenuItem {
    protected final Client client;

    public MessageItem(String btnText, Client client) {
        super(btnText);
        this.client = client;
        setFont(FontManager.JMenus.items);
        addActionListener(l -> {
            onClick();
        });
    }

    protected void onClick() {
        client.getView().showMessage(StrUtils.dontCapFull(msg()), title(), MessageCard.MessageType.INFO);

    }

    protected abstract String msg();

    protected abstract String title();

    public static String category(String name, String... values) {
        StringBuilder bldr = new StringBuilder();
        bldr.append(name).append(":").append("\n\n");
        for (int i = 0; i < values.length; i++) {
            bldr.append("\t");
            if (values.length > 1)
                bldr.append((i + 1)).append(". ");
            bldr.append(values[i]).append("\n");
        }
        return bldr.append("\n").toString();
    }
}
