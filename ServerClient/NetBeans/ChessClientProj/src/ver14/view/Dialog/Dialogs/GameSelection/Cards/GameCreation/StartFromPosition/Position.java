package ver14.view.Dialog.Dialogs.GameSelection.Cards.GameCreation.StartFromPosition;

import ver14.SharedClasses.Utils.StrUtils;

/**
 * Position - represents pre-made default positions.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public enum Position {
    /**
     * The Starting position.
     */
    StartingPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
    /**
     * The Queen gambit.
     */
    QueenGambit("queen's gambit", "rnbqkbnr/ppp1pppp/8/3p4/2PP4/8/PP2PPPP/RNBQKBNR b KQkq - 0 2"),
    /**
     * The Queen vs pawn.
     */
    QueenVsPawn("8/5K1P/8/8/3q4/8/8/2k5 w - - 0 1"),
    /**
     * The Promotion.
     */
    Promotion("2nqkbnr/pPpppppp/2b5/p7/r7/8/PPPP1PPP/RNBQKBNR w KQk - 0 1"),
    /**
     * The M 1.
     */
    M1("mate in 1", "rnb1k1nr/pppppppp/5q2/2b5/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
    /**
     * The Name.
     */
    public final String name;
    /**
     * The Fen.
     */
    public final String fen;

    /**
     * Instantiates a new Position.
     *
     * @param fen the fen
     */
    Position(String fen) {
        this.name = StrUtils.format(name());
        this.fen = fen;
    }

    /**
     * Instantiates a new Position.
     *
     * @param name the name
     * @param fen  the fen
     */
    Position(String name, String fen) {
        this.name = StrUtils.format(name);
        this.fen = fen;
    }
}
