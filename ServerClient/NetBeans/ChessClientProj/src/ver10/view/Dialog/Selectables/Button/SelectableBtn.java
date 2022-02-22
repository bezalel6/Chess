package ver10.view.Dialog.Selectables.Button;

import ver10.view.Dialog.Selectables.Selectable;

import java.awt.*;

public interface SelectableBtn {
    Selectable getValue();

    void setSelected(boolean e);

    default Component comp() {
        return (Component) this;
    }
}
