package ver14.Model;

import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.PlayerColor;
import ver14.SharedClasses.Game.moves.Direction;
import ver14.SharedClasses.Game.pieces.PieceType;

public class AttackedSquares {
//    public static final MyHashMap<Bitboard> hashMap = new MyHashMap<>(HashManager.Size.ATTACKED_SQUARES);

    private final PlayerColor attackingPlayerColor;
    private final PlayerColor attackedPlayerColor;
    private final PiecesBBs attackingPieces;
    public Bitboard attackedSquares;
    private Bitboard attackedPlayerBB;
    private Pins pins = null;
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

    public static Pins getPins(Model model, PlayerColor attackingPlayerColor) {
        return new AttackedSquares(model, attackingPlayerColor).getPins(model);
    }

    public Pins getPins(Model model) {
        setAttacks();
        Bitboard kingBB = model.getPieceBitBoard(attackedPlayerColor, PieceType.KING);
        Bitboard excluded = attackedPlayerBB.cp().exclude(attackedSquares);
        attackedPlayerBB = excluded;
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
                Bitboard and = temp.and(attackingPieces.getAll());
                if (and.notEmpty()) {
                    PieceType pieceType = attackingPieces.getPieceType(and);
//                    touncomment
//                    if (pieceType.isAttack(direction,)) {
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
                if (temp.anyMatch(attackedPlayerBB)) {
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

        Bitboard excluded = attackedPlayerBB.cp().exclude(attacksFrom(kingBB, false));
        AttackedSquares a = new AttackedSquares(attackingPlayerColor, attackingPieces, excluded);
        ret.setxRayAttack(a.attacksFrom(kingBB, false));

        return ret;
    }

    public static Bitboard getPieceAttacksFrom(PieceType pieceType, Bitboard pieceBB, PlayerColor attackingPlayerColor, Model model) {
        AttackedSquares attackedSquares = new AttackedSquares(model, attackingPlayerColor);
        attackedSquares.attack(pieceType);
        return attackedSquares.attackedSquares;
    }
}
