package de.unibi.agbi.biodwh2.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.unibi.agbi.biodwh2.core.Workspace;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Configuration {
    public int version;
    public String creationDateTime;
    public List<String> dataSourceIds;
    public boolean rdfEnabled;
    public boolean graphMLEnabled;
    public boolean splitIntoSubGraphs;
    public Map<String, Map<String, String>> dataSourceProperties;

    public Configuration() {
        version = Workspace.Version;
        creationDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        dataSourceIds = new ArrayList<>();
        rdfEnabled = true;
        graphMLEnabled = true;
        splitIntoSubGraphs = false;
        dataSourceProperties = new HashMap<>();
    }

    @JsonIgnore
    public LocalDateTime getLocalCreationDateTime() {
        return LocalDateTime.parse(creationDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
