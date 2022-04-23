package ver14.SharedClasses.Utils;

import ver14.SharedClasses.Callbacks.ObjCallback;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * ArrUtils
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * ArrUtils -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * ArrUtils -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

/**
 * The type Arr utils.
 */
public class ArrUtils {
    /**
     * <a href="https://www.baeldung.com/java-concatenate-arrays#:~:text=static%20%3CT%3E%20T%5B%5D%20concatWithArrayCopy(T%5B%5D%20array1%2C%20T%5B%5D%20array2)%20%7B%0A%20%20%20%20T%5B%5D%20result%20%3D%20Arrays.copyOf(array1%2C%20array1.length%20%2B%20array2.length)%3B%0A%20%20%20%20System.arraycopy(array2%2C%200%2C%20result%2C%20array1.length%2C%20array2.length)%3B%0A%20%20%20%20return%20result%3B%0A%7D">credit</a>
     *
     * @param <T>    the type parameter
     * @param array1 the array 1
     * @param array2 the array 2
     * @return t [ ]
     */
    public static <T> T[] concat(T[] array1, T... array2) {
        T[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }


    /**
     * Create list array list.
     *
     * @param <T>        the type parameter
     * @param objCreator the obj creator
     * @param size       the size
     * @return the array list
     */
    public static <T> ArrayList<T> createList(ObjCallback<T> objCreator, int size) {
        ArrayList<T> lst = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            lst.add(objCreator.get());
        }
        return lst;
    }

    
    public static <T> T exists(T[] arr, int... index) {
        int _index = index.length == 0 ? 0 : index[0];

        return (arr == null || arr.length <= _index) ? null : arr[_index];
    }

}
