package ver11.SharedClasses.ui;//package ver11.SharedClasses.ui;
//
//import ver11.SharedClasses.FontManager;
//import ver11.SharedClasses.Callbacks._Callback;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.Arrays;
//import java.util.Objects;
//import java.util.Random;
//import java.util.concurrent.ConcurrentLinkedDeque;
//
//public class MyLbl extends JPanel {
//    private final static ConcurrentLinkedDeque<MyLbl> allLbls = new ConcurrentLinkedDeque<>();
//    private final static Color[] colors = {new Color(228, 2, 3), new Color(255, 140, 1), new Color(254, 237, 0), new Color(0, 128, 36), new Color(0, 77, 255), new Color(118, 7, 137)};
//    private static Timer started = null;
//
////    static {
////        toggle();
////    }
//
//    private final int horizontalAlignment;
//    private Icon icon;
//    private JLabel[] lbls;
//    private String fullText = null;
//    private int colorI = new Random().nextInt(colors.length);
//    private int lblI = 0;
//    private Color userSetFg;
//    private Color userSetBg;
//
//    public MyLbl(String text, Font font) {
//        this(text);
//        setFont(font);
//    }
//
//    public MyLbl(String text) {
//        this(text, null, JLabel.LEADING);
//    }
//
//    public MyLbl(String text, Icon icon, int horizontalAlignment) {
//        this.icon = icon;
//        this.horizontalAlignment = horizontalAlignment;
//        setLayout(new GridBagLayout());
//        setBackground(null);
////        setForeground(null);
//        setText(text);
//        allLbls.add(this);
//    }
//
//    private void forEachLbl(_Callback<JLabel> callback) {
//        if (lbls != null) {
//            Arrays.stream(lbls).parallel().forEach(callback::callback);
//        }
//    }
//
//    private void addAll() {
//        removeAll();
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.fill = GridBagConstraints.NONE;
//        gbc.insets = new Insets(0, 0, 0, 0);
//        for (int i = 0; i < lbls.length; i++) {
//            JLabel lbl = lbls[i];
//            if (i == 0) {
//                if (lbl == null) {
//                    lbls[i] = new JLabel();
//                    lbl = lbls[i];
//                }
//                lbl.setIcon(icon);
//                if (fullText.length() > 0)
//                    lbl.setText(fullText.charAt(0) + "");
//                lbl.setHorizontalAlignment(horizontalAlignment);
//            }
//            add(lbl, gbc);
//        }
//    }
//
//    public MyLbl(ImageIcon icon, Font font) {
//        this(icon);
//        setFont(font);
//    }
//
//    public MyLbl(Icon image) {
//        this("", image, JLabel.CENTER);
//
//    }
//
//    public MyLbl() {
//        this("", null, JLabel.LEADING);
//    }
//
//    public static void toggle() {
//        if (started != null) {
//            started.stop();
//            started = null;
//            forEachMyLbl(MyLbl::restoreUserSelected);
//            return;
//        }
//        new Thread(() -> {
//            started = new Timer(150, l -> {
//                forEachMyLbl(myLbl -> {
//                    JLabel[] jLabels = myLbl.lbls;
//                    for (int i = 0; i < jLabels.length; i++) {
//                        JLabel lbl = jLabels[myLbl.lblI];
//                        if (lbl != null && !lbl.getText().trim().equals("")) {
//                            lbl.setForeground(colors[myLbl.colorI]);
//                        }
//                        myLbl.colorI = myLbl.colorI == colors.length - 1 ? 0 : myLbl.colorI + 1;
//                        myLbl.lblI = myLbl.lblI == jLabels.length - 1 ? 0 : myLbl.lblI + 1;
//                    }
//                });
//            });
//
//            started.start();
//        }).start();
//
//    }
//
//    private static void forEachMyLbl(_Callback<MyLbl> callback) {
//        allLbls.stream().parallel().forEach(callback::callback);
//    }
//
//    private void restoreUserSelected() {
//        setForeground(userSetFg);
//        setBackground(userSetBg);
//    }
//
//    @Override
//    public void setForeground(Color fg) {
//        super.setForeground(fg);
//        userSetFg = fg;
//        forEachLbl(lbl -> lbl.setForeground(fg));
//    }
//
//    @Override
//    public void setBackground(Color bg) {
//        super.setBackground(bg);
//        userSetBg = bg;
//
//        forEachLbl(lbl -> lbl.setBackground(bg));
//    }
//
//    @Override
//    public void setFont(Font font) {
//        super.setFont(font);
//        if (lbls == null)
//            return;
//        for (int i = 0; i < lbls.length; i++) {
//            if (lbls[i] == null)
//                lbls[i] = new JLabel();
//            lbls[i].setFont(font);
//        }
//    }
//
//    public static void main(String[] args) {
//        new JFrame() {{
//            add(new MyLbl("a", FontManager.statusLbl));
//            pack();
//            setLocationRelativeTo(null);
//            setVisible(true);
//        }};
//    }
//
//    protected void setIcon(Icon icon) {
//        if (this.icon == icon) {
//            return;
//        }
//        this.icon = icon;
//        addAll();
//    }
//
//    public String getText() {
//        return fullText;
//    }
//
//    public void setText(String text) {
//        if (text == null) {
//            text = "";
//        }
////        if (Objects.equals(fullText, text)) {
////            return;
////        }
//        lbls = new JLabel[Math.max(1, text.length())];
//        if (text.length() == 0) {
//            lbls[0] = new JLabel();
//        } else {
//            for (int i = 0; i < text.length(); i++) {
//                lbls[i] = new JLabel(text.charAt(i) + "");
//                lbls[i].setFont(getFont());
//            }
//        }
//        fullText = text;
//        addAll();
//    }
//
//    public void setText(String text, JComponent fitInside) {
//        if (text == null) {
//            text = "";
//        }
//        if (Objects.equals(fullText, text)) {
//            return;
//        }
//        lbls = new JLabel[Math.max(1, text.length())];
//
//        if (text.length() == 0) {
//            lbls[0] = new JLabel();
//        } else {
//            int totalWidth = 0;
//            for (int i = 0; i < text.length(); i++) {
//                String str = text.charAt(i) + "";
//                if (totalWidth > fitInside.getWidth()) {
//                    str = "\n" + str;
//                }
//                lbls[i] = new JLabel(str);
//                lbls[i].setFont(getFont());
//                totalWidth += lbls[i].getWidth();
//            }
//        }
//        fullText = text;
//        addAll();
//    }
//}
//
