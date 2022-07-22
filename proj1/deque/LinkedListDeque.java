package deque;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author: Wingd
 * @date: 2022/7/20 0:13
 *
 * cs61b project1 实现双端队列
 */
public class LinkedListDeque <T> implements Deque<T>, Iterable<T>{
    private int size;

    // 头节点
    private Node sentinelFront;

    // 尾结点
    private Node sentinelBack;

    public LinkedListDeque() {
        this.sentinelFront = new Node(null);
        this.sentinelBack = new Node(null);

        sentinelFront.next = sentinelBack;
        sentinelBack.next = sentinelFront;

        sentinelBack.prev = sentinelFront;
        sentinelFront.prev = sentinelBack;

        size = 0;
    }

    /**
     *  在队列的首部添加一个元素
     * @param item: 待添加的
     */
    public void addFirst(T item) {
        Node node = new Node(item);
        Node temp = sentinelFront.next;

        sentinelFront.next = node;
        node.next = temp;

        temp.prev = node;
        node.prev = sentinelFront;

        size++;
    }

    /**
     *  在队列的尾部添加一个元素
     * @param item: 待添加的
     */
    public void addLast(T item) {
        Node newNode = new Node(item);
        Node temp = sentinelBack.prev;

        sentinelBack.prev = newNode;
        newNode.prev = temp;

        temp.next = newNode;
        newNode.next = sentinelBack;

        size++;
    }


    public int size() {
        return size;
    }

    public void printDeque() {
        if (isEmpty()) {
            System.out.println();
            return;
        }
        for (T t : this) {
            System.out.print(t + " ");
        }

        System.out.println();
    }

    /**
     * 删除队列中的第一个元素
     * @return : 删除的元素。如果队列数量是0，则返回null
     */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        Node deletedNode = sentinelFront.next;

        sentinelFront.next = sentinelFront.next.next;
        sentinelFront.next.prev = sentinelFront;

        size--;

        return deletedNode.obj;
    }

    /**
     * 删除队列中的最后一个元素
     * @return : 删除的元素
     */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        Node deletedNode = sentinelBack.prev;
        sentinelBack.prev = deletedNode.prev;
        sentinelBack.prev.next = sentinelBack;

        size--;

        return deletedNode.obj;
    }

    /**
     *  返回指定位置的元素
     * @param index: 索引
     * @return : 返回相应索引位置的元素
     */
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        Iterator<T> iterator = iterator();

        for (int i = 0; i < index; i++) {
            iterator.next();
        }

        return iterator.next();
    }

    /**
     * 返回迭代器
     * @return : 返回的是该deque的迭代器
     */
    public Iterator<T> iterator() {
        Iterator<T> iterator = new Iterator<>() {
            Node p = sentinelFront.next;
            int p_index = 0;

            @Override
            public boolean hasNext() {
                return p_index < size;
            }

            @Override
            public T next() {
                T o = p.obj;
                p = p.next;
                p_index++;
                return o;
            }
        };
        return iterator;
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

        return checkEquals((Deque<T>) o);
    }

    /**
     * helpful method of equals
     * @param other
     * @return
     */
    private boolean checkEquals(Deque<T> other) {
        if (size != other.size()) {
            return false;
        }

        Iterator<T> iterator = iterator();
        boolean ans = true;
        for (int i = 0; i < size; i++) {
            if (!(Objects.equals(iterator.next(), other.get(i)))) {
                ans = false;
                break;
            }
        }
        return ans;
    }

    /**
     * 返回索引为index的元素
     * @param index
     * @return
     */
    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        Node first = sentinelFront.next;

        return recursive(index, first);
    }

    public T recursive(int index, Node node) {
        if (index == 0) {
            return node.obj;
        }

        return recursive(index - 1, node.next);
    }

    private class Node{
        T obj;
        Node next;
        Node prev;

        Node(T _obj) {
            obj = _obj;
        }
    }
}
