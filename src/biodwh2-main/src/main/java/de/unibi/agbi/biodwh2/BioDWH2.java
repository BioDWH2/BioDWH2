package de.unibi.agbi.biodwh2;

import de.unibi.agbi.biodwh2.core.Workspace;
import org.apache.commons.cli.*;

public final class BioDWH2 {
    public static void main(final String[] args) throws Exception {
        Options options = getCommandLineOptions();
        CommandLine commandLine = parseCommandLine(options, args);
        if (commandLine.hasOption("c"))
            createWorkspace(commandLine);
        else if (commandLine.hasOption("s"))
            checkWorkspaceState(commandLine);
        else if (commandLine.hasOption("u"))
            updateWorkspace(commandLine);
        else if (commandLine.hasOption("i"))
            integrateWorkspace(commandLine);
        else
            printHelp(options);
    }

    private static Options getCommandLineOptions() {
        Options options = new Options();
        options.addOption(new Option("h", "help", false, "print this message"));
        options.addOption(new Option("c", "create", true, "Create a new empty workspace"));
        options.addOption(new Option("s", "status", true, "Check and output the state of a workspace"));
        options.addOption(
                new Option("v", "verbose", false, "Output detailed information about the state of the workspace"));
        options.addOption(new Option("u", "update", true, "Update all data sources of a workspace"));
        Option integrateOption = new Option("i", "integrate", true,
                                            "Integrates manually downloaded data sources to a workspace");
        integrateOption.setArgs(3);
        options.addOption(integrateOption);
        return options;
    }

    private static CommandLine parseCommandLine(Options options, String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    private static void createWorkspace(final CommandLine commandLine) throws Exception {
        String workspacePath = commandLine.getOptionValue("c");
        new Workspace(workspacePath);
    }

    private static void checkWorkspaceState(final CommandLine commandLine) throws Exception {
        String workspacePath = commandLine.getOptionValue("s");
        Workspace workspace = new Workspace(workspacePath);
        boolean verbose = commandLine.hasOption("v");
        workspace.checkState(verbose);
    }

    private static void updateWorkspace(final CommandLine commandLine) throws Exception {
        String workspacePath = commandLine.getOptionValue("u");
        Workspace workspace = new Workspace(workspacePath);
        workspace.updateDataSources(null, null);
    }

    private static void integrateWorkspace(final CommandLine commandLine) throws Exception {
        String[] optionArguments = commandLine.getOptionValues("i");
        String workspacePath = optionArguments[0];
        String dataSourceName = optionArguments[1];
        String version = optionArguments[2];
        if (dataSourceName == null || version == null)
            throw new Exception(
                    "The integrate command needs three non-empty arguments. data source name is '" + dataSourceName +
                    "' and version '" + version + "'");
        Workspace workspace = new Workspace(workspacePath);
        workspace.updateDataSources(dataSourceName, version);
    }

    private static void printHelp(final Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("BioDWH2", options);
    }
}
