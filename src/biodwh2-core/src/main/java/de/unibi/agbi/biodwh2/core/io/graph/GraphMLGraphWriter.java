package de.unibi.agbi.biodwh2.core.io.graph;

import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.IndentingXMLStreamWriter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.commons.lang3.StringUtils;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class GraphMLGraphWriter extends GraphWriter {
    private long labelKeyIdCounter = 0;
    private final Map<String, String> labelKeyIdMap = new HashMap<>();

    @Override
    public boolean write(OutputStream stream, Graph graph) {
        labelKeyIdCounter = 0;
        labelKeyIdMap.clear();
        try {
            XMLStreamWriter writer = createXMLStreamWriter(stream);
            writer.writeStartDocument();
            writeRootStart(writer);
            writeGraph(writer, graph);
            writer.writeEndElement();
            writer.writeEndDocument();
        } catch (XMLStreamException | ExporterException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

    private void writeGraph(XMLStreamWriter writer, Graph graph) throws XMLStreamException, ExporterException {
        writeNodeProperties(writer, graph);
        writeEdgeProperties(writer, graph);
        writer.writeStartElement("graph");
        writer.writeAttribute("id", "G");
        writer.writeAttribute("edgedefault", "directed");
        for (Node node : graph.getNodes())
            writeNode(writer, node);
        long edgeId = 0;
        for (Edge edge : graph.getEdges()) {
            writeEdge(writer, edge, edgeId);
            edgeId += 1;
        }
        writer.writeEndElement();
    }

    private void writeNodeProperties(XMLStreamWriter writer, Graph graph) throws XMLStreamException, ExporterException {
        Map<String, Class<?>> nodePropertyKeyTypes = collectAllNodePropertyKeyTypes(graph.getNodes());
        for (String key : nodePropertyKeyTypes.keySet())
            writePropertyKey(writer, key, nodePropertyKeyTypes.get(key), "node");
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

    private void writePropertyKey(XMLStreamWriter writer, String key, Class<?> type,
                                  String propertyFor) throws XMLStreamException {
        writer.writeStartElement("key");
        writer.writeAttribute("id", key);
        writer.writeAttribute("for", propertyFor);
        if (labelKeyIdMap.containsValue(key)) {
            Optional<Map.Entry<String, String>> entry = labelKeyIdMap.entrySet().stream().filter(
                    e -> e.getValue().equals(key)).findFirst();
            if (entry.isPresent()) {
                String[] parts = StringUtils.split(entry.get().getKey(), "|");
                writer.writeAttribute("attr.name", parts[parts.length - 1]);
            } else
                writer.writeAttribute("attr.name", key);
        } else
            writer.writeAttribute("attr.name", key);
        if (type.isArray()) {
            String arrayType = getTypeName(type.getComponentType());
            writer.writeAttribute("attr.list", arrayType);
            type = String.class;
        }
        // Allowed types: boolean, int, long, float, double, string
        writer.writeAttribute("attr.type", getTypeName(type));
        writer.writeEndElement();
    }

    private String getTypeName(Class<?> type) {
        return type.getSimpleName().toLowerCase(Locale.US).replace("integer", "int");
    }

    private void writeEdgeProperties(XMLStreamWriter writer, Graph graph) throws XMLStreamException, ExporterException {
        Map<String, Class<?>> edgePropertyKeyTypes = collectAllEdgePropertyKeyTypes(graph.getEdges());
        for (String key : edgePropertyKeyTypes.keySet())
            writePropertyKey(writer, key, edgePropertyKeyTypes.get(key), "edge");
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
               replaceInvalidXmlCharacters(property.toString());
    }

    private static String replaceInvalidXmlCharacters(String s) {
        //s = s.replace("&", "&amp;");
        //s = s.replace("\"", "&quot;");
        //s = s.replace("\'", "&apos;");
        //s = s.replace("<", "&lt;");
        //s = s.replace(">", "&gt;");
        s = s.replaceAll("[\\x01\\x02\\x04\\x08\\x1d\\x18]", "").replace("\"", "\\\"");
        return s;
    }

    private String getArrayPropertyStringRepresentation(Object[] property) {
        String[] arrayValues = new String[property.length];
        for (int i = 0; i < arrayValues.length; i++)
            arrayValues[i] = replaceInvalidXmlCharacters(property[i].toString());
        Collector<CharSequence, ?, String> collector = isArrayPropertyStringArray(property) ? Collectors.joining(
                "\",\"", "[\"", "\"]") : Collectors.joining(",", "[", "]");
        return Arrays.stream(arrayValues).collect(collector);
    }

    private static boolean isArrayPropertyStringArray(Object[] property) {
        return property.length > 0 && property[0] instanceof CharSequence;
    }

    private void writeEdge(XMLStreamWriter writer, Edge edge, long edgeId) throws XMLStreamException {
        writer.writeStartElement("edge");
        writer.writeAttribute("id", "e" + edgeId);
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
