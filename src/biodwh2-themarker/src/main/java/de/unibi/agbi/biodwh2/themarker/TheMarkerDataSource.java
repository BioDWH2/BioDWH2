package de.unibi.agbi.biodwh2.themarker;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.themarker.etl.TheMarkerGraphExporter;
import de.unibi.agbi.biodwh2.themarker.etl.TheMarkerMappingDescriber;
import de.unibi.agbi.biodwh2.themarker.etl.TheMarkerUpdater;

public class TheMarkerDataSource extends DataSource {
    @Override
    public String getId() {
        return "TheMarker";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new TheMarkerUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new TheMarkerGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new TheMarkerMappingDescriber(this);
    }
}
