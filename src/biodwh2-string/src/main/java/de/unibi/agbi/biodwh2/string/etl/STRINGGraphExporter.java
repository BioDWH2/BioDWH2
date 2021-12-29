package de.unibi.agbi.biodwh2.string.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.string.STRINGDataSource;
import de.unibi.agbi.biodwh2.string.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class STRINGGraphExporter extends GraphExporter<STRINGDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(STRINGGraphExporter.class);
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
        graph.addIndex(IndexDescription.forNode(SPECIES_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(CLUSTER_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        exportSpecies(workspace, graph);
        exportProteins(workspace, graph);
        exportCluster(workspace, graph);
        exportClusterProteinLinks(workspace, graph);
        exportLinks(workspace, graph);
        exportPhysicalLinks(workspace, graph);
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

    private void exportProteins(final Workspace workspace, final Graph graph) {
        final Map<String, Map<String, Set<String>>> aliases = collectProteinAliases(workspace);
        for (final ProteinInfo protein : parseTsvFile(workspace, ProteinInfo.class, "9606.protein.info.txt.gz")) {
            final Map<String, Set<String>> proteinAliases = aliases.get(protein.proteinId);
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
            if (nodeAliases != null)
                graph.addNode(PROTEIN_LABEL, "id", protein.proteinId, "preferred_name", protein.preferredName, "size",
                              protein.proteinSize, "annotation", protein.annotation, "aliases", nodeAliases);
            else
                graph.addNode(PROTEIN_LABEL, "id", protein.proteinId, "preferred_name", protein.preferredName, "size",
                              protein.proteinSize, "annotation", protein.annotation);
        }
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

    private void exportClusterProteinLinks(final Workspace workspace, final Graph graph) {
        long i = 0;
        Long totalLinkCount = FileUtils.tryGetGzipLineCount(workspace, dataSource, "9606.clusters.proteins.txt.gz");
        for (final ClusterProtein link : parseTsvFile(workspace, ClusterProtein.class,
                                                      "9606.clusters.proteins.txt.gz")) {
            final Node clusterNode = graph.findNode(CLUSTER_LABEL, "id", link.clusterId);
            final Node proteinNode = graph.findNode(PROTEIN_LABEL, "id", link.proteinId);
            graph.addEdge(proteinNode, clusterNode, "PART_OF");
            i += 1;
            if (totalLinkCount != null && i % 100_000 == 0) {
                final String percent = String.format("%.2f", 100.0 / totalLinkCount * i);
                LOGGER.info(i + "/" + totalLinkCount + " (" + percent + "%)");
            }
        }
    }

    private void exportLinks(final Workspace workspace, final Graph graph) {
        long i = 0;
        Long totalLinkCount = FileUtils.tryGetGzipLineCount(workspace, dataSource, "9606.protein.links.full.txt.gz");
        for (final ProteinLink link : parseSpaceSeparatedValuesFile(workspace, ProteinLink.class,
                                                                    "9606.protein.links.full.txt.gz")) {
            final Node proteinNode1 = graph.findNode(PROTEIN_LABEL, "id", link.protein1);
            final Node proteinNode2 = graph.findNode(PROTEIN_LABEL, "id", link.protein2);
            if (proteinNode1 == null || proteinNode2 == null) {
                LOGGER.warn("Failed to add association between " + proteinNode1 + " (" + link.protein1 + ") and " +
                            proteinNode2 + " (" + link.protein2 + ")");
                continue;
            }
            graph.buildEdge().withLabel("ASSOCIATED_WITH").fromNode(proteinNode1).toNode(proteinNode2).withModel(link)
                 .build();
            i += 1;
            if (totalLinkCount != null && i % 100_000 == 0) {
                final String percent = String.format("%.2f", 100.0 / totalLinkCount * i);
                LOGGER.info(i + "/" + totalLinkCount + " (" + percent + "%)");
            }
        }
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

    private void exportPhysicalLinks(final Workspace workspace, final Graph graph) {
        long i = 0;
        Long totalLinkCount = FileUtils.tryGetGzipLineCount(workspace, dataSource,
                                                            "9606.protein.physical.links.full.txt.gz");
        for (final ProteinPhysicalLink link : parseSpaceSeparatedValuesFile(workspace, ProteinPhysicalLink.class,
                                                                            "9606.protein.physical.links.full.txt.gz")) {
            final Node proteinNode1 = graph.findNode(PROTEIN_LABEL, "id", link.protein1);
            final Node proteinNode2 = graph.findNode(PROTEIN_LABEL, "id", link.protein2);
            if (proteinNode1 == null || proteinNode2 == null) {
                LOGGER.warn("Failed to add association between " + proteinNode1 + " (" + link.protein1 + ") and " +
                            proteinNode2 + " (" + link.protein2 + ")");
                continue;
            }
            graph.buildEdge().withLabel("PHYSICALLY_INTERACTS_WITH").fromNode(proteinNode1).toNode(proteinNode2)
                 .withModel(link).build();
            i += 1;
            if (totalLinkCount != null && i % 100_000 == 0) {
                final String percent = String.format("%.2f", 100.0 / totalLinkCount * i);
                LOGGER.info(i + "/" + totalLinkCount + " (" + percent + "%)");
            }
        }
    }
}
