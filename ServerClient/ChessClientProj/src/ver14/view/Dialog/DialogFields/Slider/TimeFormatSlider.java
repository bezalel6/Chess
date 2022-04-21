package ver14.view.Dialog.DialogFields.Slider;

import com.formdev.flatlaf.FlatLightLaf;
import ver14.SharedClasses.FontManager;
import ver14.SharedClasses.Game.GameSettings;
import ver14.SharedClasses.Game.GameSetup.TimeFormatSettable;
import ver14.SharedClasses.Game.TimeFormat;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.SharedClasses.ui.MyLbl;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.DialogFields.DialogField;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.IconManager.Size;

import javax.swing.*;

public class TimeFormatSlider extends DialogField<TimeFormat> {
    private final static int maxInSec = 60 * 15;
    private final static int minInSec = 1;
    protected final JSlider slider;
    private final MyLbl timeLbl;

    public TimeFormatSlider(Parent parent, TimeFormatSettable timeFormatSettable) {
        super(new Header("Choose Time Per Move"), parent);
        this.slider = new JSlider(minInSec, maxInSec);
        slider.setMajorTickSpacing(10);
        slider.setPreferredSize(new Size(400, 30));
//        slider.setMinorTickSpacing(60);
//        slider.setSnapToTicks(true);
        slider.setPaintTicks(true);
        this.timeLbl = new MyLbl();
        timeLbl.setFont(FontManager.Dialogs.dialogInput);
        slider.addChangeListener(l -> {
            timeLbl.setText(StrUtils.createTimeStr(slider.getValue() * 1000L));
            timeFormatSettable.setTimeFormat(getResult());
        });
        addInNewLine(timeLbl);
        addInNewLine(slider);
        setValue(new TimeFormat(10000));
    }

    public void setToMinValue() {
        setValue(new TimeFormat(minInSec * 1000));
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        new JFrame() {{
            setSize(500, 500);
            add(new TimeFormatSlider(null, new GameSettings()));
            setVisible(true);
        }};
    }

    public JSlider getSlider() {
        return slider;
    }

    @Override
    protected TimeFormat getValue() {
        return new TimeFormat(slider.getValue() * 1000L);
    }

    @Override
    protected boolean verifyField() {
        return true;
    }

    @Override
    public void setValue(TimeFormat value) {
        slider.setValue((int) (value.timeInMillis / 1000));
    }

    @Override
    public String errorDetails() {
        return null;
    }
}
