package de.unibi.agbi.biodwh2.brenda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dataset {
    /**
     * Per specification value is only defined in derived types {@link TextDataset} and {@link NumericDataset}, however,
     * in the actual file value also appears in {@link Dataset} properties.
     */
    @JsonProperty("value")
    public String value;
    @JsonProperty("comment")
    public String comment;
    @JsonProperty("proteins")
    public String[] proteins;
    @JsonProperty("references")
    public String[] references;
}
