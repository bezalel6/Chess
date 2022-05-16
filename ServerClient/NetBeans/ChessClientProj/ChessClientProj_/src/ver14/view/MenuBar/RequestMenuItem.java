package ver14.view.MenuBar;

import ver14.Client;
import ver14.SharedClasses.DBActions.DBRequest.PreMadeRequest;
import ver14.view.AuthorizedComponents.MenuItem;

import java.awt.*;

/**
 * represents a Request menu item, that when clicked, will send a {@link PreMadeRequest} to the server.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class RequestMenuItem extends MenuItem {
    /**
     * Instantiates a new Request menu item.
     *
     * @param preMadeRequest the pre made request
     * @param client         the client
     * @param font           the font
     */
    public RequestMenuItem(PreMadeRequest preMadeRequest, Client client, Font font) {
        super(preMadeRequest.createBuilder().getName(), preMadeRequest.authSettings);
        addActionListener(l -> client.request(preMadeRequest));
        setFont(font);
    }
}
