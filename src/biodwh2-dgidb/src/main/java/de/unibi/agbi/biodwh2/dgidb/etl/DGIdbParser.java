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

public class DGIdbParser extends Parser<DGIdbDataSource> {
    @Override
    public boolean parse(Workspace workspace, DGIdbDataSource dataSource) throws ParserException {
        try {
            dataSource.drugs = FileUtils.openTsvWithHeader(workspace, dataSource, "drugs.tsv", Drug.class).readAll();
            dataSource.categories = FileUtils.openTsvWithHeader(workspace, dataSource, "categories.tsv", Category.class)
                                             .readAll();
            dataSource.genes = FileUtils.openTsvWithHeader(workspace, dataSource, "genes.tsv", Gene.class).readAll();
            dataSource.interactions = FileUtils.openTsvWithHeader(workspace, dataSource, "interactions.tsv",
                                                                  Interaction.class).readAll();
        } catch (IOException e) {
            throw new ParserFormatException(e);
        }
        return true;
    }
}
