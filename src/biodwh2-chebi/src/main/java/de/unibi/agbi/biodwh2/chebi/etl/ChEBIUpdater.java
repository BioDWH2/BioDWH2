package de.unibi.agbi.biodwh2.chebi.etl;

import de.unibi.agbi.biodwh2.chebi.ChEBIDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ChEBIUpdater extends MultiFileFTPWebUpdater<ChEBIDataSource> {
    // unused
    // static final String UNIPROT_FILE_NAME = "chebi_uniprot.zip";
    // static final String COMMENTS_FILE_NAME = "comments_3star.tsv";
    static final String INCHI_FILE_NAME = "chebiId_inchi_3star.tsv";
    static final String CHEMICAL_DATA_FILE_NAME = "chemical_data_3star.tsv";
    static final String COMPOUND_ORIGINS_FILE_NAME = "compound_origins_3star.tsv";
    static final String COMPOUNDS_FILE_NAME = "compounds_3star.tsv.gz";
    static final String DATABASE_ACCESSION_FILE_NAME = "database_accession_3star.tsv";
    static final String NAMES_FILE_NAME = "names_3star.tsv.gz";
    static final String REFERENCE_FILE_NAME = "reference_3star.tsv.gz";
    static final String RELATION_FILE_NAME = "relation_3star.tsv";
    static final String STRUCTURES_FILE_NAME = "structures_3star.csv.gz";

    public ChEBIUpdater(final ChEBIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        if (!super.tryUpdateFiles(workspace))
            return false;
        // The header of the 3star filtered compound origins file is wrong as well as some rows with additional empty
        // columns and needs to be fixed
        try {
            final Path filePath = dataSource.resolveSourceFilePath(workspace, COMPOUND_ORIGINS_FILE_NAME);
            final List<String> lines = Files.readAllLines(filePath, StandardCharsets.ISO_8859_1);
            lines.set(0,
                      "ID\tCOMPOUND_ID\tSPECIES_TEXT\tSPECIES_ACCESSION\tCOMPONENT_TEXT\tCOMPONENT_ACCESSION\tSTRAIN_TEXT\tSTRAIN_ACCESSION\tSOURCE_TYPE\tSOURCE_ACCESSION\tCOMMENTS");
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                final String[] parts = StringUtils.splitPreserveAllTokens(line, '\t');
                if (parts.length > 11) {
                    List<String> fixed = new ArrayList<>(List.of(parts));
                    for (int j = fixed.size() - 1; j >= 0 && fixed.size() > 11; j--)
                        if (fixed.get(j).isBlank())
                            fixed.remove(j);
                    lines.set(i, String.join("\t", fixed));
                }
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UpdaterException("Failed to fix file '" + COMPOUND_ORIGINS_FILE_NAME + "'", e);
        }
        return true;
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://ftp.ebi.ac.uk/pub/databases/chebi/Flat_file_tab_delimited/";
    }

    @Override
    protected String[] getFilePaths(final Workspace workspace) {
        return new String[]{
                INCHI_FILE_NAME, CHEMICAL_DATA_FILE_NAME, COMPOUND_ORIGINS_FILE_NAME, COMPOUNDS_FILE_NAME,
                DATABASE_ACCESSION_FILE_NAME, NAMES_FILE_NAME, REFERENCE_FILE_NAME, RELATION_FILE_NAME,
                STRUCTURES_FILE_NAME
        };
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                INCHI_FILE_NAME, CHEMICAL_DATA_FILE_NAME, COMPOUND_ORIGINS_FILE_NAME, COMPOUNDS_FILE_NAME,
                DATABASE_ACCESSION_FILE_NAME, NAMES_FILE_NAME, REFERENCE_FILE_NAME, RELATION_FILE_NAME,
                STRUCTURES_FILE_NAME
        };
    }
}
