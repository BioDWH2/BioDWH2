package de.unibi.agbi.biodwh2.drugcentral.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
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
    private static final Logger logger = LoggerFactory.getLogger(DrugCentralGraphExporter.class);

    @Override
    protected boolean exportGraph(final Workspace workspace, final DrugCentralDataSource dataSource,
                                  final Graph g) throws ExporterException {
        g.setIndexColumnNames("id", "code", "doid", "ndc_product_code", "cd_id", "type", "stem", "name");
        // "ddi_risk.tsv", "approval_type.tsv", "target_class.tsv", "ref_type.tsv", "protein_type.tsv", "id_type.tsv"
        // are ignored because no necessary additional info is included
        createNodesFromTsvFile(workspace, dataSource, g, InnStem.class, "inn_stem.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, ActionType.class, "action_type.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetComponent.class, "target_component.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetGo.class, "target_go.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetKeyword.class, "target_keyword.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetDictionary.class, "target_dictionary.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, Product.class, "product.tsv");
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
        for (ActiveIngredient activeIngredient : parseTsvFile(workspace, dataSource, ActiveIngredient.class,
                                                              "active_ingredient.tsv")) {
            Node activeIngredientNode = createNodeFromModel(g, activeIngredient);
            Long productNodeId = g.findNodeId("Product", "ndc_product_code", activeIngredient.ndcProductCode);
            Long structureNodeId = g.findNodeId("Structure", "id", activeIngredient.structId);
            g.addEdgeFast(activeIngredientNode, productNodeId, "IN_PRODUCT");
            g.addEdgeFast(activeIngredientNode, structureNodeId, "HAS_STRUCTURE");
        }
        for (ActTableFull entry : parseTsvFile(workspace, dataSource, ActTableFull.class, "act_table_full.tsv")) {
            Node actTableFullNode = createNodeFromModel(g, entry);
            if (entry.actionType != null)
                g.addEdgeFast(actTableFullNode, g.findNodeId("ActionType", "type", entry.actionType),
                              "HAS_ACTION_TYPE");
            if (entry.structId != null)
                g.addEdgeFast(actTableFullNode, g.findNodeId("Structure", "id", entry.structId), "HAS_STRUCTURE");
            if (entry.targetId != null)
                g.addEdgeFast(actTableFullNode, g.findNodeId("TargetDictionary", "id", entry.targetId),
                              "HAS_TARGET_DICTIONARY");
            if (entry.actRefId != null)
                g.addEdgeFast(actTableFullNode, g.findNodeId("Reference", "id", entry.actRefId), "HAS_ACT_REFERENCE");
            if (entry.moaRefId != null)
                g.addEdgeFast(actTableFullNode, g.findNodeId("Reference", "id", entry.moaRefId), "HAS_MOA_REFERENCE");
        }
        for (Approval approval : parseTsvFile(workspace, dataSource, Approval.class, "approval.tsv")) {
            Node approvalNode = createNodeFromModel(g, approval);
            if (approval.structId != null)
                g.addEdgeFast(approvalNode, g.findNodeId("Structure", "id", approval.structId), "HAS_STRUCTURE");
        }
        for (AtcDdd atcDdd : parseTsvFile(workspace, dataSource, AtcDdd.class, "atc_ddd.tsv")) {
            Node atcDddNode = createNodeFromModel(g, atcDdd);
            g.addEdgeFast(g.findNodeId("ATC", "code", atcDdd.atcCode), atcDddNode, "HAS_DDD");
            g.addEdgeFast(atcDddNode, g.findNodeId("Structure", "id", atcDdd.structId), "HAS_STRUCTURE");
        }
        for (Ddi ddi : parseTsvFile(workspace, dataSource, Ddi.class, "ddi.tsv")) {
            Node ddiNode = createNodeFromModel(g, ddi);
            g.addEdgeFast(ddiNode, g.findNodeId("DrugClass", "name", ddi.drugClass1), "HAS_DRUG_CLASS");
            g.addEdgeFast(ddiNode, g.findNodeId("DrugClass", "name", ddi.drugClass2), "HAS_DRUG_CLASS");
        }
        for (DoidXref doidXref : parseTsvFile(workspace, dataSource, DoidXref.class, "doid_xref.tsv")) {
            Node doidXrefNode = createNodeFromModel(g, doidXref);
            if (doidXref.doid != null)
                g.addEdgeFast(g.findNodeId("Doid", "doid", doidXref.doid), doidXrefNode, "HAS_XREF");
        }
        for (Faers faers : parseTsvFile(workspace, dataSource, Faers.class, "faers.tsv")) {
            Node faersNode = createNodeFromModel(g, faers);
            g.addEdgeFast(faersNode, g.findNodeId("Structure", "id", faers.structId), "HAS_STRUCTURE");
        }
        for (Identifier identifier : parseTsvFile(workspace, dataSource, Identifier.class, "identifier.tsv")) {
            Node identifierNode = createNodeFromModel(g, identifier);
            g.addEdgeFast(g.findNodeId("Structure", "id", identifier.structId), identifierNode, "HAS_IDENTIFIER");
        }
        for (ObExclusivity obExclusivity : parseTsvFile(workspace, dataSource, ObExclusivity.class,
                                                        "ob_exclusivity.tsv")) {
            Node obExclusivityNode = createNodeFromModel(g, obExclusivity);
            if (obExclusivity.exclusivityCode != null)
                g.addEdgeFast(obExclusivityNode,
                              g.findNodeId("ObExclusivityCode", "code", obExclusivity.exclusivityCode),
                              "HAS_OB_EXCLUSIVITY_CODE");
        }
        for (OmopRelationship omopRelationship : parseTsvFile(workspace, dataSource, OmopRelationship.class,
                                                              "omop_relationship.tsv")) {
            Node omopRelationshipNode = createNodeFromModel(g, omopRelationship);
            g.addEdgeFast(g.findNodeId("Structure", "id", omopRelationship.structId), omopRelationshipNode,
                          "RELATES_TO");
        }
        for (Pdb pdb : parseTsvFile(workspace, dataSource, Pdb.class, "pdb.tsv")) {
            Node pdbNode = createNodeFromModel(g, pdb);
            g.addEdgeFast(g.findNodeId("Structure", "id", pdb.structId), pdbNode, "HAS_PDB");
        }
        for (PharmaClass pharmaClass : parseTsvFile(workspace, dataSource, PharmaClass.class, "pharma_class.tsv")) {
            Node pharmaClassNode = createNodeFromModel(g, pharmaClass);
            g.addEdgeFast(g.findNodeId("Structure", "id", pharmaClass.structId), pharmaClassNode, "BELONGS_TO");
        }
        for (Pka pka : parseTsvFile(workspace, dataSource, Pka.class, "pka.tsv")) {
            Node pkaNode = createNodeFromModel(g, pka);
            g.addEdgeFast(g.findNodeId("Structure", "id", pka.structId), pkaNode, "HAS_PKA");
        }
        for (Section section : parseTsvFile(workspace, dataSource, Section.class, "section.tsv")) {
            Node sectionNode = createNodeFromModel(g, section);
            g.addEdgeFast(g.findNodeId("Label", "id", section.labelId), sectionNode, "HAS_SECTION");
        }
        for (Synonyms synonyms : parseTsvFile(workspace, dataSource, Synonyms.class, "synonyms.tsv")) {
            Node synonymsNode = createNodeFromModel(g, synonyms);
            if (synonyms.parentId != null)
                g.addEdgeFast(synonymsNode, g.findNodeId("Parentmol", "cd_id", synonyms.parentId), "HAS_PARENTMOL");
            if (synonyms.id != null)
                g.addEdgeFast(g.findNodeId("Structure", "id", synonyms.id), synonymsNode, "HAS_SYNONYM");
        }
        for (Struct2Atc struct2Atc : parseTsvFile(workspace, dataSource, Struct2Atc.class, "struct2atc.tsv")) {
            Long structNodeId = g.findNodeId("Structure", "id", struct2Atc.structId);
            Long atcNodeId = g.findNodeId("ATC", "code", struct2Atc.atcCode);
            g.addEdgeFast(structNodeId, atcNodeId, "HAS_ATC_CODE");
        }
        for (Struct2DrgClass struct2DrgClass : parseTsvFile(workspace, dataSource, Struct2DrgClass.class,
                                                            "struct2drgclass.tsv")) {
            Long structNodeId = g.findNodeId("Structure", "id", struct2DrgClass.structId);
            Long drugClassNodeId = g.findNodeId("DrugClass", "id", struct2DrgClass.drugClassId);
            g.addEdgeFast(structNodeId, drugClassNodeId, "BELONGS_TO");
        }
        for (Tdgo2Tc tdgo2Tc : parseTsvFile(workspace, dataSource, Tdgo2Tc.class, "tdgo2tc.tsv")) {
            Long goNodeId = g.findNodeId("TargetGo", "id", tdgo2Tc.goId);
            Long componentNodeId = g.findNodeId("TargetComponent", "id", tdgo2Tc.componentId);
            g.addEdgeFast(goNodeId, componentNodeId, "HAS_TARGET_COMPONENT");
        }
        for (Tdkey2Tc tdkey2Tc : parseTsvFile(workspace, dataSource, Tdkey2Tc.class, "tdkey2tc.tsv")) {
            Long keywordNodeId = g.findNodeId("TargetKeyword", "id", tdkey2Tc.tdKeyId);
            Long componentNodeId = g.findNodeId("TargetComponent", "id", tdkey2Tc.componentId);
            g.addEdgeFast(keywordNodeId, componentNodeId, "HAS_TARGET_COMPONENT");
        }
        for (Prd2Label prd2Label : parseTsvFile(workspace, dataSource, Prd2Label.class, "prd2label.tsv")) {
            Long productNodeId = g.findNodeId("Product", "ndc_product_code", prd2Label.ndcProductCode);
            Long labelNodeId = g.findNodeId("Label", "id", prd2Label.labelId);
            g.addEdgeFast(productNodeId, labelNodeId, "HAS_LABEL");
        }
        for (Struct2ObProd struct2ObProd : parseTsvFile(workspace, dataSource, Struct2ObProd.class,
                                                        "struct2obprod.tsv")) {
            Long structureNodeId = g.findNodeId("Structure", "id", struct2ObProd.structId);
            Long obProductNodeId = g.findNodeId("ObProduct", "id", struct2ObProd.prodId);
            Edge e = g.addEdge(structureNodeId, obProductNodeId, "HAS_OB_PRODUCT");
            e.setProperty("strength", struct2ObProd.strength);
        }
        for (Struct2Parent struct2Parent : parseTsvFile(workspace, dataSource, Struct2Parent.class,
                                                        "struct2parent.tsv")) {
            Long structureNodeId = g.findNodeId("Structure", "id", struct2Parent.structId);
            Long parentNodeId = g.findNodeId("Parentmol", "cd_id", struct2Parent.parentId);
            g.addEdgeFast(structureNodeId, parentNodeId, "HAS_PARENTMOL");
        }
        for (Td2Tc td2Tc : parseTsvFile(workspace, dataSource, Td2Tc.class, "td2tc.tsv")) {
            Long targetNodeId = g.findNodeId("TargetDictionary", "id", td2Tc.targetId);
            Long componentNodeId = g.findNodeId("TargetComponent", "id", td2Tc.componentId);
            g.addEdgeFast(targetNodeId, componentNodeId, "HAS_TARGET_COMPONENT");
        }
        return true;
    }

    private <T> void createNodesFromTsvFile(final Workspace workspace, final DrugCentralDataSource dataSource,
                                            final Graph g, final Class<T> dataType,
                                            final String fileName) throws ExporterException {
        for (T entry : parseTsvFile(workspace, dataSource, dataType, fileName))
            createNodeFromModel(g, entry);
    }

    private <T> Iterable<T> parseTsvFile(final Workspace workspace, final DrugCentralDataSource dataSource,
                                         final Class<T> typeVariableClass,
                                         final String fileName) throws ExporterException {
        logger.info("Exporting " + fileName + "...");
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
        Map<Integer, String> structureIdTypeMap = new HashMap<>();
        for (StructureType structureType : parseTsvFile(workspace, dataSource, StructureType.class,
                                                        "structure_type.tsv"))
            structureIdTypeMap.put(structureType.structId, structureType.type);
        for (Structure structure : parseTsvFile(workspace, dataSource, Structure.class, "structures.tsv")) {
            Node structureNode = createNodeFromModel(g, structure);
            if (structureIdTypeMap.containsKey(structure.id))
                structureNode.setProperty("type", structureIdTypeMap.get(structure.id));
            if (structure.stem != null)
                g.addEdgeFast(structureNode, g.findNodeId("InnStem", "stem", structure.stem), "HAS_INN_STEM");
        }
    }
}
