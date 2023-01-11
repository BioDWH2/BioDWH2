package de.unibi.agbi.biodwh2.efo;

import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.efo.etl.EFOMappingDescriber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class EFODataSource extends SingleOBOOntologyDataSource {
    private static final Pattern VERSION_PATTERN = Pattern.compile("v([0-9]+)\\.([0-9]+)\\.([0-9]+)");
    private static final String FILE_NAME = "efo.obo";

    @Override
    public String getId() {
        return "EFO";
    }

    @Override
    public String getLicense() {
        return "Apache-2.0";
    }

    @Override
    public String getFullName() {
        return "Experimental Factor Ontology (EFO)";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new EFOMappingDescriber(this);
    }

    @Override
    protected String getDownloadUrl() {
        return "http://www.ebi.ac.uk/efo/" + FILE_NAME;
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final Matcher matcher = VERSION_PATTERN.matcher(dataVersion);
        return matcher.find() ? new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                                            Integer.parseInt(matcher.group(3))) : null;
    }

    @Override
    protected String getTargetFileName() {
        return FILE_NAME;
    }
}
