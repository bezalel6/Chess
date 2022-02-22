package ver8.view.OldDialogs;

public interface VerifiedComponent {
    boolean verify();

    default String getError() {
        return "err";
    }


}
