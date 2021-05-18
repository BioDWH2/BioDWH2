package de.unibi.agbi.biodwh2.disgenet.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DisGeNetUpdater extends Updater<DisGeNetDataSource> {
    private static final String URL_PREFIX = "https://www.disgenet.org/static/disgenet_ap1/files/downloads/";
    private static final String API_VERSION_URL = "https://www.disgenet.org/api/version";
    private static final String[] FILE_NAMES = {
            "curated_gene_disease_associations.tsv.gz", "befree_gene_disease_associations.tsv.gz",
            "all_gene_disease_associations.tsv.gz", "all_gene_disease_pmid_associations.tsv.gz",
            "curated_variant_disease_associations.tsv.gz", "befree_variant_disease_associations.tsv.gz",
            "all_variant_disease_associations.tsv.gz", "all_variant_disease_pmid_associations.tsv.gz",
            "disease_to_disease_CURATED.tsv.gz", "disease_to_disease_ALL.tsv.gz"
    };
    private static final Map<String, Integer> MONTH_NAME_TO_NUMBER = new HashMap<>();

    static {
        MONTH_NAME_TO_NUMBER.put("January", 1);
        MONTH_NAME_TO_NUMBER.put("February", 2);
        MONTH_NAME_TO_NUMBER.put("March", 3);
        MONTH_NAME_TO_NUMBER.put("April", 4);
        MONTH_NAME_TO_NUMBER.put("May", 5);
        MONTH_NAME_TO_NUMBER.put("June", 6);
        MONTH_NAME_TO_NUMBER.put("July", 7);
        MONTH_NAME_TO_NUMBER.put("August", 8);
        MONTH_NAME_TO_NUMBER.put("September", 9);
        MONTH_NAME_TO_NUMBER.put("October", 10);
        MONTH_NAME_TO_NUMBER.put("November", 11);
        MONTH_NAME_TO_NUMBER.put("December", 12);
    }

    public DisGeNetUpdater(final DisGeNetDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        final JsonNode json = loadReleasesJson();
        if (json.has("lastUpdate")) {
            // February 2021
            final String version = json.get("lastUpdate").asText();
            final String[] parts = StringUtils.split(version, ' ');
            final Integer monthNumber = MONTH_NAME_TO_NUMBER.getOrDefault(parts[0], null);
            if (monthNumber == null)
                throw new UpdaterMalformedVersionException(version);
            return new Version(Integer.parseInt(parts[1]), monthNumber);
        }
        return null;
    }

    private JsonNode loadReleasesJson() throws UpdaterException {
        try {
            final String source = HTTPClient.getWebsiteSource(API_VERSION_URL);
            return parseJsonSource(source);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    private JsonNode parseJsonSource(String source) throws UpdaterMalformedVersionException {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(source);
        } catch (IOException e) {
            throw new UpdaterMalformedVersionException(source, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : FILE_NAMES) {
            try {
                final String sourceFilePath = dataSource.resolveSourceFilePath(workspace, fileName);
                HTTPClient.downloadFileAsBrowser(URL_PREFIX + fileName, sourceFilePath);
            } catch (IOException e) {
                throw new UpdaterConnectionException("Failed to download '" + fileName + "'", e);
            }
        }
        return true;
    }
}
