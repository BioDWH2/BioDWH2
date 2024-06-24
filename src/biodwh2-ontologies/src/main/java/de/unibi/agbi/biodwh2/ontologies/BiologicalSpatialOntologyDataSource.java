package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;

import java.util.regex.Matcher;

@SuppressWarnings("unused")
public class BiologicalSpatialOntologyDataSource extends SingleOBOOntologyDataSource {
    static final String FILE_NAME = "bspo-full.obo";

    @Override
    public String getId() {
        return "BiologicalSpatialOntology";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_3_0.getName();
    }

    @Override
    protected String getDownloadUrl() {
        return "https://raw.githubusercontent.com/obophenotype/biological-spatial-ontology/master/bspo-full.obo";
    }

    @Override
    protected String getTargetFileName() {
        return FILE_NAME;
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
        return "BSPO";
    }
}
