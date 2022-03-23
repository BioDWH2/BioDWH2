package de.unibi.agbi.biodwh2.intact.etl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.mixml.*;
import de.unibi.agbi.biodwh2.core.model.DataSourceFileType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.intact.IntActDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
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
        graph.addIndex(IndexDescription.forNode("BioSource", "ncbi_tax_id", false, IndexDescription.Type.UNIQUE));
        exportEntrySets(workspace, graph);
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
            while ((zipEntry = stream.getNextEntry()) != null) {
                if (zipEntry.getName().startsWith("human_9606_") && zipEntry.getName().endsWith(".xml")) {
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("Exporting '" + zipEntry.getName() + "'...");
                    exportEntrySet(graph, xmlMapper.readValue(stream, EntrySet.class));
                    break;
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + IntActUpdater.HUMAN_FILE_NAME + "'", e);
        }
    }

    private void exportEntrySet(final Graph graph, final EntrySet entrySet) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        for (final Entry entry : entrySet.entries) {
            try {
                graph.buildNode().withLabel("Entry").withProperty("source",
                                                                  objectMapper.writeValueAsString(entry.source))
                     .withProperty("attributeList", objectMapper.writeValueAsString(entry.attributeList)).withProperty(
                             "availabilityList", objectMapper.writeValueAsString(entry.availabilityList)).withProperty(
                             "interactorList", objectMapper.writeValueAsString(entry.interactorList)).withProperty(
                             "interactionList", objectMapper.writeValueAsString(entry.interactionList)).withProperty(
                             "experimentList", objectMapper.writeValueAsString(entry.experimentList)).build();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            for (final Interactor interactor : entry.interactorList) {
                getOrCreateBioSource(graph, interactor.organism);
            }
        }
    }

    private Node getOrCreateBioSource(final Graph graph, final BioSource bioSource) {
        if (bioSource.cellType != null)
            System.out.println(bioSource.cellType);
        Node node = graph.findNode("BioSource", "ncbi_tax_id", bioSource.ncbiTaxId);
        if (node == null) {
            // TODO: more properties
            node = graph.addNode("BioSource", "ncbi_tax_id", bioSource.ncbiTaxId);
        }
        return node;
    }
}
