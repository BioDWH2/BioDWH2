package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.text.License;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class BasicFormalOntologyDataSource extends OBOFoundryOntologyDataSource {
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");

    protected BasicFormalOntologyDataSource() {
        super("BasicFormalOntology", "bfo.obo", License.CC_BY_4_0, "Basic Formal Ontology (BFO)");
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final Matcher matcher = VERSION_PATTERN.matcher(dataVersion);
        if (matcher.find())
            return new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                               Integer.parseInt(matcher.group(3)));
        return null;
    }
}
