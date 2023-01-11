package de.unibi.agbi.biodwh2.mondo;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class MondoDataSource extends SingleOBOOntologyDataSource {
    private static final String FILE_NAME = "mondo.obo";

    @Override
    public String getId() {
        return "Mondo";
    }

    @Override
    public String getFullName() {
        return "Mondo Disease Ontology";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    protected String getDownloadUrl() {
        return "http://purl.obolibrary.org/obo/" + FILE_NAME;
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final String[] versionParts = dataVersion.split("releases/")[1].split("/")[0].split("-");
        return new Version(Integer.parseInt(versionParts[0]), Integer.parseInt(versionParts[1]),
                           Integer.parseInt(versionParts[2]));
    }

    @Override
    protected String getTargetFileName() {
        return FILE_NAME;
    }
}
