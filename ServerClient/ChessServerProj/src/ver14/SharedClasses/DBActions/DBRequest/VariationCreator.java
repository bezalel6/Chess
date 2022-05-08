package ver14.SharedClasses.DBActions.DBRequest;

import ver14.SharedClasses.DBActions.RequestBuilder;

/**
 * Variation creator - represents a creator of a variation.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface VariationCreator {
    /**
     * Create variation.
     *
     * @param actualBuilder the actual builder
     * @return the variation
     */
    Variation create(RequestBuilder actualBuilder);
}
