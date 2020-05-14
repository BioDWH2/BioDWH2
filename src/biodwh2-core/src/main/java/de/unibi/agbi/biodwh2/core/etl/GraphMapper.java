package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public final class GraphMapper extends Mapper {
    public void map(final Workspace workspace, final List<DataSource> dataSources, final String inputGraphFilePath,
                    final String outputGraphFilePath) {
        final Graph inputGraph = new Graph(inputGraphFilePath.replace("graphml", "sqlite"), true);
        final Graph outputGraph = new Graph(outputGraphFilePath.replace("graphml", "sqlite"));
        final Map<String, MappingDescriber> dataSourceDescriberMap = new HashMap<>();
        for (DataSource dataSource : dataSources)
            dataSourceDescriberMap.put(dataSource.getId(), dataSource.getMappingDescriber());
        for (Node node : inputGraph.getNodes()) {
            String dataSourceId = StringUtils.split(node.getLabels()[0], "_")[0];
            NodeMappingDescription mappingDescription = dataSourceDescriberMap.get(dataSourceId).describe(inputGraph,
                                                                                                          node);
            if (mappingDescription != null)
                mergeMatchingNodes(outputGraph, mappingDescription);
        }
        inputGraph.dispose();
        outputGraph.synchronize(true);
        GraphMLGraphWriter graphMLWriter = new GraphMLGraphWriter();
        graphMLWriter.write(outputGraphFilePath, outputGraph);
        outputGraph.dispose();
    }

    private void mergeMatchingNodes(final Graph graph, final NodeMappingDescription description) {
        Set<Node> matchedNodes = new HashSet<>();
        Set<String> ids = new HashSet<>();
        for (IdentifierType identifierType : description.identifier.keySet())
            for (String id : description.identifier.get(identifierType))
                ids.add(identifierType.prefix + ":" + id);
        for (String id : ids) {
            List<Node> nodes = graph.findNodes(description.type.toString(), "ids", id, true);
            if (nodes != null)
                matchedNodes.addAll(nodes);
        }
        Node mergedNode = null;
        if (matchedNodes.size() == 0) {
            mergedNode = graph.addNode(description.type.toString());
        } else {
            for (Node node : matchedNodes)
                if (mergedNode == null)
                    mergedNode = node;
                else {
                    Collections.addAll(ids, node.<String[]>getProperty("ids"));
                    graph.mergeNodes(mergedNode, node);
                }
        }
        mergedNode.setProperty("ids", ids.toArray(new String[0]));
    }
}
