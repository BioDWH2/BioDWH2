package de.unibi.agbi.biodwh2.core.io.biopax;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StructureFormat {
    @JsonProperty("CML") CML,
    @JsonProperty("InChI") InChI,
    @JsonProperty("SMILES") SMILES
}
