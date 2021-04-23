package de.unibi.agbi.biodwh2.cancerdrugsdb.etl;

import de.unibi.agbi.biodwh2.cancerdrugsdb.CancerDrugsDBDataSource;
import de.unibi.agbi.biodwh2.cancerdrugsdb.model.Entry;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import org.apache.commons.lang3.StringUtils;

public class CancerDrugsDBGraphExporter extends GraphExporter<CancerDrugsDBDataSource> {
    public CancerDrugsDBGraphExporter(final CancerDrugsDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys("symbol", "name");
        for (final Entry entry : dataSource.entries)
            exportEntry(graph, entry);
        return true;
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Drug");
        builder.withProperty("name", entry.product);
        builder.withPropertyIfNotNull("drugbank_id", getIdFromLink(entry.drugBankId));
        if (entry.atc != null)
            builder.withProperty("atc", StringUtils.splitByWholeSeparator(entry.atc, ", "));
        builder.withPropertyIfNotNull("chembl_id", getIdFromLink(entry.chEMBL));
        builder.withPropertyIfNotNull("first_approval_year", entry.firstApprovalYear);
        builder.withPropertyIfNotNull("in_who_eml", "Y".equalsIgnoreCase(entry.inWHOEML));
        builder.withPropertyIfNotNull("other_approval", entry.otherApproval);
        builder.withPropertyIfNotNull("ema_approval", "Y".equalsIgnoreCase(entry.emaApproval));
        builder.withPropertyIfNotNull("fda_approval", "Y".equalsIgnoreCase(entry.fdaApproval));
        builder.withPropertyIfNotNull("european_national_approval",
                                      "Y".equalsIgnoreCase(entry.europeanNationalApproval));
        final Node node = builder.build();
        exportEntryTargets(graph, entry, node);
        exportEntryIndications(graph, entry, node);
    }

    private String getIdFromLink(final String link) {
        if (link == null)
            return null;
        final String[] parts = StringUtils.split(link, "<>");
        if (parts.length < 3)
            return link;
        return parts[1].trim();
    }

    private void exportEntryTargets(final Graph graph, final Entry entry, final Node node) {
        if (entry.geneTargets == null)
            return;
        final String[] geneSymbols = StringUtils.splitByWholeSeparator(entry.geneTargets, ", ");
        for (final String geneSymbol : geneSymbols)
            graph.addEdge(node, findOrCreateGene(graph, geneSymbol), "TARGETS");
    }

    private Node findOrCreateGene(final Graph graph, final String geneSymbol) {
        Node node = graph.findNode("Gene", "symbol", geneSymbol);
        if (node == null)
            node = graph.addNode("Gene", "symbol", geneSymbol);
        return node;
    }

    private void exportEntryIndications(final Graph graph, final Entry entry, final Node node) {
        if (entry.cancerIndications == null)
            return;
        // TODO: splitting by ", " cuts disease names! This is a format issue
        final String[] indications = StringUtils.splitByWholeSeparator(entry.cancerIndications, ", ");
        for (final String indication : indications)
            graph.addEdge(node, findOrCreateDisease(graph, indication), "INDICATES");
    }

    private Node findOrCreateDisease(final Graph graph, final String indication) {
        Node node = graph.findNode("Disease", "name", indication);
        if (node == null)
            node = graph.addNode("Disease", "name", indication);
        return node;
    }
}
