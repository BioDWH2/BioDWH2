package de.unibi.agbi.biodwh2.core.etl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.mitab.PsiMiTab25Entry;
import de.unibi.agbi.biodwh2.core.io.mitab.PsiMiTab26Entry;
import de.unibi.agbi.biodwh2.core.io.mitab.PsiMiTab27Entry;
import de.unibi.agbi.biodwh2.core.io.mitab.PsiMiTab28Entry;
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
    public static final String INTERACTOR_LABEL = "Interactor";
    public static final String INTERACTION_LABEL = "Interaction";
    public static final String PUBLICATION_LABEL = "Publication";
    public static final String ORGANISM_LABEL = "Organism";
    public static final String INTERACTS_LABEL = "INTERACTS";
    public static final String NCBI_TAX_ID_KEY = "ncbi_tax_id";

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
        graph.addIndex(IndexDescription.forNode(INTERACTOR_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ORGANISM_LABEL, NCBI_TAX_ID_KEY, IndexDescription.Type.UNIQUE));
        if (format == MIFormat.Xml) {
            try {
                final var xmlMapper = XmlMapper.builder().disable(JsonParser.Feature.AUTO_CLOSE_SOURCE).build();
                exportFiles(workspace, (s -> exportEntrySet(graph, xmlMapper.readValue(s, EntrySet.class))));
            } catch (IOException e) {
                throw new ExporterException("Failed to export PSI-MI xml file", e);
            }
        } else {
            try {
                exportFiles(workspace, (s -> exportTabFile(graph, s)));
            } catch (IOException e) {
                throw new ExporterException("Failed to export PSI-MI tab file", e);
            }
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

    private void exportTabFile(final Graph graph, final InputStream stream) throws IOException {
        graph.beginEdgeIndicesDelay(INTERACTS_LABEL);
        if (format == MIFormat.Tab25)
            FileUtils.openTsv(stream, PsiMiTab25Entry.class, (entry) -> exportTabFileEntry25(graph, entry));
        else if (format == MIFormat.Tab26)
            FileUtils.openTsv(stream, PsiMiTab26Entry.class, (entry) -> exportTabFileEntry26(graph, entry));
        else if (format == MIFormat.Tab27)
            FileUtils.openTsv(stream, PsiMiTab27Entry.class, (entry) -> exportTabFileEntry27(graph, entry));
        else if (format == MIFormat.Tab28)
            FileUtils.openTsv(stream, PsiMiTab28Entry.class, (entry) -> exportTabFileEntry28(graph, entry));
        graph.endEdgeIndicesDelay(INTERACTS_LABEL);
    }

    private void exportTabFileEntry25(final Graph graph, final PsiMiTab25Entry entry) {
        final Long interactorANodeId = getOrCreateInteractorNode(graph, entry.interactorIdentifierA,
                                                                 entry.interactorAlternativeIdentifierA,
                                                                 entry.interactorAliasesA,
                                                                 entry.interactorNCBITaxonomyIdentifierA, null, null);
        final Long interactorBNodeId = getOrCreateInteractorNode(graph, entry.interactorIdentifierB,
                                                                 entry.interactorAlternativeIdentifierB,
                                                                 entry.interactorAliasesB,
                                                                 entry.interactorNCBITaxonomyIdentifierB, null, null);
        final var builder = graph.buildNode(INTERACTION_LABEL);
        withArrayPropertyIfNotEmpty(builder, "detection_methods", entry.interactionDetectionMethods);
        withArrayPropertyIfNotEmpty(builder, "types", entry.interactionTypes);
        withArrayPropertyIfNotEmpty(builder, "first_author", entry.firstAuthor);
        withArrayPropertyIfNotEmpty(builder, "source_databases", entry.sourceDatabases);
        withArrayPropertyIfNotEmpty(builder, "source_database_ids", entry.interactionIdentifiers);
        withArrayPropertyIfNotEmpty(builder, "confidence_scores", entry.confidenceScore);
        final Node interactionNode = builder.build();
        if (isColumnNotEmpty(entry.publicationIdentifier)) {
            for (final var publicationId : StringUtils.split(entry.publicationIdentifier, '|')) {
                final var publicationNodeId = getOrCreatePublicationNode(graph, publicationId);
                graph.addEdge(interactionNode, publicationNodeId, "REFERENCES");
            }
        }
        graph.addEdge(interactorANodeId, interactionNode, INTERACTS_LABEL);
        graph.addEdge(interactorBNodeId, interactionNode, INTERACTS_LABEL);
    }

    private Long getOrCreateInteractorNode(final Graph graph, final String id, final String alternativeIds,
                                           final String aliases, final String ncbiTaxonomyId, final String type,
                                           final String xrefs) {
        Node node = graph.findNode(INTERACTOR_LABEL, ID_KEY, id);
        if (node == null) {
            final var builder = graph.buildNode(INTERACTOR_LABEL).withProperty(ID_KEY, id);
            withArrayPropertyIfNotEmpty(builder, "alternative_ids", alternativeIds);
            withArrayPropertyIfNotEmpty(builder, "aliases", aliases);
            withArrayPropertyIfNotEmpty(builder, "types", type);
            withArrayPropertyIfNotEmpty(builder, "xrefs", xrefs);
            node = builder.build();
            // TODO: ncbiTaxonomyId
        }
        return node.getId();
    }

    private boolean isColumnNotEmpty(final String value) {
        return StringUtils.isNotEmpty(value) && !"-".equals(value);
    }

    private void withArrayPropertyIfNotEmpty(final NodeBuilder builder, final String key, final String value) {
        if (isColumnNotEmpty(value))
            builder.withProperty(key, StringUtils.split(value, '|'));
    }

    private Long getOrCreatePublicationNode(final Graph graph, final String id) {
        Node node = graph.findNode(PUBLICATION_LABEL, ID_KEY, id);
        if (node == null)
            node = graph.addNode(PUBLICATION_LABEL, ID_KEY, id);
        return node.getId();
    }

    private void exportTabFileEntry26(final Graph graph, final PsiMiTab26Entry entry) {
        final Long interactorANodeId = getOrCreateInteractorNode(graph, entry.interactorIdentifierA,
                                                                 entry.interactorAlternativeIdentifierA,
                                                                 entry.interactorAliasesA,
                                                                 entry.interactorNCBITaxonomyIdentifierA,
                                                                 entry.interactorTypeA, entry.interactorXrefA);
        final Long interactorBNodeId = getOrCreateInteractorNode(graph, entry.interactorIdentifierB,
                                                                 entry.interactorAlternativeIdentifierB,
                                                                 entry.interactorAliasesB,
                                                                 entry.interactorNCBITaxonomyIdentifierB,
                                                                 entry.interactorTypeB, entry.interactorXrefB);
        // TODO:
        //  interactorChecksum
        final var builder = graph.buildNode(INTERACTION_LABEL);
        // TODO:
        //  expansion
        //  biologicalRoleA
        //  biologicalRoleB
        //  experimentalRoleA
        //  experimentalRoleB
        //  interactionXref
        //  interactionAnnotations
        //  hostOrganismNCBITaxonomyIdentifier
        //  interactionParameters
        //  interactionChecksum
        withArrayPropertyIfNotEmpty(builder, "detection_methods", entry.interactionDetectionMethods);
        withArrayPropertyIfNotEmpty(builder, "types", entry.interactionTypes);
        withArrayPropertyIfNotEmpty(builder, "first_author", entry.firstAuthor);
        withArrayPropertyIfNotEmpty(builder, "source_databases", entry.sourceDatabases);
        withArrayPropertyIfNotEmpty(builder, "source_database_ids", entry.interactionIdentifiers);
        withArrayPropertyIfNotEmpty(builder, "confidence_scores", entry.confidenceScore);
        if (isColumnNotEmpty(entry.creationDate))
            builder.withProperty("creation_date", entry.creationDate);
        if (isColumnNotEmpty(entry.updateDate))
            builder.withProperty("update_date", entry.updateDate);
        if ("true".equalsIgnoreCase(entry.negative))
            builder.withProperty("negative", true);
        final Node interactionNode = builder.build();
        if (isColumnNotEmpty(entry.publicationIdentifier)) {
            for (final var publicationId : StringUtils.split(entry.publicationIdentifier, '|')) {
                final var publicationNodeId = getOrCreatePublicationNode(graph, publicationId);
                graph.addEdge(interactionNode, publicationNodeId, "REFERENCES");
            }
        }
        graph.addEdge(interactorANodeId, interactionNode, INTERACTS_LABEL);
        graph.addEdge(interactorBNodeId, interactionNode, INTERACTS_LABEL);
    }

    private void exportTabFileEntry27(final Graph graph, final PsiMiTab27Entry entry) {
        final Long interactorANodeId = getOrCreateInteractorNode(graph, entry.interactorIdentifierA,
                                                                 entry.interactorAlternativeIdentifierA,
                                                                 entry.interactorAliasesA,
                                                                 entry.interactorNCBITaxonomyIdentifierA,
                                                                 entry.interactorTypeA, entry.interactorXrefA);
        final Long interactorBNodeId = getOrCreateInteractorNode(graph, entry.interactorIdentifierB,
                                                                 entry.interactorAlternativeIdentifierB,
                                                                 entry.interactorAliasesB,
                                                                 entry.interactorNCBITaxonomyIdentifierB,
                                                                 entry.interactorTypeB, entry.interactorXrefB);
        // TODO:
        //  interactorChecksum
        //  interactorAnnotations
        //  interactorFeatures
        //  interactorParticipantIdentificationMethod
        //  interactorStoichiometry
        final var builder = graph.buildNode(INTERACTION_LABEL);
        // TODO:
        //  complexExpansion
        //  biologicalRoleA
        //  biologicalRoleB
        //  experimentalRoleA
        //  experimentalRoleB
        //  interactionXref
        //  interactionAnnotations
        //  hostOrganismNCBITaxonomyIdentifier
        //  interactionParameters
        //  interactionChecksum
        withArrayPropertyIfNotEmpty(builder, "detection_methods", entry.interactionDetectionMethods);
        withArrayPropertyIfNotEmpty(builder, "types", entry.interactionTypes);
        withArrayPropertyIfNotEmpty(builder, "first_author", entry.firstAuthor);
        withArrayPropertyIfNotEmpty(builder, "source_databases", entry.sourceDatabases);
        withArrayPropertyIfNotEmpty(builder, "source_database_ids", entry.interactionIdentifiers);
        withArrayPropertyIfNotEmpty(builder, "confidence_scores", entry.confidenceScore);
        if (isColumnNotEmpty(entry.creationDate))
            builder.withProperty("creation_date", entry.creationDate);
        if (isColumnNotEmpty(entry.updateDate))
            builder.withProperty("update_date", entry.updateDate);
        if ("true".equalsIgnoreCase(entry.negative))
            builder.withProperty("negative", true);
        final Node interactionNode = builder.build();
        if (isColumnNotEmpty(entry.publicationIdentifier)) {
            for (final var publicationId : StringUtils.split(entry.publicationIdentifier, '|')) {
                final var publicationNodeId = getOrCreatePublicationNode(graph, publicationId);
                graph.addEdge(interactionNode, publicationNodeId, "REFERENCES");
            }
        }
        graph.addEdge(interactorANodeId, interactionNode, INTERACTS_LABEL);
        graph.addEdge(interactorBNodeId, interactionNode, INTERACTS_LABEL);
    }

    private void exportTabFileEntry28(final Graph graph, final PsiMiTab28Entry entry) {
        final Long interactorANodeId = getOrCreateInteractorNode(graph, entry.interactorIdentifierA,
                                                                 entry.interactorAlternativeIdentifierA,
                                                                 entry.interactorAliasesA,
                                                                 entry.interactorNCBITaxonomyIdentifierA,
                                                                 entry.interactorTypeA, entry.interactorXrefA);
        final Long interactorBNodeId = getOrCreateInteractorNode(graph, entry.interactorIdentifierB,
                                                                 entry.interactorAlternativeIdentifierB,
                                                                 entry.interactorAliasesB,
                                                                 entry.interactorNCBITaxonomyIdentifierB,
                                                                 entry.interactorTypeB, entry.interactorXrefB);
        // TODO:
        //  interactorChecksum
        //  interactorAnnotations
        //  interactorFeatures
        //  interactorParticipantIdentificationMethod
        //  interactorStoichiometry
        //  interactorBiologicalEffect
        final var builder = graph.buildNode(INTERACTION_LABEL);
        // TODO:
        //  complexExpansion
        //  biologicalRoleA
        //  biologicalRoleB
        //  experimentalRoleA
        //  experimentalRoleB
        //  interactionXref
        //  interactionAnnotations
        //  hostOrganismNCBITaxonomyIdentifier
        //  interactionParameters
        //  interactionChecksum
        //  causalRegulatoryMechanism
        //  causalStatement
        withArrayPropertyIfNotEmpty(builder, "detection_methods", entry.interactionDetectionMethods);
        withArrayPropertyIfNotEmpty(builder, "types", entry.interactionTypes);
        withArrayPropertyIfNotEmpty(builder, "first_author", entry.firstAuthor);
        withArrayPropertyIfNotEmpty(builder, "source_databases", entry.sourceDatabases);
        withArrayPropertyIfNotEmpty(builder, "source_database_ids", entry.interactionIdentifiers);
        withArrayPropertyIfNotEmpty(builder, "confidence_scores", entry.confidenceScore);
        if (isColumnNotEmpty(entry.creationDate))
            builder.withProperty("creation_date", entry.creationDate);
        if (isColumnNotEmpty(entry.updateDate))
            builder.withProperty("update_date", entry.updateDate);
        if ("true".equalsIgnoreCase(entry.negative))
            builder.withProperty("negative", true);
        final Node interactionNode = builder.build();
        if (isColumnNotEmpty(entry.publicationIdentifier)) {
            for (final var publicationId : StringUtils.split(entry.publicationIdentifier, '|')) {
                final var publicationNodeId = getOrCreatePublicationNode(graph, publicationId);
                graph.addEdge(interactionNode, publicationNodeId, "REFERENCES");
            }
        }
        graph.addEdge(interactorANodeId, interactionNode, INTERACTS_LABEL);
        graph.addEdge(interactorBNodeId, interactionNode, INTERACTS_LABEL);
    }
}
