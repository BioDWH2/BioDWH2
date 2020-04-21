package de.unibi.agbi.biodwh2.sider.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.sider.SiderDataSource;

public class SiderParser extends Parser<SiderDataSource> {
    @Override
    public boolean parse(Workspace workspace, SiderDataSource dataSource) throws ParserException {
        return false;
    }
}
