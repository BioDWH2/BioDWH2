package de.unibi.agbi.biodwh2.core.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public final class Trie extends AbstractCollection<CharSequence> implements Serializable {
    private static final long serialVersionUID = 3961256311025487069L;

    private final Node root;

    public Trie() {
        root = new Node();
    }

    @Override
    public Iterator<CharSequence> iterator() {
        return values().iterator();
    }

    @Override
    public boolean add(final CharSequence word) {
        Map<Character, Node> children = root.children;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            Node node = children.get(c);
            if (node == null) {
                node = new Node(c);
                children.put(c, node);
            }
            children = node.children;
            if (i == word.length() - 1)
                node.isLeaf = true;
        }
        return true;
    }

    @Override
    public boolean contains(final Object o) {
        if (!(o instanceof CharSequence))
            return false;
        final CharSequence word = (CharSequence) o;
        Map<Character, Node> children = root.children;
        Node node = null;
        for (int i = 0; i < word.length(); i++) {
            node = children.get(word.charAt(i));
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
        for (final Node child : node.children.values())
            count = sizeRecursive(child, count);
        return count;
    }

    @Override
    public boolean isEmpty() {
        return root.children.isEmpty();
    }

    @Override
    public void clear() {
        root.children.clear();
    }

    public List<CharSequence> values() {
        final List<CharSequence> values = new ArrayList<>();
        collectValuesRecursive(root, values, "");
        return values;
    }

    private void collectValuesRecursive(final Node node, final List<CharSequence> values, final String value) {
        if (node.isLeaf)
            values.add(value);
        for (final Node child : node.children.values())
            collectValuesRecursive(child, values, value + child.character);
    }

    @Override
    public Object[] toArray() {
        return values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        final List<CharSequence> values = values();
        //noinspection SuspiciousToArrayCall
        return values.toArray(a);
    }

    @Override
    public boolean remove(final Object o) {
        return o instanceof CharSequence && removeRecursive(root, (CharSequence) o, 0);
    }

    private boolean removeRecursive(final Node node, final CharSequence word, final int index) {
        if (index == word.length()) {
            if (!node.isLeaf)
                return false;
            node.isLeaf = false;
            return node.children.isEmpty();
        }
        final char character = word.charAt(index);
        final Node child = node.children.get(character);
        if (child == null)
            return false;
        if (removeRecursive(child, word, index + 1) && !child.isLeaf) {
            node.children.remove(character);
            return node.children.isEmpty();
        }
        return false;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(toArray());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        final Object[] array = (Object[]) in.readObject();
        for (final Object obj : array)
            add((CharSequence) obj);
    }

    private static class Node {
        public final Character character;
        public final Map<Character, Node> children = new HashMap<>();
        private boolean isLeaf;

        public Node() {
            character = null;
        }

        public Node(final char character) {
            this.character = character;
        }
    }
}
