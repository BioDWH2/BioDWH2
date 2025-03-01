package de.unibi.agbi.biodwh2.gwascatalog.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.gwascatalog.GWASCatalogDataSource;
import de.unibi.agbi.biodwh2.gwascatalog.model.Ancestry;
import de.unibi.agbi.biodwh2.gwascatalog.model.Association;
import de.unibi.agbi.biodwh2.gwascatalog.model.Study;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public final class GWASCatalogGraphExporter extends GraphExporter<GWASCatalogDataSource> {
    static final String PUBLICATION_LABEL = "Publication";
    static final String STUDY_LABEL = "Study";
    static final String TRAIT_LABEL = "Trait";
    static final String ANCESTRY_LABEL = "Ancestry";
    static final String ASSOCIATION_LABEL = "Association";

    public GWASCatalogGraphExporter(final GWASCatalogDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(PUBLICATION_LABEL, "pmid", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(STUDY_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TRAIT_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        try {
            exportStudies(workspace, graph);
            exportAssociations(workspace, graph);
            exportAncestries(workspace, graph);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private void exportStudies(final Workspace workspace, final Graph graph) throws IOException {
        FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource, GWASCatalogUpdater.STUDIES_FILE_NAME,
                                                  Study.class, (entry) -> exportStudy(graph, entry));
    }

    private void exportStudy(final Graph graph, final Study study) {
        final NodeBuilder builder = graph.buildNode().withLabel(STUDY_LABEL);
        builder.withProperty(ID_KEY, study.studyAccession);
        builder.withProperty("date_added", study.dateAddedToCatalog);
        builder.withProperty("initial_sample_size", study.initialSampleSize);
        builder.withProperty("replication_sample_size", study.replicationSampleSize);
        builder.withPropertyIfNotNull("genotyping_technology", study.genotypingTechnology);
        builder.withPropertyIfNotNull("platform", study.platform);
        builder.withPropertyIfNotNull("disease_or_trait", study.diseaseOrTrait);
        builder.withPropertyIfNotNull("mapped_traits", study.mappedTrait);
        final Node node = builder.build();
        final Node publicationNode = getOrCreatePublication(graph, study.pubmedId, study.firstAuthor, study.journal,
                                                            study.studyTitle, study.datePublished);
        graph.addEdge(node, publicationNode, "HAS_REFERENCE");
        final long[] traitNodeIds = exportTraits(graph, study.mappedTrait, study.mappedTraitUri);
        for (long traitNodeId : traitNodeIds)
            graph.addEdge(node, traitNodeId, "STUDIES");
    }

    private Node getOrCreatePublication(final Graph graph, final Integer pubmedId, final String firstAuthor,
                                        final String journal, final String title, final String datePublished) {
        Node node = graph.findNode(PUBLICATION_LABEL, "pmid", pubmedId);
        if (node == null) {
            final NodeBuilder builder = graph.buildNode().withLabel(PUBLICATION_LABEL);
            builder.withProperty("pmid", pubmedId);
            builder.withPropertyIfNotNull("first_author", firstAuthor);
            builder.withPropertyIfNotNull("date_published", datePublished);
            builder.withPropertyIfNotNull("journal", journal);
            builder.withPropertyIfNotNull("title", title);
            node = builder.build();
        }
        return node;
    }

    private long[] exportTraits(final Graph graph, final String mappedTrait, final String mappedTraitUri) {
        if (mappedTraitUri == null)
            return new long[0];
        final String[] traitUris = StringUtils.split(mappedTraitUri, ',');
        String[] traits = null;
        if (mappedTrait != null) {
            traits = StringUtils.splitByWholeSeparator(mappedTrait, ", ");
            if (traits.length != traitUris.length) {
                if (traitUris.length == 1)
                    traits = new String[]{mappedTrait};
                else {
                    // TODO: trait names that could not be split properly due to the file format
                    traits = null;
                }
            }
        }
        final long[] result = new long[traitUris.length];
        for (int i = 0; i < traitUris.length; i++) {
            Node traitNode = graph.findNode(TRAIT_LABEL, ID_KEY, traitUris[i].trim());
            if (traitNode == null) {
                if (traits != null)
                    traitNode = graph.addNode(TRAIT_LABEL, ID_KEY, traitUris[i].trim(), "name", traits[i].trim());
                else
                    traitNode = graph.addNode(TRAIT_LABEL, ID_KEY, traitUris[i].trim());
            }
            result[i] = traitNode.getId();
        }
        return result;
    }

    private void exportAssociations(final Workspace workspace, final Graph graph) throws IOException {
        FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource, GWASCatalogUpdater.ASSOCIATIONS_FILE_NAME,
                                                  Association.class, (entry) -> exportAssociation(graph, entry));
    }

    private void exportAssociation(final Graph graph, final Association association) {
        final NodeBuilder builder = graph.buildNode().withLabel(ASSOCIATION_LABEL);
        builder.withProperty("date_added", association.dateAddedToCatalog);
        builder.withPropertyIfNotNull("mapped_traits", association.mappedTrait);
        builder.withPropertyIfNotNull("chr_id", association.chrId);
        builder.withPropertyIfNotNull("chr_position", association.chrPosition);
        builder.withPropertyIfNotNull("cnv", association.cnv);
        builder.withPropertyIfNotNull("context", association.context);
        builder.withPropertyIfNotNull("downstream_gene_distance", association.downstreamGeneDistance);
        builder.withPropertyIfNotNull("downstream_gene_id", association.downstreamGeneId);
        builder.withPropertyIfNotNull("intergenic", association.intergenic);
        builder.withPropertyIfNotNull("mapped_genes", association.mappedGenes);
        builder.withPropertyIfNotNull("merged", association.merged);
        builder.withPropertyIfNotNull("95_percent_confidence_interval", association.ninetyfiveConfidenceIntervalText);
        builder.withPropertyIfNotNull("or_or_beta", association.orOrBeta);
        builder.withPropertyIfNotNull("p_value", association.pValue);
        builder.withPropertyIfNotNull("p_value_mlog", association.pValueMlog);
        builder.withPropertyIfNotNull("p_value_text", association.pValueText);
        builder.withPropertyIfNotNull("region", association.region);
        builder.withPropertyIfNotNull("reported_genes", association.reportedGenes);
        builder.withPropertyIfNotNull("risk_allele_frequency", association.riskAlleleFrequency);
        builder.withPropertyIfNotNull("snp_gene_ids", association.snpGeneIds);
        builder.withPropertyIfNotNull("snp_id_current", association.snpIdCurrent);
        builder.withPropertyIfNotNull("snps", association.snps);
        builder.withPropertyIfNotNull("strongest_snp_risk_allele", association.strongestSNPRiskAllele);
        builder.withPropertyIfNotNull("upstream_gene_distance", association.upstreamGeneDistance);
        builder.withPropertyIfNotNull("upstream_gene_id", association.upstreamGeneId);
        final Node node = builder.build();
        final long[] traitNodeIds = exportTraits(graph, association.mappedTrait, association.mappedTraitUri);
        for (long traitNodeId : traitNodeIds)
            graph.addEdge(node, traitNodeId, "STUDIES");
        Node studyNode = graph.findNode(STUDY_LABEL, ID_KEY, association.studyAccession);
        if (studyNode == null) {
            final NodeBuilder studyBuilder = graph.buildNode().withLabel(STUDY_LABEL);
            studyBuilder.withProperty(ID_KEY, association.studyAccession);
            studyBuilder.withProperty("initial_sample_size", association.initialSampleSize);
            studyBuilder.withProperty("replication_sample_size", association.replicationSampleSize);
            studyBuilder.withPropertyIfNotNull("genotyping_technology", association.genotypingTechnology);
            studyBuilder.withPropertyIfNotNull("platform", association.platform);
            studyBuilder.withPropertyIfNotNull("disease_or_trait", association.diseaseOrTrait);
            studyBuilder.withPropertyIfNotNull("mapped_traits", association.mappedTrait);
            studyNode = studyBuilder.build();
            final Node publicationNode = getOrCreatePublication(graph, association.pubmedId, association.firstAuthor,
                                                                association.journal, association.studyTitle,
                                                                association.datePublished);
            graph.addEdge(studyNode, publicationNode, "HAS_REFERENCE");
        }
        graph.addEdge(studyNode, node, "WITH_ASSOCIATION");
    }

    private void exportAncestries(final Workspace workspace, final Graph graph) throws IOException {
        FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource, GWASCatalogUpdater.ANCESTRY_FILE_NAME,
                                                  Ancestry.class, (entry) -> exportAncestry(graph, entry));
    }

    private void exportAncestry(final Graph graph, final Ancestry ancestry) {
        final NodeBuilder builder = graph.buildNode().withLabel(ANCESTRY_LABEL);
        builder.withPropertyIfNotNull("stage", ancestry.stage);
        if (ancestry.numberOfIndividuals != null)
            builder.withPropertyIfNotNull("individuals", Integer.parseInt(ancestry.numberOfIndividuals));
        builder.withPropertyIfNotNull("broad_category", ancestry.broadAncestralCategory);
        builder.withPropertyIfNotNull("origin_country", ancestry.countryOfOrigin);
        builder.withPropertyIfNotNull("recruitment_country", ancestry.countryOfRecruitment);
        builder.withPropertyIfNotNull("additional_description", ancestry.additionalAncestryDescription);
        final Node node = builder.build();
        Node studyNode = graph.findNode(STUDY_LABEL, ID_KEY, ancestry.studyAccession);
        if (studyNode == null) {
            final NodeBuilder studyBuilder = graph.buildNode().withLabel(STUDY_LABEL);
            studyBuilder.withProperty(ID_KEY, ancestry.studyAccession);
            studyBuilder.withProperty("initial_sample_size", ancestry.initialSampleDescription);
            studyBuilder.withProperty("replication_sample_size", ancestry.replicationSampleDescription);
            studyNode = studyBuilder.build();
            final Node publicationNode = getOrCreatePublication(graph, ancestry.pubmedId, ancestry.firstAuthor, null,
                                                                null, ancestry.datePublished);
            graph.addEdge(studyNode, publicationNode, "HAS_REFERENCE");
        }
        graph.addEdge(studyNode, node, "WITH_ANCESTRY");
    }
}
