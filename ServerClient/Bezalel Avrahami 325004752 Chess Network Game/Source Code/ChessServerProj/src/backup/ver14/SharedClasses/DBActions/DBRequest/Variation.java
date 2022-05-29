package ver14.SharedClasses.DBActions.DBRequest;

import ver14.SharedClasses.DBActions.Arg.Arg;

/**
 * Variation - represents a variation of a premade request.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class Variation {
    /**
     * The Variation name.
     */
    public final String variationName;
    /**
     * The Building args.
     */
    public final Object[] buildingArgs;
    /**
     * The Variation args.
     */
    public final Arg[] variationArgs;

    /**
     * Instantiates a new Variation.
     *
     * @param variationName the variation name
     * @param buildingArgs  the arguments that will be used to build the original request
     * @param variationArgs the arguments required by the variation
     */
    Variation(String variationName, Object[] buildingArgs, Arg[] variationArgs) {
        this.variationName = variationName;
        this.buildingArgs = buildingArgs;
        this.variationArgs = variationArgs;
    }
}
