package de.unibi.agbi.biodwh2.brenda.etl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.brenda.BrendaDataSource;
import de.unibi.agbi.biodwh2.brenda.model.*;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BrendaGraphExporter extends GraphExporter<BrendaDataSource> {
    static final String ENZYME_LABEL = "Enzyme";
    static final String PROTEIN_LABEL = "Protein";
    static final String PUBLICATION_LABEL = "Publication";
    static final String ORGANISM_LABEL = "Organism";

    public BrendaGraphExporter(final BrendaDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 4;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(ENZYME_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, "accession", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, "pmid", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ORGANISM_LABEL, "ncbi_taxid", IndexDescription.Type.UNIQUE));
        try (final TarArchiveInputStream inputStream = FileUtils.openTarGzip(workspace, dataSource,
                                                                             BrendaUpdater.FILE_NAME)) {
            TarArchiveEntry entry;
            while ((entry = inputStream.getNextEntry()) != null) {
                if (entry.getName().startsWith("brenda_") && entry.getName().endsWith(".json")) {
                    final ObjectMapper mapper = new ObjectMapper();
                    final Brenda brenda = mapper.readValue(inputStream, Brenda.class);
                    exportBrenda(graph, brenda);
                    break;
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private void exportBrenda(final Graph graph, final Brenda brenda) {
        final Map<String, Long> organismNodeIdMap = new HashMap<>();
        final Map<String, Long> publicationKeyNodeIdMap = new HashMap<>();
        for (final Enzyme enzyme : brenda.data.values())
            exportEnzyme(graph, enzyme, organismNodeIdMap, publicationKeyNodeIdMap);
    }

    private void exportEnzyme(final Graph graph, final Enzyme enzyme, final Map<String, Long> organismNodeIdMap,
                              final Map<String, Long> publicationKeyNodeIdMap) {
        final Map<Integer, Long[]> proteinRefNodeIdMap = exportEnzymeProteinRefs(graph, enzyme, organismNodeIdMap);
        final Map<Integer, Long> publicationRefNodeIdMap = exportEnzymePublicationRefs(graph, enzyme,
                                                                                       publicationKeyNodeIdMap);
        final NodeBuilder builder = graph.buildNode().withLabel(ENZYME_LABEL);
        builder.withProperty(ID_KEY, enzyme.id);
        if (enzyme.history != null)
            builder.withPropertyIfNotNull("history", enzyme.history);
        builder.withPropertyIfNotNull("recommended_name", enzyme.recommendedName);
        builder.withPropertyIfNotNull("systematic_name", enzyme.systematicName);
        final Node node = builder.build();
        if (enzyme.kmValue != null)
            for (final NumericDataset dataset : enzyme.kmValue)
                createNumericValueNode(graph, node, "HAS_KM", proteinRefNodeIdMap, publicationRefNodeIdMap, dataset,
                                       "substrate");
        if (enzyme.turnoverNumber != null)
            for (final NumericDataset dataset : enzyme.turnoverNumber)
                createNumericValueNode(graph, node, "HAS_TURNOVER_NUMBER", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                       dataset, "substrate");
        if (enzyme.kcatKmValue != null)
            for (final NumericDataset dataset : enzyme.kcatKmValue)
                createNumericValueNode(graph, node, "HAS_KCAT_KM", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                       dataset, "substrate");
        if (enzyme.kiValue != null)
            for (final NumericDataset dataset : enzyme.kiValue)
                createNumericValueNode(graph, node, "HAS_KI", proteinRefNodeIdMap, publicationRefNodeIdMap, dataset,
                                       "inhibitor");
        if (enzyme.ic50Value != null)
            for (final NumericDataset dataset : enzyme.ic50Value)
                createNumericValueNode(graph, node, "HAS_IC50", proteinRefNodeIdMap, publicationRefNodeIdMap, dataset,
                                       "inhibitor");
        if (enzyme.phRange != null)
            for (final NumericDataset dataset : enzyme.phRange)
                createNumericValueNode(graph, node, "HAS_PH_RANGE", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                       dataset, "value");
        if (enzyme.phStability != null)
            for (final NumericDataset dataset : enzyme.phStability)
                createNumericValueNode(graph, node, "HAS_PH_STABILITY", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                       dataset, "value");
        if (enzyme.temperatureOptimum != null)
            for (final NumericDataset dataset : enzyme.temperatureOptimum)
                createNumericValueNode(graph, node, "HAS_TEMPERATURE_OPTIMUM", proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "value");
        if (enzyme.temperatureRange != null)
            for (final NumericDataset dataset : enzyme.temperatureRange)
                createNumericValueNode(graph, node, "HAS_TEMPERATURE_RANGE", proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "value");
        if (enzyme.temperatureStability != null)
            for (final NumericDataset dataset : enzyme.temperatureStability)
                createNumericValueNode(graph, node, "HAS_TEMPERATURE_STABILITY", proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "value");
        if (enzyme.molecularWeight != null)
            for (final NumericDataset dataset : enzyme.molecularWeight)
                createNumericValueNode(graph, node, "HAS_MOLECULAR_WEIGHT", proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "value");
        if (enzyme.piValue != null)
            for (final NumericDataset dataset : enzyme.piValue)
                createNumericValueNode(graph, node, "HAS_PI", proteinRefNodeIdMap, publicationRefNodeIdMap, dataset,
                                       "value");
        if (enzyme.phOptimum != null)
            for (final NumericDataset dataset : enzyme.phOptimum)
                createNumericValueNode(graph, node, "HAS_PH_OPTIMUM", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                       dataset, "value");
        if (enzyme.specificActivity != null)
            for (final NumericDataset dataset : enzyme.specificActivity)
                createNumericValueNode(graph, node, "HAS_SPECIFIC_ACTIVITY", proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "value");
        if (enzyme.crystallization != null)
            for (final Dataset dataset : enzyme.crystallization)
                createValueNode(graph, node, "HAS_CRYSTALLIZATION", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                dataset);
        if (enzyme.purification != null)
            for (final Dataset dataset : enzyme.purification)
                createValueNode(graph, node, "HAS_PURIFICATION", proteinRefNodeIdMap, publicationRefNodeIdMap, dataset);
        if (enzyme.renatured != null)
            for (final Dataset dataset : enzyme.renatured)
                createValueNode(graph, node, "HAS_RENATURATION", proteinRefNodeIdMap, publicationRefNodeIdMap, dataset);
        if (enzyme.generalStability != null)
            for (final Dataset dataset : enzyme.generalStability)
                createValueNode(graph, node, "HAS_GENERAL_STABILITY", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                dataset);
        if (enzyme.oxydationStability != null)
            for (final Dataset dataset : enzyme.oxydationStability)
                createValueNode(graph, node, "HAS_OXYDATION_STABILITY", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                dataset);
        if (enzyme.storageStability != null)
            for (final Dataset dataset : enzyme.storageStability)
                createValueNode(graph, node, "HAS_STORAGE_STABILITY", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                dataset);
        if (enzyme.synonyms != null)
            for (final TextDataset dataset : enzyme.synonyms)
                createTextValueNode(graph, node, "HAS_SYNONYM", proteinRefNodeIdMap, publicationRefNodeIdMap, dataset);
        if (enzyme.application != null)
            for (final TextDataset dataset : enzyme.application)
                createTextValueNode(graph, node, "HAS_APPLICATION", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                    dataset);
        if (enzyme.reactionType != null)
            for (final TextDataset dataset : enzyme.reactionType)
                createTextValueNode(graph, node, "HAS_REACTION_TYPE", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                    dataset);
        if (enzyme.localization != null)
            for (final TextDataset dataset : enzyme.localization)
                createTextValueNode(graph, node, "HAS_LOCALIZATION", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                    dataset);
        if (enzyme.sourceTissue != null)
            for (final TextDataset dataset : enzyme.sourceTissue)
                createTextValueNode(graph, node, "HAS_SOURCE_TISSUE", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                    dataset);
        if (enzyme.activatingCompound != null)
            for (final TextDataset dataset : enzyme.activatingCompound)
                createTextValueNode(graph, node, "HAS_ACTIVATING_COMPOUND", proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.inhibitor != null)
            for (final TextDataset dataset : enzyme.inhibitor)
                createTextValueNode(graph, node, "HAS_INHIBITOR", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                    dataset);
        if (enzyme.metalsIons != null)
            for (final TextDataset dataset : enzyme.metalsIons)
                createTextValueNode(graph, node, "HAS_METALS_IONS", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                    dataset);
        if (enzyme.posttranslationalModification != null)
            for (final TextDataset dataset : enzyme.posttranslationalModification)
                createTextValueNode(graph, node, "HAS_POSTTRANSLATIONAL_MODIFICATION", proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.subunits != null)
            for (final TextDataset dataset : enzyme.subunits)
                createTextValueNode(graph, node, "HAS_SUBUNITS", proteinRefNodeIdMap, publicationRefNodeIdMap, dataset);
        if (enzyme.cofactor != null)
            for (final TextDataset dataset : enzyme.cofactor)
                createTextValueNode(graph, node, "HAS_COFACTOR", proteinRefNodeIdMap, publicationRefNodeIdMap, dataset);
        if (enzyme.proteinVariants != null)
            for (final TextDataset dataset : enzyme.proteinVariants)
                createTextValueNode(graph, node, "HAS_PROTEIN_VARIANT", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                    dataset);
        if (enzyme.cloned != null)
            for (final TextDataset dataset : enzyme.cloned)
                createTextValueNode(graph, node, "HAS_CLONED", proteinRefNodeIdMap, publicationRefNodeIdMap, dataset);
        if (enzyme.organicSolventStability != null)
            for (final TextDataset dataset : enzyme.organicSolventStability)
                createTextValueNode(graph, node, "HAS_ORGANIC_SOLVENT_STABILITY", proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.expression != null)
            for (final TextDataset dataset : enzyme.expression)
                createTextValueNode(graph, node, "HAS_EXPRESSION", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                    dataset);
        if (enzyme.generalInformation != null)
            for (final TextDataset dataset : enzyme.generalInformation)
                createTextValueNode(graph, node, "HAS_GENERAL_INFORMATION", proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.reaction != null)
            for (final TextDataset dataset : enzyme.reaction)
                createTextValueNode(graph, node, "HAS_REACTION", proteinRefNodeIdMap, publicationRefNodeIdMap, dataset);
        if (enzyme.naturalSubstratesProducts != null)
            for (final TextDataset dataset : enzyme.naturalSubstratesProducts)
                createTextValueNode(graph, node, "HAS_NATURAL_SUBSTRATE_PRODUCT", proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.substratesProducts != null)
            for (final TextDataset dataset : enzyme.substratesProducts)
                createTextValueNode(graph, node, "HAS_SUBSTRATE_PRODUCT", proteinRefNodeIdMap, publicationRefNodeIdMap,
                                    dataset);
    }

    private Map<Integer, Long> exportEnzymePublicationRefs(Graph graph, Enzyme enzyme,
                                                           final Map<String, Long> publicationKeyNodeIdMap) {
        final Map<Integer, Long> refNodeIdMap = new HashMap<>();
        if (enzyme.references != null) {
            for (final Map.Entry<Integer, ReferenceDataset> reference : enzyme.references.entrySet()) {
                Long publicationNodeId = getOrCreatePublicationNode(graph, reference.getValue(),
                                                                    publicationKeyNodeIdMap);
                refNodeIdMap.put(reference.getKey(), publicationNodeId);
            }
        }
        return refNodeIdMap;
    }

    private Long getOrCreatePublicationNode(final Graph graph, final ReferenceDataset reference,
                                            final Map<String, Long> publicationKeyNodeIdMap) {
        Long pmid = reference.pmid;
        if (pmid != null && pmid == 3020186354L)
            pmid = 30201863L;
        final Integer pmidInt = pmid != null ? pmid.intValue() : null;
        final String key = String.join("|", reference.authors) + '$' + reference.journal + '$' + reference.volume +
                           '$' + reference.year + '$' + reference.pages;
        Long publicationNodeId = null;
        if (pmidInt != null) {
            Node publicationNode = graph.findNode(PUBLICATION_LABEL, "pmid", pmidInt);
            if (publicationNode != null)
                publicationNodeId = publicationNode.getId();
        } else {
            publicationNodeId = publicationKeyNodeIdMap.get(key);
        }
        if (publicationNodeId == null) {
            final NodeBuilder publicationBuilder = graph.buildNode().withLabel(PUBLICATION_LABEL);
            publicationBuilder.withPropertyIfNotNull("pmid", pmidInt);
            publicationBuilder.withPropertyIfNotNull("title", reference.title);
            publicationBuilder.withPropertyIfNotNull("authors", reference.authors);
            publicationBuilder.withPropertyIfNotNull("journal", reference.journal);
            publicationBuilder.withPropertyIfNotNull("volume", reference.volume);
            publicationBuilder.withPropertyIfNotNull("year", reference.year);
            publicationBuilder.withPropertyIfNotNull("pages", reference.pages);
            publicationNodeId = publicationBuilder.build().getId();
            if (pmidInt == null)
                publicationKeyNodeIdMap.put(key, publicationNodeId);
        }
        return publicationNodeId;
    }

    private Map<Integer, Long[]> exportEnzymeProteinRefs(Graph graph, Enzyme enzyme,
                                                         Map<String, Long> organismNodeIdMap) {
        final Map<Integer, Long[]> refNodeIdMap = new HashMap<>();
        if (enzyme.proteins != null) {
            for (final Map.Entry<Integer, ProteinDataset> entry : enzyme.proteins.entrySet()) {
                final ProteinDataset protein = entry.getValue();
                final Long[] proteinNodeIds = getOrCreateProteinNodes(graph, protein, organismNodeIdMap);
                refNodeIdMap.put(entry.getKey(), proteinNodeIds);
            }
        }
        return refNodeIdMap;
    }

    private Long[] getOrCreateProteinNodes(final Graph graph, final ProteinDataset protein,
                                           Map<String, Long> organismNodeIdMap) {
        // TODO: comment, references
        final Set<Long> nodeIds = new HashSet<>();
        if (protein.accessions != null) {
            for (final String accession : protein.accessions) {
                Node node = graph.findNode(PROTEIN_LABEL, "accession", accession);
                if (node == null) {
                    if (StringUtils.isNotEmpty(protein.source)) {
                        node = graph.addNode(PROTEIN_LABEL, "accession", accession, "source", protein.source);
                    } else {
                        node = graph.addNode(PROTEIN_LABEL, "accession", accession);
                    }
                    final Long organismNodeId = getOrCreateOrganismNode(graph, protein.organism, organismNodeIdMap);
                    graph.addEdge(node, organismNodeId, "BELONGS_TO");
                }
                nodeIds.add(node.getId());
            }
        } else {
            final Long nodeId = graph.addNode(PROTEIN_LABEL).getId();
            nodeIds.add(nodeId);
            final Long organismNodeId = getOrCreateOrganismNode(graph, protein.organism, organismNodeIdMap);
            graph.addEdge(nodeId, organismNodeId, "BELONGS_TO");
        }
        return nodeIds.toArray(new Long[0]);
    }

    private Long getOrCreateOrganismNode(final Graph graph, final String speciesName,
                                         final Map<String, Long> organismNodeIdMap) {
        Long organismNodeId = organismNodeIdMap.get(speciesName);
        if (organismNodeId == null) {
            final SpeciesLookup.Entry species = SpeciesLookup.getByScientificName(speciesName);
            if (species != null && species.ncbiTaxId != null) {
                organismNodeId = getOrCreateOntologyProxyTerm(graph, species.getNCBITaxonId());
            } else {
                organismNodeId = graph.addNode(ORGANISM_LABEL, "name", speciesName).getId();
            }
            organismNodeIdMap.put(speciesName, organismNodeId);
        }
        return organismNodeId;
    }

    private void createNumericValueNode(final Graph graph, final Node enzymeNode, final String edgeLabel,
                                        final Map<Integer, Long[]> proteinRefNodeIdMap,
                                        final Map<Integer, Long> publicationRefNodeIdMap, final NumericDataset dataset,
                                        final String valueKey) {
        final NodeBuilder builder = graph.buildNode().withLabel("NumericValue");
        String value = dataset.value;
        int braceIndex = value.indexOf('{');
        if (braceIndex != -1) {
            builder.withPropertyIfNotNull(valueKey, value.substring(braceIndex + 1, value.lastIndexOf('}')));
            value = value.substring(0, braceIndex).trim();
        }
        int dashIndex = value.indexOf('-');
        if (dashIndex == -1) {
            builder.withPropertyIfNotNull("num_value", value);
        } else {
            builder.withPropertyIfNotNull("min_value", value.substring(0, dashIndex));
            builder.withPropertyIfNotNull("max_value", value.substring(dashIndex + 1));
        }
        createDatasetNode(graph, enzymeNode, edgeLabel, proteinRefNodeIdMap, publicationRefNodeIdMap, dataset, builder);
    }

    private void createDatasetNode(final Graph graph, final Node enzymeNode, final String edgeLabel,
                                   final Map<Integer, Long[]> proteinRefNodeIdMap,
                                   final Map<Integer, Long> publicationRefNodeIdMap, final Dataset dataset,
                                   final NodeBuilder builder) {
        builder.withPropertyIfNotNull("comment", dataset.comment);
        final Node node = builder.build();
        graph.addEdge(enzymeNode, node, edgeLabel);
        connectDatasetWithEntities(graph, proteinRefNodeIdMap, publicationRefNodeIdMap, dataset, node);
    }

    private void connectDatasetWithEntities(final Graph graph, final Map<Integer, Long[]> proteinRefNodeIdMap,
                                            final Map<Integer, Long> publicationRefNodeIdMap, final Dataset dataset,
                                            final Node node) {
        if (dataset.proteins != null) {
            for (final String protein : dataset.proteins) {
                final int proteinRef = Integer.parseInt(protein);
                final Long[] nodeIds = proteinRefNodeIdMap.get(proteinRef);
                if (nodeIds != null) {
                    for (final Long nodeId : nodeIds) {
                        graph.addEdge(node, nodeId, "ASSOCIATED_WITH");
                    }
                }
            }
        }
        if (dataset.references != null) {
            for (final String reference : dataset.references) {
                if (StringUtils.isNotEmpty(reference) && !"-".equals(reference) && !"Swissprot".equals(reference)) {
                    try {
                        final int indexOfDash = reference.indexOf('-');
                        if (indexOfDash == -1) {
                            final int referenceRef = Integer.parseInt(reference);
                            final Long nodeId = publicationRefNodeIdMap.get(referenceRef);
                            if (nodeId != null)
                                graph.addEdge(node, nodeId, "REFERENCES");
                        } else {
                            final int start = Integer.parseInt(reference.substring(0, indexOfDash));
                            final int end = Integer.parseInt(reference.substring(indexOfDash + 1));
                            for (int referenceRef = start; referenceRef <= end; referenceRef++) {
                                final Long nodeId = publicationRefNodeIdMap.get(referenceRef);
                                if (nodeId != null)
                                    graph.addEdge(node, nodeId, "REFERENCES");
                            }
                        }
                    } catch (NumberFormatException ignored) {
                        // TODO: remove fix for malformed data once fixed
                        try {
                            final String actualReference = dataset.references[dataset.references.length - 1];
                            final int referenceRef = Integer.parseInt(actualReference.substring(1));
                            final Long nodeId = publicationRefNodeIdMap.get(referenceRef);
                            if (nodeId != null)
                                graph.addEdge(node, nodeId, "REFERENCES");
                            node.setProperty("comment", node.<String>getProperty("comment") + "<" +
                                                        String.join(" ", dataset.references)
                                                              .replace(" " + actualReference, ""));
                            graph.update(node);
                            break;
                        } catch (Exception ignored2) {
                        }
                    }
                }
            }
        }
    }

    private void createValueNode(final Graph graph, final Node enzymeNode, final String edgeLabel,
                                 final Map<Integer, Long[]> proteinRefNodeIdMap,
                                 final Map<Integer, Long> publicationRefNodeIdMap, final Dataset dataset) {
        final NodeBuilder builder = graph.buildNode().withLabel("Value");
        createDatasetNode(graph, enzymeNode, edgeLabel, proteinRefNodeIdMap, publicationRefNodeIdMap, dataset, builder);
    }

    private void createTextValueNode(final Graph graph, final Node enzymeNode, final String edgeLabel,
                                     final Map<Integer, Long[]> proteinRefNodeIdMap,
                                     final Map<Integer, Long> publicationRefNodeIdMap, final TextDataset dataset) {
        final NodeBuilder builder = graph.buildNode().withLabel("TextValue");
        builder.withPropertyIfNotNull("value", dataset.value);
        createDatasetNode(graph, enzymeNode, edgeLabel, proteinRefNodeIdMap, publicationRefNodeIdMap, dataset, builder);
    }
}
