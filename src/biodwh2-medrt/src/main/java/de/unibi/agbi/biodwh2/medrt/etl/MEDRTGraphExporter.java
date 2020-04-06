package de.unibi.agbi.biodwh2.medrt.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.medrt.MEDRTDataSource;
import de.unibi.agbi.biodwh2.medrt.model.*;

import java.util.Locale;

public class MEDRTGraphExporter extends GraphExporter<MEDRTDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final MEDRTDataSource dataSource,
                                  final Graph g) throws ExporterException {
        addTerminology(g, dataSource.terminology);
        return true;
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
        g.addEdge(terminologyNode, node, "IN_NAMESPACE");
    }

    private void addReferencedNamespaces(Graph g, Node terminologyNode,
                                         Terminology terminology) throws ExporterException {
        for (Namespace namespace : terminology.referencedNamespaces) {
            Node node = createNodeFromModel(g, namespace);
            g.addEdge(terminologyNode, node, "REFERENCES_NAMESPACE");
        }
    }

    private void addTerms(Graph g, Node terminologyNode, Terminology terminology) throws ExporterException {
        for (Term term : terminology.terms) {
            Node termNode = createNodeFromModel(g, term);
            g.addEdge(termNode, terminologyNode, "IN_TERMINOLOGY");
            g.addEdge(termNode, g.findNode("Namespace", "name", term.namespace), "IN_NAMESPACE");
        }
    }

    private void addConcepts(Graph g, Node terminologyNode, Terminology terminology) throws ExporterException {
        for (Concept concept : terminology.concepts) {
            Node conceptNode = createNodeFromModel(g, concept);
            addConceptProperties(g, concept, conceptNode);
            addConceptSynonyms(g, concept, conceptNode);
            g.addEdge(conceptNode, terminologyNode, "IN_TERMINOLOGY");
            g.addEdge(conceptNode, g.findNode("Namespace", "name", concept.namespace), "IN_NAMESPACE");
        }
    }

    private void addConceptProperties(final Graph g, final Concept concept,
                                      final Node conceptNode) throws ExporterException {
        for (Property property : concept.properties) {
            Node propertyNode = createNodeFromModel(g, property);
            g.addEdge(conceptNode, propertyNode, "HAS_PROPERTY");
            g.addEdge(propertyNode, g.findNode("Namespace", "name", property.namespace), "IN_NAMESPACE");
        }
    }

    private void addConceptSynonyms(final Graph g, final Concept concept,
                                    final Node conceptNode) throws ExporterException {
        for (Synonym synonym : concept.synonyms) {
            Node termNode = g.findNode("Term", "name", synonym.toName);
            Edge e = g.addEdge(conceptNode, termNode, "HAS_SYNONYM");
            e.setProperty("name", synonym.name);
            e.setProperty("preferred", synonym.preferred);
        }
    }

    private void addAssociations(Graph g, Node terminologyNode, Terminology terminology) throws ExporterException {
        for (Association association : terminology.associations) {
            Node associationNode = createNodeFromModel(g, association);
            associationNode.setProperty(association.qualifier.name.toLowerCase(Locale.US), association.qualifier.value);
            connectAssociationToConcepts(g, association, associationNode);
            g.addEdge(associationNode, terminologyNode, "IN_TERMINOLOGY");
            g.addEdge(associationNode, g.findNode("Namespace", "name", association.namespace), "IN_NAMESPACE");
        }
    }

    private void connectAssociationToConcepts(final Graph g, final Association association,
                                              final Node associationNode) throws ExporterException {
        if (association.fromNamespace.equals("MED-RT")) {
            Node fromNode = g.findNode("Concept", "code", association.fromCode);
            if (fromNode != null)
                g.addEdge(fromNode, associationNode, "HAS_ASSOCIATION");
        }
        if (association.toNamespace.equals("MED-RT")) {
            Node toNode = g.findNode("Concept", "code", association.toCode);
            if (toNode != null)
                g.addEdge(associationNode, toNode, "ASSOCIATED_WITH");
        }
    }
}
