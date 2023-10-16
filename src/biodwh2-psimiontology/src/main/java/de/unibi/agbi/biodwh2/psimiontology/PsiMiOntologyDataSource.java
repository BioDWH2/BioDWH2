package de.unibi.agbi.biodwh2.psimiontology;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;

public class PsiMiOntologyDataSource extends SingleOBOOntologyDataSource {
    private static final String FILE_NAME = "mi.obo";

    @Override
    public String getId() {
        return "PsiMiOntology";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    protected String getDownloadUrl() {
        return "https://purl.obolibrary.org/obo/" + FILE_NAME;
    }

    @Override
    protected String getTargetFileName() {
        return FILE_NAME;
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        // Currently, there is no data-version line in the mi.obo
        return null;
    }
}
