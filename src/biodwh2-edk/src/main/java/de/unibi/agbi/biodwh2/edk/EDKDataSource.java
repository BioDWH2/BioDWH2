package de.unibi.agbi.biodwh2.edk;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.edk.etl.EDKGraphExporter;
import de.unibi.agbi.biodwh2.edk.etl.EDKMappingDescriber;
import de.unibi.agbi.biodwh2.edk.etl.EDKUpdater;

public class EDKDataSource extends DataSource {
    @Override
    public String getId() {
        return "EDK";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new EDKUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new EDKGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new EDKMappingDescriber(this);
    }
}
