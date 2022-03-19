package ver14.SharedClasses.Threads.ErrorHandling;

import java.util.HashMap;
import java.util.Map;

public final class ErrorManager {

    private static final Map<ErrorType, ErrorHandler> map = new HashMap<>();
    private static EnvManager envManager = null;

    private ErrorManager() {
    }

    public static void setEnvManager(EnvManager manager) {
        envManager = manager;
    }

    public static void setHandler(ErrorType type, ErrorHandler handler) {
        map.put(type, handler);
    }

    public static void handle(MyError error) {
        if (map.containsKey(error.type)) {
            envManager.handledErr(error);
            map.get(error.type).handle(error);
        } else {
            envManager.criticalErr(error);
        }
    }

}
