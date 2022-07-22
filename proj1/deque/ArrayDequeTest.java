package deque;


import org.junit.Test;

import java.util.Objects;
import java.util.Random;

import static org.junit.Assert.*;
/**
 * @author: Wingd
 * @date: 2022/7/21 18:56
 */
public class ArrayDequeTest {
    @Test
    public void test_addFirst() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        for (int i = 0; i < 20; i++) {
            arrayDeque.addFirst(i);
        }

        arrayDeque.printDeque();
    }

    @Test
    public void test_get() {

        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        arrayDeque.addLast(3);
        arrayDeque.addLast(4);
        arrayDeque.addLast(5);

        assertEquals(true, arrayDeque.get(0) == 3);
        assertEquals(true, arrayDeque.get(1) == 4);
        assertEquals(true, arrayDeque.get(2) == 5);
    }

    @Test
    public void test_addLast() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        for (int i = 0; i < 16; i++) {
            arrayDeque.addLast(i * i);
        }
        arrayDeque.printDeque();
    }

    @Test
    public void test_remove() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < 20; i++) {
            deque.addLast(i);
        }

        assertEquals(20, deque.size());

        for (int i = 0; i < 20; i++) {
            Integer first = deque.removeFirst();
            assertEquals(i, (int) first);
            assertEquals(deque.size(), 20 - i - 1);
        }
    }

    @Test
    public void test_equals() {
        LinkedListDeque<Integer> linkedListDeque = new LinkedListDeque<>();
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        for (int i = 0; i < 10; i++) {
            linkedListDeque.addLast(i);
            arrayDeque.addLast(i);
        }

        assertTrue(linkedListDeque.equals(arrayDeque));
        linkedListDeque.printDeque();
        arrayDeque.printDeque();
    }

    @Test
    public void test_random() {
        Random r = new Random();
        LinkedListDeque<Integer> linkedListDeque = new LinkedListDeque<>();
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        int N = 100000;
        for (int i = 0; i < N; i++) {
            int random = r.nextInt(5);
            if (random == 0) {
                int j = r.nextInt(100);
                arrayDeque.addLast(j);
                linkedListDeque.addLast(j);
            } else if (random == 1) {
                assertTrue(arrayDeque.size() == linkedListDeque.size());
            } else if (random == 2) {
                if (arrayDeque.size() > 0) {
                    Integer m = linkedListDeque.removeFirst();
                    Integer n = arrayDeque.removeFirst();
                    assertEquals(true, Objects.equals(m, n));
                }
            } else if (random == 3) {
                if (arrayDeque.size() > 0) {
                    Integer m = linkedListDeque.removeLast();
                    Integer n = arrayDeque.removeLast();
                    assertTrue(m.equals(n));
                }
            } else {
                int j = r.nextInt(100);
                arrayDeque.addFirst(j);
                linkedListDeque.addFirst(j);
            }
        }
    }
}
