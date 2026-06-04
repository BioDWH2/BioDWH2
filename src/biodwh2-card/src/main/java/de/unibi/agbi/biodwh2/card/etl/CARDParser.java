package de.unibi.agbi.biodwh2.card.etl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.card.CARDDataSource;
import de.unibi.agbi.biodwh2.card.model.AROTerm;
import de.unibi.agbi.biodwh2.card.model.Entry;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CARDParser extends Parser<CARDDataSource> {
    private static final Pattern QUOTED_STRING_PATTERN = Pattern.compile("\"(\\.|[^\"])*\"");

    public CARDParser(final CARDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        try {
            parseCARDArchive(workspace);
            parseOntologyArchive(workspace);
            return true;
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse CARD data", e);
        }
    }

    private void parseCARDArchive(final Workspace workspace) throws IOException {
        try (InputStream fileStream = FileUtils.openInput(workspace, dataSource, CARDUpdater.FILE_NAME_DATA);
             BufferedInputStream buffered = new BufferedInputStream(fileStream);
             BZip2CompressorInputStream bzip2 = new BZip2CompressorInputStream(buffered);
             TarArchiveInputStream tar = new TarArchiveInputStream(bzip2)) {
            ArchiveEntry entry;
            while ((entry = tar.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith("card.json")) {
                    parseCARDStream(tar);
                    break;
                }
            }
        }
    }

    private void parseCARDStream(final InputStream cardJsonStream) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        // First, read the entire JSON as a generic map to handle mixed content (models + metadata)
        final Map<String, Object> rawData = mapper.readValue(cardJsonStream,
                                                             mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));

        final List<Entry> card_results = new ArrayList<>();

        // Filter out metadata fields (keys starting with _) and deserialize only model entries
        for (final Map.Entry<String, Object> entry : rawData.entrySet()) {
            final String key = entry.getKey();

            if (!key.startsWith("_")) {
                try {
                    final Entry cardEntry = mapper.convertValue(entry.getValue(), Entry.class);
                    card_results.add(cardEntry);
                } catch (final Exception e) {
                }
            }
        }

        storeResults(dataSource, card_results);
    }

    private void parseOntologyArchive(final Workspace workspace) throws IOException, ParserFormatException {
        try (InputStream fileStream = FileUtils.openInput(workspace, dataSource, CARDUpdater.FILE_NAME_ONTOLOGY);
             BufferedInputStream buffered = new BufferedInputStream(fileStream);
             BZip2CompressorInputStream bzip2 = new BZip2CompressorInputStream(buffered);
             TarArchiveInputStream tar = new TarArchiveInputStream(bzip2)) {
            boolean found = false;
            ArchiveEntry entry;
            while ((entry = tar.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith("aro.obo")) {
                    found = true;
                    parseAROStream(tar);
                    break;
                }
            }
            if (!found)
                throw new ParserFormatException("Failed to locate 'aro.obo' in archive '" + CARDUpdater.FILE_NAME_ONTOLOGY + "'");
        }
    }

    private void parseAROStream(final InputStream ontologyStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ontologyStream, StandardCharsets.UTF_8))) {
            final Map<String, AROTerm> aroTerms = new LinkedHashMap<>();
            AROTerm currentTerm = null;
            boolean inTermSection = false;
            String line;
            while ((line = reader.readLine()) != null) {
                final String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) {
                    currentTerm = flushAROCurrentTerm(aroTerms, currentTerm, inTermSection);
                    inTermSection = false;
                    continue;
                }
                if (trimmedLine.startsWith("[")) {
                    currentTerm = flushAROCurrentTerm(aroTerms, currentTerm, inTermSection);
                    inTermSection = "[Term]".equals(trimmedLine);
                    if (inTermSection)
                        currentTerm = new AROTerm();
                    else
                        currentTerm = null;
                    continue;
                }
                if (!inTermSection || currentTerm == null)
                    continue;
                parseAROTermLine(currentTerm, trimmedLine);
            }
            flushAROCurrentTerm(aroTerms, currentTerm, inTermSection);
            dataSource.aroTerms = aroTerms;
        }
    }

    private AROTerm flushAROCurrentTerm(final Map<String, AROTerm> aroTerms, final AROTerm currentTerm,
                                        final boolean inTermSection) {
        if (!inTermSection || currentTerm == null)
            return null;
        if (StringUtils.isNotBlank(currentTerm.id)) {
            final AROTerm existingTerm = aroTerms.get(currentTerm.id);
            if (existingTerm == null)
                aroTerms.put(currentTerm.id, currentTerm);
            else
                existingTerm.mergeFrom(currentTerm);
        }
        return null;
    }

    private void parseAROTermLine(final AROTerm term, final String line) {
        if (line.startsWith("id:")) {
            term.id = parseSingleValue(line);
            return;
        }
        if (line.startsWith("name:")) {
            term.name = parseSingleValue(line);
            return;
        }
        if (line.startsWith("namespace:")) {
            term.namespace = parseSingleValue(line);
            return;
        }
        if (line.startsWith("def:")) {
            term.def = parseDefinitionValue(line);
            return;
        }
        if (line.startsWith("is_a:")) {
            addUniqueValue(term.isA, parseIsAValue(line));
            return;
        }
        if (line.startsWith("synonym:")) {
            addUniqueValue(term.synonyms, parseEntryValue(line));
            return;
        }
        if (line.startsWith("xref:")) {
            addUniqueValue(term.xrefs, parseEntryValue(line));
        }
    }

    private String parseSingleValue(final String line) {
        final String value = parseEntryValue(line);
        return StringUtils.isBlank(value) ? null : value;
    }

    private String parseDefinitionValue(final String line) {
        final String value = parseEntryValue(line);
        if (StringUtils.isBlank(value))
            return null;
        final Matcher matcher = QUOTED_STRING_PATTERN.matcher(value);
        if (matcher.find())
            return StringUtils.strip(matcher.group(), "\"");
        return value;
    }

    private String parseIsAValue(final String line) {
        final String value = parseEntryValue(line);
        if (StringUtils.isBlank(value))
            return null;
        return StringUtils.split(value, " ", 2)[0];
    }

    private String parseEntryValue(final String line) {
        final int separatorIndex = line.indexOf(':');
        if (separatorIndex == -1 || separatorIndex + 1 >= line.length())
            return null;
        final String value = removeComments(line.substring(separatorIndex + 1).trim());
        return value == null ? null : value.trim();
    }

    private String removeComments(final String value) {
        if (StringUtils.isBlank(value))
            return value;
        int commentIndex = StringUtils.indexOfIgnoreCase(value, "!");
        if (commentIndex == -1)
            return value;
        final Matcher matcher = QUOTED_STRING_PATTERN.matcher(value);
        while (matcher.find() && commentIndex != -1) {
            if (commentIndex < matcher.start())
                return value.substring(0, commentIndex);
            commentIndex = StringUtils.indexOfIgnoreCase(value, "!", matcher.end());
        }
        return commentIndex == -1 ? value : value.substring(0, commentIndex);
    }

    private void addUniqueValue(final List<String> values, final String value) {
        if (StringUtils.isBlank(value) || values.contains(value))
            return;
        values.add(value);
    }

    private void storeResults(final CARDDataSource dataSource, final List<Entry> results) {
        dataSource.entries = results.stream().filter(e -> StringUtils.isNotEmpty(e.modelId)).collect(Collectors.toList());
    }
}
