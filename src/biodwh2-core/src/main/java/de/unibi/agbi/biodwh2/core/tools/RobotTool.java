package de.unibi.agbi.biodwh2.core.tools;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.GithubUtils;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

public final class RobotTool {
    private static final Logger LOGGER = LogManager.getLogger(RobotTool.class);

    private RobotTool() {
    }

    public static boolean updateIfNecessary(final Workspace workspace) {
        var robotDirectory = getRobotDirectory(workspace);
        if (!robotDirectory.toFile().exists())
            robotDirectory.toFile().mkdirs();
        var files = robotDirectory.toFile().listFiles();
        final String[] existingFileNames;
        if (files != null)
            existingFileNames = Arrays.stream(files).map(File::getName).filter(f -> f.endsWith(".jar")).toArray(
                    String[]::new);
        else
            existingFileNames = new String[0];
        final var release = GithubUtils.getLatestRelease("ontodev", "robot");
        if (release == null)
            return existingFileNames.length > 0;
        var asset = release.assets.stream().filter(a -> a.name.endsWith(".jar")).findFirst();
        if (asset.isEmpty())
            return existingFileNames.length > 0;
        try {
            HTTPClient.downloadFileAsBrowser("https://raw.githubusercontent.com/ontodev/robot/master/LICENSE.txt",
                                             robotDirectory.resolve("LICENSE.txt").toString());
        } catch (IOException ignored) {
        }
        final var outputFileName = "robot-" + release.tagName + ".jar";
        final var outputFilePath = robotDirectory.resolve(outputFileName).toString();
        if (ArrayUtils.contains(existingFileNames, outputFileName))
            return true;
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Updating ROBOT to version {}", release.tagName);
        try {
            HTTPClient.downloadFileAsBrowser(asset.get().browserDownloadUrl, outputFilePath);
        } catch (IOException ignored) {
            return existingFileNames.length > 0;
        }
        for (final var fileName : existingFileNames)
            robotDirectory.resolve(fileName).toFile().delete();
        return true;
    }

    private static Path getRobotDirectory(final Workspace workspace) {
        return workspace.getToolsDirectory().resolve("ROBOT");
    }

    private static Optional<Path> getToolFilePath(final Workspace workspace) {
        var robotDirectory = getRobotDirectory(workspace);
        var files = robotDirectory.toFile().listFiles();
        if (files == null)
            return Optional.empty();
        return Arrays.stream(files).map(File::getAbsolutePath).filter(f -> f.endsWith(".jar")).map(Paths::get)
                     .findFirst();
    }

    public static boolean convertToOBO(final Workspace workspace, final DataSource dataSource,
                                       final String sourceFileName, final String targetFileName) {
        final var toolFilePath = getToolFilePath(workspace);
        if (toolFilePath.isEmpty())
            return false;
        final var runtime = Runtime.getRuntime();
        try {
            final var process = runtime.exec(new String[]{
                    "java", "-jar", toolFilePath.toString(), "convert", "--input", dataSource.resolveSourceFilePath(
                    workspace, sourceFileName).toString(), "--output", dataSource.resolveSourceFilePath(workspace,
                                                                                                        targetFileName).toString()
            });
            // TODO stderr, stdout
            process.waitFor();
            var exitCode = process.exitValue();
            if (exitCode != 0)
                LOGGER.error("Failed to convert '{}' to '{}': Exit code {}", sourceFileName, targetFileName, exitCode);
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Failed to convert '{}' to '{}': {}", sourceFileName, targetFileName, e.getMessage());
        }
        // TODO
        return true;
    }
}
