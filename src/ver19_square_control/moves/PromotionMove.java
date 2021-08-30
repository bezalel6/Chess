package ver19_square_control.moves;

public class PromotionMove extends Move {
    private int promotingTo;

    public PromotionMove(int promotingTo, Move move) {
        super(move);
        this.promotingTo = promotingTo;
    }

    public PromotionMove(PromotionMove move) {
        super(move);
        this.promotingTo = move.promotingTo;
    }

    public String getAnnotation() {
        return super.getAnnotation() + "=" + promotingTo;
    }

    public int getPromotingTo() {
        return promotingTo;
    }

    public void setPromotingTo(int promotingTo) {
        this.promotingTo = promotingTo;
    }

    void copyConstructor(Move move) {
        super.copyConstructor(move);
    }

}
