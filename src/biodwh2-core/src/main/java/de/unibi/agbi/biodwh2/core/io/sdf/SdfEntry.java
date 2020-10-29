package de.unibi.agbi.biodwh2.core.io.sdf;

import java.util.HashMap;
import java.util.Map;

public class SdfEntry {
    private String title;
    private String programTimestamp;
    private String comment;
    private String connectionTable;
    public final Map<String, String> properties;

    public SdfEntry() {
        properties = new HashMap<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProgramTimestamp() {
        return programTimestamp;
    }

    public void setProgramTimestamp(String programTimestamp) {
        this.programTimestamp = programTimestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getConnectionTable() {
        return connectionTable;
    }

    public void setConnectionTable(String connectionTable) {
        this.connectionTable = connectionTable;
    }
}
