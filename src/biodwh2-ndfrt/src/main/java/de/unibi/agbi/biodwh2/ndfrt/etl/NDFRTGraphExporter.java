package de.unibi.agbi.biodwh2.ndfrt.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.ndfrt.NDFRTDataSource;
import de.unibi.agbi.biodwh2.ndfrt.model.Namespace;
import de.unibi.agbi.biodwh2.ndfrt.model.Terminology;

public class NDFRTGraphExporter extends GraphExporter<NDFRTDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final NDFRTDataSource dataSource,
                                  final Graph g) throws ExporterException {
        addTerminology(g, dataSource.terminology);
        return true;
    }

    private void addTerminology(Graph g, Terminology terminology) throws ExporterException {
        Node node = createNode(g, "Terminology");
        for (Namespace namespace : terminology.namespaces)
            addTerminologyNamespace(g, node, namespace);
    }

    private void addTerminologyNamespace(Graph g, Node terminologyNode, Namespace namespace) throws ExporterException {
        Node node = createNodeFromModel(g, namespace);
        g.addEdge(terminologyNode, node, "IN_NAMESPACE");
    }
}
