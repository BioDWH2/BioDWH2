package de.unibi.agbi.biodwh2.brenda;

import de.unibi.agbi.biodwh2.brenda.etl.BrendaGraphExporter;
import de.unibi.agbi.biodwh2.brenda.etl.BrendaMappingDescriber;
import de.unibi.agbi.biodwh2.brenda.etl.BrendaUpdater;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.text.License;

import java.util.Map;

public class BrendaDataSource extends DataSource {
    public static final String LICENSE_ACCEPTED_PROPERTY_KEY = "licenseAccepted";

    @Override
    public String getId() {
        return "BRENDA";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    public String getLicenseUrl() {
        return "https://www.brenda-enzymes.org/license.php";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new BrendaUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new BrendaGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new BrendaMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final Map<String, DataSourcePropertyType> result = super.getAvailableProperties();
        result.put(LICENSE_ACCEPTED_PROPERTY_KEY, DataSourcePropertyType.BOOLEAN);
        return result;
    }
}
