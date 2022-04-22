package ver14.SharedClasses.Game.Evaluation;

public enum EvaluationParameters {
    MATERIAL(1),
    PIECE_TABLES(1),
    KING_SAFETY(.1),
    HANGING_PIECES,
    SQUARE_CONTROL,
    MOVEMENT_ABILITY,
    FORCE_KING_TO_CORNER,
    EG_WEIGHT,
    STOCKFISH_SAYS;
    public double weight;

    EvaluationParameters() {
        this(1);
    }

    EvaluationParameters(double weight) {
        this.weight = weight;
    }
}