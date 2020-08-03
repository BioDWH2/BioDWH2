package de.unibi.agbi.biodwh2.core.io.graph;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.IndentingXMLStreamWriter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class GraphMLGraphWriter extends GraphWriter {
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

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphMLGraphWriter.class);
    public static final String EXTENSION = "graphml";
    private static final String INVALID_XML_CHARS = new String(
            new char[]{0x01, 0x02, 0x03, 0x04, 0x08, 0x1d, 0x12, 0x14, 0x18});

    private long labelKeyIdCounter;
    private final Map<String, String> labelKeyIdMap;
    private final List<Property> properties;

    public GraphMLGraphWriter() {
        super();
        labelKeyIdMap = new HashMap<>();
        properties = new ArrayList<>();
    }

    public boolean write(final String outputFilePath, final Graph graph) {
        labelKeyIdCounter = 0;
        labelKeyIdMap.clear();
        properties.clear();
        generateProperties(graph);
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(outputFilePath))) {
            writeSubGraphFile(outputStream, graph, null);
        } catch (XMLStreamException | IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to write graphml file", e);
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
            final List<SubGraph> subGraphs = workspace.getConfiguration().splitIntoSubGraphs ? findSubGraphs(graph) :
                                             new ArrayList<>();
            if (subGraphs.size() < 2) {
                final OutputStream outputStream = Files.newOutputStream(
                        Paths.get(dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.GRAPH_ML)));
                writeSubGraphFile(outputStream, graph, subGraphs.size() == 1 ? subGraphs.get(0) : null);
            } else {
                int partIndex = 1;
                for (final SubGraph subGraph : subGraphs) {
                    final OutputStream outputStream = Files.newOutputStream(Paths.get(
                            dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.GRAPH_ML, partIndex)));
                    writeSubGraphFile(outputStream, graph, subGraph);
                    partIndex++;
                }
            }
        } catch (XMLStreamException | IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to write graphml file", e);
            return false;
        }
        return true;
    }

    private void removeOldExports(final Workspace workspace, final DataSource dataSource) {
        removeOldExport(dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.GRAPH_ML));
        int i = 1;
        while (removeOldExport(dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.GRAPH_ML, i)))
            i++;
    }

    private boolean removeOldExport(final String filePath) {
        final File file = new File(filePath);
        return file.exists() && file.delete();
    }

    private void generateProperties(final Graph graph) {
        final Map<String, Class<?>> nodePropertyKeyTypes = collectAllNodePropertyKeyTypes(graph.getNodes());
        for (final String key : nodePropertyKeyTypes.keySet())
            properties.add(generateProperty(key, nodePropertyKeyTypes.get(key), "node"));
        final Map<String, Class<?>> edgePropertyKeyTypes = collectAllEdgePropertyKeyTypes(graph.getEdges());
        for (final String key : edgePropertyKeyTypes.keySet())
            properties.add(generateProperty(key, edgePropertyKeyTypes.get(key), "edge"));
    }

    private Map<String, Class<?>> collectAllNodePropertyKeyTypes(final Iterable<Node> nodes) {
        final Map<String, Class<?>> propertyKeyTypes = new HashMap<>();
        propertyKeyTypes.put("labels", String.class);
        for (final Node node : nodes) {
            final Map<String, Class<?>> nodePropertyKeyTypes = node.getPropertyKeyTypes();
            for (final String key : nodePropertyKeyTypes.keySet()) {
                final String labelKeyId = getNodeLabelKeyId(node, key);
                if (!propertyKeyTypes.containsKey(labelKeyId) || nodePropertyKeyTypes.get(key) != null)
                    propertyKeyTypes.put(labelKeyId, nodePropertyKeyTypes.get(key));
            }
        }
        for (final String key : propertyKeyTypes.keySet())
            propertyKeyTypes.putIfAbsent(key, String.class);
        return propertyKeyTypes;
    }

    private String getNodeLabelKeyId(final Node node, final String key) {
        final String labelKey = "node|" + node.getLabel() + "|" + key;
        if (!labelKeyIdMap.containsKey(labelKey)) {
            labelKeyIdMap.put(labelKey, "nt" + labelKeyIdCounter);
            labelKeyIdCounter++;
        }
        return labelKeyIdMap.get(labelKey);
    }

    private Property generateProperty(final String key, final Class<?> type, final String forType) {
        final Property p = new Property();
        p.id = key;
        p.forType = forType;
        if (labelKeyIdMap.containsValue(key)) {
            final Optional<Map.Entry<String, String>> entry = labelKeyIdMap.entrySet().stream().filter(
                    e -> e.getValue().equals(key)).findFirst();
            if (entry.isPresent()) {
                final String[] parts = StringUtils.split(entry.get().getKey(), "|");
                p.name = parts[parts.length - 1];
            } else
                p.name = key;
        } else
            p.name = key;
        // Allowed types: boolean, int, long, float, double, string
        if (type.isArray()) {
            p.list = getTypeName(type.getComponentType());
            p.type = getTypeName(String.class);
        } else
            p.type = getTypeName(type);
        return p;
    }

    private String getTypeName(Class<?> type) {
        return type.getSimpleName().toLowerCase(Locale.US).replace("integer", "int");
    }

    private Map<String, Class<?>> collectAllEdgePropertyKeyTypes(final Iterable<Edge> edges) {
        final Map<String, Class<?>> propertyKeyTypes = new HashMap<>();
        propertyKeyTypes.put("label", String.class);
        for (final Edge edge : edges) {
            final Map<String, Class<?>> edgePropertyKeyTypes = edge.getPropertyKeyTypes();
            for (final String key : edgePropertyKeyTypes.keySet()) {
                final String labelKeyId = getEdgeLabelKeyId(edge, key);
                if (!propertyKeyTypes.containsKey(labelKeyId) || edgePropertyKeyTypes.get(key) != null)
                    propertyKeyTypes.put(labelKeyId, edgePropertyKeyTypes.get(key));
            }
        }
        for (final String key : propertyKeyTypes.keySet())
            propertyKeyTypes.putIfAbsent(key, String.class);
        return propertyKeyTypes;
    }

    private String getEdgeLabelKeyId(final Edge edge, final String key) {
        final String labelKey = "edge|" + edge.getLabel() + "|" + key;
        if (!labelKeyIdMap.containsKey(labelKey)) {
            labelKeyIdMap.put(labelKey, "et" + labelKeyIdCounter);
            labelKeyIdCounter++;
        }
        return labelKeyIdMap.get(labelKey);
    }

    private List<SubGraph> findSubGraphs(final Graph graph) {
        final Map<Long, SubGraph> nodeIdSubGraphMap = new HashMap<>();
        for (final Edge e : graph.getEdges()) {
            final boolean hasFrom = nodeIdSubGraphMap.containsKey(e.getFromId());
            final boolean hasTo = nodeIdSubGraphMap.containsKey(e.getToId());
            if (hasFrom && hasTo) {
                final SubGraph subGraphFrom = nodeIdSubGraphMap.get(e.getFromId());
                final SubGraph subGraphTo = nodeIdSubGraphMap.get(e.getToId());
                if (!subGraphFrom.equals(subGraphTo)) {
                    subGraphFrom.edgeIds.addAll(subGraphTo.edgeIds);
                    subGraphFrom.nodeIds.addAll(subGraphTo.nodeIds);
                    for (final Long nodeId : subGraphTo.nodeIds)
                        nodeIdSubGraphMap.put(nodeId, subGraphFrom);
                }
                subGraphFrom.edgeIds.add(e.getId());
            } else if (hasFrom) {
                final SubGraph subGraph = nodeIdSubGraphMap.get(e.getFromId());
                subGraph.edgeIds.add(e.getId());
                subGraph.nodeIds.add(e.getToId());
                nodeIdSubGraphMap.put(e.getToId(), subGraph);
            } else if (hasTo) {
                final SubGraph subGraph = nodeIdSubGraphMap.get(e.getToId());
                subGraph.edgeIds.add(e.getId());
                subGraph.nodeIds.add(e.getFromId());
                nodeIdSubGraphMap.put(e.getFromId(), subGraph);
            } else {
                final SubGraph subGraph = new SubGraph();
                subGraph.edgeIds.add(e.getId());
                subGraph.nodeIds.add(e.getFromId());
                subGraph.nodeIds.add(e.getToId());
                nodeIdSubGraphMap.put(e.getFromId(), subGraph);
                nodeIdSubGraphMap.put(e.getToId(), subGraph);
            }
        }
        final int NodeSizeThreshold = 100_000;
        final List<SubGraph> uniqueSubGraphs = new ArrayList<>(new HashSet<>(nodeIdSubGraphMap.values()));
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
            final SubGraph mergeSubGraph = uniqueSubGraphs.get(i);
            if (mergeSubGraph.nodeIds.size() >= NodeSizeThreshold)
                continue;
            for (int j = uniqueSubGraphs.size() - 1; j > i; j--) {
                final SubGraph subGraph = uniqueSubGraphs.get(j);
                if (mergeSubGraph.nodeIds.size() + subGraph.nodeIds.size() < NodeSizeThreshold) {
                    mergeSubGraph.nodeIds.addAll(subGraph.nodeIds);
                    mergeSubGraph.edgeIds.addAll(subGraph.edgeIds);
                    uniqueSubGraphs.remove(j);
                }
            }
        }
        return uniqueSubGraphs;
    }

    private void writeSubGraphFile(final OutputStream outputStream, final Graph graph,
                                   final SubGraph subGraph) throws XMLStreamException {
        final XMLStreamWriter writer = createXMLStreamWriter(outputStream);
        writer.writeStartDocument();
        writeRootStart(writer);
        writeGraph(writer, graph, subGraph);
        writer.writeEndElement();
        writer.writeEndDocument();
    }

    private static XMLStreamWriter createXMLStreamWriter(final OutputStream outputStream) throws XMLStreamException {
        final XMLOutputFactory factory = XMLOutputFactory.newInstance();
        final XMLStreamWriter writer = factory.createXMLStreamWriter(new BufferedOutputStream(outputStream), "UTF-8");
        return new IndentingXMLStreamWriter(writer);
    }

    private void writeRootStart(final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("graphml");
        writer.writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
        writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        writer.writeAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns " +
                                                    "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
    }

    private void writeGraph(final XMLStreamWriter writer, final Graph graph,
                            final SubGraph subGraph) throws XMLStreamException {
        writeProperties(writer);
        writer.writeStartElement("graph");
        writer.writeAttribute("id", "G");
        writer.writeAttribute("edgedefault", "directed");
        for (final Node node : graph.getNodes())
            if (subGraph == null || subGraph.nodeIds.contains(node.getId()))
                writeNode(writer, node);
        for (final Edge edge : graph.getEdges())
            if (subGraph == null || subGraph.edgeIds.contains(edge.getId()))
                writeEdge(writer, edge);
        writer.writeEndElement();
    }

    private void writeProperties(final XMLStreamWriter writer) throws XMLStreamException {
        for (final Property p : properties) {
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

    private void writeNode(final XMLStreamWriter writer, final Node node) throws XMLStreamException {
        final String label = ":" + node.getLabel();
        writer.writeStartElement("node");
        writer.writeAttribute("id", "n" + node.getId());
        writer.writeAttribute("labels", label);
        writePropertyIfNotNull(writer, "labels", label);
        for (final String key : node.getPropertyKeys()) {
            if (!Node.IGNORED_FIELDS.contains(key)) {
                final String labelKeyId = getNodeLabelKeyId(node, key);
                writePropertyIfNotNull(writer, labelKeyId, node.getProperty(key));
            }
        }
        writer.writeEndElement();
    }

    private void writePropertyIfNotNull(final XMLStreamWriter writer, final String key,
                                        final Object value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement("data");
            writer.writeAttribute("key", key);
            writer.writeCharacters(getPropertyStringRepresentation(value));
            writer.writeEndElement();
        }
    }

    private String getPropertyStringRepresentation(final Object property) {
        return property.getClass().isArray() ? getArrayPropertyStringRepresentation((Object[]) property) :
               replaceInvalidXmlCharacters(property.toString(), false);
    }

    private static String replaceInvalidXmlCharacters(final String s, final boolean escapeQuotes) {
        final String cleanString = StringUtils.replaceChars(s, INVALID_XML_CHARS, "");
        return escapeQuotes ? StringUtils.replace(cleanString, "\"", "\\\"") : cleanString;
    }

    private String getArrayPropertyStringRepresentation(final Object... property) {
        final String[] arrayValues = new String[property.length];
        for (int i = 0; i < arrayValues.length; i++)
            arrayValues[i] = replaceInvalidXmlCharacters(property[i].toString(), true);
        final Collector<CharSequence, ?, String> collector = isArrayPropertyStringArray(property) ? Collectors.joining(
                "\",\"", "[\"", "\"]") : Collectors.joining(",", "[", "]");
        return Arrays.stream(arrayValues).collect(collector);
    }

    private static boolean isArrayPropertyStringArray(final Object... property) {
        return property.length > 0 && property[0] instanceof CharSequence;
    }

    private void writeEdge(final XMLStreamWriter writer, final Edge edge) throws XMLStreamException {
        writer.writeStartElement("edge");
        writer.writeAttribute("id", "e" + edge.getId());
        writer.writeAttribute("source", "n" + edge.getFromId());
        writer.writeAttribute("target", "n" + edge.getToId());
        writer.writeAttribute("label", edge.getLabel());
        writePropertyIfNotNull(writer, "label", edge.getLabel());
        for (final String key : edge.getPropertyKeys())
            if (!Edge.IGNORED_FIELDS.contains(key))
                writePropertyIfNotNull(writer, getEdgeLabelKeyId(edge, key), edge.getProperty(key));
        writer.writeEndElement();
    }
}
