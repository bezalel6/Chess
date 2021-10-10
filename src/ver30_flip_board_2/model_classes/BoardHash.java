package ver30_flip_board_2.model_classes;

public class BoardHash {
    private long piecesHash, enPassantHash, castlingAbilityHash, currentPlayerHash, fullHash;

    public BoardHash(Board board) {
        setAll(board);
    }

    public BoardHash(BoardHash boardHash) {
        this.piecesHash = boardHash.piecesHash;
        this.enPassantHash = boardHash.enPassantHash;
        this.castlingAbilityHash = boardHash.castlingAbilityHash;
        this.currentPlayerHash = boardHash.currentPlayerHash;
        this.fullHash = boardHash.fullHash;
    }


    //    public BoardHash(BoardHash other,Board newBoard){
//        this.board = newBoard;
//        this.piecesLocsHash = other.piecesLocsHash;
//        this.enPassantHash =
//    }
    public void setAll(Board board) {
        setCastlingAbilityHash(board);
        setPiecesLocsHash(board);
        setCurrentPlayerHash(board);
        setEnPassantHash(board);
    }

    public void setPiecesLocsHash(Board board) {
        piecesHash = Zobrist.piecesHash(board);
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

    public void setEnPassantHash(Board board) {
        enPassantHash = Zobrist.enPassantHash(board);
        setFullHash();
    }

    public long getCastlingAbilityHash() {
        return castlingAbilityHash;
    }

    public void setCastlingAbilityHash(Board board) {
        castlingAbilityHash = Zobrist.castlingAbilityHash(board);
        setFullHash();
    }

    public long getCurrentPlayerHash() {
        return currentPlayerHash;
    }

    public void setCurrentPlayerHash(Board board) {
        currentPlayerHash = Zobrist.playerHash(board);
        setFullHash();
    }

    public long getFullHash() {
        return fullHash;
    }


}
