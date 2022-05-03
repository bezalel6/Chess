package ver14.view.Dialog.Cards;

import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.UI.MyLbl;
import ver14.view.Dialog.DialogProperties;
import ver14.view.Dialog.Dialogs.Header;

import javax.swing.*;

/**
 * represents a header for a dialog card.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class CardHeader extends Header {
    /**
     * The Card name.
     */
    private final String cardName;

    /**
     * Instantiates a new Card header.
     *
     * @param txt      the txt
     * @param icon     the icon
     * @param center   the center
     * @param cardName the card name
     */
    public CardHeader(String txt, ImageIcon icon, boolean center, String cardName) {
        super(txt, icon, center);
        this.cardName = cardName;
    }

    /**
     * Instantiates a new Card header.
     *
     * @param icon     the icon
     * @param center   the center
     * @param cardName the card name
     */
    public CardHeader(ImageIcon icon, boolean center, String cardName) {
        super(icon, center);
        this.cardName = cardName;
    }

    /**
     * Instantiates a new Card header.
     *
     * @param dialogProperties the properties
     */
    public CardHeader(DialogProperties dialogProperties) {
        this(dialogProperties.details().header());
    }

    /**
     * centers
     *
     * @param txtNname the txt nname
     */
    public CardHeader(String txtNname) {
        this(txtNname, true);
    }

    /**
     * Instantiates a new Card header.
     *
     * @param txtNname the txt nname
     * @param center   the center
     */
    public CardHeader(String txtNname, boolean center) {
        this(txtNname, center, txtNname);
    }

    /**
     * Instantiates a new Card header.
     *
     * @param txt      the txt
     * @param center   the center
     * @param cardName the card name
     */
    public CardHeader(String txt, boolean center, String cardName) {
        super(txt, center);
        this.cardName = cardName;
    }

    /**
     * Gets card name.
     *
     * @return the card name
     */
    public String getCardName() {
        return (cardName);
    }

    @Override
    protected MyLbl createHeader() {
        MyLbl lbl = super.createHeader();
        lbl.setFont(FontManager.Dialogs.dialogHeader);
        return lbl;
    }
}
