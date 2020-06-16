package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public final class GraphMapper extends Mapper {
    public void map(final Workspace workspace, final List<DataSource> dataSources, final String inputGraphFilePath,
                    final String outputGraphFilePath) {
        final Graph graph = new Graph(inputGraphFilePath.replace("graphml", "sqlite"), true);
        final Map<String, MappingDescriber> dataSourceDescriberMap = new HashMap<>();
        for (DataSource dataSource : dataSources)
            dataSourceDescriberMap.put(dataSource.getId(), dataSource.getMappingDescriber());
        final Map<String, Set<Long>> idNodeIdMap = new HashMap<>();
        for (Node node : graph.getNodes()) {
            String dataSourceId = StringUtils.split(node.getLabels()[0], "_")[0];
            MappingDescriber dataSourceDescriber = dataSourceDescriberMap.get(dataSourceId);
            NodeMappingDescription mappingDescription = dataSourceDescriber.describe(graph, node);
            if (mappingDescription != null)
                mergeMatchingNodes(graph, mappingDescription, idNodeIdMap, node.getId());
        }
        graph.synchronize(true);
        GraphMLGraphWriter graphMLWriter = new GraphMLGraphWriter();
        graphMLWriter.write(outputGraphFilePath, graph);
        graph.dispose();
    }

    private void mergeMatchingNodes(final Graph graph, final NodeMappingDescription description,
                                    final Map<String, Set<Long>> idNodeIdMap, final long mappedNodeId) {
        Set<String> ids = new HashSet<>(description.getIdentifiers());
        Set<Long> matchedNodeIds = new HashSet<>();
        for (String id : ids)
            if (idNodeIdMap.containsKey(id))
                matchedNodeIds.addAll(idNodeIdMap.get(id));
        Node mergedNode = null;
        if (matchedNodeIds.size() == 0)
            mergedNode = graph.addNode(description.type.toString());
        else {
            for (Long nodeId : matchedNodeIds) {
                Node matchedNode = graph.getNode(nodeId);
                String[] nodeIds = matchedNode.getProperty("ids");
                Collections.addAll(ids, nodeIds);
                if (mergedNode == null)
                    mergedNode = matchedNode;
                else {
                    graph.mergeNodes(mergedNode, matchedNode);
                    for (String id : nodeIds)
                        idNodeIdMap.get(id).remove(nodeId);
                }
            }
        }
        graph.addEdge(mappedNodeId, mergedNode.getId(), "MAPPED_TO");
        mergedNode.setProperty("ids", ids.toArray(new String[0]));
        for (String id : ids) {
            if (!idNodeIdMap.containsKey(id))
                idNodeIdMap.put(id, new HashSet<>());
            idNodeIdMap.get(id).add(mergedNode.getId());
        }
    }
}
