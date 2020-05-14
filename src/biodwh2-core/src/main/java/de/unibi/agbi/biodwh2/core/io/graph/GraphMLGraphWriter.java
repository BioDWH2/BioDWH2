package de.unibi.agbi.biodwh2.core.io.graph;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.IndentingXMLStreamWriter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.commons.lang3.StringUtils;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class GraphMLGraphWriter extends GraphWriter {
    private static class Property {
        String id;
        String forType;
        String name;
        String list;
        String type;
    }

    private static class SubGraph {
        Set<Long> edgeIds = new HashSet<>();
        Set<Long> nodeIds = new HashSet<>();
    }

    private static final String InvalidXmlChars = new String(
            new char[]{0x01, 0x02, 0x03, 0x04, 0x08, 0x1d, 0x12, 0x14, 0x18});

    private long labelKeyIdCounter = 0;
    private final Map<String, String> labelKeyIdMap = new HashMap<>();
    private final List<Property> properties = new ArrayList<>();

    public boolean write(final String outputFilePath, final Graph graph) {
        labelKeyIdCounter = 0;
        labelKeyIdMap.clear();
        properties.clear();
        try {
            generateProperties(graph);
            FileOutputStream outputStream = new FileOutputStream(outputFilePath);
            writeSubGraphFile(outputStream, graph, null);
        } catch (XMLStreamException | ExporterException | FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean write(final Workspace workspace, final DataSource dataSource, final Graph graph) {
        labelKeyIdCounter = 0;
        labelKeyIdMap.clear();
        properties.clear();
        removeOldExports(workspace, dataSource);
        try {
            generateProperties(graph);
            List<SubGraph> subGraphs = findSubGraphs(graph);
            if (subGraphs.size() < 2) {
                FileOutputStream outputStream = new FileOutputStream(
                        dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.GraphML));
                writeSubGraphFile(outputStream, graph, subGraphs.size() == 1 ? subGraphs.get(0) : null);
            } else {
                int partIndex = 1;
                for (SubGraph subGraph : subGraphs) {
                    FileOutputStream outputStream = new FileOutputStream(
                            dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.GraphML, partIndex));
                    writeSubGraphFile(outputStream, graph, subGraph);
                    partIndex++;
                }
            }
        } catch (XMLStreamException | ExporterException | FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void removeOldExports(final Workspace workspace, final DataSource dataSource) {
        removeOldExport(dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.GraphML));
        int i = 1;
        while (removeOldExport(dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.GraphML, i)))
            i++;
    }

    private boolean removeOldExport(final String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return false;
        return file.delete();
    }

    private void generateProperties(Graph graph) throws ExporterException {
        Map<String, Class<?>> nodePropertyKeyTypes = collectAllNodePropertyKeyTypes(graph.getNodes());
        for (String key : nodePropertyKeyTypes.keySet())
            properties.add(generateProperty(key, nodePropertyKeyTypes.get(key), "node"));
        Map<String, Class<?>> edgePropertyKeyTypes = collectAllEdgePropertyKeyTypes(graph.getEdges());
        for (String key : edgePropertyKeyTypes.keySet())
            properties.add(generateProperty(key, edgePropertyKeyTypes.get(key), "edge"));
    }

    private Map<String, Class<?>> collectAllNodePropertyKeyTypes(Iterable<Node> nodes) {
        Map<String, Class<?>> propertyKeyTypes = new HashMap<>();
        propertyKeyTypes.put("labels", String.class);
        for (Node node : nodes) {
            Map<String, Class<?>> nodePropertyKeyTypes = node.getPropertyKeyTypes();
            for (String key : nodePropertyKeyTypes.keySet()) {
                String labelKeyId = getNodeLabelKeyId(node, key);
                if (!propertyKeyTypes.containsKey(labelKeyId) || nodePropertyKeyTypes.get(key) != null)
                    propertyKeyTypes.put(labelKeyId, nodePropertyKeyTypes.get(key));
            }
        }
        for (String key : propertyKeyTypes.keySet())
            propertyKeyTypes.putIfAbsent(key, String.class);
        return propertyKeyTypes;
    }

    private String getNodeLabelKeyId(final Node node, final String key) {
        String labelKey = "node|" + String.join("|", node.getLabels()) + "|" + key;
        if (!labelKeyIdMap.containsKey(labelKey)) {
            labelKeyIdMap.put(labelKey, "nt" + labelKeyIdCounter);
            labelKeyIdCounter++;
        }
        return labelKeyIdMap.get(labelKey);
    }

    private Property generateProperty(String key, Class<?> type, String forType) {
        Property p = new Property();
        p.id = key;
        p.forType = forType;
        if (labelKeyIdMap.containsValue(key)) {
            Optional<Map.Entry<String, String>> entry = labelKeyIdMap.entrySet().stream().filter(
                    e -> e.getValue().equals(key)).findFirst();
            if (entry.isPresent()) {
                String[] parts = StringUtils.split(entry.get().getKey(), "|");
                p.name = parts[parts.length - 1];
            } else
                p.name = key;
        } else
            p.name = key;
        if (type.isArray()) {
            p.list = getTypeName(type.getComponentType());
            type = String.class;
        }
        // Allowed types: boolean, int, long, float, double, string
        p.type = getTypeName(type);
        return p;
    }

    private String getTypeName(Class<?> type) {
        return type.getSimpleName().toLowerCase(Locale.US).replace("integer", "int");
    }

    private Map<String, Class<?>> collectAllEdgePropertyKeyTypes(Iterable<Edge> edges) {
        Map<String, Class<?>> propertyKeyTypes = new HashMap<>();
        propertyKeyTypes.put("label", String.class);
        for (Edge edge : edges) {
            Map<String, Class<?>> edgePropertyKeyTypes = edge.getPropertyKeyTypes();
            for (String key : edgePropertyKeyTypes.keySet()) {
                String labelKeyId = getEdgeLabelKeyId(edge, key);
                if (!propertyKeyTypes.containsKey(labelKeyId) || edgePropertyKeyTypes.get(key) != null)
                    propertyKeyTypes.put(labelKeyId, edgePropertyKeyTypes.get(key));
            }
        }
        for (String key : propertyKeyTypes.keySet())
            propertyKeyTypes.putIfAbsent(key, String.class);
        return propertyKeyTypes;
    }

    private String getEdgeLabelKeyId(final Edge edge, final String key) {
        String labelKey = "edge|" + edge.getLabel() + "|" + key;
        if (!labelKeyIdMap.containsKey(labelKey)) {
            labelKeyIdMap.put(labelKey, "et" + labelKeyIdCounter);
            labelKeyIdCounter++;
        }
        return labelKeyIdMap.get(labelKey);
    }

    private List<SubGraph> findSubGraphs(Graph graph) throws ExporterException {
        Map<Long, SubGraph> nodeIdSubGraphMap = new HashMap<>();
        for (Edge e : graph.getEdges()) {
            boolean hasFrom = nodeIdSubGraphMap.containsKey(e.getFromId());
            boolean hasTo = nodeIdSubGraphMap.containsKey(e.getToId());
            if (!hasFrom && !hasTo) {
                SubGraph subGraph = new SubGraph();
                subGraph.edgeIds.add(e.getId());
                subGraph.nodeIds.add(e.getFromId());
                subGraph.nodeIds.add(e.getToId());
                nodeIdSubGraphMap.put(e.getFromId(), subGraph);
                nodeIdSubGraphMap.put(e.getToId(), subGraph);
            } else if (hasFrom && !hasTo) {
                SubGraph subGraph = nodeIdSubGraphMap.get(e.getFromId());
                subGraph.edgeIds.add(e.getId());
                subGraph.nodeIds.add(e.getToId());
                nodeIdSubGraphMap.put(e.getToId(), subGraph);
            } else if (!hasFrom) {
                SubGraph subGraph = nodeIdSubGraphMap.get(e.getToId());
                subGraph.edgeIds.add(e.getId());
                subGraph.nodeIds.add(e.getFromId());
                nodeIdSubGraphMap.put(e.getFromId(), subGraph);
            } else {
                SubGraph subGraphFrom = nodeIdSubGraphMap.get(e.getFromId());
                SubGraph subGraphTo = nodeIdSubGraphMap.get(e.getToId());
                if (subGraphFrom != subGraphTo) {
                    subGraphFrom.edgeIds.addAll(subGraphTo.edgeIds);
                    subGraphFrom.nodeIds.addAll(subGraphTo.nodeIds);
                    for (Long nodeId : subGraphTo.nodeIds)
                        nodeIdSubGraphMap.put(nodeId, subGraphFrom);
                }
                subGraphFrom.edgeIds.add(e.getId());
            }
        }
        final int NodeSizeThreshold = 100000;
        List<SubGraph> uniqueSubGraphs = new ArrayList<>(new HashSet<>(nodeIdSubGraphMap.values()));
        SubGraph orphanSubGraph = null;
        for (long i = 1; i <= graph.getNumberOfNodes(); i++) {
            if (nodeIdSubGraphMap.containsKey(i))
                continue;
            if (orphanSubGraph == null || orphanSubGraph.nodeIds.size() == NodeSizeThreshold) {
                orphanSubGraph = new SubGraph();
                uniqueSubGraphs.add(orphanSubGraph);
            }
            orphanSubGraph.nodeIds.add(i);
        }
        for (int i = 0; i < uniqueSubGraphs.size() - 1; i++) {
            SubGraph mergeSubGraph = uniqueSubGraphs.get(i);
            if (mergeSubGraph.nodeIds.size() >= NodeSizeThreshold)
                continue;
            for (int j = uniqueSubGraphs.size() - 1; j > i; j--) {
                SubGraph subGraph = uniqueSubGraphs.get(j);
                if (mergeSubGraph.nodeIds.size() + subGraph.nodeIds.size() < NodeSizeThreshold) {
                    mergeSubGraph.nodeIds.addAll(subGraph.nodeIds);
                    mergeSubGraph.edgeIds.addAll(subGraph.edgeIds);
                    uniqueSubGraphs.remove(j);
                }
            }
        }
        return uniqueSubGraphs;
    }

    private void writeSubGraphFile(OutputStream outputStream, Graph graph,
                                   SubGraph subGraph) throws XMLStreamException, ExporterException {
        XMLStreamWriter writer = createXMLStreamWriter(outputStream);
        writer.writeStartDocument();
        writeRootStart(writer);
        writeGraph(writer, graph, subGraph);
        writer.writeEndElement();
        writer.writeEndDocument();
    }

    private static XMLStreamWriter createXMLStreamWriter(OutputStream outputStream) throws XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(outputStream, "UTF-8");
        return new IndentingXMLStreamWriter(writer);
    }

    private void writeRootStart(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("graphml");
        writer.writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
        writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        writer.writeAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns " +
                                                    "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
    }

    private void writeGraph(XMLStreamWriter writer, Graph graph,
                            SubGraph subGraph) throws XMLStreamException, ExporterException {
        writeProperties(writer);
        writer.writeStartElement("graph");
        writer.writeAttribute("id", "G");
        writer.writeAttribute("edgedefault", "directed");
        for (Node node : graph.getNodes())
            if (subGraph == null || subGraph.nodeIds.contains(node.getId()))
                writeNode(writer, node);
        for (Edge edge : graph.getEdges())
            if (subGraph == null || subGraph.edgeIds.contains(edge.getId()))
                writeEdge(writer, edge);
        writer.writeEndElement();
    }

    private void writeProperties(XMLStreamWriter writer) throws XMLStreamException {
        for (Property p : properties) {
            writer.writeStartElement("key");
            writer.writeAttribute("id", p.id);
            writer.writeAttribute("for", p.forType);
            writer.writeAttribute("attr.name", p.name);
            if (p.list != null)
                writer.writeAttribute("attr.list", p.list);
            writer.writeAttribute("attr.type", p.type);
            writer.writeEndElement();
        }
    }

    private void writeNode(XMLStreamWriter writer, Node node) throws XMLStreamException {
        String labels = prefixAndJoinNodeLabels(node);
        writer.writeStartElement("node");
        writer.writeAttribute("id", "n" + node.getId());
        writer.writeAttribute("labels", labels);
        writePropertyIfNotNull(writer, "labels", labels);
        for (String key : node.getPropertyKeys()) {
            String labelKeyId = getNodeLabelKeyId(node, key);
            writePropertyIfNotNull(writer, labelKeyId, node.getProperty(key));
        }
        writer.writeEndElement();
    }

    private String prefixAndJoinNodeLabels(Node node) {
        return Arrays.stream(node.getLabels()).map(l -> ":" + l).collect(Collectors.joining());
    }

    private void writePropertyIfNotNull(XMLStreamWriter writer, String key, Object value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement("data");
            writer.writeAttribute("key", key);
            writer.writeCharacters(getPropertyStringRepresentation(value));
            writer.writeEndElement();
        }
    }

    private String getPropertyStringRepresentation(Object property) {
        return property.getClass().isArray() ? getArrayPropertyStringRepresentation((Object[]) property) :
               replaceInvalidXmlCharacters(property.toString(), false);
    }

    private static String replaceInvalidXmlCharacters(String s, boolean escapeQuotes) {
        s = StringUtils.replaceChars(s, InvalidXmlChars, "");
        if (escapeQuotes)
            s = StringUtils.replace(s, "\"", "\\\"");
        return s;
    }

    private String getArrayPropertyStringRepresentation(Object[] property) {
        String[] arrayValues = new String[property.length];
        for (int i = 0; i < arrayValues.length; i++)
            arrayValues[i] = replaceInvalidXmlCharacters(property[i].toString(), true);
        Collector<CharSequence, ?, String> collector = isArrayPropertyStringArray(property) ? Collectors.joining(
                "\",\"", "[\"", "\"]") : Collectors.joining(",", "[", "]");
        return Arrays.stream(arrayValues).collect(collector);
    }

    private static boolean isArrayPropertyStringArray(Object[] property) {
        return property.length > 0 && property[0] instanceof CharSequence;
    }

    private void writeEdge(XMLStreamWriter writer, Edge edge) throws XMLStreamException {
        writer.writeStartElement("edge");
        writer.writeAttribute("id", "e" + edge.getId());
        writer.writeAttribute("source", "n" + edge.getFromId());
        writer.writeAttribute("target", "n" + edge.getToId());
        writer.writeAttribute("label", edge.getLabel());
        writePropertyIfNotNull(writer, "label", edge.getLabel());
        for (String key : edge.getPropertyKeys()) {
            String labelKeyId = getEdgeLabelKeyId(edge, key);
            writePropertyIfNotNull(writer, labelKeyId, edge.getProperty(key));
        }
        writer.writeEndElement();
    }
}
