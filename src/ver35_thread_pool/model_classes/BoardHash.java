package ver35_thread_pool.model_classes;

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

    public void setAll(Board board) {
        setCastlingAbilityHash(board);
        setPiecesLocsHash(board);
        setCurrentPlayerHash(board);
        setEnPassantHash(board);
        setFullHash();
    }

    private void setPiecesLocsHash(Board board) {
        piecesHash = Zobrist.piecesHash(board);
    }

    private void setFullHash() {
        fullHash = Zobrist.combineHashes(new long[]{piecesHash, enPassantHash, castlingAbilityHash, currentPlayerHash});
    }

    public long getPiecesHash() {
        return piecesHash;
    }

    public long getEnPassantHash() {
        return enPassantHash;
    }

    private void setEnPassantHash(Board board) {
        enPassantHash = Zobrist.enPassantHash(board);
    }

    public long getCastlingAbilityHash() {
        return castlingAbilityHash;
    }

    public void setCastlingAbilityHash(Board board) {
        castlingAbilityHash = Zobrist.castlingAbilityHash(board);
    }

    public long getCurrentPlayerHash() {
        return currentPlayerHash;
    }

    public void setCurrentPlayerHash(Board board) {
        currentPlayerHash = Zobrist.playerHash(board);
    }

    public long getFullHash() {
        return fullHash;
    }


}
