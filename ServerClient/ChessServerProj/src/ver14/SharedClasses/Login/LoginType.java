package ver14.SharedClasses.Login;

public enum LoginType {
    LOGIN, REGISTER, GUEST, CANCEL, NOT_SET_YET;

    public boolean asUser() {
        return this == LOGIN || this == REGISTER;
    }

    @Override
    public String toString() {
        return name();
    }
}
