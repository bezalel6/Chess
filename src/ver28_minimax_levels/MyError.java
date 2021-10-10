package ver28_minimax_levels;

public class MyError {
    public static void error(String e) {
        String reset = "\u001B[0m";
        String ANSI_RED = "\u001B[31m";
        StringBuilder msg = new StringBuilder(ANSI_RED);
        int max = 20;
        msg.append("ERROR: ").append(e).append("\n");
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2, stackTraceLength = stackTrace.length; i < stackTraceLength; i++) {
            if (i == max) {
                msg.append("skipped ").append(stackTraceLength - max).append(" calls");
                break;
            }
            StackTraceElement ste = stackTrace[i];
            msg.append(ste).append("\n");
        }
        System.out.println(msg + reset);
        System.out.println();
    }
}
