package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;

import java.util.regex.Matcher;

@SuppressWarnings("unused")
public class PlantStressOntologyDataSource extends SingleOBOOntologyDataSource {
    private static final String FILE_NAME = "pso-full.obo";

    @Override
    public String getId() {
        return "PlantStressOntology";
    }

    @Override
    public String getFullName() {
        return "Plant Stress Ontology (PSO)";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    protected String getDownloadUrl() {
        return "https://raw.githubusercontent.com/Planteome/plant-stress-ontology/master/" + FILE_NAME;
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
        return "PSO";
    }
}
