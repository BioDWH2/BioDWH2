package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.kegg.KeggDataSource;
import de.unibi.agbi.biodwh2.kegg.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KeggGraphExporter extends GraphExporter<KeggDataSource> {
    private final Map<String, Node> referenceLookup = new HashMap<>();

    @Override
    protected boolean exportGraph(final Workspace workspace, final KeggDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        graph.setIndexColumnNames("id", "pmid", "doi");
        for (Drug drug : dataSource.drugs) {
            Node drugNode = createNodeForKeggEntry(graph, drug);
            drugNode.setProperty("formula", drug.formula);
            drugNode.setProperty("exact_mass", drug.exactMass);
            drugNode.setProperty("molecular_weight", drug.molecularWeight);
            drugNode.setProperty("atoms", drug.atoms);
            drugNode.setProperty("bonds", drug.bonds);
            drugNode.setProperty("efficacy", drug.efficacy);
        }
        for (DrugGroup drugGroup : dataSource.drugGroups) {
            Node drugGroupNode = createNodeForKeggEntry(graph, drugGroup);
        }
        for (Variant variant : dataSource.variants) {
            Node variantNode = createNodeForKeggEntry(graph, variant);
            variantNode.setProperty("organism", variant.organism);
        }
        for (Disease disease : dataSource.diseases) {
            Node diseaseNode = createNodeForKeggEntry(graph, disease);
            diseaseNode.setProperty("description", disease.description);
        }
        for (Network network : dataSource.networks) {
            Node networkNode = createNodeForKeggEntry(graph, network);
            networkNode.setProperty("type", network.type);
        }
        Map<Long, Set<Long>> hierarchyRelations = new HashMap<>();
        for (Drug drug : dataSource.drugs) {
            for (KeggHierarchicalEntry.ParentChildRelation entry : drug.classes) {
                Long parentNodeId = null;
                if (entry.parent.ids.size() > 0)
                    parentNodeId = graph.findNodeId("DGroup", "id", entry.parent.ids.get(0));
                if (parentNodeId == null && entry.parent.name != null)
                    parentNodeId = graph.findNodeId("DGroup", "name", entry.parent.name);

                Long childNodeId = null;
                if (entry.child.ids.size() > 0)
                    childNodeId = graph.findNodeId("DGroup", "id", entry.child.ids.get(0));
                if (childNodeId == null && entry.child.name != null)
                    childNodeId = graph.findNodeId("DGroup", "name", entry.child.name);
                System.out.println(parentNodeId + " -> " + childNodeId);
            }
        }
        for (DrugGroup drugGroup : dataSource.drugGroups) {
        }
        referenceLookup.clear();
        return true;
    }

    private Node createNodeForKeggEntry(final Graph graph, final KeggEntry entry) throws ExporterException {
        Node node = createNode(graph, entry.tags);
        node.setProperty("id", entry.id);
        if (entry.names.size() > 0)
            node.setProperty("names", entry.names.toArray(new String[0]));
        if (entry.externalIds.size() > 0)
            node.setProperty("external_identifiers", entry.externalIds.toArray(new String[0]));
        if (entry.remarks.size() > 0)
            node.setProperty("remarks", entry.remarks.toArray(new String[0]));
        if (entry.comments.size() > 0)
            node.setProperty("comments", entry.comments.toArray(new String[0]));
        addAllReferencesForNode(graph, entry, node);
        return node;
    }

    private void addAllReferencesForNode(final Graph graph, final KeggEntry entry,
                                         final Node node) throws ExporterException {
        for (Reference reference : entry.references) {
            Node referenceNode;
            boolean doiAvailable = reference.doi != null && reference.doi.length() > 0;
            boolean pmidAvailable = reference.pmid != null && reference.pmid.length() > 0;
            if (doiAvailable && referenceLookup.containsKey(reference.doi)) {
                referenceNode = referenceLookup.get(reference.doi);
            } else if (pmidAvailable && referenceLookup.containsKey(reference.pmid)) {
                referenceNode = referenceLookup.get(reference.pmid);
            } else {
                referenceNode = createNodeFromModel(graph, reference);
                if (pmidAvailable)
                    referenceLookup.put(reference.pmid, referenceNode);
                if (doiAvailable)
                    referenceLookup.put(reference.doi, referenceNode);
            }
            Edge edge = graph.addEdge(node, referenceNode, "has_reference");
            edge.setProperty("remarks", reference.remarks);
        }
    }
}
