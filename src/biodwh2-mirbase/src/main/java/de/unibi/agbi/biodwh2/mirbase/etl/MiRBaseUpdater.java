package de.unibi.agbi.biodwh2.mirbase.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.mirbase.MiRBaseDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class MiRBaseUpdater extends MultiFileFTPWebUpdater<MiRBaseDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("RELEASE\\s+(\\d+)\\.(\\d+)");

    public MiRBaseUpdater(final MiRBaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(Workspace workspace) throws UpdaterException {
        final var source = getWebsiteSource("https://mirbase.org/download/CURRENT/");
        final var matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find())
            return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        if (!super.tryUpdateFiles(workspace))
            return false;
        for (final String fileName : expectedFileNames()) {
            final var filePath = dataSource.resolveSourceFilePath(workspace, fileName);
            try {
                String text = Files.readString(filePath, StandardCharsets.UTF_8);
                if (text.startsWith("<p>")) {
                    text = StringUtils.replace(text, "<p>", "");
                    text = StringUtils.replace(text, "</p>", "");
                    text = StringUtils.replace(text, "<br>", "\n");
                    text = StringUtils.replace(text, "&quot;", "\"");
                    text = StringUtils.replace(text, "&#34;", "\"");
                    text = StringUtils.replace(text, "&#x27;", "'");
                    text = StringUtils.replace(text, "&apos;", "'");
                    text = StringUtils.replace(text, "&#39;", "'");
                    text = StringUtils.replace(text, "&lt;", "<");
                    text = StringUtils.replace(text, "&gt;", ">");
                    text = StringUtils.replace(text, "&nbsp;", " ");
                    text = StringUtils.replace(text, "&amp;", "&");
                    Files.writeString(filePath, text, StandardCharsets.UTF_8);
                }
            } catch (IOException e) {
                throw new UpdaterException("Failed to post-process file '" + fileName + "'", e);
            }
        }
        return true;
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://mirbase.org/download/CURRENT/";
    }

    @Override
    protected String[] getFilePaths(final Workspace workspace) {
        return new String[]{
                "hairpin.fa", "mature.fa", "miRNA.dat", "miRNA.str", "database_files/mirna_species.txt",
                "database_files/mirna.txt", "database_files/confidence_score.txt",
                "database_files/mirna_chromosome_build.txt", "database_files/mirna_database_url.txt",
                "database_files/mirna_database_links.txt", "database_files/mirna_mature.txt",
                "database_files/mature_database_url.txt", "database_files/mature_database_links.txt",
                "database_files/mirna_pre_mature.txt", "database_files/mirna_2_prefam.txt",
                "database_files/mirna_prefam.txt", "database_files/confidence.txt", "database_files/mirna_context.txt"
        };
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                "hairpin.fa", "mature.fa", "miRNA.dat", "miRNA.str", "mirna_species.txt", "mirna.txt",
                "confidence_score.txt", "mirna_chromosome_build.txt", "mirna_database_url.txt",
                "mirna_database_links.txt", "mirna_mature.txt", "mature_database_url.txt", "mature_database_links.txt",
                "mirna_pre_mature.txt", "mirna_2_prefam.txt", "mirna_prefam.txt", "confidence.txt", "mirna_context.txt"
        };
    }
}
