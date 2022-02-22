package ver36_no_more_location.model_classes.moves;

import ver36_no_more_location.Location;
import ver36_no_more_location.Player;
import ver36_no_more_location.model_classes.Board;
import ver36_no_more_location.model_classes.CastlingAbility;
import ver36_no_more_location.model_classes.pieces.Piece;
import ver36_no_more_location.model_classes.pieces.PieceType;

import java.util.ArrayList;

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
        return createPseudoLegalMoves(board, pseudoMoves, piece.getPieceType(), piece.getPlayer());
    }

    public static ArrayList<ArrayList<Move>> createPseudoLegalMoves(Board board, ArrayList<ArrayList<Move>> pseudoMoves, PieceType pieceType, Player pieceColor) {
        ArrayList<ArrayList<Move>> ret = new ArrayList<>();
//        for (ArrayList<Move> list : pseudoMoves) {
//            ArrayList<Move> addingNow = new ArrayList<>();
//            boolean keepAdding = true;
//            for (Move move : list) {
//                boolean add = true;
//                move = Move.copyMove(move);
//                Location destination = move.getMovingTo();
//                Piece destPiece = board.getPiece(destination);
//                if (destPiece != null) {
//                    keepAdding = false;
//                    if (pieceType == PieceType.PAWN && !move.isCapturing()) {
//                        add = false;
//                    } else {
//                        add = !destPiece.isOnMyTeam(pieceColor);
//                        move.setCapturing(destPiece, board);
//                    }
//                }
//                if (add && move.isCapturing()) {
//                    if (destPiece == null) {
//                        add = false;
//                        if (checkEnPassantCapture(move.getMovingTo(), pieceColor, board)) {
//                            move.setCapturing(board.getPiece(board.getEnPassantActualLoc()), board);
//                            move.setMoveFlag(Move.MoveFlag.EnPassant);
//                            move.setIntermediateMove(new BasicMove(board.getEnPassantActualLoc(), board.getEnPassantTargetLoc()));
//                            add = true;
//                        }
//                    }
//                }
//                if (add && move instanceof Castling) {
//                    int side = ((Castling) move).getSide();
//                    int castlingAbility = board.getCastlingAbility();
//                    if (CastlingAbility.checkCastling(pieceColor, side, castlingAbility)) {
//                        add = checkCastlingLocs((Castling) move, pieceColor, board);
//                    } else add = false;
//                }
//                if (add)
//                    addingNow.add(move);
//                if (!keepAdding)
//                    break;
//            }
//            ret.add(addingNow);
//        }
        return ret;
    }
//
//    private static boolean checkEnPassantCapture(Location capturingLoc, Player pieceColor, Board board) {
//        Location enPassantTargetLoc = board.getEnPassantTargetLoc();
//        if (enPassantTargetLoc != null && enPassantTargetLoc.equals(capturingLoc)) {
//            Location enPassantActualLoc = board.getEnPassantActualLoc();
//            Piece epsnPiece = board.getPiece(enPassantActualLoc);
//            return epsnPiece != null && !epsnPiece.isOnMyTeam(pieceColor);
//        }
//        return false;
//    }
//
//    private static boolean checkCastlingLocs(Castling castling, Player pieceColor, Board board) {
//        Location[] list = castling.getCastlingLocs();
//        for (int i = 0; i < list.length; i++) {
//            Location loc = list[i];
//            if (Castling.ROOK_STARTING_LOC != i) {
//                if (!board.isSquareEmpty(loc))
//                    return false;
//            } else {
//                Piece r = board.getPiece(loc);
//                if (!(r.getPieceType() == PieceType.ROOK) || !r.isOnMyTeam(pieceColor))
//                    return false;
//            }
//        }
//        return true;
//    }
//
//    private boolean checkThreatenedCastling(Castling castling, Player pieceColor, Board board) {
//        for (Location loc : castling.getKingPath()) {
//            if (board.isThreatened(loc, pieceColor.getOpponent()))
//                return false;
//        }
//        return true;
//    }


}
