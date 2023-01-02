package de.unibi.agbi.biodwh2.sequenceontology.etl;

import de.unibi.agbi.biodwh2.core.etl.OBOOntologyUpdater;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.sequenceontology.SequenceOntologyDataSource;
import org.apache.commons.lang3.StringUtils;

public class SequenceOntologyUpdater extends OBOOntologyUpdater<SequenceOntologyDataSource> {
    static final String FILE_NAME = "so.obo";

    public SequenceOntologyUpdater(final SequenceOntologyDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getDownloadUrl() {
        return "https://github.com/The-Sequence-Ontology/SO-Ontologies/raw/master/Ontology_Files/so.obo";
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

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
