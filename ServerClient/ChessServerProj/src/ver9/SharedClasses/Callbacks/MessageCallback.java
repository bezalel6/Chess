package ver9.SharedClasses.Callbacks;

import ver9.SharedClasses.messages.Message;

public interface MessageCallback {
    void onMsg(Message message);

    default void onErr(String err) {

    }
}
