package ver14.view.MenuBar.MessageItems;

import ver14.Client;

/**
 * Credits message item.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Credits extends MessageItem {
    /**
     * Instantiates a new Credits.
     *
     * @param client the client
     */
    public Credits(Client client) {
        super("Credits", client);
    }

    @Override
    protected String msg() {
        String msg = "";
        msg += category("Programming", "Bezalel Avrahami (bezalel3250@gmail.com)");
        msg += category("Sound", "chess.com");
        msg += category("Graphics", "commons.wikimedia.org", "Noam Cohen(#3) (noamcohen2367@gmail.com)", "Bezalel Avrahami (bezalel3250@gmail.com)", "lichess.org");
        msg += category("Inspiration", "lichess.org", "chess.com");
        msg += category("Education", "Ilan Perets (ilanperets@gmail.com)", "stackoverflow.com", "chessprogramming.org", "youtube.com/watch?v=U4ogK0MIzqk", "wikipedia.org");
        return msg;
    }


    @Override
    protected String title() {
        return "Credits";
    }
}
