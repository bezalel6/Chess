package ver14.SharedClasses.Utils;

import java.util.Arrays;

public class ArrUtils {
    /**
     * <a href="https://www.baeldung.com/java-concatenate-arrays#:~:text=static%20%3CT%3E%20T%5B%5D%20concatWithArrayCopy(T%5B%5D%20array1%2C%20T%5B%5D%20array2)%20%7B%0A%20%20%20%20T%5B%5D%20result%20%3D%20Arrays.copyOf(array1%2C%20array1.length%20%2B%20array2.length)%3B%0A%20%20%20%20System.arraycopy(array2%2C%200%2C%20result%2C%20array1.length%2C%20array2.length)%3B%0A%20%20%20%20return%20result%3B%0A%7D">credit</a>
     *
     * @param array1
     * @param array2
     * @param <T>
     * @return
     */
    public static <T> T[] concat(T[] array1, T... array2) {
        T[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    /**
     * @param arr
     * @param index
     * @param <T>
     * @return arr[index] if index inside arr. null otherwise
     */
    public static <T> T exists(T[] arr, int index) {
        return arr.length <= index ? null : arr[index];
    }
}
