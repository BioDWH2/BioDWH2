package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterOnlyManuallyException;
import de.unibi.agbi.biodwh2.core.model.Configuration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Workspace {
    private static final Logger logger = LoggerFactory.getLogger(Workspace.class);

    public static final int Version = 1;
    private static final String SourcesDirectory = "sources";

    private final String workingDirectory;
    private final Configuration configuration;
    private final List<DataSource> dataSources;

    public Workspace(String workingDirectory) throws IOException {
        this.workingDirectory = workingDirectory;
        createWorkingDirectoryIfNotExists();
        configuration = createOrLoadConfiguration();
        dataSources = resolveUsedDataSources();
    }

    private void createWorkingDirectoryIfNotExists() throws IOException {
        Files.createDirectories(Paths.get(workingDirectory));
        Files.createDirectories(Paths.get(getSourcesDirectory()));
    }

    String getSourcesDirectory() {
        return Paths.get(workingDirectory, SourcesDirectory).toString();
    }

    private Configuration createOrLoadConfiguration() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Path path = Paths.get(workingDirectory, "config.json");
        if (Files.exists(path))
            return objectMapper.readValue(path.toFile(), Configuration.class);
        Configuration configuration = new Configuration();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(path.toFile(), configuration);
        return configuration;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    private List<DataSource> resolveUsedDataSources() {
        List<DataSource> dataSources = new ArrayList<>();
        List<Class<DataSource>> availableDataSourceClasses = Factory.getInstance().getImplementations(DataSource.class);
        for (Class<DataSource> dataSourceClass : availableDataSourceClasses) {
            DataSource dataSource = tryInstantiateDataSource(dataSourceClass);
            if (dataSource != null && isDataSourceUsed(dataSource))
                dataSources.add(dataSource);
        }
        return dataSources;
    }

    private DataSource tryInstantiateDataSource(Class<DataSource> dataSourceClass) {
        try {
            return dataSourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Failed to instantiate data source '" + dataSourceClass.getName() + "'", e);
        }
        return null;
    }

    private boolean isDataSourceUsed(DataSource dataSource) {
        return configuration.dataSourceIds.contains(dataSource.getId());
    }

    public void checkState() throws Exception {
        ensureDataSourceDirectoriesExist();
        createOrLoadDataSourcesMetadata();
        logger.error("Status intent is not yet implemented", new Exception("Not implemented"));
    }

    private void ensureDataSourceDirectoriesExist() {
        for (DataSource dataSource : dataSources) {
            try {
                dataSource.createDirectoryIfNotExists(this);
            } catch (IOException e) {
                logger.error("Failed to create data source directory for '" + dataSource.getId() + "'", e);
            }
        }
    }

    private void createOrLoadDataSourcesMetadata() {
        for (DataSource dataSource : dataSources) {
            try {
                dataSource.createOrLoadMetadata(this);
            } catch (IOException e) {
                logger.error("Failed to load data source metadata for '" + dataSource.getId() + "'", e);
            }
        }
    }

    public void updateDataSources() {
        ensureDataSourceDirectoriesExist();
        createOrLoadDataSourcesMetadata();
        for (DataSource dataSource : dataSources) {
            logger.info("Processing of data source '" + dataSource.getId() + "' started");
            try {
                boolean updated = dataSource.getUpdater().update(this, dataSource);
                System.out.println("\tupdated: " + updated);
            } catch (UpdaterOnlyManuallyException e) {
                logger.error("Data source '" + dataSource.getId() + "' can only be updated manually");
            } catch (UpdaterException e) {
                logger.error("Failed to update data source '" + dataSource.getId() + "'", e);
            }
            try {
                boolean parsed = dataSource.getParser().parse(this, dataSource);
                System.out.println("\tparsed: " + parsed);
            } catch (ParserException e) {
                logger.error("Failed to parse data source '" + dataSource.getId() + "'", e);
            }
            boolean exported = dataSource.getRdfExporter().export(this, dataSource);
            System.out.println("\texported: " + exported);
            exported = dataSource.getGraphExporter().export(this, dataSource);
            System.out.println("\texported: " + exported);
            logger.info("Processing of data source '" + dataSource.getId() + "' finished");
        }
    }
}
