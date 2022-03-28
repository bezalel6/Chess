package ver14.view.Dialog.Selectables.Button;

import ver14.view.Dialog.Selectables.Selectable;

import java.awt.*;

public interface SelectableBtn {
    Selectable getValue();

    void select(boolean e);

    default Component comp() {
        return (Component) this;
    }
}
