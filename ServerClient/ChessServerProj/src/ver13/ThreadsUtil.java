package ver13;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;

public class ThreadsUtil {
    public static final int NUM_OF_THREADS = Runtime.getRuntime().availableProcessors();


    public static void main(String[] args) {
        System.out.println(sampleCpuUsage() + "%");
    }

    public static double sampleCpuUsage() {
        JavaSysMon sysMon = new JavaSysMon();
        CpuTimes cpuTimes = sysMon.cpuTimes();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sysMon.cpuTimes().getCpuUsage(cpuTimes) * 100;
    }
}
