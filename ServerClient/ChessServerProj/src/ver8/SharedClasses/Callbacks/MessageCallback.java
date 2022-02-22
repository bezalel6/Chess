package ver8.SharedClasses.Callbacks;

import ver8.SharedClasses.messages.Message;
import ver8.SharedClasses.networking.MyErrors;

public interface MessageCallback {
    void onMsg(Message message);

    default void onErr(Exception e) throws MyErrors {
        throw MyErrors.parse(e);
    }
}
