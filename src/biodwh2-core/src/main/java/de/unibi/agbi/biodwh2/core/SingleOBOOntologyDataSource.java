package de.unibi.agbi.biodwh2.core;

import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public abstract class SingleOBOOntologyDataSource extends OntologyDataSource {
    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new SingleOBOUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new SingleOBOGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new SingleOBOMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }

    protected abstract String getDownloadUrl();

    protected abstract String getTargetFileName();

    protected abstract Version getVersionFromDataVersionLine(final String dataVersion);

    private static class SingleOBOUpdater extends OBOOntologyUpdater<SingleOBOOntologyDataSource> {
        public SingleOBOUpdater(final SingleOBOOntologyDataSource dataSource) {
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
    }

    private static class SingleOBOGraphExporter extends OntologyGraphExporter<SingleOBOOntologyDataSource> {
        public SingleOBOGraphExporter(final SingleOBOOntologyDataSource dataSource) {
            super(dataSource);
        }

        @Override
        public long getExportVersion() {
            return 1 + super.getExportVersion();
        }

        @Override
        protected String getOntologyFileName() {
            return dataSource.getTargetFileName();
        }
    }

    private static class SingleOBOMappingDescriber extends MappingDescriber {
        public SingleOBOMappingDescriber(final DataSource dataSource) {
            super(dataSource);
        }

        @Override
        public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
            return null;
        }

        @Override
        protected String[] getNodeMappingLabels() {
            return new String[0];
        }

        @Override
        public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
            return null;
        }

        @Override
        protected PathMapping[] getEdgePathMappings() {
            return new PathMapping[0];
        }
    }
}
