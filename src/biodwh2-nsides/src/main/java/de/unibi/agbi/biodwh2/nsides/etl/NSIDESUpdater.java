package de.unibi.agbi.biodwh2.nsides.etl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.nsides.NSIDESDataSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NSIDESUpdater extends Updater<NSIDESDataSource> {
    private static final String BUCKET_LISTING_URL = "https://tatonettilab-resources.s3.us-west-1.amazonaws.com";
    private static final String NSIDES_DOWNLOAD_URL_PREFIX = "https://tatonettilab-resources.s3.amazonaws.com/nsides/";
    private static final String KIDSIDES_DOWNLOAD_URL_PREFIX = "https://tlab-kidsides.s3.amazonaws.com/data/";
    static final String OFFSIDES_FILE_NAME = "OFFSIDES.csv.gz";
    static final String TWOSIDES_FILE_NAME = "TWOSIDES.csv.gz";
    static final String AWAREDX_FILE_NAME = "AwareDX_Data.xlsx.zip";
    static final String KIDSIDES_ADE_NICHD_FILE_NAME = "ade_nichd.csv.gz";
    static final String KIDSIDES_ADE_RAW_FILE_NAME = "ade_raw.csv.gz";
    static final String KIDSIDES_DICTIONARY_FILE_NAME = "dictionary.csv.gz";
    static final String KIDSIDES_DRUG_FILE_NAME = "drug.csv.gz";
    static final String KIDSIDES_EVENT_FILE_NAME = "event.csv.gz";

    public NSIDESUpdater(final NSIDESDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterConnectionException {
        final String source = getWebsiteSource(BUCKET_LISTING_URL);
        final var xmlMapper = new XmlMapper();
        try {
            final var bucketResult = xmlMapper.readValue(source, ListBucketResult.class);
            LocalDateTime newestDateTime = null;
            for (final var content : bucketResult.Contents) {
                // TODO: add AwareDX
                if (content.key.endsWith(TWOSIDES_FILE_NAME) || content.key.endsWith(OFFSIDES_FILE_NAME)) {
                    final var dt = LocalDateTime.parse(content.lastModified, DateTimeFormatter.ISO_DATE_TIME);
                    if (newestDateTime == null || dt.isAfter(newestDateTime))
                        newestDateTime = dt;
                }
            }
            if (newestDateTime != null)
                return new Version(newestDateTime.getYear(), newestDateTime.getMonthValue(),
                                   newestDateTime.getDayOfMonth());
            return null;
        } catch (JsonProcessingException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, NSIDES_DOWNLOAD_URL_PREFIX + OFFSIDES_FILE_NAME, OFFSIDES_FILE_NAME);
        downloadFileAsBrowser(workspace, NSIDES_DOWNLOAD_URL_PREFIX + TWOSIDES_FILE_NAME, TWOSIDES_FILE_NAME);
        // TODO
        //  downloadFileAsBrowser(workspace, NSIDES_DOWNLOAD_URL_PREFIX + AWAREDX_FILE_NAME, AWAREDX_FILE_NAME);
        //  downloadFileAsBrowser(workspace, KIDSIDES_DOWNLOAD_URL_PREFIX + KIDSIDES_ADE_NICHD_FILE_NAME, KIDSIDES_ADE_NICHD_FILE_NAME);
        //  downloadFileAsBrowser(workspace, KIDSIDES_DOWNLOAD_URL_PREFIX + KIDSIDES_ADE_RAW_FILE_NAME, KIDSIDES_ADE_RAW_FILE_NAME);
        //  downloadFileAsBrowser(workspace, KIDSIDES_DOWNLOAD_URL_PREFIX + KIDSIDES_DICTIONARY_FILE_NAME, KIDSIDES_DICTIONARY_FILE_NAME);
        //  downloadFileAsBrowser(workspace, KIDSIDES_DOWNLOAD_URL_PREFIX + KIDSIDES_DRUG_FILE_NAME, KIDSIDES_DRUG_FILE_NAME);
        //  downloadFileAsBrowser(workspace, KIDSIDES_DOWNLOAD_URL_PREFIX + KIDSIDES_EVENT_FILE_NAME, KIDSIDES_EVENT_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                OFFSIDES_FILE_NAME, TWOSIDES_FILE_NAME
                //, AWAREDX_FILE_NAME, KIDSIDES_ADE_NICHD_FILE_NAME,
                // KIDSIDES_ADE_RAW_FILE_NAME, KIDSIDES_DICTIONARY_FILE_NAME, KIDSIDES_DRUG_FILE_NAME,
                // KIDSIDES_EVENT_FILE_NAME
        };
    }

    @JsonIgnoreProperties({"Name", "Prefix", "Marker", "MaxKeys", "IsTruncated"})
    static class ListBucketResult {
        @JacksonXmlElementWrapper(useWrapping = false, localName = "Contents")
        public List<Contents> Contents;
    }

    @JsonIgnoreProperties({"Owner", "StorageClass"})
    static class Contents {
        @JsonProperty("Key")
        public String key;
        @JsonProperty("LastModified")
        public String lastModified;
        @JsonProperty("ETag")
        public String eTag;
        @JsonProperty("Size")
        public String size;
    }
}
