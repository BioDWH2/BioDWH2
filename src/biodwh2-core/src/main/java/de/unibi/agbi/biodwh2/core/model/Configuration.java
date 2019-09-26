package de.unibi.agbi.biodwh2.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.unibi.agbi.biodwh2.core.Workspace;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class Configuration {
    public int version;
    public String creationDateTime;
    public List<String> dataSourceIds;

    public Configuration() {
        version = Workspace.Version;
        creationDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        dataSourceIds = new ArrayList<>();
    }

    @JsonIgnore
    public LocalDateTime getLocalCreationDateTime() {
        return LocalDateTime.parse(creationDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
