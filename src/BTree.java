class BTreeNode {
    Reg[] keys; // Array to store keys
    int t; // Minimum degree (defines the range for number of keys)
    BTreeNode[] children; // Array to store child pointers
    int n; // Current number of keys
    boolean leaf; // True when node is leaf, else False

    public BTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;

        keys = new Reg[2 * t - 1];
        children = new BTreeNode[2 * t];
        n = 0;
    }

    public BTreeNode search(int ID, BTree tree) {
        int i = 0;

        while (i < n && ID > keys[i].ID)
            i++;

        if (i < n && keys[i].ID == ID){
            if (tree.found == false){
                tree.found = true;
                tree.foundata = keys[i];
            }
            return this;
        }

        if (leaf)
            return null;

        return children[i].search(ID, tree);
    }

    public void insertNonFull(Reg reg) {
        int i = n - 1;

        if (leaf) {
            while (i >= 0 && reg.ID < keys[i].ID) {
                keys[i + 1] = keys[i];
                i--;
            }
            keys[i + 1] = reg;
            n++;
        } else {
            while (i >= 0 && reg.ID < keys[i].ID)
                i--;
            i++;

            if (children[i].n == 2 * t - 1) {
                splitChild(i, children[i]);
                if (reg.ID > keys[i].ID)
                    i++;
            }
            children[i].insertNonFull(reg);
        }
    }

    public void splitChild(int i, BTreeNode y) {
        BTreeNode z = new BTreeNode(y.t, y.leaf);
        z.n = t - 1;

        for (int j = 0; j < t - 1; j++)
            z.keys[j] = y.keys[j + t];

        if (!y.leaf) {
            for (int j = 0; j < t; j++)
                z.children[j] = y.children[j + t];
        }

        y.n = t - 1;

        for (int j = n; j >= i + 1; j--)
            children[j + 1] = children[j];

        children[i + 1] = z;

        for (int j = n - 1; j >= i; j--)
            keys[j + 1] = keys[j];

        keys[i] = y.keys[t - 1];
        n++;
    }

    public void remove(int ID, BTree tree) {
        int idx = findKey(ID);

        if (idx < n && keys[idx].ID == ID) {
            if (tree.found == false){
            tree.found = true;
            tree.foundata = keys[idx];
            }
            if (leaf)
                removeFromLeaf(idx);
            else
                removeFromNonLeaf(idx, tree);
        } else {
            if (leaf) {
                tree.found = false;
                tree.foundata = null;                
                return;
            }

            boolean flag = (idx == n);

            if (children[idx].n < t)
                fill(idx);

            if (flag && idx > n)
                children[idx - 1].remove(ID, tree);
            else
                children[idx].remove(ID, tree);
        }
    }

    private void removeFromLeaf(int idx) {
        for (int i = idx + 1; i < n; ++i)
            keys[i - 1] = keys[i];
        n--;
    }

    private void removeFromNonLeaf(int idx, BTree tree) {
        Reg k = keys[idx];

        if (children[idx].n >= t) {
            Reg pred = getPred(idx);
            keys[idx] = pred;
            children[idx].remove(pred.ID, tree);
        } else if (children[idx + 1].n >= t) {
            Reg succ = getSucc(idx);
            keys[idx] = succ;
            children[idx + 1].remove(succ.ID, tree);
        } else {
            merge(idx);
            children[idx].remove(k.ID, tree);
        }
    }

    private int findKey(int ID) {
        int idx = 0;
        while (idx < n && keys[idx].ID < ID)
            ++idx;
        return idx;
    }

    private void fill(int idx) {
        if (idx != 0 && children[idx - 1].n >= t)
            borrowFromPrev(idx);
        else if (idx != n && children[idx + 1].n >= t)
            borrowFromNext(idx);
        else {
            if (idx != n)
                merge(idx);
            else
                merge(idx - 1);
        }
    }

    private void borrowFromPrev(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx - 1];

        for (int i = child.n - 1; i >= 0; --i)
            child.keys[i + 1] = child.keys[i];

        if (!child.leaf) {
            for (int i = child.n; i >= 0; --i)
                child.children[i + 1] = child.children[i];
        }

        child.keys[0] = keys[idx - 1];

        if (!child.leaf)
            child.children[0] = sibling.children[sibling.n];

        keys[idx - 1] = sibling.keys[sibling.n - 1];

        child.n += 1;
        sibling.n -= 1;
    }

    private void borrowFromNext(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];

        child.keys[child.n] = keys[idx];

        if (!(child.leaf))
            child.children[child.n + 1] = sibling.children[0];

        keys[idx] = sibling.keys[0];

        for (int i = 1; i < sibling.n; ++i)
            sibling.keys[i - 1] = sibling.keys[i];

        if (!sibling.leaf) {
            for (int i = 1; i <= sibling.n; ++i)
                sibling.children[i - 1] = sibling.children[i];
        }

        child.n += 1;
        sibling.n -= 1;
    }

    private void merge(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];

        child.keys[t - 1] = keys[idx];

        for (int i = 0; i < sibling.n; ++i)
            child.keys[i + t] = sibling.keys[i];

        if (!child.leaf) {
            for (int i = 0; i <= sibling.n; ++i)
                child.children[i + t] = sibling.children[i];
        }

        for (int i = idx + 1; i < n; ++i)
            keys[i - 1] = keys[i];

        for (int i = idx + 2; i <= n; ++i)
            children[i - 1] = children[i];

        child.n += sibling.n + 1;
        n--;
    }

    private Reg getPred(int idx) {
        BTreeNode cur = children[idx];
        while (!cur.leaf)
            cur = cur.children[cur.n];

        return cur.keys[cur.n - 1];
    }

    private Reg getSucc(int idx) {
        BTreeNode cur = children[idx + 1];
        while (!cur.leaf)
            cur = cur.children[0];

        return cur.keys[0];
    }

    public void printInOrder() {
        int i;
        for (i = 0; i < n; i++) {
            if (!leaf)
                children[i].printInOrder();
            System.out.print(keys[i] + " ");
        }
        if (!leaf)
            children[i].printInOrder();
    }
}

public class BTree {
    private BTreeNode root;
    private int t;

    Reg foundata;
    boolean found = false;

    public BTree(int t) {
        this.t = t;
        root = null;
    }

    public BTreeNode search(int ID) {
        found = false;
        foundata = null;
        return (root == null) ? null : root.search(ID, this);
    }

    public void insert(Reg reg) {
        if (root == null) {
            root = new BTreeNode(t, true);
            root.keys[0] = reg;
            root.n = 1;
        } else {
            if (root.n == 2 * t - 1) {
                BTreeNode newRoot = new BTreeNode(t, false);
                newRoot.children[0] = root;
                newRoot.splitChild(0, root);

                int i = 0;
                if (newRoot.keys[0].ID < reg.ID)
                    i++;

                newRoot.children[i].insertNonFull(reg);
                root = newRoot;
            } else {
                root.insertNonFull(reg);
            }
        }
    }

    public void remove(int ID) {
        found = false;
        foundata = null;

        if (root == null) {
            System.out.println("The tree is empty.");
            return;
        }

        root.remove(ID, this);

        if (root.n == 0) {
            root = (root.leaf) ? null : root.children[0];
        }
    }

    public void printBTree() {
        if (root != null)
            root.printInOrder();
        System.out.println();
    }
}
