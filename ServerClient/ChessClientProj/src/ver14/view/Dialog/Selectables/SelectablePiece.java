package ver14.view.Dialog.Selectables;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.Piece;
import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.view.IconManager.IconManager;

import javax.swing.*;
import java.util.ArrayList;

/**
 * represents a Selectable piece.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public record SelectablePiece(Piece piece) implements Selectable {

    /**
     * The constant promotionPieces.
     */
    public static final ArrayList<SelectablePiece>[] promotionPieces;

    static {
        promotionPieces = new ArrayList[PlayerColor.NUM_OF_PLAYERS];
        for (PlayerColor playerColor : PlayerColor.PLAYER_COLORS) {
            promotionPieces[playerColor.asInt] = new ArrayList<>();
        }
        for (PlayerColor clr : PlayerColor.PLAYER_COLORS) {
            for (PieceType pieceType : PieceType.CAN_PROMOTE_TO) {
                promotionPieces[clr.asInt].add(new SelectablePiece(Piece.getPiece(pieceType, clr)));
            }
        }
    }


    @Override
    public ImageIcon getIcon() {
        return IconManager.getPieceIcon(piece);
    }

    @Override
    public String getText() {
        return null;
    }
}
