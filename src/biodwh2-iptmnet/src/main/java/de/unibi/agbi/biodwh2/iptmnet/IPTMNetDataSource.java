package de.unibi.agbi.biodwh2.iptmnet;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.iptmnet.etl.IPTMNetGraphExporter;
import de.unibi.agbi.biodwh2.iptmnet.etl.IPTMNetMappingDescriber;
import de.unibi.agbi.biodwh2.iptmnet.etl.IPTMNetUpdater;

public class IPTMNetDataSource extends DataSource {
    @Override
    public String getId() {
        return "iPTMnet";
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
        return new IPTMNetUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new IPTMNetGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new IPTMNetMappingDescriber(this);
    }
}
