package de.unibi.agbi.biodwh2.drugcentral.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.drugcentral.DrugCentralDataSource;

public class DrugCentralParser extends Parser<DrugCentralDataSource> {
    @Override
    public boolean parse(Workspace workspace, DrugCentralDataSource dataSource) {
        return true;
    }
}
