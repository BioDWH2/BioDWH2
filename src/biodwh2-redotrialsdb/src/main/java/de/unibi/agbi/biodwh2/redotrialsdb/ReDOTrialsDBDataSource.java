package de.unibi.agbi.biodwh2.redotrialsdb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.redotrialsdb.etl.ReDOTrialsDBParser;
import de.unibi.agbi.biodwh2.redotrialsdb.etl.ReDOTrialsDBUpdater;
import de.unibi.agbi.biodwh2.redotrialsdb.etl.ReDOTrialsDBGraphExporter;
import de.unibi.agbi.biodwh2.redotrialsdb.etl.ReDOTrialsDBMappingDescriber;
import de.unibi.agbi.biodwh2.redotrialsdb.model.Entry;

import java.util.List;

public class ReDOTrialsDBDataSource extends DataSource {
    public List<Entry> entries;

    @Override
    public String getId() {
        return "ReDOTrialsDB";
    }

    @Override
    public String getFullName() {
        return "A curated database, produced by the Anticancer Fund, of active clinical trials investigating " +
               "the use of non-cancer drugs as potential cancer treatments";
    }

    @Override
    public String getDescription() {
        return "A curated database of active clinical trials investigating the use of non-cancer drugs as potential  " +
               "cancer treatments. All trials in this database include one or more licensed non-cancer drugs " +
               "in an intervention arm as an anticancer agent rather than for symptom control or supportive care. " +
               "Produced by the Anticancer Fund";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new ReDOTrialsDBUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new ReDOTrialsDBParser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new ReDOTrialsDBGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new ReDOTrialsDBMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        if (entries != null)
            entries.clear();
    }
}
