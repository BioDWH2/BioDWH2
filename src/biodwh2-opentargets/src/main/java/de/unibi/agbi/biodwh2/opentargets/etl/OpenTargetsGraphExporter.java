package de.unibi.agbi.biodwh2.opentargets.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.opentargets.OpenTargetsDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.io.DelegatingSeekableInputStream;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.SeekableInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.zip.ZipInputStream;

public class OpenTargetsGraphExporter extends GraphExporter<OpenTargetsDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(OpenTargetsGraphExporter.class);
    public static final String MOLECULE_LABEL = "Molecule";
    public static final String DISEASE_LABEL = "Disease";
    public static final String REFERENCE_LABEL = "Reference";
    public static final String MECHANISM_OF_ACTION_LABEL = "MechanismOfAction";
    public static final String DRUG_WARNING_LABEL = "DrugWarning";
    public static final String INDICATION_LABEL = "Indication";
    public static final String PATHWAY_LABEL = "Pathway";
    public static final String INDICATES_LABEL = "INDICATES";
    public static final String HAS_REFERENCE_LABEL = "HAS_REFERENCE";

    public OpenTargetsGraphExporter(final OpenTargetsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(MOLECULE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(DISEASE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, ID_KEY, IndexDescription.Type.NON_UNIQUE));
        exportMolecules(workspace, graph);
        exportDiseases(workspace, graph);
        return false;
    }

    private void exportMolecules(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting molecules...");
        processParquetZip(workspace, OpenTargetsUpdater.MOLECULE_FILE_NAME,
                          (metadata, entry) -> exportMolecule(graph, metadata, entry));
    }

    private void exportMolecule(final Graph graph, final ParquetMetadata metadata, final Map<String, Object> entry) {
        // TODO
        graph.addNode(MOLECULE_LABEL, ID_KEY, entry.get("id"));
    }

    private void processParquetZip(Workspace workspace, final String fileName,
                                   final BiConsumer<ParquetMetadata, Map<String, Object>> consumer) {
        try (final var stream = FileUtils.openZip(workspace, dataSource, fileName)) {
            while (stream.getNextEntry() != null) {
                final var inputFile = getInputFileFromZipStream(stream);
                final var metadata = ParquetReader.readMetadata(inputFile);
                ParquetReader.streamContent(inputFile).forEach((entry) -> consumer.accept(metadata, entry));
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + fileName + "'", e);
        }
    }

    private static InputFile getInputFileFromZipStream(final ZipInputStream stream) throws IOException {
        final byte[] data = stream.readAllBytes();
        final var dataStream = new ByteArrayInputStream(data) {
            public int getPos() {
                return super.pos;
            }

            public void seek(int newPos) {
                pos = newPos;
            }
        };
        return new InputFile() {
            @Override
            public long getLength() {
                return data.length;
            }

            @Override
            public SeekableInputStream newStream() {
                return new DelegatingSeekableInputStream(dataStream) {
                    @Override
                    public long getPos() {
                        return dataStream.getPos();
                    }

                    @Override
                    public void seek(long newPos) {
                        dataStream.seek((int) newPos);
                    }
                };
            }
        };
    }

    private void exportDiseases(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting diseases...");
        processParquetZip(workspace, OpenTargetsUpdater.DISEASES_FILE_NAME,
                          (metadata, entry) -> exportDisease(graph, metadata, entry));
    }

    private void exportDisease(final Graph graph, final ParquetMetadata metadata, final Map<String, Object> entry) {
        // TODO
        graph.addNode(DISEASE_LABEL, ID_KEY, entry.get("id"));
    }
}
