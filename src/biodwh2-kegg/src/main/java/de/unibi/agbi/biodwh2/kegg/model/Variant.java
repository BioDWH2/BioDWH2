package de.unibi.agbi.biodwh2.kegg.model;

import java.util.HashMap;
import java.util.Map;

public class Variant extends KeggEntry {
    public String organism;
    public final Map<String, NameIdsPair> genes = new HashMap<>();
}
