package de.unibi.agbi.biodwh2.tarbase.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.Configuration;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.tarbase.TarBaseDataSource;
import de.unibi.agbi.biodwh2.tarbase.model.Entry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class TarBaseGraphExporter extends GraphExporter<TarBaseDataSource> {
    static final String GENE_LABEL = "Gene";
    static final String TRANSCRIPT_LABEL = "Transcript";
    static final String MI_RNA_LABEL = "miRNA";
    static final String TRANSCRIBES_TO_LABEL = "TRANSCRIBES_TO";
    static final String TARGETS_LABEL = "TARGETS";

    public TarBaseGraphExporter(final TarBaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 3;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TRANSCRIPT_LABEL, ID_KEY, false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(MI_RNA_LABEL, ID_KEY, false, IndexDescription.Type.UNIQUE));
        final Configuration.GlobalProperties.SpeciesFilter speciesFilter = workspace.getConfiguration()
                                                                                    .getGlobalProperties()
                                                                                    .getSpeciesFilter();
        for (final String fileName : TarBaseUpdater.FILE_NAMES)
            exportFile(workspace, graph, fileName, speciesFilter);
        return true;
    }

    private void exportFile(final Workspace workspace, final Graph graph, final String fileName,
                            final Configuration.GlobalProperties.SpeciesFilter speciesFilter) {
        try (TarArchiveInputStream stream = FileUtils.openTarGzip(workspace, dataSource, fileName)) {
            while (stream.getNextTarEntry() != null)
                exportEntries(graph, FileUtils.openSeparatedValuesFile(stream, Entry.class, '\t', true, false),
                              speciesFilter);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + fileName + "'", e);
        }
    }

    private void exportEntries(final Graph graph, final MappingIterator<Entry> entries,
                               final Configuration.GlobalProperties.SpeciesFilter speciesFilter) {
        graph.beginEdgeIndicesDelay(TARGETS_LABEL);
        graph.beginEdgeIndicesDelay(TRANSCRIBES_TO_LABEL);
        while (entries.hasNext()) {
            final Entry entry = entries.next();
            final Integer speciesTaxonomyId = getSpeciesTaxonomyId(entry.species);
            if (!speciesFilter.isSpeciesAllowed(speciesTaxonomyId))
                continue;
            Long geneNodeId = getOrCreateGeneNode(graph, entry.geneId, entry.geneName, speciesTaxonomyId);
            Long transcriptNodeId = getOrCreateTranscriptNode(graph, geneNodeId, entry.transcriptId,
                                                              entry.transcriptName, speciesTaxonomyId);
            Long mirnaNodeId = getOrCreateMiRNANode(graph, entry.mirnaId, entry.mirnaName, speciesTaxonomyId);
            final EdgeBuilder builder = graph.buildEdge().withLabel(TARGETS_LABEL).fromNode(mirnaNodeId).toNode(
                    transcriptNodeId);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "cell_line", entry.cellLine);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "cell_type", entry.cellType);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "tissue", entry.tissue);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "strand", entry.strand);
            setEdgeBuilderIntegerPropertyIfNotNullAndNotNA(builder, "start", entry.start);
            setEdgeBuilderIntegerPropertyIfNotNullAndNotNA(builder, "end", entry.end);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "gene_location", entry.geneLocation);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "chromosome", entry.chromosome);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "experimental_method", entry.experimentalMethod);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "regulation", entry.regulation);
            setEdgeBuilderIntegerPropertyIfNotNullAndNotNA(builder, "confidence", entry.confidence);
            setEdgeBuilderIntegerPropertyIfNotNullAndNotNA(builder, "article_pubmed_id", entry.articlePubmedId);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "interaction_group", entry.interactionGroup);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "microt_score", entry.microtScore);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "comment", entry.comment);
            builder.build();
        }
        graph.endEdgeIndicesDelay(TRANSCRIBES_TO_LABEL);
        graph.endEdgeIndicesDelay(TARGETS_LABEL);
    }

    private Integer getSpeciesTaxonomyId(final String species) {
        // Murine Gammaherpesvirus 68
        if (species.equals("MHV68") || species.equals("HIV-1"))
            return null;
        if (species.equals("KSHV"))
            return SpeciesLookup.KAPOSI_SARCOMA_ASSOCIATED_HERPESVIRUS.ncbiTaxId;
        if (species.equals("EBV"))
            return SpeciesLookup.EPSTEIN_BARR_VIRUS.ncbiTaxId;
        final SpeciesLookup.Entry entry = SpeciesLookup.getByScientificName(species);
        return entry != null ? entry.ncbiTaxId : null;
    }

    private Long getOrCreateGeneNode(final Graph graph, final String id, final String name,
                                     final Integer speciesTaxonomyId) {
        Node node = graph.findNode(GENE_LABEL, ID_KEY, id);
        if (node == null) {
            if (name != null && speciesTaxonomyId != null)
                node = graph.addNode(GENE_LABEL, ID_KEY, id, "name", name, "species_ncbi_taxid", speciesTaxonomyId);
            else if (name != null)
                node = graph.addNode(GENE_LABEL, ID_KEY, id, "name", name);
            else if (speciesTaxonomyId != null)
                node = graph.addNode(GENE_LABEL, ID_KEY, id, "species_ncbi_taxid", speciesTaxonomyId);
            else
                node = graph.addNode(GENE_LABEL, ID_KEY, id);
        }
        return node.getId();
    }

    private Long getOrCreateTranscriptNode(final Graph graph, final Long geneNodeId, final String id, final String name,
                                           final Integer speciesTaxonomyId) {
        Node node = graph.findNode(TRANSCRIPT_LABEL, ID_KEY, id);
        if (node == null) {
            if (name != null && speciesTaxonomyId != null)
                node = graph.addNode(TRANSCRIPT_LABEL, ID_KEY, id, "name", name, "species_ncbi_taxid",
                                     speciesTaxonomyId);
            else if (name != null)
                node = graph.addNode(TRANSCRIPT_LABEL, ID_KEY, id, "name", name);
            else if (speciesTaxonomyId != null)
                node = graph.addNode(TRANSCRIPT_LABEL, ID_KEY, id, "species_ncbi_taxid", speciesTaxonomyId);
            else
                node = graph.addNode(TRANSCRIPT_LABEL, ID_KEY, id);
            graph.addEdge(geneNodeId, node, TRANSCRIBES_TO_LABEL);
        }
        return node.getId();
    }

    private Long getOrCreateMiRNANode(final Graph graph, final String id, final String name,
                                      final Integer speciesTaxonomyId) {
        Node node = graph.findNode(MI_RNA_LABEL, ID_KEY, id);
        if (node == null) {
            if (name != null && speciesTaxonomyId != null)
                node = graph.addNode(MI_RNA_LABEL, ID_KEY, id, "name", name, "species_ncbi_taxid", speciesTaxonomyId);
            else if (name != null)
                node = graph.addNode(MI_RNA_LABEL, ID_KEY, id, "name", name);
            else if (speciesTaxonomyId != null)
                node = graph.addNode(MI_RNA_LABEL, ID_KEY, id, "species_ncbi_taxid", speciesTaxonomyId);
            else
                node = graph.addNode(MI_RNA_LABEL, ID_KEY, id);
        }
        return node.getId();
    }

    private void setEdgeBuilderPropertyIfNotNullAndNotNA(final EdgeBuilder builder, final String key,
                                                         final String value) {
        builder.withPropertyIfNotNull(key, !"NA".equals(value) ? value : null);
    }

    private void setEdgeBuilderIntegerPropertyIfNotNullAndNotNA(final EdgeBuilder builder, final String key,
                                                                final String value) {
        builder.withPropertyIfNotNull(key,
                                      !"NA".equals(value) && StringUtils.isNotEmpty(value) ? Integer.parseInt(value) :
                                      null);
    }
}
