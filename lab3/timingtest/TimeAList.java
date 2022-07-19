package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        System.out.println("开始：");

        Stopwatch sw = new Stopwatch();

        AList<Integer> aList = new AList<>();

        AList<Integer> Ns = new AList<>();
        init(Ns);

        AList<Double> times = new AList<>();

        double beginTime = sw.elapsedTime();

        // 开始计算时间
        int lastN = 0;
        for (int i = 0; i < Ns.size(); i++) {
            int N = Ns.get(i);
            for (int j = 0; j < N - lastN; j++) {
                aList.addLast(-1);
            }

            double t = sw.elapsedTime();
            times.addLast(t - beginTime);

            lastN = N;
        }

        printTimingTable(Ns, times, Ns);
    }


    private static void init(AList<Integer> Ns) {

        Ns.addLast(1000);
        Ns.addLast(2000);
        Ns.addLast(4000);
        Ns.addLast(8000);
        Ns.addLast(16000);
        Ns.addLast(32000);
        Ns.addLast(64000);
        Ns.addLast(128000);
    }
}
