//package ver7.SharedClasses;
//
//import ver7.SharedClasses.Callbacks.ThrowingCallback;
//import ver7.SharedClasses.networking.MyErrors;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class ThrowingThread {
//
//    private ThrowingThread() {
//    }
//
//    public static void main(String[] args) {
//        ErrorHandler handler = ErrorHandler.create();
////        ThrowingThread.start(() -> {
////            System.out.println("before throw");
////
////
////        }, handler);
//
//        try {
//            while (true)
//                handler.verify();
//        } catch (MyErrors e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public static ErrorHandler start(ThrowingCallback callable) {
//        return start(callable, ErrorHandler.create());
//    }
//
//    public static ErrorHandler start(ThrowingCallback callable, ErrorHandler errorHandler) {
//        Thread.UncaughtExceptionHandler handler = (t, e) -> {
//            t.interrupt();
////            Errors thrown = ((Errors) e.getCause());
////            errorHandler.err(thrown);
//        };
//
//        Thread thread = new Thread(() -> {
//            try {
//                callable.callback(null);
//            } catch (Exception e) {
//                errorHandler.err(MyErrors.parse(e));
//                throw new RuntimeException();
////                Errors.parse(e).throwRuntime();
//            }
//        }) {
//            @Override
//            public void interrupt() {
//                super.interrupt();
//                System.out.println("interrupt");
//            }
//        };
//        thread.setUncaughtExceptionHandler(handler);
//        thread.start();
//        return errorHandler;
//    }
//
//    public interface ErrorHandler {
//        IDsGenerator errorHandlerIds = new IDsGenerator();
//        Map<String, MyErrors> errors = new HashMap<>();
//
//        static ErrorHandler create() {
//            String id = errorHandlerIds.generate();
//            return new ErrorHandler() {
//                @Override
//                public void err(MyErrors err) {
//                    System.out.println("thrown " + err);
//                    errors.put(id, err);
//                }
//
//                @Override
//                public String id() {
//                    return id;
//                }
//            };
//        }
//
//        void err(MyErrors err);
//
//        default void verify() throws MyErrors {
//            if (errors.containsKey(id())) {
//                throw errors.get(id());
//            }
//        }
//
//        String id();
//
//    }
//}