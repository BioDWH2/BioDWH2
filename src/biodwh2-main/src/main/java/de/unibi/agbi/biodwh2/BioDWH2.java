package de.unibi.agbi.biodwh2;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourceLoader;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.text.TableFormatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

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
        cmd.parseArgs(args);
        return result;
    }

    private void run(final CmdArgs commandLine) {
        if (commandLine.listDataSources)
            listDataSources(commandLine);
        else if (commandLine.create != null)
            createWorkspace(commandLine);
        else if (commandLine.status != null)
            checkWorkspaceState(commandLine);
        else if (commandLine.update != null)
            updateWorkspace(commandLine.update, commandLine.skipUpdate);
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
            for (final DataSource dataSource : dataSources)
                rows.add(Arrays.asList(dataSource.getId(), dataSource.getFullName(), dataSource.getDescription()));
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Available data sources:");
            System.out.println(new TableFormatter(false).format(Arrays.asList("ID", "Name", "Description"), rows));
        } else if (LOGGER.isInfoEnabled())
            LOGGER.info("Available data source IDs: " + StringUtils.join(dataSourceIds, ", "));
    }

    private void createWorkspace(final CmdArgs commandLine) {
        final String workspacePath = commandLine.create;
        new Workspace(workspacePath);
    }

    private void checkWorkspaceState(final CmdArgs commandLine) {
        final String workspacePath = commandLine.status;
        final Workspace workspace = new Workspace(workspacePath);
        workspace.checkState(commandLine.verbose);
    }

    private void updateWorkspace(final List<String> updateParameters, final boolean skipUpdate) {
        final String workspacePath = updateParameters.get(0);
        final String dataSourceId = updateParameters.size() > 1 ? updateParameters.get(1) : null;
        final String version = updateParameters.size() > 2 ? updateParameters.get(2) : null;
        final Workspace workspace = new Workspace(workspacePath);
        workspace.processDataSources(dataSourceId, version, skipUpdate);
    }

    private void printHelp(final CmdArgs commandLine) {
        CommandLine.usage(commandLine, System.out);
    }
}
