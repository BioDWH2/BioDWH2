package de.unibi.agbi.biodwh2.intact.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MIGraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.intact.IntActDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;

import static de.unibi.agbi.biodwh2.intact.etl.IntActUpdater.SPECIES_TAX_ID_FILE_NAME;

public class IntActGraphExporter extends MIGraphExporter<IntActDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(IntActGraphExporter.class);

    public IntActGraphExporter(final IntActDataSource dataSource) {
        super(dataSource, MIFormat.Xml);
    }

    @Override
    protected void exportFiles(final Workspace workspace, final ExportCallback<InputStream> callback) {
        for (final var entry : SPECIES_TAX_ID_FILE_NAME.entrySet()) {
            if (speciesFilter.isSpeciesAllowed(entry.getKey())) {
                final var filePath = dataSource.resolveSourceFilePath(workspace, entry.getValue()).toFile();
                if (!filePath.exists())
                    throw new ExporterException("Failed to find file '" + entry.getValue() + "'");
                try {
                    FileUtils.forEachZipEntry(workspace, dataSource, entry.getValue(), ".xml", (stream, zipEntry) -> {
                        if (LOGGER.isInfoEnabled())
                            LOGGER.info("Exporting '{}'...", zipEntry.getName());
                        callback.accept(stream);

                    });
                } catch (Exception e) {
                    throw new ExporterException("Failed to export file '" + entry.getValue() + "'", e);
                }
            }
        }
    }
}
