package de.unibi.agbi.biodwh2.mondo.etl;

import de.unibi.agbi.biodwh2.core.etl.OBOOntologyUpdater;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.mondo.MondoDataSource;

public class MondoUpdater extends OBOOntologyUpdater<MondoDataSource> {
    public MondoUpdater(final MondoDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getDownloadUrl() {
        return "http://purl.obolibrary.org/obo/mondo.obo";
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        final String[] versionParts = dataVersion.split("releases/")[1].split("/")[0].split("-");
        return new Version(Integer.parseInt(versionParts[0]), Integer.parseInt(versionParts[1]),
                           Integer.parseInt(versionParts[2]));
    }

    @Override
    protected String getTargetFileName() {
        return "mondo.obo";
    }
}
