package de.unibi.agbi.biodwh2.core.text;

import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaEdge;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaNode;

import java.awt.*;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        builder.append("      html, body { margin: 0; padding: 0; color: #000000; background-color: #FFFFFF; }\n");
        builder.append("      #surface { width: 100%; height: 100vh; background-color: #FFFFFF; }\n");
        builder.append("    </style>\n");
        builder.append(
                "    <script type=\"text/javascript\" src=\"https://cdnjs.cloudflare.com/ajax/libs/echarts/5.4.0/echarts.min.js\"></script>\n");
        builder.append("  </head>\n");
        builder.append("  <body>\n");
        builder.append("    <div id=\"surface\"></div>\n");
        builder.append("    <script type=\"text/javascript\">\n");
        builder.append("      var graphChart = echarts.init(document.getElementById('surface'));\n");
        builder.append("      var categories = [");
        final Map<String, String> categoryColorMap = new HashMap<>();
        if (graph.isMappedGraph()) {
            categoryColorMap.put("Mapping", "#000000");
            builder.append("{ name: 'Mapping', symbol: 'circle', symbolSize: 20, itemStyle: { color: '#000000' } }, ");
        }
        int currentColor = 0;
        final Collection<String> dataSourceIds = graph.getDataSourceIds();
        for (final String dataSourceId : dataSourceIds) {
            final Color color = Color.getHSBColor(currentColor / (float) dataSourceIds.size(), 0.85f, 1.0f);
            currentColor++;
            final String hexColor = getColorHex(color);
            categoryColorMap.put(dataSourceId, hexColor);
            builder.append("{ name: '").append(dataSourceId).append("', itemStyle: { color: '").append(hexColor).append(
                    "' } }, ");
        }
        builder.append("];\n");
        builder.append("      var options = {\n");
        builder.append("        legend: [{ data: categories.map(function(a) { return a.name; }) }],\n");
        builder.append("        toolbox: { feature: { saveAsImage: { show: true } } },\n");
        builder.append("        series: [{\n");
        builder.append("          type: 'graph',\n");
        builder.append("          layout: 'force',\n");
        builder.append("          roam: true,\n");
        builder.append("          draggable: true,\n");
        builder.append("          symbol: 'rect',\n");
        builder.append("          symbolSize: [20, 10],\n");
        builder.append("          emphasis: { focus: 'adjacency', lineStyle: { width: 6 } },");
        builder.append("          label: { show: true, fontWeight: 'bold' },\n");
        builder.append("          itemStyle: { borderColor: '#000000' },\n");
        builder.append("          edgeLabel: { show: true, fontWeight: 'bold', " +
                       "formatter: function(params) { return params.data.name; } },\n");
        builder.append("          lineStyle: { color: '#000000', width: 2, opacity: 1 },\n");
        builder.append("          categories: categories,\n");
        builder.append("          nodes: [\n");
        currentColor = 0;
        for (final MetaNode node : graph.getNodes()) {
            builder.append("            { ");
            builder.append("id: '").append(node.label).append("', ");
            builder.append("name: '").append(node.label).append("\\n(").append(node.count).append(")', ");
            String category = node.isMappingLabel ? "Mapping" : node.dataSourceId;
            if (category != null) {
                builder.append("category: '").append(category).append("', itemStyle: { color: '").append(
                        categoryColorMap.get(category)).append("' }, ");
            } else {
                final Color color = Color.getHSBColor(currentColor / (float) graph.getNodeLabelCount(), 0.85f, 1.0f);
                currentColor++;
                builder.append("itemStyle: { color: '").append(getColorHex(color)).append("' }, ");
            }
            if ("metadata".equals(node.label))
                builder.append("symbol: 'diamond', itemStyle: { color: '#FFFFFF' }, ");
            builder.append("},\n");
        }
        builder.append("          ],\n");
        builder.append("          edges: [\n");
        for (final MetaEdge edge : graph.getEdges()) {
            builder.append("            { ");
            builder.append("source: '").append(edge.fromLabel).append("', ");
            builder.append("target: '").append(edge.toLabel).append("', ");
            builder.append("name: '").append(edge.label).append("\\n(").append(edge.count).append(")', ");
            String category = edge.isMappingLabel ? "Mapping" : edge.dataSourceId;
            if (category != null) {
                builder.append("category: '").append(category).append("', lineStyle: { color: '").append(
                        categoryColorMap.get(category)).append("' }, ");
            }
            builder.append("},\n");
        }
        builder.append("          ]\n");
        builder.append("        }]\n");
        builder.append("      };\n");
        builder.append("      graphChart.setOption(options);\n");
        builder.append("    </script>\n");
        builder.append("  </body>\n");
        builder.append("</html>\n");
        FileUtils.writeTextToUTF8File(outputFilePath, builder.toString());
    }

    private String getColorHex(final Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
