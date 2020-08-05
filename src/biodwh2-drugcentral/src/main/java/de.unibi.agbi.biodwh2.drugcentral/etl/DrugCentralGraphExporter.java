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
    private static final Logger LOGGER = LoggerFactory.getLogger(DrugCentralGraphExporter.class);

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
            Node productNode = g.findNode("Product", "ndc_product_code", activeIngredient.ndcProductCode);
            Node structureNode = g.findNode("Structure", "id", activeIngredient.structId);
            g.addEdge(activeIngredientNode, productNode, "IN_PRODUCT");
            g.addEdge(activeIngredientNode, structureNode, "HAS_STRUCTURE");
        }
        for (ActTableFull entry : parseTsvFile(workspace, dataSource, ActTableFull.class, "act_table_full.tsv")) {
            Node actTableFullNode = createNodeFromModel(g, entry);
            if (entry.actionType != null)
                g.addEdge(actTableFullNode, g.findNode("ActionType", "type", entry.actionType), "HAS_ACTION_TYPE");
            if (entry.structId != null)
                g.addEdge(actTableFullNode, g.findNode("Structure", "id", entry.structId), "HAS_STRUCTURE");
            if (entry.targetId != null)
                g.addEdge(actTableFullNode, g.findNode("TargetDictionary", "id", entry.targetId),
                          "HAS_TARGET_DICTIONARY");
            if (entry.actRefId != null)
                g.addEdge(actTableFullNode, g.findNode("Reference", "id", entry.actRefId), "HAS_ACT_REFERENCE");
            if (entry.moaRefId != null)
                g.addEdge(actTableFullNode, g.findNode("Reference", "id", entry.moaRefId), "HAS_MOA_REFERENCE");
        }
        for (Approval approval : parseTsvFile(workspace, dataSource, Approval.class, "approval.tsv")) {
            Node approvalNode = createNodeFromModel(g, approval);
            if (approval.structId != null)
                g.addEdge(approvalNode, g.findNode("Structure", "id", approval.structId), "HAS_STRUCTURE");
        }
        for (AtcDdd atcDdd : parseTsvFile(workspace, dataSource, AtcDdd.class, "atc_ddd.tsv")) {
            Node atcDddNode = createNodeFromModel(g, atcDdd);
            g.addEdge(g.findNode("ATC", "code", atcDdd.atcCode), atcDddNode, "HAS_DDD");
            g.addEdge(atcDddNode, g.findNode("Structure", "id", atcDdd.structId), "HAS_STRUCTURE");
        }
        for (Ddi ddi : parseTsvFile(workspace, dataSource, Ddi.class, "ddi.tsv")) {
            Node ddiNode = createNodeFromModel(g, ddi);
            g.addEdge(ddiNode, g.findNode("DrugClass", "name", ddi.drugClass1), "HAS_DRUG_CLASS");
            g.addEdge(ddiNode, g.findNode("DrugClass", "name", ddi.drugClass2), "HAS_DRUG_CLASS");
        }
        for (DoidXref doidXref : parseTsvFile(workspace, dataSource, DoidXref.class, "doid_xref.tsv")) {
            Node doidXrefNode = createNodeFromModel(g, doidXref);
            if (doidXref.doid != null)
                g.addEdge(g.findNode("Doid", "doid", doidXref.doid), doidXrefNode, "HAS_XREF");
        }
        for (Faers faers : parseTsvFile(workspace, dataSource, Faers.class, "faers.tsv")) {
            Node faersNode = createNodeFromModel(g, faers);
            g.addEdge(faersNode, g.findNode("Structure", "id", faers.structId), "HAS_STRUCTURE");
        }
        for (Identifier identifier : parseTsvFile(workspace, dataSource, Identifier.class, "identifier.tsv")) {
            Node identifierNode = createNodeFromModel(g, identifier);
            g.addEdge(g.findNode("Structure", "id", identifier.structId), identifierNode, "HAS_IDENTIFIER");
        }
        for (ObExclusivity obExclusivity : parseTsvFile(workspace, dataSource, ObExclusivity.class,
                                                        "ob_exclusivity.tsv")) {
            Node obExclusivityNode = createNodeFromModel(g, obExclusivity);
            if (obExclusivity.exclusivityCode != null)
                g.addEdge(obExclusivityNode, g.findNode("ObExclusivityCode", "code", obExclusivity.exclusivityCode),
                          "HAS_OB_EXCLUSIVITY_CODE");
        }
        for (OmopRelationship omopRelationship : parseTsvFile(workspace, dataSource, OmopRelationship.class,
                                                              "omop_relationship.tsv")) {
            Node omopRelationshipNode = createNodeFromModel(g, omopRelationship);
            g.addEdge(g.findNode("Structure", "id", omopRelationship.structId), omopRelationshipNode, "RELATES_TO");
        }
        for (Pdb pdb : parseTsvFile(workspace, dataSource, Pdb.class, "pdb.tsv")) {
            Node pdbNode = createNodeFromModel(g, pdb);
            g.addEdge(g.findNode("Structure", "id", pdb.structId), pdbNode, "HAS_PDB");
        }
        for (PharmaClass pharmaClass : parseTsvFile(workspace, dataSource, PharmaClass.class, "pharma_class.tsv")) {
            Node pharmaClassNode = createNodeFromModel(g, pharmaClass);
            g.addEdge(g.findNode("Structure", "id", pharmaClass.structId), pharmaClassNode, "BELONGS_TO");
        }
        for (Pka pka : parseTsvFile(workspace, dataSource, Pka.class, "pka.tsv")) {
            Node pkaNode = createNodeFromModel(g, pka);
            g.addEdge(g.findNode("Structure", "id", pka.structId), pkaNode, "HAS_PKA");
        }
        for (Section section : parseTsvFile(workspace, dataSource, Section.class, "section.tsv")) {
            Node sectionNode = createNodeFromModel(g, section);
            g.addEdge(g.findNode("Label", "id", section.labelId), sectionNode, "HAS_SECTION");
        }
        for (Synonyms synonyms : parseTsvFile(workspace, dataSource, Synonyms.class, "synonyms.tsv")) {
            Node synonymsNode = createNodeFromModel(g, synonyms);
            if (synonyms.parentId != null)
                g.addEdge(synonymsNode, g.findNode("Parentmol", "cd_id", synonyms.parentId), "HAS_PARENTMOL");
            if (synonyms.id != null)
                g.addEdge(g.findNode("Structure", "id", synonyms.id), synonymsNode, "HAS_SYNONYM");
        }
        for (Struct2Atc struct2Atc : parseTsvFile(workspace, dataSource, Struct2Atc.class, "struct2atc.tsv")) {
            Node structNode = g.findNode("Structure", "id", struct2Atc.structId);
            Node atcNode = g.findNode("ATC", "code", struct2Atc.atcCode);
            g.addEdge(structNode, atcNode, "HAS_ATC_CODE");
        }
        for (Struct2DrgClass struct2DrgClass : parseTsvFile(workspace, dataSource, Struct2DrgClass.class,
                                                            "struct2drgclass.tsv")) {
            Node structNode = g.findNode("Structure", "id", struct2DrgClass.structId);
            Node drugClassNode = g.findNode("DrugClass", "id", struct2DrgClass.drugClassId);
            g.addEdge(structNode, drugClassNode, "BELONGS_TO");
        }
        for (Tdgo2Tc tdgo2Tc : parseTsvFile(workspace, dataSource, Tdgo2Tc.class, "tdgo2tc.tsv")) {
            Node goNode = g.findNode("TargetGo", "id", tdgo2Tc.goId);
            Node componentNode = g.findNode("TargetComponent", "id", tdgo2Tc.componentId);
            g.addEdge(goNode, componentNode, "HAS_TARGET_COMPONENT");
        }
        for (Tdkey2Tc tdkey2Tc : parseTsvFile(workspace, dataSource, Tdkey2Tc.class, "tdkey2tc.tsv")) {
            Node keywordNode = g.findNode("TargetKeyword", "id", tdkey2Tc.tdKeyId);
            Node componentNode = g.findNode("TargetComponent", "id", tdkey2Tc.componentId);
            g.addEdge(keywordNode, componentNode, "HAS_TARGET_COMPONENT");
        }
        for (Prd2Label prd2Label : parseTsvFile(workspace, dataSource, Prd2Label.class, "prd2label.tsv")) {
            Node productNode = g.findNode("Product", "ndc_product_code", prd2Label.ndcProductCode);
            Node labelNode = g.findNode("Label", "id", prd2Label.labelId);
            g.addEdge(productNode, labelNode, "HAS_LABEL");
        }
        for (Struct2ObProd struct2ObProd : parseTsvFile(workspace, dataSource, Struct2ObProd.class,
                                                        "struct2obprod.tsv")) {
            Node structureNode = g.findNode("Structure", "id", struct2ObProd.structId);
            Node obProductNode = g.findNode("ObProduct", "id", struct2ObProd.prodId);
            Edge e = g.addEdge(structureNode, obProductNode, "HAS_OB_PRODUCT");
            e.setProperty("strength", struct2ObProd.strength);
            g.update(e);
        }
        for (Struct2Parent struct2Parent : parseTsvFile(workspace, dataSource, Struct2Parent.class,
                                                        "struct2parent.tsv")) {
            Node structureNode = g.findNode("Structure", "id", struct2Parent.structId);
            Node parentNode = g.findNode("Parentmol", "cd_id", struct2Parent.parentId);
            g.addEdge(structureNode, parentNode, "HAS_PARENTMOL");
        }
        for (Td2Tc td2Tc : parseTsvFile(workspace, dataSource, Td2Tc.class, "td2tc.tsv")) {
            Node targetNode = g.findNode("TargetDictionary", "id", td2Tc.targetId);
            Node componentNode = g.findNode("TargetComponent", "id", td2Tc.componentId);
            g.addEdge(targetNode, componentNode, "HAS_TARGET_COMPONENT");
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
        Map<Integer, String> structureIdTypeMap = new HashMap<>();
        for (StructureType structureType : parseTsvFile(workspace, dataSource, StructureType.class,
                                                        "structure_type.tsv"))
            structureIdTypeMap.put(structureType.structId, structureType.type);
        for (Structure structure : parseTsvFile(workspace, dataSource, Structure.class, "structures.tsv")) {
            Node structureNode = createNodeFromModel(g, structure);
            if (structureIdTypeMap.containsKey(structure.id))
                structureNode.setProperty("type", structureIdTypeMap.get(structure.id));
            g.update(structureNode);
            if (structure.stem != null)
                g.addEdge(structureNode, g.findNode("InnStem", "stem", structure.stem), "HAS_INN_STEM");
        }
    }
}
