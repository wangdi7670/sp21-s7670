package timingtest;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {

        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = init();
        int lastN = 0;
        SLList<Integer> slList = new SLList<>();

        AList<Double> times = new AList<>();

        Stopwatch sw = new Stopwatch();

        AList<Integer> opCounts = new AList<>();

        for (int i = 0; i < Ns.size(); i++) {
            int N = Ns.get(i);
            for (int j = 0; j < N - lastN; j++) {
                slList.addLast(-1);
            }

            double begin = sw.elapsedTime();
            for (int k = 0; k < 10000; k++) {
                slList.getLast();
            }
            double end = sw.elapsedTime();
            times.addLast(end - begin);

            opCounts.addLast(10000);
        }


        printTimingTable(Ns, times, opCounts);
    }


    private static AList<Integer> init() {
        AList<Integer> Ns = new AList<>();

        Ns.addLast(1000);
        Ns.addLast(2000);
        Ns.addLast(4000);
        Ns.addLast(8000);
        Ns.addLast(16000);
        Ns.addLast(32000);
        Ns.addLast(64000);
        Ns.addLast(128000);

        return Ns;
    }

}
