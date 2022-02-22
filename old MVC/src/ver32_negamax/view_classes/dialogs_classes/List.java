package ver32_negamax.view_classes.dialogs_classes;

import ver32_negamax.view_classes.dialogs_classes.dialog_objects.DialogObject;

import java.awt.*;
import java.util.ArrayList;

public class List extends Dialogs {
    public static final int VERTICAL = 0, HORIZONTAL = 1;
    private final int orientation;
    private final ArrayList<DialogObject> objects;

    public List(String title, int orientation) {
        super(title);
        this.orientation = orientation;
        this.objects = new ArrayList<>();
    }

    public void addItem(DialogObject object) {
        objects.add(object);
    }

    private LayoutManager createLayout() {
        int rows;
        int cols;
        if (orientation == VERTICAL) {
            rows = objects.size();
            cols = 1;
        } else {
            rows = 1;
            cols = objects.size();
        }
        return createGridLayout(rows, cols);
    }

    @Override
    public Object run() {
        bodyPnl.setLayout(createLayout());
        for (DialogObject object : objects)
            bodyPnl.add(object.getComponent());
        return runDialog();
    }

    @Override
    void setBottomPnl() {
        bottomPnl.add(createCancelButton());
    }
}
