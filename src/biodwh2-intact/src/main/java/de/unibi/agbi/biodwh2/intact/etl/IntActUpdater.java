package de.unibi.agbi.biodwh2.intact.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPFTPClient;
import de.unibi.agbi.biodwh2.intact.IntActDataSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntActUpdater extends Updater<IntActDataSource> {
    private static final Pattern VERSION_DATE_PATTERN = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2})");
    private static final String SPECIES_DOWNLOAD_URL_PREFIX = "http://ftp.ebi.ac.uk/pub/databases/intact/current/psi30/species/";
    static final Map<Integer, String> SPECIES_TAX_ID_FILE_NAME = new HashMap<>();

    static {
        SPECIES_TAX_ID_FILE_NAME.put(9606, "human.zip");
        SPECIES_TAX_ID_FILE_NAME.put(559292, "yeast.zip");
        SPECIES_TAX_ID_FILE_NAME.put(7227, "drome.zip");
        SPECIES_TAX_ID_FILE_NAME.put(6239, "caeel.zip");
        SPECIES_TAX_ID_FILE_NAME.put(3702, "arath.zip");
        SPECIES_TAX_ID_FILE_NAME.put(10090, "mouse.zip");
        SPECIES_TAX_ID_FILE_NAME.put(83333, "ecoli.zip");
        SPECIES_TAX_ID_FILE_NAME.put(192222, "camje.zip");
        SPECIES_TAX_ID_FILE_NAME.put(10116, "rat.zip");
        SPECIES_TAX_ID_FILE_NAME.put(243276, "trepa.zip");
        SPECIES_TAX_ID_FILE_NAME.put(1111708, "syny3.zip");
        SPECIES_TAX_ID_FILE_NAME.put(36329, "plaf7.zip");
        SPECIES_TAX_ID_FILE_NAME.put(85962, "helpy.zip");
        SPECIES_TAX_ID_FILE_NAME.put(284812, "schpo.zip");
        SPECIES_TAX_ID_FILE_NAME.put(224308, "bacsu.zip");
        SPECIES_TAX_ID_FILE_NAME.put(2697049, "SARS-CoV-2.zip");
    }

    public IntActUpdater(final IntActDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            final var entries = new HTTPFTPClient("http://ftp.ebi.ac.uk/pub/databases/intact/").listDirectory();
            Version newestVersion = null;
            for (final var entry : entries) {
                final Matcher matcher = VERSION_DATE_PATTERN.matcher(entry.name);
                if (matcher.find()) {
                    Version v = Version.tryParse(matcher.group(1).replace('-', '.'));
                    if (v != null && (newestVersion == null || v.compareTo(newestVersion) > 0)) {
                        newestVersion = v;
                    }
                }
            }
            return newestVersion;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final var entry : SPECIES_TAX_ID_FILE_NAME.entrySet())
            if (speciesFilter.isSpeciesAllowed(entry.getKey()))
                downloadFileAsBrowser(workspace, SPECIES_DOWNLOAD_URL_PREFIX + entry.getValue(), entry.getValue());
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return SPECIES_TAX_ID_FILE_NAME.entrySet().stream().filter(e -> speciesFilter.isSpeciesAllowed(e.getKey())).map(
                Map.Entry::getValue).toArray(String[]::new);
    }
}
