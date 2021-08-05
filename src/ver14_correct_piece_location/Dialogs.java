package ver14_correct_piece_location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

enum DialogTypes {HORIZONTAL_LIST, VERTICAL_LIST, MESSAGE}

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

    public DialogObject(String text, ImageIcon icon) {
        this.text = text;
        this.icon = icon;
    }

    public int getKey() {
        return key;
    }


    public String getText() {
        return text;
    }


    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
}

public class Dialogs extends JDialog implements ActionListener {
    Font font = new Font(null, 1, 30);
    private int result;
    private DialogTypes type;
    private ArrayList<DialogObject> objects;

    public Dialogs(DialogTypes type, String title) {
        super((Frame) null, title, true);
        this.type = type;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        result = ((JButton) e.getSource()).getActionCommand().charAt(0) - '0';
        dispose();
    }

    public int run(ArrayList<DialogObject> objects) {
        this.objects = objects;
        JPanel panel = new JPanel(new GridLayout(2, 1));
        if (type != DialogTypes.MESSAGE) {
            int cols, rows;
            if (type == DialogTypes.VERTICAL_LIST) {
                cols = 1;
                rows = objects.size();
            } else {
                cols = objects.size();
                rows = 1;
            }
            panel.setLayout(new GridLayout(rows, cols));
            for (int i = 0; i < objects.size(); i++) {
                DialogObject obj = objects.get(i);
                JButton btn = new JButton(obj.getText());
                btn.setFont(font);
                btn.setFocusable(false);
                btn.setIcon(obj.getIcon());
                btn.setActionCommand(obj.getKey() + "");
                btn.addActionListener(this);
                panel.add(btn);
            }
        } else {
            JPanel topPanel = new JPanel(), bottomPanel = new JPanel();
            JLabel iconLbl = new JLabel();
            iconLbl.setFont(font);
            iconLbl.setIcon(objects.get(0).getIcon());
            topPanel.add(iconLbl);
            JLabel msgLbl = new JLabel(objects.get(0).getText());
            msgLbl.setFont(font);
            topPanel.add(msgLbl);

            JButton okBtn = new JButton("OK");
            okBtn.addActionListener(this);
            okBtn.setFocusable(false);

            bottomPanel.add(okBtn);

            panel.add(topPanel, BorderLayout.NORTH);
            panel.add(bottomPanel, BorderLayout.SOUTH);
        }
        getContentPane().add(panel);
        pack();
        this.setVisible(true);
        return result;
    }
}
