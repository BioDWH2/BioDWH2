package de.unibi.agbi.biodwh2.drugcentral.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.drugcentral.DrugCentralDataSource;
import de.unibi.agbi.biodwh2.drugcentral.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DrugCentralGraphExporter extends GraphExporter<DrugCentralDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DrugCentralGraphExporter.class);
    private final Map<Integer, Long> structureIdNodeIdMap = new HashMap<>();
    private final Map<String, Long> productCodeNodeIdMap = new HashMap<>();

    public DrugCentralGraphExporter(final DrugCentralDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph g) throws ExporterException {
        g.setNodeIndexPropertyKeys("id", "code", "doid", "ndc_product_code", "cd_id", "type", "stem", "name");
        // "ddi_risk.tsv", "approval_type.tsv", "target_class.tsv", "ref_type.tsv", "protein_type.tsv", "id_type.tsv"
        // are ignored because no necessary additional info is included
        createNodesFromTsvFile(workspace, dataSource, g, InnStem.class, "inn_stem.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, ActionType.class, "action_type.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetComponent.class, "target_component.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetGo.class, "target_go.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetKeyword.class, "target_keyword.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetDictionary.class, "target_dictionary.tsv");
        for (final Product entry : parseTsvFile(workspace, dataSource, Product.class, "product.tsv")) {
            final Node node = createNodeFromModel(g, entry);
            productCodeNodeIdMap.put(entry.ndcProductCode, node.getId());
        }
        createNodesFromTsvFile(workspace, dataSource, g, ObPatent.class, "ob_patent.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, ObPatentUseCode.class, "ob_patent_use_code.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, ObProduct.class, "ob_product.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, Parentmol.class, "parentmol.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, StructTypeDef.class, "struct_type_def.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, Atc.class, "atc.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, AttributeType.class, "attr_type.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, DataSource.class, "data_source.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, DbVersion.class, "dbversion.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, DrugClass.class, "drug_class.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, Doid.class, "doid.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, Label.class, "label.tsv");
        //createNodesFromTsvFile(workspace, dataSource, g, LincsSignature.class, "lincs_signature.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, ObExclusivityCode.class, "ob_exclusivity_code.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, Reference.class, "reference.tsv");
        addStructuresWithType(workspace, dataSource, g);
        for (final ActiveIngredient activeIngredient : parseTsvFile(workspace, dataSource, ActiveIngredient.class,
                                                                    "active_ingredient.tsv")) {
            final Node activeIngredientNode = createNodeFromModel(g, activeIngredient);
            g.addEdge(activeIngredientNode, productCodeNodeIdMap.get(activeIngredient.ndcProductCode), "IN_PRODUCT");
            g.addEdge(activeIngredientNode, structureIdNodeIdMap.get(activeIngredient.structId), "HAS_STRUCTURE");
        }
        for (final ActTableFull entry : parseTsvFile(workspace, dataSource, ActTableFull.class, "act_table_full.tsv")) {
            final Node actTableFullNode = createNodeFromModel(g, entry);
            if (entry.actionType != null)
                g.addEdge(actTableFullNode, g.findNode("ActionType", "type", entry.actionType), "HAS_ACTION_TYPE");
            if (entry.structId != null)
                g.addEdge(actTableFullNode, structureIdNodeIdMap.get(entry.structId), "HAS_STRUCTURE");
            if (entry.targetId != null)
                g.addEdge(actTableFullNode, g.findNode("TargetDictionary", "id", entry.targetId),
                          "HAS_TARGET_DICTIONARY");
            if (entry.actRefId != null)
                g.addEdge(actTableFullNode, g.findNode("Reference", "id", entry.actRefId), "HAS_ACT_REFERENCE");
            if (entry.moaRefId != null)
                g.addEdge(actTableFullNode, g.findNode("Reference", "id", entry.moaRefId), "HAS_MOA_REFERENCE");
        }
        for (final Approval approval : parseTsvFile(workspace, dataSource, Approval.class, "approval.tsv")) {
            final Node approvalNode = createNodeFromModel(g, approval);
            if (approval.structId != null)
                g.addEdge(approvalNode, structureIdNodeIdMap.get(approval.structId), "HAS_STRUCTURE");
        }
        for (final AtcDdd atcDdd : parseTsvFile(workspace, dataSource, AtcDdd.class, "atc_ddd.tsv")) {
            final Node atcDddNode = createNodeFromModel(g, atcDdd);
            g.addEdge(g.findNode("ATC", "code", atcDdd.atcCode), atcDddNode, "HAS_DDD");
            g.addEdge(atcDddNode, structureIdNodeIdMap.get(atcDdd.structId), "HAS_STRUCTURE");
        }
        for (final Ddi ddi : parseTsvFile(workspace, dataSource, Ddi.class, "ddi.tsv")) {
            final Node ddiNode = createNodeFromModel(g, ddi);
            g.addEdge(ddiNode, g.findNode("DrugClass", "name", ddi.drugClass1), "HAS_DRUG_CLASS");
            g.addEdge(ddiNode, g.findNode("DrugClass", "name", ddi.drugClass2), "HAS_DRUG_CLASS");
        }
        for (final DoidXref doidXref : parseTsvFile(workspace, dataSource, DoidXref.class, "doid_xref.tsv")) {
            final Node doidXrefNode = createNodeFromModel(g, doidXref);
            if (doidXref.doid != null)
                g.addEdge(g.findNode("Doid", "doid", doidXref.doid), doidXrefNode, "HAS_XREF");
        }
        for (final Faers faers : parseTsvFile(workspace, dataSource, Faers.class, "faers.tsv")) {
            final Node faersNode = createNodeFromModel(g, faers);
            g.addEdge(faersNode, structureIdNodeIdMap.get(faers.structId), "HAS_STRUCTURE");
        }
        for (final Identifier identifier : parseTsvFile(workspace, dataSource, Identifier.class, "identifier.tsv")) {
            final Node identifierNode = createNodeFromModel(g, identifier);
            g.addEdge(structureIdNodeIdMap.get(identifier.structId), identifierNode, "HAS_IDENTIFIER");
        }
        for (final ObExclusivity obExclusivity : parseTsvFile(workspace, dataSource, ObExclusivity.class,
                                                              "ob_exclusivity.tsv")) {
            final Node obExclusivityNode = createNodeFromModel(g, obExclusivity);
            if (obExclusivity.exclusivityCode != null)
                g.addEdge(obExclusivityNode, g.findNode("ObExclusivityCode", "code", obExclusivity.exclusivityCode),
                          "HAS_OB_EXCLUSIVITY_CODE");
        }
        for (final OmopRelationship omopRelationship : parseTsvFile(workspace, dataSource, OmopRelationship.class,
                                                                    "omop_relationship.tsv")) {
            final Node omopRelationshipNode = createNodeFromModel(g, omopRelationship);
            g.addEdge(structureIdNodeIdMap.get(omopRelationship.structId), omopRelationshipNode, "RELATES_TO");
        }
        for (final Pdb pdb : parseTsvFile(workspace, dataSource, Pdb.class, "pdb.tsv")) {
            final Node pdbNode = createNodeFromModel(g, pdb);
            g.addEdge(structureIdNodeIdMap.get(pdb.structId), pdbNode, "HAS_PDB");
        }
        for (final PharmaClass pharmaClass : parseTsvFile(workspace, dataSource, PharmaClass.class,
                                                          "pharma_class.tsv")) {
            final Node pharmaClassNode = createNodeFromModel(g, pharmaClass);
            g.addEdge(structureIdNodeIdMap.get(pharmaClass.structId), pharmaClassNode, "BELONGS_TO");
        }
        for (final Pka pka : parseTsvFile(workspace, dataSource, Pka.class, "pka.tsv")) {
            final Node pkaNode = createNodeFromModel(g, pka);
            g.addEdge(structureIdNodeIdMap.get(pka.structId), pkaNode, "HAS_PKA");
        }
        for (final Section section : parseTsvFile(workspace, dataSource, Section.class, "section.tsv")) {
            final Node sectionNode = createNodeFromModel(g, section);
            g.addEdge(g.findNode("Label", "id", section.labelId), sectionNode, "HAS_SECTION");
        }
        for (final Synonyms synonyms : parseTsvFile(workspace, dataSource, Synonyms.class, "synonyms.tsv")) {
            final Node synonymsNode = createNodeFromModel(g, synonyms);
            if (synonyms.parentId != null)
                g.addEdge(synonymsNode, g.findNode("Parentmol", "cd_id", synonyms.parentId), "HAS_PARENTMOL");
            if (synonyms.id != null)
                g.addEdge(structureIdNodeIdMap.get(synonyms.id), synonymsNode, "HAS_SYNONYM");
        }
        for (final Struct2Atc struct2Atc : parseTsvFile(workspace, dataSource, Struct2Atc.class, "struct2atc.tsv")) {
            final Node atcNode = g.findNode("ATC", "code", struct2Atc.atcCode);
            g.addEdge(structureIdNodeIdMap.get(struct2Atc.structId), atcNode, "HAS_ATC_CODE");
        }
        for (final Struct2DrgClass struct2DrgClass : parseTsvFile(workspace, dataSource, Struct2DrgClass.class,
                                                                  "struct2drgclass.tsv")) {
            final Node drugClassNode = g.findNode("DrugClass", "id", struct2DrgClass.drugClassId);
            g.addEdge(structureIdNodeIdMap.get(struct2DrgClass.structId), drugClassNode, "BELONGS_TO");
        }
        for (final Tdgo2Tc tdgo2Tc : parseTsvFile(workspace, dataSource, Tdgo2Tc.class, "tdgo2tc.tsv")) {
            final Node goNode = g.findNode("TargetGo", "id", tdgo2Tc.goId);
            final Node componentNode = g.findNode("TargetComponent", "id", tdgo2Tc.componentId);
            g.addEdge(goNode, componentNode, "HAS_TARGET_COMPONENT");
        }
        for (final Tdkey2Tc tdkey2Tc : parseTsvFile(workspace, dataSource, Tdkey2Tc.class, "tdkey2tc.tsv")) {
            final Node keywordNode = g.findNode("TargetKeyword", "id", tdkey2Tc.tdKeyId);
            final Node componentNode = g.findNode("TargetComponent", "id", tdkey2Tc.componentId);
            g.addEdge(keywordNode, componentNode, "HAS_TARGET_COMPONENT");
        }
        for (final Prd2Label prd2Label : parseTsvFile(workspace, dataSource, Prd2Label.class, "prd2label.tsv")) {
            final Node labelNode = g.findNode("Label", "id", prd2Label.labelId);
            g.addEdge(productCodeNodeIdMap.get(prd2Label.ndcProductCode), labelNode, "HAS_LABEL");
        }
        for (final Struct2ObProd struct2ObProd : parseTsvFile(workspace, dataSource, Struct2ObProd.class,
                                                              "struct2obprod.tsv")) {
            final long structureNodeId = structureIdNodeIdMap.get(struct2ObProd.structId);
            final Node obProductNode = g.findNode("ObProduct", "id", struct2ObProd.prodId);
            g.addEdge(structureNodeId, obProductNode, "HAS_OB_PRODUCT", "strength", struct2ObProd.strength);
        }
        for (final Struct2Parent struct2Parent : parseTsvFile(workspace, dataSource, Struct2Parent.class,
                                                              "struct2parent.tsv")) {
            final Node parentNode = g.findNode("Parentmol", "cd_id", struct2Parent.parentId);
            g.addEdge(structureIdNodeIdMap.get(struct2Parent.structId), parentNode, "HAS_PARENTMOL");
        }
        for (final Td2Tc td2Tc : parseTsvFile(workspace, dataSource, Td2Tc.class, "td2tc.tsv")) {
            final Node targetNode = g.findNode("TargetDictionary", "id", td2Tc.targetId);
            final Node componentNode = g.findNode("TargetComponent", "id", td2Tc.componentId);
            g.addEdge(targetNode, componentNode, "HAS_TARGET_COMPONENT");
        }
        return true;
    }

    private <T> void createNodesFromTsvFile(final Workspace workspace, final DrugCentralDataSource dataSource,
                                            final Graph g, final Class<T> dataType,
                                            final String fileName) throws ExporterException {
        for (final T entry : parseTsvFile(workspace, dataSource, dataType, fileName))
            createNodeFromModel(g, entry);
    }

    private <T> Iterable<T> parseTsvFile(final Workspace workspace, final DrugCentralDataSource dataSource,
                                         final Class<T> typeVariableClass,
                                         final String fileName) throws ExporterException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + fileName + "...");
        try {
            MappingIterator<T> iterator = FileUtils.openTsvWithHeader(workspace, dataSource, fileName,
                                                                      typeVariableClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private void addStructuresWithType(final Workspace workspace, final DrugCentralDataSource dataSource,
                                       final Graph g) throws ExporterException {
        final Map<Integer, String> structureIdTypeMap = new HashMap<>();
        for (final StructureType structureType : parseTsvFile(workspace, dataSource, StructureType.class,
                                                              "structure_type.tsv"))
            structureIdTypeMap.put(structureType.structId, structureType.type);
        for (final Structure structure : parseTsvFile(workspace, dataSource, Structure.class, "structures.tsv")) {
            final Node structureNode = createNodeFromModel(g, structure);
            structureIdNodeIdMap.put(structure.id, structureNode.getId());
            if (structureIdTypeMap.containsKey(structure.id))
                structureNode.setProperty("type", structureIdTypeMap.get(structure.id));
            g.update(structureNode);
            if (structure.stem != null)
                g.addEdge(structureNode, g.findNode("InnStem", "stem", structure.stem), "HAS_INN_STEM");
        }
    }
}
