package de.unibi.agbi.biodwh2.core.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.model.github.GithubRelease;

import java.io.IOException;
import java.net.URL;

public class GithubUtils {
    public static GithubRelease getLatestRelease(final String username, final String repository) {
        try {
            final URL releaseUrl = new URL(
                    "https://api.github.com/repos/" + username + "/" + repository + "/releases/latest");
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(releaseUrl, GithubRelease.class);
        } catch (IOException ignored) {
        }
        return null;
    }
}
