package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import de.unibi.agbi.biodwh2.canadiannutrientfile.CanadianNutrientFileDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;

public class CanadianNutrientFileParser extends Parser<CanadianNutrientFileDataSource> {
    public CanadianNutrientFileParser(CanadianNutrientFileDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(Workspace workspace) throws ParserException {
        return false;
    }
}
