package de.unibi.agbi.biodwh2.kegg.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class KeggEntry {
    public String id;
    public List<String> tags = new ArrayList<>();
    public List<String> names = new ArrayList<>();
    public Set<String> externalIds = new HashSet<>();
    public List<Reference> references = new ArrayList<>();
    public String comment;
    public String remark;
}
