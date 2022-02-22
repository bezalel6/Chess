package ver4.model_classes;

import ver4.SharedClasses.PlayerColor;
import ver4.SharedClasses.pieces.PieceType;

import java.util.concurrent.ConcurrentHashMap;

public class AttackedSquares {
    public static final ConcurrentHashMap<Long, Bitboard> hashMap = new ConcurrentHashMap<>();

    private final PlayerColor attackingPlayerColor;
    private final Bitboard allMyPieces;
    private final Bitboard allOppPieces;
    private final Bitboard[] myPieces;
    private final Bitboard attackedSquares;

    private AttackedSquares(Model model, PlayerColor attackingPlayerColor) {
        this.attackingPlayerColor = attackingPlayerColor;

        this.allMyPieces = model.getAllPlayersPieces(attackingPlayerColor);
        this.allOppPieces = model.getAllPlayersPieces(attackingPlayerColor.getOpponent());
        this.myPieces = model.getPlayersPieces(attackingPlayerColor);

        long hash = model.getBoardHash().getFullHash();
        if (HashManager.ATTACKED_SQUARES) {
            if (hashMap.containsKey(hash)) {
                this.attackedSquares = hashMap.get(hash);
                return;
            }
        }

        this.attackedSquares = new Bitboard();

        pawnsAttacks();

        //checking queen attacks isnt necessary. the bishop and rooks consider the queens
        bishopAttacks();
        rookAttacks();

        knightAttacks();
        kingAttacks();

        attackedSquares.xor(allMyPieces);//for king knight and maybe pawns

        if (HashManager.ATTACKED_SQUARES)
            hashMap.put(hash, attackedSquares);
    }

    public static Bitboard getAttackedSquares(Model model, PlayerColor attackingPlayerColor) {
        return new AttackedSquares(model, attackingPlayerColor).getAttackedSquares();
    }

    private void kingAttacks() {
        Bitboard king = getPieceBB(PieceType.KING);
        attackedSquares
                .orEqual(king.shiftLeft())
                .orEqual(king.shiftRight())
                .orEqual(king.shiftUp())
                .orEqual(king.shiftDown())
                .orEqual(king.shiftLeftUp(attackingPlayerColor))
                .orEqual(king.shiftRightUp(attackingPlayerColor))
                .orEqual(king.shiftLeftDown(attackingPlayerColor))
                .orEqual(king.shiftRightDown(attackingPlayerColor));
    }

    private void knightAttacks() {
        Bitboard knights = new Bitboard(getPieceBB(PieceType.KNIGHT));
        if (knights.isEmpty())
            return;

        attackedSquares.orEqual(knightAttack(knights, 2, 1));
        attackedSquares.orEqual(knightAttack(knights, 2, -1));
        attackedSquares.orEqual(knightAttack(knights, -2, 1));
        attackedSquares.orEqual(knightAttack(knights, -2, -1));

        attackedSquares.orEqual(knightAttack(knights, 1, 2));
        attackedSquares.orEqual(knightAttack(knights, -1, 2));
        attackedSquares.orEqual(knightAttack(knights, 1, -2));
        attackedSquares.orEqual(knightAttack(knights, -1, -2));


    }

    private Bitboard knightAttack(Bitboard knights, int up, int left) {
        for (int i = 0; i < Math.abs(up); i++) {
            knights = up > 0 ? knights.shiftUp() : knights.shiftDown();
        }
        for (int i = 0; i < Math.abs(left); i++) {
            knights = left > 0 ? knights.shiftLeft() : knights.shiftRight();
        }
        return knights;
    }

    public Bitboard getAttackedSquares() {
        return attackedSquares;
    }

    private void bishopAttacks() {

        Bitboard bishops = new Bitboard(getPieceBB(PieceType.BISHOP)).orEqual(getPieceBB(PieceType.QUEEN));

        if (bishops.isEmpty())
            return;

        Bitboard shifter = new Bitboard(bishops);
        while (!shifter.isEmpty()) {
            shifter = shifter.shiftLeftUp(attackingPlayerColor).xor(allMyPieces).xor(allOppPieces.shiftLeftUp(attackingPlayerColor));
            attackedSquares.orEqual(shifter);
        }
        shifter = new Bitboard(bishops);
        while (!shifter.isEmpty()) {
            shifter = shifter.shiftLeftDown(attackingPlayerColor).xor(allMyPieces).xor(allOppPieces.shiftLeftDown(attackingPlayerColor));
            attackedSquares.orEqual(shifter);
        }
        shifter = new Bitboard(bishops);
        while (!shifter.isEmpty()) {
            shifter = shifter.shiftRightUp(attackingPlayerColor).xor(allMyPieces).xor(allOppPieces.shiftRightUp(attackingPlayerColor));
            attackedSquares.orEqual(shifter);
        }
        shifter = new Bitboard(bishops);
        while (!shifter.isEmpty()) {
            shifter = shifter.shiftRightDown(attackingPlayerColor).xor(allMyPieces).xor(allOppPieces.shiftRightDown(attackingPlayerColor));
            attackedSquares.orEqual(shifter);
        }
    }

    private void rookAttacks() {
        Bitboard rooks = new Bitboard(getPieceBB(PieceType.ROOK)).orEqual(getPieceBB(PieceType.QUEEN));

        if (rooks.isEmpty())
            return;

        Bitboard shifter = new Bitboard(rooks);
        while (!shifter.isEmpty()) {
            shifter = shifter.shiftRight().xor(allMyPieces).xor(allOppPieces.shiftRight());
            attackedSquares.orEqual(shifter);
        }
        shifter = new Bitboard(rooks);
        while (!shifter.isEmpty()) {
            shifter = shifter.shiftLeft().xor(allMyPieces).xor(allOppPieces.shiftLeft());
            attackedSquares.orEqual(shifter);
        }
        shifter = new Bitboard(rooks);
        while (!shifter.isEmpty()) {
            shifter = shifter.shiftUp().xor(allMyPieces).xor(allOppPieces.shiftUp());
            attackedSquares.orEqual(shifter);
        }
        shifter = new Bitboard(rooks);
        while (!shifter.isEmpty()) {
            shifter = shifter.shiftDown().xor(allMyPieces).xor(allOppPieces.shiftDown());
            attackedSquares.orEqual(shifter);
        }
    }

    private void pawnsAttacks() {
        Bitboard pBB = getPieceBB(PieceType.PAWN);
        if (pBB.isEmpty())
            return;
        attackedSquares.orEqual(pBB.shiftLeftUp(attackingPlayerColor)).orEqual(pBB.shiftRightUp(attackingPlayerColor));
    }

    private Bitboard getPieceBB(PieceType pieceType) {
        return myPieces[pieceType.asInt()];
    }
}
