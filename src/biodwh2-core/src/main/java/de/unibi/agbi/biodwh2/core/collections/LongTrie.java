package de.unibi.agbi.biodwh2.core.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class LongTrie extends AbstractCollection<Long> implements Serializable {
    private static final long serialVersionUID = -8002512841290306308L;

    private Node root;

    public LongTrie() {
        root = new Node();
    }

    @Override
    public Iterator<Long> iterator() {
        return values().iterator();
    }

    @Override
    public boolean add(Long number) {
        if (number == null)
            return false;
        Node[] children = root.children;
        if (number == 0) {
            if (children[0] == null)
                children[0] = new Node(0);
            children[0].isLeaf = true;
            return true;
        }
        Node node = root;
        final int length = (int) Math.log10(number) + 1;
        long divider = 1;
        for (int i = 0; i < length - 1; i++)
            divider *= 10;
        for (int i = 0; i < length; i++) {
            final int digit = (int) (number / divider);
            number = number - digit * divider;
            divider = divider / 10;
            node = children[digit];
            if (node == null) {
                node = new Node(digit);
                children[digit] = node;
            }
            children = node.children;
        }
        node.isLeaf = true;
        return true;
    }

    @Override
    public boolean contains(final Object o) {
        if (!(o instanceof Long))
            return false;
        long number = (Long) o;
        if (number == 0)
            return root.children[0] != null && root.children[0].isLeaf;
        Node[] children = root.children;
        Node node = null;
        final int length = (int) Math.log10(number) + 1;
        long divider = 1;
        for (int i = 0; i < length - 1; i++)
            divider *= 10;
        for (int i = 0; i < length; i++) {
            final int digit = (int) (number / divider);
            number = number - digit * divider;
            divider = divider / 10;
            node = children[digit];
            if (node == null)
                return false;
            children = node.children;
        }
        return node != null && node.isLeaf;
    }

    @Override
    public int size() {
        return sizeRecursive(root, 0);
    }

    private int sizeRecursive(final Node node, int count) {
        if (node.isLeaf)
            count++;
        for (int i = 0; i < node.children.length; i++) {
            final Node child = node.children[i];
            if (child != null)
                count = sizeRecursive(child, count);
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        return isEmpty(root);
    }

    private static boolean isEmpty(final Node node) {
        for (int i = 0; i < node.children.length; i++)
            if (node.children[i] != null)
                return false;
        return true;
    }

    @Override
    public void clear() {
        Arrays.fill(root.children, null);
    }

    public List<Long> values() {
        final List<Long> values = new ArrayList<>();
        collectValuesRecursive(root, values, 0);
        return values;
    }

    private void collectValuesRecursive(final Node node, final List<Long> values, final long value) {
        if (node.isLeaf)
            values.add(value);
        for (int i = 0; i < node.children.length; i++) {
            final Node child = node.children[i];
            if (child != null)
                collectValuesRecursive(child, values, value * 10 + child.digit);
        }
    }

    @Override
    public Object[] toArray() {
        return values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        final List<Long> values = values();
        //noinspection SuspiciousToArrayCall
        return values.toArray(a);
    }

    @Override
    public boolean remove(final Object o) {
        if (!(o instanceof Long))
            return false;
        final long number = (Long) o;
        if (number == 0)
            return root.children[0] != null && removeRecursive(root.children[0], number, 0);
        final int dividerSize = (int) Math.log10(number);
        long divider = 1;
        for (int i = 0; i < dividerSize; i++)
            divider *= 10;
        return removeRecursive(root, number, divider);
    }

    private boolean removeRecursive(final Node node, final long number, final long divider) {
        if (divider == 0) {
            if (!node.isLeaf)
                return false;
            node.isLeaf = false;
            return isEmpty(node);
        }
        final int digit = (int) (number / divider);
        final Node child = node.children[digit];
        if (child == null)
            return false;
        if (removeRecursive(child, number - digit * divider, divider / 10) && !child.isLeaf) {
            node.children[digit] = null;
            return isEmpty(node);
        }
        return false;
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        root.writeObject(out);
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        root = new Node();
        root.readObject(in);
    }

    private static class Node {
        public int digit;
        public Node[] children = new Node[10];
        private boolean isLeaf;

        public Node() {
        }

        public Node(final int digit) {
            this.digit = digit;
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.writeBoolean(isLeaf);
            for (int i = 0; i < children.length; i++)
                if (children[i] != null) {
                    out.writeByte(i);
                    children[i].writeObject(out);
                }
            out.writeByte(255);
        }

        private void readObject(final ObjectInputStream in) throws IOException {
            isLeaf = in.readBoolean();
            children = new Node[10];
            int digit;
            while ((digit = in.readUnsignedByte()) != 255) {
                children[digit] = new Node(digit);
                children[digit].readObject(in);
            }
        }
    }
}
