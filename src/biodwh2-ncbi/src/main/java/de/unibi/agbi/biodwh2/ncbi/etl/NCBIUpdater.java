package de.unibi.agbi.biodwh2.ncbi.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.core.net.HTTPFTPClient;
import de.unibi.agbi.biodwh2.ncbi.NCBIDataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NCBIUpdater extends MultiFileFTPWebUpdater<NCBIDataSource> {
    public NCBIUpdater(NCBIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://ftp.ncbi.nih.gov/";
    }

    @Override
    protected String[] getFilePaths(final Workspace workspace) {
        final String genePrefix = "gene/DATA/";
        List<String> files = new ArrayList<>();
        Collections.addAll(files, genePrefix + "gene_group.gz", genePrefix + "gene_history.gz",
                           genePrefix + "gene_info.gz", genePrefix + "gene_neighbors.gz",
                           genePrefix + "gene_orthologs.gz", genePrefix + "gene_refseq_uniprotkb_collab.gz",
                           genePrefix + "gene2accession.gz", genePrefix + "gene2ensembl.gz", genePrefix + "gene2go.gz",
                           genePrefix + "gene2pubmed.gz", genePrefix + "go_process.dtd", genePrefix + "go_process.xml",
                           genePrefix + "mim2gene_medgen", genePrefix + "stopwords_gene", genePrefix + "README",
                           genePrefix + "README_ensembl");
        try {
            String pubchemPrefix = "pubchem/Compound/CURRENT-Full/SDF/";
            HTTPFTPClient.Entry[] pubchemEntries = client.listDirectory(pubchemPrefix);
            for (HTTPFTPClient.Entry entry : pubchemEntries)
                if (entry.name.endsWith(".gz"))
                    files.add(pubchemPrefix + entry.name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files.toArray(new String[0]);
    }
}
