package ver5.SharedClasses.messages;

public enum SyncedListType {
    RESUMABLE(MessageType.RESUMABLE_GAMES),
    JOINABLE(MessageType.JOINABLE_GAMES);
    public final MessageType messageType;

    SyncedListType(MessageType messageType) {
        this.messageType = messageType;
    }
}
