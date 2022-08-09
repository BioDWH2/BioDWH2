package de.unibi.agbi.biodwh2;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourceLoader;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.ResourceUtils;
import de.unibi.agbi.biodwh2.core.net.BioDWH2Updater;
import de.unibi.agbi.biodwh2.core.text.TableFormatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BioDWH2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(BioDWH2.class);

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
        BioDWH2Updater.checkForUpdate("BioDWH2", "https://api.github.com/repos/BioDWH2/BioDWH2/releases");
        if (commandLine.listDataSources)
            listDataSources(commandLine);
        else if (commandLine.addDataSource != null)
            addDataSource(commandLine);
        else if (commandLine.removeDataSource != null)
            removeDataSource(commandLine);
        else if (commandLine.create != null)
            createWorkspace(commandLine.create);
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
        final DataSourceLoader loader = new DataSourceLoader();
        final String[] dataSourceIds = Arrays.stream(loader.getDataSourceIds()).filter(id -> !id.startsWith("Mock"))
                                             .sorted().toArray(String[]::new);
        if (commandLine.verbose) {
            final DataSource[] dataSources = loader.getDataSources(dataSourceIds);
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
            LOGGER.info("Available data source IDs: " + StringUtils.join(dataSourceIds, ", "));
    }

    private void addDataSource(final CmdArgs commandLine) {
        final String workspacePath = commandLine.addDataSource.get(0);
        final String dataSourceId = commandLine.addDataSource.get(1);
        final DataSourceLoader loader = new DataSourceLoader();
        final String[] matchedIds = Arrays.stream(loader.getDataSourceIds()).filter(
                id -> id.equalsIgnoreCase(dataSourceId)).toArray(String[]::new);
        if (matchedIds.length > 0) {
            final Workspace workspace = new Workspace(workspacePath);
            workspace.addDataSource(dataSourceId);
            try {
                workspace.saveConfiguration();
            } catch (IOException e) {
                LOGGER.error("Failed to add data source with id '" + dataSourceId + "'", e);
            }
            LOGGER.info("Successfully added data source with id '" + dataSourceId + "'");
        } else {
            LOGGER.error("Could not find data source with id '" + dataSourceId + "'");
            listDataSources(commandLine);
        }
    }

    private void removeDataSource(final CmdArgs commandLine) {
        final String workspacePath = commandLine.removeDataSource.get(0);
        final String dataSourceId = commandLine.removeDataSource.get(1);
        final DataSourceLoader loader = new DataSourceLoader();
        final String[] matchedIds = Arrays.stream(loader.getDataSourceIds()).filter(
                id -> id.equalsIgnoreCase(dataSourceId)).toArray(String[]::new);
        if (matchedIds.length > 0) {
            final Workspace workspace = new Workspace(workspacePath);
            workspace.removeDataSource(dataSourceId);
            try {
                workspace.saveConfiguration();
            } catch (IOException e) {
                LOGGER.error("Failed to remove data source with id '" + dataSourceId + "'", e);
            }
            LOGGER.info("Successfully removed data source with id '" + dataSourceId + "'");
        } else {
            LOGGER.error("Could not find data source with id '" + dataSourceId + "'");
            listDataSources(commandLine);
        }
    }

    private void createWorkspace(final String workspacePath) {
        new Workspace(workspacePath);
    }

    private void checkWorkspaceState(final String workspacePath, final boolean verbose) {
        final Workspace workspace = new Workspace(workspacePath);
        workspace.checkState(verbose);
    }

    private void updateWorkspace(final CmdArgs commandLine) {
        final Workspace workspace = new Workspace(commandLine.update);
        if (commandLine.runsInParallel)
            workspace.processDataSourcesInParallel(commandLine.skipUpdate, commandLine.numThreads);
        else
            workspace.processDataSources(commandLine.skipUpdate);
    }

    private void setDataSourceVersion(final CmdArgs commandLine) {
        final String workspacePath = commandLine.setDataSourceVersion.get(0);
        final String dataSourceId = commandLine.setDataSourceVersion.get(1);
        final String version = commandLine.setDataSourceVersion.get(2);
        final Workspace workspace = new Workspace(workspacePath);
        workspace.setDataSourceVersion(dataSourceId, version);
    }

    private void printVersion() {
        LOGGER.info("Version " + ResourceUtils.getManifestBioDWH2Version());
    }

    private void printHelp(final CmdArgs commandLine) {
        CommandLine.usage(commandLine, System.out);
    }
}
