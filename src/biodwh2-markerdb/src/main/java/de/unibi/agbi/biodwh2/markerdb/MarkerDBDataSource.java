package de.unibi.agbi.biodwh2.markerdb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.markerdb.etl.MarkerDBGraphExporter;
import de.unibi.agbi.biodwh2.markerdb.etl.MarkerDBMappingDescriber;
import de.unibi.agbi.biodwh2.markerdb.etl.MarkerDBUpdater;

public class MarkerDBDataSource extends DataSource {
    @Override
    public String getId() {
        return "MarkerDB";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_NC_4_0.getName();
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new MarkerDBUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new MarkerDBGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new MarkerDBMappingDescriber(this);
    }
}
