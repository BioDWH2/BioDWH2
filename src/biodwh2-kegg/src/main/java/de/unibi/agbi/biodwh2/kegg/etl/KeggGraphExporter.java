package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.kegg.KeggDataSource;
import de.unibi.agbi.biodwh2.kegg.model.*;

import java.util.*;

public class KeggGraphExporter extends GraphExporter<KeggDataSource> {
    private final Map<String, String> idPrefixLabelMap = new HashMap<>();
    private final Map<String, Node> referenceLookup = new HashMap<>();

    public KeggGraphExporter(final KeggDataSource dataSource) {
        super(dataSource);
        idPrefixLabelMap.put("CPD", "Compound");
        idPrefixLabelMap.put("DR", "Drug");
        idPrefixLabelMap.put("ED", "EnvFactor");
        idPrefixLabelMap.put("GN", "Genome");
        idPrefixLabelMap.put("HSA", "Gene");
        idPrefixLabelMap.put("KO", "Orthology");
        idPrefixLabelMap.put("VG", "Virus");
        idPrefixLabelMap.put("TAX", "Taxonomy");
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.setNodeIndexPropertyKeys("id", "pmid", "doi", "name");
        for (Drug drug : dataSource.drugs) {
            Node drugNode = createNodeForKeggEntry(graph, drug);
            drugNode.setProperty("formula", drug.formula);
            drugNode.setProperty("exact_mass", drug.exactMass);
            drugNode.setProperty("molecular_weight", drug.molecularWeight);
            drugNode.setProperty("atoms", drug.atoms);
            drugNode.setProperty("bonds", drug.bonds);
            drugNode.setProperty("efficacy", drug.efficacy);
            if (drug.bracket != null) {
                drugNode.setProperty("bracket_value", drug.bracket.value);
                drugNode.setProperty("bracket_original", drug.bracket.original);
                drugNode.setProperty("bracket_repeat", drug.bracket.repeat);
            }
            graph.update(drugNode);
            for (Sequence sequence : drug.sequences) {
                Node sequenceNode = createNodeFromModel(graph, sequence);
                graph.addEdge(drugNode, sequenceNode, "HAS_SEQUENCE");
            }
            for (NameIdsPair source : drug.sources) {
                Node taxonomyNode = getOrCreateNodeForNameIdsPair(graph, source, "Taxonomy");
                graph.addEdge(drugNode, taxonomyNode, "HAS_SOURCE");
            }
            for (Interaction interaction : drug.interactions) {
                Node targetNode = getOrCreateNodeForNameIdsPair(graph, interaction.target, "Gene");
                Edge edge = graph.addEdge(drugNode, targetNode, "INTERACTS_WITH");
                edge.setProperty("type", interaction.type);
                graph.update(edge);
            }
            /*
            List<Metabolism> metabolisms
            List<NameIdsPair> targets
            List<NameIdsPair> efficacyDiseases
            */
        }
        for (DrugGroup drugGroup : dataSource.drugGroups) {
            Node drugGroupNode = createNodeForKeggEntry(graph, drugGroup);
            drugGroupNode.setProperty("name_stems", drugGroup.nameStems.toArray(new String[0]));
            graph.update(drugGroupNode);
        }
        for (Variant variant : dataSource.variants) {
            Node variantNode = createNodeForKeggEntry(graph, variant);
            variantNode.setProperty("organism", variant.organism);
            graph.update(variantNode);
            /*
            Map<String, NameIdsPair> genes
            List<NetworkLink> networks
            List<NameIdsPair> variations
            */
        }
        for (Network network : dataSource.networks) {
            Node networkNode = createNodeForKeggEntry(graph, network);
            networkNode.setProperty("type", network.type);
            graph.update(networkNode);
            /*
            String definition
            String expandedDefinition
            List<NameIdsPair> genes
            List<NameIdsPair> variants
            List<NameIdsPair> diseases
            List<NameIdsPair> members
            List<NameIdsPair> perturbants
            List<NameIdsPair> classes
            List<NameIdsPair> metabolites
            */
        }
        for (Disease disease : dataSource.diseases) {
            Node diseaseNode = createNodeForKeggEntry(graph, disease);
            diseaseNode.setProperty("description", disease.description);
            graph.update(diseaseNode);
            for (NameIdsPair pathogen : disease.pathogens) {
                Node pathogenNode = getOrCreateNodeForNameIdsPair(graph, pathogen, "Pathogen");
                graph.addEdge(diseaseNode, pathogenNode, "HAS_PATHOGEN");
            }
            for (NameIdsPair carcinogen : disease.carcinogens) {
                Node carcinogenNode = getOrCreateNodeForNameIdsPair(graph, carcinogen, "Carcinogen");
                graph.addEdge(diseaseNode, carcinogenNode, "HAS_CARCINOGEN");
            }
            for (NameIdsPair envFactor : disease.envFactors) {
                Node envFactorNode = getOrCreateNodeForNameIdsPair(graph, envFactor, "EnvFactor");
                graph.addEdge(diseaseNode, envFactorNode, "HAS_ENV_FACTOR");
            }
            for (NetworkLink network : disease.networks) {
                if (network.network != null)
                    graph.addEdge(diseaseNode, graph.findNode("Network", "id", network.network.ids.get(0)),
                                  "ASSOCIATED_WITH");
                for (NameIdsPair element : network.elements)
                    graph.addEdge(diseaseNode, graph.findNode("Network", "id", element.ids.get(0)), "ASSOCIATED_WITH");
            }
            /*
            List<NameIdsPair> pathogenSignatureModules
            List<NameIdsPair> drugs
            List<NameIdsPair> genes
            List<String> categories
            List<String> superGroups
            List<String> subGroups
            */
        }
        for (Network network : dataSource.networks) {
            Node networkNode = graph.findNode("Network", "id", network.id);
            for (NameIdsPair member : network.members)
                graph.addEdge(networkNode, graph.findNode("Network", "id", member.ids.get(0)), "HAS_MEMBER");
        }
        Map<Long, Set<Long>> hierarchyRelations = new HashMap<>();
        for (Drug drug : dataSource.drugs) {
            Node drugNode = graph.findNode("Drug", "id", drug.id);
            for (List<NameIdsPair> mixture : drug.mixtures) {
                Node mixtureNode = createNode(graph, "MixtureComponents");
                graph.addEdge(drugNode, mixtureNode, "HAS_MIXTURE_COMPONENTS");
                for (NameIdsPair component : mixture) {
                    Node compoundNode = getOrCreateNodeForNameIdsPair(graph, component);
                    graph.addEdge(mixtureNode, compoundNode, "HAS_COMPOUND");
                }
            }
            for (NameIdsPair networkTarget : drug.networkTargets)
                graph.addEdge(drugNode, graph.findNode("Network", "id", networkTarget.ids.get(0)), "TARGETS");
            /*
            for (KeggHierarchicalEntry.ParentChildRelation entry : drug.classes) {
                Long parentNodeId = null;
                if (entry.parent.ids.size() > 0)
                    parentNodeId = graph.findNodeId("DGroup", "id", entry.parent.ids.get(0));
                if (parentNodeId == null && entry.parent.name != null)
                    parentNodeId = graph.findNodeId("DGroup", "name", entry.parent.name, true);

                Long childNodeId = null;
                if (entry.child.ids.size() > 0)
                    childNodeId = graph.findNodeId("DGroup", "id", entry.child.ids.get(0));
                if (childNodeId == null && entry.child.name != null)
                    childNodeId = graph.findNodeId("DGroup", "name", entry.child.name, true);
                System.out.println(parentNodeId + " -> " + childNodeId);
            }
            */
        }
        for (DrugGroup drugGroup : dataSource.drugGroups) {
        }
        referenceLookup.clear();
        return true;
    }

    private Node createNodeForKeggEntry(final Graph graph, final KeggEntry entry) {
        Node node = createNode(graph, entry.tags.get(0));
        node.setProperty("id", entry.id);
        node.setProperty("tags", entry.tags.toArray(new String[0]));
        if (entry.names.size() > 0)
            node.setProperty("names", entry.names.toArray(new String[0]));
        if (entry.externalIds.size() > 0)
            node.setProperty("external_identifiers", entry.externalIds.toArray(new String[0]));
        if (entry.remarks.size() > 0)
            node.setProperty("remarks", entry.remarks.toArray(new String[0]));
        if (entry.comments.size() > 0)
            node.setProperty("comments", entry.comments.toArray(new String[0]));
        graph.update(node);
        addAllReferencesForNode(graph, entry, node);
        return node;
    }

    private void addAllReferencesForNode(final Graph graph, final KeggEntry entry, final Node node) {
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
            Edge edge = graph.addEdge(node, referenceNode, "HAS_REFERENCE");
            edge.setProperty("remarks", reference.remarks);
            graph.update(edge);
        }
    }

    private Node getOrCreateNodeForNameIdsPair(final Graph graph, final NameIdsPair pair) {
        return getOrCreateNodeForNameIdsPair(graph, pair, null);
    }

    private Node getOrCreateNodeForNameIdsPair(final Graph graph, final NameIdsPair pair, String nodeLabel) {
        Node node = null;
        String id = null;
        if (pair.ids.size() > 0) {
            String[] idParts = pair.ids.get(0).split(":");
            idParts[0] = idParts[0].toUpperCase(Locale.US);
            id = idParts[1];
            node = graph.findNode(null, "id", id);
            if (idPrefixLabelMap.containsKey(idParts[0]))
                nodeLabel = idPrefixLabelMap.get(idParts[0]);
            else
                System.out.println("Unmapped id prefix: " + pair.ids.get(0));
        }
        if (node == null && pair.name != null)
            node = graph.findNode(null, "name", pair.name);
        if (node == null) {
            node = createNode(graph, nodeLabel != null ? nodeLabel : "UNKNOWN");
            node.setProperty("id", id);
            node.setProperty("name", pair.name);
            if (pair.ids.size() > 1)
                node.setProperty("ids", pair.ids.toArray(new String[0]));
            graph.update(node);
        }
        return node;
    }
}
