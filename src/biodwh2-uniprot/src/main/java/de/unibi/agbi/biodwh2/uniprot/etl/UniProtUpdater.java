package de.unibi.agbi.biodwh2.uniprot.etl;

import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.uniprot.UniProtDataSource;

public class UniProtUpdater extends MultiFileFTPWebUpdater<UniProtDataSource> {
    public UniProtUpdater(final UniProtDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/";
    }

    @Override
    protected String[] getFilePaths() {
        return new String[]{
                "uniprot_sprot_human.xml.gz"
        };
    }
}
