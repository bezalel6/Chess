package ver14.SharedClasses.Game.Evaluation;

public enum EvaluationParameters {
    MATERIAL(10),
    PIECE_TABLES(1),
    KING_SAFETY,
    HANGING_PIECES,
    SQUARE_CONTROL,
    MOVEMENT_ABILITY,
    FORCE_KING_TO_CORNER,
    EG_WEIGHT,
    STOCKFISH_SAYS;
    public final double weight;

    EvaluationParameters() {
        this(1);
    }

    EvaluationParameters(double weight) {
        this.weight = weight;
    }
}