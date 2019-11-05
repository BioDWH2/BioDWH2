package de.unibi.agbi.biodwh2.core.io.graph;

import de.unibi.agbi.biodwh2.core.io.IndentingXMLStreamWriter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class GraphMLGraphWriter extends GraphWriter {
    @Override
    public boolean write(OutputStream stream, Graph graph) {
        try {
            XMLStreamWriter writer = createXMLStreamWriter(stream);
            writer.writeStartDocument();
            writeRootStart(writer);
            writeGraph(writer, graph);
            writer.writeEndElement();
            writer.writeEndDocument();
        } catch (XMLStreamException e) {
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

    private void writeGraph(XMLStreamWriter writer, Graph graph) throws XMLStreamException {
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

    private void writeNodeProperties(XMLStreamWriter writer, Graph graph) throws XMLStreamException {
        for (String key : collectAllNodePropertyNames(graph.getNodes()))
            writePropertyKey(writer, key, "node");
    }

    private Set<String> collectAllNodePropertyNames(Collection<Node> nodes) {
        Set<String> propertyNames = new HashSet<>();
        propertyNames.add("labels");
        for (Node node : nodes)
            propertyNames.addAll(node.getPropertyKeys());
        return propertyNames;
    }

    private void writePropertyKey(XMLStreamWriter writer, String key, String propertyFor) throws XMLStreamException {
        writer.writeStartElement("key");
        writer.writeAttribute("id", key);
        writer.writeAttribute("for", propertyFor);
        writer.writeAttribute("attr.name", key);
        writer.writeEndElement();
    }

    private void writeEdgeProperties(XMLStreamWriter writer, Graph graph) throws XMLStreamException {
        for (String key : collectAllEdgePropertyNames(graph.getEdges()))
            writePropertyKey(writer, key, "edge");
    }

    private Set<String> collectAllEdgePropertyNames(Collection<Edge> edges) {
        Set<String> propertyNames = new HashSet<>();
        propertyNames.add("label");
        for (Edge edge : edges)
            propertyNames.addAll(edge.getPropertyKeys());
        return propertyNames;
    }

    private void writeNode(XMLStreamWriter writer, Node node) throws XMLStreamException {
        String labels = prefixAndJoinNodeLabels(node);
        writer.writeStartElement("node");
        writer.writeAttribute("id", "n" + node.getId());
        writer.writeAttribute("labels", labels);
        writePropertyIfNotNull(writer, "labels", labels);
        for (String key : node.getPropertyKeys())
            writePropertyIfNotNull(writer, key, node.getProperty(key));
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
               property.toString();
    }

    private String getArrayPropertyStringRepresentation(Object[] property) {
        Collector<CharSequence, ?, String> collector = isArrayPropertyStringArray(property) ? Collectors.joining(
                "\",\"", "[\"", "\"]") : Collectors.joining(",", "[", "]");
        return Arrays.stream(property).map(Object::toString).collect(collector);
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
            writePropertyIfNotNull(writer, key, edge.getProperty(key));
        }
        writer.writeEndElement();
    }
}
