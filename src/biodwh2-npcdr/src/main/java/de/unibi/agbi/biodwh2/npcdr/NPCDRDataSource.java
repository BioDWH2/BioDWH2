package de.unibi.agbi.biodwh2.npcdr;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.npcdr.etl.NPCDRGraphExporter;
import de.unibi.agbi.biodwh2.npcdr.etl.NPCDRMappingDescriber;
import de.unibi.agbi.biodwh2.npcdr.etl.NPCDRUpdater;

public class NPCDRDataSource extends DataSource {
    @Override
    public String getId() {
        return "NPCDR";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new NPCDRUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new NPCDRGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new NPCDRMappingDescriber(this);
    }
}
