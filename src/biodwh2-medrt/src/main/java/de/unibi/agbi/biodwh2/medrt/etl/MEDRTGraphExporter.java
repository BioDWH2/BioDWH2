package de.unibi.agbi.biodwh2.medrt.etl;

import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.medrt.MEDRTDataSource;
import de.unibi.agbi.biodwh2.medrt.model.Namespace;
import de.unibi.agbi.biodwh2.medrt.model.Terminology;

public class MEDRTGraphExporter extends GraphExporter<MEDRTDataSource> {
    @Override
    protected Graph exportGraph(MEDRTDataSource dataSource) {
        Graph g = new Graph();
        addTerminology(g, dataSource.terminology);
        return g;
    }

    private void addTerminology(Graph g, Terminology terminology) {
        Node node = createNode(g, "Terminology");
        addTerminologyNamespace(g, node, terminology.namespace);
        addReferencedNamespaces(g, node, terminology);
    }

    private void addTerminologyNamespace(Graph g, Node terminologyNode, Namespace namespace) {
        Node node = addNamespace(g, namespace);
        g.addEdge(new Edge(terminologyNode, node, "MED-RT_IN_NAMESPACE"));
    }

    private Node addNamespace(Graph g, Namespace namespace) {
        Node node = createNode(g, "Namespace");
        node.setProperty("version", namespace.version);
        node.setProperty("authority", namespace.authority);
        node.setProperty("code", namespace.code);
        node.setProperty("name", namespace.name);
        return node;
    }

    private void addReferencedNamespaces(Graph g, Node terminologyNode, Terminology terminology) {
        for (Namespace namespace : terminology.referencedNamespaces) {
            Node node = addNamespace(g, namespace);
            g.addEdge(new Edge(terminologyNode, node, "MED-RT_REFERENCES_NAMESPACE"));
        }
    }
}
