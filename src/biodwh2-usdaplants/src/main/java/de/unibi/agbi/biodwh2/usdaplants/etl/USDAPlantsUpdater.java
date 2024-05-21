package de.unibi.agbi.biodwh2.usdaplants.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.usdaplants.USDAPlantsDataSource;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class USDAPlantsUpdater extends Updater<USDAPlantsDataSource> {
    private static final String VERSION_URL = "https://plants.usda.gov/home/release-notes";
    private static final String DOWNLOAD_URL = "https://plants.usda.gov/assets/docs/CompletePLANTSList/plantlst.txt";
    private static final String MAIN_JS_URL_PREFIX = "https://plants.usda.gov/";
    static final String PLANT_LIST_FILE_NAME = "plantlst.txt";

    private static final Pattern MAIN_JS_PATTERN = Pattern.compile("src=\"(main\\.[a-f0-9A-F]+\\.js)\"");

    public USDAPlantsUpdater(final USDAPlantsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        var source = getWebsiteSource(VERSION_URL, 3);
        final Matcher mainJSMatcher = MAIN_JS_PATTERN.matcher(source);
        if (mainJSMatcher.find())
            source = getWebsiteSource(MAIN_JS_URL_PREFIX + mainJSMatcher.group(1));
        else
            throw new UpdaterMalformedVersionException("Failed to retrieve versions");
        var parts = StringUtils.splitByWholeSeparator(source, "[{version:\"", 2);
        parts = StringUtils.split(parts[1], "\"", 2);
        return Version.parse(parts[0].strip());
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL, PLANT_LIST_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{PLANT_LIST_FILE_NAME};
    }
}
