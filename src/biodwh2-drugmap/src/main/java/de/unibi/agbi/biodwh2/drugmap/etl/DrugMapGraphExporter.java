package de.unibi.agbi.biodwh2.drugmap.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.drugmap.DrugMapDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DrugMapGraphExporter extends GraphExporter<DrugMapDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(DrugMapGraphExporter.class);
    static final String DRUG_LABEL = "Drug";
    static final String THERAPEUTIC_TARGET_LABEL = "TherapeuticTarget";
    static final String TRANSPORTER_LABEL = "Transporter";
    static final String METABOLIZING_ENZYME_LABEL = "MetabolizingEnzyme";
    static final String PATHWAY_LABEL = "Pathway";
    static final String TARGETS_LABEL = "TARGETS";

    public DrugMapGraphExporter(final DrugMapDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(THERAPEUTIC_TARGET_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(TRANSPORTER_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(METABOLIZING_ENZYME_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PATHWAY_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportDrugs(workspace, graph);
        exportTherapeuticTargets(workspace, graph);
        exportTransporters(workspace, graph);
        exportMetabolizingEnzymes(workspace, graph);
        exportDrugTargets(workspace, graph);
        return true;
    }

    private void exportDrugs(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting drugs...");
        try (final var reader = new FlatFileDrugMapReader(
                dataSource.resolveSourceFilePath(workspace, DrugMapUpdater.DRUGS_FILE_NAME), StandardCharsets.UTF_8)) {
            for (final var entry : reader)
                exportDrug(graph, entry);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DrugMapUpdater.DRUGS_FILE_NAME + "'", e);
        }
    }

    private void exportDrug(final Graph graph, final FlatFileDrugMapEntry entry) {
        final NodeBuilder builder = graph.buildNode(DRUG_LABEL);
        builder.withProperty(ID_KEY, entry.getID());
        builder.withPropertyIfNotNull("name", entry.getFirst("DN"));
        builder.withPropertyIfNotNull("type", entry.getFirst("DT"));
        builder.withPropertyIfNotNull("highest_status", entry.getFirst("HS"));
        builder.withPropertyIfNotNull("therapeutic_class", entry.getFirst("TC"));
        builder.withPropertyIfNotNull("formula", entry.getFirst("FM"));
        builder.withPropertyIfNotNull("inchi", entry.getFirst("IC"));
        builder.withPropertyIfNotNull("inchi_key", entry.getFirst("IK"));
        builder.withPropertyIfNotNull("canonical_smiles", entry.getFirst("CS"));
        final String chebiId = entry.getFirst("CB");
        if (chebiId != null)
            builder.withPropertyIfNotNull("chebi_id", StringUtils.strip(chebiId, ". \t"));
        builder.withPropertyIfNotNull("company", entry.getFirst("CP"));
        builder.withPropertyIfNotNull("sequence", entry.getFirst("SQ"));
        builder.withPropertyIfNotNull("molecular_weight", entry.getFirst("MW"));
        builder.withPropertyIfNotNull("iupac_name", entry.getFirst("IU"));
        builder.withPropertyIfNotNull("disease_entry", entry.getFirst("DE"));
        final String casNumber = entry.getFirst("CA");
        if (casNumber != null && casNumber.startsWith("CAS"))
            builder.withProperty("cas_number", casNumber.split(" ")[1]);
        addArrayProperty(builder, entry, "SN", "synonyms");
        String pubchemCID = entry.getFirst("PC");
        if (pubchemCID != null) {
            pubchemCID = StringUtils.strip(pubchemCID, "; \t");
            if (pubchemCID.contains("; ")) {
                builder.withProperty("pubchem_cids", Arrays.stream(StringUtils.splitByWholeSeparator(pubchemCID, "; "))
                                                           .map(Integer::parseInt).toArray(Integer[]::new));
            } else {
                builder.withProperty("pubchem_cid", Integer.parseInt(pubchemCID));
            }
        }
        builder.build();
    }

    private void addArrayProperty(final NodeBuilder builder, final FlatFileDrugMapEntry entry, final String key,
                                  final String propertyKey) {
        final String values = entry.getFirst(key);
        if (values != null)
            builder.withProperty(propertyKey, StringUtils.splitByWholeSeparator(values, "; "));
    }

    private void exportTherapeuticTargets(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting therapeutic targets...");
        try (final var reader = new FlatFileDrugMapReader(
                dataSource.resolveSourceFilePath(workspace, DrugMapUpdater.THERAPEUTIC_TARGETS_FILE_NAME),
                StandardCharsets.UTF_8)) {
            for (final var entry : reader)
                exportTherapeuticTarget(graph, entry);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DrugMapUpdater.THERAPEUTIC_TARGETS_FILE_NAME + "'", e);
        }
    }

    private void exportTherapeuticTarget(final Graph graph, final FlatFileDrugMapEntry entry) {
        final NodeBuilder builder = graph.buildNode(THERAPEUTIC_TARGET_LABEL);
        builder.withProperty(ID_KEY, entry.getID());
        builder.withPropertyIfNotNull("name", entry.getFirst("TN"));
        builder.withPropertyIfNotNull("gene_name", entry.getFirst("GN"));
        builder.withPropertyIfNotNull("sequence", entry.getFirst("SQ"));
        builder.withPropertyIfNotNull("type", entry.getFirst("TT"));
        builder.withPropertyIfNotNull("family", entry.getFirst("FM"));
        builder.withPropertyIfNotNull("function", entry.getFirst("FC"));
        builder.withPropertyIfNotNull("biochemical_class", entry.getFirst("BC"));
        builder.withPropertyIfNotNull("uniprot_id", entry.getFirst("UP"));
        builder.withPropertyIfNotNull("uniprot_accession", entry.getFirst("UC"));
        addArrayProperty(builder, entry, "SN", "synonyms");
        addArrayProperty(builder, entry, "PD", "pdb_accessions");
        final String ecNumber = entry.getFirst("EC");
        if (ecNumber != null && ecNumber.startsWith("EC"))
            builder.withProperty("ec_number", ecNumber.split(" ")[1]);
        final Node node = builder.build();
        exportPathwayLinks(graph, node, entry.getFirst("KE"), "KEGG");
        exportPathwayLinks(graph, node, entry.getFirst("RC"), "Reactome");
    }

    private void exportPathwayLinks(final Graph graph, final Node node, final String value, final String source) {
        if (value == null)
            return;
        for (final String idNamePair : StringUtils.splitByWholeSeparator(value, "; ")) {
            final String[] parts = idNamePair.split(":");
            Node pathwayNode = graph.findNode(PATHWAY_LABEL, ID_KEY, parts[0]);
            if (pathwayNode == null)
                pathwayNode = graph.addNode(PATHWAY_LABEL, ID_KEY, parts[0], "name", parts[1], "source", source);
            graph.addEdge(node, pathwayNode, "ASSOCIATED_WITH");
        }
    }

    private void exportTransporters(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting transporters...");
        try (final var reader = new FlatFileDrugMapReader(
                dataSource.resolveSourceFilePath(workspace, DrugMapUpdater.TRANSPORTERS_FILE_NAME),
                StandardCharsets.UTF_8)) {
            for (final var entry : reader)
                exportTransporter(graph, entry);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DrugMapUpdater.TRANSPORTERS_FILE_NAME + "'", e);
        }
    }

    private void exportTransporter(final Graph graph, final FlatFileDrugMapEntry entry) {
        final NodeBuilder builder = graph.buildNode(TRANSPORTER_LABEL);
        builder.withProperty(ID_KEY, entry.getID());
        builder.withPropertyIfNotNull("name", entry.getFirst("TN"));
        builder.withPropertyIfNotNull("gene_name", entry.getFirst("GN"));
        builder.withPropertyIfNotNull("sequence", entry.getFirst("SQ"));
        builder.withPropertyIfNotNull("function", entry.getFirst("FC"));
        builder.withPropertyIfNotNull("gene_id", entry.getFirst("GI"));
        builder.withPropertyIfNotNull("family", entry.getFirst("FM"));
        builder.withPropertyIfNotNull("subfamily", entry.getFirst("SF"));
        builder.withPropertyIfNotNull("uniprot_id", entry.getFirst("UP"));
        builder.withPropertyIfNotNull("uniprot_accession", entry.getFirst("UC"));
        builder.withPropertyIfNotNull("tcdb_id", entry.getFirst("TC"));
        builder.withPropertyIfNotNull("tissue_specificity", entry.getFirst("TS"));
        addArrayProperty(builder, entry, "SN", "synonyms");
        addArrayProperty(builder, entry, "RS", "representative_substrate");
        addArrayProperty(builder, entry, "ST", "substrate");
        builder.build();
    }

    private void exportMetabolizingEnzymes(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting metabolizing enzymes...");
        try (final var reader = new FlatFileDrugMapReader(
                dataSource.resolveSourceFilePath(workspace, DrugMapUpdater.ENZYMES_FILE_NAME),
                StandardCharsets.UTF_8)) {
            for (final var entry : reader)
                exportMetabolizingEnzyme(graph, entry);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DrugMapUpdater.ENZYMES_FILE_NAME + "'", e);
        }
    }

    private void exportMetabolizingEnzyme(final Graph graph, final FlatFileDrugMapEntry entry) {
        final NodeBuilder builder = graph.buildNode(METABOLIZING_ENZYME_LABEL);
        builder.withProperty(ID_KEY, entry.getID());
        builder.withPropertyIfNotNull("name", entry.getFirst("DN"));
        builder.withPropertyIfNotNull("gene_name", entry.getFirst("GN"));
        builder.withPropertyIfNotNull("sequence", entry.getFirst("SQ"));
        builder.withPropertyIfNotNull("function", entry.getFirst("FC"));
        builder.withPropertyIfNotNull("ec_number", entry.getFirst("EC"));
        builder.withPropertyIfNotNull("tissue_distribution", entry.getFirst("TD"));
        builder.withPropertyIfNotNull("kingdom", entry.getFirst("KD"));
        builder.withPropertyIfNotNull("phylum", entry.getFirst("PL"));
        builder.withPropertyIfNotNull("class", entry.getFirst("CL"));
        builder.withPropertyIfNotNull("order", entry.getFirst("OD"));
        builder.withPropertyIfNotNull("family", entry.getFirst("FM"));
        builder.withPropertyIfNotNull("genus", entry.getFirst("GE"));
        builder.withPropertyIfNotNull("species", entry.getFirst("SP"));
        builder.withPropertyIfNotNull("subspecies", entry.getFirst("SU"));
        builder.withPropertyIfNotNull("uniprot_accession", entry.getFirst("UC"));
        builder.withPropertyIfNotNull("gene_id", entry.getFirst("GI"));
        builder.withPropertyIfNotNull("ec_number_level_1", entry.getFirst("E1"));
        builder.withPropertyIfNotNull("ec_number_level_2", entry.getFirst("E2"));
        builder.withPropertyIfNotNull("ec_number_level_3", entry.getFirst("E3"));
        builder.withPropertyIfNotNull("represent_drug", entry.getFirst("RD"));
        addArrayProperty(builder, entry, "SN", "synonyms");
        addArrayProperty(builder, entry, "PD", "pdb_accessions");
        final Node node = builder.build();
        exportPathwayLinks(graph, node, entry.getFirst("KE"), "KEGG");
        exportPathwayLinks(graph, node, entry.getFirst("RC"), "Reactome");
    }

    private void exportDrugTargets(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting drug target relations...");
        try (final var reader = new FlatFileDrugMapReader(
                dataSource.resolveSourceFilePath(workspace, DrugMapUpdater.DRUG_DTT_FILE_NAME),
                StandardCharsets.UTF_8)) {
            for (final var entry : reader)
                exportDrugTarget(graph, entry);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DrugMapUpdater.DRUG_DTT_FILE_NAME + "'", e);
        }
        try (final var reader = new FlatFileDrugMapReader(
                dataSource.resolveSourceFilePath(workspace, DrugMapUpdater.DRUG_DTP_FILE_NAME),
                StandardCharsets.UTF_8)) {
            for (final var entry : reader)
                exportDrugTarget(graph, entry);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DrugMapUpdater.DRUG_DTP_FILE_NAME + "'", e);
        }
        try (final var reader = new FlatFileDrugMapReader(
                dataSource.resolveSourceFilePath(workspace, DrugMapUpdater.DRUG_DME_FILE_NAME),
                StandardCharsets.UTF_8)) {
            for (final var entry : reader)
                exportDrugTarget(graph, entry);
        } catch (IOException e) {
            throw new ExporterException("Failed to export '" + DrugMapUpdater.DRUG_DME_FILE_NAME + "'", e);
        }
    }

    private void exportDrugTarget(final Graph graph, final FlatFileDrugMapEntry entry) {
        final Node drugNode = graph.findNode(DRUG_LABEL, ID_KEY, entry.getFirst("DI"));
        if (drugNode == null)
            return;
        Node targetNode;
        if (entry.hasKey("EI")) {
            targetNode = graph.findNode(METABOLIZING_ENZYME_LABEL, ID_KEY, entry.getFirst("EI"));
        } else {
            targetNode = graph.findNode(THERAPEUTIC_TARGET_LABEL, ID_KEY, entry.getFirst("TI"));
            if (targetNode == null)
                targetNode = graph.findNode(TRANSPORTER_LABEL, ID_KEY, entry.getFirst("TI"));
        }
        if (targetNode != null) {
            final String moa = entry.getFirst("MA");
            if (moa != null)
                graph.addEdge(drugNode, targetNode, TARGETS_LABEL, "reference", entry.getFirst("RA"), "reference_url",
                              entry.getFirst("RU"), "moa", moa);
            else
                graph.addEdge(drugNode, targetNode, TARGETS_LABEL, "reference", entry.getFirst("RA"), "reference_url",
                              entry.getFirst("RU"));
        }
    }
}
