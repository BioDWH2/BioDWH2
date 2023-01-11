package de.unibi.agbi.biodwh2.uniprot;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.uniprot.etl.UniProtGraphExporter;
import de.unibi.agbi.biodwh2.uniprot.etl.UniProtMappingDescriber;
import de.unibi.agbi.biodwh2.uniprot.etl.UniProtUpdater;

public class UniProtDataSource extends DataSource {
    @Override
    public String getId() {
        return "UniProt";
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
        return new UniProtUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new UniProtGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new UniProtMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
