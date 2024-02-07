package de.unibi.agbi.biodwh2.opentargets.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.opentargets.OpenTargetsDataSource;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.io.DelegatingSeekableInputStream;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.SeekableInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

public class OpenTargetsGraphExporter extends GraphExporter<OpenTargetsDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(OpenTargetsGraphExporter.class);
    static final String MOLECULE_LABEL = "Molecule";
    static final String DISEASE_LABEL = "Disease";
    static final String REFERENCE_LABEL = "Reference";
    static final String MECHANISM_OF_ACTION_LABEL = "MechanismOfAction";
    static final String DRUG_WARNING_LABEL = "DrugWarning";
    static final String INDICATION_LABEL = "Indication";
    static final String PATHWAY_LABEL = "Pathway";
    static final String INDICATES_LABEL = "INDICATES";
    static final String HAS_REFERENCE_LABEL = "HAS_REFERENCE";
    static final String HAS_CHILD_LABEL = "HAS_CHILD";

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
        exportMolecules(workspace, graph);
        exportDiseases(workspace, graph);
        return false;
    }

    private void exportMolecules(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting molecules...");
        final Map<String, List<Long>> parentChemblIdChildNodeIdsMap = new HashMap<>();
        processParquetZip(workspace, OpenTargetsUpdater.MOLECULE_FILE_NAME,
                          (entry) -> exportMolecule(graph, parentChemblIdChildNodeIdsMap, entry));
        graph.beginEdgeIndicesDelay(HAS_CHILD_LABEL);
        for (final var entry : parentChemblIdChildNodeIdsMap.entrySet()) {
            final Node parentNode = graph.findNode(MOLECULE_LABEL, ID_KEY, entry.getKey());
            if (parentNode != null)
                for (final Long childNodeId : entry.getValue())
                    graph.addEdge(parentNode, childNodeId, HAS_CHILD_LABEL);
        }
        graph.endEdgeIndicesDelay(HAS_CHILD_LABEL);
    }

    @SuppressWarnings("unchecked")
    private void exportMolecule(final Graph graph, final Map<String, List<Long>> parentChemblIdChildNodeIdsMap,
                                final GenericRecord entry) {
        final NodeBuilder builder = graph.buildNode().withLabel(MOLECULE_LABEL);
        builder.withProperty(ID_KEY, (String) entry.get("id"));
        builder.withPropertyIfNotNull("canonical_smiles", (String) entry.get("canonicalSmiles"));
        builder.withPropertyIfNotNull("inchi_key", (String) entry.get("inchiKey"));
        builder.withPropertyIfNotNull("drug_type", (String) entry.get("drugType"));
        if (entry.get("blackBoxWarning") != null)
            builder.withProperty("black_box_warning", (Boolean) entry.get("blackBoxWarning"));
        builder.withPropertyIfNotNull("name", (String) entry.get("name"));
        if (entry.get("yearOfFirstApproval") != null)
            builder.withProperty("year_of_first_approval", ((Long) entry.get("yearOfFirstApproval")).intValue());
        if (entry.get("maximumClinicalTrialPhase") != null)
            builder.withProperty("maximum_clinical_trial_phase", ((Double) entry.get(
                    "maximumClinicalTrialPhase")).intValue());
        if (entry.get("hasBeenWithdrawn") != null)
            builder.withProperty("withdrawn", (Boolean) entry.get("hasBeenWithdrawn"));
        if (entry.get("isApproved") != null)
            builder.withProperty("approved", (Boolean) entry.get("isApproved"));
        builder.withPropertyIfNotNull("trade_names", genericDataArrayToArray(
                (GenericData.Array<GenericData.Record>) entry.get("tradeNames"), String[]::new));
        builder.withPropertyIfNotNull("synonyms", genericDataArrayToArray(
                (GenericData.Array<GenericData.Record>) entry.get("synonyms"), String[]::new));
        builder.withPropertyIfNotNull("description", (String) entry.get("description"));
        if (entry.get("crossReferences") != null) {
            final List<String> xrefs = new ArrayList<>();
            final var map = (Map<String, GenericData.Array<GenericData.Record>>) entry.get("crossReferences");
            for (final var xrefEntry : map.entrySet()) {
                final var xrefArray = genericDataArrayToArray(xrefEntry.getValue(), String[]::new);
                if (xrefArray != null)
                    xrefs.addAll(Arrays.stream(xrefArray).map(x -> xrefEntry.getKey() + ':' + x)
                                       .collect(Collectors.toList()));
            }
            if (!xrefs.isEmpty())
                builder.withProperty("xrefs", xrefs.toArray(new String[0]));
        }
        final Node node = builder.build();
        // TODO
        //  15: linkedDiseases type:UNION
        //  16: linkedTargets type:UNION
        if (entry.get("parentId") != null) {
            parentChemblIdChildNodeIdsMap.computeIfAbsent((String) entry.get("parentId"), (k) -> new ArrayList<>()).add(
                    node.getId());
        }
    }

    private void processParquetZip(Workspace workspace, final String fileName, final Consumer<GenericRecord> consumer) {
        try (final var stream = FileUtils.openZip(workspace, dataSource, fileName)) {
            while (stream.getNextEntry() != null) {
                final var inputFile = getInputFileFromZipStream(stream);
                final var conf = new Configuration();
                try (var reader = AvroParquetReader.<GenericRecord>builder(inputFile).withDataModel(
                        new ReflectData(getClass().getClassLoader())).disableCompatibility().withConf(conf).build()) {
                    GenericRecord entry;
                    while ((entry = reader.read()) != null) {
                        consumer.accept(entry);
                    }
                }
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

    private <T> T[] genericDataArrayToArray(final GenericData.Array<GenericData.Record> array,
                                            final IntFunction<T[]> generator) {
        if (array != null && !array.isEmpty())
            return array.stream().map(x -> x.get("element")).toArray(generator);
        return null;
    }

    private void exportDiseases(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting diseases...");
        processParquetZip(workspace, OpenTargetsUpdater.DISEASES_FILE_NAME, (entry) -> exportDisease(graph, entry));
    }

    @SuppressWarnings("unchecked")
    private void exportDisease(final Graph graph, final GenericRecord entry) {
        final NodeBuilder builder = graph.buildNode().withLabel(DISEASE_LABEL);
        builder.withProperty(ID_KEY, (String) entry.get("id"));
        builder.withPropertyIfNotNull("code", (String) entry.get("code"));
        builder.withPropertyIfNotNull("name", (String) entry.get("name"));
        builder.withPropertyIfNotNull("description", (String) entry.get("description"));
        builder.withPropertyIfNotNull("xrefs", genericDataArrayToArray(
                (GenericData.Array<GenericData.Record>) entry.get("dbXRefs"), String[]::new));
        final var synonyms = (GenericData.Record) entry.get("synonyms");
        if (synonyms != null) {
            builder.withPropertyIfNotNull("broad_synonyms", genericDataArrayToArray(
                    (GenericData.Array<GenericData.Record>) synonyms.get("hasBroadSynonym"), String[]::new));
            builder.withPropertyIfNotNull("exact_synonyms", genericDataArrayToArray(
                    (GenericData.Array<GenericData.Record>) synonyms.get("hasExactSynonym"), String[]::new));
            builder.withPropertyIfNotNull("narrow_synonyms", genericDataArrayToArray(
                    (GenericData.Array<GenericData.Record>) synonyms.get("hasNarrowSynonym"), String[]::new));
            builder.withPropertyIfNotNull("related_synonyms", genericDataArrayToArray(
                    (GenericData.Array<GenericData.Record>) synonyms.get("hasRelatedSynonym"), String[]::new));
        }
        // TODO
        //   5: directLocationIds
        //   6: obsoleteTerms
        //   7: parents
        //   9: ancestors
        //  10: descendants
        //  11: children
        //  12: therapeuticAreas
        //  13: indirectLocationIds
        //  14: ontology type:RECORD
        builder.build();
    }
}
