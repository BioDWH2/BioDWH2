package de.unibi.agbi.biodwh2.core.schema;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public final class GraphQLSchemaWriter extends SchemaWriter {
    public static final String Extension = "graphqls";

    public GraphQLSchemaWriter(GraphSchema schema) {
        super(schema);
    }

    @Override
    public void save(String filePath) throws IOException {
        File outputFile = new File(filePath);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writeLine(writer, "schema {");
        writeLine(writer, "  query: QueryType");
        writeLine(writer, "}");
        writeLine(writer, "type QueryType {");
        for (GraphSchema.NodeType nodeType : schema.getNodeTypes()) {
            String arguments = nodeType.propertyKeyTypes.keySet().stream().map(
                    key -> key + ": " + getGraphQLTypeName(nodeType.propertyKeyTypes.get(key))).collect(
                    Collectors.joining(", "));
            writeLine(writer, "  " + nodeType.label + "(" + arguments + "): [" + nodeType.label + "]!");
        }
        writeLine(writer, "}");
        for (GraphSchema.NodeType nodeType : schema.getNodeTypes()) {
            writeLine(writer, "type " + nodeType.label + " {");
            for (String propertyKey : nodeType.propertyKeyTypes.keySet())
                writeLine(writer,
                          "  " + propertyKey + ": " + getGraphQLTypeName(nodeType.propertyKeyTypes.get(propertyKey)));
            writeLine(writer, "}");
        }
        for (GraphSchema.EdgeType edgeType : schema.getEdgeTypes()) {
            writeLine(writer, "type rel_" + edgeType.label + " @relation(name: \"" + edgeType.label + "\") {");
            for (String propertyKey : edgeType.propertyKeyTypes.keySet())
                writeLine(writer,
                          "  " + propertyKey + ": " + getGraphQLTypeName(edgeType.propertyKeyTypes.get(propertyKey)));
            writeLine(writer, "}");
        }
        writer.close();
    }

    private void writeLine(final BufferedWriter writer, final String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }

    private String getGraphQLTypeName(final Class<?> type) {
        if (type.isArray())
            return "[" + getGraphQLTypeName(type.getComponentType()) + "]";
        if (type == String.class)
            return "String";
        if (type == Integer.class || type == int.class)
            return "Int";
        if (type == Float.class || type == float.class)
            return "Float";
        if (type == Boolean.class || type == boolean.class)
            return "Boolean";
        return "String";
    }
}
