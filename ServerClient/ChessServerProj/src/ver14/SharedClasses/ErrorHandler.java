package ver14.SharedClasses;

public class ErrorHandler {
    private final static boolean logErrs = false, throwErrs = false, exitOnErr = false;

    private ErrorHandler() {
    }

    //todo: context

    public static void thrown(Throwable error) {
        thrown(error, false);
    }

    public static void thrown(Throwable error, boolean hardThrow) {
        if (logErrs)
            error.printStackTrace();
        if (throwErrs || hardThrow)
            throw new Error(error);
        if (exitOnErr)
            System.exit(69);
    }
}
