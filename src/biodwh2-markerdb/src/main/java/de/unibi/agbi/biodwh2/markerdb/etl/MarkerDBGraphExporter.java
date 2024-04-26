package de.unibi.agbi.biodwh2.markerdb.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.markerdb.MarkerDBDataSource;
import de.unibi.agbi.biodwh2.markerdb.model.*;

import java.io.IOException;

public class MarkerDBGraphExporter extends GraphExporter<MarkerDBDataSource> {
    public static final String GENE_LABEL = "Gene";
    public static final String CHEMICAL_LABEL = "Chemical";
    public static final String PROTEIN_LABEL = "Protein";
    public static final String KARYOTYPE_LABEL = "Karyotype";
    public static final String SEQUENCE_VARIANT_LABEL = "SequenceVariant";

    public MarkerDBGraphExporter(final MarkerDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(CHEMICAL_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(KARYOTYPE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(SEQUENCE_VARIANT_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportAllCollection(workspace, graph, MarkerDBUpdater.CHEMICALS_FILE_NAME);
        exportAllCollection(workspace, graph, MarkerDBUpdater.PROTEINS_FILE_NAME);
        exportAllCollection(workspace, graph, MarkerDBUpdater.KARYOTYPES_FILE_NAME);
        exportAllCollection(workspace, graph, MarkerDBUpdater.SEQUENCE_VARIANTS_FILE_NAME);
        exportSpecificCollection(workspace, graph, MarkerDBUpdater.DIAGNOSTIC_CHEMICALS_FILE_NAME);
        exportSpecificCollection(workspace, graph, MarkerDBUpdater.DIAGNOSTIC_PROTEIN_FILE_NAME);
        exportSpecificCollection(workspace, graph, MarkerDBUpdater.DIAGNOSTIC_KARYOTYPES_FILE_NAME);
        exportSpecificCollection(workspace, graph, MarkerDBUpdater.PREDICTIVE_GENETICS_FILE_NAME);
        exportSpecificCollection(workspace, graph, MarkerDBUpdater.EXPOSURE_CHEMICALS_FILE_NAME);
        return false;
    }

    private void exportAllCollection(final Workspace workspace, final Graph graph, final String fileName) {
        final XmlMapper xmlMapper = new XmlMapper();
        try (final var inputStream = FileUtils.openInput(workspace, dataSource, fileName)) {
            final var collection = xmlMapper.readValue(inputStream, AllCollection.class);
            if (collection.chemicals != null)
                for (final Chemical chemical : collection.chemicals)
                    exportChemical(graph, chemical);
            if (collection.proteins != null)
                for (final Protein protein : collection.proteins)
                    exportProtein(graph, protein);
            if (collection.karyotypes != null)
                for (final Karyotype karyotype : collection.karyotypes)
                    exportKaryotype(graph, karyotype);
            if (collection.sequenceVariants != null)
                for (final SequenceVariant sequenceVariant : collection.sequenceVariants)
                    exportSequenceVariant(graph, sequenceVariant);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + fileName + "'", e);
        }
    }

    private void exportChemical(final Graph graph, final Chemical entry) {
        final var node = graph.addNodeFromModel(entry);
        // TODO: conditions
    }

    private void exportProtein(final Graph graph, final Protein entry) {
        final var node = graph.addNodeFromModel(entry);
        // TODO: conditions
    }

    private void exportKaryotype(final Graph graph, final Karyotype entry) {
        final var node = graph.addNodeFromModel(entry);
        // TODO: conditions
    }

    private void exportSequenceVariant(final Graph graph, final SequenceVariant entry) {
        final var node = graph.addNodeFromModel(entry);
        // TODO: sequenceVariantMeasurements
    }

    private void exportSpecificCollection(final Workspace workspace, final Graph graph, final String fileName) {
        final XmlMapper xmlMapper = new XmlMapper();
        try (final var inputStream = FileUtils.openInput(workspace, dataSource, fileName)) {
            final var collection = xmlMapper.readValue(inputStream, SpecificCollection.class);
            if (collection.biomarkers != null) {
                if (collection.biomarkers.chemicals != null)
                    for (final ChemicalSimple chemical : collection.biomarkers.chemicals)
                        exportChemical(graph, chemical);
                if (collection.biomarkers.proteins != null)
                    for (final ProteinSimple protein : collection.biomarkers.proteins)
                        exportProtein(graph, protein);
                if (collection.biomarkers.karyotypes != null)
                    for (final KaryotypeSimple karyotype : collection.biomarkers.karyotypes)
                        exportKaryotype(graph, karyotype);
                if (collection.biomarkers.genes != null)
                    for (final GeneSimple gene : collection.biomarkers.genes)
                        exportGene(graph, gene);
            }
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + fileName + "'", e);
        }
    }

    private void exportChemical(final Graph graph, final ChemicalSimple entry) {
        if ("id".equalsIgnoreCase(entry.id))
            return;
        var node = graph.findNode(CHEMICAL_LABEL, ID_KEY, Integer.parseInt(entry.id));
        if (node == null)
            node = graph.addNodeFromModel(entry);
        // TODO: conditions
    }

    private void exportProtein(final Graph graph, final ProteinSimple entry) {
        if ("id".equalsIgnoreCase(entry.id))
            return;
        var node = graph.findNode(PROTEIN_LABEL, ID_KEY, Integer.parseInt(entry.id));
        if (node == null)
            node = graph.addNodeFromModel(entry);
        // TODO: conditions
    }

    private void exportKaryotype(final Graph graph, final KaryotypeSimple entry) {
        if ("id".equalsIgnoreCase(entry.id))
            return;
        var node = graph.findNode(KARYOTYPE_LABEL, ID_KEY, Integer.parseInt(entry.id));
        if (node == null)
            node = graph.addNodeFromModel(entry);
        // TODO: conditions
    }

    private void exportGene(final Graph graph, final GeneSimple entry) {
        if ("id".equalsIgnoreCase(entry.id))
            return;
        var node = graph.findNode(GENE_LABEL, ID_KEY, Integer.parseInt(entry.id));
        if (node == null)
            node = graph.addNodeFromModel(entry);
        // TODO: conditions
    }
}
