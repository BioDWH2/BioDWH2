package de.unibi.agbi.biodwh2.cpdb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.cpdb.CPDBDataSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPDBUpdater extends Updater<CPDBDataSource> {
    private static final String VERSION_URL = "http://cpdb.molgen.mpg.de/CPDB/rlFrame";
    // Release <b>35</b> (<span>05.06.2021</span>)
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "Release <b>\\d+</b>\\s+\\(<span>(\\d{2})\\.(\\d{2})\\.(\\d{4})</span>\\)");
    public static final String PPI_TAB_FILE_NAME = "ConsensusPathDB_human_PPI.gz";
    public static final String PPI_PSI_MI_FILE_NAME = "ConsensusPathDB_human_PPI.psi25.gz";
    public static final String METABOLITES_FILE_NAME = "CPDB_pathways_metabolites.tab";
    public static final String GENES_ENTREZ_FILE_NAME = "CPDB_pathways_genes_entrez.tab";
    public static final String GENES_ENSEMBL_FILE_NAME = "CPDB_pathways_genes_ensembl.tab";
    public static final String GENES_HGNC_SYMBOL_FILE_NAME = "CPDB_pathways_genes_hgnc-symbol.tab";
    public static final String GENES_HGNC_ID_FILE_NAME = "CPDB_pathways_genes_hgnc-id.tab";
    public static final String GENES_REFSEQ_FILE_NAME = "CPDB_pathways_genes_refseq.tab";
    public static final String GENES_UNIGENE_FILE_NAME = "CPDB_pathways_genes_unigene.tab";
    public static final String GENES_UNIPROT_FILE_NAME = "CPDB_pathways_genes_uniprot.tab";
    private final Map<String, String> downloadUrlMap = new HashMap<>();

    public CPDBUpdater(final CPDBDataSource dataSource) {
        super(dataSource);
        downloadUrlMap.put(PPI_TAB_FILE_NAME, "http://cpdb.molgen.mpg.de/download/" + PPI_TAB_FILE_NAME);
        downloadUrlMap.put(PPI_PSI_MI_FILE_NAME, "http://cpdb.molgen.mpg.de/download/" + PPI_PSI_MI_FILE_NAME);
        downloadUrlMap.put(METABOLITES_FILE_NAME, "http://cpdb.molgen.mpg.de/CPDB/getPathwayMetabolites");
        downloadUrlMap.put(GENES_ENTREZ_FILE_NAME, "http://cpdb.molgen.mpg.de/CPDB/getPathwayGenes?idtype=entrez-gene");
        downloadUrlMap.put(GENES_ENSEMBL_FILE_NAME, "http://cpdb.molgen.mpg.de/CPDB/getPathwayGenes?idtype=ensembl");
        downloadUrlMap.put(GENES_HGNC_SYMBOL_FILE_NAME,
                           "http://cpdb.molgen.mpg.de/CPDB/getPathwayGenes?idtype=hgnc-symbol");
        downloadUrlMap.put(GENES_HGNC_ID_FILE_NAME, "http://cpdb.molgen.mpg.de/CPDB/getPathwayGenes?idtype=hgnc-id");
        downloadUrlMap.put(GENES_REFSEQ_FILE_NAME, "http://cpdb.molgen.mpg.de/CPDB/getPathwayGenes?idtype=refseq");
        downloadUrlMap.put(GENES_UNIGENE_FILE_NAME, "http://cpdb.molgen.mpg.de/CPDB/getPathwayGenes?idtype=unigene");
        downloadUrlMap.put(GENES_UNIPROT_FILE_NAME, "http://cpdb.molgen.mpg.de/CPDB/getPathwayGenes?idtype=uniprot");
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL, 5);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find()) {
            final int day = Integer.parseInt(matcher.group(1));
            final int month = Integer.parseInt(matcher.group(2));
            final int year = Integer.parseInt(matcher.group(3));
            return new Version(year, month, day);
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : expectedFileNames()) {
            final String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
            try {
                HTTPClient.downloadFileAsBrowser(downloadUrlMap.get(fileName), filePath);
            } catch (IOException e) {
                throw new UpdaterConnectionException("Failed to download file '" + fileName + "'", e);
            }
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                PPI_TAB_FILE_NAME, PPI_PSI_MI_FILE_NAME, METABOLITES_FILE_NAME, GENES_ENTREZ_FILE_NAME,
                GENES_ENSEMBL_FILE_NAME, GENES_HGNC_SYMBOL_FILE_NAME, GENES_HGNC_ID_FILE_NAME, GENES_REFSEQ_FILE_NAME,
                GENES_UNIGENE_FILE_NAME, GENES_UNIPROT_FILE_NAME
        };
    }
}
