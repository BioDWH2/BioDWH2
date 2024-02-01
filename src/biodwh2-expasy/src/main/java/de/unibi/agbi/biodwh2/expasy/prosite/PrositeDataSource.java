package de.unibi.agbi.biodwh2.expasy.prosite;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.expasy.prosite.etl.PrositeGraphExporter;
import de.unibi.agbi.biodwh2.expasy.prosite.etl.PrositeMappingDescriber;
import de.unibi.agbi.biodwh2.expasy.prosite.etl.PrositeUpdater;

public class PrositeDataSource extends DataSource {
    @Override
    public String getId() {
        return "PROSITE";
    }

    @Override
    public String getFullName() {
        return "Expasy PROSITE";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_NC_ND_4_0.getName();
    }

    @Override
    public String getLicenseUrl() {
        return "https://prosite.expasy.org/prosite_license.html";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new PrositeUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new PrositeGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new PrositeMappingDescriber(this);
    }
}
