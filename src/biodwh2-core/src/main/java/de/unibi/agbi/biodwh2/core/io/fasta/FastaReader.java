package de.unibi.agbi.biodwh2.core.io.fasta;

import de.unibi.agbi.biodwh2.core.io.BaseReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public final class FastaReader extends BaseReader<FastaEntry> {
    private final boolean sequenceAsSingleLine;
    private String nextHeader;

    @SuppressWarnings("unused")
    public FastaReader(final String filePath, final Charset charset) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charset, true);
    }

    public FastaReader(final InputStream stream, final Charset charset) {
        this(stream, charset, true);
    }

    @SuppressWarnings("unused")
    public FastaReader(final String filePath, final Charset charset,
                       final boolean sequenceAsSingleLine) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charset, sequenceAsSingleLine);
    }

    public FastaReader(final InputStream stream, final Charset charset, final boolean sequenceAsSingleLine) {
        super(stream, charset);
        this.sequenceAsSingleLine = sequenceAsSingleLine;
    }

    @Override
    protected FastaEntry readNextEntry() {
        if (nextHeader == null)
            nextHeader = readLineSafe();
        if (nextHeader == null)
            return null;
        final FastaEntry newEntry = new FastaEntry();
        newEntry.setHeader(nextHeader);
        final StringBuilder sequence = new StringBuilder();
        String line;
        while ((line = readLineSafe()) != null) {
            if (line.startsWith(">"))
                break;
            if (StringUtils.isNotBlank(line)) {
                if (sequenceAsSingleLine) {
                    sequence.append(line.trim());
                } else {
                    if (sequence.length() > 0)
                        sequence.append('\n');
                    sequence.append(line);
                }
            }
        }
        nextHeader = line;
        newEntry.setSequence(sequence.toString());
        return newEntry;
    }
}
