package de.unibi.agbi.biodwh2.biom2metdisease.etl;

import de.unibi.agbi.biodwh2.biom2metdisease.model.Associations;
import de.unibi.agbi.biodwh2.core.etl.SingleFileCsvParser;
import de.unibi.agbi.biodwh2.biom2metdisease.BioM2MetDiseaseDataSource;

import java.util.List;

public class BioM2MetDiseaseParser extends SingleFileCsvParser<BioM2MetDiseaseDataSource, Associations> {
    public BioM2MetDiseaseParser(final BioM2MetDiseaseDataSource dataSource) {
        super(dataSource, Associations.class, true, CsvType.TSV, BioM2MetDiseaseUpdater.FILE_NAME);
    }

    @Override
    protected void storeResults(BioM2MetDiseaseDataSource dataSource, List<Associations> results) {
        dataSource.associations = results;
    }
}
