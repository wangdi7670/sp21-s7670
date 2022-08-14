package capers;

/**
 * @author: Wingd
 * @date: 2022/8/14 8:21
 */
public class UnionFind {

    private int[] parent;

    private int[] size;

    public UnionFind(int n) {
        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = -1;
        }

        size = new int[n];
        for (int i = 0; i < n; i++) {
            size[i] = 1;
        }
    }

    /*
    Throws an exception if v1 is not a valid index.
     */
    public void validate(int v1) {
        if (v1 >= parent.length || v1 < 0) {
            throw new IllegalArgumentException();
        }
    }


    /* Returns the size of the set v1 belongs to. */
    public int sizeOf(int v1) {
        validate(v1);
        int parent = find(v1);
        return parent < 0 ? size[v1] : size[parent];
    }

    /*
    Returns the parent of v1. If v1 is the root of a tree, returns the negative size of the tree for which v1 is the root.
     */
    public int parent(int v1) {
        validate(v1);
        return parent[v1] == -1 ? -(size[v1]) : parent[v1];
    }

    /* Returns true if nodes v1 and v2 are connected. */
    public boolean connected(int v1, int v2) {
        validate(v1);
        validate(v2);

        int root1 = find(v1);
        int root2 = find(v2);

        return root1 == root2;
    }

    public void  union(int v1, int v2) {
        validate(v1);
        validate(v2);

        int size1 = sizeOf(v1);
        int size2 = sizeOf(v2);

        int root1 = find(v1);
        int root2 = find(v2);

        if (size1 <= size2) {
            if (root1 < 0 && root2 < 0) {
                parent[v1] = v2;
                size[v2] += size1;
            }
            else if (root2 >= 0 && root1 >= 0) {
                parent[root1] = root2;
                size[root2] += size1;
            }
            else if (root1 < 0) {
                parent[v1] = root2;
                size[root2] += size1;
            }
            else if (root2 < 0) {
                parent[root1] = v2;
                size[v2] += size1;
            }
        } else {
            if (root1 < 0 && root2 < 0) {
                parent[v2] = v1;
                size[v1] += size2;
            }
            else if (root2 >= 0 && root1 >= 0) {
                parent[root2] = root1;
                size[root1] += size2;
            }
            else if (root1 < 0) {
                parent[v2] = root1;
                size[root1] += size2;
            }
            else if (root2 < 0) {
                parent[root2] = v1;
                size[v1] += size2;
            }
        }
    }

    /*
    Returns the root of the set v1 belongs to. Path-compression is employed allowing for fast search-time.
     */
    public int find(int v1) {
        validate(v1);

        int temp = v1;
        while (parent[temp] >= 0) {
            temp = parent[temp];
        }

        return temp;
    }
}
