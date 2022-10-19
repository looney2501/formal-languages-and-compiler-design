package Utils;

import java.util.*;

public class CustomTable {
    private static class BinarySearchTree {
        private static class Node {
            private final String key;
            private final ItemType type;
            private int code;
            private Node leftChild, rightChild;

            public Node(String key, ItemType type) {
                this.key = key;
                this.code = 0;
                this.leftChild = null;
                this.rightChild = null;
                this.type = type;
            }

            public String getKey() {
                return key;
            }

            public int getCode() {
                return code;
            }

            public Node getLeftChild() {
                return leftChild;
            }

            public Node getRightChild() {
                return rightChild;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public void setLeftChild(Node leftChild) {
                this.leftChild = leftChild;
            }

            public void setRightChild(Node rightChild) {
                this.rightChild = rightChild;
            }

            public ItemType getType() {
                return type;
            }
        }

        private static enum ChildPosition {
            LEFT,
            RIGHT
        }

        private Node root;

        public BinarySearchTree() {
            this.root = null;
        }

        public Node add(String key, ItemType type) {
            if (root == null) {
                root = new Node(key, type);
                return root;
            }

            Node previousNode = null;
            Node currentNode = root;
            ChildPosition childPosition = null;
            while (currentNode != null) {
                previousNode = currentNode;
                if (key.compareTo(currentNode.getKey()) < 0) {
                    currentNode = currentNode.getLeftChild();
                    childPosition = ChildPosition.LEFT;
                }
                else {
                    currentNode = currentNode.getRightChild();
                    childPosition = ChildPosition.RIGHT;
                }
            }

            Node newChild = new Node(key, type);
            if (childPosition == ChildPosition.LEFT) {
                previousNode.setLeftChild(newChild);
            }
            else {
                previousNode.setRightChild(newChild);
            }

            return newChild;

        }

        public Node find(String key) {
            if (root == null) return null;

            Node currentNode = root;
            while (currentNode != null) {
                if (key.equals(currentNode.getKey())) {
                    return currentNode;
                }
                if (key.compareTo(currentNode.getKey()) < 0) {
                    currentNode = currentNode.getLeftChild();
                }
                else {
                    currentNode = currentNode.getRightChild();
                }
            }

            return null;
        }

        public List<Node> getAll() {
            if (root == null) return new ArrayList<>();

            List<Node> allNodes = new ArrayList<>();
            Node current = root;
            Stack<Node> stack = new Stack<>();

            while (current != null || stack.size() > 0) {
                while (current != null) {
                    stack.push(current);
                    current = current.getLeftChild();
                }

                current = stack.pop();

                allNodes.add(current);

                current = current.getRightChild();
            }

            return allNodes;
        }

        public List<Node> setCodes() {
            if (root == null) return new ArrayList<>();

            List<Node> allNodes = new ArrayList<>();
            Node current = root;
            Stack<Node> stack = new Stack<>();
            int counter = 1;

            while (current != null || stack.size() > 0) {
                while (current != null) {
                    stack.push(current);
                    current = current.getLeftChild();
                }

                current = stack.pop();
                current.setCode(counter);
                counter++;

                allNodes.add(current);

                current = current.getRightChild();
            }

            return allNodes;
        }
    }

    private static class TableRow {
        private final String key;
        private final int code;
        private final ItemType type;

        public TableRow(String key, int code, ItemType type) {
            this.key = key;
            this.code = code;
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public int getCode() {
            return code;
        }

        public ItemType getType() {
            return type;
        }
    }

    private final BinarySearchTree bst;

    public CustomTable() {
        this.bst = new BinarySearchTree();
    }

    public List<TableRow> addFromList(List<TableRow> tableRows) {
        tableRows.forEach(tr -> bst.add(tr.getKey(), tr.getType()));
        List<BinarySearchTree.Node> allNodes = bst.setCodes();
        return allNodes.stream().map(n -> new TableRow(n.getKey(), n.getCode(), n.getType())).toList();
    }

    public List<TableRow> getAll() {
        return bst.getAll().stream().map(n -> new TableRow(n.getKey(), n.getCode(), n.getType())).toList();
    }

    public TableRow get(String key) {
        BinarySearchTree.Node node = bst.find(key);
        if (node == null) return null;
        return new TableRow(key, node.getCode(), node.getType());
    }
}
