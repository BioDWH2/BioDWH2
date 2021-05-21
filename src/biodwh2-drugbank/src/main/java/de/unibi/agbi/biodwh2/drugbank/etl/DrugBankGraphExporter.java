package de.unibi.agbi.biodwh2.drugbank.etl;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;
import de.unibi.agbi.biodwh2.drugbank.model.*;
import de.unibi.agbi.biodwh2.drugbank.model.MetaboliteStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DrugBankGraphExporter extends GraphExporter<DrugBankDataSource> {
    private static class DrugInteractionTriple {
        Long drugNodeId;
        String drugBankIdTarget;
        String description;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DrugBankGraphExporter.class);
    private static final String DRUGBANK_ID_KEY = "drugbank_id";
    private static final String SMPDB_ID_KEY = "smpdb_id";
    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String DESCRIPTION_KEY = "description";
    private static final String SEQUENCE_KEY = "sequence";
    private static final String ABSORPTION_KEY = "absorption";
    private static final String ACTIONS_KEY = "actions";
    private static final String AHFS_CODES_KEY = "ahfs_codes";
    private static final String ALTERNATIVE_PARENTS_KEY = "alternative_parents";
    private static final String AMINOACID_SEQUENCE_KEY = "aminoacid_sequence";
    private static final String AMINOACID_SEQUENCE_FORMAT_KEY = "aminoacid_sequence_format";
    private static final String APPROVED_KEY = "approved";
    private static final String ATC_CODES_KEY = "atc_codes";
    private static final String AVERAGE_MASS_KEY = "average_mass";
    private static final String CAS_NUMBER_KEY = "cas_number";
    private static final String CATEGORY_KEY = "category";
    private static final String CELLULAR_LOCATION_KEY = "cellular_location";
    private static final String CHROMOSOME_LOCATION_KEY = "chromosome_location";
    private static final String CLASS_KEY = "class";
    private static final String CLEARANCE_KEY = "clearance";
    private static final String COST_KEY = "cost";
    private static final String COUNTRY_KEY = "country";
    private static final String CURRENCY_KEY = "currency";
    private static final String DIRECT_PARENT_KEY = "direct_parent";
    private static final String DPD_ID_KEY = "dpd_id";
    private static final String EMA_MA_NUMBER_KEY = "ema_ma_number";
    private static final String EMA_PRODUCT_CODE_KEY = "ema_product_code";
    private static final String ENDED_MARKETING_ON_KEY = "ended_marketing_on";
    private static final String FDA_APPLICATION_NUMBER_KEY = "fda_application_number";
    private static final String FDA_LABELS_KEY = "fda_labels";
    private static final String FORMAT_KEY = "format";
    private static final String GENE_NAME_KEY = "gene_name";
    private static final String GENE_SEQUENCE_KEY = "gene_sequence";
    private static final String GENE_SEQUENCE_FORMAT_KEY = "gene_sequence_format";
    private static final String GENERAL_FUNCTION_KEY = "general_function";
    private static final String GENERIC_KEY = "generic";
    private static final String GROUP_KEY = "group";
    private static final String HALF_LIFE_KEY = "half_life";
    private static final String INCHI_KEY_KEY = "inchi_key";
    private static final String INDICATION_KEY = "indication";
    private static final String INDUCTION_STRENGTH_KEY = "induction_strength";
    private static final String INHIBITION_STRENGTH_KEY = "inhibition_strength";
    private static final String KIND_KEY = "kind";
    private static final String KINGDOM_KEY = "kingdom";
    private static final String KNOWN_ACTION_KEY = "known_action";
    private static final String LABELLER_KEY = "labeller";
    private static final String LOCUS_KEY = "locus";
    private static final String MECHANISM_OF_ACTION_KEY = "mechanism_of_action";
    private static final String METABOLISM_KEY = "metabolism";
    private static final String MOLECULAR_WEIGHT_KEY = "molecular_weight";
    private static final String MONOISOTOPIC_MASS_KEY = "monoisotopic_mass";
    private static final String MSDS_KEY = "msds";
    private static final String NDC_ID_KEY = "ndc_id";
    private static final String NDC_PRODUCT_CODE_KEY = "ndc_product_code";
    private static final String OVER_THE_COUNTER_KEY = "over_the_counter";
    private static final String PDB_ENTRIES_KEY = "pdb_entries";
    private static final String PHARMACODYNAMICS_KEY = "pharmacodynamics";
    private static final String POSITION_KEY = "position";
    private static final String PROTEIN_BINDING_KEY = "protein_binding";
    private static final String RESOURCE_KEY = "resource";
    private static final String ROUTE_KEY = "route";
    private static final String FORM_KEY = "form";
    private static final String ROUTE_OF_ELIMINATION_KEY = "route_of_elimination";
    private static final String SIGNAL_REGIONS_KEY = "signal_regions";
    private static final String SOURCE_KEY = "source";
    private static final String SPECIFIC_FUNCTION_KEY = "specific_function";
    private static final String STARTED_MARKETING_ON_KEY = "started_marketing_on";
    private static final String STATE_KEY = "state";
    private static final String STRENGTH_KEY = "strength";
    private static final String SUBCLASS_KEY = "subclass";
    private static final String SUBSTITUENTS_KEY = "substituents";
    private static final String SUPERCLASS_KEY = "superclass";
    private static final String SYNONYMS_KEY = "synonyms";
    private static final String SYNTHESIS_REFERENCE_KEY = "synthesis_reference";
    private static final String THEORETICAL_PI_KEY = "theoretical_pi";
    private static final String TOXICITY_KEY = "toxicity";
    private static final String TRANSMEMBRANE_REGIONS_KEY = "transmembrane_regions";
    private static final String UNII_KEY = "unii";
    private static final String UNIT_KEY = "unit";
    private static final String URL_KEY = "url";
    private static final String VALUE_KEY = "value";
    private static final String VOLUME_OF_DISTRIBUTION_KEY = "volume_of_distribution";
    private static final String INTERACTS_WITH_DRUG_LABEL = "INTERACTS_WITH_DRUG";
    private static final String IS_IN_PATHWAY_LABEL = "IS_IN_PATHWAY";
    private static final String IS_POLYPEPTIDE_LABEL = "IS_POLYPEPTIDE";
    private static final String HAS_ENZYME_LABEL = "HAS_ENZYME";
    private static final String ENZYME_IN_LABEL = "ENZYME_IN";
    private static final String AFFECTS_LABEL = "AFFECTS";
    private static final String CLASSIFIED_AS_LABEL = "CLASSIFIED_AS";
    private static final String COSTS_LABEL = "COSTS";
    private static final String HAS_ARTICLE_LABEL = "HAS_ARTICLE";
    private static final String HAS_ATTACHMENT_LABEL = "HAS_ATTACHMENT";
    private static final String HAS_BRAND_LABEL = "HAS_BRAND";
    private static final String HAS_CATEGORY_LABEL = "HAS_CATEGORY";
    private static final String HAS_DOSAGE_LABEL = "HAS_DOSAGE";
    private static final String HAS_EXTERNAL_IDENTIFIER_LABEL = "HAS_EXTERNAL_IDENTIFIER";
    private static final String HAS_EXTERNAL_LINK_LABEL = "HAS_EXTERNAL_LINK";
    private static final String HAS_FOOD_INTERACTION_LABEL = "HAS_FOOD_INTERACTION";
    private static final String HAS_GO_CLASSIFIER_LABEL = "HAS_GO_CLASSIFIER";
    private static final String HAS_LINK_LABEL = "HAS_LINK";
    private static final String HAS_MANUFACTURER_LABEL = "HAS_MANUFACTURER";
    private static final String HAS_ORGANISM_LABEL = "HAS_ORGANISM";
    private static final String HAS_PACKAGER_LABEL = "HAS_PACKAGER";
    private static final String IS_PRODUCT_LABEL = "IS_PRODUCT";
    private static final String HAS_PATENT_LABEL = "HAS_PATENT";
    private static final String HAS_PFAM_LABEL = "HAS_PFAM";
    private static final String HAS_PHARMACOLOGY_LABEL = "HAS_PHARMACOLOGY";
    private static final String HAS_PROPERTY_LABEL = "HAS_PROPERTY";
    private static final String HAS_SALT_LABEL = "HAS_SALT";
    private static final String HAS_SEQUENCE_LABEL = "HAS_SEQUENCE";
    private static final String HAS_SNP_ADVERSE_DRUG_REACTION_LABEL = "HAS_SNP_ADVERSE_DRUG_REACTION";
    private static final String HAS_SNP_EFFECT_LABEL = "HAS_SNP_EFFECT";
    private static final String HAS_SYNONYM_LABEL = "HAS_SYNONYM";
    private static final String HAS_TARGET_LABEL = "HAS_TARGET";
    private static final String HAS_TEXTBOOK_LABEL = "HAS_TEXTBOOK";
    private static final String IS_IN_MIXTURE_LABEL = "IS_IN_MIXTURE";
    private static final String SUBSTRATE_IN_LABEL = "SUBSTRATE_IN";
    private static final String HAS_PRODUCT_LABEL = "HAS_PRODUCT";
    private static final String TARGETS_LABEL = "TARGETS";
    private static final String ORGANISM_LABEL = "Organism";
    private static final String PATHWAY_LABEL = "Pathway";
    private static final String POLYPEPTIDE_LABEL = "Polypeptide";
    private static final String ENZYME_LABEL = "Enzyme";
    private static final String REACTION_LABEL = "Reaction";
    private static final String SALT_LABEL = "Salt";
    private static final String CALCULATED_PROPERTY_LABEL = "CalculatedProperty";
    private static final String CARRIER_LABEL = "Carrier";
    private static final String CLASSIFICATION_LABEL = "Classification";
    private static final String DRUG_LABEL = "Drug";
    private static final String EXPERIMENTAL_PROPERTY_LABEL = "ExperimentalProperty";
    private static final String EXTERNAL_IDENTIFIER_LABEL = "ExternalIdentifier";
    private static final String EXTERNAL_LINK_LABEL = "ExternalLink";
    private static final String FOOD_INTERACTION_LABEL = "FoodInteraction";
    private static final String GO_CLASSIFIER_LABEL = "GOClassifier";
    private static final String MANUFACTURER_LABEL = "Manufacturer";
    private static final String MESH_TERM_LABEL = "MeshTerm";
    private static final String PACKAGER_LABEL = "Packager";
    private static final String PFAM_LABEL = "Pfam";
    private static final String PHARMACOLOGY_LABEL = "Pharmacology";
    private static final String PRICE_LABEL = "Price";
    private static final String DOSAGE_LABEL = "Dosage";
    private static final String PRODUCT_LABEL = "Product";
    private static final String SEQUENCE_LABEL = "Sequence";
    private static final String TARGET_LABEL = "Target";
    private static final String TARGET_METADATA_LABEL = "TargetMetadata";
    private static final String TRANSPORTER_LABEL = "Transporter";

    private Map<String, Long> drugLookUp;
    private Map<String, Long> referenceLookUp;
    private Map<String, Long> meshTermLookUp;
    private Map<String, Long> foodInteractionLookUp;
    private Map<String, Long> calculatedPropertyLookUp;
    private Map<String, Long> experimentalPropertyLookUp;
    private Map<String, Long> externalIdentifierLookUp;
    private Map<Integer, Long> mixtureLookUp;
    private Map<String, Map<String, Long>> dosageLookUp;
    private List<DrugInteractionTriple> drugInteractionCache;
    private Map<Long, Set<String>> pathwayEnzymeCache;
    private Map<Long, Set<String>> pathwayDrugCache;
    private List<Reaction> reactionCache;

    public DrugBankGraphExporter(final DrugBankDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        drugLookUp = new HashMap<>();
        referenceLookUp = new HashMap<>();
        meshTermLookUp = new HashMap<>();
        foodInteractionLookUp = new HashMap<>();
        calculatedPropertyLookUp = new HashMap<>();
        experimentalPropertyLookUp = new HashMap<>();
        externalIdentifierLookUp = new HashMap<>();
        mixtureLookUp = new HashMap<>();
        dosageLookUp = new HashMap<>();
        drugInteractionCache = new LinkedList<>();
        pathwayEnzymeCache = new HashMap<>();
        pathwayDrugCache = new HashMap<>();
        reactionCache = new LinkedList<>();
        graph.setNodeIndexPropertyKeys(ID_KEY, DRUGBANK_ID_KEY, SMPDB_ID_KEY, NAME_KEY);
        createMetaboliteStructures(graph, dataSource.metaboliteStructures);
        exportDrugs(workspace, graph);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export remaining " + drugInteractionCache.size() + " drug interactions...");
        for (final DrugInteractionTriple triple : drugInteractionCache) {
            if (drugLookUp.containsKey(triple.drugBankIdTarget)) {
                final Long target = drugLookUp.get(triple.drugBankIdTarget);
                graph.addEdge(triple.drugNodeId, target, INTERACTS_WITH_DRUG_LABEL, DESCRIPTION_KEY,
                              triple.description);
            } else {
                LOGGER.warn("Missing drug with id '" + triple.drugBankIdTarget + "' for interaction will be ignored");
            }
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export remaining " + pathwayEnzymeCache.size() + " pathway relationships...");
        for (final Long pathwayNodeId : pathwayEnzymeCache.keySet()) {
            for (final String enzymeId : pathwayEnzymeCache.get(pathwayNodeId)) {
                Node polypeptideNode = graph.findNode(POLYPEPTIDE_LABEL, ID_KEY, enzymeId);
                if (polypeptideNode == null)
                    polypeptideNode = graph.addNode(POLYPEPTIDE_LABEL, ID_KEY, enzymeId);
                graph.addEdge(pathwayNodeId, polypeptideNode, HAS_ENZYME_LABEL);
            }
        }
        for (final Long pathwayNodeId : pathwayDrugCache.keySet()) {
            for (final String drugbankId : pathwayDrugCache.get(pathwayNodeId)) {
                if (drugLookUp.containsKey(drugbankId))
                    graph.addEdge(drugLookUp.get(drugbankId), pathwayNodeId, IS_IN_PATHWAY_LABEL);
                else {
                    LOGGER.warn("Drug id '" + drugbankId + "' not found for pathway '" + pathwayNodeId + "'");
                }
            }
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export " + reactionCache.size() + " reactions...");
        for (final Reaction reaction : reactionCache) {
            final Node reactionsNode;
            if (reaction.sequence != null)
                reactionsNode = graph.addNode(REACTION_LABEL, SEQUENCE_KEY, reaction.sequence);
            else
                reactionsNode = graph.addNode(REACTION_LABEL);
            Node leftNode = graph.findNode(DRUGBANK_ID_KEY, reaction.leftElement.drugbankId);
            Node rightNode = graph.findNode(DRUGBANK_ID_KEY, reaction.rightElement.drugbankId);
            if (leftNode == null) {
                if (reaction.leftElement.drugbankId.startsWith("DBMET"))
                    leftNode = graph.addNode("Metabolite", DRUGBANK_ID_KEY, reaction.leftElement.drugbankId, NAME_KEY,
                                             reaction.leftElement.name);
            }
            if (rightNode == null) {
                if (reaction.rightElement.drugbankId.startsWith("DBMET"))
                    rightNode = graph.addNode("Metabolite", DRUGBANK_ID_KEY, reaction.rightElement.drugbankId, NAME_KEY,
                                              reaction.rightElement.name);
            }
            if (leftNode != null)
                graph.addEdge(leftNode, reactionsNode, SUBSTRATE_IN_LABEL);
            else
                LOGGER.warn("Missing left reaction element with id " + reaction.rightElement.drugbankId);
            if (rightNode != null)
                graph.addEdge(reactionsNode, rightNode, HAS_PRODUCT_LABEL);
            else
                LOGGER.warn("Missing right reaction element with id " + reaction.rightElement.drugbankId);
            if (reaction.enzymes != null) {
                for (final ReactionEnzyme enzyme : reaction.enzymes) {
                    Node enzymeNode = graph.findNode(ENZYME_LABEL, ID_KEY, enzyme.drugbankId);
                    if (enzymeNode == null)
                        enzymeNode = graph.addNode(ENZYME_LABEL, ID_KEY, enzyme.drugbankId, NAME_KEY, enzyme.name);
                    Node polypeptideNode = graph.findNode(POLYPEPTIDE_LABEL, ID_KEY, enzyme.uniprotId);
                    if (polypeptideNode == null)
                        polypeptideNode = graph.addNode(POLYPEPTIDE_LABEL, ID_KEY, enzyme.uniprotId);
                    final Edge edge = graph.findEdge(IS_POLYPEPTIDE_LABEL, Edge.FROM_ID_FIELD, enzymeNode.getId(),
                                                     Edge.TO_ID_FIELD, polypeptideNode.getId());
                    if (edge == null)
                        graph.addEdge(enzymeNode, polypeptideNode, IS_POLYPEPTIDE_LABEL);
                    graph.addEdge(enzymeNode, reactionsNode, ENZYME_IN_LABEL);
                }
            }
        }
        return true;
    }

    private void createMetaboliteStructures(final Graph graph, final List<MetaboliteStructure> metabolites) {
        for (MetaboliteStructure metabolite : metabolites)
            graph.addNodeFromModel(metabolite);
    }

    private void exportDrugs(final Workspace workspace, final Graph graph) {
        final String filePath = dataSource.resolveSourceFilePath(workspace, "drugbank_all_full_database.xml.zip");
        final File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ExporterException("Failed to find file 'drugbank_all_full_database.xml.zip'");
        try {
            final ZipInputStream zipInputStream = openZipInputStream(zipFile);
            int counter = 1;
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (isZipEntryCoreXml(zipEntry.getName())) {
                    final XmlMapper xmlMapper = new XmlMapper();
                    final FromXmlParser parser = createXmlParser(zipInputStream, xmlMapper);
                    // Skip the first structure token which is the root DrugBank node
                    //noinspection UnusedAssignment
                    JsonToken token = parser.nextToken();
                    while ((token = parser.nextToken()) != null)
                        if (token.isStructStart()) {
                            if (counter % 250 == 0 && LOGGER.isInfoEnabled())
                                LOGGER.info("Exporting drug progress " + counter);
                            counter++;
                            exportDrug(graph, xmlMapper.readValue(parser, Drug.class));
                        }
                }
            }
        } catch (IOException | XMLStreamException e) {
            throw new ExporterFormatException("Failed to parse the file 'drugbank_all_full_database.xml.zip'", e);
        }
    }

    private static ZipInputStream openZipInputStream(final File file) throws FileNotFoundException {
        final FileInputStream inputStream = new FileInputStream(file);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        return new ZipInputStream(bufferedInputStream);
    }

    private static boolean isZipEntryCoreXml(final String name) {
        return name.startsWith("full") && name.endsWith(".xml");
    }

    private FromXmlParser createXmlParser(final InputStream stream,
                                          final XmlMapper xmlMapper) throws IOException, XMLStreamException {
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        final XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(stream,
                                                                                   StandardCharsets.UTF_8.name());
        return xmlMapper.getFactory().createParser(streamReader);
    }

    private void exportDrug(final Graph graph, final Drug drug) {
        final Node drugNode = createDrugNode(graph, drug, drugLookUp);
        addDrugSalts(graph, drug, drugNode);
        addDrugExternalIdentifiers(graph, drug, drugNode);
        addDrugExternalLinks(graph, drug, drugNode);
        createReferenceListNode(graph, drugNode, drug.generalReferences);
        addDrugSynonyms(graph, drug, drugNode);
        addDrugBrands(graph, drug, drugNode);
        addDrugMixtures(graph, drug, drugNode);
        addDrugSnpEffects(graph, drug, drugNode);
        addDrugSnpAdverseDrugReactions(graph, drug, drugNode);
        addDrugFoodInteractions(graph, drug, drugNode);
        addDrugSequences(graph, drug, drugNode);
        addDrugExperimentalProperties(graph, drug, drugNode);
        addDrugCalculatedProperties(graph, drug, drugNode);
        addDrugAffectedOrganisms(graph, drug, drugNode);
        addDrugCategories(graph, drug, drugNode);
        addDrugInteractants(graph, drug, drugNode);
        addDrugClassification(graph, drug, drugNode);
        addPharmacology(graph, drug, drugNode);
        addPharmacoeconomics(graph, drug, drugNode);
        addOrCacheDrugInteractions(graph, drug, drugNode);
        addOrCacheDrugPathways(graph, drug, drugNode);
        if (drug.reactions != null)
            reactionCache.addAll(drug.reactions);
    }

    private Node createDrugNode(final Graph graph, final Drug drug, final Map<String, Long> drugLookUp) {
        final NodeBuilder drugBuilder = graph.buildNode().withLabel(DRUG_LABEL);
        drugBuilder.withProperty(DRUGBANK_ID_KEY, getPrimaryOrFirstDrugBankId(drug).value);
        drugBuilder.withProperty(NAME_KEY, drug.name);
        drugBuilder.withProperty(DESCRIPTION_KEY, drug.description);
        drugBuilder.withProperty(GROUP_KEY, drug.groups.stream().map(Group::toValue).toArray(String[]::new));
        drugBuilder.withPropertyIfNotNull(AHFS_CODES_KEY, drug.ahfsCodes);
        drugBuilder.withPropertyIfNotNull(PDB_ENTRIES_KEY, drug.pdbEntries);
        drugBuilder.withPropertyIfNotNull(FDA_LABELS_KEY, drug.fdaLabel);
        drugBuilder.withPropertyIfNotNull(MSDS_KEY, drug.msds);
        if (drug.atcCodes != null) {
            final String[] atcCodes = new String[drug.atcCodes.size()];
            for (int i = 0; i < atcCodes.length; i++)
                atcCodes[i] = drug.atcCodes.get(i).code;
            drugBuilder.withProperty(ATC_CODES_KEY, atcCodes);
        }
        drugBuilder.withPropertyIfNotNull(CAS_NUMBER_KEY, drug.casNumber);
        drugBuilder.withPropertyIfNotNull(UNII_KEY, drug.unii);
        drugBuilder.withPropertyIfNotNull(AVERAGE_MASS_KEY, drug.averageMass);
        drugBuilder.withPropertyIfNotNull(MONOISOTOPIC_MASS_KEY, drug.monoisotopicMass);
        if (drug.state != null)
            drugBuilder.withPropertyIfNotNull(STATE_KEY, drug.state.value);
        drugBuilder.withPropertyIfNotNull(SYNTHESIS_REFERENCE_KEY, drug.synthesisReference);
        final Node drugNode = drugBuilder.build();
        for (final DrugbankDrugSaltId id : drug.drugbankIds)
            drugLookUp.put(id.value, drugNode.getId());
        return drugNode;
    }

    private DrugbankDrugSaltId getPrimaryOrFirstDrugBankId(final Drug drug) {
        for (final DrugbankDrugSaltId id : drug.drugbankIds)
            if (id.primary)
                return id;
        return drug.drugbankIds.size() > 0 ? drug.drugbankIds.get(0) : null;
    }

    private void addDrugSalts(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.salts != null)
            for (final Salt salt : drug.salts)
                addDrugSalt(graph, salt, drugNode);
    }

    private void addDrugSalt(final Graph graph, final Salt salt, final Node drugNode) {
        final NodeBuilder builder = graph.buildNode().withLabel(SALT_LABEL);
        builder.withProperty(DRUGBANK_ID_KEY, salt.drugbankId.value);
        builder.withPropertyIfNotNull(NAME_KEY, salt.name);
        builder.withPropertyIfNotNull(UNII_KEY, salt.unii);
        builder.withPropertyIfNotNull(CAS_NUMBER_KEY, salt.casNumber);
        builder.withPropertyIfNotNull(INCHI_KEY_KEY, salt.inchikey);
        builder.withPropertyIfNotNull(AVERAGE_MASS_KEY, salt.averageMass);
        builder.withPropertyIfNotNull(MONOISOTOPIC_MASS_KEY, salt.monoisotopicMass);
        final Node saltNode = builder.build();
        graph.addEdge(drugNode, saltNode, HAS_SALT_LABEL);
    }

    private void addDrugExternalIdentifiers(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.externalIdentifiers != null)
            for (final ExternalIdentifier identifier : drug.externalIdentifiers) {
                final Long nodeId = getOrCreateExternalIdentifierNode(graph, identifier.resource.value,
                                                                      identifier.identifier);
                graph.addEdge(drugNode, nodeId, HAS_EXTERNAL_IDENTIFIER_LABEL);
            }
    }

    private Long getOrCreateExternalIdentifierNode(final Graph graph, final String resource, final String identifier) {
        final String key = resource + ":" + identifier;
        Long nodeId = externalIdentifierLookUp.get(key);
        if (nodeId == null) {
            nodeId = graph.addNode(EXTERNAL_IDENTIFIER_LABEL, RESOURCE_KEY, resource, ID_KEY, identifier).getId();
            externalIdentifierLookUp.put(key, nodeId);
        }
        return nodeId;
    }

    private void addDrugExternalLinks(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.externalLinks != null)
            for (final ExternalLink link : drug.externalLinks) {
                final Node node = graph.addNode(EXTERNAL_LINK_LABEL, URL_KEY, link.url, RESOURCE_KEY,
                                                link.resource.value);
                graph.addEdge(drugNode, node, HAS_EXTERNAL_LINK_LABEL);
            }
    }

    private void addDrugSynonyms(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.synonyms != null)
            for (final Synonym synonym : drug.synonyms) {
                final Node node = graph.addNodeFromModel(synonym);
                graph.addEdge(drugNode, node, HAS_SYNONYM_LABEL);
            }
    }

    private void addDrugBrands(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.internationalBrands != null)
            for (final InternationalBrand brand : drug.internationalBrands) {
                final Node node = graph.addNodeFromModel(brand);
                graph.addEdge(drugNode, node, HAS_BRAND_LABEL);
            }
    }

    private void addDrugMixtures(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.mixtures != null)
            for (final Mixture mixture : drug.mixtures) {
                final int hash = mixture.hashCode();
                Long nodeId = mixtureLookUp.get(hash);
                if (nodeId == null) {
                    nodeId = graph.addNodeFromModel(mixture).getId();
                    mixtureLookUp.put(hash, nodeId);
                }
                graph.addEdge(drugNode, nodeId, IS_IN_MIXTURE_LABEL);
            }
    }

    private void addDrugSnpEffects(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.snpEffects != null)
            for (final SnpEffect snpEffect : drug.snpEffects) {
                final Node node = graph.addNodeFromModel(snpEffect);
                graph.addEdge(drugNode, node, HAS_SNP_EFFECT_LABEL);
            }
    }

    private void addDrugSnpAdverseDrugReactions(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.snpAdverseDrugReactions != null)
            for (final SnpAdverseDrugReaction snpAdverseDrugReaction : drug.snpAdverseDrugReactions) {
                final Node node = graph.addNodeFromModel(snpAdverseDrugReaction);
                graph.addEdge(drugNode, node, HAS_SNP_ADVERSE_DRUG_REACTION_LABEL);
            }
    }

    private void addDrugFoodInteractions(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.foodInteractions != null)
            for (final String interaction : drug.foodInteractions) {
                if (!foodInteractionLookUp.containsKey(interaction)) {
                    final Node node = graph.addNode(FOOD_INTERACTION_LABEL, DESCRIPTION_KEY, interaction);
                    foodInteractionLookUp.put(interaction, node.getId());
                }
                graph.addEdge(drugNode, foodInteractionLookUp.get(interaction), HAS_FOOD_INTERACTION_LABEL);
            }
    }

    private void addDrugSequences(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.sequences != null)
            for (final Sequence sequence : drug.sequences) {
                final Node node = graph.addNode(SEQUENCE_LABEL, VALUE_KEY, sequence.value, FORMAT_KEY, sequence.format);
                graph.addEdge(drugNode, node, HAS_SEQUENCE_LABEL);
            }
    }

    private void addDrugExperimentalProperties(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.experimentalProperties != null)
            for (final ExperimentalProperty property : drug.experimentalProperties) {
                final String key = property.kind + ";" + property.value;
                if (!experimentalPropertyLookUp.containsKey(key)) {
                    final Node node = graph.addNode(EXPERIMENTAL_PROPERTY_LABEL, VALUE_KEY, property.value, KIND_KEY,
                                                    property.kind.value);
                    experimentalPropertyLookUp.put(key, node.getId());
                }
                if (property.source != null)
                    graph.addEdge(drugNode, experimentalPropertyLookUp.get(key), HAS_PROPERTY_LABEL, SOURCE_KEY,
                                  property.source);
                else
                    graph.addEdge(drugNode, experimentalPropertyLookUp.get(key), HAS_PROPERTY_LABEL);
            }
    }

    private void addDrugCalculatedProperties(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.calculatedProperties != null)
            for (final CalculatedProperty property : drug.calculatedProperties) {
                final String key = property.kind + ";" + property.value;
                if (!calculatedPropertyLookUp.containsKey(key)) {
                    final Node node = graph.addNode(CALCULATED_PROPERTY_LABEL, VALUE_KEY, property.value, KIND_KEY,
                                                    property.kind.value);
                    calculatedPropertyLookUp.put(key, node.getId());
                }
                if (property.source != null)
                    graph.addEdge(drugNode, calculatedPropertyLookUp.get(key), HAS_PROPERTY_LABEL, SOURCE_KEY,
                                  property.source.value);
                else
                    graph.addEdge(drugNode, calculatedPropertyLookUp.get(key), HAS_PROPERTY_LABEL);
            }
    }

    private void addDrugAffectedOrganisms(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.affectedOrganisms != null)
            for (final String organism : drug.affectedOrganisms) {
                final Long affectedOrganismNodeId = updateOrCreateOrganism(graph, organism, null);
                graph.addEdge(drugNode, affectedOrganismNodeId, AFFECTS_LABEL);
            }
    }

    private Long updateOrCreateOrganism(final Graph graph, final String name, final String ncbiTaxonomyId) {
        Node foundNode = null;
        if (ncbiTaxonomyId != null)
            foundNode = graph.findNode(ORGANISM_LABEL, ID_KEY, ncbiTaxonomyId);
        if (foundNode == null && name != null)
            foundNode = graph.findNode(ORGANISM_LABEL, NAME_KEY, name);
        if (foundNode == null) {
            if (name != null && ncbiTaxonomyId != null)
                foundNode = graph.addNode(ORGANISM_LABEL, ID_KEY, ncbiTaxonomyId, NAME_KEY, name);
            else if (name != null)
                foundNode = graph.addNode(ORGANISM_LABEL, NAME_KEY, name);
            else
                foundNode = graph.addNode(ORGANISM_LABEL, ID_KEY, ncbiTaxonomyId);
        } else {
            boolean changed = false;
            if (foundNode.getProperty(ID_KEY) == null && ncbiTaxonomyId != null) {
                foundNode.setProperty(ID_KEY, ncbiTaxonomyId);
                changed = true;
            }
            if (foundNode.getProperty(NAME_KEY) == null && name != null) {
                foundNode.setProperty(NAME_KEY, name);
                changed = true;
            }
            if (changed)
                graph.update(foundNode);
        }
        return foundNode.getId();
    }

    private void addDrugCategories(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.categories != null)
            for (final Category category : drug.categories) {
                if (!meshTermLookUp.containsKey(category.meshId)) {
                    final Node node = graph.addNode(MESH_TERM_LABEL, ID_KEY, category.meshId, NAME_KEY,
                                                    category.category);
                    meshTermLookUp.put(category.meshId, node.getId());
                }
                graph.addEdge(drugNode, meshTermLookUp.get(category.meshId), HAS_CATEGORY_LABEL);
            }
    }

    private void addDrugInteractants(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.targets != null)
            for (final Target target : drug.targets) {
                final Node node = getOrCreateInteractantNode(graph, target, TARGET_LABEL);
                connectInteractant(graph, drugNode, target, node);
            }
        if (drug.enzymes != null)
            for (final Enzyme enzyme : drug.enzymes) {
                final Node node = getOrCreateInteractantNode(graph, enzyme, ENZYME_LABEL);
                connectInteractant(graph, drugNode, enzyme, node);
            }
        if (drug.carriers != null)
            for (final Carrier carrier : drug.carriers) {
                final Node node = getOrCreateInteractantNode(graph, carrier, CARRIER_LABEL);
                connectInteractant(graph, drugNode, carrier, node);
            }
        if (drug.transporters != null)
            for (final Transporter transporter : drug.transporters) {
                final Node node = getOrCreateInteractantNode(graph, transporter, TRANSPORTER_LABEL);
                connectInteractant(graph, drugNode, transporter, node);
            }
    }

    private Node getOrCreateInteractantNode(final Graph graph, final Interactant interactant, final String label) {
        Node node = graph.findNode(label, ID_KEY, interactant.id);
        if (node == null) {
            node = graph.addNode(label, ID_KEY, interactant.id, NAME_KEY, interactant.name);
            if (interactant.polypeptide != null) {
                Node polypeptideNode = graph.findNode(POLYPEPTIDE_LABEL, ID_KEY, interactant.polypeptide.id);
                if (polypeptideNode == null)
                    polypeptideNode = createPolypeptideNode(graph, interactant.polypeptide);
                graph.addEdge(node, polypeptideNode, IS_POLYPEPTIDE_LABEL);
            }
            if (interactant.organism != null) {
                final Long organismNodeId = updateOrCreateOrganism(graph, interactant.organism, null);
                graph.addEdge(node, organismNodeId, HAS_ORGANISM_LABEL);
            }
        }
        return node;
    }

    private Node createPolypeptideNode(final Graph graph, final Polypeptide polypeptide) {
        final NodeBuilder builder = graph.buildNode().withLabel(POLYPEPTIDE_LABEL);
        builder.withPropertyIfNotNull(ID_KEY, polypeptide.id);
        builder.withPropertyIfNotNull(NAME_KEY, polypeptide.name);
        builder.withPropertyIfNotNull(SOURCE_KEY, polypeptide.source);
        builder.withPropertyIfNotNull(GENERAL_FUNCTION_KEY, polypeptide.generalFunction);
        builder.withPropertyIfNotNull(SPECIFIC_FUNCTION_KEY, polypeptide.specificFunction);
        builder.withPropertyIfNotNull(GENE_NAME_KEY, polypeptide.geneName);
        builder.withPropertyIfNotNull(LOCUS_KEY, polypeptide.locus);
        builder.withPropertyIfNotNull(CELLULAR_LOCATION_KEY, polypeptide.cellularLocation);
        builder.withPropertyIfNotNull(TRANSMEMBRANE_REGIONS_KEY, polypeptide.transmembraneRegions);
        builder.withPropertyIfNotNull(SIGNAL_REGIONS_KEY, polypeptide.signalRegions);
        builder.withPropertyIfNotNull(THEORETICAL_PI_KEY, polypeptide.theoreticalPi);
        builder.withPropertyIfNotNull(MOLECULAR_WEIGHT_KEY, polypeptide.molecularWeight);
        builder.withPropertyIfNotNull(CHROMOSOME_LOCATION_KEY, polypeptide.chromosomeLocation);
        builder.withPropertyIfNotNull(AMINOACID_SEQUENCE_KEY, polypeptide.aminoAcidSequence.value);
        builder.withPropertyIfNotNull(AMINOACID_SEQUENCE_FORMAT_KEY, polypeptide.aminoAcidSequence.format);
        builder.withPropertyIfNotNull(GENE_SEQUENCE_KEY, polypeptide.geneSequence.value);
        builder.withPropertyIfNotNull(GENE_SEQUENCE_FORMAT_KEY, polypeptide.geneSequence.format);
        builder.withPropertyIfNotNull(SYNONYMS_KEY, polypeptide.synonyms);
        final Node polypeptideNode = builder.build();
        if (polypeptide.externalIdentifiers != null) {
            for (final PolypeptideExternalIdentifier identifier : polypeptide.externalIdentifiers) {
                final Long nodeId = getOrCreateExternalIdentifierNode(graph, identifier.resource.value,
                                                                      identifier.identifier);
                graph.addEdge(polypeptideNode, nodeId, HAS_EXTERNAL_IDENTIFIER_LABEL);
            }
        }
        if (polypeptide.pfams != null) {
            for (final Pfam pfam : polypeptide.pfams) {
                Node pfamNode = graph.findNode(PFAM_LABEL, ID_KEY, pfam.identifier);
                if (pfamNode == null)
                    pfamNode = graph.addNode(PFAM_LABEL, ID_KEY, pfam.identifier, NAME_KEY, pfam.name);
                graph.addEdge(polypeptideNode, pfamNode, HAS_PFAM_LABEL);
            }
        }
        if (polypeptide.goClassifiers != null) {
            for (final GoClassifier classifier : polypeptide.goClassifiers) {
                final Node node = graph.addNode(GO_CLASSIFIER_LABEL, CATEGORY_KEY, classifier.category, DESCRIPTION_KEY,
                                                classifier.description);
                graph.addEdge(polypeptideNode, node, HAS_GO_CLASSIFIER_LABEL);
            }
        }
        if (polypeptide.organism != null) {
            final Long organismNodeId = updateOrCreateOrganism(graph, polypeptide.organism.value,
                                                               polypeptide.organism.ncbiTaxonomyId);
            graph.addEdge(polypeptideNode, organismNodeId, HAS_ORGANISM_LABEL);
        }
        return polypeptideNode;
    }

    private void connectInteractant(final Graph graph, final Node drugNode, final Interactant interactant,
                                    final Node interactantNode) {
        final NodeBuilder builder = graph.buildNode().withLabel(TARGET_METADATA_LABEL);
        builder.withPropertyIfNotNull(KNOWN_ACTION_KEY, interactant.knownAction.value);
        builder.withPropertyIfNotNull(ACTIONS_KEY, interactant.actions);
        builder.withPropertyIfNotNull(POSITION_KEY, interactant.position);
        if (interactant instanceof Enzyme) {
            builder.withPropertyIfNotNull(INHIBITION_STRENGTH_KEY, ((Enzyme) interactant).inhibitionStrength);
            builder.withPropertyIfNotNull(INDUCTION_STRENGTH_KEY, ((Enzyme) interactant).inductionStrength);
        }
        final Node metadataNode = builder.build();
        createReferenceListNode(graph, metadataNode, interactant.references);
        graph.addEdge(drugNode, metadataNode, TARGETS_LABEL);
        graph.addEdge(metadataNode, interactantNode, HAS_TARGET_LABEL);
    }

    private void addDrugClassification(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.classification == null)
            return;
        final NodeBuilder builder = graph.buildNode().withLabel(CLASSIFICATION_LABEL);
        builder.withPropertyIfNotNull(DESCRIPTION_KEY, drug.classification.description);
        builder.withPropertyIfNotNull(DIRECT_PARENT_KEY, drug.classification.directParent);
        builder.withPropertyIfNotNull(KINGDOM_KEY, drug.classification.kingdom);
        builder.withPropertyIfNotNull(SUPERCLASS_KEY, drug.classification.superclass);
        builder.withPropertyIfNotNull(CLASS_KEY, drug.classification.class_);
        builder.withPropertyIfNotNull(SUBCLASS_KEY, drug.classification.subclass);
        builder.withPropertyIfNotNull(ALTERNATIVE_PARENTS_KEY, drug.classification.alternativeParents);
        builder.withPropertyIfNotNull(SUBSTITUENTS_KEY, drug.classification.substituents);
        if (builder.getPropertyCount() > 0)
            graph.addEdge(drugNode, builder.build(), CLASSIFIED_AS_LABEL);
    }

    private void addPharmacology(final Graph graph, final Drug drug, final Node drugNode) {
        final NodeBuilder builder = graph.buildNode().withLabel(PHARMACOLOGY_LABEL);
        builder.withPropertyIfNotNull(MECHANISM_OF_ACTION_KEY, drug.mechanismOfAction);
        builder.withPropertyIfNotNull(TOXICITY_KEY, drug.toxicity);
        builder.withPropertyIfNotNull(METABOLISM_KEY, drug.metabolism);
        builder.withPropertyIfNotNull(ABSORPTION_KEY, drug.absorption);
        builder.withPropertyIfNotNull(INDICATION_KEY, drug.indication);
        builder.withPropertyIfNotNull(PHARMACODYNAMICS_KEY, drug.pharmacodynamics);
        builder.withPropertyIfNotNull(HALF_LIFE_KEY, drug.halfLife);
        builder.withPropertyIfNotNull(PROTEIN_BINDING_KEY, drug.proteinBinding);
        builder.withPropertyIfNotNull(ROUTE_OF_ELIMINATION_KEY, drug.routeOfElimination);
        builder.withPropertyIfNotNull(VOLUME_OF_DISTRIBUTION_KEY, drug.volumeOfDistribution);
        builder.withPropertyIfNotNull(CLEARANCE_KEY, drug.clearance);
        if (builder.getPropertyCount() > 0)
            graph.addEdge(drugNode, builder.build(), HAS_PHARMACOLOGY_LABEL);
    }

    private void createReferenceListNode(final Graph graph, final Node parent, final ReferenceList references) {
        if (references == null)
            return;
        if (isListNullOrEmpty(references.textbooks) && isListNullOrEmpty(references.articles) && isListNullOrEmpty(
                references.attachments) && isListNullOrEmpty(references.links))
            return;
        if (isListNotEmpty(references.textbooks)) {
            for (final Textbook reference : references.textbooks) {
                if (!referenceLookUp.containsKey(reference.refId)) {
                    final Node node = graph.addNodeFromModel(reference);
                    referenceLookUp.put(reference.refId, node.getId());
                }
                graph.addEdge(parent, referenceLookUp.get(reference.refId), HAS_TEXTBOOK_LABEL);
            }
        }
        if (isListNotEmpty(references.articles)) {
            for (final Article reference : references.articles) {
                if (!referenceLookUp.containsKey(reference.refId)) {
                    final Node node = graph.addNodeFromModel(reference);
                    referenceLookUp.put(reference.refId, node.getId());
                }
                graph.addEdge(parent, referenceLookUp.get(reference.refId), HAS_ARTICLE_LABEL);
            }
        }
        if (isListNotEmpty(references.links)) {
            for (final Link reference : references.links) {
                if (!referenceLookUp.containsKey(reference.refId)) {
                    final Node node = graph.addNodeFromModel(reference);
                    referenceLookUp.put(reference.refId, node.getId());
                }
                graph.addEdge(parent, referenceLookUp.get(reference.refId), HAS_LINK_LABEL);
            }
        }
        if (isListNotEmpty(references.attachments)) {
            for (final Attachment reference : references.attachments) {
                if (!referenceLookUp.containsKey(reference.refId)) {
                    final Node node = graph.addNodeFromModel(reference);
                    referenceLookUp.put(reference.refId, node.getId());
                }
                graph.addEdge(parent, referenceLookUp.get(reference.refId), HAS_ATTACHMENT_LABEL);
            }
        }
    }

    private boolean isListNullOrEmpty(final List<?> list) {
        return list == null || list.size() == 0;
    }

    private boolean isListNotEmpty(final List<?> list) {
        return list != null && list.size() > 0;
    }

    private void addPharmacoeconomics(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.products != null) {
            for (final Product product : drug.products) {
                final NodeBuilder productBuilder = graph.buildNode().withLabel(PRODUCT_LABEL);
                productBuilder.withPropertyIfNotNull(NAME_KEY, product.name);
                productBuilder.withPropertyIfNotNull(LABELLER_KEY, product.labeller);
                productBuilder.withPropertyIfNotNull(NDC_ID_KEY, product.ndcId);
                productBuilder.withPropertyIfNotNull(NDC_PRODUCT_CODE_KEY, product.ndcProductCode);
                productBuilder.withPropertyIfNotNull(DPD_ID_KEY, product.dpdId);
                productBuilder.withPropertyIfNotNull(EMA_PRODUCT_CODE_KEY, product.emaProductCode);
                productBuilder.withPropertyIfNotNull(EMA_MA_NUMBER_KEY, product.emaMaNumber);
                productBuilder.withPropertyIfNotNull(STARTED_MARKETING_ON_KEY, product.startedMarketingOn);
                productBuilder.withPropertyIfNotNull(ENDED_MARKETING_ON_KEY, product.endedMarketingOn);
                productBuilder.withPropertyIfNotNull(FDA_APPLICATION_NUMBER_KEY, product.fdaApplicationNumber);
                productBuilder.withPropertyIfNotNull(GENERIC_KEY, product.generic);
                productBuilder.withPropertyIfNotNull(OVER_THE_COUNTER_KEY, product.overTheCounter);
                productBuilder.withPropertyIfNotNull(APPROVED_KEY, product.approved);
                productBuilder.withPropertyIfNotNull(COUNTRY_KEY, product.country.value);
                productBuilder.withPropertyIfNotNull(SOURCE_KEY, product.source.value);
                final Node productNode = productBuilder.build();
                graph.addEdge(drugNode, productNode, IS_PRODUCT_LABEL);
                final Long dosageNodeId = getOrCreateDosageNode(graph, product.dosageForm, product.route,
                                                                product.strength);
                graph.addEdge(productNode, dosageNodeId, HAS_DOSAGE_LABEL);
            }
        }
        if (drug.packagers != null)
            for (final Packager packager : drug.packagers) {
                Node node = graph.findNode(PACKAGER_LABEL, NAME_KEY, packager.name);
                if (node == null)
                    node = graph.addNode(PACKAGER_LABEL, NAME_KEY, packager.name, URL_KEY, packager.url);
                graph.addEdge(drugNode, node, HAS_PACKAGER_LABEL);
            }
        if (drug.manufacturers != null)
            for (final Manufacturer manufacturer : drug.manufacturers) {
                Node node = graph.findNode(MANUFACTURER_LABEL, NAME_KEY, manufacturer.value);
                if (node == null)
                    node = graph.addNode(MANUFACTURER_LABEL, NAME_KEY, manufacturer.value, URL_KEY, manufacturer.url);
                graph.addEdge(drugNode, node, HAS_MANUFACTURER_LABEL);
            }
        if (drug.prices != null)
            for (final Price price : drug.prices) {
                final Node node;
                if (price.unit != null)
                    node = graph.addNode(PRICE_LABEL, DESCRIPTION_KEY, price.description, COST_KEY, price.cost.value,
                                         CURRENCY_KEY, price.cost.currency, UNIT_KEY, price.unit);
                else
                    node = graph.addNode(PRICE_LABEL, DESCRIPTION_KEY, price.description, COST_KEY, price.cost.value,
                                         CURRENCY_KEY, price.cost.currency);
                graph.addEdge(drugNode, node, COSTS_LABEL);
            }
        if (drug.dosages != null)
            for (final Dosage dosage : drug.dosages) {
                final Long dosageNodeId = getOrCreateDosageNode(graph, dosage.form, dosage.route, dosage.strength);
                graph.addEdge(drugNode, dosageNodeId, HAS_DOSAGE_LABEL);
            }
        if (drug.patents != null)
            for (final Patent patent : drug.patents) {
                final Node node = graph.addNodeFromModel(patent);
                graph.addEdge(drugNode, node, HAS_PATENT_LABEL);
            }
    }

    private Long getOrCreateDosageNode(final Graph graph, final String form, final String route,
                                       final String strength) {
        final String formRouteKey = (form != null ? form : "") + "|" + (route != null ? route : "");
        final Map<String, Long> strengthNodeIdMap = dosageLookUp.computeIfAbsent(formRouteKey, k -> new HashMap<>());
        final String strengthKey = strength != null ? strength : "";
        final Long nodeId = strengthNodeIdMap.get(strengthKey);
        if (nodeId != null)
            return nodeId;
        return graph.buildNode().withLabel(DOSAGE_LABEL).withPropertyIfNotNull(FORM_KEY, form).withPropertyIfNotNull(
                ROUTE_KEY, route).withPropertyIfNotNull(STRENGTH_KEY, strength).build().getId();
    }

    private void addOrCacheDrugInteractions(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.drugInteractions == null)
            return;
        for (final DrugInteraction interaction : drug.drugInteractions) {
            if (drugLookUp.containsKey(interaction.drugbankId.value)) {
                final Long target = drugLookUp.get(interaction.drugbankId.value);
                graph.addEdge(drugNode, target, INTERACTS_WITH_DRUG_LABEL, DESCRIPTION_KEY, interaction.description);
            } else {
                final DrugInteractionTriple triple = new DrugInteractionTriple();
                triple.drugNodeId = drugNode.getId();
                triple.drugBankIdTarget = interaction.drugbankId.value;
                triple.description = interaction.description;
                drugInteractionCache.add(triple);
            }
        }
    }

    private void addOrCacheDrugPathways(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.pathways == null)
            return;
        for (final Pathway pathway : drug.pathways) {
            Node pathwayNode = graph.findNode(PATHWAY_LABEL, SMPDB_ID_KEY, pathway.smpdbId);
            if (pathwayNode == null) {
                pathwayNode = graph.addNode(PATHWAY_LABEL, NAME_KEY, pathway.name, SMPDB_ID_KEY, pathway.smpdbId,
                                            CATEGORY_KEY, pathway.category);
            }
            if (!pathwayDrugCache.containsKey(pathwayNode.getId()))
                pathwayDrugCache.put(pathwayNode.getId(), new HashSet<>());
            pathwayDrugCache.get(pathwayNode.getId()).add(drugNode.getProperty(DRUGBANK_ID_KEY));
            if (pathway.enzymes != null) {
                if (!pathwayEnzymeCache.containsKey(pathwayNode.getId()))
                    pathwayEnzymeCache.put(pathwayNode.getId(), new HashSet<>());
                pathwayEnzymeCache.get(pathwayNode.getId()).addAll(pathway.enzymes);
            }
            for (final PathwayDrug pathwayDrug : pathway.drugs)
                pathwayDrugCache.get(pathwayNode.getId()).add(pathwayDrug.drugbankId.value);
        }
    }
}
