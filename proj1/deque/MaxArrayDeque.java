package deque;

import java.util.Comparator;
import java.util.Iterator;

/**
 * @author: Wingd
 * @date: 2022/7/22 20:03
 */
public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }


    /**
     * 根据 comparator, 返回最大值
     * @return
     */
    public T max() {
        if (isEmpty()) {
            return null;
        }

        Iterator<T> iterator = iterator();
        T max = iterator.next();

        while (iterator.hasNext()) {
            T current = iterator.next();
            if (comparator.compare(current, max) > 0) {
                max = current;
            }
        }

        return max;
    }

    public T max(Comparator<T> c) {
        comparator = c;
        return max();
    }
}
