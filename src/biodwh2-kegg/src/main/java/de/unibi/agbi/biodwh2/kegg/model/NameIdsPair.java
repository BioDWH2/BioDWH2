package de.unibi.agbi.biodwh2.kegg.model;

import java.util.ArrayList;
import java.util.List;

public class NameIdsPair {
    public String name;
    public final List<String> ids = new ArrayList<>();

    @Override
    public String toString() {
        return ids.size() == 0 ? name : name + " [" + String.join("; ", ids) + "]";
    }
}
