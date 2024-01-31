package de.unibi.agbi.biodwh2.chebi;

import de.unibi.agbi.biodwh2.chebi.etl.ChEBIGraphExporter;
import de.unibi.agbi.biodwh2.chebi.etl.ChEBIMappingDescriber;
import de.unibi.agbi.biodwh2.chebi.etl.ChEBIUpdater;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.text.License;

public class ChEBIDataSource extends DataSource {
    @Override
    public String getId() {
        return "ChEBI";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new ChEBIUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new ChEBIGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new ChEBIMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
