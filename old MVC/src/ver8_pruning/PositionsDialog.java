package ver8_pruning;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PositionsDialog extends JDialog implements ActionListener {
    private JButton arr[];
    private int result;

    public PositionsDialog(Frame parent) {
        super(parent, "", true);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout());

        ArrayList<String[]> positions = Positions.getAllPositionsNames();
        arr = new JButton[positions.size()];
        for (int i = 0; i < arr.length; i++) {
            JButton btn = new JButton(positions.get(i)[0]);
            btn.setActionCommand(positions.get(i)[1]);
            arr[i] = btn;
        }

        for (JButton btn : arr) {
            btn.addActionListener(this);
            panel.add(btn);
        }

        getContentPane().add(panel);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        String res = source.getActionCommand();
        result = res.charAt(0) - '0';
        dispose();
    }

    public int run() {
        this.setVisible(true);
        return result;
    }

}
