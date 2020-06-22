package de.unibi.agbi.biodwh2.drugcentral.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.drugcentral.DrugCentralDataSource;
import de.unibi.agbi.biodwh2.drugcentral.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class DrugCentralGraphExporter extends GraphExporter<DrugCentralDataSource> {
    private static final Logger logger = LoggerFactory.getLogger(DrugCentralGraphExporter.class);

    @Override
    protected boolean exportGraph(final Workspace workspace, final DrugCentralDataSource dataSource,
                                  final Graph g) throws ExporterException {
        g.setIndexColumnNames("id", "code", "doid", "ndc_product_code", "cd_id");
        // "ddi_risk.tsv" is ignored because no additional info is included
        createNodesFromTsvFile(workspace, dataSource, g, InnStem.class, "inn_stem.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, RefType.class, "ref_type.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, ActionType.class, "action_type.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetClass.class, "target_class.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetComponent.class, "target_component.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetGo.class, "target_go.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetKeyword.class, "target_keyword.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, TargetDictionary.class, "target_dictionary.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, Product.class, "product.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, ObPatent.class, "ob_patent.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, ObPatentUseCode.class, "ob_patent_use_code.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, ObProduct.class, "ob_product.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, Parentmol.class, "parentmol.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, ProteinType.class, "protein_type.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, StructTypeDef.class, "struct_type_def.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, ApprovalType.class, "approval_type.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, Atc.class, "atc.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, AttributeType.class, "attr_type.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, DataSource.class, "data_source.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, DbVersion.class, "dbversion.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, DrugClass.class, "drug_class.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, Doid.class, "doid.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, IdType.class, "id_type.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, Label.class, "label.tsv");
        //createNodesFromTsvFile(workspace, dataSource, g, LincsSignature.class, "lincs_signature.tsv");
        createNodesFromTsvFile(workspace, dataSource, g, ObExclusivityCode.class, "ob_exclusivity_code.tsv");
        for (Structures structures : parseTsvFile(workspace, dataSource, Structures.class, "structures.tsv")) {
            Node structuresNode = createNodeFromModel(g, structures);
            if (structures.stem != null)
                g.addEdge(structuresNode, g.findNodeId("InnStem", "stem", structures.stem), "HAS_INN_STEM");
        }
        for (Reference reference : parseTsvFile(workspace, dataSource, Reference.class, "reference.tsv")) {
            Node referenceNode = createNodeFromModel(g, reference);
            g.addEdge(referenceNode, g.findNodeId("RefType", "type", reference.type), "HAS_REF_TYPE");
        }
        for (ActiveIngredient activeIngredient : parseTsvFile(workspace, dataSource, ActiveIngredient.class,
                                                              "active_ingredient.tsv")) {
            Node activeIngredientNode = createNodeFromModel(g, activeIngredient);
            Long productNodeId = g.findNodeId("Product", "ndc_product_code", activeIngredient.ndcProductCode);
            Long structureNodeId = g.findNodeId("Structures", "id", activeIngredient.structId);
            g.addEdge(activeIngredientNode, productNodeId, "IN_PRODUCT");
            g.addEdge(activeIngredientNode, structureNodeId, "HAS_STRUCTURE");
        }
        for (ActTableFull entry : parseTsvFile(workspace, dataSource, ActTableFull.class, "act_table_full.tsv")) {
            Node actTableFullNode = createNodeFromModel(g, entry);
            if (entry.actionType != null)
                g.addEdge(actTableFullNode, g.findNodeId("ActionType", "action_type", entry.actionType),
                          "HAS_ACTION_TYPE");
            if (entry.targetClass != null)
                g.addEdge(actTableFullNode, g.findNodeId("TargetClass", "l1", entry.targetClass), "HAS_TARGET_CLASS");
            if (entry.structId != null)
                g.addEdge(actTableFullNode, g.findNodeId("Structures", "id", entry.structId), "HAS_STRUCTURE");
            if (entry.targetId != null)
                g.addEdge(actTableFullNode, g.findNodeId("TargetDictionary", "id", entry.targetId),
                          "HAS_TARGET_DICTIONARY");
            if (entry.actRefId != null)
                g.addEdge(actTableFullNode, g.findNodeId("Reference", "id", entry.actRefId), "HAS_ACT_REFERENCE");
            if (entry.moaRefId != null)
                g.addEdge(actTableFullNode, g.findNodeId("Reference", "id", entry.moaRefId), "HAS_MOA_REFERENCE");
        }
        for (Approval approval : parseTsvFile(workspace, dataSource, Approval.class, "approval.tsv")) {
            Node approvalNode = createNodeFromModel(g, approval);
            if (approval.structId != null)
                g.addEdge(approvalNode, g.findNodeId("Structures", "id", approval.structId), "HAS_STRUCTURE");
            if (approval.type != null)
                g.addEdge(approvalNode, g.findNodeId("ApprovalType", "description", approval.type), "HAS_STRUCTURE");
        }
        for (AtcDdd atcDdd : parseTsvFile(workspace, dataSource, AtcDdd.class, "atc_ddd.tsv")) {
            Node atcDddNode = createNodeFromModel(g, atcDdd);
            g.addEdge(g.findNodeId("ATC", "code", atcDdd.actCode), atcDddNode, "HAS_DDD");
            g.addEdge(atcDddNode, g.findNodeId("Structures", "id", atcDdd.structId), "HAS_STRUCTURE");
        }
        for (Ddi ddi : parseTsvFile(workspace, dataSource, Ddi.class, "ddi.tsv")) {
            Node ddiNode = createNodeFromModel(g, ddi);
            g.addEdge(ddiNode, g.findNodeId("DrugClass", "name", ddi.drugClass1), "HAS_DRUG_CLASS");
            g.addEdge(ddiNode, g.findNodeId("DrugClass", "name", ddi.drugClass2), "HAS_DRUG_CLASS");
        }
        for (DoidXref doidXref : parseTsvFile(workspace, dataSource, DoidXref.class, "doid_xref.tsv")) {
            Node doidXrefNode = createNodeFromModel(g, doidXref);
            if (doidXref.doid != null)
                g.addEdge(doidXrefNode, g.findNodeId("Doid", "doid", doidXref.doid), "HAS_DOID");
        }
        for (Faers faers : parseTsvFile(workspace, dataSource, Faers.class, "faers.tsv")) {
            Node faersNode = createNodeFromModel(g, faers);
            g.addEdge(faersNode, g.findNodeId("Structures", "id", faers.structId), "HAS_STRUCTURE");
        }
        for (Identifier identifier : parseTsvFile(workspace, dataSource, Identifier.class, "identifier.tsv")) {
            Node identifierNode = createNodeFromModel(g, identifier);
            if (identifier.idType != null)
                g.addEdge(identifierNode, g.findNodeId("IdType", "type", identifier.idType), "HAS_ID_TYPE");
            g.addEdge(identifierNode, g.findNodeId("Structures", "id", identifier.structId), "HAS_STRUCTURE");
        }
        for (ObExclusivity obExclusivity : parseTsvFile(workspace, dataSource, ObExclusivity.class,
                                                        "ob_exclusivity.tsv")) {
            Node obExclusivityNode = createNodeFromModel(g, obExclusivity);
            if (obExclusivity.exclusivityCode != null)
                g.addEdge(obExclusivityNode, g.findNodeId("ObExclusivityCode", "code", obExclusivity.exclusivityCode),
                          "HAS_OB_EXCLUSIVITY_CODE");
        }
        for (OmopRelationship omopRelationship : parseTsvFile(workspace, dataSource, OmopRelationship.class,
                                                              "omop_relationship.tsv")) {
            Node omopRelationshipNode = createNodeFromModel(g, omopRelationship);
            g.addEdge(omopRelationshipNode, g.findNodeId("Structures", "id", omopRelationship.structId),
                      "HAS_STRUCTURE");
        }
        for (Pdb pdb : parseTsvFile(workspace, dataSource, Pdb.class, "pdb.tsv")) {
            Node pdbNode = createNodeFromModel(g, pdb);
            g.addEdge(pdbNode, g.findNodeId("Structures", "id", pdb.structId), "HAS_STRUCTURE");
        }
        for (PharmaClass pharmaClass : parseTsvFile(workspace, dataSource, PharmaClass.class, "pharma_class.tsv")) {
            Node pharmaClassNode = createNodeFromModel(g, pharmaClass);
            g.addEdge(pharmaClassNode, g.findNodeId("Structures", "id", pharmaClass.structId), "HAS_STRUCTURE");
        }
        for (Pka pka : parseTsvFile(workspace, dataSource, Pka.class, "pka.tsv")) {
            Node pkaNode = createNodeFromModel(g, pka);
            g.addEdge(pkaNode, g.findNodeId("Structures", "id", pka.structId), "HAS_STRUCTURE");
        }
        for (Section section : parseTsvFile(workspace, dataSource, Section.class, "section.tsv")) {
            Node sectionNode = createNodeFromModel(g, section);
            g.addEdge(g.findNodeId("Label", "id", section.labelId), sectionNode, "HAS_SECTION");
        }
        for (StructureType structureType : parseTsvFile(workspace, dataSource, StructureType.class,
                                                        "structure_type.tsv")) {
            Node structureTypeNode = createNodeFromModel(g, structureType);
            g.addEdge(structureTypeNode, g.findNodeId("Structures", "id", structureType.structId), "HAS_STRUCTURE");
            g.addEdge(structureTypeNode, g.findNodeId("StructTypeDef", "type", structureType.type),
                      "HAS_STRUCT_TYPE_DEF");
        }
        for (Synonyms synonyms : parseTsvFile(workspace, dataSource, Synonyms.class, "synonyms.tsv")) {
            Node synonymsNode = createNodeFromModel(g, synonyms);
            if (synonyms.parentId != null)
                g.addEdge(synonymsNode, g.findNodeId("Parentmol", "cd_id", synonyms.parentId), "HAS_PARENTMOL");
            if (synonyms.id != null)
                g.addEdge(synonymsNode, g.findNodeId("Structures", "id", synonyms.id), "HAS_STRUCTURE");
        }
        for (Struct2Atc struct2Atc : parseTsvFile(workspace, dataSource, Struct2Atc.class, "struct2atc.tsv")) {
            Long structNodeId = g.findNodeId("Structures", "id", struct2Atc.structId);
            Long atcNodeId = g.findNodeId("ATC", "code", struct2Atc.atcCode);
            g.addEdge(structNodeId, atcNodeId, "HAS_ATC_CODE");
        }
        for (Struct2DrgClass struct2DrgClass : parseTsvFile(workspace, dataSource, Struct2DrgClass.class,
                                                            "struct2drgclass.tsv")) {
            Long structNodeId = g.findNodeId("Structures", "id", struct2DrgClass.structId);
            Long drugClassNodeId = g.findNodeId("DrugClass", "id", struct2DrgClass.drugClassId);
            g.addEdge(structNodeId, drugClassNodeId, "HAS_DRUG_CLASS");
        }
        for (Tdgo2Tc tdgo2Tc : parseTsvFile(workspace, dataSource, Tdgo2Tc.class, "tdgo2tc.tsv")) {
            Long goNodeId = g.findNodeId("TargetGo", "id", tdgo2Tc.goId);
            Long componentNodeId = g.findNodeId("TargetComponent", "id", tdgo2Tc.componentId);
            g.addEdge(goNodeId, componentNodeId, "HAS_TARGET_COMPONENT");
        }
        for (Tdkey2Tc tdkey2Tc : parseTsvFile(workspace, dataSource, Tdkey2Tc.class, "tdkey2tc.tsv")) {
            Long keywordNodeId = g.findNodeId("TargetKeyword", "id", tdkey2Tc.tdKeyId);
            Long componentNodeId = g.findNodeId("TargetComponent", "id", tdkey2Tc.componentId);
            g.addEdge(keywordNodeId, componentNodeId, "HAS_TARGET_COMPONENT");
        }
        for (Prd2Label prd2Label : parseTsvFile(workspace, dataSource, Prd2Label.class, "prd2label.tsv")) {
            Long productNodeId = g.findNodeId("Product", "ndc_product_code", prd2Label.ndcProductCode);
            Long labelNodeId = g.findNodeId("Label", "id", prd2Label.labelId);
            g.addEdge(productNodeId, labelNodeId, "HAS_LABEL");
        }
        for (Struct2ObProd struct2ObProd : parseTsvFile(workspace, dataSource, Struct2ObProd.class,
                                                        "struct2obprod.tsv")) {
            Long structureNodeId = g.findNodeId("Structures", "id", struct2ObProd.structId);
            Long obProductNodeId = g.findNodeId("ObProduct", "id", struct2ObProd.prodId);
            Edge e = g.addEdge(structureNodeId, obProductNodeId, "HAS_OB_PRODUCT");
            e.setProperty("strength", struct2ObProd.strength);
        }
        for (Struct2Parent struct2Parent : parseTsvFile(workspace, dataSource, Struct2Parent.class,
                                                        "struct2parent.tsv")) {
            Long structureNodeId = g.findNodeId("Structures", "id", struct2Parent.structId);
            Long parentNodeId = g.findNodeId("Parentmol", "cd_id", struct2Parent.parentId);
            g.addEdge(structureNodeId, parentNodeId, "HAS_PARENTMOL");
        }
        for (Td2Tc td2Tc : parseTsvFile(workspace, dataSource, Td2Tc.class, "td2tc.tsv")) {
            Long targetNodeId = g.findNodeId("TargetDictionary", "id", td2Tc.targetId);
            Long componentNodeId = g.findNodeId("TargetComponent", "id", td2Tc.componentId);
            g.addEdge(targetNodeId, componentNodeId, "HAS_TARGET_COMPONENT");
        }
        return true;
    }

    private <T> void createNodesFromTsvFile(Workspace workspace, DrugCentralDataSource dataSource, Graph g,
                                            Class<T> dataType, String fileName) throws ExporterException {
        for (T entry : parseTsvFile(workspace, dataSource, dataType, fileName))
            createNodeFromModel(g, entry);
    }

    private <T> Iterable<T> parseTsvFile(Workspace workspace, DrugCentralDataSource dataSource,
                                         Class<T> typeVariableClass, String fileName) throws ExporterException {
        logger.info("Exporting " + fileName + "...");
        String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        File sourceFile = new File(filePath);
        if (!sourceFile.exists())
            throw new ExporterException(new ParserFileNotFoundException(fileName));
        ObjectReader reader = getFormatReader(typeVariableClass);
        try {
            MappingIterator<T> iterator = reader.readValues(sourceFile);
            iterator.next();
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private <T> ObjectReader getFormatReader(Class<T> typeVariableClass) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(typeVariableClass).withColumnSeparator('\t').withNullValue("");
        return csvMapper.readerFor(typeVariableClass).with(schema);
    }
}
