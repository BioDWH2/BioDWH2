package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.SingleOWLOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;

import java.util.regex.Matcher;

@SuppressWarnings("unused")
public class AgroOntologyDataSource extends SingleOWLOntologyDataSource {
    @Override
    protected String getDownloadUrl() {
        return "https://raw.githubusercontent.com/AgriculturalSemantics/agro/master/agro.owl";
    }

    @Override
    protected String getTargetFileName() {
        return "agro.owl";
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final Matcher matcher = DASHED_YYYY_MM_DD_VERSION_PATTERN.matcher(dataVersion);
        if (matcher.find())
            return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                               Integer.parseInt(matcher.group(3)));
        return null;
    }

    @Override
    public String getIdPrefix() {
        return "AGRO";
    }

    @Override
    public String getId() {
        return "AgronomyOntology";
    }

    @Override
    public String getFullName() {
        return "Agronomy Ontology (AGRO)";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }
}
