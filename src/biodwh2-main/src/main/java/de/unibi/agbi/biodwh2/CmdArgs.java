package de.unibi.agbi.biodwh2;

import picocli.CommandLine;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
@CommandLine.Command(name = "BioDWH2.jar", sortOptions = false, separator = " ", footer = "Visit https://biodwh2.github.io for more documentation.")
public class CmdArgs {
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Print this message", order = 0)
    public boolean help;
    @CommandLine.Option(names = {"-ui"}, description = "Start BioDWH2 in UI mode", order = 1)
    public boolean ui;
    @CommandLine.Option(names = {
            "-c", "--create"
    }, arity = "1", paramLabel = "<workspacePath>", description = "Create a new empty workspace", order = 2)
    public String create;
    @CommandLine.Option(names = {"--data-sources"}, description = "List all available data sources", order = 3)
    public boolean listDataSources;
    @CommandLine.Option(names = {
            "--add-data-source"
    }, arity = "2", paramLabel = "<workspacePath> <dataSourceId>", hideParamSyntax = true, description = "Add a data source to the configuration", order = 4)
    public List<String> addDataSource;
    @CommandLine.Option(names = {
            "--remove-data-source"
    }, arity = "2", paramLabel = "<workspacePath> <dataSourceId>", hideParamSyntax = true, description = "Remove a data source from the configuration", order = 5)
    public List<String> removeDataSource;
    @CommandLine.Option(names = {
            "-u", "--update"
    }, arity = "1..3", paramLabel = "<workspacePath> [<dataSourceId> <version>]", hideParamSyntax = true, description = "Update all data sources of a workspace", order = 6)
    public List<String> update;
    @CommandLine.Option(names = {
            "-s", "--status"
    }, arity = "1", paramLabel = "<workspacePath>", description = "Check and output the state of a workspace", order = 7)
    public String status;
    @CommandLine.Option(names = {"--skip-update"}, description = "Skip update, only parse and export", order = 100)
    public boolean skipUpdate;
    @CommandLine.Option(names = {
            "-v", "--verbose"
    }, description = "Output detailed information about the state of the workspace", order = 101)
    public boolean verbose;
}
