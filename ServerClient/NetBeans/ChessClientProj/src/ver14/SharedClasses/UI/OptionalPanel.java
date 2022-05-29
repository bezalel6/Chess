package ver14.SharedClasses.UI;

import ver14.SharedClasses.UI.Buttons.MyJButton;

import javax.swing.*;
import java.awt.*;

public class OptionalPanel extends JPanel {
    private boolean showing = false;
    private JPanel optionalPnl;

    public OptionalPanel(String title) {
        setLayout(new BorderLayout());
        optionalPnl = new JPanel(new GridLayout(0, 1));
        add(new JPanel() {{
            add(new MyJButton(title) {{
                addActionListener(l -> {
                    showing = !showing;
                    setShow(showing);
                });
            }});
        }}, BorderLayout.LINE_START);

        add(Box.createGlue(), BorderLayout.NORTH);
        add(optionalPnl, BorderLayout.CENTER);
        setShow(false);
    }


    public void setShow(boolean show) {
        optionalPnl.setVisible(show);
        comp(this);
    }

    private Component comp(Component comp) {
        if (comp == null) {
            return null;
        }
        if (comp instanceof JDialog dialog) {
            dialog.pack();
            return null;
        }
        return comp(comp.getParent());
    }

    public void addToOptional(JComponent comp) {
        optionalPnl.add(comp);
    }
}
