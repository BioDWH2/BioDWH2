package de.unibi.agbi.biodwh2.tissues;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.tissues.etl.TissuesGraphExporter;
import de.unibi.agbi.biodwh2.tissues.etl.TissuesMappingDescriber;
import de.unibi.agbi.biodwh2.tissues.etl.TissuesUpdater;

public class TissuesDataSource extends DataSource {
    @Override
    public String getId() {
        return "TISSUES";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new TissuesUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new TissuesGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new TissuesMappingDescriber(this);
    }
}
