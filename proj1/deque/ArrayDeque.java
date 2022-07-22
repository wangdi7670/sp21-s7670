package deque;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author: Wingd
 * @date: 2022/7/20 15:01
 */
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;

    private T[] array;

    // 如果往队列首添加，下一个待添加的位置
    private int nextFirst;

    // 如果往队列尾添加，下一个待添加的位置
    private int nextLast;


    public ArrayDeque() {
        array = (T[]) new Object[8];
        size = 0;
        nextFirst = 3;
        nextLast = 4;
    }


    /**
     * insert the specified element at the front of deque
     * @param item: the element to addd
     */
    public void addFirst(T item) {
        if (size() == array.length) {
            grow(array.length * 2);
        }

        array[nextFirst] = item;
        nextFirst = minusOne(nextFirst, array.length);
        size++;
    }


    /**
     * insert the specified element at the end of the deque
     * @param item : the element to add
     */
    public void addLast(T item) {
        if (size == array.length) {
            grow(array.length * 2);
        }

        array[nextLast] = item;
        nextLast = plusOne(nextLast, array.length);
        size++;
    }


    private void grow(int capacity) {
        T[] newArr = (T[]) new Object[capacity];

        transfer(newArr);

        nextFirst = minusOne(0, newArr.length);
        nextLast = plusOne(size - 1, newArr.length );
        array = newArr;
    }


    // 数据迁移
    private void transfer(T[] newArr) {
        int i = 0;
        for (T t : this) {
            newArr[i] = t;
            i++;
        }
    }


    private int plusOne(int index, int length) {
        return  (index + 1) % length;
    }


    private int minusOne(int index, int length) {
        return index == 0 ? length - 1 : index - 1;
    }


    public int size() {
        return size;
    }


    public void printDeque() {
        for (T t : this) {
            System.out.print(t + " ");
        }

        System.out.println();
    }


    /**
     * remove the first element of the deque
     * @return : the head of this deque, or null if the deque is empty
     */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (array.length >= 16 && (size - 1) < array.length / 4) {
            shrink((size - 1) * 2);
        }

        nextFirst = plusOne(nextFirst, array.length);
        T t = array[nextFirst];
        array[nextFirst] = null;
        size--;
        return t;
    }


    /**
     * remove the last element of this deque
     * @return : the tail of this deque, or null if this deque is empty
     */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (array.length >= 16 && (size - 1) < array.length / 4) {
            shrink((size - 1) * 2);
        }

        nextLast = minusOne(nextLast, array.length);
        T t = array[nextLast];
        array[nextLast] = null;
        size--;
        return t;
    }


    private void shrink(int capacity) {
        T[] newArr = (T[]) new Object[capacity];

        transfer(newArr);

        nextFirst = minusOne(0, newArr.length);
        nextLast = plusOne(size - 1, newArr.length);
        array = newArr;
    }


    /**
     * return the element at the specified position of this deque
     * @param index: index of the element to return
     * @return : the element at the specified position of this deque
     */
    public T get(int index) {
        if (index < 0 || index >= size()) {
            return null;
        }

        int i = (nextFirst + index + 1) % array.length;
        return array[i];
    }


    public Iterator<T> iterator() {
        return new DequeIterator();
    }


    private class DequeIterator implements Iterator<T> {
        int p = plusOne(nextFirst, array.length);

        int count = 0;

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public T next() {
            T t = array[p];
            p = plusOne(p, array.length);
            count++;
            return t;
        }
    }


    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }

        if (!(o instanceof Deque)) {
            return false;
        }

        return checkEqual((Deque<T>) o);
    }


    private boolean checkEqual(Deque<T> other) {
        if (other.size() != size) {
            return false;
        }

        boolean b = true;
        Iterator<T> iterator = iterator();
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(iterator.next(), other.get(i))) {
                b = false;
                break;
            }
        }

        return b;
    }
}
