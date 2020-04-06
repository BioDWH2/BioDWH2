package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.kegg.KeggDataSource;
import de.unibi.agbi.biodwh2.kegg.model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class KeggGraphExporter extends GraphExporter<KeggDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final KeggDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        Map<String, Node> idNodeMap = new HashMap<>();
        for (DrugGroup drugGroup : dataSource.drugGroups) {
            Node node = createNode(graph, drugGroup, idNodeMap);
            addNodePropertyArrayIfNotEmpty(node, "name_stems", drugGroup.nameStems);
            addNodePropertyArrayIfNotEmpty(node, "comments", drugGroup.comments);
        }
        for (Drug drug : dataSource.drugs) {
            Node node = createNode(graph, drug, idNodeMap);
            addNodePropertyArrayIfNotEmpty(node, "sequences", drug.sequences);
            if (drug.formula != null)
                node.setProperty("formula", drug.formula);
            if (drug.exactMass != null)
                node.setProperty("exact_mass", drug.exactMass);
            if (drug.molecularWeight != null)
                node.setProperty("molecular_weight", drug.molecularWeight);
        }
        for (Disease disease : dataSource.diseases) {
            Node node = createNode(graph, disease, idNodeMap);
            if (disease.indicatedDrugIds != null)
                for (String drugId : disease.indicatedDrugIds)
                    graph.addEdge(idNodeMap.get(drugId), node, "INDICATES");
        }
        for (Network network : dataSource.networks)
            createNode(graph, network, idNodeMap);
        for (Variant variant : dataSource.variants)
            createNode(graph, variant, idNodeMap);
        for (String key : dataSource.drugGroupChildMap.keySet()) {
            for (String child : dataSource.drugGroupChildMap.get(key)) {
                // TODO: handle chemical and missing links
                if (idNodeMap.containsKey(child))
                    graph.addEdge(idNodeMap.get(key), idNodeMap.get(child), "HAS_MEMBER");
            }
        }
        return true;
    }

    private Node createNode(Graph graph, KeggEntry entry, Map<String, Node> idNodeMap) throws ExporterException {
        String[] labels = entry.tags.stream().map(x -> "KEGG_" + x).toArray(String[]::new);
        Node node = createNode(graph, labels);
        node.setProperty("_id", entry.id);
        addNodePropertyArrayIfNotEmpty(node, "names", entry.names);
        addNodePropertyArrayIfNotEmpty(node, "ids", entry.externalIds);
        idNodeMap.put(entry.id, node);
        return node;
    }

    private void addNodePropertyArrayIfNotEmpty(Node node, String key,
                                                Collection<String> list) throws ExporterException {
        if (list != null && list.size() > 0)
            node.setProperty(key, list.toArray(new String[0]));
    }
}
