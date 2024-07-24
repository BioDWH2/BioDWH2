package de.unibi.agbi.biodwh2.core.io.graph;

import de.unibi.agbi.biodwh2.core.io.IndentingXMLStreamWriter;
import de.unibi.agbi.biodwh2.core.lang.Type;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * https://gexf.net
 */
@SuppressWarnings("unused")
public class GEXFOutputFormatWriter extends OutputFormatWriter {
    private static class Property {
        String id;
        String title;
        String type;
    }

    private static final Logger LOGGER = LogManager.getLogger(GEXFOutputFormatWriter.class);
    private long labelKeyIdCounter;
    private final Map<String, String> labelKeyIdMap = new HashMap<>();
    private final List<Property> nodeProperties = new ArrayList<>();
    private final List<Property> edgeProperties = new ArrayList<>();

    @Override
    public String getId() {
        return "GEXF";
    }

    @Override
    public String getExtension() {
        return "gexf.gz";
    }

    @Override
    public boolean write(final Path outputFilePath, final Graph graph) {
        reset();
        generateProperties(graph);
        boolean success = true;
        try (final var stream = new GzipCompressorOutputStream(Files.newOutputStream(outputFilePath))) {
            writeGraphFile(stream, graph);
        } catch (XMLStreamException | IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to write GEXF file", e);
            success = false;
        }
        reset();
        return success;
    }

    private void reset() {
        labelKeyIdCounter = 0;
        labelKeyIdMap.clear();
        nodeProperties.clear();
        edgeProperties.clear();
    }

    private void generateProperties(final Graph graph) {
        final Map<String, Type> nodePropertyKeyTypes = collectAllNodePropertyKeyTypes(graph);
        for (final String key : nodePropertyKeyTypes.keySet())
            nodeProperties.add(generateProperty(key, nodePropertyKeyTypes.get(key)));
        final Map<String, Type> edgePropertyKeyTypes = collectAllEdgePropertyKeyTypes(graph);
        for (final String key : edgePropertyKeyTypes.keySet())
            edgeProperties.add(generateProperty(key, edgePropertyKeyTypes.get(key)));
    }

    private Map<String, Type> collectAllNodePropertyKeyTypes(final Graph graph) {
        final Map<String, Type> propertyKeyTypes = new HashMap<>();
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

    private Property generateProperty(final String key, final Type type) {
        final var propertyType = GEXFPropertyFormatter.getPropertyType(type);
        final var p = new Property();
        p.id = key;
        p.type = propertyType;
        if (labelKeyIdMap.containsValue(key)) {
            final var entry = labelKeyIdMap.entrySet().stream().filter(e -> e.getValue().equals(key)).findFirst();
            if (entry.isPresent()) {
                final String[] parts = StringUtils.split(entry.get().getKey(), "|");
                p.title = parts[parts.length - 1];
            } else
                p.title = key;
        } else
            p.title = key;
        return p;
    }

    private Map<String, Type> collectAllEdgePropertyKeyTypes(final Graph graph) {
        final Map<String, Type> propertyKeyTypes = new HashMap<>();
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
        writer.writeStartElement("gexf");
        writer.writeAttribute("xmlns", "http://gexf.net/1.3");
        writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema−instance");
        writer.writeAttribute("xsi:schemaLocation", "http://gexf.net/1.3 http://gexf.net/1.3/gexf.xsd");
        writer.writeAttribute("version", "1.3");
    }

    private void writeGraph(final XMLStreamWriter writer, final Graph graph) throws XMLStreamException {
        writeMeta(writer);
        writer.writeStartElement("graph");
        writer.writeAttribute("defaultedgetype", "directed");
        writeAttributes(writer);
        writer.writeStartElement("nodes");
        for (final Node node : graph.getNodes())
            writeNode(writer, node);
        writer.writeEndElement();
        writer.writeStartElement("edges");
        for (final Edge edge : graph.getEdges())
            writeEdge(writer, edge);
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private void writeMeta(final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("meta");
        writer.writeAttribute("lastmodifieddate", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        writer.writeStartElement("creator");
        writer.writeCharacters("BioDWH2");
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private void writeAttributes(final XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("attributes");
        writer.writeAttribute("class", "node");
        for (final Property p : nodeProperties) {
            writer.writeStartElement("attribute");
            writer.writeAttribute("id", p.id);
            writer.writeAttribute("title", p.title);
            writer.writeAttribute("type", p.type);
            writer.writeEndElement();
        }
        writer.writeEndElement();
        writer.writeStartElement("attributes");
        writer.writeAttribute("class", "edge");
        for (final Property p : edgeProperties) {
            writer.writeStartElement("attribute");
            writer.writeAttribute("id", p.id);
            writer.writeAttribute("title", p.title);
            writer.writeAttribute("type", p.type);
            writer.writeEndElement();
        }
        writer.writeEndElement();
    }

    private void writeNode(final XMLStreamWriter writer, final Node node) throws XMLStreamException {
        writer.writeStartElement("node");
        writer.writeAttribute("id", node.getId().toString());
        writer.writeAttribute("label", node.getLabel());
        writer.writeStartElement("attvalues");
        for (final String key : node.keySet())
            if (!Node.IGNORED_FIELDS.contains(key))
                writePropertyIfNotNull(writer, getNodeLabelKeyId(node.getLabel(), key), node.getProperty(key));
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private void writePropertyIfNotNull(final XMLStreamWriter writer, final String key,
                                        final Object value) throws XMLStreamException {
        if (value == null)
            return;
        writer.writeStartElement("attvalue");
        writer.writeAttribute("for", key);
        writer.writeAttribute("value", GEXFPropertyFormatter.format(value));
        writer.writeEndElement();
    }

    private void writeEdge(final XMLStreamWriter writer, final Edge edge) throws XMLStreamException {
        writer.writeStartElement("edge");
        writer.writeAttribute("id", edge.getId().toString());
        writer.writeAttribute("source", edge.getFromId().toString());
        writer.writeAttribute("target", edge.getToId().toString());
        writer.writeAttribute("label", edge.getLabel());
        writer.writeStartElement("attvalues");
        for (final String key : edge.keySet())
            if (!Edge.IGNORED_FIELDS.contains(key))
                writePropertyIfNotNull(writer, getEdgeLabelKeyId(edge.getLabel(), key), edge.getProperty(key));
        writer.writeEndElement();
        writer.writeEndElement();
    }
}
