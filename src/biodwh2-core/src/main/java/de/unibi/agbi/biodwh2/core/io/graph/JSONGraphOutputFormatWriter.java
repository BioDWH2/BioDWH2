package de.unibi.agbi.biodwh2.core.io.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * https://jsongraphformat.info
 */
@SuppressWarnings("unused")
public class JSONGraphOutputFormatWriter extends OutputFormatWriter {
    private static final Logger LOGGER = LogManager.getLogger(JSONGraphOutputFormatWriter.class);

    @Override
    public String getId() {
        return "JGF";
    }

    @Override
    public String getExtension() {
        return "json.gz";
    }

    @Override
    public boolean write(Path outputFilePath, Graph graph) {
        boolean success = true;
        try (final var stream = new GzipCompressorOutputStream(Files.newOutputStream(outputFilePath))) {
            writeGraphFile(stream, graph);
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to write JGF file", e);
            success = false;
        }
        return success;
    }

    private void writeGraphFile(final OutputStream stream, final Graph graph) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final var writer = new PrintWriter(new BufferedOutputStream(stream), true, StandardCharsets.UTF_8);
        writer.println("{");
        writer.println("  \"graph\": {");
        writer.println("    \"nodes\": {");
        boolean first = true;
        for (final var node : graph.getNodes()) {
            if (!first)
                writer.println(",");
            first = false;
            final Map<String, Object> properties = new HashMap<>();
            properties.put("label", node.getLabel());
            for (final var key : node.keySet())
                if (!Node.IGNORED_FIELDS.contains(key))
                    properties.put(key, node.get(key));
            writer.print("      \"" + node.getId() + "\": " + mapper.writeValueAsString(properties));
        }
        if (!first)
            writer.println();
        writer.println("    },");
        writer.println("    \"edges\": {");
        first = true;
        for (final var edge : graph.getEdges()) {
            if (!first)
                writer.println(",");
            first = false;
            final Map<String, Object> properties = new HashMap<>();
            properties.put("label", edge.getLabel());
            properties.put("source", edge.getFromId());
            properties.put("target", edge.getToId());
            for (final var key : edge.keySet())
                if (!Edge.IGNORED_FIELDS.contains(key))
                    properties.put(key, edge.get(key));
            writer.print("      \"" + edge.getId() + "\": " + mapper.writeValueAsString(properties));
        }
        if (!first)
            writer.println();
        writer.println("    }");
        writer.println("  }");
        writer.println("}");
    }
}
