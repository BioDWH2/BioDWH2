package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;

public class PeriodStruct {
    @JsonProperty(required = true)
    public String title;
    @JsonProperty(value = "milestone_list", required = true)
    public PeriodStruct.MilestoneList milestoneList;
    @JsonProperty(value = "drop_withdraw_reason_list")
    public PeriodStruct.DropWithdrawReasonList dropWithdrawReasonList;

    public static class DropWithdrawReasonList {
        @JsonProperty(value = "drop_withdraw_reason")
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<MilestoneStruct> dropWithdrawReason;
    }

    public static class MilestoneList {
        @JsonProperty(required = true)
        @JacksonXmlElementWrapper(useWrapping = false)
        public ArrayList<MilestoneStruct> milestone;
    }
}
