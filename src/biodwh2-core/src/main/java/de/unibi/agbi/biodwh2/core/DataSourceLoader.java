package de.unibi.agbi.biodwh2.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class DataSourceLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceLoader.class);

    private final List<DataSource> dataSources;

    public DataSourceLoader() {
        dataSources = new ArrayList<>();
        final List<Class<DataSource>> allDataSourceClasses = Factory.getInstance().getImplementations(DataSource.class);
        for (final Class<DataSource> dataSourceClass : allDataSourceClasses) {
            final DataSource dataSource = tryInstantiateDataSource(dataSourceClass);
            if (dataSource != null)
                dataSources.add(dataSource);
        }
    }

    private DataSource tryInstantiateDataSource(final Class<DataSource> dataSourceClass) {
        if (Modifier.isAbstract(dataSourceClass.getModifiers()))
            return null;
        try {
            return dataSourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to instantiate data source '" + dataSourceClass.getName() + "'", e);
        }
        return null;
    }

    public DataSource[] getDataSources(final String... dataSourceIds) {
        return Arrays.stream(dataSourceIds).map(this::getDataSourceById).filter(Objects::nonNull).toArray(
                DataSource[]::new);
    }

    private DataSource getDataSourceById(final String id) {
        for (final DataSource dataSource : dataSources)
            if (dataSource.getId().equals(id))
                return dataSource;
        if (LOGGER.isWarnEnabled())
            LOGGER.warn("Failed to retrieve data source with id '" + id + "'");
        return null;
    }

    public String[] getDataSourceIds() {
        return dataSources.stream().map(DataSource::getId).toArray(String[]::new);
    }
}
