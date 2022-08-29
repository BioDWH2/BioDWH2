package de.unibi.agbi.biodwh2.sider.model;

import java.util.HashSet;
import java.util.Set;

public final class CombinedDrug {
    public final String flatId;
    public String stereoId;
    public String name;
    public final Set<String> atcCodes;

    public CombinedDrug(final String flatId) {
        this.flatId = flatId;
        atcCodes = new HashSet<>();
    }
}
