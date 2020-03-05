package de.unibi.agbi.biodwh2;

import de.unibi.agbi.biodwh2.core.Workspace;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.List;

public final class BioDWH2 {
    public static void main(final String[] args) throws Exception {
        CmdArgs commandLine = parseCommandLine(args);
        if (commandLine.create != null)
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
        String dataSourceName = optionArguments.size() > 1 ? optionArguments.get(1) : null;
        String version = optionArguments.size() > 2 ? optionArguments.get(2) : null;
        Workspace workspace = new Workspace(workspacePath);
        workspace.updateDataSources(dataSourceName, version, commandLine.skipUpdate);
    }

    private static void printHelp(final CmdArgs commandLine) {
        CommandLine.usage(commandLine, System.out);
    }
}
