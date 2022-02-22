package ver8.view.Wishfull.dialogs;

public interface VerifiedComponent {
    boolean verify();

    default String getError() {
        return "err";
    }
}
