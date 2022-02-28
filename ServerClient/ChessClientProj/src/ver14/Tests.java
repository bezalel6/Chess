package ver14;

public class Tests {
    final Object o = new Object();

    public static void main(String[] args) {
        Tests t = new Tests();
        new Thread(t::printNum1).start();
        new Thread(t::printNum2).start();
    }


    public void printNum1() {
        for (int i = 0; i < 50; i++) {
            System.out.println((i + 1) + ": T1");
        }
    }

    public void printNum2() {
        synchronized (this) {
            for (int i = 0; i < 50; i++) {
                System.out.println((i + 1) + ": T2");
            }
        }
    }


}
