import java.util.ArrayList;

public class BStarTree {
    private int minDegree;
    private Node root;

    public BStarTree(int minDegree) {
        this.minDegree = minDegree;
        this.root = new Node(true);
    }

    // Node class for B* tree
    class Node {
        boolean leaf;
        ArrayList<Reg> keys;
        ArrayList<Integer> values;
        ArrayList<Node> children;

        Node(boolean leaf) {
            this.leaf = leaf;
            this.keys = new ArrayList<>();
            this.values = new ArrayList<>();
            this.children = new ArrayList<>();
        }
    }

    // Method to insert a key in the B* tree
    public void insert(Reg key) {
        Node r = root;
        if (r.keys.size() == 2 * minDegree - 1) {
            Node s = new Node(false);
            root = s;
            s.children.add(r);
            splitChild(s, 0, r);
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
    }

    // Method to split the child node
    private void splitChild(Node parent, int i, Node child) {
        int t = minDegree; // 't' is the minimum degree
        Node newNode = new Node(child.leaf);
        
        // Ensure the number of keys is consistent with B* tree properties
        int numKeysToMove = t - 1; // B* trees maintain more keys per node than B-trees
        if (child.keys.size() < 2 * t - 1) {
            numKeysToMove = child.keys.size() - t;
        }
    
        // Move keys and children to the new node
        newNode.keys.addAll(child.keys.subList(t, t + numKeysToMove));
        if (!child.leaf) {
            newNode.children.addAll(child.children.subList(t, t + numKeysToMove + 1));
        }
    
        // Insert new key to parent
        parent.keys.add(i, child.keys.get(t - 1));
        parent.children.add(i + 1, newNode);
    
        // Remove moved keys and children from the child node
        child.keys.subList(t - 1, child.keys.size()).clear();
        if (!child.leaf) {
            child.children.subList(t, child.children.size()).clear();
        }
    }

    // Method to insert in a non-full node
    private void insertNonFull(Node node, Reg key) {
        int i = node.keys.size() - 1;

        if (node.leaf) {
            while (i >= 0 && key.ID < node.keys.get(i).ID) {
                i--;
            }
            node.keys.add(i + 1, key);
            node.values.add(i + 1);
        } else {
            while (i >= 0 && key.ID < node.keys.get(i).ID) {
                i--;
            }
            i++;
            if (node.children.get(i).keys.size() == 2 * minDegree - 1) {
                splitChild(node, i, node.children.get(i));
                if (key.ID > node.keys.get(i).ID) {
                    i++;
                }
            }
            insertNonFull(node.children.get(i), key);
        }
    }

    // Method to search for a key in the B* tree
    public Integer search(Reg key) {
        return search(root, key);
    }

    private Integer search(Node node, Reg key) {
        int i = 0;
        while (i < node.keys.size() && key.ID > node.keys.get(i).ID) {
            i++;
        }
        if (i < node.keys.size() && key.ID == node.keys.get(i).ID) {
            return node.values.get(i);
        }
        if (node.leaf) {
            return null;
        } else {
            return search(node.children.get(i), key);
        }
    }

    // Method to delete a key from the B* tree
    public void delete(int ID) {
        delete(root, ID);
        if (root.keys.size() == 0 && !root.leaf) {
            root = root.children.get(0);
        }
    }

    private void delete(Node node, int ID) {
        int idx = findKey(node, ID);

        if (idx < node.keys.size() && node.keys.get(idx).ID == ID) {
            if (node.leaf) {
                node.keys.remove(idx);
            } else {
                deleteInternalNode(node, ID, idx);
            }
        } else {
            if (node.leaf) {
                System.out.println("The key " + ID + " is not present in the tree.");
                return;
            }

            boolean flag = (idx == node.keys.size());
            if (node.children.get(idx).keys.size() < minDegree) {
                fill(node, idx);
            }

            if (flag && idx > node.keys.size()) {
                delete(node.children.get(idx - 1), ID);
            } else {
                delete(node.children.get(idx), ID);
            }
        }
    }

    private void deleteInternalNode(Node node, int ID, int idx) {
        if (node.children.get(idx).keys.size() >= minDegree) {
            Reg pred = getPredecessor(node, idx);
            node.keys.set(idx, pred);
            delete(node.children.get(idx), pred.ID);
        } else if (node.children.get(idx + 1).keys.size() >= minDegree) {
            Reg succ = getSuccessor(node, idx);
            node.keys.set(idx, succ);
            delete(node.children.get(idx + 1), succ.ID);
        } else {
            merge(node, idx);
            delete(node.children.get(idx), ID);
        }
    }

    private int findKey(Node node, int ID) {
        int idx = 0;
        while (idx < node.keys.size() && node.keys.get(idx).ID < ID) {
            idx++;
        }
        return idx;
    }

    private Reg getPredecessor(Node node, int idx) {
        Node current = node.children.get(idx);
        while (!current.leaf) {
            current = current.children.get(current.keys.size());
        }
        return current.keys.get(current.keys.size() - 1);
    }

    private Reg getSuccessor(Node node, int idx) {
        Node current = node.children.get(idx + 1);
        while (!current.leaf) {
            current = current.children.get(0);
        }
        return current.keys.get(0);
    }

    private void fill(Node node, int idx) {
        if (idx != 0 && node.children.get(idx - 1).keys.size() >= minDegree) {
            borrowFromPrev(node, idx);
        } else if (idx != node.keys.size() && node.children.get(idx + 1).keys.size() >= minDegree) {
            borrowFromNext(node, idx);
        } else {
            if (idx != node.keys.size()) {
                merge(node, idx);
            } else {
                merge(node, idx - 1);
            }
        }
    }

    private void borrowFromPrev(Node node, int idx) {
        Node child = node.children.get(idx);
        Node sibling = node.children.get(idx - 1);

        child.keys.add(0, node.keys.get(idx - 1));
        node.keys.set(idx - 1, sibling.keys.remove(sibling.keys.size() - 1));

        if (!child.leaf) {
            child.children.add(0, sibling.children.remove(sibling.children.size() - 1));
        }
    }

    private void borrowFromNext(Node node, int idx) {
        Node child = node.children.get(idx);
        Node sibling = node.children.get(idx + 1);

        child.keys.add(node.keys.get(idx));
        node.keys.set(idx, sibling.keys.remove(0));

        if (!child.leaf) {
            child.children.add(sibling.children.remove(0));
        }
    }

    private void merge(Node node, int idx) {
        Node child = node.children.get(idx);
        Node sibling = node.children.get(idx + 1);

        child.keys.add(node.keys.remove(idx));
        child.keys.addAll(sibling.keys);
        child.children.addAll(sibling.children);

        node.children.remove(idx + 1);
    }

}
