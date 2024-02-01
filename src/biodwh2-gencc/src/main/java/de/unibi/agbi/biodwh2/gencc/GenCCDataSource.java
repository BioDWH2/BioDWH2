package de.unibi.agbi.biodwh2.gencc;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.gencc.etl.GenCCGraphExporter;
import de.unibi.agbi.biodwh2.gencc.etl.GenCCMappingDescriber;
import de.unibi.agbi.biodwh2.gencc.etl.GenCCUpdater;

public class GenCCDataSource extends DataSource {
    @Override
    public String getId() {
        return "GenCC";
    }

    @Override
    public String getLicense() {
        return License.CC0_1_0.getName();
    }

    @Override
    public String getLicenseUrl() {
        return "https://thegencc.org/terms.html";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new GenCCUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new GenCCGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new GenCCMappingDescriber(this);
    }
}
