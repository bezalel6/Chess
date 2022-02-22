package ver19_square_control;

public class Error {
    public static void error(String e) {
        String reset = "\u001B[0m";
        String ANSI_RED = "\u001B[31m";
        String msg = ANSI_RED;
        int max = 20;
        msg += "ERROR: " + e + "\n";
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2, stackTraceLength = stackTrace.length; i < stackTraceLength; i++) {
            if (i == max) {
                msg += "skipped " + (stackTraceLength - max) + " calls";
                break;
            }
            StackTraceElement ste = stackTrace[i];
            msg += ste + "\n";
        }
        System.out.println(msg + reset);
        System.out.println();
    }
}
