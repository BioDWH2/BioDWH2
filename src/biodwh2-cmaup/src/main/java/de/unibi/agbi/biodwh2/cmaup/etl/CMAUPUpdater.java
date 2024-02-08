package de.unibi.agbi.biodwh2.cmaup.etl;

import de.unibi.agbi.biodwh2.cmaup.CMAUPDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMAUPUpdater extends Updater<CMAUPDataSource> {
    private static final Pattern VERSION_PATTERN = Pattern.compile("CMAUPv([0-9]+\\.[0-9]+)_");
    static final String PLANTS_FILE_NAME = "Plants.txt";
    static final String INGREDIENTS_ONLY_ACTIVE_FILE_NAME = "Ingredients_onlyActive.txt";
    static final String TARGETS_FILE_NAME = "Targets.txt";
    static final String PLANT_INGREDIENT_ASSOCIATIONS_ONLY_ACTIVE_FILE_NAME = "Plant_Ingredient_Associations_onlyActiveIngredients.txt";
    static final String INGREDIENT_TARGET_ASSOCIATIONS_FILE_NAME = "Ingredient_Target_Associations_ActivityValues_References.txt";

    private String lastVersion;

    public CMAUPUpdater(final CMAUPDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource("http://bidd.group/CMAUP/download.html", 3);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find()) {
            lastVersion = matcher.group(1);
            return Version.tryParse(matcher.group(1));
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final String url = "http://bidd.group/CMAUP/downloadFiles/CMAUPv" + lastVersion + "_download_";
        for (final String fileName : expectedFileNames())
            downloadFileAsBrowser(workspace, url + fileName, fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                PLANTS_FILE_NAME, INGREDIENTS_ONLY_ACTIVE_FILE_NAME, TARGETS_FILE_NAME,
                PLANT_INGREDIENT_ASSOCIATIONS_ONLY_ACTIVE_FILE_NAME, INGREDIENT_TARGET_ASSOCIATIONS_FILE_NAME
        };
    }
}
