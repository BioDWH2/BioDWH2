package de.unibi.agbi.biodwh2;

import org.apache.commons.cli.*;

public final class BioDWH2 {
    public static void main(String[] args) throws ParseException {
        Options options = getCommandLineOptions();
        CommandLine commandLine = parseCommandLine(options, args);
        if (commandLine.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("BioDWH2", options);
        }
    }

    private static Options getCommandLineOptions() {
        Options options = new Options();
        options.addOption(new Option("h", "help", false, "print this message"));
        return options;
    }

    private static CommandLine parseCommandLine(Options options, String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }
}
