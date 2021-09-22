package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class ReplacerInputStream extends InputStream {
    private final InputStream in;
    private final String search;
    private final String replace;
    private final List<Integer> buffer = new LinkedList<>();

    public ReplacerInputStream(final InputStream in, final String search, final String replace) {
        this.in = in;
        this.search = search;
        this.replace = replace;
    }

    @Override
    public int read() throws IOException {
        int b;
        do {
            b = in.read();
            if (b != -1)
                buffer.add(b);
        } while (buffer.size() < search.length() && b != -1);
        if (buffer.size() >= search.length()) {
            boolean found = true;
            for (int i = 0; i < search.length(); i++) {
                if (getBufferChar(i) != search.charAt(i)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                for (int i = 0; i < search.length(); i++)
                    buffer.remove(0);
                for (int i = replace.length() - 1; i >= 0; i--)
                    buffer.add(0, (int) replace.charAt(i));
            }
        }
        b = -1;
        if (buffer.size() > 0) {
            b = buffer.get(0);
            buffer.remove(0);
        }
        return b;
    }

    private char getBufferChar(final int i) {
        return (char) (int) buffer.get(i);
    }
}
