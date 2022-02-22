package ver7.SharedClasses.Callbacks;

import ver7.SharedClasses.messages.Message;
import ver7.SharedClasses.networking.MyErrors;

public interface MessageCallback {
    void onMsg(Message message);

    default void onErr(Exception e) throws MyErrors {
        throw MyErrors.parse(e);
    }
}
