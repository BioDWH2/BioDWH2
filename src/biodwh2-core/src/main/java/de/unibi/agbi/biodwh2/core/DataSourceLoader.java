package de.unibi.agbi.biodwh2.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public final class DataSourceLoader {
    private static final Logger LOGGER = LogManager.getLogger(DataSourceLoader.class);
    private static DataSourceLoader instance;

    private final List<DataSource> dataSources;

    private DataSourceLoader() {
        dataSources = new ArrayList<>();
        final List<Class<DataSource>> allDataSourceClasses = Factory.getInstance().getImplementations(DataSource.class);
        for (final Class<DataSource> dataSourceClass : allDataSourceClasses) {
            final DataSource dataSource = tryInstantiateDataSource(dataSourceClass);
            if (dataSource != null)
                dataSources.add(dataSource);
        }
    }

    public static DataSourceLoader getInstance() {
        if (instance == null)
            instance = new DataSourceLoader();
        return instance;
    }

    private static DataSource tryInstantiateDataSource(final Class<DataSource> dataSourceClass) {
        if (Modifier.isAbstract(dataSourceClass.getModifiers()))
            return null;
        try {
            return dataSourceClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to instantiate data source '" + dataSourceClass.getName() + "'", e);
        }
        return null;
    }

    public DataSource[] getDataSources() {
        return dataSources.toArray(DataSource[]::new);
    }

    public DataSource[] getDataSources(final String... dataSourceIds) {
        final Set<String> failedDataSourceIds = new HashSet<>();
        final Map<String, DataSource> result = new HashMap<>();
        for (final String id : dataSourceIds)
            result.put(id, null);
        final Set<String> remainingIds = new HashSet<>(result.keySet());
        while (!remainingIds.isEmpty()) {
            for (final String id : remainingIds) {
                final DataSource dataSource = getDataSourceById(id);
                if (dataSource != null) {
                    result.put(id, dataSource);
                    final String[] dependencyIds = dataSource.getDependencies();
                    if (dependencyIds != null) {
                        for (final String dependencyId : dependencyIds) {
                            if (!result.containsKey(dependencyId)) {
                                result.put(dependencyId, null);
                            }
                        }
                    }
                } else {
                    failedDataSourceIds.add(id);
                }
            }
            remainingIds.clear();
            remainingIds.addAll(
                    result.keySet().stream().filter(k -> result.get(k) == null).collect(Collectors.toSet()));
            remainingIds.removeAll(failedDataSourceIds);
        }
        final List<DataSource> sortedResult = new ArrayList<>(result.values());
        sortedResult.sort(this::dataSourceOrderComparator);
        return sortedResult.toArray(new DataSource[0]);
    }

    private DataSource getDataSourceById(final String id) {
        for (final DataSource dataSource : dataSources)
            if (dataSource.getId().equals(id))
                return dataSource;
        if (LOGGER.isWarnEnabled())
            LOGGER.warn("Failed to retrieve data source with id '" + id + "'");
        return null;
    }

    /**
     * We order the data sources by being an ontology data source first and alphabetically by id secondly.
     */
    private int dataSourceOrderComparator(DataSource a, DataSource b) {
        final boolean aIsOntology = a instanceof OntologyDataSource;
        final boolean bIsOntology = b instanceof OntologyDataSource;
        if (aIsOntology && bIsOntology) {
            return a.getId().compareTo(b.getId());
        } else if (aIsOntology) {
            return -1;
        } else if (bIsOntology) {
            return 1;
        }
        return a.getId().toLowerCase(Locale.ROOT).compareTo(b.getId().toLowerCase(Locale.ROOT));
    }

    public String[] getDataSourceIds() {
        return dataSources.stream().map(DataSource::getId).toArray(String[]::new);
    }
}
