package de.unibi.agbi.biodwh2.pathwaycommons.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.gmt.GMTReader;
import de.unibi.agbi.biodwh2.core.io.gmt.GeneSet;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.pathwaycommons.PathwayCommonsDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PathwayCommonsGraphExporter extends GraphExporter<PathwayCommonsDataSource> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PathwayCommonsGraphExporter.class);

    static final String PATHWAY_LABEL = "Pathway";
    static final String GENE_LABEL = "Gene";
    static final String PROTEIN_LABEL = "Protein";

    public PathwayCommonsGraphExporter(final PathwayCommonsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(PATHWAY_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, "symbol", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, "id", IndexDescription.Type.UNIQUE));
        exportPathwayGeneSets(workspace, graph);
        exportPathwayProteinSets(workspace, graph);
        return true;
    }

    private void exportPathwayGeneSets(final Workspace workspace, final Graph graph) {
        try {
            final GMTReader reader = new GMTReader(
                    FileUtils.openGzip(workspace, dataSource, "PathwayCommons12.All.hgnc.gmt.gz"),
                    StandardCharsets.UTF_8);
            for (final GeneSet set : reader) {
                final Node node = getOrCreatePathway(graph, set);
                for (final String geneSymbol : set.getGenes()) {
                    final Node geneNode = getOrCreateGeneNode(graph, geneSymbol);
                    graph.addEdge(geneNode, node, "ASSOCIATED_WITH");
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private Node getOrCreatePathway(final Graph graph, final GeneSet set) {
        Node node = graph.findNode(PATHWAY_LABEL, "id", set.getName());
        if (node == null) {
            final Map<String, String> description = parseGeneSetDescription(set.getDescription());
            node = graph.addNode(PATHWAY_LABEL, "id", set.getName(), "name", description.get("name"), "organism",
                                 description.get("organism"), "source", description.get("datasource"));
        }
        return node;
    }

    private Map<String, String> parseGeneSetDescription(final String description) {
        final Map<String, String> result = new HashMap<>();
        if (description != null) {
            final int[] splitIndices = new int[]{
                    description.indexOf("; datasource:"), description.indexOf("; organism:"), description.indexOf(
                    "; idtype:")
            };
            final String[] descriptionParts = new String[]{
                    description.substring(0, splitIndices[0]), description.substring(splitIndices[0] + 1,
                                                                                     splitIndices[1]),
                    description.substring(splitIndices[1] + 1, splitIndices[2]), description.substring(
                    splitIndices[2] + 1)
            };
            for (final String part : descriptionParts) {
                final String[] keyValuePair = StringUtils.split(part, ":", 2);
                if (keyValuePair.length == 2)
                    result.put(keyValuePair[0].trim(), keyValuePair[1].trim());
                else
                    LOGGER.warn("Failed to parse gene set description: " + description);
            }
        }
        return result;
    }

    private Node getOrCreateGeneNode(final Graph graph, final String hgncSymbol) {
        Node node = graph.findNode(GENE_LABEL, "symbol", hgncSymbol);
        if (node == null)
            node = graph.addNode(GENE_LABEL, "symbol", hgncSymbol);
        return node;
    }

    private void exportPathwayProteinSets(final Workspace workspace, final Graph graph) {
        try {
            final GMTReader reader = new GMTReader(
                    FileUtils.openGzip(workspace, dataSource, "PathwayCommons12.All.uniprot.gmt.gz"),
                    StandardCharsets.UTF_8);
            for (final GeneSet set : reader) {
                final Node node = getOrCreatePathway(graph, set);
                for (final String uniprotAccession : set.getGenes()) {
                    final Node proteinNode = getOrCreateProteinNode(graph, uniprotAccession);
                    graph.addEdge(proteinNode, node, "ASSOCIATED_WITH");
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private Node getOrCreateProteinNode(final Graph graph, final String uniprotAccession) {
        Node node = graph.findNode(PROTEIN_LABEL, "id", uniprotAccession);
        if (node == null)
            node = graph.addNode(PROTEIN_LABEL, "id", uniprotAccession);
        return node;
    }
}
