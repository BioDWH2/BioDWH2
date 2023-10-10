package de.unibi.agbi.biodwh2.core.etl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.mixml.BioSource;
import de.unibi.agbi.biodwh2.core.io.mixml.Entry;
import de.unibi.agbi.biodwh2.core.io.mixml.EntrySet;
import de.unibi.agbi.biodwh2.core.io.mixml.Interactor;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

public abstract class MIGraphExporter<D extends DataSource> extends GraphExporter<D> {
    protected enum MIFormat {
        Xml,
        Tab25,
        Tab26,
        Tab27,
        Tab28
    }

    @FunctionalInterface
    public interface ExportCallback<T> {
        void accept(T t) throws IOException;
    }

    private static final Logger LOGGER = LogManager.getLogger(MIGraphExporter.class);

    private final MIFormat format;

    protected MIGraphExporter(final D dataSource, final MIFormat format) {
        super(dataSource);
        this.format = format;
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        if (format == MIFormat.Xml) {
            graph.addIndex(IndexDescription.forNode("BioSource", "ncbi_tax_id", false, IndexDescription.Type.UNIQUE));
            try {
                final XmlMapper xmlMapper = XmlMapper.builder().disable(JsonParser.Feature.AUTO_CLOSE_SOURCE).build();
                exportFiles(workspace, (s -> exportEntrySet(graph, xmlMapper.readValue(s, EntrySet.class))));
            } catch (IOException e) {
                throw new ExporterException("Failed to export PSI-MI xml file", e);
            }
        } else {
            // TODO
            throw new ExporterException("PSI-MI TAB export not implemented yet");
        }
        return true;
    }

    protected abstract void exportFiles(final Workspace workspace,
                                        final ExportCallback<InputStream> callback) throws IOException;

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
