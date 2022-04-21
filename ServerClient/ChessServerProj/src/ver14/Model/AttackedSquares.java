package ver14.Model;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.Moves.Direction;
import ver14.SharedClasses.Game.PlayerColor;

public class AttackedSquares {
//    public static final MyHashMap<Bitboard> hashMap = new MyHashMap<>(HashManager.Size.ATTACKED_SQUARES);

    private final PlayerColor attackingPlayerColor;
    private final PlayerColor attackedPlayerColor;
    private final PiecesBBs attackingPieces;
    public Bitboard attackedSquares;
    private Bitboard attackedPlayerBB;
    private Location checkingAttacked;

    public AttackedSquares(Model model, PlayerColor attackingPlayerColor) {
        this(attackingPlayerColor, model.getPlayersPieces(attackingPlayerColor), model.getPlayersPieces(attackingPlayerColor.getOpponent()).getAll());
    }

    AttackedSquares(PlayerColor attackingPlayerColor, PiecesBBs attackingPieces, Bitboard attackedPlayerBB) {
        this.attackingPlayerColor = attackingPlayerColor;
        this.attackedPlayerColor = attackingPlayerColor.getOpponent();
        this.attackingPieces = attackingPieces;
        this.attackedPlayerBB = attackedPlayerBB;
        this.attackedSquares = new Bitboard();
    }

    public static Bitboard getAttackedSquares(Model model, PlayerColor attackingPlayerColor) {
        return new AttackedSquares(model, attackingPlayerColor).getAttackedSquares();
    }

    public Bitboard getAttackedSquares() {
//        long hash = model.getBoardHash().getFullHash();
//        hash = Zobrist.combineHashes(hash, Zobrist.hash(attackingPlayerColor));
//        if (hashMap.containsKey(hash)) {
//            setAttacks();
//            return hashMap.get(hash);
//            this.attackedSquares = (Bitboard) hashMap.get(hash);
//            return attackedSquares;
//        }
        setAttacks();

//        hashMap.put(hash, attackedSquares);

        return attackedSquares;
    }

    private Bitboard setAttacks() {
        this.attackedSquares.reset();
        PieceType[] piece_types = PieceType.PIECE_TYPES;
        for (PieceType pieceType : piece_types) {
            attack(pieceType);
        }
        return attackedSquares;
    }

    public void attack(PieceType pieceType, Direction... attackingDirections) {
        attack(pieceType, attackingPieces.getBB(pieceType), attackingDirections);
    }

    void attack(PieceType pieceType, Bitboard attackingPiecesBB, Direction... attackingDirections) {
        if (attackingDirections.length == 0)
            attackingDirections = pieceType.getAttackingDirections();

        for (int i = 0, attackingDirectionsLength = attackingDirections.length; i < attackingDirectionsLength && (checkingAttacked == null || !attackedSquares.isSet(checkingAttacked)); i++) {
            Direction direction = attackingDirections[i];

            Bitboard pieceBB = attackingPiecesBB.cp();
            /*
             *moving the opponent in the attacking player direction, so it can be attacked and detected one step later.
             * (supposed to be opp perspective, so it moves with it)
             */
            Bitboard adjustedOpponent = attackedPlayerBB.shift(attackingPlayerColor, direction);
            do {
                attackedSquares
                        .orEqual(pieceBB.shiftMe(attackingPlayerColor, direction)
                                .exclude(attackingPieces.getAll())
                                .exclude(adjustedOpponent));
            } while (pieceType.isSliding && pieceBB.notEmpty()
                    && (checkingAttacked == null || !attackedSquares.isSet(checkingAttacked))
            );

        }
    }

    public static boolean isAttacked(Model model, Location loc, PlayerColor attackingPlayerColor) {
        return new AttackedSquares(model, attackingPlayerColor).isAttacked(loc);
    }

    private boolean isAttacked(Location loc) {
        PieceType[] piece_types = PieceType.ATTACKING_PIECE_TYPES;
        this.checkingAttacked = loc;
        for (PieceType pieceType : piece_types) {
            attack(pieceType);
            if (attackedSquares.isSet(loc))
                return true;
        }
        return false;
//        return attackedSquares.isSet(loc);
    }


    public static Bitboard getPieceAttacksFrom(PieceType pieceType, Bitboard pieceBB, PlayerColor attackingPlayerColor, Model model) {
        AttackedSquares attackedSquares = new AttackedSquares(model, attackingPlayerColor);
        attackedSquares.attack(pieceType, pieceBB);
        return attackedSquares.attackedSquares;
    }
}
