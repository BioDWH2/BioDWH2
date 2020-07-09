package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;
import org.apache.commons.lang3.StringUtils;

public class HGNCGraphExporter extends GraphExporter<HGNCDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final HGNCDataSource dataSource, final Graph graph) {
        for (Gene gene : dataSource.genes) {
            Node node = createNodeFromModel(graph, gene);
            if (gene.aliasSymbol != null && gene.aliasSymbol.length() > 0) {
                String[] aliasSymbols = StringUtils.split(gene.aliasSymbol, "|");
                node.setProperty("alias_symbols", aliasSymbols);
            }
            if (gene.aliasName != null && gene.aliasName.length() > 0) {
                String[] aliasNames = StringUtils.split(gene.aliasName, "|");
                node.setProperty("alias_names", aliasNames);
            }
            graph.update(node);
        }
        return true;
    }
}
