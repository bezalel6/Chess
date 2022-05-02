package ver14.view;

import ver14.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;

public class MyTextArea extends JPanel {
    private final JTextArea textArea;
    private final JScrollPane scrollPane;

    public MyTextArea() {
        this("");
    }

    public MyTextArea(String text) {
        this.textArea = new JTextArea();
        this.scrollPane = new JScrollPane(textArea);
//        this.textArea
        initializeUI();
        setText(text);
        setEditable(false);
    }

    private void initializeUI() {
//        this.setLayout(new BorderLayout());
        this.add(scrollPane);
        scrollPane.setPreferredSize(new Size(300));
        setMinimumSize(new Size(400));
    }

    public void setText(String text) {
        if (textArea != null)
            textArea.setText(text);
    }

    public void setEditable(boolean e) {
        textArea.setEditable(e);
    }

    public void setWrap() {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }

    public void setHeight(int height) {
        this.setPreferredSize(new Size(getPreferredSize().width, height));
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
//        super.setPreferredSize(preferredSize);
        scrollPane.setPreferredSize(preferredSize);
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

//    @Override
//    public Dimension getPreferredSize() {
//        invalidate();
//        return new Size(Math.max(600, textArea.getPreferredSize().width), textArea.getPreferredSize().height);
//    }

    public void setFont(Font font) {
        if (textArea != null)
            textArea.setFont(font);
    }

    public JTextArea getTextArea() {
        return textArea;
    }


}