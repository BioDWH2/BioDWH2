package de.unibi.agbi.biodwh2.string.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import de.unibi.agbi.biodwh2.string.STRINGDataSource;
import de.unibi.agbi.biodwh2.string.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class STRINGGraphExporter extends GraphExporter<STRINGDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(STRINGGraphExporter.class);
    static final String SPECIES_LABEL = "Species";
    static final String PROTEIN_LABEL = "Protein";
    static final String CLUSTER_LABEL = "Cluster";

    public STRINGGraphExporter(final STRINGDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(SPECIES_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(CLUSTER_LABEL, "id", IndexDescription.Type.UNIQUE));
        exportSpecies(workspace, graph);
        final Map<String, Long> proteinIdNodeIdMap = exportProteins(workspace, graph);
        exportCluster(workspace, graph);
        exportClusterProteinLinks(workspace, graph, proteinIdNodeIdMap);
        exportLinks(workspace, graph, proteinIdNodeIdMap);
        exportPhysicalLinks(workspace, graph, proteinIdNodeIdMap);
        return true;
    }

    private void exportSpecies(final Workspace workspace, final Graph graph) {
        for (final Species species : parseTsvFile(workspace, Species.class, "species.txt")) {
            graph.addNode(SPECIES_LABEL, "id", species.taxonId, "type", species.type, "name_compact",
                          species.nameCompact, "official_name_ncbi", species.officialNameNCBI, "domain",
                          species.domain);
        }
    }

    private <T> Iterable<T> parseTsvFile(final Workspace workspace, final Class<T> typeVariableClass,
                                         final String fileName) throws ExporterException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + fileName + "...");
        try {
            final MappingIterator<T> iterator;
            if (fileName.endsWith(".gz"))
                iterator = FileUtils.openGzipTsv(workspace, dataSource, fileName, typeVariableClass);
            else
                iterator = FileUtils.openTsv(workspace, dataSource, fileName, typeVariableClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private Map<String, Long> exportProteins(final Workspace workspace, final Graph graph) {
        final Map<String, Map<String, Set<String>>> aliases = collectProteinAliases(workspace);
        final Map<String, Long> proteinIdNodeIdMap = new HashMap<>();
        for (final ProteinInfo protein : parseTsvFile(workspace, ProteinInfo.class, "9606.protein.info.txt.gz")) {
            final Map<String, Set<String>> proteinAliases = aliases.get(protein.id);
            String[] nodeAliases = null;
            if (proteinAliases != null) {
                nodeAliases = new String[proteinAliases.size()];
                int i = 0;
                for (final String alias : proteinAliases.keySet()) {
                    final String[] sources = proteinAliases.get(alias).stream().sorted().toArray(String[]::new);
                    nodeAliases[i] = alias + ';' + String.join("|", sources);
                    i++;
                }
            }
            final Node node;
            if (nodeAliases != null)
                node = graph.addNode(PROTEIN_LABEL, "id", protein.id, "preferred_name", protein.preferredName, "size",
                                     protein.size, "annotation", protein.annotation, "aliases", nodeAliases);
            else
                node = graph.addNode(PROTEIN_LABEL, "id", protein.id, "preferred_name", protein.preferredName, "size",
                                     protein.size, "annotation", protein.annotation);
            proteinIdNodeIdMap.put(protein.id, node.getId());
        }
        return proteinIdNodeIdMap;
    }

    private Map<String, Map<String, Set<String>>> collectProteinAliases(final Workspace workspace) {
        final Map<String, Map<String, Set<String>>> aliases = new HashMap<>();
        for (final ProteinAlias alias : parseTsvFile(workspace, ProteinAlias.class, "9606.protein.aliases.txt.gz")) {
            final Map<String, Set<String>> proteinAliases = aliases.computeIfAbsent(alias.proteinId,
                                                                                    (id) -> new HashMap<>());
            final Set<String> aliasSources = proteinAliases.computeIfAbsent(alias.alias, (id) -> new HashSet<>());
            aliasSources.add(alias.source);
        }
        return aliases;
    }

    private void exportCluster(final Workspace workspace, final Graph graph) {
        for (final ClusterInfo cluster : parseTsvFile(workspace, ClusterInfo.class, "9606.clusters.info.txt.gz")) {
            graph.addNode(CLUSTER_LABEL, "id", cluster.clusterId, "ncbi_taxid", cluster.ncbiTaxId, "size",
                          cluster.clusterSize, "best_described_by", cluster.bestDescribedBy);
        }
        for (final ClusterTree tree : parseTsvFile(workspace, ClusterTree.class, "9606.clusters.tree.txt.gz")) {
            final Node childNode = graph.findNode(CLUSTER_LABEL, "id", tree.childClusterId);
            final Node parentNode = graph.findNode(CLUSTER_LABEL, "id", tree.parentClusterId);
            graph.addEdge(childNode, parentNode, "CHILD_OF");
        }
    }

    private void exportClusterProteinLinks(final Workspace workspace, final Graph graph,
                                           final Map<String, Long> proteinIdNodeIdMap) {
        long i = 0;
        Long totalLinkCount = FileUtils.tryGetGzipLineCount(workspace, dataSource, "9606.clusters.proteins.txt.gz");
        for (final ClusterProtein link : parseTsvFile(workspace, ClusterProtein.class,
                                                      "9606.clusters.proteins.txt.gz")) {
            final Node clusterNode = graph.findNode(CLUSTER_LABEL, "id", link.clusterId);
            graph.addEdge(proteinIdNodeIdMap.get(link.proteinId), clusterNode, "PART_OF");
            i += 1;
            logProgressIfNecessary(i, totalLinkCount);
        }
    }

    private void logProgressIfNecessary(final long current, final Long total) {
        if (total != null && current % 100_000 == 0)
            LOGGER.info(TextUtils.getProgressText(current, total));
    }

    private void exportLinks(final Workspace workspace, final Graph graph, final Map<String, Long> proteinIdNodeIdMap) {
        graph.beginEdgeIndicesDelay("ASSOCIATED_WITH");
        long i = 0;
        Long totalLinkCount = FileUtils.tryGetGzipLineCount(workspace, dataSource, "9606.protein.links.full.txt.gz");
        for (final ProteinLink link : parseSpaceSeparatedValuesFile(workspace, ProteinLink.class,
                                                                    "9606.protein.links.full.txt.gz")) {
            final Long proteinNode1 = proteinIdNodeIdMap.get(link.protein1);
            final Long proteinNode2 = proteinIdNodeIdMap.get(link.protein2);
            if (proteinNode1 == null || proteinNode2 == null) {
                LOGGER.warn("Failed to add association between " + proteinNode1 + " (" + link.protein1 + ") and " +
                            proteinNode2 + " (" + link.protein2 + ")");
                continue;
            }
            graph.buildEdge().withLabel("ASSOCIATED_WITH").fromNode(proteinNode1).toNode(proteinNode2).withModel(link)
                 .build();
            i += 1;
            logProgressIfNecessary(i, totalLinkCount);
        }
        graph.endEdgeIndicesDelay("ASSOCIATED_WITH");
    }

    private <T> Iterable<T> parseSpaceSeparatedValuesFile(final Workspace workspace, final Class<T> typeVariableClass,
                                                          final String fileName) throws ExporterException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + fileName + "...");
        try {
            final InputStream stream = FileUtils.openGzip(workspace, dataSource, fileName);
            final MappingIterator<T> iterator = FileUtils.openSeparatedValuesFile(stream, typeVariableClass, ' ', true);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private void exportPhysicalLinks(final Workspace workspace, final Graph graph,
                                     final Map<String, Long> proteinIdNodeIdMap) {
        graph.beginEdgeIndicesDelay("PHYSICALLY_INTERACTS_WITH");
        long i = 0;
        Long totalLinkCount = FileUtils.tryGetGzipLineCount(workspace, dataSource,
                                                            "9606.protein.physical.links.full.txt.gz");
        for (final ProteinPhysicalLink link : parseSpaceSeparatedValuesFile(workspace, ProteinPhysicalLink.class,
                                                                            "9606.protein.physical.links.full.txt.gz")) {
            final Long proteinNode1 = proteinIdNodeIdMap.get(link.protein1);
            final Long proteinNode2 = proteinIdNodeIdMap.get(link.protein2);
            if (proteinNode1 == null || proteinNode2 == null) {
                LOGGER.warn("Failed to add association between " + proteinNode1 + " (" + link.protein1 + ") and " +
                            proteinNode2 + " (" + link.protein2 + ")");
                continue;
            }
            graph.buildEdge().withLabel("PHYSICALLY_INTERACTS_WITH").fromNode(proteinNode1).toNode(proteinNode2)
                 .withModel(link).build();
            i += 1;
            logProgressIfNecessary(i, totalLinkCount);
        }
        graph.endEdgeIndicesDelay("PHYSICALLY_INTERACTS_WITH");
    }
}
