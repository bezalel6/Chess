package ver7;

public class CreateClients {
    public static void main(String[] args) {
        createClients(2);
    }

    public static void createClients(int numOfClients) {
        for (int i = 0; i < numOfClients; i++) {
            new Thread(() -> {
                Client c = new Client();
                c.runClient();
            }).start();
        }
    }
}
