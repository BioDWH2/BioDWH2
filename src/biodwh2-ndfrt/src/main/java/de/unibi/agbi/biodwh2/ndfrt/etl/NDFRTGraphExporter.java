package de.unibi.agbi.biodwh2.ndfrt.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.ndfrt.NDFRTDataSource;
import de.unibi.agbi.biodwh2.ndfrt.model.Namespace;
import de.unibi.agbi.biodwh2.ndfrt.model.Terminology;

public class NDFRTGraphExporter extends GraphExporter<NDFRTDataSource> {
    public NDFRTGraphExporter(final NDFRTDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph g) {
        addTerminology(g, dataSource.terminology);
        return true;
    }

    private void addTerminology(Graph g, Terminology terminology) {
        Node node = createNode(g, "Terminology");
        for (Namespace namespace : terminology.namespaces)
            addTerminologyNamespace(g, node, namespace);
    }

    private void addTerminologyNamespace(Graph g, Node terminologyNode, Namespace namespace) {
        Node node = createNodeFromModel(g, namespace);
        g.addEdge(terminologyNode, node, "IN_NAMESPACE");
    }
}
