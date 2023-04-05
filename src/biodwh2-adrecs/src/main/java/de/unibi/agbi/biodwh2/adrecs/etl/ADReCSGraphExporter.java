package de.unibi.agbi.biodwh2.adrecs.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.adrecs.ADReCSDataSource;
import de.unibi.agbi.biodwh2.adrecs.model.ADROntologyEntry;
import de.unibi.agbi.biodwh2.adrecs.model.DrugADREntry;
import de.unibi.agbi.biodwh2.adrecs.model.DrugADRQuantificationEntry;
import de.unibi.agbi.biodwh2.adrecs.model.DrugInformationEntry;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.collections.Tuple2;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.XlsxMappingIterator;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ADReCSGraphExporter extends GraphExporter<ADReCSDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(ADReCSGraphExporter.class);
    static final String DRUG_LABEL = "Drug";
    static final String ADR_LABEL = "ADR";
    private static final String ADRECS_ID_KEY = "adrecs_id";

    public ADReCSGraphExporter(final ADReCSDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 5;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ADR_LABEL, ID_KEY, IndexDescription.Type.NON_UNIQUE));
        graph.addIndex(IndexDescription.forNode(ADR_LABEL, ADRECS_ID_KEY, IndexDescription.Type.NON_UNIQUE));
        try {
            exportDrugs(workspace, graph);
            exportADROntology(workspace, graph);
            exportADRDrugAssociations(workspace, graph);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private void exportDrugs(final Workspace workspace, final Graph graph) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting Drug information...");
        final InputStream stream = FileUtils.openInput(workspace, dataSource, ADReCSUpdater.DRUG_INFO_FILE_NAME);
        final XlsxMappingIterator<DrugInformationEntry> iterator = new XlsxMappingIterator<>(DrugInformationEntry.class,
                                                                                             stream);
        while (iterator.hasNext())
            graph.addNodeFromModel(iterator.next());
        iterator.close();
    }

    private void exportADROntology(final Workspace workspace, final Graph graph) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting ADR ontology...");
        final Map<String, Map<String, Map<String, Set<String>>>> hierarchy = new HashMap<>();
        final InputStream stream = FileUtils.openInput(workspace, dataSource, ADReCSUpdater.ADR_ONTOLOGY_FILE_NAME);
        final XlsxMappingIterator<ADROntologyEntry> iterator = new XlsxMappingIterator<>(ADROntologyEntry.class,
                                                                                         stream);
        while (iterator.hasNext()) {
            final ADROntologyEntry entry = iterator.next();
            graph.addNodeFromModel(entry);
            final String[] adrecsIdParts = StringUtils.split(entry.adrecsId, '.');
            if (adrecsIdParts.length > 0) {
                final Map<String, Map<String, Set<String>>> level1 = hierarchy.computeIfAbsent(adrecsIdParts[0],
                                                                                               (k) -> new HashMap<>());
                if (adrecsIdParts.length > 1) {
                    final Map<String, Set<String>> level2 = level1.computeIfAbsent(adrecsIdParts[1],
                                                                                   (k) -> new HashMap<>());
                    if (adrecsIdParts.length > 2) {
                        final Set<String> level3 = level2.computeIfAbsent(adrecsIdParts[2], (k) -> new HashSet<>());
                        if (adrecsIdParts.length > 3) {
                            level3.add(adrecsIdParts[3]);
                        }
                    }
                }
            }
        }
        iterator.close();
        // Export the ADReCS id hierarchy
        for (final String level1Key : hierarchy.keySet()) {
            final Long[] level1Ids = getNodeIds(graph.findNodes(ADR_LABEL, ADRECS_ID_KEY, level1Key));
            for (final String level2Key : hierarchy.get(level1Key).keySet()) {
                final String level2FullKey = level1Key + '.' + level2Key;
                final Long[] level2Ids = getNodeIds(graph.findNodes(ADR_LABEL, ADRECS_ID_KEY, level2FullKey));
                for (final long level1Id : level1Ids)
                    for (final long level2Id : level2Ids)
                        graph.addEdge(level2Id, level1Id, "CHILD_OF");
                for (final String level3Key : hierarchy.get(level1Key).get(level2Key).keySet()) {
                    final String level3FullKey = level2FullKey + '.' + level3Key;
                    final Long[] level3Ids = getNodeIds(graph.findNodes(ADR_LABEL, ADRECS_ID_KEY, level3FullKey));
                    for (final long level2Id : level2Ids)
                        for (final long level3Id : level3Ids)
                            graph.addEdge(level3Id, level2Id, "CHILD_OF");
                    for (final String level4Key : hierarchy.get(level1Key).get(level2Key).get(level3Key)) {
                        final String level4FullKey = level3FullKey + '.' + level4Key;
                        final Long[] level4Ids = getNodeIds(graph.findNodes(ADR_LABEL, ADRECS_ID_KEY, level4FullKey));
                        for (final long level3Id : level3Ids)
                            for (final long level4Id : level4Ids)
                                graph.addEdge(level4Id, level3Id, "CHILD_OF");
                    }
                }
            }
        }
    }

    private Long[] getNodeIds(Iterable<Node> nodes) {
        final List<Long> ids = new ArrayList<>();
        for (final Node n : nodes)
            ids.add(n.getId());
        return ids.toArray(new Long[0]);
    }

    private void exportADRDrugAssociations(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting ADR drug associations...");
        Map<String, Map<String, List<Tuple2<String, String>>>> associations = new HashMap<>();
        try (final MappingIterator<DrugADREntry> iterator = FileUtils.openGzipTsv(workspace, dataSource,
                                                                                  ADReCSUpdater.DRUG_ADR_FILE_NAME,
                                                                                  DrugADREntry.class)) {
            while (iterator.hasNext()) {
                final DrugADREntry entry = iterator.next();
                final Map<String, List<Tuple2<String, String>>> diseases = associations.computeIfAbsent(entry.drugId,
                                                                                                        s -> new HashMap<>());
                final List<Tuple2<String, String>> frequencies = diseases.computeIfAbsent(entry.adrId,
                                                                                          s -> new ArrayList<>());
                if (frequencies.size() == 0)
                    frequencies.add(new Tuple2<>(null, null));
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        try (final MappingIterator<DrugADRQuantificationEntry> iterator = FileUtils.openGzipTsv(workspace, dataSource,
                                                                                                ADReCSUpdater.DRUG_ADR_QUANTIFICATION_FILE_NAME,
                                                                                                DrugADRQuantificationEntry.class)) {
            while (iterator.hasNext()) {
                final DrugADRQuantificationEntry entry = iterator.next();
                final Map<String, List<Tuple2<String, String>>> diseases = associations.computeIfAbsent(entry.drugId,
                                                                                                        s -> new HashMap<>());
                final List<Tuple2<String, String>> frequencies = diseases.computeIfAbsent(entry.adrId,
                                                                                          s -> new ArrayList<>());
                boolean added = false;
                for (int i = 0; i < frequencies.size(); i++) {
                    final Tuple2<String, String> tuple = frequencies.get(i);
                    if (tuple.getFirst() == null && tuple.getSecond() == null) {
                        frequencies.set(i, new Tuple2<>(entry.adrSeverityGradeFAERS, entry.adrFrequencyFAERS));
                        added = true;
                        break;
                    }
                }
                if (!added)
                    frequencies.add(new Tuple2<>(entry.adrSeverityGradeFAERS, entry.adrFrequencyFAERS));
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        for (final String drugId : associations.keySet()) {
            final Node drugNode = graph.findNode(DRUG_LABEL, ID_KEY, drugId);
            for (final String adrId : associations.get(drugId).keySet()) {
                final Iterable<Node> adrNodes = graph.findNodes(ADR_LABEL, ID_KEY, adrId);
                for (final Tuple2<String, String> association : associations.get(drugId).get(adrId)) {
                    if (association.getFirst() != null && association.getSecond() != null) {
                        for (final Node adrNode : adrNodes) {
                            graph.addEdge(drugNode, adrNode, "ASSOCIATED_WITH", "frequency_faers",
                                          association.getSecond(), "severity_grade_faers", association.getFirst());
                        }
                    } else {
                        for (final Node adrNode : adrNodes) {
                            graph.addEdge(drugNode, adrNode, "ASSOCIATED_WITH");
                        }
                    }
                }
            }
        }
    }
}
