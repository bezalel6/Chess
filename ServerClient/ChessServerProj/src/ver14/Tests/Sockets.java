package ver14.Tests;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Sockets {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        new Thread(() -> {
//            while (true) {
            try {
                serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            }
        }).start();
        Socket s = new Socket("127.0.0.1", 1234);
        Thread t = new Thread(() -> {
            try {
                int res = s.getInputStream().read();
                System.out.println("res = " + res);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        t.start();

        new Thread(() -> {
//            synchronized (t) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            t.interrupt();
//            }
//            try {
//                s.getInputStream().close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }).start();
    }

}
