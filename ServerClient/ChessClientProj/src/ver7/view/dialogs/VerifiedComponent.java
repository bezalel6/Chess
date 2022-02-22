package ver7.view.dialogs;

public interface VerifiedComponent {
    boolean verify();

    default String getError() {
        return "err";
    }
}
