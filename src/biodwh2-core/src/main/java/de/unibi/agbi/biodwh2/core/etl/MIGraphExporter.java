package de.unibi.agbi.biodwh2.core.etl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.mixml.*;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import org.apache.commons.lang3.StringUtils;
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
    private static final String ORGANISM_LABEL = "Organism";
    private static final String NCBI_TAX_ID_KEY = "ncbi_tax_id";

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
            graph.addIndex(IndexDescription.forNode(ORGANISM_LABEL, NCBI_TAX_ID_KEY, IndexDescription.Type.UNIQUE));
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
        for (final Entry entry : entrySet.entries) {
            if (entry.interactorList != null) {
                for (final Interactor interactor : entry.interactorList) {
                    // TODO
                    getOrCreateBioSource(graph, interactor.organism);
                }
            }
            if (entry.interactionList != null) {
                if (entry.interactionList.interaction != null) {
                    for (final Interaction interaction : entry.interactionList.interaction) {
                        // TODO
                    }
                }
                if (entry.interactionList.abstractInteraction != null) {
                    for (final AbstractInteraction interaction : entry.interactionList.abstractInteraction) {
                        // TODO
                        getOrCreateBioSource(graph, interaction.organism);
                    }
                }
            }
            if (entry.experimentList != null) {
                for (final ExperimentDescription experiment : entry.experimentList) {
                    final NodeBuilder builder = graph.buildNode().withLabel("Experiment");
                    builder.withProperty(ID_KEY, experiment.id);
                    if (experiment.names != null) {
                        if (StringUtils.isNotEmpty(experiment.names.fullName))
                            builder.withProperty("full_name", experiment.names.fullName);
                        if (StringUtils.isNotEmpty(experiment.names.shortLabel))
                            builder.withProperty("short_label", experiment.names.shortLabel);
                        // TODO: alias
                    }
                    final Node experimentNode = builder.build();
                    // TODO
                    if (experiment.hostOrganismList != null) {
                        for (final HostOrganism organism : experiment.hostOrganismList) {
                            // TODO: experimentRefList
                            getOrCreateBioSource(graph, organism);
                        }
                    }
                }
            }
        }
    }

    private void getOrCreateBioSource(final Graph graph, final BioSource bioSource) {
        // TODO: names, cellType, compartment, tissue
        if (bioSource.ncbiTaxId >= 0) {
            final Long organismNodeId = getOrCreateOrganismNode(graph, bioSource.ncbiTaxId, bioSource.names);
        } else {
            // TODO
        }
    }

    private Long getOrCreateOrganismNode(final Graph graph, final Integer ncbiTaxId, final Names names) {
        Node node = graph.findNode(ORGANISM_LABEL, NCBI_TAX_ID_KEY, ncbiTaxId);
        if (node == null) {
            final NodeBuilder builder = graph.buildNode().withLabel(ORGANISM_LABEL);
            builder.withProperty(NCBI_TAX_ID_KEY, ncbiTaxId);
            if (names != null) {
                if (StringUtils.isNotEmpty(names.fullName))
                    builder.withProperty("full_name", names.fullName);
                if (StringUtils.isNotEmpty(names.shortLabel))
                    builder.withProperty("short_label", names.shortLabel);
                // TODO: alias
            }
            node = builder.build();
        }
        return node.getId();
    }
}
