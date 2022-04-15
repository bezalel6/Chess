package ver14;

public class CreateClients {
    public static void main(String[] args) {
        createClients(2, args);
    }

    public static void createClients(int numOfClients, String[] args) {
        for (int i = 0; i < numOfClients; i++) {
            new Thread(() -> {
                Client.main(args);
            }).start();
        }
    }
}
