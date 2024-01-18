package de.unibi.agbi.biodwh2.mir2disease.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.mir2disease.Mir2diseaseDataSource;
import de.unibi.agbi.biodwh2.mir2disease.model.AllEntries;
import de.unibi.agbi.biodwh2.mir2disease.model.Disease;
import de.unibi.agbi.biodwh2.mir2disease.model.MiRNATarget;
import org.apache.commons.lang3.StringUtils;

public class Mir2diseaseGraphExporter extends GraphExporter<Mir2diseaseDataSource> {
    static final String MI_RNA_LABEL = "MiRNA";
    static final String DISEASE_LABEL = "Disease";
    static final String PUBLICATION_LABEL = "Publication";
    static final String TARGET_LABEL = "Target of miRNA";
    static final String ASSOCIATION_NODE_LABEL = "Association";
    private static final String ASSOCIATION_LABEL = "ASSOCIATED_WITH";

    public Mir2diseaseGraphExporter(Mir2diseaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(Workspace workspace, Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(MI_RNA_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TARGET_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportDiseaseList(graph);
        exportMiRNAList(graph);
        exportPublicationList(graph);
        exportTargetList(graph);
        exportEffectsOfMiRNA(graph);
        return true;
    }

    private void exportEffectsOfMiRNA(Graph graph) {
        for (final AllEntries entry : dataSource.allEntries) {
            final Node miRNA = findOrCreateNodeMiRNA(entry, graph);
            final Node disease = findOrCreateNodeDiseaseByName(graph, entry);
            final Node publication = findOrCreateNodePublication(graph, entry.reference, entry.pubDate);
            final Node association = graph.addNode(ASSOCIATION_NODE_LABEL);
            graph.addEdge(miRNA, association, ASSOCIATION_LABEL);
            graph.addEdge(disease, association, ASSOCIATION_LABEL);
            graph.addEdge(publication, association, ASSOCIATION_LABEL);
            for (final MiRNATarget target : dataSource.miRNATarget) {
                if (entry.miRNA.equals(target.miRNA) && target.reference != null && entry.reference.contains(
                        target.reference) && entry.pubDate.equals(target.pubDate)) {
                    final Node targetGene = findOrCreateNodeTarget(graph, target.validatedTarget);
                    if (StringUtils.isNotEmpty(entry.effect))
                        graph.addEdge(targetGene, association, ASSOCIATION_LABEL, "effect", entry.effect);
                    else
                        graph.addEdge(targetGene, association, ASSOCIATION_LABEL);
                }
            }
        }
    }

    private Node findOrCreateNodeDiseaseByName(Graph graph, AllEntries entry) {
        Node node = graph.findNode(DISEASE_LABEL, "name", entry.diseaseNameInOriginalPaper);
        if (node == null) {
            node = graph.addNode(DISEASE_LABEL, "name", entry.diseaseNameInOriginalPaper);
        }
        return node;
    }

    private Node findOrCreateNodeMiRNA(AllEntries entry, Graph graph) {
        Node node = graph.findNode(MI_RNA_LABEL, ID_KEY, entry.miRNA);
        if (node == null) {
            node = graph.addNode(MI_RNA_LABEL, ID_KEY, entry.miRNA, "sequencing method", entry.sequencingMethod);
        }
        return node;
    }

    private void exportTargetList(Graph graph) {
        for (final MiRNATarget target : dataSource.miRNATarget)
            findOrCreateNodeTarget(graph, target.validatedTarget);
    }

    private Node findOrCreateNodeTarget(Graph graph, String validatedTarget) {
        Node node = graph.findNode(TARGET_LABEL, "validated target", validatedTarget);
        if (node == null) {
            node = graph.addNode(TARGET_LABEL, "validated target", validatedTarget);
        }
        return node;
    }

    private void exportPublicationList(Graph graph) {
        for (final AllEntries entry : dataSource.allEntries)
            findOrCreateNodePublication(graph, entry.reference, entry.pubDate);
    }

    private Node findOrCreateNodePublication(Graph graph, String reference, String pubDate) {
        Node node = graph.findNode(PUBLICATION_LABEL, "reference", reference, "publication date", pubDate);
        if (node == null) {
            node = graph.addNode(PUBLICATION_LABEL, "reference", reference, "publication date", pubDate);
        }
        return node;
    }

    private void exportMiRNAList(Graph graph) {
        for (final AllEntries entry : dataSource.allEntries) {
            Node node = graph.findNode(MI_RNA_LABEL, ID_KEY, entry.miRNA);
            if (node == null)
                graph.addNode(MI_RNA_LABEL, ID_KEY, entry.miRNA, "sequencing method", entry.sequencingMethod);
        }
    }

    private void exportDiseaseList(Graph graph) {
        for (final Disease disease : dataSource.disease) {
            Node node = graph.findNode(DISEASE_LABEL, ID_KEY, disease.diseaseOntologyID);
            if (node == null) {
                graph.addNode(DISEASE_LABEL, ID_KEY, disease.diseaseOntologyID, "name",
                              disease.diseaseNameInOriginalPaper);
            }
        }
    }
}
