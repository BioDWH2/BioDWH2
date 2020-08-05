package de.unibi.agbi.biodwh2.core.io.graph;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.IndentingXMLStreamWriter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.nio.charset.StandardCharsets;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphMLGraphWriter.class);
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
                Paths.get(dataSource.getIntermediateGraphFilePath(workspace)))) {
            writeGraphFile(outputStream, graph);
        } catch (XMLStreamException | IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to write graphml file", e);
            return false;
        }
        return true;
    }

    private void removeOldExport(final Workspace workspace, final DataSource dataSource) {
        final File file = new File(dataSource.getIntermediateGraphFilePath(workspace));
        if (file.exists())
            //noinspection ResultOfMethodCallIgnored
            file.delete();
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
