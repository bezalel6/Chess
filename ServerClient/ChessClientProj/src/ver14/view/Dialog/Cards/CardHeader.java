package ver14.view.Dialog.Cards;

import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.UI.MyLbl;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.Dialog.Properties;

import javax.swing.*;

public class CardHeader extends Header {
    private final String cardName;

    public CardHeader(String txt, ImageIcon icon, boolean center, String cardName) {
        super(txt, icon, center);
        this.cardName = cardName;
    }

    public CardHeader(ImageIcon icon, boolean center, String cardName) {
        super(icon, center);
        this.cardName = cardName;
    }

    public CardHeader(Properties properties) {
        this(properties.details().header());
    }

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

    public String getCardName() {
        return cardName;
    }

    @Override
    protected MyLbl createHeader() {
        MyLbl lbl = super.createHeader();
        lbl.setFont(FontManager.Dialogs.dialogHeader);
        return lbl;
    }
}
