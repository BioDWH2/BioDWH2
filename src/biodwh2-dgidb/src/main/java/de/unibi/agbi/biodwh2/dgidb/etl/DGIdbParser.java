package de.unibi.agbi.biodwh2.dgidb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.dgidb.DGIdbDataSource;
import de.unibi.agbi.biodwh2.dgidb.model.Category;
import de.unibi.agbi.biodwh2.dgidb.model.Drug;
import de.unibi.agbi.biodwh2.dgidb.model.Gene;
import de.unibi.agbi.biodwh2.dgidb.model.Interaction;

import java.io.IOException;
import java.util.List;

public class DGIdbParser extends Parser<DGIdbDataSource> {
    static final String INTERACTIONS_FILE_NAME = "interactions.tsv";
    static final String DRUGS_FILE_NAME = "drugs.tsv";
    static final String GENES_FILE_NAME = "genes.tsv";
    static final String CATEGORIES_FILE_NAME = "categories.tsv";

    public DGIdbParser(final DGIdbDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        try {
            dataSource.drugs = readAllFromTsv(workspace, DRUGS_FILE_NAME, Drug.class);
            dataSource.categories = readAllFromTsv(workspace, CATEGORIES_FILE_NAME, Category.class);
            dataSource.genes = readAllFromTsv(workspace, GENES_FILE_NAME, Gene.class);
            dataSource.interactions = readAllFromTsv(workspace, INTERACTIONS_FILE_NAME, Interaction.class);
        } catch (IOException e) {
            throw new ParserFormatException(e);
        }
        return true;
    }

    private <T> List<T> readAllFromTsv(final Workspace workspace, final String fileName,
                                       final Class<T> classType) throws IOException {
        return FileUtils.openTsvWithHeader(workspace, dataSource, fileName, classType).readAll();
    }
}
