package deque;

import java.util.Iterator;

/**
 * @author: Wingd
 * @date: 2022/7/21 0:10
 */
public interface Deque <T>{

    void addFirst(T item);

    void addLast(T item) ;

    int size();

    void printDeque();

    T removeFirst();

    T removeLast();

    T get(int index);

    default boolean isEmpty() {
        return size() == 0;
    }
}
