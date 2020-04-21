package de.unibi.agbi.biodwh2.mondo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.mondo.MondoDataSource;

public class MondoParser extends Parser<MondoDataSource> {
    @Override
    public boolean parse(Workspace workspace, MondoDataSource dataSource) throws ParserException {
        return false;
    }
}
