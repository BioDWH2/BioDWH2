package de.unibi.agbi.biodwh2.core.text;

import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.lang.Type;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaEdge;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaNode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class MetaGraphExtendedStatisticsWriter {
    private final MetaGraph graph;

    public MetaGraphExtendedStatisticsWriter(final MetaGraph graph) {
        this.graph = graph;
    }

    public void write(final Path filePath) {
        try (final OutputStream stream = FileUtils.openOutput(filePath);
             final var writer = FileUtils.createBufferedWriterFromStream(stream, StandardCharsets.UTF_8)) {
            write(writer);
        } catch (IOException ignored) {
        }
    }

    private void write(final BufferedWriter writer) throws IOException {
        writer.write("{\n");
        writer.write("\t\"nodeCount\": " + graph.getTotalNodeCount() + ",\n");
        writer.write("\t\"nodeLabelCount\": " + graph.getNodeLabelCount() + ",\n");
        writer.write("\t\"edgeCount\": " + graph.getTotalEdgeCount() + ",\n");
        writer.write("\t\"edgeLabelCount\": " + graph.getEdgeLabelCount() + ",\n");
        writer.write("\t\"nodeLabels\": {\n");
        for (final var nodeIterator = getLabelSortedMetaNodes().iterator(); nodeIterator.hasNext(); ) {
            final MetaNode node = nodeIterator.next();
            writer.write("\t\t\"" + node.label + "\": {\n");
            if (node.dataSourceId != null)
                writer.write("\t\t\t\"dataSourceId\": \"" + node.dataSourceId + "\",\n");
            writer.write("\t\t\t\"isMappingLabel\": " + (node.isMappingLabel ? "true" : "false") + ",\n");
            writer.write("\t\t\t\"count\": " + node.count + ",\n");
            writer.write("\t\t\t\"properties\": [\n");
            for (final var propertyIterator = node.propertyKeyTypes.entrySet().iterator();
                 propertyIterator.hasNext(); ) {
                var entry = propertyIterator.next();
                writer.write("\t\t\t\t{ \"key\": \"" + entry.getKey() + "\", \"type\": \"" +
                             getPropertyTypeName(entry.getValue()) + "\" }");
                if (propertyIterator.hasNext())
                    writer.write(',');
                writer.write('\n');
            }
            writer.write("\t\t\t]\n");
            writer.write("\t\t}");
            if (nodeIterator.hasNext())
                writer.write(',');
            writer.write('\n');
        }
        writer.write("\t},\n");
        writer.write("\t\"edgeLabels\": {\n");
        for (final var edgeIterator = getLabelSortedMetaEdges().iterator(); edgeIterator.hasNext(); ) {
            final MetaEdge edge = edgeIterator.next();
            writer.write("\t\t\"" + edge.label + "\": {\n");
            if (edge.dataSourceId != null)
                writer.write("\t\t\t\"dataSourceId\": \"" + edge.dataSourceId + "\",\n");
            writer.write("\t\t\t\"isMappingLabel\": " + (edge.isMappingLabel ? "true" : "false") + ",\n");
            writer.write("\t\t\t\"count\": " + edge.count + ",\n");
            writer.write("\t\t\t\"properties\": [\n");
            for (final var propertyIterator = edge.propertyKeyTypes.entrySet().iterator();
                 propertyIterator.hasNext(); ) {
                var entry = propertyIterator.next();
                writer.write("\t\t\t\t{ \"key\": \"" + entry.getKey() + "\", \"type\": \"" +
                             getPropertyTypeName(entry.getValue()) + "\" }");
                if (propertyIterator.hasNext())
                    writer.write(',');
                writer.write('\n');
            }
            writer.write("\t\t\t]\n");
            writer.write("\t\t}");
            if (edgeIterator.hasNext())
                writer.write(',');
            writer.write('\n');
        }
        writer.write("\t}\n");
        writer.write("}\n");
    }

    private Collection<MetaNode> getLabelSortedMetaNodes() {
        return graph.getNodes().stream().sorted(Comparator.comparing(a -> a.label)).collect(Collectors.toList());
    }

    private String getPropertyTypeName(final Type type) {
        if (type.isList()) {
            if (type.getType().isArray())
                return getPropertyClassName(type.getComponentType()) + "[]";
            if (Set.class.isAssignableFrom(type.getType()))
                return "set<" + getPropertyClassName(type.getComponentType()) + ">";
            if (List.class.isAssignableFrom(type.getType()))
                return "list<" + getPropertyClassName(type.getComponentType()) + ">";
        }
        return getPropertyClassName(type.getType());
    }

    private String getPropertyClassName(final Class<?> type) {
        if (type == null)
            return "?";
        if (type == Byte.class)
            return "byte";
        if (type == Short.class)
            return "short";
        if (type == Integer.class)
            return "int";
        if (type == Long.class)
            return "long";
        if (type == Float.class)
            return "float";
        if (type == Double.class)
            return "double";
        if (type == String.class)
            return "string";
        if (type == Boolean.class)
            return "bool";
        return type.getSimpleName().toLowerCase(Locale.ROOT);
    }

    private Collection<MetaEdge> getLabelSortedMetaEdges() {
        return graph.getEdges().stream().sorted(
                Comparator.comparing(a -> a.label + "|" + a.fromLabel + "|" + a.toLabel)).collect(Collectors.toList());
    }
}
