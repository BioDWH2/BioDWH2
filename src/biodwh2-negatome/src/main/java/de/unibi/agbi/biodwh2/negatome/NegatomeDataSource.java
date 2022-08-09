package de.unibi.agbi.biodwh2.negatome;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.negatome.etl.NegatomeGraphExporter;
import de.unibi.agbi.biodwh2.negatome.etl.NegatomeMappingDescriber;
import de.unibi.agbi.biodwh2.negatome.etl.NegatomeParser;
import de.unibi.agbi.biodwh2.negatome.etl.NegatomeUpdater;
import de.unibi.agbi.biodwh2.negatome.model.ProteinPair;

import java.util.HashMap;
import java.util.Map;

public class NegatomeDataSource extends DataSource {
    public final Map<String, ProteinPair> pairs = new HashMap<>();

    @Override
    public String getId() {
        return "Negatome";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new NegatomeUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new NegatomeParser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new NegatomeGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new NegatomeMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        pairs.clear();
    }
}
