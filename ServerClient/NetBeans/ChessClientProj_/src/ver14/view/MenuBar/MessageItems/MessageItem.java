package ver14.view.MenuBar.MessageItems;

import ver14.Client;
import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.Cards.MessageCard.MessageType;

import javax.swing.*;

/**
 * Message item - represents a message item that will show a static message on click.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public abstract class MessageItem extends JMenuItem {
    /**
     * The Client.
     */
    protected final Client client;

    /**
     * Instantiates a new Message item.
     *
     * @param btnText the btn text
     * @param client  the client
     */
    public MessageItem(String btnText, Client client) {
        super(btnText);
        this.client = client;
        setFont(FontManager.JMenus.items);
        addActionListener(l -> {
            onClick();
        });
    }

    /**
     * On click.
     */
    protected void onClick() {
        client.getView().showMessage(StrUtils.dontCapFull(msg()), title(), MessageType.INFO);

    }

    /**
     * Msg string.
     *
     * @return the string
     */
    protected abstract String msg();

    /**
     * Title string.
     *
     * @return the string
     */
    protected abstract String title();

    /**
     * Category string.
     *
     * @param name   the name
     * @param values the values
     * @return the string
     */
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
