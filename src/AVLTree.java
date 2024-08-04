// Node class for AVL tree
class Node {
    Reg data;
    int height;
    Node left, right;

    public Node(Reg item) {
        data = item;
        height = 1;
        left = right = null;
    }
}

// AVLTree class
class AVLTree {
    Node root;
    Reg foundata;
    boolean found = false;

    int height(Node N) {
        if (N == null)
            return 0;
        return N.height;
    }

    int max(int a, int b) {
        return (a > b) ? a : b;
    }

    Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;

        return x;
    }

    Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;

        return y;
    }

    int getBalance(Node N) {
        if (N == null)
            return 0;
        return height(N.left) - height(N.right);
    }

    Node insert(Node node, Reg key) {
        if (node == null)
            return (new Node(key));

        if (key.ID < node.data.ID)
            node.left = insert(node.left, key);
        else if (key.ID > node.data.ID)
            node.right = insert(node.right, key);
        else 
            return node;

        node.height = 1 + max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && key.ID < node.left.data.ID)
            return rightRotate(node);

        if (balance < -1 && key.ID > node.right.data.ID)
            return leftRotate(node);

        if (balance > 1 && key.ID > node.left.data.ID) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && key.ID < node.right.data.ID) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    Node minValueNode(Node node) {
        Node current = node;

        while (current.left != null)
            current = current.left;

        return current;
    }

    Node deleteNode(Node root, Reg key) {
        if (root == null) {
            found = false;
            foundata = null;
            return root;
        }
    
        if (key.ID < root.data.ID) {
            root.left = deleteNode(root.left, key);
        }
        else if (key.ID > root.data.ID) {
            root.right = deleteNode(root.right, key);
        }
        else {
            if (found == false){
                found = true;
                foundata = root.data;
            }
            
    
            if ((root.left == null) || (root.right == null)) {
                Node temp = (root.left != null) ? root.left : root.right;
    
                if (temp == null) {
                    temp = root;
                    root = null;
                } else { 
                    root = temp; 
                }
            } else {
                Node temp = minValueNode(root.right);
    
                root.data = temp.data;
    
                root.right = deleteNode(root.right, temp.data);
            }
        }
    
        if (root == null)
            return root;
    
        root.height = 1 + max(height(root.left), height(root.right));
    
        int balance = getBalance(root);
        
        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);
    
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }
    
        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);
    
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }
    
        return root;
    }

    Node search(Node root, int ID) {

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
