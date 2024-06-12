package de.unibi.agbi.biodwh2.hpo;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;

public class HPODataSource extends SingleOBOOntologyDataSource {
    static final String OBO_FILE_NAME = "hp.obo";
    public static final String DOWNLOAD_URL =
            "https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/" + OBO_FILE_NAME;

    @Override
    public String getId() {
        return "HPO";
    }

    @Override
    public String getLicense() {
        return "HPO license";
    }

    @Override
    public String getLicenseUrl() {
        return "https://hpo.jax.org/app/license";
    }

    @Override
    protected String getDownloadUrl() {
        return DOWNLOAD_URL;
    }

    @Override
    protected String getTargetFileName() {
        return OBO_FILE_NAME;
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        return versionFromDataVersionLine(dataVersion);
    }

    public static Version versionFromDataVersionLine(final String dataVersion) {
        final String[] versionParts = dataVersion.split("hp/releases/")[1].split("-");
        return new Version(Integer.parseInt(versionParts[0]), Integer.parseInt(versionParts[1]),
                           Integer.parseInt(versionParts[2]));
    }
}
