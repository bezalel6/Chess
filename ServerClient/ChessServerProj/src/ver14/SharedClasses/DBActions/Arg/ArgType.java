package ver14.SharedClasses.DBActions.Arg;


/*
 * ArgType -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */


/*
 * ArgType -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */


/*
 * ArgType
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

//askilan creating generic class instances
public enum ArgType {
    Date, DateRange, Text, ServerAddress, Number, Username(false), Password, Url, PictureUrl;
    final boolean isUserInput;

    ArgType() {
        this(true);
    }

    ArgType(boolean isUserInput) {
        this.isUserInput = isUserInput;
    }
}
