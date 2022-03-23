package ver14.SharedClasses.Utils;

import ver14.SharedClasses.Callbacks.ObjCallback;

import java.util.ArrayList;
import java.util.Arrays;

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

//    totest

    /**
     * if
     * for example:
     * <br/><br/>
     * <code>
     * mat[][][]              exists(mat,1,2,3) will return mat[1][2][3] (if it exists ofc).              <br/>              mat[][]              exists(mat,1) => mat[1][0]              <br/>              mat[][][]              exists(mat,1) => mat[1][0][0]
     * </code>
     *
     * @param <T>     the type parameter
     * @param arr     the arr. can be arr of any dimension.
     * @param indices the indices corresponding to the matrix dimension. if the number of indices passed are shorter than the matrix dimension, index 0 will replace the missing indices. pass -1 at any dimension to prevent the recursive "unfolding" of a multidimensional array. notice that this will not prevent the first dimension from being "unfolded"
     * @return arr[index] if index inside arr. null otherwise
     */
    public static <T> T exists(T[] arr, int... indices) {
        int index = indices.length == 0 ? 0 : indices[0];
        T ret = arr.length <= index ? null : arr[index];
        if (ret != null && ret.getClass().isArray() && (indices.length >= 1 && indices[1] != -1) || indices.length < 1) {
            arr = ((T[]) ret);
            indices = indices.length > 0 ? Arrays.copyOfRange(indices, 1, indices.length) : new int[0];
            ret = exists(arr, indices);
        }
        return ret;
    }

}
