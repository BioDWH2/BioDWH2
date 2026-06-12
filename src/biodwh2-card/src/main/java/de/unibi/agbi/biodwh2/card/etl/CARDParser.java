package de.unibi.agbi.biodwh2.card.etl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.card.CARDDataSource;
import de.unibi.agbi.biodwh2.card.model.CARD_Model;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
            parseCARDArchive(workspace);
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

    private void storeResults(final CARDDataSource dataSource, final List<CARD_Model> results) {
        dataSource.model_entries = results.stream().filter(e -> StringUtils.isNotEmpty(e.modelId)).collect(Collectors.toList());
    }
}