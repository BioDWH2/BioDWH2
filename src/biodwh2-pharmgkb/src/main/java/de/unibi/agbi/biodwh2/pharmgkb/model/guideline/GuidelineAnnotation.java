package de.unibi.agbi.biodwh2.pharmgkb.model.guideline;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class GuidelineAnnotation {
    public List<Citation> citations;
    public Guideline guideline;
}
