package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MeasureParamEnum {
    GEOMETRIC_MEAN("Geometric Mean"),
    GEOMETRIC_LEAST_SQUARES_MEAN("Geometric Least Squares Mean"),
    LEAST_SQUARES_MEAN("Least Squares Mean"),
    LOG_MEAN("Log Mean"),
    MEAN("Mean"),
    MEDIAN("Median"),
    NUMBER("Number"),
    COUNT_OF_PARTICIPANTS("Count of Participants"),
    COUNT_OF_UNITS("Count of Units");

    MeasureParamEnum(String value) {
        this.value = value;
    }

    public final String value;

    @JsonValue
    public String toValue() {
        return value;
    }
}
