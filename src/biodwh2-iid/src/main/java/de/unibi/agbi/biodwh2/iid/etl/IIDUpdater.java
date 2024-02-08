package de.unibi.agbi.biodwh2.iid.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.iid.IIDDataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IIDUpdater extends Updater<IIDDataSource> {
    private static final String VERSION_URL = "https://iid.ophid.utoronto.ca/static/Search_By_Proteins.css";
    private static final String DOWNLOAD_URL_PREFIX = "https://iid.ophid.utoronto.ca/static/download/";
    static final String ALPACA_PPI_FILE_NAME = "alpaca_annotated_PPIs.txt.gz";
    static final String CAT_PPI_FILE_NAME = "cat_annotated_PPIs.txt.gz";
    static final String CHICKEN_PPI_FILE_NAME = "chicken_annotated_PPIs.txt.gz";
    static final String COW_PPI_FILE_NAME = "cow_annotated_PPIs.txt.gz";
    static final String DOG_PPI_FILE_NAME = "dog_annotated_PPIs.txt.gz";
    static final String DUCK_PPI_FILE_NAME = "duck_annotated_PPIs.txt.gz";
    static final String FLY_PPI_FILE_NAME = "fly_annotated_PPIs.txt.gz";
    static final String GUINEA_PIG_PPI_FILE_NAME = "guinea_pig_annotated_PPIs.txt.gz";
    static final String HORSE_PPI_FILE_NAME = "horse_annotated_PPIs.txt.gz";
    static final String HUMAN_PPI_FILE_NAME = "human_annotated_PPIs.txt.gz";
    static final String MOUSE_PPI_FILE_NAME = "mouse_annotated_PPIs.txt.gz";
    static final String PIG_PPI_FILE_NAME = "pig_annotated_PPIs.txt.gz";
    static final String RABBIT_PPI_FILE_NAME = "rabbit_annotated_PPIs.txt.gz";
    static final String RAT_PPI_FILE_NAME = "rat_annotated_PPIs.txt.gz";
    static final String SHEEP_PPI_FILE_NAME = "sheep_annotated_PPIs.txt.gz";
    static final String TURKEY_PPI_FILE_NAME = "turkey_annotated_PPIs.txt.gz";
    static final String WORM_PPI_FILE_NAME = "worm_annotated_PPIs.txt.gz";
    static final String YEAST_PPI_FILE_NAME = "yeast_annotated_PPIs.txt.gz";
    // static final String HUMAN_TISSUE_EXPRESSION_FILE_NAME = "human.uniprot2tissues.txt.gz";
    private static final Pattern VERSION_PATTERN = Pattern.compile("content:\\s*\"version ([0-9]{4})-([0-3]?[0-9])");

    public IIDUpdater(final IIDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String source = getWebsiteSource(VERSION_URL);
        final Matcher matcher = VERSION_PATTERN.matcher(source);
        if (matcher.find())
            return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), 0);
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : expectedFileNames())
            downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + fileName, fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                ALPACA_PPI_FILE_NAME, CAT_PPI_FILE_NAME, CHICKEN_PPI_FILE_NAME, COW_PPI_FILE_NAME, DOG_PPI_FILE_NAME,
                DUCK_PPI_FILE_NAME, FLY_PPI_FILE_NAME, GUINEA_PIG_PPI_FILE_NAME, HORSE_PPI_FILE_NAME,
                HUMAN_PPI_FILE_NAME, MOUSE_PPI_FILE_NAME, PIG_PPI_FILE_NAME, RABBIT_PPI_FILE_NAME, RAT_PPI_FILE_NAME,
                SHEEP_PPI_FILE_NAME, TURKEY_PPI_FILE_NAME, WORM_PPI_FILE_NAME, YEAST_PPI_FILE_NAME,
                // HUMAN_TISSUE_EXPRESSION_FILE_NAME
        };
    }
}
