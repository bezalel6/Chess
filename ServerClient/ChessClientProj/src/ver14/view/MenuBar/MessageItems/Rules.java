package ver14.view.MenuBar.MessageItems;

import ver14.Client;

import java.awt.*;
import java.io.File;

public class Rules extends MessageItem {
    public Rules(Client client) {
        super("Rules", client);
    }


    @Override
    protected void onClick() {
        String url = Rules.class.getResource("/assets/rules.pdf").getPath();
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(url);
                Desktop.getDesktop().open(myFile);
            } catch (Exception ex) {
                // no application registered for PDFs
            }
        }
    }

    @Override
    protected String msg() {
        return null;
    }

    @Override
    protected String title() {
        return "Game Rules";
    }
}
