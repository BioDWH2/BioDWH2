package de.unibi.agbi.biodwh2.chebi.etl;

import de.unibi.agbi.biodwh2.chebi.ChEBIDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;

public class ChEBIUpdater extends MultiFileFTPWebUpdater<ChEBIDataSource> {
    // unused
    // static final String STRUCTURE_REGISTRY_FILE_NAME = "structure_registry.tsv.gz";
    static final String CHEMICAL_DATA_FILE_NAME = "chemical_data.tsv.gz";
    static final String COMMENTS_FILE_NAME = "comments.tsv.gz";
    static final String COMPOUND_ORIGINS_FILE_NAME = "compound_origins.tsv.gz";
    static final String COMPOUNDS_FILE_NAME = "compounds.tsv.gz";
    static final String DATABASE_ACCESSION_FILE_NAME = "database_accession.tsv.gz";
    static final String NAMES_FILE_NAME = "names.tsv.gz";
    static final String REFERENCE_FILE_NAME = "reference.tsv.gz";
    static final String RELATION_FILE_NAME = "relation.tsv.gz";
    static final String RELATION_TYPE_FILE_NAME = "relation_type.tsv.gz";
    static final String SECONDARY_IDS_FILE_NAME = "secondary_ids.tsv.gz";
    static final String SOURCE_FILE_NAME = "source.tsv.gz";
    static final String STATUS_FILE_NAME = "status.tsv.gz";
    static final String STRUCTURES_FILE_NAME = "structures.tsv.gz";
    static final String WURCS_FILE_NAME = "wurcs.tsv.gz";

    public ChEBIUpdater(final ChEBIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://ftp.ebi.ac.uk/pub/databases/chebi/flat_files/";
    }

    @Override
    protected String[] getFilePaths(final Workspace workspace) {
        return new String[]{
                CHEMICAL_DATA_FILE_NAME, COMMENTS_FILE_NAME, COMPOUND_ORIGINS_FILE_NAME, COMPOUNDS_FILE_NAME,
                DATABASE_ACCESSION_FILE_NAME, NAMES_FILE_NAME, REFERENCE_FILE_NAME, RELATION_FILE_NAME,
                RELATION_TYPE_FILE_NAME, SECONDARY_IDS_FILE_NAME, SOURCE_FILE_NAME, STATUS_FILE_NAME,
                STRUCTURES_FILE_NAME, WURCS_FILE_NAME
        };
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                CHEMICAL_DATA_FILE_NAME, COMMENTS_FILE_NAME, COMPOUND_ORIGINS_FILE_NAME, COMPOUNDS_FILE_NAME,
                DATABASE_ACCESSION_FILE_NAME, NAMES_FILE_NAME, REFERENCE_FILE_NAME, RELATION_FILE_NAME,
                RELATION_TYPE_FILE_NAME, SECONDARY_IDS_FILE_NAME, SOURCE_FILE_NAME, STATUS_FILE_NAME,
                STRUCTURES_FILE_NAME, WURCS_FILE_NAME
        };
    }
}
