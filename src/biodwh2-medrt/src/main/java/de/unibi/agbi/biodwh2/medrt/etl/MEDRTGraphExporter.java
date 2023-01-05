package de.unibi.agbi.biodwh2.medrt.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.medrt.MEDRTDataSource;
import de.unibi.agbi.biodwh2.medrt.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class MEDRTGraphExporter extends GraphExporter<MEDRTDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(MEDRTGraphExporter.class);
    static final String HAS_PREFERRED_TERM_LABEL = "HAS_PREFERRED_TERM";
    static final String HAS_SYNONYM_LABEL = "HAS_SYNONYM";
    static final String TERM_LABEL = "Term";

    private final Map<String, String> conceptLabelMap;

    public MEDRTGraphExporter(final MEDRTDataSource dataSource) {
        super(dataSource);
        conceptLabelMap = new HashMap<>();
        conceptLabelMap.put("MoA", "MechanismOfAction");
        conceptLabelMap.put("PE", "PhysiologicEffect");
        conceptLabelMap.put("EPC", "EstablishedPharmacologicClass");
        conceptLabelMap.put("APC", "AdditionalPharmacologicClass");
        conceptLabelMap.put("PK", "Pharmacokinetics");
        conceptLabelMap.put("TC", "TherapeuticCategory");
        conceptLabelMap.put("EXT", "TerminologyExtensionForClassification");
        conceptLabelMap.put("HC", "HierarchicalConcept");
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.addIndex(IndexDescription.forNode("Drug", "code", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Disease", "code", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Concept", "code", IndexDescription.Type.UNIQUE));
        for (final String label : conceptLabelMap.values())
            graph.addIndex(IndexDescription.forNode(label, "code", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TERM_LABEL, "name", IndexDescription.Type.NON_UNIQUE));
        graph.addIndex(IndexDescription.forNode(TERM_LABEL, "namespace", IndexDescription.Type.NON_UNIQUE));
        addTerminology(graph);
        return true;
    }

    private void addTerminology(final Graph g) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export terminology...");
        final Node node = g.addNode("Terminology");
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export namespaces...");
        addTerminologyNamespace(g, node, dataSource.terminology.namespace);
        addReferencedNamespaces(g, node);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export definitions...");
        addPropertyDefinitions(g);
        addAssociationDefinitions(g);
        addQualitativeDefinitions(g);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export terms...");
        addTerms(g);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export concepts...");
        addConcepts(g);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export associations...");
        addAssociations(g);
    }

    private void addTerminologyNamespace(final Graph g, final Node terminologyNode, final Namespace namespace) {
        final Node node = g.addNodeFromModel(namespace);
        g.addEdge(terminologyNode, node, "IN_NAMESPACE");
    }

    private void addReferencedNamespaces(final Graph g, final Node terminologyNode) {
        for (final Namespace namespace : dataSource.terminology.referencedNamespaces) {
            final Node node = g.addNodeFromModel(namespace);
            g.addEdge(terminologyNode, node, "REFERENCES_NAMESPACE");
        }
    }

    private void addPropertyDefinitions(final Graph g) {
        for (final PropertyType propertyType : dataSource.terminology.propertyTypes) {
            final Node node = g.addNodeFromModel(propertyType);
            g.addEdge(node, g.findNode("Namespace", "name", propertyType.namespace), "IN_NAMESPACE");
        }
    }

    private void addAssociationDefinitions(final Graph g) {
        for (final AssociationType associationType : dataSource.terminology.associationTypes) {
            final Node node = g.addNode("AssociationDefinition", "name", normalizeAssociationName(associationType.name),
                                        "inverse_name", normalizeAssociationName(associationType.inverseName), "type",
                                        associationType.type);
            g.addEdge(node, g.findNode("Namespace", "name", associationType.namespace), "IN_NAMESPACE");
        }
    }

    private String normalizeAssociationName(final String name) {
        return StringUtils.replace(name, " ", "_").toUpperCase(Locale.US);
    }

    private void addQualitativeDefinitions(final Graph g) {
        for (final QualitativeType qualitativeType : dataSource.terminology.qualitativeTypes) {
            final Node node = g.addNodeFromModel(qualitativeType);
            g.addEdge(node, g.findNode("Namespace", "name", qualitativeType.namespace), "IN_NAMESPACE");
        }
    }

    private void addTerms(final Graph g) {
        for (final Term term : dataSource.terminology.terms)
            g.addNodeFromModel(term);
    }

    private void addConcepts(final Graph g) {
        for (final Concept concept : dataSource.terminology.concepts)
            addConcept(g, concept);
    }

    private void addConcept(final Graph g, final Concept concept) {
        final Optional<Property> conceptTypeProperty = concept.properties.stream().filter(p -> "CTY".equals(p.name))
                                                                         .findFirst();
        String label = conceptTypeProperty.isPresent() ? conceptTypeProperty.get().value : "Concept";
        label = conceptLabelMap.getOrDefault(label, label);
        final Map<String, Object> properties = new HashMap<>();
        properties.put("name", StringUtils.replace(concept.name, " [" + label + "]", ""));
        properties.put("code", concept.code);
        properties.put("status", concept.status);
        properties.put("namespace", concept.namespace);
        concept.properties.stream().filter(p -> !"CTY".equals(p.name)).forEach(p -> properties.put(p.name, p.value));
        final Node conceptNode = g.addNode(label, properties);
        for (final Synonym synonym : concept.synonyms) {
            final String edgeLabel = synonym.preferred ? HAS_PREFERRED_TERM_LABEL : HAS_SYNONYM_LABEL;
            final Node termNode = g.findNode(TERM_LABEL, "name", synonym.toName, "namespace", synonym.toNamespace);
            g.addEdge(conceptNode, termNode, edgeLabel);
        }
    }

    private void addAssociations(final Graph g) {
        for (final Association association : dataSource.terminology.associations)
            addAssociation(g, association);
    }

    private void addAssociation(final Graph g, final Association association) {
        final Node source = findAssociationNode(g, association.fromCode, association.fromName,
                                                association.fromNamespace);
        final Node target = findAssociationNode(g, association.toCode, association.toName, association.toNamespace);
        final String edgeLabel = normalizeAssociationName(association.name);
        if (association.qualifier != null && association.qualifier.value != null)
            g.addEdge(source, target, edgeLabel, association.qualifier.name, association.qualifier.value);
        else
            g.addEdge(source, target, edgeLabel);
    }

    private Node findAssociationNode(final Graph g, final String code, String name, final String namespace) {
        Node node = g.findNode("Drug", "code", code);
        if (node == null)
            node = g.findNode("Disease", "code", code);
        if (node == null)
            node = g.findNode("Concept", "code", code);
        if (node == null) {
            if ("RxNorm".equals(namespace)) {
                node = g.addNode("Drug", "code", code, "namespace", namespace, "name", name);
            } else {
                if (isConceptSynonymOfDrug(code))
                    node = g.addNode("Drug", "code", code, "namespace", namespace, "name", name);
                else if (isConceptDisease(code))
                    node = g.addNode("Disease", "code", code, "namespace", namespace, "name", name);
                else
                    node = g.addNode("Concept", "code", code, "namespace", namespace, "name", name);
            }
        }
        return node;
    }

    private boolean isConceptSynonymOfDrug(final String code) {
        return dataSource.terminology.associations.stream().anyMatch(
                a -> "RxNorm".equals(a.fromNamespace) && "synonym of".equalsIgnoreCase(a.name) &&
                     a.toCode.equals(code));
    }

    private boolean isConceptDisease(final String code) {
        return dataSource.terminology.associations.stream().anyMatch(
                a -> "RxNorm".equals(a.fromNamespace) && isAssociationFromDrugToDisease(a) && a.toCode.equals(code));
    }

    private boolean isAssociationFromDrugToDisease(final Association association) {
        if (association.name == null)
            return false;
        return "CI_with".equals(association.name) || "may_treat".equals(association.name) || "may_prevent".equals(
                association.name) || "may_diagnose".equals(association.name) || "induces".equals(association.name);
    }
}
