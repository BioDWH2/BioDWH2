package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class AROOntologyDataSource extends SingleOBOOntologyDataSource {
    private static final String FILE_NAME = "aro.obo";
    private static final String ARCHIVE_FILE_NAME = "card-ontology.tar.bz2";

    @Override
    public String getId() {
        return "AROOntology";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    public String getIdPrefix() {
        return "ARO";
    }

    @Override
    protected String getTargetFileName() {
        return FILE_NAME;
    }

    @Override
    protected String getDownloadUrl() {
        return null;
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        return null;
    }

    @Override
    protected Updater<? extends AROOntologyDataSource> getUpdater() {
        return new AROUpdater(this);
    }

    private static final class AROUpdater extends Updater<AROOntologyDataSource> {
        private static final Pattern VERSION_PATTERN = Pattern.compile(
                "<td class=\"hidden-xs\">([0-9]{4}-[0-9]{2}-[0-9]{2}) [0-9]{2}:[0-9]{2}:[0-9]{2}",
                Pattern.CASE_INSENSITIVE);
        private static final String DOWNLOAD_URL = "https://card.mcmaster.ca/latest/ontology";
        private static final String VERSION_PAGE_URL = "https://card.mcmaster.ca/download";

        AROUpdater(final AROOntologyDataSource dataSource) {
            super(dataSource);
        }

        @Override
        public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
            final String source = getWebsiteSource(VERSION_PAGE_URL);
            if (source == null)
                return null;
            final Matcher matcher = VERSION_PATTERN.matcher(source);
            if (matcher.find()) {
                final String[] parts = StringUtils.split(matcher.group(1), '-');
                return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                                   Integer.parseInt(parts[2]));
            }
            return null;
        }

        @Override
        protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
            downloadFileAsBrowser(workspace, DOWNLOAD_URL, ARCHIVE_FILE_NAME);
            extractAROFromArchive(workspace);
            deleteArchive(workspace);
            return true;
        }

        private void extractAROFromArchive(final Workspace workspace) throws UpdaterException {
            final Path targetPath = dataSource.resolveSourceFilePath(workspace, FILE_NAME);
            try (InputStream fileStream = FileUtils.openInput(workspace, dataSource, ARCHIVE_FILE_NAME);
                 BufferedInputStream buffered = new BufferedInputStream(fileStream);
                 BZip2CompressorInputStream bzip2 = new BZip2CompressorInputStream(buffered);
                 TarArchiveInputStream tar = new TarArchiveInputStream(bzip2)) {
                ArchiveEntry entry;
                while ((entry = tar.getNextEntry()) != null) {
                    if (!entry.isDirectory() && entry.getName().endsWith("aro.obo")) {
                        try (OutputStream out = Files.newOutputStream(targetPath)) {
                            tar.transferTo(out);
                        }
                        return;
                    }
                }
            } catch (IOException e) {
                throw new UpdaterConnectionException("Failed to extract aro.obo from " + ARCHIVE_FILE_NAME, e);
            }
            throw new UpdaterConnectionException("aro.obo not found in " + ARCHIVE_FILE_NAME);
        }

        private void deleteArchive(final Workspace workspace) {
            try {
                Files.deleteIfExists(dataSource.resolveSourceFilePath(workspace, ARCHIVE_FILE_NAME));
            } catch (IOException ignored) {
            }
        }

        @Override
        protected String[] expectedFileNames() {
            return new String[]{FILE_NAME};
        }
    }
}