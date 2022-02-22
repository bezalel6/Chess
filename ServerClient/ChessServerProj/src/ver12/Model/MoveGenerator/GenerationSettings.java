package ver12.Model.MoveGenerator;

import ver12.SharedClasses.pieces.PieceType;

public record GenerationSettings(boolean legalize, PieceType... generateFor) {

    public static final GenerationSettings defaultSettings = new GenerationSettings(true, PieceType.PIECE_TYPES);
}
