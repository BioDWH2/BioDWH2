package de.unibi.agbi.biodwh2.abdamed2.io.k2;

import java.util.List;

/**
 * K2 format "Header" entry
 */
public class K2KEntry extends K2Entry {
    /**
     * Filename with a maximum of 8 characters
     */
    private String fileName;
    /**
     * GES|UPD
     */
    private String fileExtension;
    /**
     * YYYYMMDD
     */
    private String validityDate;
    /**
     * YYYYMMDD
     */
    private String previousValidityDate;
    private String supplier;
    /**
     * Filename with a maximum of 20 characters
     */
    private String longFileName;
    /**
     * ABDATA file number with a maximum of 4 digits
     */
    private int abdataFileNumber;
    private int numberOfFBlocks;


    K2KEntry(final EntryType type) {
        super(type);
    }

    @Override
    protected void parse(final String[] lines, final List<K2FEntry> fields) {
        fileName = lines[0];
        fileExtension = lines[1];
        validityDate = lines[2];
        previousValidityDate = lines[3];
        supplier = lines[4];
        longFileName = lines[5];
        abdataFileNumber = Integer.parseInt(lines[6]);
        numberOfFBlocks = Integer.parseInt(lines[7]);
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getValidityDate() {
        return validityDate;
    }

    public String getPreviousValidityDate() {
        return previousValidityDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getLongFileName() {
        return longFileName;
    }

    public int getAbdataFileNumber() {
        return abdataFileNumber;
    }

    public int getNumberOfFBlocks() {
        return numberOfFBlocks;
    }
}
