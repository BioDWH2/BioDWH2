package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class MolecularInteractionsOntologyDataSource extends SingleOBOOntologyDataSource {
    static final String FILE_NAME = "psi-mi.obo";

    @Override
    public String getId() {
        return "MolecularInteractionsOntology";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    protected String getDownloadUrl() {
        return "https://raw.githubusercontent.com/HUPO-PSI/psi-mi-CV/master/psi-mi.obo";
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
