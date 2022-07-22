package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> alist = new AListNoResizing<>();

        BuggyAList<Integer> buggyAList = new BuggyAList<>();

        int[] arr = {4, 5, 6};
        for (int i = 0; i < arr.length; i++) {
            alist.addLast(arr[i]);
            buggyAList.addLast(arr[i]);
        }

        Integer r1 = alist.removeLast();
        int r2 = buggyAList.removeLast();

        assertEquals(true, r1 == r2);

        r1 = alist.removeLast();
        r2 = buggyAList.removeLast();
        assertEquals(true, r1 == r2);

        r1 = alist.removeLast();
        r2 = buggyAList.removeLast();
        assertEquals(true, r1 == r2);
    }

    @Test
    public void name() {
        Deque<Integer> deque = new ArrayDeque<>();
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();

        int N = 8000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggyAList.addLast(randVal);
                // System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int buggySize = buggyAList.size();
                // System.out.println("size: " + size);
                // System.out.println("buggySize: " + buggySize);
                assertEquals(true, size == buggySize);
            } else if (operationNumber == 2) {
                if (L.size() > 0) {
                    // getLast
                    Integer last = L.getLast();
                    Integer buggyLast = buggyAList.getLast();
                    // System.out.println("last = " + last);
                    // System.out.println("buggyLast = " + buggyLast);
                    assertEquals(true, last == buggyLast);
                }
            } else if (operationNumber == 3) {
                if (L.size() > 0) {
                    // removeLast
                    Integer integer = L.removeLast();
                    Integer buggyRemoved = buggyAList.removeLast();
                    // System.out.println("removeLast = " + integer);
                    // System.out.println("buggyRemoved = " + buggyRemoved);
                    assertEquals(true, buggyRemoved == integer);
                }
            }
        }
    }


}
