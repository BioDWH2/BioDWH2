package de.unibi.agbi.biodwh2.card.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.card.CARDDataSource;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CARDUpdater extends Updater<CARDDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "<td class=\"hidden-xs\">([0-9]{4}-[0-9]{2}-[0-9]{2}) [0-9]{2}:[0-9]{2}:[0-9]{2}", Pattern.CASE_INSENSITIVE);
    static final String FILE_NAME_DATA = "card-data.tar.bz2";
    private static final String DOWNLOAD_URL_DATA = "https://card.mcmaster.ca/latest/data";
    static final String FILE_NAME_ONTOLOGY = "card-ontology.tar.bz2";
    private static final String DOWNLOAD_URL_ONTOLOGY = "https://card.mcmaster.ca/latest/ontology";

    public CARDUpdater(final CARDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource("https://card.mcmaster.ca/download");
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find()) {
            final String[] parts = StringUtils.split(matcher.group(1), '-');
            return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_DATA, FILE_NAME_DATA);
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_ONTOLOGY, FILE_NAME_ONTOLOGY);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME_DATA,  FILE_NAME_ONTOLOGY};
    }
}
