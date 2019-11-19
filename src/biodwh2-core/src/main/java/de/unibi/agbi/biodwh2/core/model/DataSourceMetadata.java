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
    public Boolean parseSuccessfull;
    public Boolean exportRDFSuccessfull;
    public Boolean exportGraphMLSuccessfull;
    public Boolean updateSuccessful;

    public DataSourceMetadata() {
        sourceFileNames = new ArrayList<>();
    }

    @JsonIgnore
    public LocalDateTime getLocalUpdateDateTime() {
        return LocalDateTime.parse(updateDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public void setUpdateDateTimeNow() {
        updateDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}
