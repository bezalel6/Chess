package ver34_faster_move_generation.model_classes.moves;

import ver34_faster_move_generation.Location;
import ver34_faster_move_generation.model_classes.Board;
import ver34_faster_move_generation.model_classes.pieces.Pawn;
import ver34_faster_move_generation.model_classes.pieces.Piece;
import ver34_faster_move_generation.model_classes.pieces.Rook;

import java.util.ArrayList;

public class PieceMoves {
    private final Piece piece;
    private long currentBoardHash;
    private ArrayList<ArrayList<Move>> pseudoMoves;
    private ArrayList<ArrayList<Move>> pseudoLegalMoves;
    private ArrayList<Move> legalMoves;

    public PieceMoves(Piece piece) {
        this.piece = piece;
    }

    private ArrayList<Move> legalize(Board board) {
        ArrayList<Move> ret = new ArrayList<>();
        for (ArrayList<Move> list : getPseudoLegalMoves(board)) {
            for (Move move : list) {
                board.applyMove(move);
                if (!board.isInCheck()) {
                    ret.add(move);
                }
                board.undoMove(move);
            }
        }
        return ret;
    }

    private ArrayList<ArrayList<Move>> createPseudoLegalMoves(Board board) {
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
        for (ArrayList<Move> list : pseudoMoves) {
            ArrayList<Move> addingNow = new ArrayList<>();
            boolean keepAdding = true;
            for (Move move : list) {
                boolean add = true;
                Location destination = move.getMovingTo();
                Piece destPiece = board.getPiece(destination);
                if (destPiece != null) {
                    keepAdding = false;
                    if (piece instanceof Pawn && !move.isCapturing()) {
                        add = false;
                    } else {
                        add = !destPiece.isOnMyTeam(piece);
                        move.setCapturing(destPiece, board);
                    }
                }
                if (add && move.isCapturing()) {
                    if (destPiece == null) {
                        add = false;
                        if (checkEnPassantCapture(move.getMovingTo(), board)) {
                            move.setCapturing(board.getPiece(board.getEnPassantActualLoc()), board);
                            move.setMoveFlag(Move.MoveFlag.EnPassant);
                            move.setIntermediateMove(new BasicMove(board.getEnPassantActualLoc(), board.getEnPassantTargetLoc()));
                            add = true;
                        }
                    }
                }
                if (add && move instanceof Castling) {
                    add = checkCastlingLocs((Castling) move, board);
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

    private boolean checkCastlingLocs(Castling castling, Board board) {
        Location[] list = castling.getCastlingLocs();
        for (int i = 0; i < list.length; i++) {
            Location loc = list[i];
            if (Castling.ROOK_STARTING_LOC != i) {
                if (!board.isSquareEmpty(loc) || board.isThreatened(loc, piece.getOpponent()))
                    return false;
            } else {
                Piece r = board.getPiece(loc);
                if (!(r instanceof Rook) || !r.isOnMyTeam(piece))
                    return false;
            }
        }
        return true;
    }

    private boolean checkEnPassantCapture(Location capturingLoc, Board board) {
        Location enPassantTargetLoc = board.getEnPassantTargetLoc();
        if (enPassantTargetLoc != null && enPassantTargetLoc.equals(capturingLoc)) {
            Location enPassantActualLoc = board.getEnPassantActualLoc();
            Piece epsnPiece = board.getPiece(enPassantActualLoc);
            return epsnPiece != null && !epsnPiece.isOnMyTeam(piece);
        }
        return false;
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
            pseudoLegalMoves = createPseudoLegalMoves(board);
        }
        return pseudoLegalMoves;
    }

    private void setPseudoMoves() {
        pseudoMoves = piece.generatePseudoMoves();
    }

    private void setPseudoLegalMoves(Board board) {
        setPseudoMoves();
        pseudoLegalMoves = createPseudoLegalMoves(board);
    }

    public void verifyMoves(Board board) {
        long hash = board.getBoardHash().getPiecesHash();
        if (currentBoardHash != hash) {
            pseudoMoves = null;
            pseudoLegalMoves = null;
            legalMoves = null;
            setPseudoMoves();
        }
    }
}
