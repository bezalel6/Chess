package ver14;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;


/**
 * Threads util.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class ThreadsUtil {
    /**
     * The constant NUM_OF_THREADS.
     */
    public static final int NUM_OF_THREADS = Runtime.getRuntime().availableProcessors();
//    public static final int

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        System.out.println(sampleCpuUsage() + "%");
    }

    /**
     * Sample cpu usage double.
     *
     * @return the double
     */
    public static double sampleCpuUsage() {
        try {
            JavaSysMon sysMon = new JavaSysMon();
            CpuTimes cpuTimes = sysMon.cpuTimes();
            Thread.sleep(500);
            return sysMon.cpuTimes().getCpuUsage(cpuTimes) * 100;
        } catch (Error | Exception e) {
            return -1;
        }

    }
}
