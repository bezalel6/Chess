package ver4.model_classes;

import java.io.Serializable;

public class BoardHash implements Serializable {
    private long piecesHash, enPassantHash, castlingAbilityHash, currentPlayerHash, fullHash;

    public BoardHash(Model model) {
        setAll(model);
    }

    public BoardHash(BoardHash boardHash) {
        this.piecesHash = boardHash.piecesHash;
        this.enPassantHash = boardHash.enPassantHash;
        this.castlingAbilityHash = boardHash.castlingAbilityHash;
        this.currentPlayerHash = boardHash.currentPlayerHash;
        this.fullHash = boardHash.fullHash;
    }

    public void setAll(Model model) {
        setCastlingAbilityHash(model);
        setPiecesLocsHash(model);
        setCurrentPlayerHash(model);
        setEnPassantHash(model);
        setFullHash();
    }

    private void setPiecesLocsHash(Model model) {
        piecesHash = Zobrist.piecesHash(model);
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

    private void setEnPassantHash(Model model) {
        enPassantHash = Zobrist.enPassantHash(model);
    }

    public long getCastlingAbilityHash() {
        return castlingAbilityHash;
    }

    public void setCastlingAbilityHash(Model model) {
        castlingAbilityHash = Zobrist.castlingAbilityHash(model);
    }

    public long getCurrentPlayerHash() {
        return currentPlayerHash;
    }

    public void setCurrentPlayerHash(Model model) {
        currentPlayerHash = Zobrist.playerHash(model);
    }


    public long getRepetitionHash() {
        return Zobrist.combineHashes(new long[]{castlingAbilityHash, piecesHash, enPassantHash});
    }

    public long getFullHash() {
        return fullHash;
    }


}