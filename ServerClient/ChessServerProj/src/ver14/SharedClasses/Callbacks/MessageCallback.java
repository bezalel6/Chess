package ver14.SharedClasses.Callbacks;

import ver14.SharedClasses.Networking.Messages.Message;


public interface MessageCallback {
    void onMsg(Message message);
}
