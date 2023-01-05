package de.unibi.agbi.biodwh2.core.net;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.io.ResourceUtils;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.model.github.GithubAsset;
import de.unibi.agbi.biodwh2.core.model.github.GithubRelease;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public final class BioDWH2Updater {
    private static final Logger LOGGER = LogManager.getLogger(BioDWH2Updater.class);

    private BioDWH2Updater() {
    }

    public static void checkForUpdate(final String toolRepositoryName, final String githubReleasesUrl) {
        final Version currentVersion = ResourceUtils.getManifestBioDWH2Version();
        Version latestVersion = null;
        String latestDownloadUrl = null;
        GithubRelease latestRelease = null;
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final URL releaseUrl = new URL(githubReleasesUrl);
            final List<GithubRelease> releases = mapper.readValue(releaseUrl, new TypeReference<List<GithubRelease>>() {
            });
            for (final GithubRelease release : releases) {
                final Version version = Version.tryParse(release.tagName.replace("v", ""));
                if (version != null) {
                    final String jarName = toolRepositoryName + '-' + release.tagName + ".jar";
                    final Optional<GithubAsset> jarAsset = release.assets.stream().filter(
                            asset -> asset.name.equalsIgnoreCase(jarName)).findFirst();
                    if (jarAsset.isPresent() && (latestVersion == null || version.compareTo(latestVersion) > 0)) {
                        latestVersion = version;
                        latestRelease = release;
                        latestDownloadUrl = jarAsset.get().browserDownloadUrl;
                    }
                }
            }
        } catch (IOException | ClassCastException ignored) {
        }
        if (currentVersion == null && latestVersion != null || currentVersion != null && currentVersion.compareTo(
                latestVersion) < 0) {
            LOGGER.info("=======================================");
            LOGGER.info("New version " + latestVersion + " of " + toolRepositoryName + " is available at:");
            LOGGER.info(latestDownloadUrl);
            if (latestRelease != null && StringUtils.isNotEmpty(latestRelease.body))
                LOGGER.info("Description: " + latestRelease.body);
            LOGGER.info("=======================================");
        }
    }
}
