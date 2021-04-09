package de.unibi.agbi.biodwh2.abdamed2;

import de.unibi.agbi.biodwh2.abdamed2.etl.ABDAMED2GraphExporter;
import de.unibi.agbi.biodwh2.abdamed2.etl.ABDAMED2MappingDescriber;
import de.unibi.agbi.biodwh2.abdamed2.etl.ABDAMED2Parser;
import de.unibi.agbi.biodwh2.abdamed2.etl.ABDAMED2Updater;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;

public class ABDAMED2DataSource extends DataSource {
    @Override
    public String getId() {
        return "ABDAMED2";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new ABDAMED2Updater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new ABDAMED2Parser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new ABDAMED2GraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new ABDAMED2MappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
