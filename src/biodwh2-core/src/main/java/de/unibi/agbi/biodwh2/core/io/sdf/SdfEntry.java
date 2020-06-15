package de.unibi.agbi.biodwh2.core.io.sdf;

import java.util.HashMap;
import java.util.Map;

public class SdfEntry {
    public String title;
    public String programTimestamp;
    public String comment;
    public String connectionTable;
    public final Map<String, String> properties = new HashMap<>();
}
