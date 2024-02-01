package de.unibi.agbi.biodwh2.trrust;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.trrust.etl.TRRUSTGraphExporter;
import de.unibi.agbi.biodwh2.trrust.etl.TRRUSTMappingDescriber;
import de.unibi.agbi.biodwh2.trrust.etl.TRRUSTUpdater;

public class TRRUSTDataSource extends DataSource {
    @Override
    public String getId() {
        return "TRRUST";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_SA_4_0.getName();
    }

    @Override
    public String getFullName() {
        return "Transcriptional Regulatory Relationships Unraveled by Sentence-based Text mining (TRRUST)";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new TRRUSTUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new TRRUSTGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new TRRUSTMappingDescriber(this);
    }
}
