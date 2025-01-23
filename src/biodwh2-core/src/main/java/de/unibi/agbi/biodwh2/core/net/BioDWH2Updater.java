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

    public static Status checkForUpdate(final String toolRepositoryName, final String githubReleasesUrl) {
        final Version currentVersion = ResourceUtils.getManifestBioDWH2Version();
        Version latestVersion = null;
        String latestDownloadUrl = null;
        GithubRelease latestRelease = null;
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final URL releaseUrl = new URL(githubReleasesUrl);
            final List<GithubRelease> releases = mapper.readValue(releaseUrl, new TypeReference<>() {
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
        final var isUpToDate = currentVersion != null && (latestVersion == null || currentVersion.compareTo(
                latestVersion) < 0);
        return new Status(toolRepositoryName, isUpToDate, currentVersion, latestVersion, latestDownloadUrl,
                          latestRelease != null && StringUtils.isNotEmpty(latestRelease.body) ?
                          StringUtils.replace(latestRelease.body, "```", "") : null);
    }

    public static void logStatus(final Status status) {
        if (status.currentVersion == null && status.latestVersion != null ||
            status.currentVersion != null && status.currentVersion.compareTo(status.latestVersion) < 0) {
            LOGGER.info("=======================================");
            LOGGER.info("New version {} of {} is available at:", status.latestVersion, status.name);
            LOGGER.info(status.latestDownloadUrl);
            if (StringUtils.isNotEmpty(status.changelog))
                LOGGER.info("Description: {}", status.changelog);
            LOGGER.info("=======================================");
        }
    }

    public static class Status {
        public final String name;
        public final boolean isUpToDate;
        public final Version currentVersion;
        public final Version latestVersion;
        public final String latestDownloadUrl;
        public final String changelog;

        public Status(final String name, final boolean isUpToDate, final Version currentVersion,
                      final Version latestVersion, final String latestDownloadUrl, final String changelog) {
            this.name = name;
            this.isUpToDate = isUpToDate;
            this.currentVersion = currentVersion;
            this.latestVersion = latestVersion;
            this.latestDownloadUrl = latestDownloadUrl;
            this.changelog = changelog;
        }
    }
}
