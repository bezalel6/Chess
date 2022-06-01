package ver14.view.Dialog.DialogFields;

import com.toedter.calendar.JCalendar;
import ver14.SharedClasses.UI.FontManager;
import ver14.view.Dialog.Components.Parent;
import ver14.view.Dialog.Dialogs.Header;
import ver14.view.IconManager.Size;

import java.awt.*;
import java.util.Date;

/**
 * a Date field.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class DateField extends DialogField<Date> {

    /**
     * The J calendar.
     */
    private final JCalendar jCalendar;
    /**
     * The Before.
     */
    private DateField before;
    /**
     * The After.
     */
    private DateField after;
    /**
     * The Err.
     */
    private String err;

    /**
     * Instantiates a new Date field.
     *
     * @param header the header
     * @param parent the parent
     */
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


    @Override
    protected Date getValue() {
        return getDate();
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

    /**
     * Gets the selected date.
     *
     * @return the date
     */
    public Date getDate() {

        Date date = jCalendar.getDate();

        return date;
    }

    @Override
    public void setValue(Date value) {
        jCalendar.setDate(value);
    }

    /**
     * Sets the selected date to be before the provided date field.
     *
     * @param before the before
     */
    public void setBefore(DateField before) {
        this.before = before;
    }

    /**
     * Sets the selected date to be after the provided date field.
     *
     * @param after the after
     */
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
