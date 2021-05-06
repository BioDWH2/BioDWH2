package de.unibi.agbi.biodwh2.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class DataSourceMetadata {
    public Version version;
    public String updateDateTime;
    public List<String> sourceFileNames;
    public Boolean updateSuccessful;
    public Boolean parseSuccessful;
    public Boolean exportSuccessful;
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
