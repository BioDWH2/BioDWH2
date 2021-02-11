package de.unibi.agbi.biodwh2.core.io.gff;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * GFF version 3 file format reader. https://github.com/The-Sequence-Ontology/Specifications/blob/master/gff3.md
 */
public final class GFF3Reader implements Iterable<GFF3Entry> {
    private static final Map<String, String> PERCENT_ENCODED_CHARACTERS = new HashMap<>();
    /**
     * Small lookup table for some Sequence Ontology (SO) terms and respective accession numbers.
     */
    private static final Map<String, String> SEQUENCE_ONTOLOGY_MAP = new HashMap<>();
    private static final char UNDEFINED_CHARACTER = '.';

    private final BufferedReader reader;
    GFF3Entry lastEntry;
    private boolean inFastaMode;
    private String nextSequenceTag;

    static {
        PERCENT_ENCODED_CHARACTERS.put("%09", "\t");
        PERCENT_ENCODED_CHARACTERS.put("%0A", "\n");
        PERCENT_ENCODED_CHARACTERS.put("%0D", "\r");
        PERCENT_ENCODED_CHARACTERS.put("%25", "%");
        for (int i = 0x00; i <= 0x1F; i++) {
            String hex = Integer.toHexString(i).toUpperCase(Locale.US);
            if (hex.length() == 1)
                hex = '0' + hex;
            PERCENT_ENCODED_CHARACTERS.put('%' + hex, "" + (char) i);
        }
        PERCENT_ENCODED_CHARACTERS.put("%7F", "" + 0x7F);
        PERCENT_ENCODED_CHARACTERS.put("%3B", ";");
        PERCENT_ENCODED_CHARACTERS.put("%3D", "=");
        PERCENT_ENCODED_CHARACTERS.put("%26", "&");
        PERCENT_ENCODED_CHARACTERS.put("%2C", ",");
        SEQUENCE_ONTOLOGY_MAP.put("gene", "SO:0000704");
        SEQUENCE_ONTOLOGY_MAP.put("SO:0000704", "gene");
        SEQUENCE_ONTOLOGY_MAP.put("mRNA", "SO:0000234");
        SEQUENCE_ONTOLOGY_MAP.put("SO:0000234", "mRNA");
        SEQUENCE_ONTOLOGY_MAP.put("exon", "SO:0000147");
        SEQUENCE_ONTOLOGY_MAP.put("SO:0000147", "exon");
        SEQUENCE_ONTOLOGY_MAP.put("intron", "SO:0000188");
        SEQUENCE_ONTOLOGY_MAP.put("SO:0000188", "intron");
        SEQUENCE_ONTOLOGY_MAP.put("cds", "SO:0000316");
        SEQUENCE_ONTOLOGY_MAP.put("SO:0000316", "cds");
    }

    public GFF3Reader(final String filePath, final String charsetName) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charsetName);
    }

    public GFF3Reader(final InputStream stream, final String charsetName) throws UnsupportedEncodingException {
        final InputStream baseStream = new BufferedInputStream(stream);
        reader = new BufferedReader(new InputStreamReader(baseStream, charsetName));
    }

    @Override
    public Iterator<GFF3Entry> iterator() {
        return new Iterator<GFF3Entry>() {
            @Override
            public boolean hasNext() {
                lastEntry = readNextEntry();
                return lastEntry != null;
            }

            @Override
            public GFF3Entry next() {
                return lastEntry;
            }
        };
    }

    GFF3Entry readNextEntry() {
        String line;
        while ((line = readLineSafe()) != null) {
            if (line.trim().length() <= 0)
                continue;
            if (nextSequenceTag != null) {
                final String tag = nextSequenceTag;
                nextSequenceTag = null;
                final String sequence = readSequence(line);
                return new GFF3FastaEntry(tag, sequence);
            }
            if (StringUtils.startsWith(line, ">")) {
                inFastaMode = true;
                final String tag = line.substring(1).trim();
                final String sequence = readSequence(null);
                return new GFF3FastaEntry(tag, sequence);
            }
            if (StringUtils.startsWith(line, "##FASTA")) {
                inFastaMode = true;
                continue;
            }
            if (!inFastaMode) {
                if (StringUtils.startsWith(line, "##")) {
                    return parsePragmaEntry(line);
                }
                if (!StringUtils.startsWith(line, "#"))
                    return parseDataEntry(line);
            }
        }
        return null;
    }

    private String readLineSafe() {
        try {
            return reader.readLine();
        } catch (IOException ignored) {
            return null;
        }
    }

    private String readSequence(String line) {
        StringBuilder sequence = new StringBuilder();
        if (line != null)
            sequence.append(line);
        String seekLine;
        while ((seekLine = readLineSafe()) != null) {
            if (StringUtils.startsWith(seekLine, ">")) {
                nextSequenceTag = seekLine.substring(1).trim();
                break;
            } else if (seekLine.length() > 0)
                sequence.append(seekLine);
        }
        return sequence.toString();
    }

    private GFF3Entry parsePragmaEntry(final String line) {
        return new GFF3PragmaEntry(StringUtils.trim(line.substring(2)));
    }

    private GFF3Entry parseDataEntry(final String line) {
        final String[] columns = StringUtils.split(line, '\t');
        if (columns.length != 9) {
            // TODO: warn wrong column count
        }
        final String seqId = isFieldUndefined(columns[0]) ? null : replaceEscapedChars(columns[0]);
        final String source = isFieldUndefined(columns[1]) ? null : replaceEscapedChars(columns[1]);
        final String type = isFieldUndefined(columns[2]) ? null : replaceEscapedChars(columns[2]);
        String typeSOId = null;
        String typeSOName = null;
        if (type != null) {
            if (type.startsWith("SO:")) {
                typeSOId = type;
                typeSOName = SEQUENCE_ONTOLOGY_MAP.get(typeSOId);
            } else {
                typeSOName = type;
                typeSOId = SEQUENCE_ONTOLOGY_MAP.get(typeSOName.toLowerCase(Locale.US));
            }
        }
        final Long start = isFieldUndefined(columns[3]) ? null : Long.parseLong(columns[3]);
        final Long end = isFieldUndefined(columns[4]) ? null : Long.parseLong(columns[4]);
        final Double score = isFieldUndefined(columns[5]) ? null : Double.parseDouble(columns[5]);
        final GFF3DataEntry.Strand strand = parseStrand(
                isFieldUndefined(columns[6]) ? null : replaceEscapedChars(columns[6]));
        final Integer phase = isFieldUndefined(columns[7]) ? null : Integer.parseInt(columns[7]);
        final Map<String, String> attributes = parseAttributes(isFieldUndefined(columns[8]) ? null : columns[8]);
        return new GFF3DataEntry(seqId, source, typeSOId, typeSOName, start, end, score, strand, phase, attributes);
    }

    private boolean isFieldUndefined(final String field) {
        return field == null || field.length() == 0 || (field.length() == 1 && field.charAt(0) == UNDEFINED_CHARACTER);
    }

    private String replaceEscapedChars(String value) {
        for (final String key : PERCENT_ENCODED_CHARACTERS.keySet())
            value = StringUtils.replace(value, key, PERCENT_ENCODED_CHARACTERS.get(key));
        return value;
    }

    private GFF3DataEntry.Strand parseStrand(final String value) {
        if (value == null)
            return GFF3DataEntry.Strand.UNSTRANDED;
        switch (value.charAt(0)) {
            case '?':
                return GFF3DataEntry.Strand.RELEVANT_BUT_UNKNOWN;
            case '+':
                return GFF3DataEntry.Strand.POSITIVE;
            case '-':
                return GFF3DataEntry.Strand.NEGATIVE;
            default:
                return GFF3DataEntry.Strand.UNKNOWN_VALUE;
        }
    }

    private Map<String, String> parseAttributes(final String line) {
        final Map<String, String> result = new HashMap<>();
        if (line != null) {
            final String[] attributePairs = StringUtils.split(line, ';');
            for (final String pair : attributePairs) {
                final String[] parts = StringUtils.split(pair, '=');
                final String tag = replaceEscapedChars(parts[0].trim());
                final String value = replaceEscapedChars(parts[1].trim());
                result.put(tag, value);
            }
        }
        return result;
    }
}
