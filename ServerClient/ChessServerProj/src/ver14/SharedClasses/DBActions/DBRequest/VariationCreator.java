package ver14.SharedClasses.DBActions.DBRequest;

import ver14.SharedClasses.DBActions.RequestBuilder;

/**
 * represents a creator of a variation.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface VariationCreator {
    /**
     * Create a variation.
     *
     * @param actualBuilder the builder of the original request
     * @return the variation
     */
    Variation create(RequestBuilder actualBuilder);
}
