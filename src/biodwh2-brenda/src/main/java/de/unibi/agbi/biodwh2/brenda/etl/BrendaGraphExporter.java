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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrendaGraphExporter extends GraphExporter<BrendaDataSource> {
    private static final Pattern ORGANISMS_PATTERN = Pattern.compile("#(\\d+(,\\d+)*)#");
    private static final Pattern REFERENCES_PATTERN = Pattern.compile("<(\\d+(,\\d+)*)>");
    static final String ENZYME_LABEL = "Enzyme";
    static final String PROTEIN_LABEL = "Protein";
    static final String PUBLICATION_LABEL = "Publication";
    static final String ORGANISM_LABEL = "Organism";

    public BrendaGraphExporter(final BrendaDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 3;
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
        final Map<Integer, Long> organismRefNodeIdMap = exportEnzymeOrganismRefs(graph, enzyme, organismNodeIdMap);
        final Map<Integer, Long[]> proteinRefNodeIdMap = exportEnzymeProteinRefs(graph, enzyme);
        final Map<Integer, Long> publicationRefNodeIdMap = exportEnzymePublicationRefs(graph, enzyme,
                                                                                       publicationKeyNodeIdMap);
        final NodeBuilder builder = graph.buildNode().withLabel(ENZYME_LABEL);
        builder.withProperty(ID_KEY, enzyme.id);
        builder.withPropertyIfNotNull("name", enzyme.name);
        builder.withPropertyIfNotNull("systematic_name", enzyme.systematicName);
        final Node node = builder.build();
        if (enzyme.kmValue != null)
            for (final NumericDataset dataset : enzyme.kmValue)
                createNumericValueNode(graph, node, "HAS_KM", organismRefNodeIdMap, proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "substrate");
        if (enzyme.turnoverNumber != null)
            for (final NumericDataset dataset : enzyme.turnoverNumber)
                createNumericValueNode(graph, node, "HAS_TURNOVER_NUMBER", organismRefNodeIdMap, proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "substrate");
        if (enzyme.kcatKm != null)
            for (final NumericDataset dataset : enzyme.kcatKm)
                createNumericValueNode(graph, node, "HAS_KCAT_KM", organismRefNodeIdMap, proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "substrate");
        if (enzyme.kiValue != null)
            for (final NumericDataset dataset : enzyme.kiValue)
                createNumericValueNode(graph, node, "HAS_KI_VALUE", organismRefNodeIdMap, proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "inhibitor");
        if (enzyme.ic50 != null)
            for (final NumericDataset dataset : enzyme.ic50)
                createNumericValueNode(graph, node, "HAS_IC50", organismRefNodeIdMap, proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "inhibitor");
        if (enzyme.phRange != null)
            for (final NumericDataset dataset : enzyme.phRange)
                createNumericValueNode(graph, node, "HAS_PH_RANGE", organismRefNodeIdMap, proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "value");
        if (enzyme.phStability != null)
            for (final NumericDataset dataset : enzyme.phStability)
                createNumericValueNode(graph, node, "HAS_PH_STABILITY", organismRefNodeIdMap, proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "value");
        if (enzyme.temperatureOptimum != null)
            for (final NumericDataset dataset : enzyme.temperatureOptimum)
                createNumericValueNode(graph, node, "HAS_TEMPERATURE_OPTIMUM", organismRefNodeIdMap,
                                       proteinRefNodeIdMap, publicationRefNodeIdMap, dataset, "value");
        if (enzyme.temperatureRange != null)
            for (final NumericDataset dataset : enzyme.temperatureRange)
                createNumericValueNode(graph, node, "HAS_TEMPERATURE_RANGE", organismRefNodeIdMap, proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "value");
        if (enzyme.temperatureStability != null)
            for (final NumericDataset dataset : enzyme.temperatureStability)
                createNumericValueNode(graph, node, "HAS_TEMPERATURE_STABILITY", organismRefNodeIdMap,
                                       proteinRefNodeIdMap, publicationRefNodeIdMap, dataset, "value");
        if (enzyme.molecularWeight != null)
            for (final NumericDataset dataset : enzyme.molecularWeight)
                createNumericValueNode(graph, node, "HAS_MOLECULAR_WEIGHT", organismRefNodeIdMap, proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "value");
        if (enzyme.isoelectricPoint != null)
            for (final NumericDataset dataset : enzyme.isoelectricPoint)
                createNumericValueNode(graph, node, "HAS_ISOELECTRIC_POINT", organismRefNodeIdMap, proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "value");
        if (enzyme.phOptimum != null)
            for (final NumericDataset dataset : enzyme.phOptimum)
                createNumericValueNode(graph, node, "HAS_PH_OPTIMUM", organismRefNodeIdMap, proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "value");
        if (enzyme.specificActivity != null)
            for (final NumericDataset dataset : enzyme.specificActivity)
                createNumericValueNode(graph, node, "HAS_SPECIFIC_ACTIVITY", organismRefNodeIdMap, proteinRefNodeIdMap,
                                       publicationRefNodeIdMap, dataset, "value");
        if (enzyme.crystallization != null)
            for (final Dataset dataset : enzyme.crystallization)
                createValueNode(graph, node, "HAS_CRYSTALLIZATION", organismRefNodeIdMap, proteinRefNodeIdMap,
                                publicationRefNodeIdMap, dataset);
        if (enzyme.purification != null)
            for (final Dataset dataset : enzyme.purification)
                createValueNode(graph, node, "HAS_PURIFICATION", organismRefNodeIdMap, proteinRefNodeIdMap,
                                publicationRefNodeIdMap, dataset);
        if (enzyme.renaturation != null)
            for (final Dataset dataset : enzyme.renaturation)
                createValueNode(graph, node, "HAS_RENATURATION", organismRefNodeIdMap, proteinRefNodeIdMap,
                                publicationRefNodeIdMap, dataset);
        if (enzyme.generalStability != null)
            for (final Dataset dataset : enzyme.generalStability)
                createValueNode(graph, node, "HAS_GENERAL_STABILITY", organismRefNodeIdMap, proteinRefNodeIdMap,
                                publicationRefNodeIdMap, dataset);
        if (enzyme.oxygenStability != null)
            for (final Dataset dataset : enzyme.oxygenStability)
                createValueNode(graph, node, "HAS_OXYGEN_STABILITY", organismRefNodeIdMap, proteinRefNodeIdMap,
                                publicationRefNodeIdMap, dataset);
        if (enzyme.storageStability != null)
            for (final Dataset dataset : enzyme.storageStability)
                createValueNode(graph, node, "HAS_STORAGE_STABILITY", organismRefNodeIdMap, proteinRefNodeIdMap,
                                publicationRefNodeIdMap, dataset);
        if (enzyme.synonyms != null)
            for (final TextDataset dataset : enzyme.synonyms)
                createTextValueNode(graph, node, "HAS_SYNONYM", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.application != null)
            for (final TextDataset dataset : enzyme.application)
                createTextValueNode(graph, node, "HAS_APPLICATION", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.reactionType != null)
            for (final TextDataset dataset : enzyme.reactionType)
                createTextValueNode(graph, node, "HAS_REACTION_TYPE", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.localization != null)
            for (final TextDataset dataset : enzyme.localization)
                createTextValueNode(graph, node, "HAS_LOCALIZATION", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.tissue != null)
            for (final TextDataset dataset : enzyme.tissue)
                createTextValueNode(graph, node, "HAS_TISSUE", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.activatingCompound != null)
            for (final TextDataset dataset : enzyme.activatingCompound)
                createTextValueNode(graph, node, "HAS_ACTIVATING_COMPOUND", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.inhibitor != null)
            for (final TextDataset dataset : enzyme.inhibitor)
                createTextValueNode(graph, node, "HAS_INHIBITOR", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.metalsIons != null)
            for (final TextDataset dataset : enzyme.metalsIons)
                createTextValueNode(graph, node, "HAS_METALS_IONS", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.posttranslationalModification != null)
            for (final TextDataset dataset : enzyme.posttranslationalModification)
                createTextValueNode(graph, node, "HAS_POSTTRANSLATIONAL_MODIFICATION", organismRefNodeIdMap,
                                    proteinRefNodeIdMap, publicationRefNodeIdMap, dataset);
        if (enzyme.subunits != null)
            for (final TextDataset dataset : enzyme.subunits)
                createTextValueNode(graph, node, "HAS_SUBUNITS", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.cofactor != null)
            for (final TextDataset dataset : enzyme.cofactor)
                createTextValueNode(graph, node, "HAS_COFACTOR", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.engineering != null)
            for (final TextDataset dataset : enzyme.engineering)
                createTextValueNode(graph, node, "HAS_ENGINEERING", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.cloned != null)
            for (final TextDataset dataset : enzyme.cloned)
                createTextValueNode(graph, node, "HAS_CLONED", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.organicSolventStability != null)
            for (final TextDataset dataset : enzyme.organicSolventStability)
                createTextValueNode(graph, node, "HAS_ORGANIC_SOLVENT_STABILITY", organismRefNodeIdMap,
                                    proteinRefNodeIdMap, publicationRefNodeIdMap, dataset);
        if (enzyme.expression != null)
            for (final TextDataset dataset : enzyme.expression)
                createTextValueNode(graph, node, "HAS_EXPRESSION", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.generalInformation != null)
            for (final TextDataset dataset : enzyme.generalInformation)
                createTextValueNode(graph, node, "HAS_GENERAL_INFORMATION", organismRefNodeIdMap, proteinRefNodeIdMap,
                                    publicationRefNodeIdMap, dataset);
        if (enzyme.genericReaction != null)
            for (final ReactionDataset dataset : enzyme.genericReaction)
                createReactionValueNode(graph, node, "HAS_GENERIC_REACTION", organismRefNodeIdMap, proteinRefNodeIdMap,
                                        publicationRefNodeIdMap, dataset);
        if (enzyme.naturalReaction != null)
            for (final ReactionDataset dataset : enzyme.naturalReaction)
                createReactionValueNode(graph, node, "HAS_NATURAL_REACTION", organismRefNodeIdMap, proteinRefNodeIdMap,
                                        publicationRefNodeIdMap, dataset);
        if (enzyme.reaction != null)
            for (final ReactionDataset dataset : enzyme.reaction)
                createReactionValueNode(graph, node, "HAS_REACTION", organismRefNodeIdMap, proteinRefNodeIdMap,
                                        publicationRefNodeIdMap, dataset);
    }

    private Map<Integer, Long> exportEnzymeOrganismRefs(Graph graph, Enzyme enzyme,
                                                        Map<String, Long> organismNodeIdMap) {
        final Map<Integer, Long> refNodeIdMap = new HashMap<>();
        if (enzyme.organisms != null) {
            for (final Map.Entry<Integer, Organism> organism : enzyme.organisms.entrySet()) {
                Long organismNodeId = getOrCreateOrganismNode(graph, organism.getValue().value, organismNodeIdMap);
                refNodeIdMap.put(organism.getKey(), organismNodeId);
            }
        }
        return refNodeIdMap;
    }

    private Long getOrCreateOrganismNode(final Graph graph, final String speciesName,
                                         final Map<String, Long> organismNodeIdMap) {
        // TODO: comment
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

    private Map<Integer, Long> exportEnzymePublicationRefs(Graph graph, Enzyme enzyme,
                                                           final Map<String, Long> publicationKeyNodeIdMap) {
        final Map<Integer, Long> refNodeIdMap = new HashMap<>();
        if (enzyme.references != null) {
            for (final Map.Entry<Integer, Reference> reference : enzyme.references.entrySet()) {
                Long publicationNodeId = getOrCreatePublicationNode(graph, reference.getValue(),
                                                                    publicationKeyNodeIdMap);
                refNodeIdMap.put(reference.getKey(), publicationNodeId);
            }
        }
        return refNodeIdMap;
    }

    private Long getOrCreatePublicationNode(final Graph graph, final Reference reference,
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

    private Map<Integer, Long[]> exportEnzymeProteinRefs(Graph graph, Enzyme enzyme) {
        final Map<Integer, Long[]> refNodeIdMap = new HashMap<>();
        if (enzyme.proteins != null) {
            for (final Map.Entry<Integer, Protein[]> entry : enzyme.proteins.entrySet()) {
                // TODO: if (entry.getValue().length > 1)
                //    LOGGER.warn("Proteins ref with " + entry.getValue().length + " entries found");
                final Protein protein = entry.getValue()[0];
                final Long[] proteinNodeIds = getOrCreateProteinNodes(graph, protein);
                refNodeIdMap.put(entry.getKey(), proteinNodeIds);
            }
        }
        return refNodeIdMap;
    }

    private Long[] getOrCreateProteinNodes(final Graph graph, final Protein protein) {
        // TODO: comment
        final Set<Long> nodeIds = new HashSet<>();
        if (protein.accessions != null) {
            for (final String accession : protein.accessions) {
                Node node = graph.findNode(PROTEIN_LABEL, "accession", accession);
                if (node == null)
                    node = graph.addNode(PROTEIN_LABEL, "accession", accession, "source", protein.source);
                nodeIds.add(node.getId());
            }
        } else {
            nodeIds.add(graph.addNode(PROTEIN_LABEL).getId());
        }
        return nodeIds.toArray(new Long[0]);
    }

    private void createNumericValueNode(final Graph graph, final Node enzymeNode, final String edgeLabel,
                                        final Map<Integer, Long> organismRefNodeIdMap,
                                        final Map<Integer, Long[]> proteinRefNodeIdMap,
                                        final Map<Integer, Long> publicationRefNodeIdMap, final NumericDataset dataset,
                                        final String valueKey) {
        final NodeBuilder builder = graph.buildNode().withLabel("NumericValue");
        builder.withPropertyIfNotNull("num_value", dataset.numValue);
        builder.withPropertyIfNotNull("min_value", dataset.minValue);
        builder.withPropertyIfNotNull("max_value", dataset.maxValue);
        builder.withPropertyIfNotNull(valueKey, dataset.value);
        createDatasetNode(graph, enzymeNode, edgeLabel, organismRefNodeIdMap, proteinRefNodeIdMap,
                          publicationRefNodeIdMap, dataset, builder);
    }

    private void createDatasetNode(final Graph graph, final Node enzymeNode, final String edgeLabel,
                                   final Map<Integer, Long> organismRefNodeIdMap,
                                   final Map<Integer, Long[]> proteinRefNodeIdMap,
                                   final Map<Integer, Long> publicationRefNodeIdMap, final Dataset dataset,
                                   final NodeBuilder builder) {
        if (dataset.comment != null) {
            final CommentData[] comments = parseComment(dataset.comment);
            for (final CommentData comment : comments) {
                builder.withProperty("comment", comment.text);
                final Node node = builder.build();
                graph.addEdge(enzymeNode, node, edgeLabel);
                if (comment.referenceRefs != null) {
                    for (final Integer referenceRef : comment.referenceRefs) {
                        final Long nodeId = publicationRefNodeIdMap.get(referenceRef);
                        if (nodeId != null)
                            graph.addEdge(node, nodeId, "REFERENCES");
                    }
                }
                if (comment.organismProteinRefs != null) {
                    for (final Integer organismProteinRef : comment.organismProteinRefs) {
                        final Long organismNodeId = organismRefNodeIdMap.get(organismProteinRef);
                        if (organismNodeId != null)
                            graph.addEdge(node, organismNodeId, "BELONGS_TO");
                        if (dataset.proteins != null) {
                            for (final String proteinRef : dataset.proteins) {
                                if (proteinRef.equals(organismProteinRef.toString())) {
                                    final Long[] proteinNodeIds = proteinRefNodeIdMap.get(organismProteinRef);
                                    if (proteinNodeIds != null) {
                                        for (final Long proteinNodeId : proteinNodeIds)
                                            graph.addEdge(node, proteinNodeId, "ASSOCIATED_WITH");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // TODO: association between organism/protein and references is not discernible
            final Node node = builder.build();
            graph.addEdge(enzymeNode, node, edgeLabel);
            connectDatasetWithEntities(graph, organismRefNodeIdMap, proteinRefNodeIdMap, publicationRefNodeIdMap,
                                       dataset, node);
        }
    }

    private CommentData[] parseComment(final String comment) {
        final String[] sentences = StringUtils.splitByWholeSeparator(comment, "; ");
        final CommentData[] result = new CommentData[sentences.length];
        for (int i = 0; i < sentences.length; i++) {
            result[i] = new CommentData();
            result[i].text = sentences[i];
            final Matcher organismsMatcher = ORGANISMS_PATTERN.matcher(sentences[i]);
            if (organismsMatcher.find()) {
                final String[] organismRefs = StringUtils.split(organismsMatcher.group(1), ',');
                result[i].organismProteinRefs = Arrays.stream(organismRefs).map(Integer::parseInt).toArray(
                        Integer[]::new);
            }
            final Matcher referencesMatcher = REFERENCES_PATTERN.matcher(sentences[i]);
            if (referencesMatcher.find()) {
                final String[] referenceRefs = StringUtils.split(referencesMatcher.group(1), ',');
                result[i].referenceRefs = Arrays.stream(referenceRefs).map(Integer::parseInt).toArray(Integer[]::new);
            }
        }
        return result;
    }

    private void connectDatasetWithEntities(final Graph graph, final Map<Integer, Long> organismRefNodeIdMap,
                                            final Map<Integer, Long[]> proteinRefNodeIdMap,
                                            final Map<Integer, Long> publicationRefNodeIdMap, final Dataset dataset,
                                            final Node node) {
        if (dataset.organisms != null) {
            for (final String organism : dataset.organisms) {
                final int organismRef = Integer.parseInt(organism);
                final Long nodeId = organismRefNodeIdMap.get(organismRef);
                graph.addEdge(node, nodeId, "BELONGS_TO");
            }
        }
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
                if (StringUtils.isNotEmpty(reference)) {
                    final int referenceRef = Integer.parseInt(reference);
                    final Long nodeId = publicationRefNodeIdMap.get(referenceRef);
                    if (nodeId != null)
                        graph.addEdge(node, nodeId, "REFERENCES");
                }
            }
        }
    }

    private void createValueNode(final Graph graph, final Node enzymeNode, final String edgeLabel,
                                 final Map<Integer, Long> organismRefNodeIdMap,
                                 final Map<Integer, Long[]> proteinRefNodeIdMap,
                                 final Map<Integer, Long> publicationRefNodeIdMap, final Dataset dataset) {
        final NodeBuilder builder = graph.buildNode().withLabel("Value");
        createDatasetNode(graph, enzymeNode, edgeLabel, organismRefNodeIdMap, proteinRefNodeIdMap,
                          publicationRefNodeIdMap, dataset, builder);
    }

    private void createTextValueNode(final Graph graph, final Node enzymeNode, final String edgeLabel,
                                     final Map<Integer, Long> organismRefNodeIdMap,
                                     final Map<Integer, Long[]> proteinRefNodeIdMap,
                                     final Map<Integer, Long> publicationRefNodeIdMap, final TextDataset dataset) {
        final NodeBuilder builder = graph.buildNode().withLabel("TextValue");
        builder.withPropertyIfNotNull("value", dataset.value);
        createDatasetNode(graph, enzymeNode, edgeLabel, organismRefNodeIdMap, proteinRefNodeIdMap,
                          publicationRefNodeIdMap, dataset, builder);
    }

    private void createReactionValueNode(final Graph graph, final Node enzymeNode, final String edgeLabel,
                                         final Map<Integer, Long> organismRefNodeIdMap,
                                         final Map<Integer, Long[]> proteinRefNodeIdMap,
                                         final Map<Integer, Long> publicationRefNodeIdMap,
                                         final ReactionDataset dataset) {
        final NodeBuilder builder = graph.buildNode().withLabel("ReactionValue");
        builder.withPropertyIfNotNull("educts", dataset.educts);
        builder.withPropertyIfNotNull("products", dataset.products);
        createDatasetNode(graph, enzymeNode, edgeLabel, organismRefNodeIdMap, proteinRefNodeIdMap,
                          publicationRefNodeIdMap, dataset, builder);
    }
}
