package de.unibi.agbi.biodwh2.sider.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.sider.SiderDataSource;
import de.unibi.agbi.biodwh2.sider.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MedDRA concept types are hierarchical: PT - Represents a single medical concept LLT - Lowest level of the
 * terminology, related to a single PT as a synonym, lexical variant, or quasisynonym (Note: All PTs have an identical
 * LLT)
 */
public class SiderGraphExporter extends GraphExporter<SiderDataSource> {
    public SiderGraphExporter(final SiderDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace,
                                  final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys("id", "flat_id", "stereo_id");
        addAllDrugNames(workspace, dataSource, graph);
        addAllDrugAtcCodes(workspace, dataSource, graph);
        addAllIndications(workspace, dataSource, graph);
        addAllSideEffects(workspace, dataSource, graph);
        addAllSideEffectFrequencies(workspace, dataSource, graph);
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
            graph.update(drugNode);
        }
    }

    private <T> MappingIterator<T> parseTsvFile(final Workspace workspace, final DataSource dataSource,
                                                final String fileName,
                                                final Class<T> typeClass) throws ExporterException {
        try {
            return FileUtils.openTsv(workspace, dataSource, fileName, typeClass);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + fileName + "'", e);
        }
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
            graph.update(drugNode);
        }
    }

    private void addAllIndications(final Workspace workspace, final DataSource dataSource,
                                   final Graph graph) throws ExporterException {
        List<Indication> indications = parseGzipTsvFile(workspace, dataSource, "meddra_all_label_indications.tsv.gz",
                                                        Indication.class);
        for (Indication indication : indications) {
            Node meddraTermNode = getOrAddmeddraTermNode(graph, indication.getConceptId());
            if (!indication.umlsConceptId.equals(indication.meddraUmlsConceptId))
                meddraTermNode.setProperty("umls_concept_id", indication.umlsConceptId);
            meddraTermNode.setProperty("concept_name", indication.conceptName);
            meddraTermNode.setProperty("meddra_concept_name", indication.meddraConceptName);
            graph.update(meddraTermNode);
            Node drugNode = getOrAddDrugNode(graph, indication.stereoCompoundId);
            Edge edge = graph.addEdge(drugNode, meddraTermNode, "INDICATES");
            edge.setProperty("label", indication.label);
            edge.setProperty("detection_method", indication.detectionMethod);
            graph.update(edge);
        }
    }

    private Node getOrAddmeddraTermNode(final Graph graph, final String conceptId) {
        Node meddraTermNode = graph.findNode("meddraTerm", "id", conceptId);
        if (meddraTermNode == null) {
            meddraTermNode = createNode(graph, "meddraTerm");
            meddraTermNode.setProperty("id", conceptId);
            graph.update(meddraTermNode);
        }
        return meddraTermNode;
    }

    private Node getOrAddDrugNode(final Graph graph, final String compoundId) {
        Node drugNode = graph.findNode("Drug", "id", compoundId);
        if (drugNode == null) {
            drugNode = createNode(graph, "Drug");
            drugNode.setProperty("id", compoundId);
            graph.update(drugNode);
        }
        return drugNode;
    }

    private <T> List<T> parseGzipTsvFile(final Workspace workspace, final DataSource dataSource, final String fileName,
                                         final Class<T> typeClass) throws ExporterException {
        try {
            return FileUtils.openGzipTsv(workspace, dataSource, fileName, typeClass).readAll().stream().distinct()
                            .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private void addAllSideEffects(final Workspace workspace, final DataSource dataSource,
                                   final Graph graph) throws ExporterException {
        List<SideEffect> sideEffects = parseGzipTsvFile(workspace, dataSource, "meddra_all_label_se.tsv.gz",
                                                        SideEffect.class);
        for (SideEffect sideEffect : sideEffects) {
            Node meddraTermNode = getOrAddmeddraTermNode(graph, sideEffect.getConceptId());
            if (!sideEffect.umlsConceptId.equals(sideEffect.meddraUmlsConceptId))
                meddraTermNode.setProperty("umls_concept_id", sideEffect.umlsConceptId);
            meddraTermNode.setProperty("meddra_concept_name", sideEffect.sideEffectName);
            graph.update(meddraTermNode);
            Node drugNode = getOrAddDrugNode(graph, sideEffect.stereoCompoundId);
            Edge edge = graph.addEdge(drugNode, meddraTermNode, "HAS_SIDE_EFFECT");
            edge.setProperty("label", sideEffect.label);
            graph.update(edge);
        }
    }


    private void addAllSideEffectFrequencies(final Workspace workspace, final DataSource dataSource,
                                             final Graph graph) throws ExporterException {
        List<Frequency> frequencies = parseGzipTsvFile(workspace, dataSource, "meddra_freq.tsv.gz", Frequency.class);
        for (Frequency frequency : frequencies) {
            Node meddraTermNode = getOrAddmeddraTermNode(graph, frequency.getConceptId());
            if (!frequency.umlsConceptId.equals(frequency.meddraUmlsConceptId))
                meddraTermNode.setProperty("umls_concept_id", frequency.umlsConceptId);
            meddraTermNode.setProperty("meddra_concept_name", frequency.sideEffectName);
            graph.update(meddraTermNode);
            Node drugNode = getOrAddDrugNode(graph, frequency.stereoCompoundId);
            Edge edge = graph.addEdge(drugNode, meddraTermNode, "HAS_SIDE_EFFECT");
            edge.setProperty("frequency", frequency.frequency);
            edge.setProperty("frequency_lower_bound", frequency.frequencyLowerBound);
            edge.setProperty("frequency_upper_bound", frequency.frequencyUpperBound);
            if (frequency.placebo != null)
                edge.setProperty("placebo", frequency.placebo.equalsIgnoreCase("placebo"));
            graph.update(edge);
        }
    }
}
