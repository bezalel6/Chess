package ver25_pieces_hashmap.view_classes.dialogs_classes.dialog_objects;


import javax.swing.*;
import java.awt.*;

public interface DialogObject {
    Font font = new Font(null, Font.BOLD, 30);

    String getText();

    ImageIcon getIcon();

    Object getKey();

    Component getComponent();
}
