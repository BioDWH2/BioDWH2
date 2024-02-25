package de.unibi.agbi.biodwh2.iid.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.iid.IIDDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class IIDGraphExporter extends GraphExporter<IIDDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(IIDGraphExporter.class);
    static final String PROTEIN_LABEL = "Protein";
    static final String INTERACTS_WITH_LABEL = "INTERACTS_WITH";

    private static final Set<String> SPECIES_HEADER_KEYS = Set.of("alpaca", "cat", "chicken", "cow", "dog", "duck",
                                                                  "fly", "guinea_pig", "horse", "human", "mouse", "pig",
                                                                  "rabbit", "rat", "sheep", "turkey", "worm", "yeast");
    private static final Set<String> CC_HEADER_KEYS = Set.of("Golgi apparatus", "cytoplasm", "cytoskeleton",
                                                             "endoplasmic reticulum", "extracellular space",
                                                             "mitochondrion", "nuclear matrix", "nucleolus",
                                                             "nucleoplasm", "nucleus", "peroxisome", "plasma membrane",
                                                             "vacuole", "cartilage", "ovary", "oocyte", "kidney",
                                                             "liver", "lung", "lymph node", "pancreas", "spleen",
                                                             "adipose tissue", "eye", "hindgut", "midgut", "neurons",
                                                             "salivary gland", "testes", "alveolar macrophage",
                                                             "adrenal gland", "amygdala", "bone", "bone marrow",
                                                             "dorsal root ganglia", "hypothalamus", "lymph nodes",
                                                             "mammary gland", "pituitary gland", "placenta", "prostate",
                                                             "small intestine", "stomach", "uterus", "blastocyst",
                                                             "brain", "heart", "synovial macrophages", "chondrocytes",
                                                             "growth plate cartilage", "synovial membrane",
                                                             "articular cartilage", "cerebral nuclei", "basal ganglia",
                                                             "cerebral cortex", "superior frontal gyrus",
                                                             "frontal lobe", "fusiform gyrus, left", "fusiform gyrus",
                                                             "gyrus rectus", "dorsal thalamus", "thalamus",
                                                             "diencephalon", "parietal lobe",
                                                             "superior parietal lobule, left",
                                                             "superior parietal lobule", "head of the caudate nucleus",
                                                             "lateral group of nuclei, ventral division",
                                                             "lateral group of nuclei", "precuneus", "precuneus, left",
                                                             "superior frontal gyrus, left",
                                                             "middle temporal gyrus, left", "middle temporal gyrus",
                                                             "temporal lobe", "cingulate gyrus, frontal part, left",
                                                             "cingulate gyrus, frontal part", "cingulate gyrus",
                                                             "limbic lobe", "basal part of pons",
                                                             "inferior temporal gyrus, left", "inferior temporal gyrus",
                                                             "middle frontal gyrus, left", "middle frontal gyrus",
                                                             "dentate gyrus", "inferior olivary complex",
                                                             "medial orbital gyrus", "postcentral gyrus",
                                                             "postcentral gyrus, left", "skeletal muscle",
                                                             "occipital lobe", "lingual gyrus, left", "lingual gyrus",
                                                             "superior temporal gyrus", "superior temporal gyrus, left",
                                                             "parahippocampal gyrus, left", "parahippocampal gyrus",
                                                             "body of the caudate nucleus", "caudate nucleus",
                                                             "hippocampal formation", "subiculum", "striatum",
                                                             "telencephalon", "putamen", "metencephalon",
                                                             "pontine nuclei", "pons", "spinal trigeminal nucleus",
                                                             "myelencephalon", "claustrum", "midbrain tegmentum",
                                                             "substantia nigra", "substantia nigra, left",
                                                             "mesencephalon", "morula", "jejunum", "putamen, left",
                                                             "superior frontal gyrus, left, medial bank of gyrus",
                                                             "middle temporal gyrus, left, inferior bank of gyrus",
                                                             "superior frontal gyrus, left, lateral bank of gyrus",
                                                             "middle temporal gyrus, left, superior bank of gyrus",
                                                             "cingulate gyrus, frontal part, left, inferior bank of gyrus",
                                                             "pontine nuclei, left", "spinal trigeminal nucleus, left",
                                                             "claustrum, left",
                                                             "inferior temporal gyrus, left, bank of the its",
                                                             "middle frontal gyrus, left, superior bank of gyrus",
                                                             "cingulate gyrus, frontal part, left, superior bank of gyrus",
                                                             "inferior temporal gyrus, left, bank of mts",
                                                             "middle frontal gyrus, left, inferior bank of gyrus",
                                                             "lingual gyrus, left, peristriate",
                                                             "superior temporal gyrus, left, inferior bank of gyrus",
                                                             "superior temporal gyrus, left, lateral bank of gyrus",
                                                             "parahippocampal gyrus, left, lateral bank of gyrus",
                                                             "body of caudate nucleus, left",
                                                             "lingual gyrus, left, striate", "subiculum, left",
                                                             "inferior temporal gyrus, left, lateral bank of gyrus",
                                                             "superior parietal lobule, left, inferior bank of gyrus",
                                                             "head of caudate nucleus, left",
                                                             "precuneus, left, inferior lateral bank of gyrus",
                                                             "dentate gyrus, left", "inferior olivary complex, left",
                                                             "medial orbital gyrus, left",
                                                             "postcentral gyrus, left, superior lateral aspect of gyrus",
                                                             "substantia nigra, pars compacta, left",
                                                             "fusiform gyrus, left, bank of the its",
                                                             "gyrus rectus, left",
                                                             "lateral group of nuclei, left, ventral division",
                                                             "parahippocampal gyrus, left, bank of the cos",
                                                             "precuneus, left, superior lateral bank of gyrus",
                                                             "adipose MSCs", "bone marrow MSCs", "CA1 field",
                                                             "CA1 field, left", "CA3 field", "CA3 field, left",
                                                             "CA4 field", "CA4 field, left", "adipose_tissue");
    private static final Set<String> CANCER_HEADER_KEYS = Set.of("breast_cancer", "breast_carcinoma", "prostate_cancer",
                                                                 "prostate_carcinoma", "stomach_cancer",
                                                                 "large_intestine_cancer", "colorectal_cancer",
                                                                 "ovarian_cancer", "melanoma", "malignant_glioma",
                                                                 "lung_carcinoma", "stomach_carcinoma",
                                                                 "non_small_cell_lung_carcinoma",
                                                                 "urinary_bladder_cancer", "colon_carcinoma",
                                                                 "squamous_cell_carcinoma", "colon_cancer",
                                                                 "glioblastoma_multiforme", "pancreatic_carcinoma",
                                                                 "adenocarcinoma", "liver_cancer", "neuroblastoma",
                                                                 "multiple_myeloma", "lymphoma", "cancer",
                                                                 "thoracic_cancer", "organ_system_cancer",
                                                                 "male_reproductive_organ_cancer",
                                                                 "reproductive_organ_cancer",
                                                                 "gastrointestinal_system_cancer", "intestinal_cancer",
                                                                 "female_reproductive_organ_cancer", "cell_type_cancer",
                                                                 "respiratory_system_cancer", "lung_cancer",
                                                                 "immune_system_cancer", "hematologic_cancer",
                                                                 "urinary_system_cancer", "carcinoma", "astrocytoma",
                                                                 "pancreatic_cancer", "endocrine_gland_cancer",
                                                                 "autonomic_nervous_system_neoplasm",
                                                                 "peripheral_nervous_system_neoplasm",
                                                                 "nervous_system_cancer", "bone_marrow_cancer",
                                                                 "myeloid_neoplasm");
    private static final Set<String> DISEASE_HEADER_KEYS = Set.of("alzheimer_s_disease", "leukemia",
                                                                  "coronary_artery_disease", "rheumatoid_arthritis",
                                                                  "acute_myeloid_leukemia", "schizophrenia", "obesity",
                                                                  "type_2_diabetes_mellitus", "diabetes_mellitus",
                                                                  "asthma", "atherosclerosis", "hypertension",
                                                                  "chronic_lymphocytic_leukemia", "tauopathy",
                                                                  "nervous_system_disease", "neurodegenerative_disease",
                                                                  "central_nervous_system_disease",
                                                                  "disease_of_anatomical_entity", "artery_disease",
                                                                  "cardiovascular_system_disease", "vascular_disease",
                                                                  "bone_disease", "bone_inflammation_disease",
                                                                  "arthritis", "connective_tissue_disease",
                                                                  "musculoskeletal_system_disease", "myeloid_leukemia",
                                                                  "cognitive_disorder", "psychotic_disorder",
                                                                  "disease_of_mental_health", "overnutrition",
                                                                  "acquired_metabolic_disease", "disease_of_metabolism",
                                                                  "nutrition_disease", "glucose_metabolism_disease",
                                                                  "carbohydrate_metabolism_disease",
                                                                  "lower_respiratory_tract_disease",
                                                                  "respiratory_system_disease", "lung_disease",
                                                                  "bronchial_disease", "obstructive_lung_disease",
                                                                  "arteriosclerotic_cardiovascular_disease",
                                                                  "arteriosclerosis", "lymphocytic_leukemia");
    private static final Set<String> DRUG_HEADER_KEYS = Set.of("targeted by drugs", "drugs targeting both proteins",
                                                               "drugs targeting one or both proteins",
                                                               "orthologs targeted by drugs",
                                                               "drugs targeting orthologs of both proteins",
                                                               "drugs targeting orthologs of one or both proteins");
    private static final Set<String> EMBRYO_HEADER_KEYS = Set.of("1 cell embryo", "2 cell embryo", "4 cell embryo",
                                                                 "8 cell embryo", "16 cell embryo");
    private static final Set<String> MUTATION_HEADER_KEYS = Set.of("causing mutations", "decreasing mutations",
                                                                   "decreasing rate mutations",
                                                                   "decreasing strength mutations",
                                                                   "disrupting mutations", "disrupting rate mutations",
                                                                   "disrupting strength mutations",
                                                                   "increasing mutations", "increasing rate mutations",
                                                                   "increasing strength mutations",
                                                                   "no effect mutations", "unknown effect mutations");
    private static final Set<String> IGNORED_HEADER_KEYS = Set.of("# of causing mutations", "# of decreasing mutations",
                                                                  "# of decreasing rate mutations",
                                                                  "# of decreasing strength mutations",
                                                                  "# of disrupting mutations",
                                                                  "# of disrupting rate mutations",
                                                                  "# of disrupting strength mutations",
                                                                  "# of increasing mutations",
                                                                  "# of increasing rate mutations",
                                                                  "# of increasing strength mutations",
                                                                  "# of no effect mutations",
                                                                  "# of unknown effect mutations", "directed",
                                                                  "bidirected", "in_complex");

    public IIDGraphExporter(final IIDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.beginEdgeIndicesDelay(INTERACTS_WITH_LABEL);
        final Map<String, Long> uniprotIdNodeIdMap = new HashMap<>();
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.ALPACA_PPI_FILE_NAME,
                      SpeciesLookup.VICUGNA_PACOS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.CAT_PPI_FILE_NAME,
                      SpeciesLookup.FELIS_CATUS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.CHICKEN_PPI_FILE_NAME,
                      SpeciesLookup.GALLUS_GALLUS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.COW_PPI_FILE_NAME,
                      SpeciesLookup.BOS_TAURUS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.DOG_PPI_FILE_NAME,
                      SpeciesLookup.CANIS_FAMILIARIS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.DUCK_PPI_FILE_NAME,
                      SpeciesLookup.ANAS_PLATYRHYNCHOS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.FLY_PPI_FILE_NAME,
                      SpeciesLookup.DROSOPHILA_MELANOGASTER.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.GUINEA_PIG_PPI_FILE_NAME,
                      SpeciesLookup.CAVIA_PORCELLUS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.HORSE_PPI_FILE_NAME,
                      SpeciesLookup.EQUUS_CABALLUS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.HUMAN_PPI_FILE_NAME,
                      SpeciesLookup.HOMO_SAPIENS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.MOUSE_PPI_FILE_NAME,
                      SpeciesLookup.MUS_MUSCULUS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.PIG_PPI_FILE_NAME,
                      SpeciesLookup.SUS_SCROFA.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.RABBIT_PPI_FILE_NAME,
                      SpeciesLookup.ORYCTOLAGUS_CUNICULUS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.RAT_PPI_FILE_NAME,
                      SpeciesLookup.RATTUS_NORVEGICUS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.SHEEP_PPI_FILE_NAME,
                      SpeciesLookup.OVIS_ARIES.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.TURKEY_PPI_FILE_NAME,
                      SpeciesLookup.MELEAGRIS_GALLOPAVO.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.WORM_PPI_FILE_NAME,
                      SpeciesLookup.CAENORHABDITIS_ELEGANS.ncbiTaxId);
        exportSpecies(workspace, graph, uniprotIdNodeIdMap, IIDUpdater.YEAST_PPI_FILE_NAME,
                      SpeciesLookup.SACCHAROMYCES_CEREVISIAE.ncbiTaxId);
        graph.endEdgeIndicesDelay(INTERACTS_WITH_LABEL);
        return true;
    }

    private void exportSpecies(final Workspace workspace, final Graph graph, final Map<String, Long> uniprotIdNodeIdMap,
                               final String fileName, final Integer taxonId) {
        if (!speciesFilter.isSpeciesAllowed(taxonId))
            return;
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting file '" + fileName + "'...");
        try (final var iterator = FileUtils.openGzipTsv(workspace, dataSource, fileName, String[].class)) {
            final String[] headers = iterator.next();
            while (iterator.hasNext())
                exportInteraction(graph, uniprotIdNodeIdMap, headers, iterator.next());
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export file '" + fileName + "'", e);
        }
    }

    private void exportInteraction(final Graph graph, final Map<String, Long> uniprotIdNodeIdMap,
                                   final String[] headers, String[] values) {
        final Long proteinNodeId1 = getOrCreateProtein(graph, uniprotIdNodeIdMap, values[0], values[2]);
        final Long proteinNodeId2 = getOrCreateProtein(graph, uniprotIdNodeIdMap, values[1], values[3]);
        final EdgeBuilder builder = graph.buildEdge(INTERACTS_WITH_LABEL);
        addEdgeArrayPropertyIfNotEmpty(builder, "methods", values[4]);
        addEdgeArrayPropertyIfNotEmpty(builder, "pmids", values[5]);
        addEdgeArrayPropertyIfNotEmpty(builder, "db_with_ppi", values[6]);
        addEdgeArrayPropertyIfNotEmpty(builder, "evidence_type", values[7]);
        addEdgeIntegerPropertyIfNotEmpty(builder, "n_exp_pmids", values[9]);
        addEdgeIntegerPropertyIfNotEmpty(builder, "n_pred_pmids", values[10]);
        addEdgeIntegerPropertyIfNotEmpty(builder, "n_pmids", values[11]);
        String direction = "-";
        for (int i = 15; i < headers.length; i++) {
            final String header = headers[i].trim();
            final String value = values[i].trim();
            if (SPECIES_HEADER_KEYS.contains(header)) {
                addEdgeIntegerPropertyIfNotEmpty(builder, convertHeaderToPropertyKey(header), value);
            } else if (CANCER_HEADER_KEYS.contains(header)) {
                addEdgeIntegerPropertyIfNotEmpty(builder, convertHeaderToPropertyKey(header), value);
            } else if (DISEASE_HEADER_KEYS.contains(header)) {
                addEdgeIntegerPropertyIfNotEmpty(builder, convertHeaderToPropertyKey(header), value);
            } else if (CC_HEADER_KEYS.contains(header)) {
                addEdgeIntegerPropertyIfNotEmpty(builder, convertHeaderToPropertyKey(header), value);
            } else if (DRUG_HEADER_KEYS.contains(header)) {
                addEdgeArrayPropertyIfNotEmpty(builder, convertHeaderToPropertyKey(header), value);
            } else if (MUTATION_HEADER_KEYS.contains(header)) {
                addEdgeArrayPropertyIfNotEmpty(builder, convertHeaderToPropertyKey(header), value);
            } else if (EMBRYO_HEADER_KEYS.contains(header)) {
                addEdgeIntegerPropertyIfNotEmpty(builder, '_' + convertHeaderToPropertyKey(header), value);
            } else if (!IGNORED_HEADER_KEYS.contains(header)) {
                switch (header) {
                    case "enzymes":
                    case "ion channels":
                    case "receptors":
                    case "transporters":
                        addEdgeIntegerPropertyIfNotEmpty(builder, convertHeaderToPropertyKey(header), value);
                        break;
                    case "stable":
                    case "transient":
                        addEdgeIntegerPropertyIfNotEmpty(builder, header, value);
                        break;
                    case "mutation effects summary":
                        if (StringUtils.isNotEmpty(value) && !"-".equals(value))
                            builder.withProperty("mutation_effects_summary", value);
                        break;
                    case "complexes with both proteins":
                    case "complexes with one or both proteins":
                    case "direction information":
                        addEdgeArrayPropertyIfNotEmpty(builder, convertHeaderToPropertyKey(header), value);
                        break;
                    case "directions":
                        direction = value;
                        break;
                }

            }
        }
        if ("-".equals(direction) || ">".equals(direction) || "><".equals(direction)) {
            builder.fromNode(proteinNodeId1).toNode(proteinNodeId2);
            builder.build();
        }
        if ("<".equals(direction) || "><".equals(direction)) {
            builder.fromNode(proteinNodeId2).toNode(proteinNodeId1);
            builder.build();
        }
    }

    private Long getOrCreateProtein(final Graph graph, final Map<String, Long> uniprotIdNodeIdMap,
                                    final String uniprotId, final String symbol) {
        Long nodeId = uniprotIdNodeIdMap.get(uniprotId);
        if (nodeId == null) {
            nodeId = graph.addNode(PROTEIN_LABEL, ID_KEY, uniprotId, "symbol", symbol).getId();
            uniprotIdNodeIdMap.put(uniprotId, nodeId);
        }
        return nodeId;
    }

    private void addEdgeArrayPropertyIfNotEmpty(final EdgeBuilder builder, final String key, final String value) {
        if (StringUtils.isNotEmpty(value) && !"-".equals(value))
            builder.withProperty(key, StringUtils.split(value, '|'));
    }

    private void addEdgeIntegerPropertyIfNotEmpty(final EdgeBuilder builder, final String key, final String value) {
        if (StringUtils.isNotEmpty(value) && !"-".equals(value))
            builder.withProperty(key, Integer.parseInt(value));
    }

    private String convertHeaderToPropertyKey(final String header) {
        return StringUtils.replace(header.toLowerCase(Locale.ROOT), " ", "_");
    }
}
