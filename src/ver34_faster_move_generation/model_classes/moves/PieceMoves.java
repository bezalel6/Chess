package ver34_faster_move_generation.model_classes.moves;

import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.Player;
import ver34_faster_move_generation.model_classes.Board;
import ver34_faster_move_generation.model_classes.CastlingAbility;
import ver34_faster_move_generation.model_classes.pieces.Pawn;
import ver34_faster_move_generation.model_classes.pieces.Piece;
import ver34_faster_move_generation.model_classes.pieces.Rook;

import java.util.ArrayList;

import static ver34_faster_move_generation.model_classes.pieces.Piece.PAWN;

public class PieceMoves {
    private Piece piece;
    private long currentBoardHash;
    private ArrayList<ArrayList<Move>> pseudoMoves;
    private ArrayList<ArrayList<Move>> pseudoLegalMoves;
    private ArrayList<Move> legalMoves;

    public PieceMoves(Piece piece) {
        this.piece = piece;
    }

    private static ArrayList<ArrayList<Move>> createPseudoLegalMoves(Board board, ArrayList<ArrayList<Move>> pseudoMoves, Piece piece) {
        return createPseudoLegalMoves(board, pseudoMoves, piece.getPieceType(), piece.getPieceColor());
    }

    public static ArrayList<ArrayList<Move>> createPseudoLegalMoves(Board board, ArrayList<ArrayList<Move>> pseudoMoves, int pieceType, int pieceColor) {
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        for (ArrayList<Move> list : pseudoMoves) {
            ArrayList<Move> addingNow = new ArrayList<>();
            boolean keepAdding = true;
            for (Move move : list) {
                boolean add = true;
                move = Move.copyMove(move);
                Location destination = move.getMovingTo();
                Piece destPiece = board.getPiece(destination);
                if (destPiece != null) {
                    keepAdding = false;
                    if (pieceType == PAWN && !move.isCapturing()) {
                        add = false;
                    } else {
                        add = !destPiece.isOnMyTeam(pieceColor);
                        move.setCapturing(destPiece, board);
                    }
                }
                if (add && move.isCapturing()) {
                    if (destPiece == null) {
                        add = false;
                        if (checkEnPassantCapture(move.getMovingTo(), pieceColor, board)) {
                            move.setCapturing(board.getPiece(board.getEnPassantActualLoc()), board);
                            move.setMoveFlag(Move.MoveFlag.EnPassant);
                            move.setIntermediateMove(new BasicMove(board.getEnPassantActualLoc(), board.getEnPassantTargetLoc()));
                            add = true;
                        }
                    }
                }
                if (add && move instanceof Castling) {

                    int side = ((Castling) move).getSide();
                    int castlingAbility = board.getCastlingAbility();
                    if (CastlingAbility.checkCastling(pieceColor, side, castlingAbility)) {
                        add = checkCastlingLocs((Castling) move, pieceColor, board);
                    } else add = false;
                }
                if (add)
                    addingNow.add(move);
                if (!keepAdding)
                    break;
            }
            ret.add(addingNow);
        }
        return ret;
    }

    private static boolean checkEnPassantCapture(Location capturingLoc, int pieceColor, Board board) {
        Location enPassantTargetLoc = board.getEnPassantTargetLoc();
        if (enPassantTargetLoc != null && enPassantTargetLoc.equals(capturingLoc)) {
            Location enPassantActualLoc = board.getEnPassantActualLoc();
            Piece epsnPiece = board.getPiece(enPassantActualLoc);
            return epsnPiece != null && !epsnPiece.isOnMyTeam(pieceColor);
        }
        return false;
    }

    private static boolean checkCastlingLocs(Castling castling, int pieceColor, Board board) {
        Location[] list = castling.getCastlingLocs();
        for (int i = 0; i < list.length; i++) {
            Location loc = list[i];
            if (Castling.ROOK_STARTING_LOC != i) {
                if (!board.isSquareEmpty(loc))
                    return false;
            } else {
                Piece r = board.getPiece(loc);
                if (!(r instanceof Rook) || !r.isOnMyTeam(pieceColor))
                    return false;
            }
        }
        return true;
    }

    private boolean checkThreatenedCastling(Castling castling, int pieceColor, Board board) {
        for (Location loc : castling.getKingPath()) {
            if (board.isThreatened(loc, Player.getOpponent(pieceColor)))
                return false;
        }
        return true;
    }

    private ArrayList<Move> legalize(Board board) {
        ArrayList<Move> ret = new ArrayList<>();
        for (ArrayList<Move> list : getPseudoLegalMoves(board)) {
            for (Move move : list) {
                if (move instanceof Castling && !checkThreatenedCastling((Castling) move, piece.getPieceColor(), board)) {
                    continue;
                }
                board.applyMove(move);
                if (!board.isInCheck(piece.getPieceColor())) {
                    ret.add(move);
                }
                board.undoMove();
            }
        }
        return ret;
    }

    public ArrayList<Move> getLegalMoves(Board board) {
        verifyMoves(board);
        if (legalMoves == null) {
            legalMoves = legalize(board);
        }
        return legalMoves;
    }

    public ArrayList<ArrayList<Move>> getPseudoLegalMoves(Board board) {
        verifyMoves(board);
        if (pseudoLegalMoves == null) {
            pseudoLegalMoves = createPseudoLegalMoves(board, pseudoMoves, piece);
        }
        return pseudoLegalMoves;
    }

    private void setPseudoMoves() {
        pseudoMoves = piece.generatePseudoMoves();
    }

    private void setPseudoLegalMoves(Board board) {
        setPseudoMoves();
        pseudoLegalMoves = createPseudoLegalMoves(board, pseudoMoves, piece);
    }

    public void verifyMoves(Board board) {
        long hash = board.getBoardHash().getFullHash();
        if (currentBoardHash != hash) {
            pseudoMoves = null;
            pseudoLegalMoves = null;
            legalMoves = null;
            setPseudoMoves();
        }
    }
}
