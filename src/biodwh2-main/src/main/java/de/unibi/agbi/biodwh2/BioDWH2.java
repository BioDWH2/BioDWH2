package de.unibi.agbi.biodwh2;

import de.unibi.agbi.biodwh2.core.Workspace;
import org.apache.commons.cli.*;

public final class BioDWH2 {
    public static void main(String[] args) throws Exception {
        Options options = getCommandLineOptions();
        CommandLine commandLine = parseCommandLine(options, args);
        if (commandLine.hasOption("c"))
            createWorkspace(commandLine);
        else if (commandLine.hasOption("s"))
            checkWorkspaceState(commandLine);
        else if (commandLine.hasOption("u"))
            updateWorkspace(commandLine);
        else
            printHelp(options);
    }

    private static Options getCommandLineOptions() {
        Options options = new Options();
        options.addOption(new Option("h", "help", false, "print this message"));
        options.addOption(new Option("c", "create", true, "Create a new empty workspace"));
        options.addOption(new Option("s", "status", true, "Check and output the state of a workspace"));
        options.addOption(new Option("u", "update", true, "Update all data sources of a workspace"));
        return options;
    }

    private static CommandLine parseCommandLine(Options options, String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    private static void createWorkspace(CommandLine commandLine) throws Exception {
        String workspacePath = commandLine.getOptionValue("c");
        Workspace workspace = new Workspace(workspacePath);
    }

    private static void checkWorkspaceState(CommandLine commandLine) throws Exception {
        String workspacePath = commandLine.getOptionValue("s");
        Workspace workspace = new Workspace(workspacePath);
        workspace.checkState();
    }

    private static void updateWorkspace(CommandLine commandLine) throws Exception {
        String workspacePath = commandLine.getOptionValue("u");
        Workspace workspace = new Workspace(workspacePath);
        workspace.updateDataSources();
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("BioDWH2", options);
    }
}
