package de.unibi.agbi.biodwh2.iid.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.iid.IIDDataSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IIDUpdater extends Updater<IIDDataSource> {
    private static final String VERSION_URL = "https://iid.ophid.utoronto.ca/static/Search_By_Proteins.css";
    private static final String DOWNLOAD_URL_PREFIX = "https://iid.ophid.utoronto.ca/static/download/";
    static final String[] PPI_NETWORK_FILE_NAMES = {
            "alpaca_annotated_PPIs.txt.gz", "cat_annotated_PPIs.txt.gz", "chicken_annotated_PPIs.txt.gz",
            "cow_annotated_PPIs.txt.gz", "dog_annotated_PPIs.txt.gz", "duck_annotated_PPIs.txt.gz",
            "fly_annotated_PPIs.txt.gz", "guinea_pig_annotated_PPIs.txt.gz", "horse_annotated_PPIs.txt.gz",
            "human_annotated_PPIs.txt.gz", "mouse_annotated_PPIs.txt.gz", "pig_annotated_PPIs.txt.gz",
            "rabbit_annotated_PPIs.txt.gz", "rat_annotated_PPIs.txt.gz", "sheep_annotated_PPIs.txt.gz",
            "turkey_annotated_PPIs.txt.gz", "worm_annotated_PPIs.txt.gz", "yeast_annotated_PPIs.txt.gz"
    };
    static final String HUMAN_TISSUE_EXPRESSION_FILE_NAME = "human.uniprot2tissues.txt.gz";
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
        for (String fileName : PPI_NETWORK_FILE_NAMES)
            downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + fileName, fileName);
        downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + HUMAN_TISSUE_EXPRESSION_FILE_NAME,
                              HUMAN_TISSUE_EXPRESSION_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        final List<String> result = new ArrayList<>();
        Collections.addAll(result, PPI_NETWORK_FILE_NAMES);
        result.add(HUMAN_TISSUE_EXPRESSION_FILE_NAME);
        return result.toArray(new String[0]);
    }
}
