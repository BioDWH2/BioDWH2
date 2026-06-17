package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.SingleOBOOntologyDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.OntologyGraphExporter;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.CompoundNodeMappingDescription;
import de.unibi.agbi.biodwh2.core.text.License;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class AROOntologyDataSource extends SingleOBOOntologyDataSource {
    private static final String FILE_NAME = "aro.obo";
    private static final String ARCHIVE_FILE_NAME = "card-ontology.tar.bz2";

    @Override
    public String getId() {
        return "AROOntology";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    public String getIdPrefix() {
        return "ARO";
    }

    @Override
    protected String getTargetFileName() {
        return FILE_NAME;
    }

    @Override
    protected String getDownloadUrl() {
        return null;
    }

    @Override
    protected Version getVersionFromDataVersionLine(final String dataVersion) {
        return null;
    }

    @Override
    protected Updater<? extends AROOntologyDataSource> getUpdater() {
        return new AROUpdater(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new AROOntologyMappingDescriber(this);
    }

    private static final class AROUpdater extends Updater<AROOntologyDataSource> {
        private static final Pattern VERSION_PATTERN = Pattern.compile(
                "<td class=\"hidden-xs\">([0-9]{4}-[0-9]{2}-[0-9]{2}) [0-9]{2}:[0-9]{2}:[0-9]{2}",
                Pattern.CASE_INSENSITIVE);
        private static final String DOWNLOAD_URL = "https://card.mcmaster.ca/latest/ontology";
        private static final String VERSION_PAGE_URL = "https://card.mcmaster.ca/download";

        AROUpdater(final AROOntologyDataSource dataSource) {
            super(dataSource);
        }

        @Override
        public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
            final String source = getWebsiteSource(VERSION_PAGE_URL);
            if (source == null)
                return null;
            final Matcher matcher = VERSION_PATTERN.matcher(source);
            if (matcher.find()) {
                final String[] parts = StringUtils.split(matcher.group(1), '-');
                return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                                   Integer.parseInt(parts[2]));
            }
            return null;
        }

        @Override
        protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
            downloadFileAsBrowser(workspace, DOWNLOAD_URL, ARCHIVE_FILE_NAME);
            extractAROFromArchive(workspace);
            deleteArchive(workspace);
            return true;
        }

        private void extractAROFromArchive(final Workspace workspace) throws UpdaterException {
            final Path targetPath = dataSource.resolveSourceFilePath(workspace, FILE_NAME);
            try (InputStream fileStream = FileUtils.openInput(workspace, dataSource, ARCHIVE_FILE_NAME);
                 BufferedInputStream buffered = new BufferedInputStream(fileStream);
                 BZip2CompressorInputStream bzip2 = new BZip2CompressorInputStream(buffered);
                 TarArchiveInputStream tar = new TarArchiveInputStream(bzip2)) {
                ArchiveEntry entry;
                while ((entry = tar.getNextEntry()) != null) {
                    if (!entry.isDirectory() && entry.getName().endsWith("aro.obo")) {
                        try (OutputStream out = Files.newOutputStream(targetPath)) {
                            tar.transferTo(out);
                        }
                        return;
                    }
                }
            } catch (IOException e) {
                throw new UpdaterConnectionException("Failed to extract aro.obo from " + ARCHIVE_FILE_NAME, e);
            }
            throw new UpdaterConnectionException("aro.obo not found in " + ARCHIVE_FILE_NAME);
        }

        private void deleteArchive(final Workspace workspace) {
            try {
                Files.deleteIfExists(dataSource.resolveSourceFilePath(workspace, ARCHIVE_FILE_NAME));
            } catch (IOException ignored) {
            }
        }

        @Override
        protected String[] expectedFileNames() {
            return new String[]{FILE_NAME};
        }
    }

    private static final class AROOntologyMappingDescriber extends MappingDescriber {
        AROOntologyMappingDescriber(final DataSource dataSource) {
            super(dataSource);
        }

        @Override
        public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
            if (!OntologyGraphExporter.TERM_LABEL.equals(localMappingLabel))
                return null;
            final String[] xrefs = node.getProperty("xrefs");
            if (xrefs == null || xrefs.length == 0)
                return null;
            final CompoundNodeMappingDescription compoundDescription = new CompoundNodeMappingDescription();
            final NodeMappingDescription drugDescription = new NodeMappingDescription(
                    NodeMappingDescription.NodeType.DRUG);
            for (final String xref : xrefs) {
                if (StringUtils.isBlank(xref))
                    continue;
                final String[] parts = StringUtils.split(xref, ":", 2);
                if (parts.length < 2)
                    continue;
                final String value = parts[1].trim();
                switch (parts[0].trim().toLowerCase()) {
                    case "cas":
                        compoundDescription.addIdentifier(IdentifierType.CAS, value);
                        drugDescription.addIdentifier(IdentifierType.CAS, value);
                        break;
                    case "chebi":
                        final String chebiValue = StringUtils.removeStartIgnoreCase(value, "CHEBI:");
                        if (NumberUtils.isDigits(chebiValue)) {
                            final int chebiId = Integer.parseInt(chebiValue);
                            compoundDescription.addIdentifier(IdentifierType.CHEBI, chebiId);
                            drugDescription.addIdentifier(IdentifierType.CHEBI, chebiId);
                        }
                        break;
                    case "chembl":
                        compoundDescription.addIdentifier(IdentifierType.CHEMBL, value);
                        drugDescription.addIdentifier(IdentifierType.CHEMBL, value);
                        break;
                    case "pubchem":
                        if (NumberUtils.isDigits(value)) {
                            final int pubChemId = Integer.parseInt(value);
                            compoundDescription.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, pubChemId);
                            drugDescription.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, pubChemId);
                        }
                        break;
                    default:
                        break;
                }
            }
            if (!compoundDescription.hasIdentifiers())
                return null;
            final String name = node.getProperty("name");
            compoundDescription.addName(name);
            drugDescription.addName(name);
            return new NodeMappingDescription[]{compoundDescription, drugDescription};
        }

        @Override
        public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
            return null;
        }

        @Override
        protected String[] getNodeMappingLabels() {
            return new String[]{OntologyGraphExporter.TERM_LABEL};
        }

        @Override
        protected PathMapping[] getEdgePathMappings() {
            return new PathMapping[0];
        }
    }
}