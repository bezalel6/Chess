package ver14.Model;

import ver14.SharedClasses.Game.pieces.PieceType;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PiecesBBs {
    private final int size;
    private final Bitboard[] bitboards;
    private volatile Bitboard prevAll = null;

    public PiecesBBs(int size) {
        this.size = size;

        bitboards = new Bitboard[size];
        for (int i = 0; i < size; i++) {
            bitboards[i] = new Bitboard() {{
                setOnSet(() -> prevAll = null);
            }};
        }

    }

    //tosync
    public synchronized Bitboard getAll() {
        if (prevAll != null)
            return prevAll;
        Bitboard ret = new Bitboard();
        PieceType[] piece_types = PieceType.PIECE_TYPES;
        for (int i = 0, piece_typesLength = piece_types.length; i < piece_typesLength; i++) {
            PieceType pieceType = piece_types[i];
            ret.orEqual(getBB(pieceType));
        }
        prevAll = ret;

        return ret;
    }

    public Bitboard getBB(PieceType pieceType) {
        return bitboards[pieceType.asInt];
    }

    public Bitboard[] getBitboards() {
        return bitboards;
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


    @Override
    public String toString() {
        return Arrays.stream(bitboards).map(Bitboard::prettyBoard).collect(Collectors.joining("\n"));
    }
}
