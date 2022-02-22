package ver28_minimax_levels.model_classes;

public class BoardHash {
    private Board board;
    private long piecesHash, enPassantHash, castlingAbilityHash, currentPlayerHash, fullHash;

    public BoardHash(Board board) {
        this.board = board;
        setAll();
    }

    public BoardHash(BoardHash boardHash) {

        this.board = boardHash.board;
        this.piecesHash = boardHash.piecesHash;
        this.enPassantHash = boardHash.enPassantHash;
        this.castlingAbilityHash = boardHash.castlingAbilityHash;
        this.currentPlayerHash = boardHash.currentPlayerHash;
        this.fullHash = boardHash.fullHash;
    }

    public BoardHash(BoardHash boardHash, Board board) {
        this(boardHash);
        this.board = board;
    }

    //    public BoardHash(BoardHash other,Board newBoard){
//        this.board = newBoard;
//        this.piecesLocsHash = other.piecesLocsHash;
//        this.enPassantHash =
//    }
    public void setAll() {
        setCastlingAbilityHash();
        setPiecesLocsHash();
        setCurrentPlayerHash();
        setEnPassantHash();
    }

    public void setPiecesLocsHash() {
        piecesHash = Zobrist.piecesHash(board);
        setFullHash();
    }

    public void setEnPassantHash() {
        enPassantHash = Zobrist.enPassantHash(board);
        setFullHash();
    }

    public void setCastlingAbilityHash() {
        castlingAbilityHash = Zobrist.castlingAbilityHash(board);
        setFullHash();
    }

    public void setCurrentPlayerHash() {
        currentPlayerHash = Zobrist.player2MoveHash(board);
        setFullHash();
    }

    public void setFullHash() {
        fullHash = Zobrist.combineHashes(new long[]{piecesHash, enPassantHash, castlingAbilityHash, currentPlayerHash});
    }

    public long getPiecesHash() {
        return piecesHash;
    }

    public long getEnPassantHash() {
        return enPassantHash;
    }

    public long getCastlingAbilityHash() {
        return castlingAbilityHash;
    }

    public long getCurrentPlayerHash() {
        return currentPlayerHash;
    }

    public long getFullHash() {
        return fullHash;
    }


}
