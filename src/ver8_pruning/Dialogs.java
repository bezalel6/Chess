package ver8_pruning;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

enum DialogTypes {HORIZONTAL_LIST, VERTICAL_LIST}

class DialogObject {
    private String text;
    private ImageIcon icon;
    private int key;

    public DialogObject(String text, ImageIcon icon, int key) {
        this.text = text;
        this.icon = icon;
        this.key = key;
    }

    public DialogObject(ImageIcon icon, int key) {
        text = "";
        this.icon = icon;
        this.key = key;
    }

    public DialogObject(String text, int key) {
        icon = null;
        this.text = text;
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
}

public class Dialogs extends JDialog implements ActionListener {
    private int result;
    private DialogTypes type;
    private ArrayList<DialogObject> objects;

    public Dialogs(Frame parent, DialogTypes type) {
        super(parent, "", true);
        this.type = type;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        result = ((JButton) e.getSource()).getActionCommand().charAt(0) - '0';
        dispose();
    }

    public int run() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout());
        for (int i = 0; i < objects.size(); i++) {
            DialogObject obj = objects.get(i);
            JButton btn = new JButton(obj.getText());
            btn.setIcon(obj.getIcon());
            btn.setActionCommand(obj.getKey() + "");
            btn.addActionListener(this);
            panel.add(btn);
        }
        getContentPane().add(panel);
        pack();
        this.setVisible(true);
        return result;
    }
}
