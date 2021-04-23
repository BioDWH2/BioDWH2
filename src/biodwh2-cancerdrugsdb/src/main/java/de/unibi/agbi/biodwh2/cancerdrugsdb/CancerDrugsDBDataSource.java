package de.unibi.agbi.biodwh2.cancerdrugsdb;

import de.unibi.agbi.biodwh2.cancerdrugsdb.etl.CancerDrugsDBGraphExporter;
import de.unibi.agbi.biodwh2.cancerdrugsdb.etl.CancerDrugsDBMappingDescriber;
import de.unibi.agbi.biodwh2.cancerdrugsdb.etl.CancerDrugsDBParser;
import de.unibi.agbi.biodwh2.cancerdrugsdb.etl.CancerDrugsDBUpdater;
import de.unibi.agbi.biodwh2.cancerdrugsdb.model.Entry;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;

import java.util.List;

public class CancerDrugsDBDataSource extends DataSource {
    public List<Entry> entries;

    @Override
    public String getId() {
        return "CancerDrugsDB";
    }

    @Override
    public String getFullName() {
        return "A curated listing of licensed cancer drugs by the Anticancer Fund";
    }

    @Override
    public String getDescription() {
        return "CancerDrugs_DB is a curated listing of licensed cancer drugs produced by the Anticancer Fund. " +
               "Source data comes from the NCI, FDA, EMA and other data sources.";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new CancerDrugsDBUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new CancerDrugsDBParser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new CancerDrugsDBGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new CancerDrugsDBMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        if (entries != null)
            entries.clear();
    }
}
