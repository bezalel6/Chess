package ver14.SharedClasses.Callbacks;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * represents a supplier for a hash that will later be fully calculated.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public interface LazyHashSupplier<T> extends Supplier<T>, Serializable {
    long serialVersionUID = 1000L;
}
