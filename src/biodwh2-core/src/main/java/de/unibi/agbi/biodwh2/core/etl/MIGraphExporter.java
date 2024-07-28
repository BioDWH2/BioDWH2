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
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MIGraphExporter<D extends DataSource> extends GraphExporter<D> {
    protected enum MIFormat {
        Xml25,
        Xml30,
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
    public static final String PARTICIPATES_IN_LABEL = "PARTICIPATES_IN";
    public static final String NCBI_TAX_ID_KEY = "ncbi_tax_id";

    private final MIFormat format;
    private final Map<String, Long> sourceReleaseLabelNodeIdMap = new HashMap<>();

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
        sourceReleaseLabelNodeIdMap.clear();
        graph.addIndex(IndexDescription.forNode(INTERACTOR_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ORGANISM_LABEL, NCBI_TAX_ID_KEY, IndexDescription.Type.UNIQUE));
        if (format == MIFormat.Xml25 || format == MIFormat.Xml30) {
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
        for (final Entry entry : entrySet.entries)
            exportEntry(graph, entry);
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        // TODO: 2.5 vs. 3.0 switches
        // TODO:
        //  attributeList
        final Map<Integer, String> availabilityIdValueMap = new HashMap<>();
        if (entry.availabilityList != null)
            for (final var availability : entry.availabilityList)
                availabilityIdValueMap.put(availability.id, availability.value);
        exportSource(graph, entry.source);
        final Map<Integer, Long> experimentIdNodeIdMap = new HashMap<>();
        if (entry.experimentList != null)
            for (final ExperimentDescription experiment : entry.experimentList)
                exportExperiment(graph, experiment, experimentIdNodeIdMap);
        final Map<Integer, Long> interactorIdNodeIdMap = new HashMap<>();
        if (entry.interactorList != null)
            for (final Interactor interactor : entry.interactorList)
                exportInteractor(graph, interactor, interactorIdNodeIdMap);
        final Map<Integer, Long> interactionIdNodeIdMap = new HashMap<>();
        if (entry.interactionList != null) {
            if (entry.interactionList.interaction != null) {
                for (final Interaction interaction : entry.interactionList.interaction) {
                    final var builder = graph.buildNode().withLabel(INTERACTION_LABEL);
                    withNamesIfNotNull(builder, interaction.names);
                    withAttributesIfNotNull(builder, interaction.attributeList);
                    withXrefsIfNotNull(builder, interaction.xref);
                    // TODO:
                    //  inferredInteractionList
                    //  interactionType
                    //  confidenceList
                    //  parameterList
                    //  experimentalVariableValueList
                    //  causalRelationshipList
                    //  imexId
                    if (interaction.availabilityRef != null)
                        builder.withPropertyIfNotNull("availability",
                                                      availabilityIdValueMap.get(interaction.availabilityRef));
                    else if (interaction.availability != null)
                        builder.withPropertyIfNotNull("availability", interaction.availability.value);
                    if (interaction.modelled != null && interaction.modelled)
                        builder.withProperty("modelled", true);
                    if (interaction.intraMolecular != null && interaction.intraMolecular)
                        builder.withProperty("intra_molecular", true);
                    if (interaction.negative != null && interaction.negative)
                        builder.withProperty("negative", true);
                    final var interactionNode = builder.build();
                    interactionIdNodeIdMap.put(interaction.id, interactionNode.getId());
                    if (interaction.experimentList != null) {
                        if (interaction.experimentList.experimentRef != null) {
                            for (final var experimentId : interaction.experimentList.experimentRef) {
                                final var experimentNodeId = experimentIdNodeIdMap.get(experimentId);
                                graph.addEdge(interactionNode, experimentNodeId, "ASSOCIATED_WITH");
                            }
                        }
                        if (interaction.experimentList.experimentDescription != null) {
                            for (final var experiment : interaction.experimentList.experimentDescription) {
                                final var experimentNodeId = exportExperiment(graph, experiment, experimentIdNodeIdMap);
                                graph.addEdge(interactionNode, experimentNodeId, "ASSOCIATED_WITH");
                            }
                        }
                    }
                    if (interaction.participantList != null) {
                        for (final var participant : interaction.participantList) {
                            if (participant.interactorRef != null) {
                                final var interactorNodeId = interactorIdNodeIdMap.get(participant.interactorRef);
                                if (interactorNodeId == null)
                                    LOGGER.warn("Missing interactor ref {}", participant.interactorRef);
                                else
                                    graph.addEdge(interactorNodeId, interactionNode, PARTICIPATES_IN_LABEL);
                            } else if (participant.interactor != null) {
                                final var interactorNodeId = exportInteractor(graph, participant.interactor,
                                                                              interactorIdNodeIdMap);
                                graph.addEdge(interactorNodeId, interactionNode, PARTICIPATES_IN_LABEL);
                            } else if (participant.interactionRef != null) {
                                final var interactionNodeId = interactionIdNodeIdMap.get(participant.interactionRef);
                                if (interactionNodeId == null)
                                    LOGGER.warn("Missing interaction ref {}", participant.interactionRef);
                                else
                                    graph.addEdge(interactionNodeId, interactionNode, PARTICIPATES_IN_LABEL);
                            } else if (participant.interactorCandidateList != null) {
                                // TODO
                            }
                        }
                    }
                }
            }
            if (entry.interactionList.abstractInteraction != null) {
                for (final AbstractInteraction interaction : entry.interactionList.abstractInteraction) {
                    // TODO
                    getOrCreateBioSource(graph, interaction.organism);
                }
            }
        }
    }

    private Long exportExperiment(final Graph graph, final ExperimentDescription experiment,
                                  final Map<Integer, Long> experimentIdNodeIdMap) {
        final NodeBuilder builder = graph.buildNode().withLabel("Experiment");
        withNamesIfNotNull(builder, experiment.names);
        withAttributesIfNotNull(builder, experiment.attributeList);
        withXrefsIfNotNull(builder, experiment.xref);
        // TODO:
        //  bibref
        //  interactionDetectionMethod
        //  participantIdentificationMethod
        //  featureDetectionMethod
        //  confidenceList
        //  variableParameterList
        final Node experimentNode = builder.build();
        experimentIdNodeIdMap.put(experiment.id, experimentNode.getId());
        if (experiment.hostOrganismList != null) {
            for (final HostOrganism organism : experiment.hostOrganismList) {
                graph.addEdge(experimentNode, getOrCreateBioSource(graph, organism), "PERFORMED_IN");
            }
        }
        return experimentNode.getId();
    }

    private void exportSource(final Graph graph, final Source source) {
        final String key = source.names.shortLabel + "|" + source.release + "|" + source.releaseDate;
        if (sourceReleaseLabelNodeIdMap.containsKey(key))
            return;
        final var builder = graph.buildNode().withLabel("Source");
        builder.withPropertyIfNotNull("release", source.release);
        builder.withPropertyIfNotNull("release_date", source.releaseDate);
        withNamesIfNotNull(builder, source.names);
        withAttributesIfNotNull(builder, source.attributeList);
        withXrefsIfNotNull(builder, source.xref);
        // TODO: bibref
        final Long nodeId = builder.build().getId();
        sourceReleaseLabelNodeIdMap.put(key, nodeId);
    }

    private Long getOrCreateBioSource(final Graph graph, final BioSource bioSource) {
        // TODO: deduplicate
        final var builder = graph.buildNode().withLabel("BioSource");
        withNamesIfNotNull(builder, bioSource.names);
        withOpenCVTypeIfNotNull(builder, bioSource.cellType, "cell_type_");
        withOpenCVTypeIfNotNull(builder, bioSource.compartment, "compartment_");
        withOpenCVTypeIfNotNull(builder, bioSource.tissue, "tissue_");
        final var nodeId = builder.build().getId();
        if (bioSource.ncbiTaxId >= 0) {
            final Long taxonNodeId = getOrCreateOntologyProxyTerm(graph, "NCBITaxon:" + bioSource.ncbiTaxId);
            graph.addEdge(nodeId, taxonNodeId, "BELONGS_TO");
        }
        return nodeId;
    }

    private <T extends ModelBuilder<T>> void withOpenCVTypeIfNotNull(final T builder, final OpenCvType type,
                                                                     final String keyPrefix) {
        if (type != null) {
            withNamesIfNotNull(builder, type.names, keyPrefix);
            withAttributesIfNotNull(builder, type.attributeList, keyPrefix);
            withXrefsIfNotNull(builder, type.xref, keyPrefix);
        }
    }

    private <T extends ModelBuilder<T>> void withCVTypeIfNotNull(final T builder, final CvType type,
                                                                 final String keyPrefix) {
        if (type != null) {
            withNamesIfNotNull(builder, type.names, keyPrefix);
            withXrefsIfNotNull(builder, type.xref, keyPrefix);
        }
    }

    private <T extends ModelBuilder<T>> void withNamesIfNotNull(final T builder, final Names names) {
        if (names != null) {
            if (StringUtils.isNotEmpty(names.fullName))
                builder.withProperty("full_name", names.fullName);
            if (StringUtils.isNotEmpty(names.shortLabel))
                builder.withProperty("short_label", names.shortLabel);
            builder.withPropertyIfNotNull("alias", buildAliasArray(names));
        }
    }

    private <T extends ModelBuilder<T>> void withNamesIfNotNull(final T builder, final Names names,
                                                                final String keyPrefix) {
        if (names != null) {
            if (StringUtils.isNotEmpty(names.fullName))
                builder.withProperty(keyPrefix + "full_name", names.fullName);
            if (StringUtils.isNotEmpty(names.shortLabel))
                builder.withProperty(keyPrefix + "short_label", names.shortLabel);
            builder.withPropertyIfNotNull(keyPrefix + "alias", buildAliasArray(names));
        }
    }

    private String[] buildAliasArray(final Names names) {
        if (names == null || names.alias == null || names.alias.isEmpty())
            return null;
        return names.alias.stream().map(a -> {
            if (a.type != null && a.typeAc != null)
                return a.value + " [" + a.type + " (" + a.typeAc + ")]";
            if (a.type != null)
                return a.value + " [" + a.type + ']';
            if (a.typeAc != null)
                return a.value + " [(" + a.typeAc + ")]";
            return a.value;
        }).toArray(String[]::new);
    }

    private <T extends ModelBuilder<T>> void withXrefsIfNotNull(final T builder, final Xref refs) {
        if (refs != null) {
            if (refs.primaryRef != null)
                builder.withPropertyIfNotNull("primary_xref", buildDBReference(refs.primaryRef));
            if (refs.secondaryRef != null && !refs.secondaryRef.isEmpty()) {
                builder.withPropertyIfNotNull("secondary_xref", refs.secondaryRef.stream().map(this::buildDBReference)
                                                                                 .toArray(String[]::new));
            }
        }
    }

    private <T extends ModelBuilder<T>> void withXrefsIfNotNull(final T builder, final Xref refs,
                                                                final String keyPrefix) {
        if (refs != null) {
            if (refs.primaryRef != null)
                builder.withPropertyIfNotNull(keyPrefix + "primary_xref", buildDBReference(refs.primaryRef));
            if (refs.secondaryRef != null && !refs.secondaryRef.isEmpty()) {
                builder.withPropertyIfNotNull(keyPrefix + "secondary_xref",
                                              refs.secondaryRef.stream().map(this::buildDBReference)
                                                               .toArray(String[]::new));
            }
        }
    }

    private String buildDBReference(final DbReference ref) {
        // TODO:
        //  dbAc
        //  refType
        //  refTypeAc
        //  secondary
        //  version
        return ref.db + ":" + ref.id;
    }

    private <T extends ModelBuilder<T>> void withAttributesIfNotNull(final T builder,
                                                                     final List<Attribute> attributes) {
        if (attributes != null && !attributes.isEmpty())
            builder.withPropertyIfNotNull("attributes", buildAttributesArray(attributes));
    }

    private <T extends ModelBuilder<T>> void withAttributesIfNotNull(final T builder, final List<Attribute> attributes,
                                                                     final String keyPrefix) {
        if (attributes != null && !attributes.isEmpty())
            builder.withPropertyIfNotNull(keyPrefix + "attributes", buildAttributesArray(attributes));
    }

    private String[] buildAttributesArray(final List<Attribute> attributes) {
        return attributes.stream().map(
                a -> a.nameAc != null ? a.name + " (" + a.nameAc + "): " + a.value : a.name + ": " + a.value).toArray(
                String[]::new);
    }

    private Long exportInteractor(final Graph graph, final Interactor interactor,
                                  final Map<Integer, Long> interactorIdNodeIdMap) {
        final NodeBuilder builder = graph.buildNode().withLabel(INTERACTOR_LABEL);
        withNamesIfNotNull(builder, interactor.names);
        withAttributesIfNotNull(builder, interactor.attributeList);
        withXrefsIfNotNull(builder, interactor.xref);
        if (StringUtils.isNotEmpty(interactor.sequence))
            builder.withProperty("sequence", interactor.sequence);
        withCVTypeIfNotNull(builder, interactor.interactorType, "type_");
        // TODO: id
        final var interactorNode = builder.build();
        interactorIdNodeIdMap.put(interactor.id, interactorNode.getId());
        getOrCreateBioSource(graph, interactor.organism);
        return interactorNode.getId();
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
