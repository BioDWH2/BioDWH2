package de.unibi.agbi.biodwh2.uniprot;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.uniprot.etl.UniProtParser;
import de.unibi.agbi.biodwh2.uniprot.etl.UniProtUpdater;

public class UniProtDataSource extends DataSource {
    @Override
    public String getId() {
        return "UniProt";
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
        return new UniProtParser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return null;
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return null;
    }

    @Override
    protected void unloadData() {
    }
}
