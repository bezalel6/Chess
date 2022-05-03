package ver14.view.Dialog.Components;

/**
 * Verified - represents a verified component that according to the situation, might not verify.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface Verified {
    /**
     * Verify check the current conditions, and try to verify.
     *
     * @return true if this component verified successfully, false otherwise.
     */
    boolean verify();

    /**
     * gets the error details of this component. will only be called after not verifying.
     *
     * @return the error details.
     */
    String errorDetails();
}
