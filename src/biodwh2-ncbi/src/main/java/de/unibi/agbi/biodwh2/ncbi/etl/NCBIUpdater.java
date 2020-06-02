package de.unibi.agbi.biodwh2.ncbi.etl;

import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.ncbi.NCBIDataSource;

public class NCBIUpdater extends MultiFileFTPWebUpdater<NCBIDataSource> {
    @Override
    protected String getFTPIndexUrl() {
        return "https://ftp.ncbi.nih.gov/";
    }

    @Override
    protected String[] getFilePaths() {
        final String genePrefix = "gene/DATA/";
        return new String[]{
                genePrefix + "gene_group.gz",
                genePrefix + "gene_history.gz",
                genePrefix + "gene_info.gz",
                genePrefix + "gene_neighbors.gz",
                genePrefix + "gene_orthologs.gz",
                genePrefix + "gene_refseq_uniprotkb_collab.gz",
                genePrefix + "gene2accession.gz",
                genePrefix + "gene2ensembl.gz",
                genePrefix + "gene2go.gz",
                genePrefix + "gene2pubmed.gz",
                genePrefix + "go_process.dtd",
                genePrefix + "go_process.xml",
                genePrefix + "mim2gene_medgen",
                genePrefix + "stopwords_gene",
                genePrefix + "README",
                genePrefix + "README_ensembl"
        };
    }
}
