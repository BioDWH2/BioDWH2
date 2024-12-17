package de.unibi.agbi.biodwh2.geneontology.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.geneontology.GeneOntologyAnnotationsDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;

public class GeneOntologyUpdater extends Updater<GeneOntologyAnnotationsDataSource> {
    private static final String VERSION_JSON_URL = "https://current.geneontology.org/release_stats/go-stats-summary.json";
    private static final String DOWNLOAD_URL_PREFIX = "http://current.geneontology.org/annotations/";
    static final String GOA_CHICKEN_FILE_NAME = "goa_chicken.gaf.gz";
    static final String GOA_COW_FILE_NAME = "goa_cow.gaf.gz";
    static final String GOA_DOG_FILE_NAME = "goa_dog.gaf.gz";
    static final String GOA_HUMAN_FILE_NAME = "goa_human.gaf.gz";
    static final String GOA_PIG_FILE_NAME = "goa_pig.gaf.gz";
    private static final String[] ANNOTATION_FILE_NAMES = new String[]{
            GOA_CHICKEN_FILE_NAME, GOA_COW_FILE_NAME, GOA_DOG_FILE_NAME, GOA_HUMAN_FILE_NAME, GOA_PIG_FILE_NAME
    };

    public GeneOntologyUpdater(final GeneOntologyAnnotationsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(Workspace workspace) throws UpdaterException {
        final var mapper = new ObjectMapper();
        try {
            final JsonNode root = mapper.readTree(new URL(VERSION_JSON_URL));
            final String version = root.get("release_date").textValue();
            final String[] dateParts = StringUtils.split(version, "-", 3);
            if (dateParts.length != 3)
                throw new UpdaterMalformedVersionException(version);
            return new Version(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
                               Integer.parseInt(dateParts[2]));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : ANNOTATION_FILE_NAMES)
            downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + fileName, fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return ANNOTATION_FILE_NAMES;
    }
}
