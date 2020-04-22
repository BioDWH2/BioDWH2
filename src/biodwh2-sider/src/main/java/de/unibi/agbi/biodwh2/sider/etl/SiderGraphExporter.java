package de.unibi.agbi.biodwh2.sider.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.sider.SiderDataSource;
import de.unibi.agbi.biodwh2.sider.model.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * MedDRA concept types are hierarchical: PT - Represents a single medical concept LLT - Lowest level of the
 * terminology, related to a single PT as a synonym, lexical variant, or quasisynonym (Note: All PTs have an identical
 * LLT)
 */
public class SiderGraphExporter extends GraphExporter<SiderDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final SiderDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        graph.setIndexColumnNames("id", "flat_id", "stereo_id");
        addAllDrugNames(workspace, dataSource, graph);
        addAllDrugAtcCodes(workspace, dataSource, graph);
        addAllIndications(workspace, dataSource, graph);
        addAllSideEffects(workspace, dataSource, graph);
        addAllFrequencies(workspace, dataSource, graph);
        return true;
    }

    private void addAllDrugNames(final Workspace workspace, final DataSource dataSource,
                                 final Graph graph) throws ExporterException {
        MappingIterator<DrugName> iterator = parseTsvFile(workspace, dataSource, "drug_names.tsv", DrugName.class);
        while (iterator.hasNext()) {
            DrugName drugName = iterator.next();
            Node drugNode = createNode(graph, "Drug");
            drugNode.setProperty("id", drugName.id);
            drugNode.setProperty("name", drugName.name);
        }
    }

    private <T> MappingIterator<T> parseTsvFile(final Workspace workspace, final DataSource dataSource,
                                                final String fileName,
                                                final Class<T> typeClass) throws ExporterException {
        try {
            String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
            return parseTsvFile(typeClass, reader);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private <T> MappingIterator<T> parseTsvFile(final Class<T> typeClass,
                                                final BufferedReader inputReader) throws IOException {
        ObjectReader reader = getFormatReader(typeClass);
        return reader.readValues(inputReader);
    }

    private <T> ObjectReader getFormatReader(final Class<T> typeClass) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(typeClass).withColumnSeparator('\t').withNullValue("");
        return csvMapper.readerFor(typeClass).with(schema);
    }

    private void addAllDrugAtcCodes(final Workspace workspace, final DataSource dataSource,
                                    final Graph graph) throws ExporterException {
        MappingIterator<DrugAtc> iterator = parseTsvFile(workspace, dataSource, "drug_atc.tsv", DrugAtc.class);
        while (iterator.hasNext()) {
            DrugAtc atcCode = iterator.next();
            Node drugNode = graph.findNode("Drug", "id", atcCode.id);
            List<String> atcCodes = new ArrayList<>();
            if (drugNode.hasProperty("atc_codes"))
                Collections.addAll(atcCodes, drugNode.getProperty("atc_codes"));
            atcCodes.add(atcCode.atc);
            drugNode.setProperty("atc_codes", atcCodes.toArray(new String[0]));
        }
    }

    private void addAllIndications(final Workspace workspace, final DataSource dataSource,
                                   final Graph graph) throws ExporterException {
        MappingIterator<Indication> iterator = parseGzipTsvFile(workspace, dataSource,
                                                                "meddra_all_label_indications.tsv.gz",
                                                                Indication.class);
        while (iterator.hasNext()) {
            Indication indication = iterator.next();
            Node meddraTermNode = getOrAddmeddraTermNode(graph, indication.meddraUmlsConceptId);
            if (!indication.umlsConceptId.equals(indication.meddraUmlsConceptId))
                meddraTermNode.setProperty("umls_concept_id", indication.umlsConceptId);
            meddraTermNode.setProperty("concept_name", indication.conceptName);
            updateConceptTypeIfPreviouslyLLTOrEmpty(meddraTermNode, indication.meddraConceptType);
            meddraTermNode.setProperty("meddra_concept_name", indication.meddraConceptName);
            Long drugNodeId = getOrAddDrugNode(graph, indication.stereoCompoundId);
            Edge edge = graph.addEdge(drugNodeId, meddraTermNode, "indicates");
            edge.setProperty("label", indication.label);
            edge.setProperty("detection_method", indication.detectionMethod);
        }
    }

    private Node getOrAddmeddraTermNode(final Graph graph, final String meddraUmlsConceptId) throws ExporterException {
        Node meddraTermNode = graph.findNode("meddraTerm", "id", meddraUmlsConceptId);
        if (meddraTermNode == null) {
            meddraTermNode = createNode(graph, "meddraTerm");
            meddraTermNode.setProperty("id", meddraUmlsConceptId);
        }
        return meddraTermNode;
    }

    private void updateConceptTypeIfPreviouslyLLTOrEmpty(final Node meddraTermNode,
                                                         final String conceptType) throws ExporterException {
        if (meddraTermNode.hasProperty("meddra_concept_type") && conceptType.equals("PT"))
            meddraTermNode.setProperty("meddra_concept_type", conceptType);
        else
            meddraTermNode.setProperty("meddra_concept_type", conceptType);
    }

    private Long getOrAddDrugNode(final Graph graph, final String compoundId) throws ExporterException {
        Long drugNodeId = graph.findNodeId("Drug", "id", compoundId);
        if (drugNodeId == null) {
            Node drugNode = createNode(graph, "Drug");
            drugNode.setProperty("id", compoundId);
            drugNodeId = drugNode.getId();
        }
        return drugNodeId;
    }

    private <T> MappingIterator<T> parseGzipTsvFile(final Workspace workspace, final DataSource dataSource,
                                                    final String fileName,
                                                    final Class<T> typeClass) throws ExporterException {
        try {
            String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
            BufferedReader reader = getReaderForGzipTsvFile(filePath);
            return parseTsvFile(typeClass, reader);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private BufferedReader getReaderForGzipTsvFile(final String filePath) throws IOException {
        GZIPInputStream zipStream = new GZIPInputStream(new FileInputStream(filePath));
        return new BufferedReader(new InputStreamReader(zipStream, StandardCharsets.UTF_8));
    }

    private void addAllSideEffects(final Workspace workspace, final DataSource dataSource,
                                   final Graph graph) throws ExporterException {
        MappingIterator<SideEffect> iterator = parseGzipTsvFile(workspace, dataSource, "meddra_all_label_se.tsv.gz",
                                                                SideEffect.class);
        while (iterator.hasNext()) {
            SideEffect sideEffect = iterator.next();
            Node meddraTermNode = getOrAddmeddraTermNode(graph, sideEffect.meddraUmlsConceptId);
            if (!sideEffect.umlsConceptId.equals(sideEffect.meddraUmlsConceptId))
                meddraTermNode.setProperty("umls_concept_id", sideEffect.umlsConceptId);
            updateConceptTypeIfPreviouslyLLTOrEmpty(meddraTermNode, sideEffect.meddraConceptType);
            meddraTermNode.setProperty("meddra_concept_name", sideEffect.sideEffectName);
            Long drugNodeId = getOrAddDrugNode(graph, sideEffect.stereoCompoundId);
            Edge edge = graph.addEdge(drugNodeId, meddraTermNode, "has_side_effect");
            edge.setProperty("label", sideEffect.label);
        }
    }

    private void addAllFrequencies(final Workspace workspace, final DataSource dataSource,
                                   final Graph graph) throws ExporterException {
        MappingIterator<Frequency> iterator = parseGzipTsvFile(workspace, dataSource, "meddra_freq.tsv.gz",
                                                               Frequency.class);
        while (iterator.hasNext()) {
            Frequency frequency = iterator.next();
            Node meddraTermNode = getOrAddmeddraTermNode(graph, frequency.meddraUmlsConceptId);
            if (!frequency.umlsConceptId.equals(frequency.meddraUmlsConceptId))
                meddraTermNode.setProperty("umls_concept_id", frequency.umlsConceptId);
            updateConceptTypeIfPreviouslyLLTOrEmpty(meddraTermNode, frequency.meddraConceptType);
            meddraTermNode.setProperty("meddra_concept_name", frequency.sideEffectName);
            Long drugNodeId = getOrAddDrugNode(graph, frequency.stereoCompoundId);
            Edge edge = graph.addEdge(drugNodeId, meddraTermNode, "has_side_effect");
            edge.setProperty("frequency", frequency.frequency);
            edge.setProperty("frequency_lower_bound", frequency.frequencyLowerBound);
            edge.setProperty("frequency_upper_bound", frequency.frequencyUpperBound);
            if (frequency.placebo != null)
                edge.setProperty("placebo", frequency.placebo.equalsIgnoreCase("placebo"));
        }
    }
}
