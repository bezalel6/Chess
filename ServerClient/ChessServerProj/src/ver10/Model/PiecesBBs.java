package ver10.Model;

import ver10.SharedClasses.pieces.PieceType;

public class PiecesBBs {
    private final int size;
    private final Bitboard[] bitboards;

    public PiecesBBs(int size) {
        this.size = size;
        bitboards = new Bitboard[size];
        for (int i = 0; i < size; i++) {
            bitboards[i] = new Bitboard();
        }
    }

    public Bitboard getAll() {
        Bitboard ret = new Bitboard();
        for (PieceType pieceType : PieceType.PIECE_TYPES) {
            ret.orEqual(getBB(pieceType));
        }
        return ret;
    }

    public Bitboard[] getBitboards() {
        return bitboards;
    }

    public Bitboard getBB(PieceType pieceType) {
        return bitboards[pieceType.asInt()];
    }

    public PieceType getPieceType(Bitboard bb) {
        for (int i = 0, bitboardsLength = bitboards.length; i < bitboardsLength; i++) {
            Bitboard bitboard = bitboards[i];
            if (bitboard.anyMatch(bb)) {
                return PieceType.getPieceType(i);
            }
        }
        return null;
    }
}
