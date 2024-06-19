package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class NCBITaxonOntologyDataSource extends SingleOBOOntologyDataSource {
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
    private static final String FILE_NAME = "ncbitaxon.obo";

    @Override
    public String getId() {
        return "NCBITaxonOntology";
    }

    @Override
    public String getFullName() {
        return "NCBITaxon Ontology";
    }

    @Override
    public String getLicense() {
        return License.CC0_1_0.getName();
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
        final Matcher matcher = VERSION_PATTERN.matcher(dataVersion);
        if (matcher.find())
            return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                               Integer.parseInt(matcher.group(3)));
        return null;
    }

    @Override
    public String getIdPrefix() {
        return "NCBITaxon";
    }
}
