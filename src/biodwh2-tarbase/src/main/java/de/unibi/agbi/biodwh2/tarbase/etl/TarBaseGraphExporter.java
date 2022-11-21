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
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.tarbase.TarBaseDataSource;
import de.unibi.agbi.biodwh2.tarbase.model.Entry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TarBaseGraphExporter extends GraphExporter<TarBaseDataSource> {
    static final String GENE_LABEL = "Gene";
    static final String MIRNA_LABEL = "miRNA";

    public TarBaseGraphExporter(final TarBaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, false, IndexDescription.Type.NON_UNIQUE));
        graph.addIndex(IndexDescription.forNode(MIRNA_LABEL, ID_KEY, false, IndexDescription.Type.UNIQUE));
        final Configuration.GlobalProperties.SpeciesFilter speciesFilter = workspace.getConfiguration()
                                                                                    .getGlobalProperties()
                                                                                    .getSpeciesFilter();
        try (TarArchiveInputStream stream = FileUtils.openTarGzip(workspace, dataSource, TarBaseUpdater.FILE_NAME)) {
            while (stream.getNextTarEntry() != null)
                exportEntries(graph, FileUtils.openSeparatedValuesFile(stream, Entry.class, '\t', true, false),
                              speciesFilter);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + TarBaseUpdater.FILE_NAME + "'", e);
        }
        return true;
    }

    private void exportEntries(final Graph graph, final MappingIterator<Entry> entries,
                               final Configuration.GlobalProperties.SpeciesFilter speciesFilter) {
        final Map<String, Long> geneKeyNodeIdMap = new HashMap<>();
        final Map<String, Long> rnaNodeIdMap = new HashMap<>();
        graph.beginEdgeIndicesDelay("TARGETS");
        while (entries.hasNext()) {
            final Entry entry = entries.next();
            Long geneNodeId = getOrCreateGeneNode(graph, speciesFilter, geneKeyNodeIdMap, entry);
            Long rnaNodeId = getOrCreateRNANode(graph, speciesFilter, rnaNodeIdMap, entry);
            if (geneNodeId == null || rnaNodeId == null)
                continue;
            final EdgeBuilder builder = graph.buildEdge().withLabel("TARGETS").fromNode(rnaNodeId).toNode(geneNodeId);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "cell_line", entry.cellLine);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "tissue", entry.tissue);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "category", entry.category);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "method", entry.method);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "positive_negative", entry.positiveNegative);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "direct_indirect", entry.directIndirect);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "up_down", entry.upDown);
            setEdgeBuilderPropertyIfNotNullAndNotNA(builder, "condition", entry.condition);
            builder.build();
        }
        graph.endEdgeIndicesDelay("TARGETS");
    }

    private Long getOrCreateGeneNode(final Graph graph,
                                     final Configuration.GlobalProperties.SpeciesFilter speciesFilter,
                                     final Map<String, Long> geneKeyNodeIdMap, final Entry entry) {
        final String species = fixSpecies(entry.species);
        final String geneKey = entry.geneId + "|" + species;
        Long geneNodeId = geneKeyNodeIdMap.get(geneKey);
        if (geneNodeId == null) {
            final Integer speciesNCBITaxId = getSpeciesTaxonomyId(species);
            if (!speciesFilter.isSpeciesAllowed(speciesNCBITaxId))
                return null;
            final NodeBuilder builder = graph.buildNode().withLabel(GENE_LABEL);
            builder.withPropertyIfNotNull(ID_KEY, stripSpecies(entry.geneId));
            builder.withPropertyIfNotNull("name", stripSpecies(entry.geneName));
            builder.withPropertyIfNotNull("species", species);
            builder.withPropertyIfNotNull("species_ncbi_taxid", speciesNCBITaxId);
            geneNodeId = builder.build().getId();
            geneKeyNodeIdMap.put(geneKey, geneNodeId);
        }
        return geneNodeId;
    }

    private String fixSpecies(final String species) {
        if (species.equals("Pan_troglodytes"))
            return "Pan troglodytes";
        return species;
    }

    private Integer getSpeciesTaxonomyId(final String species) {
        if (species.equals("KSHV"))
            return SpeciesLookup.getByScientificName("Kaposi sarcoma-associated herpesvirus").ncbiTaxId;
        final SpeciesLookup.Entry entry = SpeciesLookup.getByScientificName(species);
        return entry != null ? entry.ncbiTaxId : null;
    }

    private String stripSpecies(final String value) {
        final int abbreviationIndex = value.indexOf('(');
        if (abbreviationIndex == -1)
            return value;
        final String bracedValue = value.substring(abbreviationIndex);
        if (bracedValue.contains(" of "))
            return value;
        return value.substring(0, abbreviationIndex).trim();
    }

    private Long getOrCreateRNANode(final Graph graph, final Configuration.GlobalProperties.SpeciesFilter speciesFilter,
                                    final Map<String, Long> rnaNodeIdMap, final Entry entry) {
        Long rnaNodeId = rnaNodeIdMap.get(entry.mirna);
        if (rnaNodeId == null) {
            final String species = fixSpecies(entry.species);
            final Integer speciesNCBITaxId = getSpeciesTaxonomyId(species);
            if (!speciesFilter.isSpeciesAllowed(speciesNCBITaxId))
                return null;
            final NodeBuilder builder = graph.buildNode().withLabel(MIRNA_LABEL);
            builder.withPropertyIfNotNull(ID_KEY, entry.mirna);
            builder.withPropertyIfNotNull("species", species);
            builder.withPropertyIfNotNull("species_ncbi_taxid", speciesNCBITaxId);
            rnaNodeId = builder.build().getId();
            rnaNodeIdMap.put(entry.mirna, rnaNodeId);
        }
        return rnaNodeId;
    }

    private void setEdgeBuilderPropertyIfNotNullAndNotNA(final EdgeBuilder builder, final String key,
                                                         final String value) {
        builder.withPropertyIfNotNull(key, !"NA".equals(value) ? value : null);
    }
}
