package ver14.view.MenuBars.MessageItems;

import ver14.Client;

public class Credits extends MessageItem {
    public Credits(Client client) {
        super("Credits", client);
    }

    @Override
    protected String msg() {
        String msg = "";
        msg += category("Programming", "Bezalel Avrahami (bezalel3250@gmail.com)");
        msg += category("Sound", "N/A");
        msg += category("Graphics", category("Icons", "google.com"), "Noam Cohen(#3) (noamcohen2367@gmail.com)", "MASELF");
        msg += category("Inspiration", "lichess.org", "chess.com");
        msg += category("Educational", "Ilan Perets (ilanperets@gmail.com)", "chessprogramming.org", "https://www.youtube.com/watch?v=U4ogK0MIzqk");
        return msg;
    }


    @Override
    protected String title() {
        return "Credits";
    }
}
