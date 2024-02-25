package de.unibi.agbi.biodwh2.intact.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MIGraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.intact.IntActDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IntActGraphExporter extends MIGraphExporter<IntActDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(IntActGraphExporter.class);

    public IntActGraphExporter(final IntActDataSource dataSource) {
        super(dataSource, MIFormat.Xml);
    }

    @Override
    protected void exportFiles(final Workspace workspace,
                               final ExportCallback<InputStream> callback) throws IOException {
        final var filePath = dataSource.resolveSourceFilePath(workspace, IntActUpdater.HUMAN_FILE_NAME).toFile();
        if (!filePath.exists())
            throw new ExporterException("Failed to find file '" + IntActUpdater.HUMAN_FILE_NAME + "'");
        final ZipInputStream stream = FileUtils.openZip(workspace, dataSource, IntActUpdater.HUMAN_FILE_NAME);
        ZipEntry zipEntry;
        while ((zipEntry = stream.getNextEntry()) != null) {
            if (zipEntry.getName().startsWith("human_9606_") && zipEntry.getName().endsWith(".xml")) {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Exporting '" + zipEntry.getName() + "'...");
                callback.accept(stream);
            }
        }
    }
}
