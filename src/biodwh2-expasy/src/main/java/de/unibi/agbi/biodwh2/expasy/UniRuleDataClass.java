package de.unibi.agbi.biodwh2.expasy;

import java.util.Arrays;

public enum UniRuleDataClass {
    /**
     * Historical flag now only means, if absent, that a protein rule is not shown to the public
     */
    Auto("auto"),
    Protein("Protein"),
    TopoDomain("Topo_domain"),
    Domain("Domain"),
    Repeat("Repeat"),
    Segment("Segment"),
    CompBias("Comp_bias"),
    Site("Site"),
    Undef("Undef");

    private final String value;

    UniRuleDataClass(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UniRuleDataClass fromValue(final String value) {
        return Arrays.stream(values()).filter((k) -> value.equals(k.value)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return value;
    }
}
