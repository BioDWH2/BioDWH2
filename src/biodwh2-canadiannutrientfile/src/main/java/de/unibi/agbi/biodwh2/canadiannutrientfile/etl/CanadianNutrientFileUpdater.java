package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import de.unibi.agbi.biodwh2.canadiannutrientfile.CanadianNutrientFileDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;

public class CanadianNutrientFileUpdater extends Updater<CanadianNutrientFileDataSource> {
    public CanadianNutrientFileUpdater(CanadianNutrientFileDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        return false;
    }
}
