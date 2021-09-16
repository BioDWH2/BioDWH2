package de.unibi.agbi.biodwh2.core.text;

import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaEdge;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaNode;

import java.awt.*;
import java.nio.file.Path;

public final class MetaGraphDynamicVisWriter {
    private final MetaGraph graph;

    public MetaGraphDynamicVisWriter(final MetaGraph graph) {
        this.graph = graph;
    }

    public void write(final Path outputFilePath) {
        final StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n");
        builder.append("<html lang=\"en\">\n");
        builder.append("  <head>\n");
        builder.append("    <style type=\"text/css\">\n");
        builder.append("      html, body { margin: 0; padding: 0; color: #d3d3d3; background-color: #222222; }\n");
        builder.append("      #surface { width: 100%; height: 100vh; background-color: #222222; }\n");
        builder.append("    </style>\n");
        builder.append(
                "    <script type=\"text/javascript\" src=\"https://unpkg.com/vis-network/standalone/umd/vis-network.min.js\"></script>\n");
        builder.append("  </head>\n");
        builder.append("  <body>\n");
        builder.append("    <div id=\"surface\"></div>\n");
        builder.append("    <script type=\"text/javascript\">\n");
        builder.append("      var nodes = new vis.DataSet([\n");
        int currentColor = 0;
        for (final MetaNode node : graph.getNodes()) {
            final Color color = Color.getHSBColor(currentColor / (float) graph.getNodeLabelCount(), 0.85f, 1.0f);
            final String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            currentColor++;
            builder.append("        { ");
            builder.append("id: \"").append(node.label).append("\", ");
            builder.append("label: \"").append(node.label).append("\", ");
            builder.append("color: \"").append(hexColor).append("\", ");
            builder.append("},\n");
        }
        builder.append("      ]);\n");
        builder.append("      var edges = new vis.DataSet([\n");
        for (final MetaEdge edge : graph.getEdges()) {
            builder.append("        { ");
            builder.append("from: \"").append(edge.fromLabel).append("\", ");
            builder.append("to: \"").append(edge.toLabel).append("\", ");
            builder.append("label: \"").append(edge.label).append("\", ");
            builder.append("},\n");
        }
        builder.append("      ]);\n");
        builder.append("      var container = document.getElementById(\"surface\");\n");
        builder.append("      var data = { nodes: nodes, edges: edges };\n");
        builder.append("      var options = {\n");
        builder.append("        nodes: {\n");
        builder.append("          shadow: {enabled: true},\n");
        builder.append("          font: {\n");
        builder.append("            color: \"#FFFFFF\",\n");
        builder.append("            strokeColor: \"#000000\",\n");
        builder.append("            strokeWidth: 3,\n");
        builder.append("          }\n");
        builder.append("        },\n");
        builder.append("        edges: {\n");
        builder.append("          color: \"#FFFFFF\",\n");
        builder.append("          arrows: \"to\",\n");
        builder.append("          length: 300,\n");
        builder.append("          shadow: {enabled: true},\n");
        builder.append("          font: {\n");
        builder.append("            color: \"#FFFFFF\",\n");
        builder.append("            strokeColor: \"#000000\",\n");
        builder.append("            strokeWidth: 3,\n");
        builder.append("            align: \"middle\",\n");
        builder.append("          }\n");
        builder.append("        },\n");
        builder.append("      };\n");
        builder.append("      var network = new vis.Network(container, data, options);\n");
        builder.append("    </script>\n");
        builder.append("  </body>\n");
        builder.append("</html>\n");
        FileUtils.writeTextToUTF8File(outputFilePath, builder.toString());
    }
}
