package ver14.view.Dialog;

import ver14.SharedClasses.Utils.ArrUtils;

/**
 * represents a dynamic object with a few optional Dialog Details.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public record DialogDetails(String header, String title, String error) {

    /**
     * Instantiates a new Details.
     *
     * @param details []/[header]/[header,title]/[header,title,error]
     */
    public DialogDetails(String... details) {
        this(ArrUtils.exists(details, 0),
                ArrUtils.exists(details, 1),
                ArrUtils.exists(details, 2));
    }

}
