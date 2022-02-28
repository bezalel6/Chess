package ver14.SharedClasses.Callbacks;

import ver14.SharedClasses.messages.Message;

public interface MessageCallback {
    void onMsg(Message message);

    default void onErr(String err) {

    }
}
