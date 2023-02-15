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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrendaGraphExporter extends GraphExporter<BrendaDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(BrendaGraphExporter.class);
    private static final Pattern ORGANISMS_PATTERN = Pattern.compile("#(\\d+(,\\d+)*)#");
    private static final Pattern REFERENCES_PATTERN = Pattern.compile("<(\\d+(,\\d+)*)>");

    public BrendaGraphExporter(final BrendaDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Enzyme", ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Protein", "accession", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Publication", "pmid", IndexDescription.Type.UNIQUE));
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
        return false;
    }

    private void exportBrenda(final Graph graph, final Brenda brenda) {
        final Map<String, Long> organismNodeIdMap = new HashMap<>();
        for (final Enzyme enzyme : brenda.data.values())
            exportEnzyme(graph, enzyme, organismNodeIdMap);
    }

    private void exportEnzyme(final Graph graph, final Enzyme enzyme, final Map<String, Long> organismNodeIdMap) {
        final Map<Integer, Long> organismRefNodeIdMap = new HashMap<>();
        if (enzyme.organisms != null) {
            for (final Map.Entry<Integer, Organism> organism : enzyme.organisms.entrySet()) {
                Long organismNodeId = getOrCreateOrganismNode(graph, organism.getValue().value, organismNodeIdMap);
                organismRefNodeIdMap.put(organism.getKey(), organismNodeId);
            }
        }
        final Map<Integer, Long> publicationRefNodeIdMap = new HashMap<>();
        if (enzyme.references != null) {
            for (final Map.Entry<Integer, Reference> reference : enzyme.references.entrySet()) {
                Long publicationNodeId = getOrCreatePublicationNode(graph, reference.getValue());
                publicationRefNodeIdMap.put(reference.getKey(), publicationNodeId);
            }
        }
        final Map<Integer, Long[]> proteinRefNodeIdMap = new HashMap<>();
        if (enzyme.proteins != null) {
            for (final Map.Entry<Integer, Protein[]> protein : enzyme.proteins.entrySet()) {
                if (protein.getValue().length > 1)
                    LOGGER.warn("Proteins ref with " + protein.getValue().length + " entries found");
                final Long[] proteinNodeIds = getOrCreateProteinNodes(graph, protein.getValue()[0]);
                proteinRefNodeIdMap.put(protein.getKey(), proteinNodeIds);
            }
        }
        final NodeBuilder builder = graph.buildNode().withLabel("Enzyme");
        builder.withProperty(ID_KEY, enzyme.id);
        builder.withPropertyIfNotNull("name", enzyme.name);
        builder.withPropertyIfNotNull("systematic_name", enzyme.systematicName);
        final Node node = builder.build();
        if (enzyme.synonyms != null) {
            for (final TextDataset dataset : enzyme.synonyms) {
                final Node synonymNode;
                if (dataset.comment != null)
                    synonymNode = graph.addNode("Synonym", "value", dataset.value, "comment", dataset.comment);
                else
                    synonymNode = graph.addNode("Synonym", "value", dataset.value);
                graph.addEdge(node, synonymNode, "HAS_SYNONYM");
            }
        }
        if (enzyme.kmValue != null)
            for (final NumericDataset dataset : enzyme.kmValue)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_KM");
        if (enzyme.phOptimum != null)
            for (final NumericDataset dataset : enzyme.phOptimum)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_PH_OPTIMUM");
        if (enzyme.phRange != null)
            for (final NumericDataset dataset : enzyme.phRange)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_PH_RANGE");
        if (enzyme.phStability != null)
            for (final NumericDataset dataset : enzyme.phStability)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_PH_STABILITY");
        if (enzyme.turnoverNumber != null)
            for (final NumericDataset dataset : enzyme.turnoverNumber)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_TURNOVER_NUMBER");
        if (enzyme.specificActivity != null)
            for (final NumericDataset dataset : enzyme.specificActivity)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_SPECIFIC_ACTIVITY");
        if (enzyme.temperatureOptimum != null)
            for (final NumericDataset dataset : enzyme.temperatureOptimum)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_TEMPERATURE_OPTIMUM");
        if (enzyme.temperatureRange != null)
            for (final NumericDataset dataset : enzyme.temperatureRange)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_TEMPERATURE_RANGE");
        if (enzyme.temperatureStability != null)
            for (final NumericDataset dataset : enzyme.temperatureStability)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_TEMPERATURE_STABILITY");
        if (enzyme.molecularWeight != null)
            for (final NumericDataset dataset : enzyme.molecularWeight)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_MOLECULAR_WEIGHT");
        if (enzyme.isoelectricPoint != null)
            for (final NumericDataset dataset : enzyme.isoelectricPoint)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_ISOELECTRIC_POINT");
        if (enzyme.kiValue != null)
            for (final NumericDataset dataset : enzyme.kiValue)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_KI_VALUE");
        if (enzyme.ic50 != null)
            for (final NumericDataset dataset : enzyme.ic50)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_IC50");
        if (enzyme.kcatKm != null)
            for (final NumericDataset dataset : enzyme.kcatKm)
                graph.addEdge(node, createNumericValueNode(graph, dataset), "HAS_KCAT_KM");
        /* TODO
        TextDataset[] synonyms;
        Dataset[] crystallization;
        Dataset[] purification;
        Dataset[] renaturation;
        Dataset[] generalStability;
        Dataset[] oxygenStability;
        Dataset[] storageStability;
        ReactionDataset[] genericReaction;
        ReactionDataset[] naturalReaction;
        ReactionDataset[] reaction;
        TextDataset[] localization;
        TextDataset[] tissue;
        TextDataset[] activatingCompound;
        TextDataset[] inhibitor;
        TextDataset[] metalsIons;
        TextDataset[] posttranslationalModification;
        TextDataset[] subunits;
        TextDataset[] cofactor;
        TextDataset[] engineering;
        TextDataset[] cloned;
        TextDataset[] organicSolventStability;
        TextDataset[] expression;
        TextDataset[] generalInformation;
        */
    }

    private Long getOrCreateOrganismNode(final Graph graph, final String speciesName,
                                         final Map<String, Long> organismNodeIdMap) {
        Long organismNodeId = organismNodeIdMap.get(speciesName);
        if (organismNodeId == null) {
            final SpeciesLookup.Entry species = SpeciesLookup.getByScientificName(speciesName);
            if (species != null && species.ncbiTaxId != null) {
                organismNodeId = graph.addNode("Organism", "ncbi_taxid", species.ncbiTaxId, "name", speciesName)
                                      .getId();
            } else {
                organismNodeId = graph.addNode("Organism", "name", speciesName).getId();
            }
            organismNodeIdMap.put(speciesName, organismNodeId);
        }
        return organismNodeId;
    }

    private Long getOrCreatePublicationNode(final Graph graph, final Reference reference) {
        // TODO: key lookup if pmid is null
        Long pmid = reference.pmid;
        if (pmid != null && pmid == 3020186354L)
            pmid = 30201863L;
        final Integer pmidInt = pmid != null ? pmid.intValue() : null;
        Node publicationNode = null;
        if (pmidInt != null)
            publicationNode = graph.findNode("Publication", "pmid", pmidInt);
        if (publicationNode == null) {
            final NodeBuilder publicationBuilder = graph.buildNode().withLabel("Publication");
            publicationBuilder.withPropertyIfNotNull("pmid", pmidInt);
            publicationBuilder.withPropertyIfNotNull("authors", reference.authors);
            publicationBuilder.withPropertyIfNotNull("journal", reference.journal);
            publicationBuilder.withPropertyIfNotNull("volume", reference.volume);
            publicationBuilder.withPropertyIfNotNull("year", reference.year);
            publicationBuilder.withPropertyIfNotNull("pages", reference.pages);
            publicationNode = publicationBuilder.build();
        }
        return publicationNode.getId();
    }

    private Long[] getOrCreateProteinNodes(final Graph graph, final Protein protein) {
        // TODO: comment
        final Set<Long> nodeIds = new HashSet<>();
        for (final String accession : protein.accessions) {
            Node node = graph.findNode("Protein", "accession", accession);
            if (node == null)
                node = graph.addNode("Protein", "accession", accession, "source", protein.source);
            nodeIds.add(node.getId());
        }
        return nodeIds.toArray(new Long[0]);
    }

    private Node createNumericValueNode(final Graph graph, final NumericDataset dataset) {
        final NodeBuilder builder = graph.buildNode().withLabel("NumericValue");
        builder.withPropertyIfNotNull("num_value", dataset.numValue);
        builder.withPropertyIfNotNull("min_value", dataset.minValue);
        builder.withPropertyIfNotNull("max_value", dataset.maxValue);
        builder.withPropertyIfNotNull("comment", dataset.comment);
        builder.withPropertyIfNotNull("value", dataset.value);
        return builder.build();
        // TODO: organism, references, protein
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
            final Matcher referencesMatcher = ORGANISMS_PATTERN.matcher(sentences[i]);
            while (referencesMatcher.find()) {
                final String[] referenceRefs = StringUtils.split(referencesMatcher.group(1), ',');
                result[i].referenceRefs = Arrays.stream(referenceRefs).map(Integer::parseInt).toArray(Integer[]::new);
            }
        }
        return result;
    }
}
