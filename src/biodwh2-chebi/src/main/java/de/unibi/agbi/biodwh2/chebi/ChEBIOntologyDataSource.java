package de.unibi.agbi.biodwh2.chebi;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

@SuppressWarnings("unused")
public class ChEBIOntologyDataSource extends SingleOBOOntologyDataSource {
    private static final String FILE_NAME = "chebi.obo";

    @Override
    public String getId() {
        return "ChEBIOntology";
    }

    @Override
    public String getFullName() {
        return "ChEBI Ontology";
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
        final String version = StringUtils.split(dataVersion, ":", 2)[1].strip();
        return NumberUtils.isDigits(version) ? new Version(Integer.parseInt(version), 0) : null;
    }

    @Override
    public String getIdPrefix() {
        return "CHEBI";
    }
}
