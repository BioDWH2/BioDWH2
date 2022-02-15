package de.unibi.agbi.biodwh2.intact.etl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.mixml.EntrySet;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.intact.IntActDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IntActGraphExporter extends GraphExporter<IntActDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntActGraphExporter.class);

    public IntActGraphExporter(final IntActDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        exportEntrySets(workspace, graph);
        System.exit(0);
        return true;
    }

    private void exportEntrySets(final Workspace workspace, final Graph graph) {
        final String filePath = dataSource.resolveSourceFilePath(workspace, IntActUpdater.HUMAN_FILE_NAME);
        final File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ExporterException("Failed to find file '" + IntActUpdater.HUMAN_FILE_NAME + "'");
        try {
            final ZipInputStream stream = FileUtils.openZip(workspace, dataSource, IntActUpdater.HUMAN_FILE_NAME);
            ZipEntry zipEntry;
            final XmlMapper xmlMapper = XmlMapper.builder().disable(JsonParser.Feature.AUTO_CLOSE_SOURCE).build();
            // TODO: check negative before parsing
            while ((zipEntry = stream.getNextEntry()) != null) {
                if (zipEntry.getName().startsWith("human_9606_") && !zipEntry.getName().contains("_negative")) {
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("Exporting '" + zipEntry.getName() + "'...");
                    exportEntrySet(graph, xmlMapper.readValue(stream, EntrySet.class));
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + IntActUpdater.HUMAN_FILE_NAME + "'", e);
        }
    }

    private void exportEntrySet(final Graph graph, final EntrySet entrySet) {
        // TODO
        return;
    }
}
