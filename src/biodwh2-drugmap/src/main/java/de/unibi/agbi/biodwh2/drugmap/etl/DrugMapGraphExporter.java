package de.unibi.agbi.biodwh2.drugmap.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.drugmap.DrugMapDataSource;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DrugMapGraphExporter extends GraphExporter<DrugMapDataSource> {
    static final String DRUG_LABEL = "Drug";

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
        exportDrugs(workspace, graph);
        exportTherapeuticTargets(workspace, graph);
        exportTransporters(workspace, graph);
        exportMetabolizingEnzymes(workspace, graph);
        return false;
    }

    private void exportDrugs(final Workspace workspace, final Graph graph) {
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
        builder.withPropertyIfNotNull("chebi_id", entry.getFirst("CB"));
        builder.withPropertyIfNotNull("company", entry.getFirst("CP"));
        builder.withPropertyIfNotNull("sequence", entry.getFirst("SQ"));
        builder.withPropertyIfNotNull("molecular_weight", entry.getFirst("MW"));
        builder.withPropertyIfNotNull("iupac_name", entry.getFirst("IU"));
        builder.withPropertyIfNotNull("disease_entry", entry.getFirst("DE"));
        final String casNumber = entry.getFirst("CA");
        if (casNumber != null)
            builder.withProperty("cas_number", casNumber.split(" ")[1]);
        final String synonyms = entry.getFirst("SN");
        if (synonyms != null)
            builder.withProperty("synonyms", StringUtils.splitByWholeSeparator(synonyms, "; "));
        final String pubchemCID = entry.getFirst("PC");
        if (pubchemCID != null)
            builder.withProperty("pubchem_cid", Integer.parseInt(pubchemCID));
        builder.build();
    }

    private void exportTherapeuticTargets(final Workspace workspace, final Graph graph) {
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
        final NodeBuilder builder = graph.buildNode("TherapeuticTarget");
        builder.withProperty(ID_KEY, entry.getID());
        // TODO
        // TN: DTT_Name
        // UP: Uniprot_ID
        // UC: Uniprot_ID_HUMAN
        // BC: BioChemical_Class
        // SN: Synonyms
        // GN: Gene_Name
        // FC: Function
        // EC: EC_Number
        // PD: PDB_Structure
        // SQ: Sequence
        // TT: DTT_type
        // FM: Family
        // KE: KEGG_pathway
        // RC: Reactome
        builder.build();
    }

    private void exportTransporters(final Workspace workspace, final Graph graph) {
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
        final NodeBuilder builder = graph.buildNode("Transporter");
        builder.withProperty(ID_KEY, entry.getID());
        // TODO
        // TN: DTP Name
        // GN: Gene Name
        // SN: Synonyms
        // RS: Representative Substrate
        // ST: Substrate
        // GI: Gene ID
        // UP: Uniprot ID
        // UC: Uniprot Entry name
        // TC: TCDB ID
        // FM: Family
        // SF: SubFamily
        // TS: Tissue Specificity
        // FC: Function
        // SQ: Sequence
        builder.build();
    }

    private void exportMetabolizingEnzymes(final Workspace workspace, final Graph graph) {
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
        final NodeBuilder builder = graph.buildNode("MetabolizingEnzyme");
        builder.withProperty(ID_KEY, entry.getID());
        // TODO
        // DN: DME Name
        // GN: Gene Name
        // SN: Synonyms
        // UC: Uniprot AC
        // RD: Represent Drug
        // GI: Gene ID
        // E1: EC 1
        // E2: EC 2
        // E3: EC 3
        // EC: EC ID
        // RC: Reactome Pathway
        // KG: KEGG Pathway
        // PD: PDB ID
        // SQ: Sequence
        // TD: Tissue Distribution
        // FC: Function
        // KD: Kingdom
        // PL: Phylum
        // CL: Class
        // OD: Order
        // FM: Family
        // GE: Genus
        // SP: Species
        // SU: Subspecies
        builder.build();
    }
}
