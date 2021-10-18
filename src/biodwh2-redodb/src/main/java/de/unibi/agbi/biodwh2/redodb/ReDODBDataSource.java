package de.unibi.agbi.biodwh2.redodb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.redodb.etl.ReDODBGraphExporter;
import de.unibi.agbi.biodwh2.redodb.etl.ReDODBMappingDescriber;
import de.unibi.agbi.biodwh2.redodb.etl.ReDODBParser;
import de.unibi.agbi.biodwh2.redodb.etl.ReDODBUpdater;
import de.unibi.agbi.biodwh2.redodb.model.Entry;

import java.util.List;

public class ReDODBDataSource extends DataSource {
    public List<Entry> entries;

    @Override
    public String getId() {
        return "ReDO-DB";
    }

    @Override
    public String getFullName() {
        return "ReDO_DB";
    }

    @Override
    public String getDescription() {
        return "ReDO_DB is a curated listing of non-cancer drugs which have shown some evidence of anticancer " +
               "activity produced by the Anticancer Fund.";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new ReDODBUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new ReDODBParser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new ReDODBGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new ReDODBMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        if (entries != null)
            entries.clear();
    }
}
