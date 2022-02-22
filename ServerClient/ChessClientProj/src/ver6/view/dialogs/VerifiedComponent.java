package ver6.view.dialogs;

public interface VerifiedComponent {
    boolean verify();

    default String getError() {
        return "err";
    }
}
