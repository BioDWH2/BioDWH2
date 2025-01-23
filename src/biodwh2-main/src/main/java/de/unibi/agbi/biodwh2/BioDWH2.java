package de.unibi.agbi.biodwh2;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourceLoader;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.ResourceUtils;
import de.unibi.agbi.biodwh2.core.net.BioDWH2Updater;
import de.unibi.agbi.biodwh2.core.text.TableFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class BioDWH2 {
    private static final Logger LOGGER = LogManager.getLogger(BioDWH2.class);

    private BioDWH2() {
    }

    public static void main(final String... args) {
        final CmdArgs commandLine = parseCommandLine(args);
        new BioDWH2().run(commandLine);
    }

    private static CmdArgs parseCommandLine(final String... args) {
        final CmdArgs result = new CmdArgs();
        final CommandLine cmd = new CommandLine(result);
        cmd.setPosixClusteredShortOptionsAllowed(true);
        cmd.parseArgs(args);
        return result;
    }

    private void run(final CmdArgs commandLine) {
        final var status = BioDWH2Updater.checkForUpdate("BioDWH2",
                                                         "https://api.github.com/repos/BioDWH2/BioDWH2/releases");
        BioDWH2Updater.logStatus(status);
        if (commandLine.listDataSources)
            listDataSources(commandLine);
        else if (commandLine.addDataSource != null)
            addDataSource(commandLine);
        else if (commandLine.removeDataSource != null)
            removeDataSource(commandLine);
        else if (commandLine.setConfig != null)
            setConfig(commandLine);
        else if (commandLine.create != null)
            createWorkspace(commandLine.create);
        else if (commandLine.configure)
            configureWorkspace(commandLine.webConfiguratorPort);
        else if (commandLine.status != null)
            checkWorkspaceState(commandLine.status, commandLine.verbose);
        else if (commandLine.update != null)
            updateWorkspace(commandLine);
        else if (commandLine.setDataSourceVersion != null)
            setDataSourceVersion(commandLine);
        else if (commandLine.version)
            printVersion();
        else
            printHelp(commandLine);
    }

    private void listDataSources(final CmdArgs commandLine) {
        final DataSourceLoader loader = DataSourceLoader.getInstance();
        final String[] dataSourceIds = Arrays.stream(loader.getDataSourceIds()).filter(id -> !id.startsWith("Mock"))
                                             .sorted().toArray(String[]::new);
        if (commandLine.verbose) {
            DataSource[] dataSources = loader.getDataSources(dataSourceIds);
            dataSources = Arrays.stream(dataSources).sorted(
                    Comparator.comparing(DataSource::getId, String::compareToIgnoreCase)).toArray(DataSource[]::new);
            final List<List<String>> rows = new ArrayList<>();
            for (final DataSource dataSource : dataSources) {
                final String availableProperties = String.join(", ", dataSource.getAvailableProperties().keySet()
                                                                               .toArray(new String[0]));
                rows.add(Arrays.asList(dataSource.getId(), dataSource.getLicense(),
                                       dataSource.getDevelopmentState().toString(), dataSource.getFullName(),
                                       availableProperties, dataSource.getDescription()));
            }
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Available data sources:");
            final TableFormatter formatter = new TableFormatter(false);
            System.out.println(
                    formatter.format(Arrays.asList("ID", "License", "State", "Name", "Properties", "Description"),
                                     rows));
        } else if (LOGGER.isInfoEnabled())
            LOGGER.info("Available data source IDs: {}", StringUtils.join(dataSourceIds, ", "));
    }

    private void addDataSource(final CmdArgs commandLine) {
        final String workspacePath = commandLine.addDataSource.get(0);
        final String dataSourceId = commandLine.addDataSource.get(1);
        final DataSourceLoader loader = DataSourceLoader.getInstance();
        final String[] matchedIds = Arrays.stream(loader.getDataSourceIds()).filter(
                id -> id.equalsIgnoreCase(dataSourceId)).toArray(String[]::new);
        if (matchedIds.length > 0) {
            final Workspace workspace = new Workspace(workspacePath);
            workspace.addDataSource(dataSourceId);
            try {
                workspace.saveConfiguration();
            } catch (IOException e) {
                LOGGER.error("Failed to add data source with id '{}'", dataSourceId, e);
            }
            LOGGER.info("Successfully added data source with id '{}'", dataSourceId);
        } else {
            LOGGER.error("Could not find data source with id '{}'", dataSourceId);
            listDataSources(commandLine);
        }
    }

    private void removeDataSource(final CmdArgs commandLine) {
        final String workspacePath = commandLine.removeDataSource.get(0);
        final String dataSourceId = commandLine.removeDataSource.get(1);
        final DataSourceLoader loader = DataSourceLoader.getInstance();
        final String[] matchedIds = Arrays.stream(loader.getDataSourceIds()).filter(
                id -> id.equalsIgnoreCase(dataSourceId)).toArray(String[]::new);
        if (matchedIds.length > 0) {
            final Workspace workspace = new Workspace(workspacePath);
            workspace.removeDataSource(dataSourceId);
            try {
                workspace.saveConfiguration();
            } catch (IOException e) {
                LOGGER.error("Failed to remove data source with id '{}'", dataSourceId, e);
            }
            LOGGER.info("Successfully removed data source with id '{}'", dataSourceId);
        } else {
            LOGGER.error("Could not find data source with id '{}'", dataSourceId);
            listDataSources(commandLine);
        }
    }

    private void setConfig(final CmdArgs commandLine) {
        final String workspacePath = commandLine.setConfig.get(0);
        String configKey = commandLine.setConfig.get(1);
        final String value = commandLine.setConfig.get(2);
        final var workspace = new Workspace(workspacePath);
        final var config = workspace.getConfiguration();
        final var loader = DataSourceLoader.getInstance();
        boolean success = true;
        try {
            if ("skipMetaGraphGeneration".equals(configKey)) {
                config.setSkipMetaGraphGeneration(CmdConfigPropertyParser.parseBoolean(value));
            } else if ("dataSourceIds".equals(configKey)) {
                final var ids = CmdConfigPropertyParser.parseStringList(value);
                if (ids != null) {
                    for (final var id : config.getDataSourceIds())
                        config.removeDataSource(id);
                    for (final var id : ids)
                        config.addDataSource(id);
                } else {
                    success = false;
                }
            } else if ("outputFormatIds".equals(configKey)) {
                final var ids = CmdConfigPropertyParser.parseStringList(value);
                if (ids != null) {
                    for (final var id : config.getOutputFormatIds())
                        config.removeOutputFormat(id);
                    for (final var id : ids)
                        config.addOutputFormat(id);
                } else {
                    success = false;
                }
            } else if (configKey.contains(".")) {
                final var domainEndIndex = configKey.indexOf('.');
                final var domain = configKey.substring(0, domainEndIndex);
                configKey = configKey.substring(domainEndIndex + 1);
                if ("globalProperties".equals(domain)) {
                    if ("speciesFilter".equals(configKey)) {
                        final var speciesFilter = CmdConfigPropertyParser.parseIntegerList(value);
                        if (speciesFilter != null) {
                            config.getGlobalProperties().speciesFilter = speciesFilter;
                        } else {
                            success = false;
                        }
                    } else {
                        success = false;
                    }
                } else if ("dataSourceProperties".equals(domain)) {
                    final var dataSourceEndIndex = configKey.indexOf('.');
                    if (dataSourceEndIndex == -1) {
                        success = false;
                    } else {
                        final var dataSourceId = configKey.substring(0, dataSourceEndIndex);
                        configKey = configKey.substring(dataSourceEndIndex + 1);
                        final var dataSource = loader.getDataSourceById(dataSourceId);
                        if (dataSource != null) {
                            final var propertyType = dataSource.getAvailableProperties().get(configKey);
                            if (propertyType != null) {
                                final var properties = config.getDataSourceProperties(dataSourceId);
                                final var parsedValue = CmdConfigPropertyParser.parse(value, propertyType);
                                if (parsedValue != null) {
                                    properties.put(configKey, parsedValue);
                                } else {
                                    success = false;
                                }
                            } else {
                                success = false;
                            }
                        } else {
                            success = false;
                        }
                    }
                } else {
                    success = false;
                }
            } else {
                success = false;
            }
            if (success) {
                workspace.saveConfiguration();
                LOGGER.info("Successfully set config key '{}' to '{}'", configKey, value);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to set config key '{}' to '{}'", configKey, value, e);
        }
        if (!success)
            LOGGER.error("Failed to set config key '{}' to '{}'", configKey, value);
    }

    private void createWorkspace(final String workspacePath) {
        new Workspace(workspacePath);
    }

    private void configureWorkspace(Integer webConfiguratorPort) {
        new WebConfigurator(webConfiguratorPort).run();
    }

    private void checkWorkspaceState(final String workspacePath, final boolean verbose) {
        final var workspace = new Workspace(workspacePath);
        workspace.checkState(verbose);
    }

    private void updateWorkspace(final CmdArgs commandLine) {
        final var workspace = new Workspace(commandLine.update);
        if (commandLine.runsInParallel)
            workspace.processDataSourcesInParallel(commandLine.skipUpdate, commandLine.numThreads);
        else
            workspace.processDataSources(commandLine.skipUpdate);
    }

    private void setDataSourceVersion(final CmdArgs commandLine) {
        final String workspacePath = commandLine.setDataSourceVersion.get(0);
        final String dataSourceId = commandLine.setDataSourceVersion.get(1);
        final String version = commandLine.setDataSourceVersion.get(2);
        final var workspace = new Workspace(workspacePath);
        workspace.setDataSourceVersion(dataSourceId, version);
    }

    private void printVersion() {
        LOGGER.info("Version " + ResourceUtils.getManifestBioDWH2Version());
    }

    private void printHelp(final CmdArgs commandLine) {
        CommandLine.usage(commandLine, System.out);
    }
}
