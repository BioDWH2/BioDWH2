package de.unibi.agbi.biodwh2.uniprot.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.uniprot.UniProtDataSource;

public class UniProtUpdater extends MultiFileFTPWebUpdater<UniProtDataSource> {
    static final String HUMAN_SPROT_FILE_NAME = "uniprot_sprot_human.xml.gz";

    public UniProtUpdater(final UniProtDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/";
    }

    @Override
    protected String[] getFilePaths(final Workspace workspace) {
        return new String[]{HUMAN_SPROT_FILE_NAME};
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{HUMAN_SPROT_FILE_NAME};
    }
}
