package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.kegg.KeggDataSource;
import de.unibi.agbi.biodwh2.kegg.model.Disease;
import de.unibi.agbi.biodwh2.kegg.model.Drug;
import de.unibi.agbi.biodwh2.kegg.model.DrugGroup;

import java.util.HashMap;
import java.util.Map;

public class KeggGraphExporter extends GraphExporter {
    @Override
    protected Graph exportGraph(DataSource dataSource) {
        Map<String, Node> idNodeMap = new HashMap<>();
        long nodeId = 1;
        Graph graph = new Graph();
        KeggDataSource keggDataSource = (KeggDataSource) dataSource;
        for (DrugGroup drugGroup : keggDataSource.drugGroups) {
            String[] labels = drugGroup.tags.stream().map(x -> "KEGG_" + x).toArray(String[]::new);
            Node node = new Node(nodeId, labels);
            nodeId += 1;
            node.setProperty("_id", drugGroup.id);
            node.setProperty("names", drugGroup.names.toArray(new String[0]));
            node.setProperty("ids", drugGroup.externalIds.toArray(new String[0]));
            if (drugGroup.nameStems != null && drugGroup.nameStems.size() > 0)
                node.setProperty("name_stems", drugGroup.nameStems.toArray(new String[0]));
            if (drugGroup.comments != null && drugGroup.comments.size() > 0)
                node.setProperty("comments", drugGroup.comments.toArray(new String[0]));
            idNodeMap.put(drugGroup.id, node);
            graph.addNode(node);
        }
        for (Drug drug : keggDataSource.drugs) {
            String[] labels = drug.tags.stream().map(x -> "KEGG_" + x).toArray(String[]::new);
            Node node = new Node(nodeId, labels);
            nodeId += 1;
            node.setProperty("_id", drug.id);
            if (drug.names != null && drug.names.size() > 0)
                node.setProperty("names", drug.names.toArray(new String[0]));
            node.setProperty("ids", drug.externalIds.toArray(new String[0]));
            if (drug.sequences != null && drug.sequences.size() > 0)
                node.setProperty("sequences", drug.sequences.toArray(new String[0]));
            if (drug.formula != null)
                node.setProperty("formula", drug.formula);
            if (drug.exactMass != null)
                node.setProperty("exact_mass", drug.exactMass);
            if (drug.molecularWeight != null)
                node.setProperty("molecular_weight", drug.molecularWeight);
            idNodeMap.put(drug.id, node);
            graph.addNode(node);
        }
        for (Disease disease : keggDataSource.diseases) {
            String[] labels = disease.tags.stream().map(x -> "KEGG_" + x).toArray(String[]::new);
            Node node = new Node(nodeId, labels);
            nodeId += 1;
            node.setProperty("_id", disease.id);
            if (disease.names != null && disease.names.size() > 0)
                node.setProperty("names", disease.names.toArray(new String[0]));
            node.setProperty("ids", disease.externalIds.toArray(new String[0]));
            if (disease.indicatedDrugIds != null)
                for (String drugId : disease.indicatedDrugIds)
                    graph.addEdge(new Edge(idNodeMap.get(drugId), node, "INDICATES"));
            idNodeMap.put(disease.id, node);
            graph.addNode(node);
        }
        for (String key : keggDataSource.drugGroupChildMap.keySet()) {
            for (String child : keggDataSource.drugGroupChildMap.get(key)) {
                // TODO: handle chemical and missing links
                if (idNodeMap.containsKey(child))
                    graph.addEdge(new Edge(idNodeMap.get(key), idNodeMap.get(child), "HAS_MEMBER"));
            }
        }
        return graph;
    }
}
