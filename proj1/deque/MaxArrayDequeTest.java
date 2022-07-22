package deque;

import org.junit.Test;

import java.util.Comparator;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author: Wingd
 * @date: 2022/7/22 20:18
 */
public class MaxArrayDequeTest {
    @Test
    public void test() {
        Comparator<Integer> c = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };

        MaxArrayDeque<Integer> maxArrayDeque = new MaxArrayDeque<>(c);

        maxArrayDeque.addLast(100);
        for (int i = 0; i < 100; i++) {
            Random r = new Random();
            int j = r.nextInt(100);
            maxArrayDeque.addLast(j);
        }


        assertEquals(100, (int) maxArrayDeque.max());
    }
}
