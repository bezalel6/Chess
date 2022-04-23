package ver14;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;

/*
 * ThreadsUtil
 *
 * 23.4.2022, 2:02
 * author: Bezalel Avrahami
 */

/*
 * ThreadsUtil -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com)
 */

/*
 * ThreadsUtil -
 * ---------------------------------------------------------------
 * by Bezalel Avrahami(bezalel3250@gmail.com) 23/04/2022
 */

public class ThreadsUtil {
    public static final int NUM_OF_THREADS = Runtime.getRuntime().availableProcessors();
//    public static final int

    public static void main(String[] args) {
        System.out.println(sampleCpuUsage() + "%");
    }

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
