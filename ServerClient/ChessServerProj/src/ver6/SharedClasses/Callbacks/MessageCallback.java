package ver6.SharedClasses.Callbacks;

import ver6.SharedClasses.messages.Message;
import ver6.SharedClasses.networking.MyErrors;

public interface MessageCallback {
    void onRes(Message message);

    default void onErr(Exception e) throws MyErrors {
        throw MyErrors.parse(e);
    }
}
