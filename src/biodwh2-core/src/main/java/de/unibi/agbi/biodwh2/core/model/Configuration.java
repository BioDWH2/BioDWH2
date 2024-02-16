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
    private final Map<String, Map<String, String>> dataSourceProperties;
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
        final Map<String, String> properties = dataSourceProperties.get(dataSourceId);
        return properties == null ? new HashMap<>() : properties;
    }

    public GlobalProperties getGlobalProperties() {
        return globalProperties == null ? new GlobalProperties() : globalProperties;
    }

    public Integer getDataSourcePropertiesHash(final String dataSourceId) {
        final Map<String, String> properties = dataSourceProperties.get(dataSourceId);
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

    @JsonIgnoreProperties(ignoreUnknown = true, value = {"speciesFilterHelper"})
    public static class GlobalProperties {
        private SpeciesFilter speciesFilterHelper;

        @JsonProperty("speciesFilter")
        private Integer[] speciesFilter;

        public GlobalProperties() {
            speciesFilter = new Integer[0];
        }

        @JsonIgnore
        public SpeciesFilter getSpeciesFilter() {
            if (speciesFilterHelper == null)
                speciesFilterHelper = new SpeciesFilter(speciesFilter);
            return speciesFilterHelper;
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

        public static class SpeciesFilter {
            private final Set<Integer> taxonIds;

            public SpeciesFilter(final Integer[] taxonIds) {
                this.taxonIds = new HashSet<>();
                if (taxonIds != null)
                    this.taxonIds.addAll(Arrays.asList(taxonIds));
            }

            public boolean isSpeciesAllowed(final Integer taxonId) {
                return taxonIds == null || taxonIds.isEmpty() || taxonIds.contains(taxonId);
            }
        }
    }
}
