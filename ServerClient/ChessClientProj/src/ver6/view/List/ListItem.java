package ver6.view.List;

import java.awt.*;

public interface ListItem {

    default void onClick() {
    }


    Component comp();
}
