package de.unibi.agbi.biodwh2.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class DataSourceMetadata {
    @JsonProperty("version")
    public Version version;
    @JsonProperty("updateDateTime")
    public String updateDateTime;
    @JsonProperty("sourceFileNames")
    public List<String> sourceFileNames;
    @JsonProperty("updateSuccessful")
    public Boolean updateSuccessful;
    @JsonProperty("parseSuccessful")
    public Boolean parseSuccessful;
    @JsonProperty("exportSuccessful")
    public Boolean exportSuccessful;
    @JsonProperty("exportVersion")
    public Long exportVersion;

    public DataSourceMetadata() {
        sourceFileNames = new ArrayList<>();
    }

    @JsonIgnore
    public LocalDateTime getLocalUpdateDateTime() {
        return updateDateTime == null ? null : LocalDateTime.parse(updateDateTime,
                                                                   DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public void setUpdateDateTimeNow() {
        updateDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
