package ver14.SharedClasses.Callbacks;

import ver14.SharedClasses.Networking.Messages.Message;


/**
 * Message callback - a message callback.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface MessageCallback {
    /**
     * On msg.
     *
     * @param message the message
     */
    void onMsg(Message message);
}
