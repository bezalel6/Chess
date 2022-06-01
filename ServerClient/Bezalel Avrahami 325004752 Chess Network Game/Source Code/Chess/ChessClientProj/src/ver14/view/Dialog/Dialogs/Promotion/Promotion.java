package ver14.view.Dialog.Dialogs.Promotion;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.view.Dialog.DialogDetails;
import ver14.view.Dialog.DialogProperties;
import ver14.view.Dialog.Selectables.SelectablePiece;
import ver14.view.Dialog.SimpleDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * represents a Promotion dialog for choosing a piece to promote to.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Promotion extends SimpleDialog {
    /**
     * The List.
     */
    private final PromotionList list;

    /**
     * Instantiates a new Promotion dialog.
     *
     * @param playerColor the player color
     */
    public Promotion(PlayerColor playerColor) {
        super(new DialogProperties(new DialogDetails("promotion", "promotion")));
//        MyJFrame.debugAdapter(this);
        list = new PromotionList(this, playerColor);
        setup(null, list);

    }

    public static void main(String[] args) {
//        FlatLightLaf.setup();
//        new Promotion(PlayerColor.WHITE).start();

        JLabel lbl = new JLabel("jhsadhia");
        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println(e);
            }
        });
        new JFrame() {{
            setSize(500, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new GridLayout(0, 1));
            add(new JButton("dw"));
            add(lbl);
            setVisible(true);
        }};
    }
//    /**
//     * The entry point of application.
//     *
//     * @param args the input arguments
//     */
//    public static void main(String[] args) {
//        new PromotionDialog(PlayerColor.WHITE).start();
//    }

    /**
     * Gets the dialog's result.
     *
     * @return the chosen piece type
     */
    public Piece getResult() {
        return ((SelectablePiece) list.getResult()).piece();
    }


}
