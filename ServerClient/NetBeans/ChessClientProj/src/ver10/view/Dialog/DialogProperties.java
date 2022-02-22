package ver10.view.Dialog;

public final class DialogProperties {
    public static final DialogProperties example = new DialogProperties("header", "title");

    private final String header;
    private final String err;
    private final String title;

    public DialogProperties(String header, String title) {
        this(header, null, title);
    }

    public DialogProperties(String header, String err, String title) {
        this.header = header;
        this.err = err;
        this.title = title;
    }


    public String header() {
        return header;
    }

    public String err() {
        return err;
    }

    public String title() {
        return title;
    }

    @Override
    public String toString() {
        return "DialogProperties[" +
                "msg=" + header + ", " +
                "err=" + err + ", " +
                "title=" + title + ']';
    }

}
