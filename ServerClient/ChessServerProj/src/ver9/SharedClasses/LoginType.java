package ver9.SharedClasses;

import java.util.Arrays;

public enum LoginType {
    LOGIN, REGISTER, GUEST, CANCEL;

    //todo ↓ wtf
    public static final Object[] ALL_WO_CANCEL;

    static {
        ALL_WO_CANCEL = Arrays.stream(values()).filter(type -> type != CANCEL).toArray();
    }

    public boolean asUser() {
        return this == LOGIN || this == REGISTER;
    }

    @Override
    public String toString() {
        return name();
    }
}
