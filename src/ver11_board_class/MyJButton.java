package ver11_board_class;

import javax.swing.*;

public class MyJButton extends JButton {
    private Location btnLoc;

    public MyJButton(Location btnLoc) {
        this.btnLoc = btnLoc;
    }

    public Location getBtnLoc() {
        return btnLoc;
    }
}
