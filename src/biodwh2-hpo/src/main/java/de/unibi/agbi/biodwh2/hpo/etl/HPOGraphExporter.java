package de.unibi.agbi.biodwh2.hpo.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.OntologyGraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.hpo.HPODataSource;
import de.unibi.agbi.biodwh2.hpo.model.EvidenceCode;
import de.unibi.agbi.biodwh2.hpo.model.PhenotypeAnnotation;
import de.unibi.agbi.biodwh2.hpo.model.PhenotypeToGenesEntry;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class HPOGraphExporter extends OntologyGraphExporter<HPODataSource> {
    private boolean omimLicensed = false;

    public HPOGraphExporter(final HPODataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected String getOntologyFileName() {
        return "hp.obo";
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Gene", "id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Disease", "id", IndexDescription.Type.UNIQUE));
        final Map<String, String> properties = dataSource.getProperties(workspace);
        omimLicensed = "true".equalsIgnoreCase(properties.get("omimLicensed"));
        return super.exportGraph(workspace, graph) && exportAnnotations(workspace, graph);
    }

    private boolean exportAnnotations(final Workspace workspace, final Graph graph) throws ExporterException {
        try {
            for (final PhenotypeAnnotation entry : loadPhenotypeAnnotationsFile(workspace))
                exportPhenotypeAnnotation(graph, entry);
            for (final PhenotypeToGenesEntry entry : loadPhenotypeToGenesFile(workspace))
                exportPhenotypeGeneAssociation(graph, entry);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private Iterable<PhenotypeAnnotation> loadPhenotypeAnnotationsFile(final Workspace workspace) throws IOException {
        final MappingIterator<PhenotypeAnnotation> entries = FileUtils.openTsv(workspace, dataSource, "phenotype.hpoa",
                                                                               PhenotypeAnnotation.class);
        return () -> entries;
    }

    private void exportPhenotypeAnnotation(final Graph graph, final PhenotypeAnnotation entry) {
        final Node termNode = graph.findNode("Term", "id", entry.hpoId);
        // If referencing an obsolete term excluded via config file, just skip this annotation
        if (termNode == null || !isPhenotypeAnnotationAllowed(entry))
            return;
        final Node diseaseNode = getOrCreateDiseaseNode(graph, entry.databaseId, entry.diseaseName);
        final EdgeBuilder builder = graph.buildEdge().fromNode(termNode).toNode(diseaseNode);
        builder.withLabel("NOT".equalsIgnoreCase(entry.qualifier) ? "NOT_ASSOCIATED_WITH" : "ASSOCIATED_WITH");
        builder.withPropertyIfNotNull("reference", entry.reference);
        builder.withPropertyIfNotNull("evidence", entry.evidence.name());
        builder.withPropertyIfNotNull("onset", entry.onset);
        builder.withPropertyIfNotNull("frequency", entry.frequency);
        builder.withPropertyIfNotNull("sex", entry.sex);
        builder.withPropertyIfNotNull("modifier", entry.modifier);
        builder.withPropertyIfNotNull("aspect", entry.aspect);
        builder.withPropertyIfNotNull("biocuration", StringUtils.split(entry.biocuration, ';'));
        builder.build();
    }

    private boolean isPhenotypeAnnotationAllowed(final PhenotypeAnnotation entry) {
        return entry.evidence != EvidenceCode.IEA || omimLicensed;
    }

    private Node getOrCreateDiseaseNode(final Graph graph, final String diseaseId, final String diseaseName) {
        Node node = graph.findNode("Disease", "id", diseaseId);
        if (node == null)
            node = graph.addNode("Disease", "id", diseaseId, "names",
                                 new HashSet<>(Collections.singletonList(diseaseName)));
        else {
            // Add name if not already added to the disease node
            Set<String> names = node.getProperty("names");
            if (names == null)
                names = new HashSet<>();
            final int previousSize = names.size();
            names.add(diseaseName);
            if (previousSize != names.size()) {
                node.setProperty("names", names);
                graph.update(node);
            }
        }
        return node;
    }

    private Iterable<PhenotypeToGenesEntry> loadPhenotypeToGenesFile(final Workspace workspace) throws IOException {
        final MappingIterator<PhenotypeToGenesEntry> entries;
        entries = FileUtils.openTsvWithHeader(workspace, dataSource, "phenotype_to_genes.txt",
                                              PhenotypeToGenesEntry.class);
        return () -> entries;
    }

    private void exportPhenotypeGeneAssociation(final Graph graph, final PhenotypeToGenesEntry entry) {
        final Node termNode = graph.findNode("Term", "id", entry.hpoId);
        // If referencing an obsolete term excluded via config file, just skip this annotation
        if (termNode == null)
            return;
        final Node geneNode = getOrCreateGeneNode(graph, entry.entrezGeneId, entry.entrezGeneSymbol);
        final EdgeBuilder builder = graph.buildEdge().fromNode(geneNode).toNode(termNode).withLabel("ASSOCIATED_WITH");
        builder.withPropertyIfNotNull("source", entry.gdSource);
        builder.withPropertyIfNotNull("source_disease_id", entry.diseaseIdForLink);
        if (isAdditionalInfoFromGDSourceNotEmpty(entry))
            builder.withProperty("additional_source_info", entry.additionalInfoFromGDSource);
        builder.build();
    }

    private boolean isAdditionalInfoFromGDSourceNotEmpty(final PhenotypeToGenesEntry entry) {
        return StringUtils.isNotEmpty(entry.additionalInfoFromGDSource) && !"-".equals(
                entry.additionalInfoFromGDSource);
    }

    private Node getOrCreateGeneNode(final Graph graph, final String id, final String symbol) {
        Node node = graph.findNode("Gene", "id", id);
        if (node == null)
            node = graph.addNode("Gene", "id", id, "symbol", symbol);
        return node;
    }
}
