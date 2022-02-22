package ver10.view.Dialog.DialogFields;

import com.formdev.flatlaf.FlatLightLaf;
import com.toedter.calendar.JCalendar;
import ver10.SharedClasses.DBActions.Arg.Arg;
import ver10.SharedClasses.DBActions.Arg.ArgType;
import ver10.SharedClasses.DBActions.Arg.Config;
import ver10.SharedClasses.FontManager;
import ver10.view.Dialog.Components.Parent;
import ver10.view.Dialog.DialogProperties;
import ver10.view.Dialog.Dialogs.Header;
import ver10.view.Dialog.Dialogs.SimpleDialogs.CustomDialog;
import ver10.view.IconManager.Size;

import java.awt.*;
import java.util.Date;

public class DateField extends DialogField<Date> {
    static {
        FlatLightLaf.setup();
    }

    private final JCalendar jCalendar;
    private DateField before;
    private DateField after;
    private String err;

    public DateField(Header header, Parent parent) {
        super(header, parent);
        this.jCalendar = new JCalendar() {
            @Override
            public Dimension getPreferredSize() {
                return new Size(200, 200);
            }
        };
//        this.jCalendar.setMinSelectableDate(new Date(0));
//        this.jCalendar.setMaxSelectableDate(new Date());
        this.jCalendar.addPropertyChangeListener(evt -> {
            onUpdate();
        });
        addMainComp(this.jCalendar);

        setFont(FontManager.normal);

        jCalendar.setSize(new Size(200, 200));

//        jCalendar.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
//        setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (this.jCalendar != null)
            this.jCalendar.setFont(font);
    }

    public static void main(String[] args) {
        Arg a = new Arg(ArgType.Date, new Config<>("iuehwdifdhsdhjdoishj neoifcneoiemcoism kpekoms pom\nddwaawaddawdwad", new Date()));
        new CustomDialog(null, DialogProperties.example, a, a, a).start();
    }

    @Override
    protected boolean verifyField() {
        err = null;
        Date currentDate = getDate();
        if (currentDate == null) {
            err = "choose a date";
            return false;
        }
        if (before != null) {
            Date beforeDate = before.getDate();
            if (beforeDate != null && !currentDate.before(beforeDate)) {
                err = "choose a date before " + beforeDate;
            }
        }
        if (err == null && after != null) {
            Date afterDate = after.getDate();
            if (afterDate != null && !currentDate.after(afterDate)) {
                err = "choose a date after " + afterDate;
            }
        }
        return err == null;
    }

    @Override
    protected Date getValue() {
        return getDate();
    }

    @Override
    protected void setValue(Date value) {
        jCalendar.setDate(value);
    }

    public Date getDate() {

        Date date = jCalendar.getDate();

        return date;
    }

    public void setBefore(DateField before) {
        this.before = before;
    }

    public void setAfter(DateField after) {
        this.after = after;
    }

    @Override
    public String errorDetails() {
        return err;
    }


    @Override
    public String toString() {
        return "DateField{" +
                "date=" + getDate() +
                ", before=" + before +
                ", after=" + after +
                '}';
    }
}
