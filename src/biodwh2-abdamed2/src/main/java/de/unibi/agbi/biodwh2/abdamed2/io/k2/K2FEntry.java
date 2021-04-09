package de.unibi.agbi.biodwh2.abdamed2.io.k2;

import java.util.List;

/**
 * K2 format "Field definition" entry
 */
public class K2FEntry extends K2Entry {
    private String identifier;
    private String identifierName;
    private boolean primaryKey;
    private boolean required;
    private FieldLengthType fieldLengthType;
    private long fieldLength;
    private DataType dataType;

    K2FEntry(final EntryType type) {
        super(type);
    }

    @Override
    protected void parse(final String[] lines, final List<K2FEntry> fields) {
        identifier = lines[0];
        identifierName = lines[1];
        primaryKey = "1".equals(lines[2]);
        required = !"1".equals(lines[3]);
        fieldLengthType = FieldLengthType.valueOf(lines[4]);
        fieldLength = Long.parseLong(lines[5]);
        dataType = DataType.valueOf(lines[6]);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getIdentifierName() {
        return identifierName;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isRequired() {
        return required;
    }

    public FieldLengthType getFieldLengthType() {
        return fieldLengthType;
    }

    public long getFieldLength() {
        return fieldLength;
    }

    public DataType getDataType() {
        return dataType;
    }
}
