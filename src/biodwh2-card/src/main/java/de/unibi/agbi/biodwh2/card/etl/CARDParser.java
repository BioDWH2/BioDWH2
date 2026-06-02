package de.unibi.agbi.biodwh2.card.etl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.card.CARDDataSource;
import de.unibi.agbi.biodwh2.card.model.Entry;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class CARDParser extends Parser<CARDDataSource> {
    public CARDParser(final CARDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        try {
            // Prefer a plain card.json in the source folder if present (useful for local testing)
            final Path plainJson = dataSource.resolveSourceFilePath(workspace, "card.json");
            if (Files.exists(plainJson)) {
                try (InputStream stream = FileUtils.openInput(plainJson)) {
                    parseStream(stream);
                }
                return true;
            }

            // Otherwise try to read card.json from the downloaded tar.bz2 archive
            try (InputStream fileStream = FileUtils.openInput(workspace, dataSource, CARDUpdater.FILE_NAME);
                 BufferedInputStream buffered = new BufferedInputStream(fileStream);
                 BZip2CompressorInputStream bzip2 = new BZip2CompressorInputStream(buffered);
                 TarArchiveInputStream tar = new TarArchiveInputStream(bzip2)) {
                ArchiveEntry entry;
                while ((entry = tar.getNextEntry()) != null) {
                    if (!entry.isDirectory() && entry.getName().endsWith("card.json")) {
                        parseStream(tar);
                        break;
                    }
                }
            }
            return true;
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse CARD data", e);
        }
    }

    private void parseStream(final InputStream stream) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        // First, read the entire JSON as a generic map to handle mixed content (models + metadata)
        final Map<String, Object> rawData = mapper.readValue(stream,
                mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));

        final List<Entry> results = new ArrayList<>();

        // Filter out metadata fields (keys starting with _) and deserialize only model entries
        for (final Map.Entry<String, Object> entry : rawData.entrySet()) {
            final String key = entry.getKey();

            // Skip metadata fields that start with underscore (e.g., _version, _comment, _timestamp)
            if (key.startsWith("_")) {
                continue;
            }

            // Deserialize only valid model entries
            try {
                final Entry cardEntry = mapper.convertValue(entry.getValue(), Entry.class);
                results.add(cardEntry);
            } catch (final Exception e) {
                // Skip entries that cannot be deserialized as CARD models
                continue;
            }
        }

        storeResults(dataSource, results);
    }

    private void storeResults(final CARDDataSource dataSource, final List<Entry> results) {
        dataSource.entries = results.stream().filter(e -> StringUtils.isNotEmpty(e.modelId)).collect(Collectors.toList());
    }
}
