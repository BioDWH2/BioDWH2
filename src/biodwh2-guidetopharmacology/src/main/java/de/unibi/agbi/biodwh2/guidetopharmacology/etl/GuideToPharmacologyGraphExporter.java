package de.unibi.agbi.biodwh2.guidetopharmacology.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.guidetopharmacology.GuideToPharmacologyDataSource;
import de.unibi.agbi.biodwh2.guidetopharmacology.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GuideToPharmacologyGraphExporter extends GraphExporter<GuideToPharmacologyDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(GuideToPharmacologyGraphExporter.class);

    public GuideToPharmacologyGraphExporter(final GuideToPharmacologyDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("INN", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Species", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Disease", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Object", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Ontology", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("OntologyTerm", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("GoProcess", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Variant", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Ligand", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Reference", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("ClinicalTrial", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("ClinicalTrial", "nct_id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Interaction", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("AntibioticDB", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Family", "id", false, IndexDescription.Type.UNIQUE));
        // Ignored currently empty files:
        // cellular_location.tsv, cellular_location_refs.tsv, iuphar2discoverx.tsv, do_disease_isa.tsv, discoverx.tsv,
        // drug2disease.tsv, further_reading.tsv, primary_regulator.tsv, ligand2meshpharmacology.tsv,
        // primary_regulator_refs.tsv, ligand_physchem_public.tsv
        // Ignored other files:
        // hottopic_refs.tsv, hot_topics.tsv, hot_topics_refs.tsv, hot_topics2object.tsv, hot_topics2family.tsv,
        // object_vectors.tsv, version.tsv, contributor.tsv, contributor_copy.tsv, contributor_link.tsv,
        // contributor2committee.tsv, contributor2intro.tsv, contributor2family.tsv, contributor2object.tsv,
        // committee.tsv, subcommittee.tsv, deleted_family.tsv, ligand_cluster.tsv, ligand_cluster_new.tsv,
        // peptide_ligand_cluster.tsv, peptide_ligand_sequence_cluster.tsv, iuphar2tocris.tsv, tocris.tsv,
        // tocris_update.tsv
        createNodesFromTsvFile(workspace, graph, Species.class, "species.tsv");
        createNodesFromTsvFile(workspace, graph, INN.class, "inn.tsv");
        exportDiseases(workspace, graph);
        exportLigands(workspace, graph);
        createNodesFromTsvFile(workspace, graph, Ontology.class, "ontology.tsv");
        createNodesFromTsvFile(workspace, graph, Database.class, "database.tsv");
        createNodesFromTsvFile(workspace, graph, GoProcess.class, "go_process.tsv");
        createNodesFromTsvFile(workspace, graph, Reference.class, "reference.tsv");
        createNodesFromTsvFile(workspace, graph, ClinicalTrial.class, "clinical_trial.tsv");
        for (final ClinicalTrialRef entry : parseTsvFile(workspace, ClinicalTrialRef.class,
                                                         "clinical_trial_refs.tsv")) {
            final Node trialNode = graph.findNode("ClinicalTrial", "id", entry.clinicalTrialId);
            final Node referenceNode = graph.findNode("Reference", "id", entry.referenceId);
            graph.addEdge(trialNode, referenceNode, "REFERENCES");
        }
        for (final OntologyTerm entry : parseTsvFile(workspace, OntologyTerm.class, "ontology_term.tsv")) {
            final Node node = graph.addNodeFromModel(entry);
            graph.addEdge(node, graph.findNode("Ontology", "id", entry.ontologyId), "BELONGS_TO");
        }
        for (final Family entry : parseTsvFile(workspace, Family.class, "family.tsv")) {
            final Node node = graph.addNodeFromModel(entry);
            // TODO: citeId
        }
        for (final Objects entry : parseTsvFile(workspace, Objects.class, "object.tsv")) {
            final Node node = graph.addNodeFromModel(entry);
            // TODO: citeId
        }
        for (final Variant entry : parseTsvFile(workspace, Variant.class, "variant.tsv")) {
            final Node node = graph.addNodeFromModel(entry);
            if (entry.speciesId != null) {
                final Node speciesNode = graph.findNode("Species", "id", entry.speciesId);
                graph.addEdge(node, speciesNode, "BELONGS_TO");
            }
            // TODO: objectId
        }
        for (final VariantRef entry : parseTsvFile(workspace, VariantRef.class, "variant_refs.tsv")) {
            final Node variantNode = graph.findNode("Variant", "id", entry.variantId);
            final Node referenceNode = graph.findNode("Reference", "id", entry.referenceId);
            graph.addEdge(variantNode, referenceNode, "REFERENCES");
        }
        for (final GoProcessRel entry : parseTsvFile(workspace, GoProcessRel.class, "go_process_rel.tsv")) {
            final Node parentNode = graph.findNode("GoProcess", "id", entry.parentId);
            final Node childNode = graph.findNode("GoProcess", "id", entry.childId);
            graph.addEdge(parentNode, childNode, "HAS_CHILD");
        }
        exportInteractions(workspace, graph);
        exportAntibioticDB(workspace, graph);
        // TODO: accessory_protein.tsv
        // TODO: allele.tsv
        // TODO: altered_expression.tsv
        // TODO: altered_expression_refs.tsv
        // TODO: analogue_cluster.tsv
        // TODO: associated_protein.tsv
        // TODO: associated_protein_refs.tsv
        // TODO: binding_partner.tsv
        // TODO: binding_partner_refs.tsv
        // TODO: catalytic_receptor.tsv
        // TODO: celltype_assoc.tsv
        // TODO: celltype_assoc_colist.tsv
        // TODO: celltype_assoc_refs.tsv
        // TODO: chembl_cluster.tsv
        // TODO: cite.tsv
        // TODO: cofactor.tsv
        // TODO: cofactor_refs.tsv
        // TODO: conductance.tsv
        // TODO: conductance_refs.tsv
        // TODO: conductance_states.tsv
        // TODO: conductance_states_refs.tsv
        // TODO: coregulator.tsv
        // TODO: coregulator_gene.tsv
        // TODO: coregulator_refs.tsv
        // TODO: covid_ligand.tsv
        // TODO: covid_target.tsv
        // TODO: co_celltype.tsv
        // TODO: co_celltype_isa.tsv
        // TODO: co_celltype_relationship.tsv
        // TODO: database_link.tsv
        // TODO: disease2synonym.tsv
        // TODO: disease_database_link.tsv
        // TODO: disease_synonym2database_link.tsv
        // TODO: dna_binding.tsv
        // TODO: dna_binding_refs.tsv
        // TODO: do_disease.tsv
        // TODO: enzyme.tsv
        // TODO: export_refs.tsv
        // TODO: expression_experiment.tsv
        // TODO: expression_level.tsv
        // TODO: expression_pathophysiology.tsv
        // TODO: expression_pathophysiology_refs.tsv
        // TODO: functional_assay.tsv
        // TODO: functional_assay_refs.tsv
        // TODO: gpcr.tsv
        // TODO: grac_family_text.tsv
        // TODO: grac_functional_characteristics.tsv
        // TODO: grac_further_reading.tsv
        // TODO: grac_ligand_rank_potency.tsv
        // TODO: grac_ligand_rank_potency_refs.tsv
        // TODO: grac_transduction.tsv
        // TODO: grouping.tsv
        // TODO: gtip2go_process.tsv
        // TODO: gtip_process.tsv
        // TODO: immuno2co_celltype.tsv
        // TODO: immunopaedia2family.tsv
        // TODO: immunopaedia2ligand.tsv
        // TODO: immunopaedia2object.tsv
        // TODO: immunopaedia_cases.tsv
        // TODO: immuno_celltype.tsv
        // TODO: immuno_disease2ligand.tsv
        // TODO: immuno_disease2ligand_refs.tsv
        // TODO: immuno_disease2object.tsv
        // TODO: immuno_disease2object_refs.tsv
        // TODO: interaction_affinity_refs.tsv
        // TODO: introduction.tsv
        // TODO: lgic.tsv
        // TODO: ligand2clinical_trial.tsv
        // TODO: ligand2clinical_trial_refs.tsv
        // TODO: ligand2family.tsv
        // TODO: ligand2subunit.tsv
        // TODO: ligand2synonym.tsv
        // TODO: ligand2synonym_refs.tsv
        // TODO: ligand2tcp.tsv
        // TODO: ligand2tcp_refs.tsv
        // TODO: ligand_database_link.tsv
        // TODO: list_ligand.tsv
        // TODO: malaria_stage.tsv
        // TODO: malaria_stage2interaction.tsv
        // TODO: multimer.tsv
        // TODO: mutation.tsv
        // TODO: mutation_refs.tsv
        // TODO: nhr.tsv
        // TODO: object2go_process.tsv
        // TODO: object2reaction.tsv
        // TODO: other_ic.tsv
        // TODO: other_protein.tsv
        // TODO: pathophysiology.tsv
        // TODO: pathophysiology_refs.tsv
        // TODO: pdb_structure.tsv
        // TODO: pdb_structure_refs.tsv
        // TODO: physiological_function.tsv
        // TODO: physiological_function_refs.tsv
        // TODO: precursor.tsv
        // TODO: precursor2peptide.tsv
        // TODO: precursor2synonym.tsv
        // TODO: process_assoc.tsv
        // TODO: process_assoc_refs.tsv
        // TODO: product.tsv
        // TODO: product_refs.tsv
        // TODO: reaction.tsv
        // TODO: receptor2family.tsv
        // TODO: receptor2subunit.tsv
        // TODO: receptor_basic.tsv
        // TODO: reference2immuno.tsv
        // TODO: reference2ligand.tsv
        // TODO: screen.tsv
        // TODO: screen_interaction.tsv
        // TODO: screen_refs.tsv
        // TODO: selectivity.tsv
        // TODO: selectivity_refs.tsv
        // TODO: specific_reaction.tsv
        // TODO: specific_reaction_refs.tsv
        // TODO: structural_info.tsv
        // TODO: structural_info_refs.tsv
        // TODO: substrate.tsv
        // TODO: substrate_refs.tsv
        // TODO: synonym.tsv
        // TODO: synonym_refs.tsv
        // TODO: target_candidate_profile.tsv
        // TODO: target_gene.tsv
        // TODO: target_gene_refs.tsv
        // TODO: target_ligand_same_entity.tsv
        // TODO: tissue.tsv
        // TODO: tissue_distribution.tsv
        // TODO: tissue_distribution_refs.tsv
        // TODO: transduction.tsv
        // TODO: transduction_refs.tsv
        // TODO: transporter.tsv
        // TODO: variant2database_link.tsv
        // TODO: vgic.tsv
        // TODO: voltage_dependence.tsv
        // TODO: voltage_dep_activation_refs.tsv
        // TODO: voltage_dep_deactivation_refs.tsv
        // TODO: voltage_dep_inactivation_refs.tsv
        // TODO: xenobiotic_expression.tsv
        // TODO: xenobiotic_expression_refs.tsv
        return true;
    }

    private <T> void createNodesFromTsvFile(final Workspace workspace, final Graph g, final Class<T> dataType,
                                            final String fileName) throws ExporterException {
        for (final T entry : parseTsvFile(workspace, dataType, fileName))
            g.addNodeFromModel(entry);
    }

    private <T> Iterable<T> parseTsvFile(final Workspace workspace, final Class<T> typeVariableClass,
                                         final String fileName) throws ExporterException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + fileName + "...");
        try {
            MappingIterator<T> iterator = FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource, fileName,
                                                                                    typeVariableClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private void exportDiseases(final Workspace workspace, final Graph graph) {
        final Map<Long, String> diseaseCategories = new HashMap<>();
        for (final DiseaseCategory entry : parseTsvFile(workspace, DiseaseCategory.class, "disease_category.tsv")) {
            diseaseCategories.put(entry.diseaseCategoryId, entry.name);
        }
        final Map<Long, String> diseaseIdCategoryMap = new HashMap<>();
        for (final Disease2Category entry : parseTsvFile(workspace, Disease2Category.class, "disease2category.tsv")) {
            diseaseIdCategoryMap.put(entry.diseaseId, diseaseCategories.get(entry.diseaseCategoryId));
        }
        for (final Disease entry : parseTsvFile(workspace, Disease.class, "disease.tsv")) {
            final String category = diseaseIdCategoryMap.get(entry.diseaseId);
            if (category == null)
                graph.addNodeFromModel(entry);
            else
                graph.addNodeFromModel(entry, "category", category);
        }
    }

    private void exportLigands(final Workspace workspace, final Graph graph) {
        final Map<Long, LigandStructure> ligandStructures = new HashMap<>();
        for (final LigandStructure entry : parseTsvFile(workspace, LigandStructure.class, "ligand_structure.tsv")) {
            ligandStructures.put(entry.ligandId, entry);
        }
        final Map<Long, LigandPhysChem> ligandPhysChems = new HashMap<>();
        for (final LigandPhysChem entry : parseTsvFile(workspace, LigandPhysChem.class, "ligand_physchem.tsv")) {
            ligandPhysChems.put(entry.ligandId, entry);
        }
        final Map<Long, Peptide> peptides = new HashMap<>();
        for (final Peptide entry : parseTsvFile(workspace, Peptide.class, "peptide.tsv")) {
            peptides.put(entry.ligandId, entry);
        }
        for (final Ligand entry : parseTsvFile(workspace, Ligand.class, "ligand.tsv")) {
            final NodeBuilder builder = graph.buildNode().withLabel("Ligand").withModel(entry);
            if (ligandStructures.containsKey(entry.ligandId))
                builder.withModel(ligandStructures.get(entry.ligandId));
            if (ligandPhysChems.containsKey(entry.ligandId))
                builder.withModel(ligandPhysChems.get(entry.ligandId));
            if (peptides.containsKey(entry.ligandId))
                builder.withModel(peptides.get(entry.ligandId));
            builder.build();
        }
        for (final ProDrug entry : parseTsvFile(workspace, ProDrug.class, "prodrug.tsv")) {
            final Node proDrugNode = graph.findNode("Ligand", "id", entry.prodrugLigandId);
            final Node drugNode = graph.findNode("Ligand", "id", entry.drugLigandId);
            graph.addEdge(proDrugNode, drugNode, "IS_PRODRUG_OF");
        }
        for (final Ligand2INN entry : parseTsvFile(workspace, Ligand2INN.class, "ligand2inn.tsv")) {
            final Node ligandNode = graph.findNode("Ligand", "id", entry.ligandId);
            final Node innNode = graph.findNode("INN", "id", entry.innNumber);
            graph.addEdge(ligandNode, innNode, "HAS_INN");
        }
    }

    private void exportInteractions(final Workspace workspace, final Graph graph) {
        for (final Interaction entry : parseTsvFile(workspace, Interaction.class, "interaction.tsv")) {
            final Node node = graph.addNodeFromModel(entry);
            if (entry.ligandId != null)
                graph.addEdge(graph.findNode("Ligand", "id", entry.ligandId), node, "ASSOCIATED_WITH");
            if (entry.targetLigandId != null)
                graph.addEdge(graph.findNode("Ligand", "id", entry.targetLigandId), node, "ASSOCIATED_WITH");
            if (entry.objectId != null)
                graph.addEdge(graph.findNode("Object", "id", entry.objectId), node, "ASSOCIATED_WITH");
            if (entry.speciesId != null)
                graph.addEdge(node, graph.findNode("Species", "id", entry.speciesId), "BELONGS_TO");
        }
    }

    private void exportAntibioticDB(final Workspace workspace, final Graph graph) {
        createNodesFromTsvFile(workspace, graph, AntibioticDB.class, "antibiotic_db.tsv");
        for (final Ligand2ADB entry : parseTsvFile(workspace, Ligand2ADB.class, "ligand2adb.tsv")) {
            graph.addEdge(graph.findNode("Ligand", "id", entry.ligandId),
                          graph.findNode("AntibioticDB", "id", entry.adbId), "HAS_XREF");
        }
    }
}
