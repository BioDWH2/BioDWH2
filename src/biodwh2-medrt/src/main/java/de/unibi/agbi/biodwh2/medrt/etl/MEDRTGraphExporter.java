package de.unibi.agbi.biodwh2.medrt.etl;

import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.medrt.MEDRTDataSource;
import de.unibi.agbi.biodwh2.medrt.model.*;

public class MEDRTGraphExporter extends GraphExporter<MEDRTDataSource> {
    @Override
    protected Graph exportGraph(MEDRTDataSource dataSource) throws ExporterException {
        Graph g = new Graph();
        addTerminology(g, dataSource.terminology);
        return g;
    }

    private void addTerminology(Graph g, Terminology terminology) throws ExporterException {
        Node node = createNode(g, "Terminology");
        addTerminologyNamespace(g, node, terminology.namespace);
        addReferencedNamespaces(g, node, terminology);
        addTerms(g, node, terminology);
        addConcepts(g, node, terminology);
        addAssociations(g, node, terminology);
    }

    private void addTerminologyNamespace(Graph g, Node terminologyNode, Namespace namespace) throws ExporterException {
        Node node = createNodeFromModel(g, namespace);
        g.addEdge(new Edge(terminologyNode, node, "IN_NAMESPACE"));
    }

    private void addReferencedNamespaces(Graph g, Node terminologyNode,
                                         Terminology terminology) throws ExporterException {
        for (Namespace namespace : terminology.referencedNamespaces) {
            Node node = createNodeFromModel(g, namespace);
            g.addEdge(new Edge(terminologyNode, node, "REFERENCES_NAMESPACE"));
        }
    }

    private void addTerms(Graph g, Node terminologyNode, Terminology terminology) throws ExporterException {
        for (Term term : terminology.terms) {
            Node termNode = createNodeFromModel(g, term);
            g.addEdge(new Edge(terminologyNode, termNode, "HAS_TERM"));
            g.addEdge(new Edge(termNode, g.findNode("Namespace", "name", term.namespace), "IN_NAMESPACE"));
        }
    }

    private void addConcepts(Graph g, Node terminologyNode, Terminology terminology) throws ExporterException {
        for (Concept concept : terminology.concepts) {
            Node conceptNode = createNodeFromModel(g, concept);
            for (Property property : concept.properties) {
                Node propertyNode = createNodeFromModel(g, property);
                g.addEdge(new Edge(conceptNode, propertyNode, "HAS_PROPERTY"));
                g.addEdge(new Edge(propertyNode, g.findNode("Namespace", "name", property.namespace), "IN_NAMESPACE"));
            }
            for (Synonym synonym : concept.synonyms) {
                Node termNode = g.findNode("Term", "name", synonym.toName);
                Edge e = new Edge(conceptNode, termNode, "HAS_SYNONYM");
                e.setProperty("name", synonym.name);
                e.setProperty("preferred", synonym.preferred);
                g.addEdge(e);
            }
            g.addEdge(new Edge(terminologyNode, conceptNode, "HAS_CONCEPT"));
            g.addEdge(new Edge(conceptNode, g.findNode("Namespace", "name", concept.namespace), "IN_NAMESPACE"));
        }
    }

    private void addAssociations(Graph g, Node terminologyNode, Terminology terminology) throws ExporterException {
        for (Association association : terminology.associations) {
            Node associationNode = createNodeFromModel(g, association);
            Node qualifierNode = createNodeFromModel(g, association.qualifier);
            g.addEdge(new Edge(associationNode, qualifierNode, "HAS_QUALIFIER"));
            g.addEdge(new Edge(qualifierNode, g.findNode("Namespace", "name", association.qualifier.namespace),
                               "IN_NAMESPACE"));
            g.addEdge(new Edge(terminologyNode, associationNode, "HAS_ASSOCIATION"));
            g.addEdge(
                    new Edge(associationNode, g.findNode("Namespace", "name", association.namespace), "IN_NAMESPACE"));
        }
    }
}
