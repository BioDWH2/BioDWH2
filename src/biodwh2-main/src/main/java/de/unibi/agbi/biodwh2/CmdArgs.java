package de.unibi.agbi.biodwh2;

import picocli.CommandLine;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
@CommandLine.Command(name = "BioDWH2.jar", sortOptions = false, separator = " ", footer = "Visit https://biodwh2.github.io for more documentation.")
public class CmdArgs {
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Print this message", order = 0)
    public boolean help;
    @CommandLine.Option(names = {
            "--version"
    }, description = "Print the BioDWH2 version and check for updates", order = 1)
    public boolean version;
    @CommandLine.Option(names = {
            "-c", "--create"
    }, arity = "1", paramLabel = "<workspacePath>", description = "Create a new empty workspace", order = 10)
    public String create;
    @CommandLine.Option(names = {"--data-sources"}, description = "List all available data sources", order = 11)
    public boolean listDataSources;
    @CommandLine.Option(names = {
            "--add-data-source"
    }, arity = "2", paramLabel = "<workspacePath> <dataSourceId>", hideParamSyntax = true, description = "Add a data source to the configuration", order = 12)
    public List<String> addDataSource;
    @CommandLine.Option(names = {
            "--remove-data-source"
    }, arity = "2", paramLabel = "<workspacePath> <dataSourceId>", hideParamSyntax = true, description = "Remove a data source from the configuration", order = 13)
    public List<String> removeDataSource;
    @CommandLine.Option(names = {
            "-u", "--update"
    }, arity = "1", paramLabel = "<workspacePath>", hideParamSyntax = true, description = "Update all data sources of a workspace", order = 14)
    public String update;
    @CommandLine.Option(names = {
            "-s", "--status"
    }, arity = "1", paramLabel = "<workspacePath>", description = "Check and output the state of a workspace", order = 15)
    public String status;
    @CommandLine.Option(names = {
            "--set-data-source-version"
    }, arity = "3", paramLabel = "<workspacePath> <dataSourceId> <version>", hideParamSyntax = true, description = "Set the data source to a specific version for manually updated modules", order = 16)
    public List<String> setDataSourceVersion;
    @CommandLine.Option(names = {"--skip-update"}, description = "Skip update, only parse and export", order = 100)
    public boolean skipUpdate;
    @CommandLine.Option(names = {
            "-v", "--verbose"
    }, description = "Output detailed information about the state of the workspace", order = 101)
    public boolean verbose;

    @CommandLine.Option(names = {"-p", "--parallel"}, description = "Run parallelizable pipeline steps in parallel", order = 2)
    public boolean runsInParallel;

    @CommandLine.Option(names = {"-t", "--threads"}, description = "Number of threads used for parallelism", order = 2)
    public int numThreads = Runtime.getRuntime().availableProcessors();

}
