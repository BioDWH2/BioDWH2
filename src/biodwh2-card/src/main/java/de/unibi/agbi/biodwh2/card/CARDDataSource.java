package de.unibi.agbi.biodwh2.card;


import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.card.etl.CARDGraphExporter;
import de.unibi.agbi.biodwh2.card.etl.CARDParser;
import de.unibi.agbi.biodwh2.card.etl.CARDMappingDescriber;
import de.unibi.agbi.biodwh2.card.etl.CARDUpdater;
import de.unibi.agbi.biodwh2.card.model.AROTerm;
import de.unibi.agbi.biodwh2.card.model.Entry;


import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

public class CARDDataSource extends DataSource {
    public List<Entry> model_entries;
    public Map<String, AROTerm> aroTerms = new LinkedHashMap<>();

    @Override
    public String getId() {
        return "CARD";
    }

    @Override
    public String getFullName() {
        return "CARD_DB";
    }

    @Override
    public String getDescription() {
        return "CARD_DB is a bioinformatic database of resistance genes, their products and associated phenotypes.";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new CARDUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new CARDParser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new CARDGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new CARDMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        if (model_entries != null)
            model_entries.clear();
        if (aroTerms != null)
            aroTerms.clear();
    }
}
