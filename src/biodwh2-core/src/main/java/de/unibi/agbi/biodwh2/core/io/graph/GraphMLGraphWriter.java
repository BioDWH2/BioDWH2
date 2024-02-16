package de.unibi.agbi.biodwh2.core.io.graph;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.IndentingXMLStreamWriter;
import de.unibi.agbi.biodwh2.core.lang.Type;
import de.unibi.agbi.biodwh2.core.model.DataSourceFileType;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

public final class GraphMLGraphWriter implements GraphWriter {
    private static class Property {
        String id;
        String forType;
        String name;
        String list;
        String type;
    }

    private static final Logger LOGGER = LogManager.getLogger(GraphMLGraphWriter.class);
    private long labelKeyIdCounter;
    private final Map<String, String> labelKeyIdMap;
    private final List<Property> properties;

    public GraphMLGraphWriter() {
        super();
        labelKeyIdMap = new HashMap<>();
        properties = new ArrayList<>();
    }

    @Override
    public boolean write(final Workspace workspace, final DataSource dataSource, final Graph graph) {
        removeOldExport(workspace, dataSource);
        return write(dataSource.getFilePath(workspace, DataSourceFileType.INTERMEDIATE_GRAPHML_GZ), graph);
    }

    public void removeOldExport(final Workspace workspace, final DataSource dataSource) {
        FileUtils.safeDelete(dataSource.getFilePath(workspace, DataSourceFileType.INTERMEDIATE_GRAPHML_GZ));
    }

    public boolean write(final Path outputFilePath, final Graph graph) {
        labelKeyIdCounter = 0;
        labelKeyIdMap.clear();
        properties.clear();
        generateProperties(graph);
        try (final var stream = new GzipCompressorOutputStream(Files.newOutputStream(outputFilePath))) {
            writeGraphFile(stream, graph);
        } catch (XMLStreamException | IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to write graphml file", e);
            return false;
        }
        return true;
    }

    private void generateProperties(final Graph graph) {
        final Map<String, Type> nodePropertyKeyTypes = collectAllNodePropertyKeyTypes(graph);
        for (final String key : nodePropertyKeyTypes.keySet())
            properties.add(generateProperty(key, nodePropertyKeyTypes.get(key), "node"));
        final Map<String, Type> edgePropertyKeyTypes = collectAllEdgePropertyKeyTypes(graph);
        for (final String key : edgePropertyKeyTypes.keySet())
            properties.add(generateProperty(key, edgePropertyKeyTypes.get(key), "edge"));
    }

    private Map<String, Type> collectAllNodePropertyKeyTypes(final Graph graph) {
        final Map<String, Type> propertyKeyTypes = new HashMap<>();
        propertyKeyTypes.put("labels", new Type(String.class));
        for (final String label : graph.getNodeLabels()) {
            final Map<String, Type> nodePropertyKeyTypes = graph.getPropertyKeyTypesForNodeLabel(label);
            for (final String key : nodePropertyKeyTypes.keySet())
                if (!Node.IGNORED_FIELDS.contains(key))
                    propertyKeyTypes.put(getNodeLabelKeyId(label, key), nodePropertyKeyTypes.get(key));
        }
        for (final String key : propertyKeyTypes.keySet())
            propertyKeyTypes.putIfAbsent(key, new Type(String.class));
        return propertyKeyTypes;
    }

    private String getNodeLabelKeyId(final String label, final String key) {
        final String labelKey = "node|" + label + "|" + key;
        if (!labelKeyIdMap.containsKey(labelKey)) {
            labelKeyIdMap.put(labelKey, "nt" + labelKeyIdCounter);
            labelKeyIdCounter++;
        }
        return labelKeyIdMap.get(labelKey);
    }

    private Property generateProperty(final String key, final Type type, final String forType) {
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

    private Map<String, Type> collectAllEdgePropertyKeyTypes(final Graph graph) {
        final Map<String, Type> propertyKeyTypes = new HashMap<>();
        propertyKeyTypes.put("label", new Type(String.class));
        for (final String label : graph.getEdgeLabels()) {
            final Map<String, Type> edgePropertyKeyTypes = graph.getPropertyKeyTypesForEdgeLabel(label);
            for (final String key : edgePropertyKeyTypes.keySet())
                if (!Edge.IGNORED_FIELDS.contains(key))
                    propertyKeyTypes.put(getEdgeLabelKeyId(label, key), edgePropertyKeyTypes.get(key));
        }
        for (final String key : propertyKeyTypes.keySet())
            propertyKeyTypes.putIfAbsent(key, new Type(String.class));
        return propertyKeyTypes;
    }

    private String getEdgeLabelKeyId(final String label, final String key) {
        final String labelKey = "edge|" + label + "|" + key;
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
        final String label = ':' + node.getLabel();
        writer.writeStartElement("node");
        writer.writeAttribute("id", "n" + node.getId());
        writer.writeAttribute("labels", label);
        writePropertyIfNotNull(writer, "labels", label);
        for (final String key : node.keySet())
            if (!Node.IGNORED_FIELDS.contains(key))
                writePropertyIfNotNull(writer, getNodeLabelKeyId(node.getLabel(), key), node.getProperty(key));
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
                writePropertyIfNotNull(writer, getEdgeLabelKeyId(edge.getLabel(), key), edge.getProperty(key));
        writer.writeEndElement();
    }
}
