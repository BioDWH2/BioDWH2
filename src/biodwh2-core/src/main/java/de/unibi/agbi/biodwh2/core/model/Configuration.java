package de.unibi.agbi.biodwh2.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.Workspace;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Configuration {
    @JsonProperty("version")
    private final int version;
    @JsonProperty("creationDateTime")
    private final String creationDateTime;
    @JsonProperty("dataSourceIds")
    private final List<String> dataSourceIds;
    @JsonProperty("globalProperties")
    private final GlobalProperties globalProperties;
    @JsonProperty("dataSourceProperties")
    private final Map<String, Map<String, Object>> dataSourceProperties;
    @JsonProperty("skipGraphMLExport")
    private final Boolean skipGraphMLExport;
    @JsonProperty("skipMetaGraphGeneration")
    private final Boolean skipMetaGraphGeneration;

    public Configuration() {
        version = Workspace.VERSION;
        creationDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        dataSourceIds = new ArrayList<>();
        globalProperties = new GlobalProperties();
        dataSourceProperties = new HashMap<>();
        skipGraphMLExport = false;
        skipMetaGraphGeneration = false;
    }

    public String[] getDataSourceIds() {
        return dataSourceIds.toArray(new String[0]);
    }

    @JsonIgnore
    public int getNumberOfDataSources() {
        return new HashSet<>(dataSourceIds).size();
    }

    public void addDataSource(final String dataSourceId) {
        if (!dataSourceIds.contains(dataSourceId))
            dataSourceIds.add(dataSourceId);
    }

    public void removeDataSource(final String dataSourceId) {
        dataSourceIds.remove(dataSourceId);
    }

    public Map<String, Object> getDataSourceProperties(final String dataSourceId) {
        final Map<String, Object> properties = dataSourceProperties.get(dataSourceId);
        return properties == null ? new HashMap<>() : properties;
    }

    public GlobalProperties getGlobalProperties() {
        return globalProperties == null ? new GlobalProperties() : globalProperties;
    }

    public Integer getDataSourcePropertiesHash(final String dataSourceId) {
        final Map<String, Object> properties = dataSourceProperties.get(dataSourceId);
        if (properties == null)
            return 0;
        final int dataSourcePropertiesHash = properties.keySet().stream().sorted().map((k) -> (k + properties.get(
                k)).hashCode()).reduce(0, Integer::sum);
        return getGlobalProperties().hashCode() ^ dataSourcePropertiesHash;
    }

    public boolean shouldSkipGraphMLExport() {
        return Boolean.TRUE.equals(skipGraphMLExport);
    }

    public boolean shouldSkipMetaGraphGeneration() {
        return Boolean.TRUE.equals(skipMetaGraphGeneration);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GlobalProperties {
        @JsonProperty("speciesFilter")
        public Integer[] speciesFilter;

        public GlobalProperties() {
            speciesFilter = new Integer[0];
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            GlobalProperties that = (GlobalProperties) o;
            return Arrays.equals(speciesFilter, that.speciesFilter);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(speciesFilter);
        }
    }
}
