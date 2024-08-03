import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// B+ Tree Node class to represent internal and leaf nodes
class BPlusTreeNode {
    boolean isLeaf;
    List<Reg> keys;
    List<BPlusTreeNode> children;
    BPlusTreeNode next;

    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.next = null;
    }
}

// B+ Tree class with basic operations: insert, search, and delete
class BPlusTree {
    private BPlusTreeNode root;
    private final int order;
    Reg foundata;
    boolean found = false;

    public BPlusTree(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("El orden debe ser mayor a 3");
        }
        this.root = new BPlusTreeNode(true);
        this.order = order;
    }

    private BPlusTreeNode findLeaf(int ID) {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && ID >= node.keys.get(i).ID) {
                i++;
            }
            node = node.children.get(i);
        }
        return node;
    }

    public void insert(Reg reg) {
        BPlusTreeNode leaf = findLeaf(reg.ID);
        insertIntoLeaf(leaf, reg);

        if (leaf.keys.size() > order - 1) {
            splitLeaf(leaf);
        }
    }

    private void insertIntoLeaf(BPlusTreeNode leaf, Reg reg) {
        int pos = Collections.binarySearch(leaf.keys, reg, (r1, r2) -> Integer.compare(r1.ID, r2.ID));
        if (pos < 0) {
            pos = -(pos + 1);
        }
        leaf.keys.add(pos, reg);
    }

    private void splitLeaf(BPlusTreeNode leaf) {
        int mid = (order + 1) / 2;
        BPlusTreeNode newLeaf = new BPlusTreeNode(true);

        newLeaf.keys.addAll(leaf.keys.subList(mid, leaf.keys.size()));
        leaf.keys.subList(mid, leaf.keys.size()).clear();

        newLeaf.next = leaf.next;
        leaf.next = newLeaf;

        if (leaf == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(newLeaf.keys.get(0));
            newRoot.children.add(leaf);
            newRoot.children.add(newLeaf);
            root = newRoot;
        } else {
            insertIntoParent(leaf, newLeaf, newLeaf.keys.get(0));
        }
    }

    private void insertIntoParent(BPlusTreeNode left, BPlusTreeNode right, Reg key) {
        BPlusTreeNode parent = findParent(root, left);

        if (parent == null) {
            throw new RuntimeException("No se encontró un nodo padre para insertar.");
        }

        int pos = Collections.binarySearch(parent.keys, key, (r1, r2) -> Integer.compare(r1.ID, r2.ID));
        if (pos < 0) {
            pos = -(pos + 1);
        }

        parent.keys.add(pos, key);
        parent.children.add(pos + 1, right);

        if (parent.keys.size() > order - 1) {
            splitInternal(parent);
        }
    }

    private void splitInternal(BPlusTreeNode internal) {
        int mid = (order - 1) / 2; // Corrected the mid index calculation
        BPlusTreeNode newInternal = new BPlusTreeNode(false);
    
        newInternal.keys.addAll(internal.keys.subList(mid + 1, internal.keys.size()));
        internal.keys.subList(mid + 1, internal.keys.size()).clear();
    
        newInternal.children.addAll(internal.children.subList(mid + 1, internal.children.size()));
        internal.children.subList(mid + 1, internal.children.size()).clear();
    
        Reg midKey = internal.keys.remove(mid);
    
        if (internal == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(midKey);
            newRoot.children.add(internal);
            newRoot.children.add(newInternal);
            root = newRoot;
        } else {
            insertIntoParent(internal, newInternal, midKey);
        }
    }
    

    private BPlusTreeNode findParent(BPlusTreeNode current, BPlusTreeNode target) {
        if (current.isLeaf || current.children.isEmpty()) {
            return null;
        }

        for (int i = 0; i < current.children.size(); i++) {
            BPlusTreeNode child = current.children.get(i);

            if (child == target) {
                return current;
            }

            BPlusTreeNode possibleParent = findParent(child, target);
            if (possibleParent != null) {
                return possibleParent;
            }
        }

        return null;
    }

    public boolean search(int ID) {
        BPlusTreeNode node = findLeaf(ID);
        int pos = Collections.binarySearch(node.keys, new Reg(ID, ""), (r1, r2) -> Integer.compare(r1.ID, r2.ID));
        return pos >= 0;
    }

    public void delete(int ID) {
        BPlusTreeNode leaf = findLeaf(ID);
        int pos = Collections.binarySearch(leaf.keys, new Reg(ID, ""), (r1, r2) -> Integer.compare(r1.ID, r2.ID));
        if (pos >= 0) {
            leaf.keys.remove(pos);
            if (leaf.keys.size() < Math.ceil((order - 1) / 2.0) && leaf != root) {
                handleUnderflow(leaf);
            }
        }
    }

    private void handleUnderflow(BPlusTreeNode node) {
        BPlusTreeNode parent = findParent(root, node);
        int index = parent.children.indexOf(node);
        BPlusTreeNode leftSibling = (index > 0) ? parent.children.get(index - 1) : null;
        BPlusTreeNode rightSibling = (index < parent.children.size() - 1) ? parent.children.get(index + 1) : null;

        if (leftSibling != null && leftSibling.keys.size() > Math.ceil((order - 1) / 2.0)) {
            // Borrow from left sibling
            node.keys.add(0, leftSibling.keys.remove(leftSibling.keys.size() - 1));
            if (!node.isLeaf) {
                node.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
            }
        } else if (rightSibling != null && rightSibling.keys.size() > Math.ceil((order - 1) / 2.0)) {
            // Borrow from right sibling
            node.keys.add(rightSibling.keys.remove(0));
            if (!node.isLeaf) {
                node.children.add(rightSibling.children.remove(0));
            }
        } else {
            // Merge with a sibling
            if (leftSibling != null) {
                // Merge with left sibling
                leftSibling.keys.addAll(node.keys);
                if (!node.isLeaf) {
                    leftSibling.children.addAll(node.children);
                }
                leftSibling.next = node.next;
                parent.children.remove(node);
                parent.keys.remove(index - 1);
            } else {
                // Merge with right sibling
                node.keys.addAll(rightSibling.keys);
                if (!node.isLeaf) {
                    node.children.addAll(rightSibling.children);
                }
                node.next = rightSibling.next;
                parent.children.remove(rightSibling);
                parent.keys.remove(index);
            }

            if (parent.keys.size() < Math.ceil((order - 1) / 2.0) && parent != root) {
                handleUnderflow(parent);
            } else if (parent == root && parent.keys.isEmpty()) {
                root = node.isLeaf ? node : node.children.get(0);
            }
        }
    }

    public void printTree() {
        printNode(root, 0);
    }

    private void printNode(BPlusTreeNode node, int level) {
        System.out.println("Level " + level + ": " + node.keys.stream().map(reg -> reg.ID).toList());
        if (!node.isLeaf) {
            for (BPlusTreeNode child : node.children) {
                printNode(child, level + 1);
            }
        }
    }
}

