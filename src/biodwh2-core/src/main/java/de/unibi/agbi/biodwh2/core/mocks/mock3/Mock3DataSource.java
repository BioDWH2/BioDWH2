package de.unibi.agbi.biodwh2.core.mocks.mock3;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.mocks.MockParser;
import de.unibi.agbi.biodwh2.core.mocks.MockUpdater;
import de.unibi.agbi.biodwh2.core.mocks.mock3.etl.Mock3GraphExporter;
import de.unibi.agbi.biodwh2.core.mocks.mock3.etl.Mock3MappingDescriber;

public class Mock3DataSource extends DataSource {
    @Override
    public String getId() {
        return "Mock3";
    }

    @Override
    public Updater<Mock3DataSource> getUpdater() {
        return new MockUpdater<>(this);
    }

    @Override
    public Parser<Mock3DataSource> getParser() {
        return new MockParser<>(this);
    }

    @Override
    public GraphExporter<Mock3DataSource> getGraphExporter() {
        return new Mock3GraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new Mock3MappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
