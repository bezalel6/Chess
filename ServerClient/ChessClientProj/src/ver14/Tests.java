package ver14;

import org.junit.Test;
import ver14.view.IconManager.IconManager;

import javax.swing.*;

public class Tests {


    public static void main(String[] args) {
        Client client = new Client();
        client.runClient();
//        client.getView().flipBoard();

    }

    @Test
    public void boardTest() {
        Client client = new Client();
        client.runClient();
//        client.getView().flipBoard();
    }


    @Test
    public void a() {
        JLabel lbl = new JLabel("hello", IconManager.errorIcon, JLabel.TRAILING);
        var f = new JFrame() {{
            setSize(500, 500);
            add(lbl);
            setVisible(true);
        }};
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                lbl.setIconTextGap(100);
                f.repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
