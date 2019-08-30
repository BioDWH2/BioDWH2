package de.unibi.agbi.biodwh2.dgidb.model;

import java.util.List;

public abstract class Claim {
    public String source;
    public List<NameValuePair> attributes;
    public List<Integer> publications;
}
