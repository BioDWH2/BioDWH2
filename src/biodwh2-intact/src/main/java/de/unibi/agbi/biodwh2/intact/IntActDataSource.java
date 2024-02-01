package de.unibi.agbi.biodwh2.intact;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.intact.etl.IntActGraphExporter;
import de.unibi.agbi.biodwh2.intact.etl.IntActMappingDescriber;
import de.unibi.agbi.biodwh2.intact.etl.IntActUpdater;

public class IntActDataSource extends DataSource {
    @Override
    public String getId() {
        return "IntAct";
    }

    @Override
    public String getFullName() {
        return "IntAct";
    }

    @Override
    public String getDescription() {
        return "IntAct provides a free, open source database system for molecular interaction data.";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    public String getLicenseUrl() {
        return "https://www.ebi.ac.uk/intact/about#license_privacy";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new IntActUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new IntActGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new IntActMappingDescriber(this);
    }
}
