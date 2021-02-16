package de.unibi.agbi.biodwh2.mondo;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.mondo.etl.MondoGraphExporter;
import de.unibi.agbi.biodwh2.mondo.etl.MondoMappingDescriber;
import de.unibi.agbi.biodwh2.mondo.etl.MondoParser;
import de.unibi.agbi.biodwh2.mondo.etl.MondoUpdater;

public class MondoDataSource extends DataSource {
    @Override
    public String getId() {
        return "Mondo";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    public Updater<MondoDataSource> getUpdater() {
        return new MondoUpdater(this);
    }

    @Override
    protected Parser<MondoDataSource> getParser() {
        return new MondoParser(this);
    }

    @Override
    protected GraphExporter<MondoDataSource> getGraphExporter() {
        return new MondoGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new MondoMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
