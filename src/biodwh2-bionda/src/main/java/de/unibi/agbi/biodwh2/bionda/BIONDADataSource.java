package de.unibi.agbi.biodwh2.bionda;

import de.unibi.agbi.biodwh2.bionda.etl.BIONDAGraphExporter;
import de.unibi.agbi.biodwh2.bionda.etl.BIONDAMappingDescriber;
import de.unibi.agbi.biodwh2.bionda.etl.BIONDAUpdater;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.text.License;

public class BIONDADataSource extends DataSource {
    @Override
    public String getId() {
        return "BIONDA";
    }

    @Override
    public String getLicense() {
        return License.BSD_3_CLAUSE.getName();
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new BIONDAUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new BIONDAGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new BIONDAMappingDescriber(this);
    }
}
