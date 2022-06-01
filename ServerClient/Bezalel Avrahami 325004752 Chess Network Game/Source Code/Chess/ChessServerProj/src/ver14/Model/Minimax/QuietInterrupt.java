package ver14.Model.Minimax;

/**
 * Quiet interrupt - an interrupt meant to stop the search quietly. without throwing anything outside the minimax. the returned move will be the best move found until interrupted.
 * NOTE: the move returned might be null as the search could've found no move yet.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
class QuietInterrupt extends Throwable {

}
