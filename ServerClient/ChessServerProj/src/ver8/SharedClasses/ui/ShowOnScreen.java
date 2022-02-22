package ver8.SharedClasses.ui;

import javax.swing.*;
import java.awt.*;

public class ShowOnScreen {
    private static int screenIndex = 0;

    public static void showOnScreen(JFrame frame) {
        screenIndex++;
        showOnScreen(screenIndex - 1, frame);
    }

    public static void showOnScreen(int screen, JFrame frame) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        if (screen >= gd.length || screen <= -1)
            screen = 0;
        Dimension ogD = gd[screen].getDefaultConfiguration().getBounds().getSize();

        int div = 1;
        int divHeight = div;
        int divWidth = div;
//        int div = numOfViewsInWin;

        Dimension d = new Dimension(ogD.width / divWidth, ogD.height / divHeight);
        frame.setSize(d);
        int x = gd[screen].getDefaultConfiguration().getBounds().getLocation().x;
        int y = gd[screen].getDefaultConfiguration().getBounds().getLocation().y;
//        x = x / numOfViewsInWin;
//        x *= currentViewIndex + 1;
//        y /= ((double) numOfViewsInWin / 2);
        frame.setLocation(x, y);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.setVisible(true);
    }
}
