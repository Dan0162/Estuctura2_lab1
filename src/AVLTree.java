// Node class for AVL tree
class Node {
    Reg data;
    int height;
    Node left, right;

    public Node(Reg item) {
        data = item;
        height = 1; // height is initialized as 1 for new nodes
        left = right = null;
    }
}

// AVLTree class
class AVLTree {
    Node root;
    Reg foundata;
    boolean found = false;

    // Utility function to get the height of the tree
    int height(Node N) {
        if (N == null)
            return 0;
        return N.height;
    }

    // Utility function to get maximum of two integers
    int max(int a, int b) {
        return (a > b) ? a : b;
    }

    // Utility function to right rotate subtree rooted with y
    Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;

        // Return new root
        return x;
    }

    // Utility function to left rotate subtree rooted with x
    Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;

        // Return new root
        return y;
    }

    // Get Balance factor of node N
    int getBalance(Node N) {
        if (N == null)
            return 0;
        return height(N.left) - height(N.right);
    }

    // Function to insert a key in the subtree rooted with node and returns the new root of the subtree.
    Node insert(Node node, Reg key) {
        // 1. Perform the normal BST insertion
        if (node == null)
            return (new Node(key));

        if (key.ID < node.data.ID)
            node.left = insert(node.left, key);
        else if (key.ID > node.data.ID)
            node.right = insert(node.right, key);
        else // Duplicate IDs not allowed
            return node;

        // 2. Update height of this ancestor node
        node.height = 1 + max(height(node.left), height(node.right));

        // 3. Get the balance factor of this ancestor node to check whether this node became unbalanced
        int balance = getBalance(node);

        // If this node becomes unbalanced, then there are 4 cases

        // Left Left Case
        if (balance > 1 && key.ID < node.left.data.ID)
            return rightRotate(node);

        // Right Right Case
        if (balance < -1 && key.ID > node.right.data.ID)
            return leftRotate(node);

        // Left Right Case
        if (balance > 1 && key.ID > node.left.data.ID) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && key.ID < node.right.data.ID) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        // return the (unchanged) node pointer
        return node;
    }

    // Function to find the node with the minimum value in a given subtree
    Node minValueNode(Node node) {
        Node current = node;

        // Loop down to find the leftmost leaf
        while (current.left != null)
            current = current.left;

        return current;
    }

    // Function to delete a node with a given key in the AVL tree and return the new root
    Node deleteNode(Node root, Reg key) {
        // Step 1: Perform standard BST delete
        if (root == null) {
            found = false;
            foundata = null;
            return root;
        }
    
        // If the key to be deleted is smaller than the root's key, then it lies in left subtree
        if (key.ID < root.data.ID) {
            root.left = deleteNode(root.left, key);
        }
        // If the key to be deleted is greater than the root's key, then it lies in right subtree
        else if (key.ID > root.data.ID) {
            root.right = deleteNode(root.right, key);
        }
        // If key is same as root's key, then this is the node to be deleted
        else {
            if (found == false){
                found = true;
                foundata = root.data;
            }
            
    
            // Node with only one child or no child
            if ((root.left == null) || (root.right == null)) {
                Node temp = (root.left != null) ? root.left : root.right;
    
                // No child case
                if (temp == null) {
                    temp = root;
                    root = null;
                } else { // One child case
                    root = temp; // Copy the contents of the non-empty child
                }
            } else {
                // Node with two children: Get the inorder successor (smallest in the right subtree)
                Node temp = minValueNode(root.right);
    
                // Copy the inorder successor's data to this node
                root.data = temp.data;
    
                // Delete the inorder successor
                root.right = deleteNode(root.right, temp.data);
            }
        }
    
        // If the tree had only one node then return
        if (root == null)
            return root;
    
        // Step 2: Update the height of the current node
        root.height = 1 + max(height(root.left), height(root.right));
    
        // Step 3: Get the balance factor of this node to check whether this node became unbalanced
        int balance = getBalance(root);
    
        // If this node becomes unbalanced, then there are 4 cases
    
        // Left Left Case
        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);
    
        // Left Right Case
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }
    
        // Right Right Case
        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);
    
        // Right Left Case
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }
    
        return root;
    }

    // Function to search for a node with a given ID in the AVL tree
    Node search(Node root, int ID) {

        // Base cases: root is null or key is present at root
        if (root == null){
            return root;
        }
        else if (root.data.ID == ID){
            found = true;
            foundata = root.data;
            return root;
        }
        else if (root.data.ID < ID){
            return search(root.right, ID);
        }
        else{
            return search(root.left, ID);
        }
    }
}
