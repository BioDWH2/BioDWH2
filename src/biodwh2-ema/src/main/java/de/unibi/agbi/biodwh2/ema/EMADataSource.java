package de.unibi.agbi.biodwh2.ema;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.ema.etl.EMAGraphExporter;
import de.unibi.agbi.biodwh2.ema.etl.EMAMappingDescriber;
import de.unibi.agbi.biodwh2.ema.etl.EMAParser;
import de.unibi.agbi.biodwh2.ema.etl.EMAUpdater;
import de.unibi.agbi.biodwh2.ema.model.EPAREntry;
import de.unibi.agbi.biodwh2.ema.model.HMPCEntry;

import java.util.List;

public class EMADataSource extends DataSource {
    public List<EPAREntry> EPAREntries;
    public List<HMPCEntry> HMPCEntries;

    @Override
    public String getId() {
        return "EMA";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new EMAUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new EMAParser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new EMAGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new EMAMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        EPAREntries = null;
        HMPCEntries = null;
    }
}
