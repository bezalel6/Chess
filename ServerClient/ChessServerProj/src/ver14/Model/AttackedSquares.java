package ver14.Model;

import ver14.SharedClasses.Game.GameSetup.BoardSetup.Pieces.PieceType;
import ver14.SharedClasses.Game.Location;
import ver14.SharedClasses.Game.Moves.Direction;
import ver14.SharedClasses.Game.PlayerColor;


/**
 * Attacked squares - calculates which squares are attacked by using bitwise operations on pieces {@link Bitboard}s.
 * saving a lot of time by batch calculating for each piece type instead of calculating by individual piece
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class AttackedSquares {

    /**
     * The Attacking player color.
     */
    private final PlayerColor attackingPlayerColor;
    /**
     * The Attacked player color.
     */
    private final PlayerColor attackedPlayerColor;
    /**
     * The Attacking pieces.
     */
    private final PiecesBBs attackingPieces;
    /**
     * The Attacked squares.
     */
    public Bitboard attackedSquares;
    /**
     * The Attacked player bb.
     */
    private Bitboard attackedPlayerBB;
    /**
     * The Checking attacked.
     */
    private Location checkingAttacked;

    /**
     * Instantiates a new Attacked squares.
     *
     * @param model                the model
     * @param attackingPlayerColor the attacking player color
     */
    public AttackedSquares(Model model, PlayerColor attackingPlayerColor) {
        this(attackingPlayerColor, model.getPlayersPieces(attackingPlayerColor), model.getPlayersPieces(attackingPlayerColor.getOpponent()).getAll());
    }

    /**
     * Instantiates a new Attacked squares.
     *
     * @param attackingPlayerColor the attacking player color
     * @param attackingPieces      the attacking pieces
     * @param attackedPlayerBB     the attacked player bb
     */
    AttackedSquares(PlayerColor attackingPlayerColor, PiecesBBs attackingPieces, Bitboard attackedPlayerBB) {
        this.attackingPlayerColor = attackingPlayerColor;
        this.attackedPlayerColor = attackingPlayerColor.getOpponent();
        this.attackingPieces = attackingPieces;
        this.attackedPlayerBB = attackedPlayerBB;
        this.attackedSquares = new Bitboard();
    }

    /**
     * Gets a bitboard of all the attacked squares.
     *
     * @param model                the model
     * @param attackingPlayerColor the attacking player color
     * @return a bitboard of all the squares attacked by the {@code attackingPlayerColor}
     */
    public static Bitboard getAttackedSquares(Model model, PlayerColor attackingPlayerColor) {
        return new AttackedSquares(model, attackingPlayerColor).getAttackedSquares();
    }

    /**
     * Get all attacked squares.
     *
     * @return the attacked squares
     */
    public Bitboard getAttackedSquares() {
        this.attackedSquares.reset();
        PieceType[] piece_types = PieceType.PIECE_TYPES;
        for (PieceType pieceType : piece_types) {
            attack(pieceType);
        }
        return attackedSquares;
    }

    /**
     * Attack.
     *
     * @param pieceType           the piece type
     * @param attackingDirections the attacking directions
     */
    public void attack(PieceType pieceType, Direction... attackingDirections) {
        attack(pieceType, attackingPieces.getBB(pieceType), attackingDirections);
    }

    /**
     * Attack -  shift the given {@link Bitboard} according to the attacking directions. resulting in a quick, batch calculation of all the attacked squares by the given piece
     *
     * @param pieceType           the piece type
     * @param attackingPiecesBB   the attacking pieces bb
     * @param attackingDirections the attacking directions that will be used to calculate the attack. if none will be passed, the directions used will be the piece type's attacking directions
     */
    private void attack(PieceType pieceType, Bitboard attackingPiecesBB, Direction... attackingDirections) {
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

    /**
     * Is the given loc threatened by the {@code attackingPlayerColor}.
     *
     * @param model                the model representing the current position
     * @param loc                  the loc
     * @param attackingPlayerColor the attacking player color
     * @return true if the loc is attacked by the attacking player, false otherwise.
     */
    public static boolean isAttacked(Model model, Location loc, PlayerColor attackingPlayerColor) {
        return new AttackedSquares(model, attackingPlayerColor).isAttacked(loc);
    }

    /**
     * Is a location attacked.
     *
     * @param loc the loc
     * @return true if the location is attacked, false otherwise.
     */
    private boolean isAttacked(Location loc) {
        PieceType[] piece_types = PieceType.ATTACKING_PIECE_TYPES;
        this.checkingAttacked = loc;
        for (PieceType pieceType : piece_types) {
            attack(pieceType);
            if (attackedSquares.isSet(loc))
                return true;
        }
        return false;
    }


    /**
     * Calculate a bitboard of the {@code pieceType} attacks from the {@code pieceBB}. every bit set on the  {@code pieceBB} will be treated
     * as a {@code pieceType}, and will attack like one.
     *
     * @param pieceType            the piece type
     * @param pieceBB              the piece bb
     * @param attackingPlayerColor the attacking player color
     * @param model                the model representing the current position
     * @return a {@link Bitboard} representation of all the attacked squares
     */
    public static Bitboard getPieceAttacksFrom(PieceType pieceType, Bitboard pieceBB, PlayerColor attackingPlayerColor, Model model) {
        AttackedSquares attackedSquares = new AttackedSquares(model, attackingPlayerColor);
        attackedSquares.attack(pieceType, pieceBB);
        return attackedSquares.attackedSquares;
    }
}
