package ver14.view;

import ver14.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;

public class TextWrapPnl extends JPanel {
    private final JTextArea textArea;

    public TextWrapPnl() {
        this("");
    }

    public TextWrapPnl(String text) {
        this.textArea = new JTextArea(5, 40);
        initializeUI();
        setText(text);
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(500, 200));

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void setText(String text) {
        if (textArea != null)
            textArea.setText(text);
    }

    public void setHeight(int height) {
        this.setPreferredSize(new Size(getPreferredSize().width, height));
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (textArea != null)
            textArea.setForeground(fg);
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (textArea != null)
            textArea.setBackground(bg);
    }

    public void setFont(Font font) {
        if (textArea != null)
            textArea.setFont(font);
    }

    public void setEditable(boolean e) {
        textArea.setEditable(e);
    }

}