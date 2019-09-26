package de.unibi.agbi.biodwh2.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.unibi.agbi.biodwh2.core.model.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Workspace {
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
            e.printStackTrace();
        }
        return null;
    }

    private boolean isDataSourceUsed(DataSource dataSource) {
        return configuration.dataSourceIds.contains(dataSource.getId());
    }

    public void checkState() throws Exception {
        ensureDataSourceDirectoriesExist();
        createOrLoadDataSourcesMetadata();
        throw new Exception("Not implemented");
    }

    private void ensureDataSourceDirectoriesExist() {
        for (DataSource dataSource : dataSources) {
            try {
                dataSource.createDirectoryIfNotExists(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createOrLoadDataSourcesMetadata() {
        for (DataSource dataSource : dataSources) {
            try {
                dataSource.createOrLoadMetadata(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateDataSources() {
        ensureDataSourceDirectoriesExist();
        createOrLoadDataSourcesMetadata();
        for (DataSource dataSource : dataSources) {
            System.out.println(dataSource.getId());
            boolean updated = dataSource.getUpdater().update(this, dataSource);
            System.out.println("\tupdated: " + updated);
            boolean parsed = dataSource.getParser().parse(this, dataSource);
            System.out.println("\tparsed: " + parsed);
            boolean exported = dataSource.getRdfExporter().export(this, dataSource);
            System.out.println("\texported: " + exported);
        }
    }
}
