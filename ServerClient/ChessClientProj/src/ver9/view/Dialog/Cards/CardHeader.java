package ver9.view.Dialog.Cards;

import ver9.SharedClasses.FontManager;
import ver9.view.Dialog.Dialogs.Header;

import javax.swing.*;

public class CardHeader extends Header {
    private final String cardName;

    /**
     * centers
     *
     * @param txtNname
     */
    public CardHeader(String txtNname) {
        this(txtNname, true);
    }

    public CardHeader(String txtNname, boolean center) {
        this(txtNname, center, txtNname);
    }

    public CardHeader(String txt, boolean center, String cardName) {
        super(txt, center);
        this.cardName = cardName;
    }

    public CardHeader(String txt, ImageIcon icon, boolean center, String cardName) {
        super(txt, icon, center);
        this.cardName = cardName;
    }

    public CardHeader(ImageIcon icon, boolean center, String cardName) {
        super(icon, center);
        this.cardName = cardName;
    }

    public String getCardName() {
        return cardName;
    }

    @Override
    protected JLabel createHeader() {
        JLabel lbl = super.createHeader();
        lbl.setFont(FontManager.xLarge);
        return lbl;
    }
}
