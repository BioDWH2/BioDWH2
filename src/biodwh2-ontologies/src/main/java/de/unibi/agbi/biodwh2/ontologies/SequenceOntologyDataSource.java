package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unused")
public class SequenceOntologyDataSource extends SingleOBOOntologyDataSource {
    static final String FILE_NAME = "so.obo";

    @Override
    public String getId() {
        return "SequenceOntology";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_SA_4_0.getName();
    }

    @Override
    protected String getDownloadUrl() {
        return "https://github.com/The-Sequence-Ontology/SO-Ontologies/raw/master/Ontology_Files/" + FILE_NAME;
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final String[] versionParts = StringUtils.split(StringUtils.split(dataVersion, ' ')[1], '-');
        return new Version(Integer.parseInt(versionParts[0]), Integer.parseInt(versionParts[1]),
                           Integer.parseInt(versionParts[2]));
    }

    @Override
    protected String getTargetFileName() {
        return FILE_NAME;
    }
}
