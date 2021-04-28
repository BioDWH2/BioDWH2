package de.unibi.agbi.biodwh2.cancerdrugsdb.etl;

import de.unibi.agbi.biodwh2.cancerdrugsdb.CancerDrugsDBDataSource;
import de.unibi.agbi.biodwh2.cancerdrugsdb.model.Entry;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.mapping.IdentifierUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class CancerDrugsDBGraphExporter extends GraphExporter<CancerDrugsDBDataSource> {
    private static final String LIST_SEPARATOR = "; ";

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
        final String drugBankId = getDrugBankId(entry.drugBankId);
        if (drugBankId != null)
            builder.withPropertyIfNotNull("drugbank_id", drugBankId);
        if (entry.atc != null)
            builder.withProperty("atc", StringUtils.splitByWholeSeparator(entry.atc, LIST_SEPARATOR));
        builder.withPropertyIfNotNull("chembl_ids", getChemblIds(entry.chEMBL));
        if (StringUtils.isNotEmpty(entry.firstApprovalYear))
            builder.withPropertyIfNotNull("first_approval_year", Integer.parseInt(entry.firstApprovalYear));
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

    private String getDrugBankId(final String link) {
        if (StringUtils.isEmpty(link))
            return null;
        final Matcher matcher = IdentifierUtils.DRUGBANK_DRUG_ID_PATTERN.matcher(link);
        return matcher.find() ? matcher.group() : null;
    }

    private String[] getChemblIds(final String link) {
        if (StringUtils.isEmpty(link))
            return null;
        final Matcher matcher = IdentifierUtils.CHEMBL_ID_PATTERN.matcher(link);
        final Set<String> ids = new HashSet<>();
        while (matcher.find())
            ids.add(matcher.group());
        return ids.toArray(new String[0]);
    }

    private void exportEntryTargets(final Graph graph, final Entry entry, final Node node) {
        if (entry.geneTargets == null)
            return;
        final String[] geneSymbols = StringUtils.splitByWholeSeparator(entry.geneTargets, LIST_SEPARATOR);
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
        final String[] indications = StringUtils.splitByWholeSeparator(entry.cancerIndications, LIST_SEPARATOR);
        for (final String indication : indications)
            graph.addEdge(node, findOrCreateDisease(graph, indication), "INDICATES");
    }

    private Node findOrCreateDisease(final Graph graph, final String indication) {
        final String transformedIndication = indication.replace("&#39;", "'");
        Node node = graph.findNode("Disease", "name", transformedIndication);
        if (node == null)
            node = graph.addNode("Disease", "name", transformedIndication);
        return node;
    }
}
