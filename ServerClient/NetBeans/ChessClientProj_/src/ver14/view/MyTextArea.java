package ver14.view;

import ver14.view.IconManager.Size;

import javax.swing.*;
import java.awt.*;

/**
 * My implementation of a text area.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class MyTextArea extends JPanel {
    /**
     * The Text area.
     */
    private final JTextArea textArea;
    /**
     * The Scroll pane.
     */
    private final JScrollPane scrollPane;

    /**
     * Instantiates a new My text area.
     */
    public MyTextArea() {
        this("");
    }

    /**
     * Instantiates a new My text area.
     *
     * @param text the text
     */
    public MyTextArea(String text) {
        this.textArea = new JTextArea();
        this.scrollPane = new JScrollPane(textArea);
//        this.textArea
        initializeUI();
        setText(text);
        setEditable(false);
    }

    /**
     * Initialize ui.
     */
    private void initializeUI() {
//        this.setLayout(new BorderLayout());
        this.add(scrollPane);
        scrollPane.setPreferredSize(new Size(300));
        setMinimumSize(new Size(400));
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(String text) {
        if (textArea != null)
            textArea.setText(text);
    }

    /**
     * Sets editable.
     *
     * @param e the e
     */
    public void setEditable(boolean e) {
        textArea.setEditable(e);
    }

    /**
     * Sets wrap text.
     */
    public void setWrap() {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }

    /**
     * Sets height.
     *
     * @param height the height
     */
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

    /**
     * Gets text area.
     *
     * @return the text area
     */
    public JTextArea getTextArea() {
        return textArea;
    }


}