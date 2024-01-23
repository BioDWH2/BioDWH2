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
        return 2;
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
            while ((entry = inputStream.getNextTarEntry()) != null) {
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
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "substrate"), "HAS_KM");
        if (enzyme.turnoverNumber != null)
            for (final NumericDataset dataset : enzyme.turnoverNumber)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "substrate"),
                              "HAS_TURNOVER_NUMBER");
        if (enzyme.kcatKm != null)
            for (final NumericDataset dataset : enzyme.kcatKm)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "substrate"),
                              "HAS_KCAT_KM");
        if (enzyme.kiValue != null)
            for (final NumericDataset dataset : enzyme.kiValue)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "inhibitor"),
                              "HAS_KI_VALUE");
        if (enzyme.ic50 != null)
            for (final NumericDataset dataset : enzyme.ic50)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "inhibitor"), "HAS_IC50");
        if (enzyme.phRange != null)
            for (final NumericDataset dataset : enzyme.phRange)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "value"), "HAS_PH_RANGE");
        if (enzyme.phStability != null)
            for (final NumericDataset dataset : enzyme.phStability)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "value"),
                              "HAS_PH_STABILITY");
        if (enzyme.temperatureOptimum != null)
            for (final NumericDataset dataset : enzyme.temperatureOptimum)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "value"),
                              "HAS_TEMPERATURE_OPTIMUM");
        if (enzyme.temperatureRange != null)
            for (final NumericDataset dataset : enzyme.temperatureRange)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "value"),
                              "HAS_TEMPERATURE_RANGE");
        if (enzyme.temperatureStability != null)
            for (final NumericDataset dataset : enzyme.temperatureStability)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "value"),
                              "HAS_TEMPERATURE_STABILITY");
        if (enzyme.molecularWeight != null)
            for (final NumericDataset dataset : enzyme.molecularWeight)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "value"),
                              "HAS_MOLECULAR_WEIGHT");
        if (enzyme.isoelectricPoint != null)
            for (final NumericDataset dataset : enzyme.isoelectricPoint)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "value"),
                              "HAS_ISOELECTRIC_POINT");
        if (enzyme.phOptimum != null)
            for (final NumericDataset dataset : enzyme.phOptimum)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "value"),
                              "HAS_PH_OPTIMUM");
        if (enzyme.specificActivity != null)
            for (final NumericDataset dataset : enzyme.specificActivity)
                graph.addEdge(node, createNumericValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                           publicationRefNodeIdMap, dataset, "value"),
                              "HAS_SPECIFIC_ACTIVITY");
        if (enzyme.crystallization != null)
            for (final Dataset dataset : enzyme.crystallization)
                graph.addEdge(node,
                              createValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap, publicationRefNodeIdMap,
                                              dataset), "HAS_CRYSTALLIZATION");
        if (enzyme.purification != null)
            for (final Dataset dataset : enzyme.purification)
                graph.addEdge(node,
                              createValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap, publicationRefNodeIdMap,
                                              dataset), "HAS_PURIFICATION");
        if (enzyme.renaturation != null)
            for (final Dataset dataset : enzyme.renaturation)
                graph.addEdge(node,
                              createValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap, publicationRefNodeIdMap,
                                              dataset), "HAS_RENATURATION");
        if (enzyme.generalStability != null)
            for (final Dataset dataset : enzyme.generalStability)
                graph.addEdge(node,
                              createValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap, publicationRefNodeIdMap,
                                              dataset), "HAS_GENERAL_STABILITY");
        if (enzyme.oxygenStability != null)
            for (final Dataset dataset : enzyme.oxygenStability)
                graph.addEdge(node,
                              createValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap, publicationRefNodeIdMap,
                                              dataset), "HAS_OXYGEN_STABILITY");
        if (enzyme.storageStability != null)
            for (final Dataset dataset : enzyme.storageStability)
                graph.addEdge(node,
                              createValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap, publicationRefNodeIdMap,
                                              dataset), "HAS_STORAGE_STABILITY");
        if (enzyme.synonyms != null)
            for (final TextDataset dataset : enzyme.synonyms)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_SYNONYM");
        if (enzyme.application != null)
            for (final TextDataset dataset : enzyme.application)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_APPLICATION");
        if (enzyme.reactionType != null)
            for (final TextDataset dataset : enzyme.reactionType)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_REACTION_TYPE");
        if (enzyme.localization != null)
            for (final TextDataset dataset : enzyme.localization)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_LOCALIZATION");
        if (enzyme.tissue != null)
            for (final TextDataset dataset : enzyme.tissue)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_TISSUE");
        if (enzyme.activatingCompound != null)
            for (final TextDataset dataset : enzyme.activatingCompound)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_ACTIVATING_COMPOUND");
        if (enzyme.inhibitor != null)
            for (final TextDataset dataset : enzyme.inhibitor)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_INHIBITOR");
        if (enzyme.metalsIons != null)
            for (final TextDataset dataset : enzyme.metalsIons)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_METALS_IONS");
        if (enzyme.posttranslationalModification != null)
            for (final TextDataset dataset : enzyme.posttranslationalModification)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset),
                              "HAS_POSTTRANSLATIONAL_MODIFICATION");
        if (enzyme.subunits != null)
            for (final TextDataset dataset : enzyme.subunits)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_SUBUNITS");
        if (enzyme.cofactor != null)
            for (final TextDataset dataset : enzyme.cofactor)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_COFACTOR");
        if (enzyme.engineering != null)
            for (final TextDataset dataset : enzyme.engineering)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_ENGINEERING");
        if (enzyme.cloned != null)
            for (final TextDataset dataset : enzyme.cloned)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_CLONED");
        if (enzyme.organicSolventStability != null)
            for (final TextDataset dataset : enzyme.organicSolventStability)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset),
                              "HAS_ORGANIC_SOLVENT_STABILITY");
        if (enzyme.expression != null)
            for (final TextDataset dataset : enzyme.expression)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_EXPRESSION");
        if (enzyme.generalInformation != null)
            for (final TextDataset dataset : enzyme.generalInformation)
                graph.addEdge(node, createTextValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                        publicationRefNodeIdMap, dataset), "HAS_GENERAL_INFORMATION");
        if (enzyme.genericReaction != null)
            for (final ReactionDataset dataset : enzyme.genericReaction)
                graph.addEdge(node, createReactionValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                            publicationRefNodeIdMap, dataset), "HAS_GENERIC_REACTION");
        if (enzyme.naturalReaction != null)
            for (final ReactionDataset dataset : enzyme.naturalReaction)
                graph.addEdge(node, createReactionValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                            publicationRefNodeIdMap, dataset), "HAS_NATURAL_REACTION");
        if (enzyme.reaction != null)
            for (final ReactionDataset dataset : enzyme.reaction)
                graph.addEdge(node, createReactionValueNode(graph, organismRefNodeIdMap, proteinRefNodeIdMap,
                                                            publicationRefNodeIdMap, dataset), "HAS_REACTION");
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
        Long organismNodeId = organismNodeIdMap.get(speciesName);
        if (organismNodeId == null) {
            final SpeciesLookup.Entry species = SpeciesLookup.getByScientificName(speciesName);
            if (species != null && species.ncbiTaxId != null) {
                organismNodeId = graph.addNode(ORGANISM_LABEL, "ncbi_taxid", species.ncbiTaxId, "name", speciesName)
                                      .getId();
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
        Long publicationNodeId = pmidInt != null ? graph.findNode(PUBLICATION_LABEL, "pmid", pmidInt).getId() :
                                 publicationKeyNodeIdMap.get(key);
        if (publicationNodeId == null) {
            final NodeBuilder publicationBuilder = graph.buildNode().withLabel(PUBLICATION_LABEL);
            publicationBuilder.withPropertyIfNotNull("pmid", pmidInt);
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
                Protein protein = entry.getValue()[0];
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

    private Node createNumericValueNode(final Graph graph, final Map<Integer, Long> organismRefNodeIdMap,
                                        final Map<Integer, Long[]> proteinRefNodeIdMap,
                                        final Map<Integer, Long> publicationRefNodeIdMap, final NumericDataset dataset,
                                        final String valueKey) {
        final NodeBuilder builder = graph.buildNode().withLabel("NumericValue");
        builder.withPropertyIfNotNull("num_value", dataset.numValue);
        builder.withPropertyIfNotNull("min_value", dataset.minValue);
        builder.withPropertyIfNotNull("max_value", dataset.maxValue);
        builder.withPropertyIfNotNull("comment", dataset.comment);
        builder.withPropertyIfNotNull(valueKey, dataset.value);
        final Node node = builder.build();
        connectDatasetWithEntities(graph, organismRefNodeIdMap, proteinRefNodeIdMap, publicationRefNodeIdMap, dataset,
                                   node);
        return node;
    }

    private void connectDatasetWithEntities(final Graph graph, final Map<Integer, Long> organismRefNodeIdMap,
                                            final Map<Integer, Long[]> proteinRefNodeIdMap,
                                            final Map<Integer, Long> publicationRefNodeIdMap, final Dataset dataset,
                                            final Node node) {
        if (dataset.organisms != null) {
            for (final String organism : dataset.organisms) {
                final int organismRef = Integer.parseInt(organism);
                final Long organismNodeId = organismRefNodeIdMap.get(organismRef);
                graph.addEdge(node, organismNodeId, "BELONGS_TO");
            }
        }
        // TODO: references, protein
    }

    private CommentData[] parseComment(final String comment) {
        final String[] sentences = StringUtils.splitByWholeSeparator(comment, "; ");
        final CommentData[] result = new CommentData[sentences.length];
        for (int i = 0; i < sentences.length; i++) {
            result[i] = new CommentData();
            result[i].text = sentences[i];
            final Matcher organismsMatcher = ORGANISMS_PATTERN.matcher(sentences[i]);
            while (organismsMatcher.find()) {
                final String[] organismRefs = StringUtils.split(organismsMatcher.group(1), ',');
                result[i].organismRefs = Arrays.stream(organismRefs).map(Integer::parseInt).toArray(Integer[]::new);
            }
            final Matcher referencesMatcher = REFERENCES_PATTERN.matcher(sentences[i]);
            while (referencesMatcher.find()) {
                final String[] referenceRefs = StringUtils.split(referencesMatcher.group(1), ',');
                result[i].referenceRefs = Arrays.stream(referenceRefs).map(Integer::parseInt).toArray(Integer[]::new);
            }
        }
        return result;
    }

    private Node createValueNode(final Graph graph, final Map<Integer, Long> organismRefNodeIdMap,
                                 final Map<Integer, Long[]> proteinRefNodeIdMap,
                                 final Map<Integer, Long> publicationRefNodeIdMap, final Dataset dataset) {
        final NodeBuilder builder = graph.buildNode().withLabel("Value");
        builder.withPropertyIfNotNull("comment", dataset.comment);
        final Node node = builder.build();
        connectDatasetWithEntities(graph, organismRefNodeIdMap, proteinRefNodeIdMap, publicationRefNodeIdMap, dataset,
                                   node);
        return node;
    }

    private Node createTextValueNode(final Graph graph, final Map<Integer, Long> organismRefNodeIdMap,
                                     final Map<Integer, Long[]> proteinRefNodeIdMap,
                                     final Map<Integer, Long> publicationRefNodeIdMap, final TextDataset dataset) {
        final NodeBuilder builder = graph.buildNode().withLabel("TextValue");
        builder.withPropertyIfNotNull("comment", dataset.comment);
        builder.withPropertyIfNotNull("value", dataset.value);
        final Node node = builder.build();
        connectDatasetWithEntities(graph, organismRefNodeIdMap, proteinRefNodeIdMap, publicationRefNodeIdMap, dataset,
                                   node);
        return node;
    }

    private Node createReactionValueNode(final Graph graph, final Map<Integer, Long> organismRefNodeIdMap,
                                         final Map<Integer, Long[]> proteinRefNodeIdMap,
                                         final Map<Integer, Long> publicationRefNodeIdMap,
                                         final ReactionDataset dataset) {
        final NodeBuilder builder = graph.buildNode().withLabel("ReactionValue");
        builder.withPropertyIfNotNull("comment", dataset.comment);
        builder.withPropertyIfNotNull("educts", dataset.educts);
        builder.withPropertyIfNotNull("products", dataset.products);
        final Node node = builder.build();
        connectDatasetWithEntities(graph, organismRefNodeIdMap, proteinRefNodeIdMap, publicationRefNodeIdMap, dataset,
                                   node);
        return node;
    }
}
