package ver14.SharedClasses.Utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;


/**
 * Array Utility Class.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ArrUtils {
    /**
     * `
     * concatenate two arrays
     * <a href="https://www.baeldung.com/java-concatenate-arrays#:~:text=static%20%3CT%3E%20T%5B%5D%20concatWithArrayCopy(T%5B%5D%20array1%2C%20T%5B%5D%20array2)%20%7B%0A%20%20%20%20T%5B%5D%20result%20%3D%20Arrays.copyOf(array1%2C%20array1.length%20%2B%20array2.length)%3B%0A%20%20%20%20System.arraycopy(array2%2C%200%2C%20result%2C%20array1.length%2C%20array2.length)%3B%0A%20%20%20%20return%20result%3B%0A%7D">credit</a>
     *
     * @param <T>    the type of the arrays
     * @param array1 the first array
     * @param array2 the second array
     * @return an array containing all the objects from both arrays
     */
    public static <T> T[] concat(T[] array1, T... array2) {
        T[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }


    /**
     * Create a list of objects in a given size, using a supplier to create each object.
     *
     * @param <T>        the type of the objects
     * @param objCreator the supplier
     * @param size       the size of the returned list
     * @return the list of the objects
     */
    public static <T> ArrayList<T> createList(Supplier<T> objCreator, int size) {
        ArrayList<T> lst = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            lst.add(objCreator.get());
        }
        return lst;
    }


    /**
     * if an item exists, it will be returned. if it doesn't null will be returned
     *
     * @param <T>   the type of the array
     * @param arr   the array
     * @param index the index
     * @return the item if it exists, null otherwise.
     */
    public static <T> T exists(T[] arr, int... index) {
        int _index = index.length == 0 ? 0 : index[0];

        return (arr == null || arr.length <= _index) ? null : arr[_index];
    }

}
