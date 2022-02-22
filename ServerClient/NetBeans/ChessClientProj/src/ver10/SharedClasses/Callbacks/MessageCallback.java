package ver10.SharedClasses.Callbacks;

import ver10.SharedClasses.messages.Message;

public interface MessageCallback {
    void onMsg(Message message);

    default void onErr(String err) {

    }
}
