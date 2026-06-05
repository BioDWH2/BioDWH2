package de.unibi.agbi.biodwh2.card.etl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.card.CARDDataSource;
import de.unibi.agbi.biodwh2.card.model.AROTerm;
import de.unibi.agbi.biodwh2.card.model.CARD_Model;
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

        final List<CARD_Model> card_results = new ArrayList<>();

        // Filter out metadata fields (keys starting with _) and deserialize only model entries
        for (final Map.Entry<String, Object> entry : rawData.entrySet()) {
            final String key = entry.getKey();

            if (!key.startsWith("_") && entry.getValue() instanceof Map<?, ?>)
                card_results.add(mapper.convertValue(entry.getValue(), CARD_Model.class));
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
                    flushAROCurrentTerm(aroTerms, currentTerm, inTermSection);
                    currentTerm = null;
                    inTermSection = false;
                    continue;
                }
                if (trimmedLine.startsWith("[")) {
                    flushAROCurrentTerm(aroTerms, currentTerm, inTermSection);
                    currentTerm = null;
                    inTermSection = "[Term]".equals(trimmedLine);
                    if (inTermSection)
                        currentTerm = new AROTerm();
                    continue;
                }
                if (!inTermSection)
                    continue;
                parseAROTermLine(currentTerm, trimmedLine);
            }
            flushAROCurrentTerm(aroTerms, currentTerm, inTermSection);
            dataSource.aroTerms = aroTerms;
        }
    }

    private void flushAROCurrentTerm(final Map<String, AROTerm> aroTerms, final AROTerm currentTerm,
                                     final boolean inTermSection) {
        if (!inTermSection || currentTerm == null)
            return;
        if (StringUtils.isNotBlank(currentTerm.id)) {
            final AROTerm existingTerm = aroTerms.get(currentTerm.id);
            if (existingTerm == null) {
                aroTerms.put(currentTerm.id, currentTerm);
            } else {
                // Merge non-null scalar fields from currentTerm into existingTerm if missing
                if (existingTerm.id == null)
                    existingTerm.id = currentTerm.id;
                if (existingTerm.name == null)
                    existingTerm.name = currentTerm.name;
                if (existingTerm.namespace == null)
                    existingTerm.namespace = currentTerm.namespace;
                if (existingTerm.def == null)
                    existingTerm.def = currentTerm.def;
                if (existingTerm.categoryAroAccession == null)
                    existingTerm.categoryAroAccession = currentTerm.categoryAroAccession;
                // Merge lists uniquely
                for (final String v : currentTerm.isA) {
                    if (v != null && !existingTerm.isA.contains(v))
                        existingTerm.isA.add(v);
                }
                for (final String v : currentTerm.synonyms) {
                    if (v != null && !existingTerm.synonyms.contains(v))
                        existingTerm.synonyms.add(v);
                }
                for (final String v : currentTerm.xrefs) {
                    if (v != null && !existingTerm.xrefs.contains(v))
                        existingTerm.xrefs.add(v);
                }
                // Merge relationships uniquely by name+targetId
                relationshipLoop:
                for (final AROTerm.Relationship r : currentTerm.relationships) {
                    if (r == null || StringUtils.isBlank(r.name) || StringUtils.isBlank(r.targetId))
                        continue;
                    for (final AROTerm.Relationship existingRel : existingTerm.relationships) {
                        if (existingRel == null)
                            continue;
                        if (StringUtils.equals(existingRel.name, r.name) && StringUtils.equals(existingRel.targetId, r.targetId))
                            continue relationshipLoop;
                    }
                    existingTerm.relationships.add(r);
                }
            }
        }
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
        if (line.startsWith("category_aro_accession:")) {
            term.categoryAroAccession = parseSingleValue(line);
            return;
        }
        if (line.startsWith("is_a:")) {
            addUniqueValue(term.isA, parseIsAValue(line));
            return;
        }
        if (line.startsWith("relationship:")) {
            addUniqueRelationship(term.relationships, parseRelationshipValue(line));
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

    private AROTerm.Relationship parseRelationshipValue(final String line) {
        final String value = parseEntryValue(line);
        if (StringUtils.isBlank(value))
            return null;
        final String[] parts = StringUtils.split(value, " ", 2);
        if (parts.length < 2 || StringUtils.isBlank(parts[0]) || StringUtils.isBlank(parts[1]))
            return null;
        final String targetId = StringUtils.split(parts[1], " ", 2)[0];
        if (StringUtils.isBlank(targetId))
            return null;
        final AROTerm.Relationship relationship = new AROTerm.Relationship();
        relationship.name = parts[0];
        relationship.targetId = targetId;
        return relationship;
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

    private void addUniqueRelationship(final List<AROTerm.Relationship> relationships,
                                       final AROTerm.Relationship relationship) {
        if (relationship == null || StringUtils.isBlank(relationship.name) || StringUtils.isBlank(relationship.targetId))
            return;
        for (final AROTerm.Relationship existing : relationships) {
            if (existing == null)
                continue;
            if (StringUtils.equals(existing.name, relationship.name) && StringUtils.equals(existing.targetId, relationship.targetId))
                return;
        }
        relationships.add(relationship);
    }

    private void storeResults(final CARDDataSource dataSource, final List<CARD_Model> results) {
        dataSource.model_entries = results.stream().filter(e -> StringUtils.isNotEmpty(e.modelId)).collect(Collectors.toList());
    }
}
