package ver12.SharedClasses.Callbacks;

import ver12.SharedClasses.messages.Message;

public interface MessageCallback {
    void onMsg(Message message);

    default void onErr(String err) {

    }
}
