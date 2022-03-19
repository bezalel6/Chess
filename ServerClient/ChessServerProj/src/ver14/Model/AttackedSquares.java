package ver14.Model;

import ver14.SharedClasses.Location;
import ver14.SharedClasses.PlayerColor;
import ver14.SharedClasses.moves.Direction;
import ver14.SharedClasses.pieces.PieceType;

public class AttackedSquares {
//    public static final MyHashMap<Bitboard> hashMap = new MyHashMap<>(HashManager.Size.ATTACKED_SQUARES);

    private final PlayerColor attackingPlayerColor;
    private final PlayerColor attackedPlayerColor;
    private final PiecesBBs myPieces;
    private Bitboard oppPieces;
    private Bitboard attackedSquares;
    private Pins pins = null;
    private Location checkingAttacked;

    private AttackedSquares(Model model, PlayerColor attackingPlayerColor) {
        this(attackingPlayerColor, model.getPlayersPieces(attackingPlayerColor), model.getPlayersPieces(attackingPlayerColor.getOpponent()).getAll());
    }

    private AttackedSquares(PlayerColor attackingPlayerColor, PiecesBBs myPieces, Bitboard oppPieces) {
        this.attackingPlayerColor = attackingPlayerColor;
        this.attackedPlayerColor = attackingPlayerColor.getOpponent();
        this.myPieces = myPieces;
        this.oppPieces = oppPieces;
        this.attackedSquares = new Bitboard();
    }

    public static Bitboard getAttackedSquares(Model model, PlayerColor attackingPlayerColor) {
        return new AttackedSquares(model, attackingPlayerColor).getAttackedSquares(model);
    }

    public Bitboard getAttackedSquares(Model model) {
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
        this.attackedSquares = new Bitboard();
        PieceType[] piece_types = PieceType.PIECE_TYPES;
        for (PieceType pieceType : piece_types) {
            attack(pieceType, myPieces.getBB(pieceType));
        }
        return attackedSquares;
    }

    private void attack(PieceType pieceType, Bitboard ogBB, Direction... attackingDirections) {
        if (attackingDirections.length == 0)
            attackingDirections = pieceType.getAttackingDirections();

        boolean isSliding = pieceType.isSliding;
        for (int i = 0, attackingDirectionsLength = attackingDirections.length; i < attackingDirectionsLength; i++) {
            Direction direction = attackingDirections[i];
            Bitboard pieceBB = ogBB.cp();
            Bitboard opp = oppPieces.cp();
            opp.shiftMe(attackingPlayerColor, direction);
            do {
                attackedSquares
                        .orEqual(pieceBB.shiftMe(attackingPlayerColor, direction)
                                .exclude(myPieces.getAll())
                                .exclude(opp));
            } while (isSliding && pieceBB.notEmpty()
//                    && (checkingAttacked == null || !attackedSquares.isSet(checkingAttacked))
            );

        }
    }

    public static boolean isAttacked(Model model, Location loc, PlayerColor attackingPlayerColor) {
        return new AttackedSquares(model, attackingPlayerColor).isAttacked(loc);
    }

    private boolean isAttacked(Location loc) {
        PieceType[] piece_types = PieceType.PIECE_TYPES;
        this.checkingAttacked = loc;
        for (PieceType pieceType : piece_types) {
            attack(pieceType, myPieces.getBB(pieceType));
            if (attackedSquares.isSet(loc))
                return true;
        }
        return false;
    }

    public static Pins getPins(Model model, PlayerColor attackingPlayerColor) {
        return new AttackedSquares(model, attackingPlayerColor).getPins(model);
    }

    public Pins getPins(Model model) {
        setAttacks();
        Bitboard kingBB = model.getPieceBitBoard(attackedPlayerColor, PieceType.KING);
        Bitboard excluded = oppPieces.cp().exclude(attackedSquares);
        oppPieces = excluded;
        pins = new Pins();
        attacksFrom(kingBB, true);
        return pins;
    }

    private Bitboard attacksFrom(Bitboard from, boolean onlyAttack) {
        Direction[] all_directions = Direction.ALL_DIRECTIONS;
        for (int i = 0, all_directionsLength = all_directions.length; i < all_directionsLength; i++) {
            Direction direction = all_directions[i];
            Bitboard temp = from.cp();
            while (temp.notEmpty()) {
                temp.shiftMe(attackingPlayerColor, direction);
                Bitboard and = temp.and(myPieces.getAll());
                if (and.notEmpty()) {
                    PieceType pieceType = myPieces.getPieceType(and);
//                    touncomment
//                    if (pieceType.isAttack(direction)) {
                    if (pieceType != PieceType.KNIGHT) {
                        Bitboard attck = new Bitboard(from, temp, direction, attackingPlayerColor);
                        if (pins != null) {
                            pins.addPin(direction, attck);
                        }
                        attackedSquares.orEqual(attck);
                    }
                    if (onlyAttack)
                        attack(pieceType, and, direction); //only check direction
                    else
                        attack(pieceType, and);

                    break;

//                    }

                }
                if (temp.anyMatch(oppPieces)) {
                    break;
                }
            }
        }
        return attackedSquares;
    }

    public static Attack getCheck(Model model, PlayerColor attackingPlayerColor) {
        return new AttackedSquares(model, attackingPlayerColor).getCheck(model);
    }

    public Attack getCheck(Model model) {
        Attack ret = new Attack();
        Bitboard kingBB = model.getPieceBitBoard(attackedPlayerColor, PieceType.KING);
        ret.setActualAttack(setAttacks());
        attackedSquares.reset();
        ret.setCheckRay(attacksFrom(kingBB, true));
        attackedSquares.reset();

        Bitboard excluded = oppPieces.cp().exclude(attacksFrom(kingBB, false));
        AttackedSquares a = new AttackedSquares(attackingPlayerColor, myPieces, excluded);
        ret.setxRayAttack(a.attacksFrom(kingBB, false));

        return ret;
    }

    public static Bitboard getPieceAttacksFrom(PieceType pieceType, Bitboard pieceBB, PlayerColor attackingPlayerColor, Model model) {
        AttackedSquares attackedSquares = new AttackedSquares(model, attackingPlayerColor);
        attackedSquares.attack(pieceType, pieceBB);
        return attackedSquares.attackedSquares;
    }
}
