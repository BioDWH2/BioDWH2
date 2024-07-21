package de.unibi.agbi.biodwh2.drugcentral.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.drugcentral.DrugCentralDataSource;
import de.unibi.agbi.biodwh2.drugcentral.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DrugCentralGraphExporter extends GraphExporter<DrugCentralDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(DrugCentralGraphExporter.class);
    static final String DRUG_LABEL_LABEL = "DrugLabel";
    public static final String REFERENCE_LABEL = "Reference";
    static final String ATTRIBUTE_TYPE_LABEL = "AttributeType";
    public static final String FAERS_LABEL = "FAERS";
    static final String INN_STEM_LABEL = "InnStem";
    public static final String VET_PROD_LABEL = "VetProd";
    public static final String VET_OMOP_LABEL = "VetOmop";
    static final String ORANGE_BOOK_EXCLUSIVITY_LABEL = "OrangeBookExclusivity";
    public static final String ORANGE_BOOK_PRODUCT_LABEL = "OrangeBookProduct";
    static final String STRUCTURE_LABEL = "Structure";
    static final String TARGET_KEYWORD_LABEL = "TargetKeyword";
    static final String ACTION_TYPE_LABEL = "ActionType";
    public static final String PDB_LABEL = "PDB";
    public static final String PARENT_DRUG_MOLECULE_LABEL = "ParentDrugMolecule";
    public static final String OMOP_CONCEPT_LABEL = "OMOPConcept";
    public static final String ACTIVE_INGREDIENT_LABEL = "ActiveIngredient";
    public static final String TARGET_COMPONENT_LABEL = "TargetComponent";

    public DrugCentralGraphExporter(final DrugCentralDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 5;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph g) throws ExporterException {
        final boolean skipLINCSSignatures = dataSource.getBooleanProperty(workspace, "skipLINCSSignatures");
        final boolean skipFAERSReports = dataSource.getBooleanProperty(workspace, "skipFAERSReports");
        final boolean skipDrugLabelFullTexts = dataSource.getBooleanProperty(workspace, "skipDrugLabelFullTexts");
        g.addIndex(IndexDescription.forNode(DRUG_LABEL_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        g.addIndex(IndexDescription.forNode(REFERENCE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        g.addIndex(IndexDescription.forNode(ATTRIBUTE_TYPE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        g.addIndex(IndexDescription.forNode(FAERS_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        g.addIndex(IndexDescription.forNode(INN_STEM_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        g.addIndex(IndexDescription.forNode(ORANGE_BOOK_EXCLUSIVITY_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        g.addIndex(IndexDescription.forNode(ORANGE_BOOK_PRODUCT_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        g.addIndex(IndexDescription.forNode(STRUCTURE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        g.addIndex(IndexDescription.forNode(TARGET_KEYWORD_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        g.addIndex(IndexDescription.forNode(INN_STEM_LABEL, "stem", IndexDescription.Type.UNIQUE));
        g.addIndex(IndexDescription.forNode(VET_PROD_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        g.addIndex(IndexDescription.forNode(VET_OMOP_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        // "ddi_risk", "approval_type", "target_class", "ref_type", "protein_type", "ijc_connect_items",
        // "ijc_connect_structures", "struct_type_def", "doid" are ignored because no necessary additional info is included
        createNodesFromTsvFile(workspace, g, DataSource.class, "data_source.tsv");
        createNodesFromTsvFile(workspace, g, DbVersion.class, "dbversion.tsv");
        createNodesFromTsvFile(workspace, g, AttributeType.class, "attr_type.tsv");
        createNodesFromTsvFile(workspace, g, InnStem.class, "inn_stem.tsv");
        createNodesFromTsvFile(workspace, g, IdType.class, "id_type.tsv");
        createNodesFromTsvFile(workspace, g, ActionType.class, "action_type.tsv");
        createNodesFromTsvFile(workspace, g, Reference.class, "reference.tsv");
        /*
        omop_relationship
            LEFT JOIN (
                    SELECT doid_xref.xref, string_agg((doid_xref.doid)::text, ','::text) AS doid
                    FROM public.doid_xref
                    WHERE ((doid_xref.source)::text ~~ 'SNOMED%'::text)
                    GROUP BY doid_xref.xref) d
            ON ((omop_relationship.snomed_conceptid = (d.xref)::bigint)))
         */
        final var snomedConceptIdToDOIdMap = new HashMap<Long, List<String>>();
        for (final DoidXref xref : parseTsvFile(workspace, DoidXref.class, "doid_xref.tsv")) {
            if (!xref.source.startsWith("SNOMED"))
                continue;
            final var snomedConceptId = Long.parseLong(xref.xref);
            var doidList = snomedConceptIdToDOIdMap.computeIfAbsent(snomedConceptId, k -> new ArrayList<>());
            doidList.add(xref.doid);
        }
        final Map<Long, Long> structureIdNodeIdMap = addStructuresWithType(workspace, g);
        for (final Approval approval : parseTsvFile(workspace, Approval.class, "approval.tsv")) {
            final Node approvalNode = g.addNodeFromModel(approval);
            g.addEdge(structureIdNodeIdMap.get(approval.structId), approvalNode, "HAS_APPROVAL");
        }
        addAtcCodeHierarchy(workspace, g, structureIdNodeIdMap);
        for (final Identifier identifier : parseTsvFile(workspace, Identifier.class, "identifier.tsv")) {
            final Node identifierNode = g.addNodeFromModel(identifier);
            g.addEdge(structureIdNodeIdMap.get(identifier.structId), identifierNode, "HAS_IDENTIFIER");
        }
        addStructureProperties(workspace, g, structureIdNodeIdMap);
        addStructurePharmaClasses(workspace, g, structureIdNodeIdMap);
        addOrangeBookPatentProducts(workspace, g, structureIdNodeIdMap);
        addOMOPRelationships(workspace, g, structureIdNodeIdMap, snomedConceptIdToDOIdMap);
        addDrugClassesAndDrugInteractions(workspace, g, structureIdNodeIdMap);
        addPDBEntries(workspace, g, structureIdNodeIdMap);
        addParentDrugMoleculesAndSynonyms(workspace, g, structureIdNodeIdMap);
        addProductsWithLabelsAndIngredients(workspace, g, structureIdNodeIdMap, skipDrugLabelFullTexts);
        addTargets(workspace, g, structureIdNodeIdMap);
        addVetProducts(workspace, g, structureIdNodeIdMap);
        if (!skipFAERSReports)
            addFAERSEntries(workspace, g, structureIdNodeIdMap);
        if (!skipLINCSSignatures)
            addLINCSSignatures(workspace, g, structureIdNodeIdMap);
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
            LOGGER.info("Exporting {}...", fileName);
        try {
            MappingIterator<T> iterator = FileUtils.openTsvWithHeaderWithoutQuoting(workspace, dataSource, fileName,
                                                                                    typeVariableClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private Map<Long, Long> addStructuresWithType(final Workspace workspace, final Graph g) throws ExporterException {
        final Map<Long, Humanim> structureHumanimMap = new HashMap<>();
        for (final Humanim entry : parseTsvFile(workspace, Humanim.class, "humanim.tsv")) {
            structureHumanimMap.put(entry.structId, entry);
        }
        final Map<Long, List<String>> structureIdTypeMap = new HashMap<>();
        for (final StructureType structureType : parseTsvFile(workspace, StructureType.class, "structure_type.tsv")) {
            if (!structureIdTypeMap.containsKey(structureType.structId))
                structureIdTypeMap.put(structureType.structId, new ArrayList<>());
            structureIdTypeMap.get(structureType.structId).add(structureType.type);
        }
        final Map<Long, Pka> structureIdPkaMap = new HashMap<>();
        for (final Pka pka : parseTsvFile(workspace, Pka.class, "pka.tsv"))
            structureIdPkaMap.put(pka.structId, pka);
        final Map<Long, Long> structureIdNodeIdMap = new HashMap<>();
        for (final Structure structure : parseTsvFile(workspace, Structure.class, "structures.tsv")) {
            final Map<String, Object> additionalProperties = new HashMap<>();
            final List<String> types = structureIdTypeMap.get(structure.id);
            if (types != null)
                additionalProperties.put("types", types.toArray(new String[0]));
            final Pka pka = structureIdPkaMap.get(structure.id);
            if (pka != null) {
                additionalProperties.put("pka_level", pka.pkaLevel);
                additionalProperties.put("pka_type", pka.pkaType);
                additionalProperties.put("pka_value", pka.value);
            }
            final Humanim humanim = structureHumanimMap.get(structure.id);
            if (humanim != null) {
                additionalProperties.put("human", "t".equalsIgnoreCase(humanim.human));
                additionalProperties.put("animal", "t".equalsIgnoreCase(humanim.animal));
            }
            final Node node = g.addNodeFromModel(structure, additionalProperties);
            structureIdNodeIdMap.put(structure.id, node.getId());
            if (structure.stem != null)
                g.addEdge(node, g.findNode("InnStem", "stem", structure.stem), "HAS_INN_STEM");
        }
        return structureIdNodeIdMap;
    }

    private void addAtcCodeHierarchy(final Workspace workspace, final Graph g,
                                     final Map<Long, Long> structureIdNodeIdMap) throws ExporterException {
        final Map<String, Long> atcCodeNodeIdMap = new HashMap<>();
        for (final Atc atc : parseTsvFile(workspace, Atc.class, "atc.tsv")) {
            final Node node = g.addNodeFromModel(atc, "level", 5);
            atcCodeNodeIdMap.put(atc.code, node.getId());
            Long l1NodeId = atcCodeNodeIdMap.get(atc.l1Code);
            if (l1NodeId == null) {
                l1NodeId = g.addNode("ATC", "level", 1, "code", atc.l1Code, "name", atc.l1Name).getId();
                atcCodeNodeIdMap.put(atc.l1Code, l1NodeId);
            }
            Long l2NodeId = atcCodeNodeIdMap.get(atc.l2Code);
            if (l2NodeId == null) {
                l2NodeId = g.addNode("ATC", "level", 2, "code", atc.l2Code, "name", atc.l2Name).getId();
                atcCodeNodeIdMap.put(atc.l2Code, l2NodeId);
                g.addEdge(l2NodeId, l1NodeId, "HAS_PARENT");
            }
            Long l3NodeId = atcCodeNodeIdMap.get(atc.l3Code);
            if (l3NodeId == null) {
                l3NodeId = g.addNode("ATC", "level", 3, "code", atc.l3Code, "name", atc.l3Name).getId();
                atcCodeNodeIdMap.put(atc.l3Code, l3NodeId);
                g.addEdge(l3NodeId, l2NodeId, "HAS_PARENT");
            }
            Long l4NodeId = atcCodeNodeIdMap.get(atc.l4Code);
            if (l4NodeId == null) {
                l4NodeId = g.addNode("ATC", "level", 4, "code", atc.l4Code, "name", atc.l4Name).getId();
                atcCodeNodeIdMap.put(atc.l4Code, l4NodeId);
                g.addEdge(l4NodeId, l3NodeId, "HAS_PARENT");
            }
            g.addEdge(node, l4NodeId, "HAS_PARENT");
        }
        for (final Struct2Atc struct2Atc : parseTsvFile(workspace, Struct2Atc.class, "struct2atc.tsv"))
            g.addEdge(structureIdNodeIdMap.get(struct2Atc.structId), atcCodeNodeIdMap.get(struct2Atc.atcCode),
                      "HAS_ATC");
        for (final AtcDdd atcDdd : parseTsvFile(workspace, AtcDdd.class, "atc_ddd.tsv")) {
            final Node atcDddNode = g.addNodeFromModel(atcDdd);
            g.addEdge(atcCodeNodeIdMap.get(atcDdd.atcCode), atcDddNode, "HAS_DDD");
            g.addEdge(structureIdNodeIdMap.get(atcDdd.structId), atcDddNode, "HAS_DDD");
        }
    }

    private void addStructureProperties(final Workspace workspace, final Graph g,
                                        final Map<Long, Long> structureIdNodeIdMap) throws ExporterException {
        final Map<Integer, PropertyType> propertyTypeMap = new HashMap<>();
        for (final PropertyType type : parseTsvFile(workspace, PropertyType.class, "property_type.tsv"))
            propertyTypeMap.put(type.id, type);
        for (final Property property : parseTsvFile(workspace, Property.class, "property.tsv")) {
            final PropertyType type = propertyTypeMap.get(property.propertyTypeId);
            final Node node = g.addNodeFromModel(property, "type_category", type.category, "type_name", type.name,
                                                 "type_units", type.units);
            g.addEdge(structureIdNodeIdMap.get(property.structId), node, "HAS_PROPERTY");
            g.addEdge(node, g.findNode(REFERENCE_LABEL, ID_KEY, property.referenceId), "HAS_REFERENCE");
        }
    }

    private void addStructurePharmaClasses(final Workspace workspace, final Graph g,
                                           final Map<Long, Long> structureIdNodeIdMap) throws ExporterException {
        final Map<String, Long> classCodeNodeIdMap = new HashMap<>();
        for (final PharmaClass pharmaClass : parseTsvFile(workspace, PharmaClass.class, "pharma_class.tsv")) {
            if (!classCodeNodeIdMap.containsKey(pharmaClass.classCode)) {
                final Node node = g.addNodeFromModel(pharmaClass);
                classCodeNodeIdMap.put(pharmaClass.classCode, node.getId());
            }
            g.addEdge(structureIdNodeIdMap.get(pharmaClass.structId), classCodeNodeIdMap.get(pharmaClass.classCode),
                      "BELONGS_TO");
        }
    }

    private void addOrangeBookPatentProducts(final Workspace workspace, final Graph g,
                                             final Map<Long, Long> structureIdNodeIdMap) throws ExporterException {
        final Map<String, String> useCodeDescriptionMap = new HashMap<>();
        for (final ObPatentUseCode useCode : parseTsvFile(workspace, ObPatentUseCode.class, "ob_patent_use_code.tsv"))
            useCodeDescriptionMap.put(useCode.code, useCode.description);
        final Map<String, List<Long>> applicationProductNoPatentNodeIdsMap = new HashMap<>();
        for (final ObPatent patent : parseTsvFile(workspace, ObPatent.class, "ob_patent.tsv")) {
            final Node node;
            if (patent.patentUseCode != null)
                node = g.addNodeFromModel(patent, "patent_use", useCodeDescriptionMap.get(patent.patentUseCode));
            else
                node = g.addNodeFromModel(patent);
            final String applicationProductNo = patent.applNo + "_" + patent.productNo;
            if (!applicationProductNoPatentNodeIdsMap.containsKey(applicationProductNo))
                applicationProductNoPatentNodeIdsMap.put(applicationProductNo, new ArrayList<>());
            applicationProductNoPatentNodeIdsMap.get(applicationProductNo).add(node.getId());
        }
        useCodeDescriptionMap.clear();
        final Map<String, String> exclusivityCodeDescriptionMap = new HashMap<>();
        for (final ObExclusivityCode exclusivityCode : parseTsvFile(workspace, ObExclusivityCode.class,
                                                                    "ob_exclusivity_code.tsv"))
            exclusivityCodeDescriptionMap.put(exclusivityCode.code, exclusivityCode.description);
        final Map<String, List<Long>> applicationProductNoExclusivityNodeIdsMap = new HashMap<>();
        for (final ObExclusivity exclusivity : parseTsvFile(workspace, ObExclusivity.class, "ob_exclusivity.tsv")) {
            final Node node;
            if (exclusivity.exclusivityCode != null)
                node = g.addNodeFromModel(exclusivity, "exclusivity",
                                          exclusivityCodeDescriptionMap.get(exclusivity.exclusivityCode));
            else
                node = g.addNodeFromModel(exclusivity);
            final String applicationProductNo = exclusivity.applNo + "_" + exclusivity.productNo;
            if (!applicationProductNoExclusivityNodeIdsMap.containsKey(applicationProductNo))
                applicationProductNoExclusivityNodeIdsMap.put(applicationProductNo, new ArrayList<>());
            applicationProductNoExclusivityNodeIdsMap.get(applicationProductNo).add(node.getId());
        }
        exclusivityCodeDescriptionMap.clear();
        final Map<Integer, Long> productIdNodeIdMap = new HashMap<>();
        for (final ObProduct product : parseTsvFile(workspace, ObProduct.class, "ob_product.tsv")) {
            final Node node = g.addNodeFromModel(product);
            productIdNodeIdMap.put(product.id, node.getId());
            final String applicationProductNo = product.applNo + "_" + product.productNo;
            if (applicationProductNoPatentNodeIdsMap.containsKey(applicationProductNo))
                for (final Long nodeId : applicationProductNoPatentNodeIdsMap.get(applicationProductNo))
                    g.addEdge(node, nodeId, "BELONGS_TO");
            if (applicationProductNoExclusivityNodeIdsMap.containsKey(applicationProductNo))
                for (final Long nodeId : applicationProductNoExclusivityNodeIdsMap.get(applicationProductNo))
                    g.addEdge(node, nodeId, "HAS_EXCLUSIVITY");
        }
        for (final Struct2ObProd link : parseTsvFile(workspace, Struct2ObProd.class, "struct2obprod.tsv"))
            g.addEdge(structureIdNodeIdMap.get(link.structId), productIdNodeIdMap.get(link.prodId), "HAS_PRODUCT",
                      "strength", link.strength);
    }

    private void addOMOPRelationships(final Workspace workspace, final Graph g,
                                      final Map<Long, Long> structureIdNodeIdMap,
                                      final Map<Long, List<String>> snomedConceptIdToDOIdMap) throws ExporterException {
        final Map<String, Long> conceptKeyNodeIdMap = new HashMap<>();
        for (final OmopRelationship relationship : parseTsvFile(workspace, OmopRelationship.class,
                                                                "omop_relationship.tsv")) {
            final String key =
                    relationship.conceptId + "_" + relationship.umlsCui + "_" + relationship.cuiSemanticType + "_" +
                    relationship.snomedConceptId;
            if (!conceptKeyNodeIdMap.containsKey(key)) {
                final var conceptNode = g.addNodeFromModel(relationship);
                if (relationship.snomedConceptId != null) {
                    final var doids = snomedConceptIdToDOIdMap.get(relationship.snomedConceptId);
                    if (doids != null) {
                        for (final var doid : doids) {
                            final Long termNodeId = getOrCreateOntologyProxyTerm(g, doid);
                            if (termNodeId != null)
                                g.addEdge(conceptNode, termNodeId, "HAS_DO_TERM");
                        }
                    }
                }
                conceptKeyNodeIdMap.put(key, conceptNode.getId());
            }
            final String edgeLabel = relationship.relationshipName.replaceAll("[- ]", "_").toUpperCase(Locale.US);
            g.addEdge(structureIdNodeIdMap.get(relationship.structId), conceptKeyNodeIdMap.get(key), edgeLabel);
        }
    }

    private void addDrugClassesAndDrugInteractions(final Workspace workspace, final Graph g,
                                                   final Map<Long, Long> structureIdNodeIdMap) throws ExporterException {
        final Map<Integer, Long> classIdNodeIdMap = new HashMap<>();
        final Map<String, Long> classNameNodeIdMap = new HashMap<>();
        for (final DrugClass drugClass : parseTsvFile(workspace, DrugClass.class, "drug_class.tsv")) {
            final Long nodeId = g.addNodeFromModel(drugClass).getId();
            classIdNodeIdMap.put(drugClass.id, nodeId);
            classNameNodeIdMap.put(drugClass.name, nodeId);
        }
        for (final Struct2DrgClass link : parseTsvFile(workspace, Struct2DrgClass.class, "struct2drgclass.tsv"))
            g.addEdge(structureIdNodeIdMap.get(link.structId), classIdNodeIdMap.get(link.drugClassId), "BELONGS_TO");
        final Map<String, Object> ddiProperties = new HashMap<>();
        for (final Ddi interaction : parseTsvFile(workspace, Ddi.class, "ddi.tsv")) {
            ddiProperties.put("description", interaction.description);
            ddiProperties.put("risk", interaction.ddiRisk);
            ddiProperties.put("source", interaction.sourceId);
            g.addEdge(classNameNodeIdMap.get(interaction.drugClass1), classNameNodeIdMap.get(interaction.drugClass2),
                      "INTERACTS", ddiProperties);
        }
    }

    private void addPDBEntries(final Workspace workspace, final Graph g,
                               final Map<Long, Long> structureIdNodeIdMap) throws ExporterException {
        for (final Pdb pdb : parseTsvFile(workspace, Pdb.class, "pdb.tsv")) {
            final Node node = g.addNodeFromModel(pdb);
            g.addEdge(structureIdNodeIdMap.get(pdb.structId), node, "BINDS_PROTEIN");
        }
    }

    private void addParentDrugMoleculesAndSynonyms(final Workspace workspace, final Graph g,
                                                   final Map<Long, Long> structureIdNodeIdMap) throws ExporterException {
        final Map<Long, Long> parentIdNodeIdMap = new HashMap<>();
        for (final Parentmol parent : parseTsvFile(workspace, Parentmol.class, "parentmol.tsv"))
            parentIdNodeIdMap.put(parent.cdId, g.addNodeFromModel(parent).getId());
        for (final Struct2Parent link : parseTsvFile(workspace, Struct2Parent.class, "struct2parent.tsv"))
            g.addEdge(structureIdNodeIdMap.get(link.structId), parentIdNodeIdMap.get(link.parentId), "HAS_PARENT");
        for (final Synonym synonym : parseTsvFile(workspace, Synonym.class, "synonyms.tsv")) {
            final Node node = g.addNodeFromModel(synonym);
            if (synonym.id != null)
                g.addEdge(structureIdNodeIdMap.get(synonym.id), node, "HAS_SYNONYM");
            if (synonym.parentId != null)
                g.addEdge(parentIdNodeIdMap.get(synonym.parentId), node, "HAS_SYNONYM");
        }
    }

    private void addProductsWithLabelsAndIngredients(final Workspace workspace, final Graph g,
                                                     final Map<Long, Long> structureIdNodeIdMap,
                                                     final boolean skipDrugLabelFullTexts) throws ExporterException {
        final Map<String, Long> labelIdNodeIdMap = new HashMap<>();
        for (final Label label : parseTsvFile(workspace, Label.class, "label.tsv"))
            labelIdNodeIdMap.put(label.id, g.addNodeFromModel(label).getId());
        if (!skipDrugLabelFullTexts)
            for (final Section section : parseTsvFile(workspace, Section.class, "section.tsv")) {
                final Node node = g.addNodeFromModel(section);
                g.addEdge(labelIdNodeIdMap.get(section.labelId), node, "HAS_SECTION");
            }
        final Map<String, Long> ndcProductCodeNodeIdMap = new HashMap<>();
        for (final Product product : parseTsvFile(workspace, Product.class, "product.tsv"))
            ndcProductCodeNodeIdMap.put(product.ndcProductCode, g.addNodeFromModel(product).getId());
        for (final Prd2Label link : parseTsvFile(workspace, Prd2Label.class, "prd2label.tsv"))
            g.addEdge(ndcProductCodeNodeIdMap.get(link.ndcProductCode), labelIdNodeIdMap.get(link.labelId),
                      "HAS_LABEL");
        final Map<String, Long> uniiNodeIdMap = new HashMap<>();
        for (final ActiveIngredient ingredient : parseTsvFile(workspace, ActiveIngredient.class,
                                                              "active_ingredient.tsv")) {
            final String key = ingredient.activeMoietyUnii + "|" + ingredient.substanceUnii;
            final Long ingredientNodeId;
            if (uniiNodeIdMap.containsKey(key)) {
                ingredientNodeId = uniiNodeIdMap.get(key);
            } else {
                ingredientNodeId = g.addNodeFromModel(ingredient).getId();
                uniiNodeIdMap.put(key, ingredientNodeId);
                g.addEdge(structureIdNodeIdMap.get(ingredient.structId), ingredientNodeId, "IS_INGREDIENT");
            }
            final EdgeBuilder builder = g.buildEdge().fromNode(ndcProductCodeNodeIdMap.get(ingredient.ndcProductCode))
                                         .toNode(ingredientNodeId).withLabel("CONTAINS");
            builder.withPropertyIfNotNull("unit", ingredient.unit);
            builder.withPropertyIfNotNull("quantity", ingredient.quantity);
            builder.withPropertyIfNotNull("quantity_denom_unit", ingredient.quantityDenomUnit);
            builder.withPropertyIfNotNull("quantity_denom_value", ingredient.quantityDenomValue);
            builder.build();
        }
    }

    private void addTargets(final Workspace workspace, final Graph g,
                            final Map<Long, Long> structureIdNodeIdMap) throws ExporterException {
        final Map<String, Long> keywordIdNodeIdMap = new HashMap<>();
        for (final TargetKeyword keyword : parseTsvFile(workspace, TargetKeyword.class, "target_keyword.tsv"))
            keywordIdNodeIdMap.put(keyword.id, g.addNodeFromModel(keyword).getId());
        final Map<Integer, Long> componentIdNodeIdMap = new HashMap<>();
        for (final TargetComponent component : parseTsvFile(workspace, TargetComponent.class, "target_component.tsv"))
            componentIdNodeIdMap.put(component.id, g.addNodeFromModel(component).getId());
        for (final Tdgo2Tc link : parseTsvFile(workspace, Tdgo2Tc.class, "tdgo2tc.tsv")) {
            final Long termNodeId = getOrCreateOntologyProxyTerm(g, link.goId);
            if (termNodeId != null)
                g.addEdge(componentIdNodeIdMap.get(link.componentId), termNodeId, "HAS_GO_TERM");
        }
        for (final Tdkey2Tc link : parseTsvFile(workspace, Tdkey2Tc.class, "tdkey2tc.tsv"))
            g.addEdge(componentIdNodeIdMap.get(link.componentId), keywordIdNodeIdMap.get(link.tdKeyId), "HAS_KEYWORD");
        final Map<Integer, Long> targetIdNodeIdMap = new HashMap<>();
        for (final TargetDictionary target : parseTsvFile(workspace, TargetDictionary.class, "target_dictionary.tsv"))
            targetIdNodeIdMap.put(target.id, g.addNodeFromModel(target).getId());
        for (final Td2Tc link : parseTsvFile(workspace, Td2Tc.class, "td2tc.tsv"))
            g.addEdge(componentIdNodeIdMap.get(link.componentId), targetIdNodeIdMap.get(link.targetId), "COMPONENT_OF");
        for (final ActTableFull bioactivity : parseTsvFile(workspace, ActTableFull.class, "act_table_full.tsv")) {
            final Node node = g.addNodeFromModel(bioactivity);
            g.addEdge(node, targetIdNodeIdMap.get(bioactivity.targetId), "HAS_TARGET");
            g.addEdge(structureIdNodeIdMap.get(bioactivity.structId), node, "TARGETS");
            if (bioactivity.actionType != null)
                g.addEdge(node, g.findNode(ACTION_TYPE_LABEL, "type", bioactivity.actionType), "OF_TYPE");
            if (bioactivity.moaRefId != null)
                g.addEdge(node, g.findNode(REFERENCE_LABEL, ID_KEY, bioactivity.moaRefId), "HAS_MOA_REFERENCE");
            if (bioactivity.actRefId != null)
                g.addEdge(node, g.findNode(REFERENCE_LABEL, ID_KEY, bioactivity.actRefId), "HAS_REFERENCE");
        }
    }

    private void addVetProducts(final Workspace workspace, final Graph g,
                                final Map<Long, Long> structureIdNodeIdMap) throws ExporterException {
        final Map<String, String> applicationTypeDescriptionMap = new HashMap<>();
        for (final VetprodType type : parseTsvFile(workspace, VetprodType.class, "vetprod_type.tsv")) {
            applicationTypeDescriptionMap.put(type.applType, type.description);
        }
        final Map<Long, String> vetProdTypeMap = new HashMap<>();
        for (final Vettype type : parseTsvFile(workspace, Vettype.class, "vettype.tsv")) {
            vetProdTypeMap.put(type.prodId, type.type);
        }
        final Map<Long, Long> vetProdNodeIdMap = new HashMap<>();
        for (final Vetprod prod : parseTsvFile(workspace, Vetprod.class, "vetprod.tsv")) {
            final String type = vetProdTypeMap.get(prod.prodId);
            final String applicationTypeDescription = applicationTypeDescriptionMap.get(prod.applType);
            final Node node;
            if (type != null && applicationTypeDescription != null) {
                node = g.addNodeFromModel(prod, "type", type, "appl_type_description", applicationTypeDescription);
            } else if (type != null) {
                node = g.addNodeFromModel(prod, "type", type);
            } else if (applicationTypeDescription != null) {
                node = g.addNodeFromModel(prod, "appl_type_description", applicationTypeDescription);
            } else {
                node = g.addNodeFromModel(prod);
            }
            vetProdNodeIdMap.put(prod.prodId, node.getId());
        }
        for (final Vetprod2Struct entry : parseTsvFile(workspace, Vetprod2Struct.class, "vetprod2struct.tsv")) {
            final Long vetProdNodeId = vetProdNodeIdMap.get(entry.prodId);
            final Long structNodeId = structureIdNodeIdMap.get(entry.structId);
            g.addEdge(structNodeId, vetProdNodeId, "IS_VET_PROD");
        }
        final Map<String, Long> conceptKeyNodeIdMap = new HashMap<>();
        for (final VetOmop relationship : parseTsvFile(workspace, VetOmop.class, "vetomop.tsv")) {
            if (!conceptKeyNodeIdMap.containsKey(relationship.conceptName))
                conceptKeyNodeIdMap.put(relationship.conceptName, g.addNodeFromModel(relationship).getId());
            final String edgeLabel = relationship.relationshipType.toUpperCase(Locale.US);
            g.addEdge(structureIdNodeIdMap.get(relationship.structId),
                      conceptKeyNodeIdMap.get(relationship.conceptName), edgeLabel, "species", relationship.species);
        }
    }

    private void addFAERSEntries(final Workspace workspace, final Graph g,
                                 final Map<Long, Long> structureIdNodeIdMap) throws ExporterException {
        for (final Faers faers : parseTsvFile(workspace, Faers.class, "faers.tsv")) {
            final Node faersNode = g.addNodeFromModel(faers);
            g.addEdge(faersNode, structureIdNodeIdMap.get(faers.structId), "HAS_STRUCTURE");
        }
        for (final Faers faers : parseTsvFile(workspace, Faers.class, "faers_female.tsv")) {
            final Node faersNode = g.addNodeFromModel(faers, "gender", "female");
            g.addEdge(faersNode, structureIdNodeIdMap.get(faers.structId), "HAS_STRUCTURE");
        }
        for (final Faers faers : parseTsvFile(workspace, Faers.class, "faers_male.tsv")) {
            final Node faersNode = g.addNodeFromModel(faers, "gender", "male");
            g.addEdge(faersNode, structureIdNodeIdMap.get(faers.structId), "HAS_STRUCTURE");
        }
        // TODO: define ger and ped
        for (final Faers faers : parseTsvFile(workspace, Faers.class, "faers_ger.tsv")) {
            //final Node faersNode = g.addNodeFromModel(faers, "gender", "male");
            //g.addEdge(faersNode, structureIdNodeIdMap.get(faers.structId), "HAS_STRUCTURE");
        }
        for (final Faers faers : parseTsvFile(workspace, Faers.class, "faers_ped.tsv")) {
            //final Node faersNode = g.addNodeFromModel(faers, "gender", "male");
            //g.addEdge(faersNode, structureIdNodeIdMap.get(faers.structId), "HAS_STRUCTURE");
        }
    }

    private void addLINCSSignatures(final Workspace workspace, final Graph g,
                                    final Map<Long, Long> structureIdNodeIdMap) throws ExporterException {
        final Set<Long> missingStructureIds = new HashSet<>();
        for (final LincsSignature lincs : parseTsvFile(workspace, LincsSignature.class, "lincs_signature.tsv")) {
            final Long nodeId1 = structureIdNodeIdMap.get(lincs.structId1);
            final Long nodeId2 = structureIdNodeIdMap.get(lincs.structId2);
            if (nodeId1 == null || nodeId2 == null) {
                if (nodeId1 == null)
                    missingStructureIds.add(lincs.structId1);
                if (nodeId2 == null)
                    missingStructureIds.add(lincs.structId2);
                continue;
            }
            final Node node = g.addNodeFromModel(lincs);
            g.addEdge(node, nodeId1, "HAS_STRUCTURE", "is_parent", lincs.isParent1);
            g.addEdge(node, nodeId2, "HAS_STRUCTURE", "is_parent", lincs.isParent2);
        }
        if (LOGGER.isWarnEnabled())
            LOGGER.warn("Ignoring LINCS signatures referencing missing structures (" +
                        missingStructureIds.stream().sorted().map(Object::toString).collect(Collectors.joining()) +
                        ")");
    }
}
