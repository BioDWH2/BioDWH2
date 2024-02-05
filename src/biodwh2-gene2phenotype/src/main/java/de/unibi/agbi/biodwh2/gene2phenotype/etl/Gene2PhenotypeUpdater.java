package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.gene2phenotype.Gene2PhenotypeDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gene2PhenotypeUpdater extends Updater<Gene2PhenotypeDataSource> {
    private static final String G2P_MAIN_URL = "https://www.ebi.ac.uk/gene2phenotype";
    private static final String G2P_DOWNLOAD_URL = "https://www.ebi.ac.uk/gene2phenotype/downloads/";
    static final String[] FILE_NAMES = new String[]{
            "CancerG2P.csv.gz", "CardiacG2P.csv.gz", "DDG2P.csv.gz", "EyeG2P.csv.gz", "SkinG2P.csv.gz"
    };

    public Gene2PhenotypeUpdater(final Gene2PhenotypeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(G2P_MAIN_URL);
        final Pattern versionPattern = Pattern.compile("<strong>([0-9]{4}-[0-9]{2}-[0-9]{2})</strong>");
        final Matcher matcher = versionPattern.matcher(source);
        Version newestVersion = null;
        while (matcher.find()) {
            final String[] dateParts = StringUtils.split(matcher.group(1), '-');
            final Version version = new Version(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
                                                Integer.parseInt(dateParts[2]));
            if (newestVersion == null || newestVersion.compareTo(version) < 0)
                newestVersion = version;
        }
        return newestVersion;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : FILE_NAMES)
            downloadFileAsBrowser(workspace, G2P_DOWNLOAD_URL + fileName, fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return FILE_NAMES;
    }
}
