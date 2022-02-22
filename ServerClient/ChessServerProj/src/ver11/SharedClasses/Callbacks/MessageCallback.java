package ver11.SharedClasses.Callbacks;

import ver11.SharedClasses.messages.Message;

public interface MessageCallback {
    void onMsg(Message message);

    default void onErr(String err) {

    }
}
