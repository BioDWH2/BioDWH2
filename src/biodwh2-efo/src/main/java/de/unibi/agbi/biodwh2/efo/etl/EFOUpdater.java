package de.unibi.agbi.biodwh2.efo.etl;

import de.unibi.agbi.biodwh2.core.etl.OBOOntologyUpdater;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.efo.EFODataSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EFOUpdater extends OBOOntologyUpdater<EFODataSource> {
    final Pattern versionPattern = Pattern.compile("v([0-9]+)\\.([0-9]+)\\.([0-9]+)");

    public EFOUpdater(final EFODataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getDownloadUrl() {
        return "http://www.ebi.ac.uk/efo/efo.obo";
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final Matcher matcher = versionPattern.matcher(dataVersion);
        return matcher.find() ? new Version(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                                            Integer.parseInt(matcher.group(3))) : null;
    }

    @Override
    protected String getTargetFileName() {
        return "efo.obo";
    }
}
