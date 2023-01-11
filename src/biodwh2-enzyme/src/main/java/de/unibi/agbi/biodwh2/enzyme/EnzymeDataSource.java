package de.unibi.agbi.biodwh2.enzyme;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.enzyme.etl.EnzymeGraphExporter;
import de.unibi.agbi.biodwh2.enzyme.etl.EnzymeMappingDescriber;
import de.unibi.agbi.biodwh2.enzyme.etl.EnzymeUpdater;

public class EnzymeDataSource extends DataSource {
    @Override
    public String getId() {
        return "ENZYME";
    }

    @Override
    public String getFullName() {
        return "Expasy ENZYME";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    public String getLicenseUrl() {
        return "https://enzyme.expasy.org/enzuser.txt";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new EnzymeUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new EnzymeGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new EnzymeMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
