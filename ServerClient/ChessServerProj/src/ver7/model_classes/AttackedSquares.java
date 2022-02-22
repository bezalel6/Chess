package ver7.model_classes;

import ver7.SharedClasses.PlayerColor;
import ver7.SharedClasses.moves.Direction;
import ver7.SharedClasses.pieces.PieceType;
import ver7.model_classes.hashing.HashManager;
import ver7.model_classes.hashing.Zobrist;
import ver7.model_classes.hashing.my_hash_maps.MyHashMap;

public class AttackedSquares {
    public static final MyHashMap hashMap = new MyHashMap(HashManager.Size.ATTACKED_SQUARES);

    private final PlayerColor attackingPlayerColor;
    private final PlayerColor attackedPlayerColor;
    private final PiecesBBs myPieces;
    private Bitboard oppPieces;
    private Bitboard attackedSquares;
    private Pins pins = null;

    private AttackedSquares(Model model, PlayerColor attackingPlayerColor) {
        this(attackingPlayerColor, model.getPlayersPieces(attackingPlayerColor), model.getPlayersPieces(attackingPlayerColor.getOpponent()).getAll());
    }

    private AttackedSquares(PlayerColor attackingPlayerColor, PiecesBBs myPieces, Bitboard oppPieces) {
        this.attackingPlayerColor = attackingPlayerColor;
        this.attackedPlayerColor = attackingPlayerColor.getOpponent();
        this.myPieces = myPieces;
        this.oppPieces = oppPieces;
        attackedSquares = new Bitboard();
    }

    public static Bitboard getAttackedSquares(Model model, PlayerColor attackingPlayerColor) {
        return new AttackedSquares(model, attackingPlayerColor).getAttackedSquares(model);
    }

    public Bitboard getAttackedSquares(Model model) {
        long hash = model.getBoardHash().getFullHash();
        hash = Zobrist.combineHashes(hash, Zobrist.hash(attackingPlayerColor));
        if (hashMap.containsKey(hash)) {
            setAttacks();
            return attackedSquares;
//            this.attackedSquares = (Bitboard) hashMap.get(hash);
//            return attackedSquares;
        }
        setAttacks();

        hashMap.put(hash, attackedSquares);

        return attackedSquares;
    }

    private Bitboard setAttacks() {
        this.attackedSquares = new Bitboard();
        for (PieceType pieceType : PieceType.PIECE_TYPES) {
            attack(pieceType, myPieces.getBB(pieceType));
        }
        return attackedSquares;
    }

    private void attack(PieceType pieceType, Bitboard ogBB, Direction... attackingDirections) {
        if (attackingDirections.length == 0)
            attackingDirections = pieceType.getAttackingDirections();

        boolean isSliding = pieceType.isSlidingPiece();
        for (Direction direction : attackingDirections) {
            Bitboard pieceBB = ogBB.cp();
            Bitboard opp = oppPieces.cp();
            opp.shiftMe(attackingPlayerColor, direction);
            do {
                attackedSquares
                        .orEqual(pieceBB.shiftMe(attackingPlayerColor, direction)
                                .exclude(myPieces.getAll())
                                .exclude(opp));
            } while (isSliding && pieceBB.notEmpty());
        }
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
        for (Direction direction : Direction.ALL_DIRECTIONS) {
            Bitboard temp = from.cp();
            while (temp.notEmpty()) {
                temp.shiftMe(attackingPlayerColor, direction);
                Bitboard and = temp.and(myPieces.getAll());
                if (and.notEmpty()) {
                    PieceType pieceType = myPieces.getPieceType(and);
                    if (pieceType.isAttack(direction)) {
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
                    }

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
