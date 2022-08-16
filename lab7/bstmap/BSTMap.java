package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * @author: Wingd
 * @date: 2022/8/14 10:20
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{

    public class Node {
        K key;
        V val;
        Node left, right;
        int size;

        Node(K key, V val, int size) {
            this.key = key;
            this.val = val;
            this.size = size;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key +
                    "} ";
        }
    }

    private Node root;

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(key, root);
    }

    private boolean containsKey(K key, Node root) {
        if (root == null) {
            return false;
        }

        if (key.compareTo(root.key) > 0) {
            return containsKey(key, root.right);
        } else if (key.compareTo(root.key) < 0) {
            return containsKey(key, root.left);
        } else {
            return true;
        }
    }

    @Override
    public V get(K key) {
        return get(key, root);
    }

    private V get(K key, Node root) {
        if (root == null) {
            return null;
        }

        if (key.compareTo(root.key) > 0) {
            return get(key, root.right);
        } else if (key.compareTo(root.key) < 0) {
            return get(key, root.left);
        } else {
            return root.val;
        }
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(Node node) {
        if (node == null) {
            return 0;
        }
        return node.size;
    }

    @Override
    public void put(K key, V value) {
        root = put(key, value, root);
    }

    private Node put(K key, V value, Node root) {
        if (root == null) {
            return new Node(key, value, 1);
        }

        if (key.compareTo(root.key) > 0) {
            root.right = put(key, value, root.right);
        } else if (key.compareTo(root.key) < 0) {
            root.left = put(key, value, root.left);
        } else {
            root.val = value;
        }

        root.size = size(root.left) + size(root.right) + 1;
        return root;
    }


    /* prints out your BSTMap in order of increasing Key. */
    public void printInOrder() {
        inOrder(root);
    }

    private void inOrder(Node node) {
        if (node == null) {
            return;
        }
        inOrder(node.left);

        System.out.println(node);

        inOrder(node.right);
    }

    /*
    For an extra challenge implement keySet() and iterator without using a second instance variable to store the set of keys
     */
    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        keySet(set, root);
        return set;
    }

    private void keySet(Set<K> set, Node root) {
        if (root == null) {
            return;
        }
        keySet(set, root.left);
        set.add(root.key);
        keySet(set, root.right);
    }

    private V removedVal;

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        removedVal = null;
        root = remove(key, root);
        return removedVal;
    }

    private Node remove(K key, Node node) {
        if (node == null) {
            return null;
        }

        int i = key.compareTo(node.key);
        if (i > 0) {
            node.right = remove(key, node.right);
        } else if (i < 0) {
            node.left = remove(key, node.left);
        } else {
            removedVal = node.val;
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node temp = node;
            node = min(node.right);
            node.right = deleteMin(temp.right);
            node.left = temp.left;
        }

        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    public Node min() {
        return min(root);
    }

    private Node min(Node node) {
        if (node.left == null) {
            return node;
        }

        return min(node.left);
    }

    public Node deleteMin() {
        return deleteMin(root);
    }

    private Node deleteMin(Node node) {
        if (node.left == null) {
            return node.right;
        }

        node.left = deleteMin(node.left);
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    @Override
    public V remove(K key, V value) {
        removedVal = null;
        root = remove(key, root, value);
        return removedVal;
    }

    private Node remove(K key, Node node, V desiredVal) {
        if (node == null) {
            return null;
        }

        int i = key.compareTo(node.key);
        if (i > 0) {
            node.right = remove(key, node.right, desiredVal);
        } else if (i < 0) {
            node.left = remove(key, node.left, desiredVal);
        } else {
            if (!Objects.equals(node.val, desiredVal)) {
                removedVal = null;
                return node;
            }

            removedVal = node.val;
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node temp = node;
            node = min(node.right);
            node.right = deleteMin(temp.right);
            node.left = temp.left;
        }

        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIterator();
    }

    private class BSTMapIterator implements Iterator<K> {
        Iterator<K> iterator;

        BSTMapIterator() {
            Set<K> ks = keySet();
            iterator = ks.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public K next() {
            return iterator.next();
        }
    }
}
