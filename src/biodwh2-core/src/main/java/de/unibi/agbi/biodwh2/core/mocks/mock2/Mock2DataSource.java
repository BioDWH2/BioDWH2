package de.unibi.agbi.biodwh2.core.mocks.mock2;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.mocks.MockParser;
import de.unibi.agbi.biodwh2.core.mocks.MockUpdater;
import de.unibi.agbi.biodwh2.core.mocks.mock2.etl.Mock2GraphExporter;
import de.unibi.agbi.biodwh2.core.mocks.mock2.etl.Mock2MappingDescriber;

public class Mock2DataSource extends DataSource {
    @Override
    public String getId() {
        return "Mock2";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    public Updater<Mock2DataSource> getUpdater() {
        return new MockUpdater<>(this);
    }

    @Override
    public Parser<Mock2DataSource> getParser() {
        return new MockParser<>(this);
    }

    @Override
    public GraphExporter<Mock2DataSource> getGraphExporter() {
        return new Mock2GraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new Mock2MappingDescriber(this);
    }
}
