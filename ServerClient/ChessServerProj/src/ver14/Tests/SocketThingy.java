package ver14.Tests;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

/*
 * SocketThingy
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * SocketThingy -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * SocketThingy -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class SocketThingy {
    private static final ExecutorService pool = Executors.newFixedThreadPool(2);
    private final static AtomicBoolean threw = new AtomicBoolean(false);
    private static ServerSocket serverSocket;

    public static void testSocket() throws Exception {
        serverSocket = new ServerSocket(1234);
        new Thread(SocketThingy::startAccepting).start();
        for (int i = 1; i <= 10 && !threw.get(); i++) {
            createClientInNewThread("127.0.0.1", 1234, "Id = " + i + "");
//            Thread.sleep(6);
        }
    }

    private static void startAccepting() {
        int num = 0;
        while (true) {
            try {
                String[] ids = new String[2];
                IntStream.range(0, 2).forEach(i -> {
                    try {
                        ids[i] = acceptSocket();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                num++;
                System.out.println(Arrays.toString(ids));
                if (!Objects.equals(ids[0], ids[1])) {
                    throw new Exception("mismatch after " + num + " double clients");
                }
                System.out.println("accepted " + num + " clients");
            } catch (Exception e) {
                e.printStackTrace();
                threw.set(true);
                break;
            }

        }
    }

    private static void createClientInNewThread(String url, int port, String id) {
        pool.execute(() -> {
            IntStream.range(0, 2).forEach(i -> {
                try {
                    Socket socket = new Socket(url, port);
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeUTF(id);
                    System.out.println("created client");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private static String acceptSocket() throws IOException {
        Socket socket = serverSocket.accept();
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        return inputStream.readUTF();
    }
}
