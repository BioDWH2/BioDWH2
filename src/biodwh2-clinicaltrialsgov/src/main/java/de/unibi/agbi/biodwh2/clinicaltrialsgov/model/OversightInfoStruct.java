package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OversightInfoStruct {
    @JsonProperty(value = "has_dmc")
    public YesNoEnum hasDmc;
    @JsonProperty(value = "is_fda_regulated_drug")
    public YesNoEnum isFdaRegulatedDrug;
    @JsonProperty(value = "is_fda_regulated_device")
    public YesNoEnum isFdaRegulatedDevice;
    @JsonProperty(value = "is_unapproved_device")
    public YesNoEnum isUnapprovedDevice;
    @JsonProperty(value = "is_ppsd")
    public YesNoEnum isPpsd;
    @JsonProperty(value = "is_us_export")
    public YesNoEnum isUsExport;
}
