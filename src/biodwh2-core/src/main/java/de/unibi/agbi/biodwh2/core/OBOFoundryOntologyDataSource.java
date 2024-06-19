package de.unibi.agbi.biodwh2.core;

import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class OBOFoundryOntologyDataSource extends SingleOBOOntologyDataSource {
    public enum DataVersionFormat {
        UNKNOWN,
        DASHED_YYYY_MM_DD
    }

    private static final Pattern DASHED_YYYY_MM_DD_VERSION_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");

    private final String id;
    private final String fileName;
    private final String license;
    private final String fullName;
    private final String idPrefix;
    private final DataVersionFormat dataVersionFormat;

    protected OBOFoundryOntologyDataSource(final String id, final String fileName, final License license,
                                           final String fullName, String idPrefix) {
        this(id, fileName, license.getName(), fullName, idPrefix, DataVersionFormat.UNKNOWN);
    }

    protected OBOFoundryOntologyDataSource(final String id, final String fileName, final License license,
                                           final String fullName, String idPrefix,
                                           DataVersionFormat dataVersionFormat) {
        this(id, fileName, license.getName(), fullName, idPrefix, dataVersionFormat);
    }

    protected OBOFoundryOntologyDataSource(final String id, final String fileName, final String license,
                                           final String fullName, String idPrefix,
                                           DataVersionFormat dataVersionFormat) {
        this.id = id;
        this.fileName = fileName;
        this.license = license;
        this.fullName = fullName;
        this.idPrefix = idPrefix;
        this.dataVersionFormat = dataVersionFormat;
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
    public String getDownloadUrl() {
        return "https://purl.obolibrary.org/obo/" + fileName;
    }

    @Override
    public String getTargetFileName() {
        return fileName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getIdPrefix() {
        return idPrefix;
    }

    @Override
    public Version getVersionFromDataVersionLine(final String dataVersion) {
        if (dataVersionFormat == DataVersionFormat.DASHED_YYYY_MM_DD) {
            final Matcher matcher = DASHED_YYYY_MM_DD_VERSION_PATTERN.matcher(dataVersion);
            if (matcher.find())
                return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                                   Integer.parseInt(matcher.group(3)));
        }
        return null;
    }
}
