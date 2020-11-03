package de.unibi.agbi.biodwh2.medrt.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.medrt.MEDRTDataSource;
import de.unibi.agbi.biodwh2.medrt.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MEDRTGraphExporter extends GraphExporter<MEDRTDataSource> {
    public MEDRTGraphExporter(final MEDRTDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph g) {
        addTerminology(g);
        return true;
    }

    private void addTerminology(final Graph g) {
        final Node node = createNode(g, "Terminology");
        addTerminologyNamespace(g, node, dataSource.terminology.namespace);
        addReferencedNamespaces(g, node);
        addTerms(g);
        addConcepts(g);
    }

    private void addTerminologyNamespace(final Graph g, final Node terminologyNode, final Namespace namespace) {
        final Node node = createNodeFromModel(g, namespace);
        g.addEdge(terminologyNode, node, "IN_NAMESPACE");
    }

    private void addReferencedNamespaces(final Graph g, final Node terminologyNode) {
        for (final Namespace namespace : dataSource.terminology.referencedNamespaces) {
            final Node node = createNodeFromModel(g, namespace);
            g.addEdge(terminologyNode, node, "REFERENCES_NAMESPACE");
        }
    }

    private void addTerms(final Graph g) {
        for (final Term term : dataSource.terminology.terms) {
            final Node termNode = createNodeFromModel(g, term);
            g.addEdge(termNode, g.findNode("Namespace", "name", term.namespace), "IN_NAMESPACE");
        }
    }

    private void addConcepts(final Graph g) {
        for (final Concept concept : dataSource.terminology.concepts)
            addConcept(g, concept);
    }

    private void addConcept(final Graph g, final Concept concept) {
        final Optional<Property> conceptTypeProperty = concept.properties.stream().filter(p -> "CTY".equals(p.name))
                                                                         .findFirst();
        final String label = conceptTypeProperty.isPresent() ? conceptTypeProperty.get().value : "Concept";
        final Map<String, Object> properties = new HashMap<>();
        properties.put("name", concept.name);
        properties.put("code", concept.code);
        properties.put("status", concept.status);
        properties.put("namespace", concept.namespace);
        concept.properties.stream().filter(p -> !"CTY".equals(p.name)).forEach(p -> properties.put(p.name, p.value));
        g.addNode(label, properties);
        // TODO: synonyms
    }

    /*
    private void addConceptProperties(final Graph g, final Concept concept, final Node conceptNode) {
        for (Property property : concept.properties) {
            Node propertyNode = createNodeFromModel(g, property);
            g.addEdge(conceptNode, propertyNode, "HAS_PROPERTY");
            g.addEdge(propertyNode, g.findNode("Namespace", "name", property.namespace), "IN_NAMESPACE");
        }
    }

    private void addConceptSynonyms(final Graph g, final Concept concept, final Node conceptNode) {
        for (Synonym synonym : concept.synonyms) {
            Node termNode = g.findNode("Term", "name", synonym.toName);
            Edge e = g.addEdge(conceptNode, termNode, "HAS_SYNONYM");
            e.setProperty("name", synonym.name);
            e.setProperty("preferred", synonym.preferred);
            g.update(e);
        }
    }

    private void addAssociations(Graph g, Node terminologyNode, Terminology terminology) {
        for (Association association : terminology.associations) {
            Node associationNode = createNodeFromModel(g, association);
            associationNode.setProperty(association.qualifier.name.toLowerCase(Locale.US), association.qualifier.value);
            g.update(associationNode);
            connectAssociationToConcepts(g, association, associationNode);
            g.addEdge(associationNode, terminologyNode, "IN_TERMINOLOGY");
            g.addEdge(associationNode, g.findNode("Namespace", "name", association.namespace), "IN_NAMESPACE");
        }
    }

    private void connectAssociationToConcepts(final Graph g, final Association association,
                                              final Node associationNode) {
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
    */
}
