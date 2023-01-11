package de.unibi.agbi.biodwh2.refseq;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.refseq.etl.RefSeqGraphExporter;
import de.unibi.agbi.biodwh2.refseq.etl.RefSeqMappingDescriber;
import de.unibi.agbi.biodwh2.refseq.etl.RefSeqUpdater;

import java.util.Map;

public class RefSeqDataSource extends DataSource {
    @Override
    public String getId() {
        return "RefSeq";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new RefSeqUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new RefSeqGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new RefSeqMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final Map<String, DataSourcePropertyType> result = super.getAvailableProperties();
        result.put("assembly", DataSourcePropertyType.STRING);
        return result;
    }
}
