package ver14.view.Board;

import org.intellij.lang.annotations.MagicConstant;

/**
 * represents the different button States a board button can have.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
@MagicConstant(flagsFromClass = State.class)
public @interface State {
    /**
     * The constant CHECK.
     */
    int CHECK = 1;
    /**
     * The constant CAPTURE.
     */
    int CAPTURE = 2;
    /**
     * The constant CAN_MOVE_TO.
     */
    int CAN_MOVE_TO = 4;
    /**
     * The constant CURRENT.
     */
    int CURRENT = 8;
    /**
     * The constant PROMOTING.
     */
    int PROMOTING = 16;
    /**
     * The constant MOVING_FROM.
     */
    int MOVING_FROM = 32;
    /**
     * The constant MOVING_TO.
     */
    int MOVING_TO = 64;
    /**
     * The constant HOVERED.
     */
    int HOVERED = 128;
    /**
     * The constant CLICKED_ONCE.
     */
    int CLICKED_ONCE = 256;
    /**
     * The constant DRAGGING.
     */
    int DRAGGING = 512;
    /**
     * The constant SELECTED.
     */
    int SELECTED = 1024;


}
