package ver14.view.Dialog.DialogFields.Slider;

import ver14.SharedClasses.Game.GameSetup.TimeFormat;
import ver14.SharedClasses.Misc.ParentOf;
import ver14.SharedClasses.UI.FontManager;
import ver14.SharedClasses.UI.MyLbl;
import ver14.SharedClasses.Utils.StrUtils;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.DialogFields.DialogField;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.IconManager.Size;

import javax.swing.*;

/**
 * a Time format slider.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class TimeFormatSlider extends DialogField<TimeFormat> {
    /**
     * The constant maxInSec.
     */
    private final static int maxInSec = 60 * 15;
    /**
     * The constant minInSec.
     */
    private final static int minInSec = 1;
    /**
     * The Slider.
     */
    protected final JSlider slider;
    /**
     * The Time lbl.
     */
    private final MyLbl timeLbl;

    /**
     * Instantiates a new Time format slider.
     *
     * @param parent              the parent
     * @param timeFormatComponent the time format component
     */
    public TimeFormatSlider(Parent parent, ParentOf<TimeFormat> timeFormatComponent) {
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
            timeFormatComponent.set(getResult());
        });
        addInNewLine(timeLbl);
        addInNewLine(slider);
        setValue(new TimeFormat(5000));
    }


    /**
     * Sets to min value.
     */
    public void setToMinValue() {
        setValue(new TimeFormat(minInSec * 1000));
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
