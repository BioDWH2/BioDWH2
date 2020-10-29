package de.unibi.agbi.biodwh2.kegg.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Variant extends KeggEntry {
    public String organism;
    public final Map<String, NameIdsPair> genes = new HashMap<>();
    public final List<NetworkLink> networks = new ArrayList<>();
    public final List<NameIdsPair> variations = new ArrayList<>();
}
