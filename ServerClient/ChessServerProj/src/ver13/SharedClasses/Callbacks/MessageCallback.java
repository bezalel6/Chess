package ver13.SharedClasses.Callbacks;

import ver13.SharedClasses.messages.Message;

public interface MessageCallback {
    void onMsg(Message message);

    default void onErr(String err) {

    }
}
