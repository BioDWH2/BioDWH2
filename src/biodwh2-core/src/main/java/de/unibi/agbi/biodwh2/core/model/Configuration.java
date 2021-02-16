package de.unibi.agbi.biodwh2.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.Workspace;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Configuration {
    @JsonProperty("version")
    private int version;
    @JsonProperty("creationDateTime")
    private final String creationDateTime;
    @JsonProperty("dataSourceIds")
    private final List<String> dataSourceIds;
    @JsonProperty("dataSourceProperties")
    private final Map<String, Map<String, String>> dataSourceProperties;

    public Configuration() {
        version = Workspace.VERSION;
        creationDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        dataSourceIds = new ArrayList<>();
        dataSourceProperties = new HashMap<>();
    }

    @JsonIgnore
    public LocalDateTime getLocalCreationDateTime() {
        return LocalDateTime.parse(creationDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String[] getDataSourceIds() {
        return dataSourceIds.toArray(new String[0]);
    }

    @JsonIgnore
    public int getNumberOfDataSources() {
        return dataSourceIds.size();
    }

    public void addDataSource(final String dataSourceId) {
        if (!dataSourceIds.contains(dataSourceId))
            dataSourceIds.add(dataSourceId);
    }

    public void removeDataSource(final String dataSourceId) {
        dataSourceIds.remove(dataSourceId);
    }

    public boolean hasPropertiesForDataSource(final String dataSourceId) {
        return dataSourceProperties.containsKey(dataSourceId);
    }

    public Map<String, String> getDataSourceProperties(final String dataSourceId) {
        Map<String, String> properties = dataSourceProperties.get(dataSourceId);
        return properties == null ? new HashMap<>() : properties;
    }
}
