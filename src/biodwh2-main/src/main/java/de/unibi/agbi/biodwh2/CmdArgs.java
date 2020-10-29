package de.unibi.agbi.biodwh2;

import picocli.CommandLine;

import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
@CommandLine.Command(name = "BioDWH2.jar")
public class CmdArgs {
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "print this message")
    public boolean help;
    @CommandLine.Option(names = {"-ds", "--data-sources"}, description = "List all available data sources")
    public boolean listDataSources;
    @CommandLine.Option(names = {
            "-c", "--create"
    }, arity = "1", paramLabel = "<workspacePath>", description = "Create a new empty workspace")
    public String create;
    @CommandLine.Option(names = {
            "-s", "--status"
    }, arity = "1", paramLabel = "<workspacePath>", description = "Check and output the state of a workspace")
    public String status;
    @CommandLine.Option(names = {
            "-u", "--update"
    }, arity = "1..3", description = "Update all data sources of a workspace")
    public List<String> update;
    @CommandLine.Option(names = {"-su", "--skip-update"}, description = "Skip update, only parse and export")
    public boolean skipUpdate;
    @CommandLine.Option(names = {
            "-v", "--verbose"
    }, description = "Output detailed information about the state of the workspace")
    public boolean verbose;
}
