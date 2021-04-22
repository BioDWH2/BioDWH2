package de.unibi.agbi.biodwh2.core.io.graph;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.IndentingXMLStreamWriter;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreModel;
import de.unibi.agbi.biodwh2.core.model.DataSourceFileType;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class GraphMLGraphWriter extends GraphWriter {
    private static class Property {
        String id;
        String forType;
        String name;
        String list;
        String type;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphMLGraphWriter.class);
    private long labelKeyIdCounter;
    private final Map<String, String> labelKeyIdMap;
    private final List<Property> properties;

    public GraphMLGraphWriter() {
        super();
        labelKeyIdMap = new HashMap<>();
        properties = new ArrayList<>();
    }

    public boolean write(final Path outputFilePath, final Graph graph) {
        labelKeyIdCounter = 0;
        labelKeyIdMap.clear();
        properties.clear();
        generateProperties(graph);
        try (OutputStream outputStream = Files.newOutputStream(outputFilePath)) {
            writeGraphFile(outputStream, graph);
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
        removeOldExport(workspace, dataSource);
        generateProperties(graph);
        try (final OutputStream outputStream = Files.newOutputStream(
                dataSource.getFilePath(workspace, DataSourceFileType.INTERMEDIATE_GRAPHML))) {
            writeGraphFile(outputStream, graph);
        } catch (XMLStreamException | IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to write graphml file", e);
            return false;
        }
        return true;
    }

    public void removeOldExport(final Workspace workspace, final DataSource dataSource) {
        final Path path = dataSource.getFilePath(workspace, DataSourceFileType.INTERMEDIATE_GRAPHML);
        if (!FileUtils.safeDelete(path) && LOGGER.isWarnEnabled())
            LOGGER.warn("Failed to remove old GraphML export for data source '" + dataSource.getId() + "'");
    }

    private void generateProperties(final Graph graph) {
        final Map<String, GraphMLPropertyFormatter.Type> nodePropertyKeyTypes = collectAllNodePropertyKeyTypes(
                graph.getNodes());
        for (final String key : nodePropertyKeyTypes.keySet())
            properties.add(generateProperty(key, nodePropertyKeyTypes.get(key), "node"));
        final Map<String, GraphMLPropertyFormatter.Type> edgePropertyKeyTypes = collectAllEdgePropertyKeyTypes(
                graph.getEdges());
        for (final String key : edgePropertyKeyTypes.keySet())
            properties.add(generateProperty(key, edgePropertyKeyTypes.get(key), "edge"));
    }

    private Map<String, GraphMLPropertyFormatter.Type> collectAllNodePropertyKeyTypes(final Iterable<Node> nodes) {
        final Map<String, GraphMLPropertyFormatter.Type> propertyKeyTypes = new HashMap<>();
        propertyKeyTypes.put("labels", new GraphMLPropertyFormatter.Type(String.class));
        for (final Node node : nodes) {
            final Map<String, GraphMLPropertyFormatter.Type> nodePropertyKeyTypes = getPropertyKeyTypes(node);
            for (final String key : nodePropertyKeyTypes.keySet()) {
                if (!Node.IGNORED_FIELDS.contains(key)) {
                    final String labelKeyId = getNodeLabelKeyId(node, key);
                    if (!propertyKeyTypes.containsKey(labelKeyId))
                        propertyKeyTypes.put(labelKeyId, nodePropertyKeyTypes.get(key));
                    else if (nodePropertyKeyTypes.get(key) != null) {
                        final GraphMLPropertyFormatter.Type oldType = propertyKeyTypes.get(labelKeyId);
                        final GraphMLPropertyFormatter.Type newType = nodePropertyKeyTypes.get(key);
                        if (oldType.isList() && newType.getComponentType() != null) {
                            if (oldType.getComponentType() == null || newType.getComponentType().isAssignableFrom(
                                    oldType.getComponentType()))
                                propertyKeyTypes.put(labelKeyId, newType);
                        }
                    }
                }
            }
        }
        for (final String key : propertyKeyTypes.keySet())
            propertyKeyTypes.putIfAbsent(key, new GraphMLPropertyFormatter.Type(String.class));
        return propertyKeyTypes;
    }

    private Map<String, GraphMLPropertyFormatter.Type> getPropertyKeyTypes(final MVStoreModel obj) {
        final Map<String, GraphMLPropertyFormatter.Type> keyTypeMap = new HashMap<>();
        for (final String key : obj.keySet()) {
            final Object value = obj.get(key);
            if (value != null)
                keyTypeMap.put(key, GraphMLPropertyFormatter.Type.fromObject(value));
        }
        return keyTypeMap;
    }

    private String getNodeLabelKeyId(final Node node, final String key) {
        final String sortedLabels = Arrays.stream(node.getLabels()).sorted().collect(Collectors.joining(","));
        final String labelKey = "node|" + sortedLabels + "|" + key;
        if (!labelKeyIdMap.containsKey(labelKey)) {
            labelKeyIdMap.put(labelKey, "nt" + labelKeyIdCounter);
            labelKeyIdCounter++;
        }
        return labelKeyIdMap.get(labelKey);
    }

    private Property generateProperty(final String key, final GraphMLPropertyFormatter.Type type,
                                      final String forType) {
        final GraphMLPropertyFormatter.PropertyType propertyType = GraphMLPropertyFormatter.getPropertyType(type);
        final Property p = new Property();
        p.id = key;
        p.forType = forType;
        p.list = propertyType.listTypeName;
        p.type = propertyType.typeName;
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
        return p;
    }

    private Map<String, GraphMLPropertyFormatter.Type> collectAllEdgePropertyKeyTypes(final Iterable<Edge> edges) {
        final Map<String, GraphMLPropertyFormatter.Type> propertyKeyTypes = new HashMap<>();
        propertyKeyTypes.put("label", new GraphMLPropertyFormatter.Type(String.class));
        for (final Edge edge : edges) {
            final Map<String, GraphMLPropertyFormatter.Type> edgePropertyKeyTypes = getPropertyKeyTypes(edge);
            for (final String key : edgePropertyKeyTypes.keySet()) {
                if (!Edge.IGNORED_FIELDS.contains(key)) {
                    final String labelKeyId = getEdgeLabelKeyId(edge, key);
                    if (!propertyKeyTypes.containsKey(labelKeyId) || edgePropertyKeyTypes.get(key) != null)
                        propertyKeyTypes.put(labelKeyId, edgePropertyKeyTypes.get(key));
                }
            }
        }
        for (final String key : propertyKeyTypes.keySet())
            propertyKeyTypes.putIfAbsent(key, new GraphMLPropertyFormatter.Type(String.class));
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

    private void writeGraphFile(final OutputStream outputStream, final Graph graph) throws XMLStreamException {
        final XMLStreamWriter writer = createXMLStreamWriter(outputStream);
        writer.writeStartDocument();
        writeRootStart(writer);
        writeGraph(writer, graph);
        writer.writeEndElement();
        writer.writeEndDocument();
    }

    private static XMLStreamWriter createXMLStreamWriter(final OutputStream outputStream) throws XMLStreamException {
        final XMLOutputFactory factory = XMLOutputFactory.newInstance();
        final XMLStreamWriter writer = factory.createXMLStreamWriter(new BufferedOutputStream(outputStream),
                                                                     StandardCharsets.UTF_8.name());
        return new IndentingXMLStreamWriter(writer);
    }

    private void writeRootStart(final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("graphml");
        writer.writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
        writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        writer.writeAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns " +
                                                    "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
    }

    private void writeGraph(final XMLStreamWriter writer, final Graph graph) throws XMLStreamException {
        writeProperties(writer);
        writer.writeStartElement("graph");
        writer.writeAttribute("id", "G");
        writer.writeAttribute("edgedefault", "directed");
        for (final Node node : graph.getNodes())
            writeNode(writer, node);
        for (final Edge edge : graph.getEdges())
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
        final String label = Arrays.stream(node.getLabels()).sorted().collect(Collectors.joining(":", ":", ""));
        writer.writeStartElement("node");
        writer.writeAttribute("id", "n" + node.getId());
        writer.writeAttribute("labels", label);
        writePropertyIfNotNull(writer, "labels", label);
        for (final String key : node.keySet())
            if (!Node.IGNORED_FIELDS.contains(key))
                writePropertyIfNotNull(writer, getNodeLabelKeyId(node, key), node.getProperty(key));
        writer.writeEndElement();
    }

    private void writePropertyIfNotNull(final XMLStreamWriter writer, final String key,
                                        final Object value) throws XMLStreamException {
        if (value != null) {
            writer.writeStartElement("data");
            writer.writeAttribute("key", key);
            writer.writeCharacters(GraphMLPropertyFormatter.format(value));
            writer.writeEndElement();
        }
    }

    private void writeEdge(final XMLStreamWriter writer, final Edge edge) throws XMLStreamException {
        writer.writeStartElement("edge");
        writer.writeAttribute("id", "e" + edge.getId());
        writer.writeAttribute("source", "n" + edge.getFromId());
        writer.writeAttribute("target", "n" + edge.getToId());
        writer.writeAttribute("label", edge.getLabel());
        writePropertyIfNotNull(writer, "label", edge.getLabel());
        for (final String key : edge.keySet())
            if (!Edge.IGNORED_FIELDS.contains(key))
                writePropertyIfNotNull(writer, getEdgeLabelKeyId(edge, key), edge.getProperty(key));
        writer.writeEndElement();
    }
}
