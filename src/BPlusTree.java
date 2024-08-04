import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

class BPlusTree {
    private BPlusTreeNode root;
    Reg foundata;
    boolean found = false;
  
    private final int order; 

    public BPlusTree(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("Order must be at least 3");
        }
        this.root = new BPlusTreeNode(true);
        this.order = order;
    }

    private BPlusTreeNode findLeaf(Reg key) {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && key.ID >= node.keys.get(i).ID) {
                i++;
            }
            node = node.children.get(i);
        }
        return node;
    }

    public void insert(Reg key) {
        BPlusTreeNode leaf = findLeaf(key);
        insertIntoLeaf(leaf, key);

        if (leaf.keys.size() > order - 1) {
            splitLeaf(leaf);
        }
    }

    private void insertIntoLeaf(BPlusTreeNode leaf, Reg key) {
        int pos = Collections.binarySearch(leaf.keys, key);
        if (pos < 0) {
            pos = -(pos + 1);
        }
        leaf.keys.add(pos, key);
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
            throw new RuntimeException("Parent node not found for insertion");
        }

        int pos = Collections.binarySearch(parent.keys, key);
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
        int mid = (order - 1) / 2; 
        BPlusTreeNode newInternal = new BPlusTreeNode(false);
    
        newInternal.keys.addAll(internal.keys.subList(mid + 1, internal.keys.size()));
        internal.keys.subList(mid + 1, internal.keys.size()).clear();
    
        newInternal.children.addAll(internal.children.subList(mid + 1, internal.children.size()));
        internal.children.subList(mid + 1, internal.children.size()).clear();
    
        if (internal == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(internal.keys.remove(mid));
            newRoot.children.add(internal);
            newRoot.children.add(newInternal);
            root = newRoot;
        } else {
            insertIntoParent(internal, newInternal, internal.keys.remove(mid));
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

    public boolean search(Reg key) {
        BPlusTreeNode leaf = findLeaf(key);
        int pos = Collections.binarySearch(leaf.keys, key);
        if (pos >= 0) {
            found = true;
            foundata = leaf.keys.get(pos);
        }
        return pos >= 0;
    }    

    public void printTree() {
        printNode(root, 0);
    }

    public void delete(Reg key) {
        BPlusTreeNode leaf = findLeaf(key);
        int pos = Collections.binarySearch(leaf.keys, key);
        if (pos >= 0) {

            if (found == false){
                found = true;
                foundata = leaf.keys.get(pos);
            }

            deleteFromLeaf(leaf, pos);
            if (leaf.keys.size() < (order + 1) / 2) {
                balanceAfterDeletion(leaf);
            }
        }
    }

    private void deleteFromLeaf(BPlusTreeNode leaf, int pos) {
        leaf.keys.remove(pos);
    }

    private void balanceAfterDeletion(BPlusTreeNode node) {
        if (node == root) {
            if (node.keys.isEmpty() && !node.isLeaf) {
                root = node.children.get(0);
            }
            return;
        }

        BPlusTreeNode parent = findParent(root, node);
        int index = parent.children.indexOf(node);
        BPlusTreeNode leftSibling = (index > 0) ? parent.children.get(index - 1) : null;
        BPlusTreeNode rightSibling = (index < parent.children.size() - 1) ? parent.children.get(index + 1) : null;

        if (leftSibling != null && leftSibling.keys.size() > (order + 1) / 2) {
            borrowFromLeftSibling(node, leftSibling, parent, index - 1);
        } else if (rightSibling != null && rightSibling.keys.size() > (order + 1) / 2) {
            borrowFromRightSibling(node, rightSibling, parent, index);
        } else {
            if (leftSibling != null) {
                mergeNodes(leftSibling, node, parent, index - 1);
            } else if (rightSibling != null) {
                mergeNodes(node, rightSibling, parent, index);
            }
        }
    }

    private void borrowFromLeftSibling(BPlusTreeNode node, BPlusTreeNode leftSibling, BPlusTreeNode parent, int parentIndex) {
        node.keys.add(0, leftSibling.keys.remove(leftSibling.keys.size() - 1));
        if (!leftSibling.isLeaf) {
            node.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
        }
        parent.keys.set(parentIndex, node.keys.get(0));
    }

    private void borrowFromRightSibling(BPlusTreeNode node, BPlusTreeNode rightSibling, BPlusTreeNode parent, int parentIndex) {
        node.keys.add(rightSibling.keys.remove(0));
        if (!rightSibling.isLeaf) {
            node.children.add(rightSibling.children.remove(0));
        }
        parent.keys.set(parentIndex, rightSibling.keys.get(0));
    }

    private void mergeNodes(BPlusTreeNode left, BPlusTreeNode right, BPlusTreeNode parent, int parentIndex) {
        left.keys.addAll(right.keys);
        if (!right.isLeaf) {
            left.children.addAll(right.children);
        }
        left.next = right.next;

        parent.keys.remove(parentIndex);
        parent.children.remove(right);

        if (parent.keys.size() < (order + 1) / 2) {
            balanceAfterDeletion(parent);
        }
    }
    
    private void printNode(BPlusTreeNode node, int level) {
        System.out.println("Level " + level + ": " + node.keys);
        if (!node.isLeaf) {
            for (BPlusTreeNode child : node.children) {
                printNode(child, level + 1);
            }
        }
    }
}

