package de.unibi.agbi.biodwh2.biom2metdisease.etl;

import de.unibi.agbi.biodwh2.biom2metdisease.BioM2MetDiseaseDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BioM2MetDiseaseUpdater extends Updater<BioM2MetDiseaseDataSource> {
    private static final String B2MD_MAIN_URL = "http://bio-bigdata.hrbmu.edu.cn/BioM2MetDisease/";
    static final String FILE_NAME = "BioM2MetDiseaseData.txt";
    private static final String B2MD_DOWNLOAD_URL =
            "http://bio-bigdata.hrbmu.edu.cn/BioM2MetDisease/resources/" + FILE_NAME;

    public BioM2MetDiseaseUpdater(final BioM2MetDiseaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(B2MD_MAIN_URL);
        final Pattern versionPattern = Pattern.compile("([a-zA-Z]{3} [0-9]{1,2}, [0-9]{4})");
        final Matcher matcher = versionPattern.matcher(source);
        Version newestVersion = null;
        while (matcher.find()) {
            final String[] dateParts = StringUtils.split(matcher.group(0), " ,()");
            final Version version = new Version(Integer.parseInt(dateParts[2]),
                                                TextUtils.threeLetterMonthNameToInt(dateParts[0].toLowerCase()),
                                                Integer.parseInt(dateParts[1]));
            if (newestVersion == null || newestVersion.compareTo(version) < 0)
                newestVersion = version;
        }
        return newestVersion;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, B2MD_DOWNLOAD_URL, FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
