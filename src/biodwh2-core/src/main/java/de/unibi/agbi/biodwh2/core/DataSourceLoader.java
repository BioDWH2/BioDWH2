package de.unibi.agbi.biodwh2.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class DataSourceLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceLoader.class);

    private final List<DataSource> dataSources;

    public DataSourceLoader() {
        dataSources = new ArrayList<>();
        final List<Class<DataSource>> availableDataSourceClasses = Factory.getInstance().getImplementations(DataSource.class);
        for (Class<DataSource> dataSourceClass : availableDataSourceClasses) {
            final DataSource dataSource = tryInstantiateDataSource(dataSourceClass);
            if (dataSource != null)
                dataSources.add(dataSource);
        }
    }

    private DataSource tryInstantiateDataSource(final Class<DataSource> dataSourceClass) {
        try {
            return dataSourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to instantiate data source '" + dataSourceClass.getName() + "'", e);
        }
        return null;
    }

    public DataSource[] getDataSources() {
        return dataSources.toArray(new DataSource[0]);
    }
}
