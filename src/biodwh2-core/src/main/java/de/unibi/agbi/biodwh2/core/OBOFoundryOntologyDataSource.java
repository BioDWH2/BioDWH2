package de.unibi.agbi.biodwh2.core;

import de.unibi.agbi.biodwh2.core.text.License;

public abstract class OBOFoundryOntologyDataSource extends SingleOBOOntologyDataSource {
    private final String id;
    private final String fileName;
    private final String license;
    private final String fullName;

    protected OBOFoundryOntologyDataSource(final String id, final String fileName, final License license,
                                           final String fullName) {
        this(id, fileName, license.getName(), fullName);
    }

    protected OBOFoundryOntologyDataSource(final String id, final String fileName, final String license,
                                           final String fullName) {
        this.id = id;
        this.fileName = fileName;
        this.license = license;
        this.fullName = fullName;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getLicense() {
        return license;
    }

    @Override
    protected String getDownloadUrl() {
        return "https://purl.obolibrary.org/obo/" + fileName;
    }

    @Override
    protected String getTargetFileName() {
        return fileName;
    }

    @Override
    public String getId() {
        return id;
    }
}
