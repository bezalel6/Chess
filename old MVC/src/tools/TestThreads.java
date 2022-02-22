package tools;

import ver35_thread_pool.Controller;
import ver35_thread_pool.model_classes.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TestThreads {
    public static void main(String[] args) throws InterruptedException {
        int checkUpTo = 20;
        ArrayList<String> results = new ArrayList<>();
        Controller controller = new Controller();
        Model model = controller.getModel();
        for (int i = 1; i <= checkUpTo; i++) {
            controller.startNewGame();
            model.setNumOfThreads(i);
            String moveString = model.getAiMove().toString();
            String str = "Used " + i + " Threads. Reached " + model.getPositionsReached() + " Positions. AI move: " + moveString;
            results.add(str);
//            JOptionPane.showMessageDialog(null, str);
        }
        results.sort(Comparator.comparingLong(o -> Long.parseLong(o.split(" ")[4])));
        Collections.reverse(results);
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        for (String st : results) {
            System.out.println(st);
        }
    }
}
