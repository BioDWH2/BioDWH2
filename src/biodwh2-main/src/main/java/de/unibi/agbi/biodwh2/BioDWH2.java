package de.unibi.agbi.biodwh2;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourceLoader;
import de.unibi.agbi.biodwh2.core.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class BioDWH2 {
    private static final Logger logger = LoggerFactory.getLogger(BioDWH2.class);

    public static void main(final String[] args) throws Exception {
        CmdArgs commandLine = parseCommandLine(args);
        if (commandLine.listDataSources)
            listDataSources(commandLine);
        else if (commandLine.create != null)
            createWorkspace(commandLine);
        else if (commandLine.status != null)
            checkWorkspaceState(commandLine);
        else if (commandLine.update != null)
            updateWorkspace(commandLine);
        else
            printHelp(commandLine);
    }

    private static CmdArgs parseCommandLine(String[] args) {
        CmdArgs result = new CmdArgs();
        CommandLine cmd = new CommandLine(result);
        cmd.parseArgs(args);
        return result;
    }

    private static void listDataSources(final CmdArgs commandLine) throws Exception {
        DataSourceLoader loader = new DataSourceLoader();
        String dataSourceIds = Arrays.stream(loader.getDataSources()).map(DataSource::getId).collect(
                Collectors.joining(", "));
        logger.info("Available data source IDs: " + dataSourceIds);
    }

    private static void createWorkspace(final CmdArgs commandLine) throws Exception {
        String workspacePath = commandLine.create;
        new Workspace(workspacePath);
    }

    private static void checkWorkspaceState(final CmdArgs commandLine) throws Exception {
        String workspacePath = commandLine.status;
        Workspace workspace = new Workspace(workspacePath);
        workspace.checkState(commandLine.verbose);
    }

    private static void updateWorkspace(final CmdArgs commandLine) throws Exception {
        List<String> optionArguments = commandLine.update;
        String workspacePath = optionArguments.get(0);
        String dataSourceId = optionArguments.size() > 1 ? optionArguments.get(1) : null;
        String version = optionArguments.size() > 2 ? optionArguments.get(2) : null;
        Workspace workspace = new Workspace(workspacePath);
        workspace.processDataSources(dataSourceId, version, commandLine.skipUpdate);
    }

    private static void printHelp(final CmdArgs commandLine) {
        CommandLine.usage(commandLine, System.out);
    }
}
