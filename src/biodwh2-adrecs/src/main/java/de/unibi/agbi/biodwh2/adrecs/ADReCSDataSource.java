package de.unibi.agbi.biodwh2.adrecs;

import de.unibi.agbi.biodwh2.adrecs.etl.ADReCSGraphExporter;
import de.unibi.agbi.biodwh2.adrecs.etl.ADReCSMappingDescriber;
import de.unibi.agbi.biodwh2.adrecs.etl.ADReCSUpdater;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.text.License;

public class ADReCSDataSource extends DataSource {
    @Override
    public String getId() {
        return "ADReCS";
    }

    @Override
    public String getFullName() {
        return "Adverse Drug Reaction Classification System (ADReCS)";
    }

    @Override
    public String getWebsite() {
        return "https://bioinf.xmu.edu.cn/ADReCS/";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_NC_SA_4_0.getName();
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new ADReCSUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new ADReCSGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new ADReCSMappingDescriber(this);
    }
}
