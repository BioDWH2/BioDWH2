package de.unibi.agbi.biodwh2.chebi;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

@SuppressWarnings("unused")
public class ChEBIOntologyDataSource extends OBOFoundryOntologyDataSource {
    public ChEBIOntologyDataSource() {
        super("ChEBIOntology", "chebi.obo", License.CC_BY_4_0, "ChEBI Ontology", "CHEBI", DataVersionFormat.UNKNOWN);
    }

    @Override
    public Version getVersionFromDataVersionLine(final String dataVersion) {
        final String version = StringUtils.split(dataVersion, ":", 2)[1].strip();
        return NumberUtils.isDigits(version) ? new Version(Integer.parseInt(version), 0) : null;
    }
}
