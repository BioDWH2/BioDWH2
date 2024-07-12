package de.unibi.agbi.biodwh2.core;

import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.tools.RobotTool;
import org.apache.commons.io.FilenameUtils;

public abstract class SingleOWLOntologyDataSource extends OntologyDataSource {
    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new SingleOWLUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new SingleOWLGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new SingleOBOOntologyDataSource.SingleOBOMappingDescriber(this);
    }

    protected abstract String getDownloadUrl();

    protected abstract String getTargetFileName();

    protected final String getTargetOBOFileName() {
        return FilenameUtils.removeExtension(getTargetFileName()) + ".obo";
    }

    protected abstract Version getVersionFromDataVersionLine(final String dataVersion);

    private static class SingleOWLUpdater extends OWLOntologyUpdater<SingleOWLOntologyDataSource> {
        public SingleOWLUpdater(final SingleOWLOntologyDataSource dataSource) {
            super(dataSource);
        }

        @Override
        protected String getDownloadUrl() {
            return dataSource.getDownloadUrl();
        }

        @Override
        protected Version getVersionFromDataVersionLine(final String dataVersion) {
            return dataSource.getVersionFromDataVersionLine(dataVersion);
        }

        @Override
        protected String getTargetFileName() {
            return dataSource.getTargetFileName();
        }

        @Override
        protected String[] expectedFileNames() {
            return new String[]{getTargetFileName()};
        }

        @Override
        protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
            if (!super.tryUpdateFiles(workspace))
                return false;
            return RobotTool.convertToOBO(workspace, dataSource, getTargetFileName(),
                                          dataSource.getTargetOBOFileName());
        }
    }

    private static class SingleOWLGraphExporter extends OntologyGraphExporter<SingleOWLOntologyDataSource> {
        public SingleOWLGraphExporter(final SingleOWLOntologyDataSource dataSource) {
            super(dataSource);
        }

        @Override
        public long getExportVersion() {
            return 1 + super.getExportVersion();
        }

        @Override
        protected String getOntologyFileName() {
            return dataSource.getTargetOBOFileName();
        }
    }
}
