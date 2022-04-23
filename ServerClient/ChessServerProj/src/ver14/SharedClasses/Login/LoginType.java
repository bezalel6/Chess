package ver14.SharedClasses.Login;

/*
 * LoginType -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

/*
 * LoginType -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * LoginType
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

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
