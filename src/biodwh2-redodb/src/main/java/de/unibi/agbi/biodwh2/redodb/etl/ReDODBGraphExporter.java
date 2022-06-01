package de.unibi.agbi.biodwh2.redodb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.mapping.IdentifierUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.redodb.ReDODBDataSource;
import de.unibi.agbi.biodwh2.redodb.model.Entry;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;

public final class ReDODBGraphExporter extends GraphExporter<ReDODBDataSource> {
    public ReDODBGraphExporter(final ReDODBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Drug", "name", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Drug", "drugbank_id", IndexDescription.Type.NON_UNIQUE));
        graph.addIndex(IndexDescription.forNode("Gene", "symbol", IndexDescription.Type.UNIQUE));
        for (final Entry entry : dataSource.entries)
            exportEntry(graph, entry);
        return true;
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        final NodeBuilder builder = graph.buildNode().withLabel("Drug");
        builder.withProperty("name", entry.drug);
        builder.withPropertyIfNotNull("drugbank_id", getDrugBankId(entry.drugBank));
        builder.withPropertyIfNotNull("pubchem_id", getPubChemId(entry.pubChem));
        builder.withProperty("date_update", entry.dateUpdate);
        builder.withPropertyIfNotNull("in_who_eml", "YES".equalsIgnoreCase(entry.who));
        builder.withPropertyIfNotNull("off_patent", entry.offPatent);
        builder.withPropertyIfNotNull("evidence_in_vitro", "YES".equalsIgnoreCase(entry.vitro));
        builder.withPropertyIfNotNull("evidence_in_vivo", "YES".equalsIgnoreCase(entry.vivo));
        builder.withPropertyIfNotNull("evidence_in_cases", "YES".equalsIgnoreCase(entry.cases));
        builder.withPropertyIfNotNull("evidence_in_observational_studies", "YES".equalsIgnoreCase(entry.obs));
        builder.withPropertyIfNotNull("in_anticancer_trials", "YES".equalsIgnoreCase(entry.trials));
        builder.withPropertyIfNotNull("has_human_data", "YES".equalsIgnoreCase(entry.human));
        builder.withPropertyIfNotNull("pubmed", entry.pubMed);
        if (StringUtils.isNotEmpty(entry.synonym)) {
            final String[] synonyms = StringUtils.splitByWholeSeparator(entry.synonym, ", ");
            builder.withProperty("synonyms", synonyms);
        }
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

    private String getPubChemId(final String link) {
        if (StringUtils.isEmpty(link))
            return null;
        final Matcher matcher = IdentifierUtils.CAS_NUMBER_PATTERN.matcher(link);
        return matcher.find() ? matcher.group() : null;
    }

    private void exportEntryTargets(final Graph graph, final Entry entry, final Node node) {
        if (entry.targets == null)
            return;
        final String[] geneSymbols = StringUtils.split(entry.targets, ';');
        for (final String geneSymbol : geneSymbols)
            if (StringUtils.isNotEmpty(geneSymbol.trim()))
                graph.addEdge(node, findOrCreateGene(graph, geneSymbol.trim()), "TARGETS");
    }

    private Node findOrCreateGene(final Graph graph, final String geneSymbol) {
        Node node = graph.findNode("Gene", "symbol", geneSymbol);
        if (node == null)
            node = graph.addNode("Gene", "symbol", geneSymbol);
        return node;
    }

    private void exportEntryIndications(final Graph graph, final Entry entry, final Node node) {
        if (entry.mainIndications == null)
            return;
        final String[] indications = StringUtils.splitByWholeSeparator(entry.mainIndications, ", ");
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
