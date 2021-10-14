package de.unibi.agbi.biodwh2.core.mocks.mock1;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.mocks.MockParser;
import de.unibi.agbi.biodwh2.core.mocks.MockUpdater;
import de.unibi.agbi.biodwh2.core.mocks.mock1.etl.Mock1GraphExporter;
import de.unibi.agbi.biodwh2.core.mocks.mock1.etl.Mock1MappingDescriber;

public final class Mock1DataSource extends DataSource {

    @Override
    public String getId() {
        return "Mock1";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    public Updater<Mock1DataSource> getUpdater() {
        return new MockUpdater<>(this);
    }

    @Override
    public Parser<Mock1DataSource> getParser() {
        return new MockParser<>(this);
    }

    @Override
    public GraphExporter<Mock1DataSource> getGraphExporter() {
        return new Mock1GraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new Mock1MappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
