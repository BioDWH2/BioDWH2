package de.unibi.agbi.biodwh2.core.text;

public enum License {
    CC0_1_0("CC0 1.0"),
    CC_BY_4_0("CC BY 4.0"),
    CC_BY_SA_4_0("CC BY-SA 4.0"),
    CC_BY_NC_4_0("CC BY-NC 4.0"),
    CC_BY_ND_4_0("CC BY-ND 4.0"),
    CC_BY_NC_ND_4_0("CC BY-NC-ND 4.0"),
    CC_BY_NC_SA_4_0("CC BY-NC-SA 4.0");

    private final String name;

    License(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
