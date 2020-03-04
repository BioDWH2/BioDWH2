package de.unibi.agbi.biodwh2.drugcentral.etl;

import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.drugcentral.DrugCentralDataSource;
import de.unibi.agbi.biodwh2.drugcentral.model.*;

import java.util.ArrayList;
import java.util.List;

public class DrugCentralGraphExporter extends GraphExporter<DrugCentralDataSource> {
    private <T> List<T> getDataList(DrugCentralDataSource dataSource, Class<T> dataType) {
        if (!dataSource.drugCentralDict.containsKey(dataType))
            return new ArrayList<>();
        //noinspection unchecked
        return (List<T>) dataSource.drugCentralDict.get(dataType);
    }

    @Override
    protected Graph exportGraph(DrugCentralDataSource dataSource) {
        Graph g = new Graph();
        for (InnStem innStem : getDataList(dataSource, InnStem.class)) {
            Node innStemNode = createNode(g, "innStem");
            innStemNode.setProperty("id", innStem.id);
            innStemNode.setProperty("stem", innStem.stem);
            innStemNode.setProperty("definition", innStem.definition);
            innStemNode.setProperty("national_name", innStem.nationalName);
            innStemNode.setProperty("length", innStem.length);
            innStemNode.setProperty("discontinued", innStem.discontinued);
        }
        for (Structures structures : getDataList(dataSource, Structures.class)) {
            Node structuresNode = createNode(g, "structures");
            structuresNode.setProperty("cd_id", structures.cdId);
            structuresNode.setProperty("cd_formula", structures.cdFormular);
            structuresNode.setProperty("cd_molweight", structures.cdMolweight);
            structuresNode.setProperty("id", structures.id);
            structuresNode.setProperty("clogp", structures.clogp);
            structuresNode.setProperty("alogs", structures.alogs);
            structuresNode.setProperty("cas_reg_no", structures.casRegNo);
            structuresNode.setProperty("tpsa", structures.tpsa);
            structuresNode.setProperty("lipinski", structures.lipinski);
            structuresNode.setProperty("name", structures.name);
            structuresNode.setProperty("no_formulations", structures.noFormulations);
            structuresNode.setProperty("stem", structures.stem);
            structuresNode.setProperty("molfile", structures.molfile);
            structuresNode.setProperty("mrdef", structures.mrdef);
            structuresNode.setProperty("enhanced_stereo", structures.enhancedStereo);
            structuresNode.setProperty("arom_c", structures.aromC);
            structuresNode.setProperty("sp3_c", structures.sp3C);
            structuresNode.setProperty("sp2_c", structures.sp2C);
            structuresNode.setProperty("sp_c", structures.spC);
            structuresNode.setProperty("halogen", structures.halogen);
            structuresNode.setProperty("hetero_sp2_c", structures.heteroSp2C);
            structuresNode.setProperty("rotb", structures.rotb);
            structuresNode.setProperty("molimg", structures.molimg);
            structuresNode.setProperty("o_n", structures.oN);
            structuresNode.setProperty("oh_nh", structures.oh_nh);
            structuresNode.setProperty("inchi", structures.inchi);
            structuresNode.setProperty("smiles", structures.smiles);
            structuresNode.setProperty("rgb", structures.rgb);
            structuresNode.setProperty("fda_labels", structures.fdaLabels);
            structuresNode.setProperty("inchikey", structures.inchiKey);
            addEdge(g, structuresNode, g.findNode("innStem", "stem", structures.stem), "hasInnStem");
        }
        for (ActionType actionType : getDataList(dataSource, ActionType.class)) {
            Node actionTypeNode = createNode(g, "actionType");
            actionTypeNode.setProperty("id", actionType.id);
            actionTypeNode.setProperty("action_type", actionType.actionType);
            actionTypeNode.setProperty("description", actionType.description);
            actionTypeNode.setProperty("parent_type", actionType.parentType);

        }
        for (RefType refType : getDataList(dataSource, RefType.class)) {
            Node refTypeNode = createNode(g, "refType");
            refTypeNode.setProperty("id", refType.id);
            refTypeNode.setProperty("type", refType.type);
            g.addNode(refTypeNode);
        }
        for (Reference reference : getDataList(dataSource, Reference.class)) {
            Node referenceNode = createNode(g, "reference");
            referenceNode.setProperty("id", reference.id);
            referenceNode.setProperty("pmid", reference.pmid);
            referenceNode.setProperty("doi", reference.doi);
            referenceNode.setProperty("document_id", reference.documentId);
            referenceNode.setProperty("type", reference.type);
            referenceNode.setProperty("authors", reference.authors);
            referenceNode.setProperty("title", reference.title);
            referenceNode.setProperty("isbn10", reference.isbn10);
            referenceNode.setProperty("url", reference.url);
            referenceNode.setProperty("journal", reference.journal);
            referenceNode.setProperty("volume", reference.volume);
            referenceNode.setProperty("issue", reference.issue);
            referenceNode.setProperty("dp_year", reference.dpYear);
            referenceNode.setProperty("pages", reference.pages);
            g.addNode(referenceNode);
            addEdge(g, referenceNode, g.findNode("refType", "type", reference.type), "hasRefType");
        }
        for (TargetClass targetClass : getDataList(dataSource, TargetClass.class)) {
            Node targetClassNode = createNode(g, "targetClass");
            targetClassNode.setProperty("l1", targetClass.l1);
            g.addNode(targetClassNode);
        }
        for (TargetDictionary targetDictionary : getDataList(dataSource, TargetDictionary.class)) {
            Node targetDictionaryNode = createNode(g, "targetDictionary");
            targetDictionaryNode.setProperty("id", targetDictionary.id);
            targetDictionaryNode.setProperty("name", targetDictionary.name);
            targetDictionaryNode.setProperty("target_class", targetDictionary.targetClass);
            targetDictionaryNode.setProperty("protein_components", targetDictionary.proteiComponents);
            targetDictionaryNode.setProperty("protein_type", targetDictionary.proteinType);
            targetDictionaryNode.setProperty("tdl", targetDictionary.tdl);
            g.addNode(targetDictionaryNode);
        }
        for (Product product : getDataList(dataSource, Product.class)) {
            Node productNode = createNode(g, "product");
            productNode.setProperty("id", product.id);
            productNode.setProperty("ndc_product_code", product.ndcProductCode);
            productNode.setProperty("form", product.form);
            productNode.setProperty("generic_name", product.genericName);
            productNode.setProperty("product_name", product.productName);
            productNode.setProperty("route", product.route);
            productNode.setProperty("marketing_status", product.marketingStatus);
            productNode.setProperty("active_ingredient_count", product.activeIngredientCount);
            g.addNode(productNode);
        }
        for (ActiveIngredient activeIngredient : getDataList(dataSource, ActiveIngredient.class)) {
            Node activeIngredientNode = createNode(g, "activeIngredient");
            activeIngredientNode.setProperty("id", activeIngredient.id);
            activeIngredientNode.setProperty("active_moiety_name", activeIngredient.name);
            activeIngredientNode.setProperty("unit", activeIngredient.unit);
            activeIngredientNode.setProperty("quantity", activeIngredient.quantity);
            activeIngredientNode.setProperty("substance_unii", activeIngredient.unii);
            activeIngredientNode.setProperty("ndc_product_code", activeIngredient.ndcProductCode);
            activeIngredientNode.setProperty("struct_id", activeIngredient.structId);
            activeIngredientNode.setProperty("quantity_denom_unit", activeIngredient.quantityDenomUnit);
            activeIngredientNode.setProperty("quantity_denom_value", activeIngredient.quantityDenomValue);
            g.addNode(activeIngredientNode);
            addEdge(g, activeIngredientNode, g.findNode("product", "ndc_product_code", activeIngredient.ndcProductCode),
                    "hasNdcProductCode");
            addEdge(g, activeIngredientNode, g.findNode("structures", "id", activeIngredient.structId), "hasStructure");
        }
        for (ActTableFull actTableFull : getDataList(dataSource, ActTableFull.class)) {
            Node actTableFullNode = createNode(g, "actTableFull");
            actTableFullNode.setProperty("act_id", actTableFull.actId);
            actTableFullNode.setProperty("struct_id", actTableFull.structId);
            actTableFullNode.setProperty("target_id", actTableFull.targetId);
            actTableFullNode.setProperty("target_name", actTableFull.targetName);
            actTableFullNode.setProperty("target_class", actTableFull.targetClass);
            actTableFullNode.setProperty("accession", actTableFull.accession);
            actTableFullNode.setProperty("gene", actTableFull.gene);
            actTableFullNode.setProperty("swissprot", actTableFull.swissprot);
            actTableFullNode.setProperty("act_value", actTableFull.actValue);
            actTableFullNode.setProperty("act_unit", actTableFull.actUnit);
            actTableFullNode.setProperty("act_type", actTableFull.actType);
            actTableFullNode.setProperty("act_comment", actTableFull.actComment);
            actTableFullNode.setProperty("act_source", actTableFull.actSource);
            actTableFullNode.setProperty("relation", actTableFull.relation);
            actTableFullNode.setProperty("moa", actTableFull.moa);
            actTableFullNode.setProperty("moa_source", actTableFull.moaSource);
            actTableFullNode.setProperty("act_source_url", actTableFull.actSourceUrl);
            actTableFullNode.setProperty("moa_source_url", actTableFull.moaSourceUrl);
            actTableFullNode.setProperty("action_type", actTableFull.actionType);
            actTableFullNode.setProperty("first_in_class", actTableFull.firstInClass);
            actTableFullNode.setProperty("tdl", actTableFull.tdl);
            actTableFullNode.setProperty("act_ref_id", actTableFull.actRefId);
            actTableFullNode.setProperty("moa_ref_id", actTableFull.moaRefId);
            actTableFullNode.setProperty("organism", actTableFull.organism);
            g.addNode(actTableFullNode);
            addEdge(g, actTableFullNode, g.findNode("actionType", "action_type", actTableFull.actionType),
                    "hasActionType");
            addEdge(g, actTableFullNode, g.findNode("targetClass", "l1", actTableFull.targetClass), "hasTargetClass");
            addEdge(g, actTableFullNode, g.findNode("structures", "id", actTableFull.structId), "hasStructure");
            addEdge(g, actTableFullNode, g.findNode("targetDictionary", "id", actTableFull.targetId),
                    "hasTargetDictionary");
            addEdge(g, actTableFullNode, g.findNode("reference", "id", actTableFull.actRefId), "hasReference");
            addEdge(g, actTableFullNode, g.findNode("reference", "id", actTableFull.moaRefId), "hasReference");
        }
        for (ApprovalType approvalType : getDataList(dataSource, ApprovalType.class)) {
            Node approvalTypeNode = createNode(g, "approvalType");
            approvalTypeNode.setProperty("id", approvalType.id);
            approvalTypeNode.setProperty("description", approvalType.description);
            g.addNode(approvalTypeNode);
        }
        for (Approval approval : getDataList(dataSource, Approval.class)) {
            Node approvalNode = createNode(g, "approval");
            approvalNode.setProperty("id", approval.id);
            approvalNode.setProperty("struct_id", approval.structId);
            approvalNode.setProperty("approval", approval.approval);
            approvalNode.setProperty("type", approval.type);
            approvalNode.setProperty("applicant", approval.applicant);
            approvalNode.setProperty("orphan", approval.orphan);
            g.addNode(approvalNode);
            addEdge(g, approvalNode, g.findNode("structures", "id", approval.structId), "hasStructure");
            addEdge(g, approvalNode, g.findNode("approvalType", "descr", approval.type), "hasStructure");
        }
        for (Atc atc : getDataList(dataSource, Atc.class)) {
            Node atcNode = createNode(g, "atc");
            atcNode.setProperty("id", atc.id);
            atcNode.setProperty("code", atc.code);
            atcNode.setProperty("chemical_substance", atc.chemicalSubstance);
            atcNode.setProperty("l1_code", atc.l1Code);
            atcNode.setProperty("l1_name", atc.l1Name);
            atcNode.setProperty("l2_code", atc.l2Code);
            atcNode.setProperty("l2_name", atc.l2Name);
            atcNode.setProperty("l3_code", atc.l3Code);
            atcNode.setProperty("l3_name", atc.l3Name);
            atcNode.setProperty("l4_code", atc.l4Code);
            atcNode.setProperty("l4_name", atc.l4Name);
            atcNode.setProperty("chemical_substance_count", atc.chemicalSubstanceCount);
            g.addNode(atcNode);
        }
        for (AtcDdd atcDdd : getDataList(dataSource, AtcDdd.class)) {
            Node atcDddNode = createNode(g, "atcDdd");
            atcDddNode.setProperty("id", atcDdd.id);
            atcDddNode.setProperty("atc_code", atcDdd.actCode);
            atcDddNode.setProperty("ddd", atcDdd.ddd);
            atcDddNode.setProperty("unit_type", atcDdd.unitType);
            atcDddNode.setProperty("route", atcDdd.route);
            atcDddNode.setProperty("comment", atcDdd.comment);
            atcDddNode.setProperty("struct_id", atcDdd.structId);
            g.addNode(atcDddNode);
            addEdge(g, atcDddNode, g.findNode("structures", "id", atcDdd.structId), "hasStructure");
        }
        for (AttributeType attributeType : getDataList(dataSource, AttributeType.class)) {
            Node attributeTypeNode = createNode(g, "attributeType");
            attributeTypeNode.setProperty("id", attributeType.id);
            attributeTypeNode.setProperty("name", attributeType.name);
            attributeTypeNode.setProperty("type", attributeType.type);
            g.addNode(attributeTypeNode);
        }
        for (de.unibi.agbi.biodwh2.drugcentral.model.DataSource dataSource_dc : getDataList(dataSource,
                                                                                            de.unibi.agbi.biodwh2.drugcentral.model.DataSource.class)) {
            Node dataSourceNode = createNode(g, "dataSource");
            dataSourceNode.setProperty("src_id", dataSource_dc.srcId);
            dataSourceNode.setProperty("source_name", dataSource_dc.sourceName);
            g.addNode(dataSourceNode);
        }
        for (DbVersion dbVersion : getDataList(dataSource, DbVersion.class)) {
            Node dbVersionNode = createNode(g, "dbVersion");
            dbVersionNode.setProperty("version", dbVersion.version);
            dbVersionNode.setProperty("dtime", dbVersion.dtime);
            g.addNode(dbVersionNode);
        }
        for (DrugClass drugClass : getDataList(dataSource, DrugClass.class)) {
            Node drugClassNode = createNode(g, "drugClass");
            drugClassNode.setProperty("id", drugClass.id);
            drugClassNode.setProperty("name", drugClass.name);
            drugClassNode.setProperty("is_group", drugClass.isGroup);
            drugClassNode.setProperty("source", drugClass.source);
            g.addNode(drugClassNode);
        }
        for (Ddi ddi : getDataList(dataSource, Ddi.class)) {
            Node ddiNode = createNode(g, "ddi");
            ddiNode.setProperty("id", ddi.id);
            ddiNode.setProperty("drug_class1", ddi.drugClass1);
            ddiNode.setProperty("drug_class2", ddi.drugClass2);
            ddiNode.setProperty("ddi_ref_id", ddi.ddiRefId);
            ddiNode.setProperty("ddi_risk", ddi.ddiRisk);
            ddiNode.setProperty("description", ddi.description);
            ddiNode.setProperty("source_id", ddi.sourceId);
            g.addNode(ddiNode);
            addEdge(g, ddiNode, g.findNode("DrugClass", "name", ddi.drugClass1), "hasDrugClass");
            addEdge(g, ddiNode, g.findNode("DrugClass", "name", ddi.drugClass2), "hasDrugClass");
        }
        for (Doid doid : getDataList(dataSource, Doid.class)) {
            Node doidNode = createNode(g, "doid");
            doidNode.setProperty("id", doid.id);
            doidNode.setProperty("label", doid.label);
            doidNode.setProperty("doid", doid.doId);
            doidNode.setProperty("url", doid.url);
            g.addNode(doidNode);
        }
        for (DoidXref doidXref : getDataList(dataSource, DoidXref.class)) {
            Node doidXrefNode = createNode(g, "doidXref");
            doidXrefNode.setProperty("id", doidXref.id);
            doidXrefNode.setProperty("doid", doidXref.doid);
            doidXrefNode.setProperty("source", doidXref.source);
            doidXrefNode.setProperty("xref", doidXref.xref);
            g.addNode(doidXrefNode);
            addEdge(g, doidXrefNode, g.findNode("doid", "doid", doidXref.doid), "hasDoid");
        }
        for (Faers faers : getDataList(dataSource, Faers.class)) {
            Node faersNode = createNode(g, "faers");
            faersNode.setProperty("id", faers.id);
            faersNode.setProperty("struct_id", faers.structId);
            faersNode.setProperty("meddra_name", faers.meddraName);
            faersNode.setProperty("meddra_code", faers.meddraCode);
            faersNode.setProperty("level", faers.level);
            faersNode.setProperty("llr", faers.llr);
            faersNode.setProperty("llr_threshold", faers.llrThreshold);
            faersNode.setProperty("drug_ae", faers.drugAe);
            faersNode.setProperty("drug_no_ae", faers.drugNoAe);
            faersNode.setProperty("no_drug_ae", faers.noDrugAe);
            faersNode.setProperty("no_drug_no_ae", faers.noDrugNoAe);
            g.addNode(faersNode);
            addEdge(g, faersNode, g.findNode("structures", "id", faers.structId), "hasStructure");
        }
        for (IdType idType : getDataList(dataSource, IdType.class)) {
            Node idTypeNode = createNode(g, "idType");
            idTypeNode.setProperty("id", idType.id);
            idTypeNode.setProperty("type", idType.type);
            idTypeNode.setProperty("description", idType.description);
            idTypeNode.setProperty("url", idType.url);
            g.addNode(idTypeNode);
        }
        for (Identifier identifier : getDataList(dataSource, Identifier.class)) {
            Node identifierNode = createNode(g, "identifier");
            identifierNode.setProperty("id", identifier.id);
            identifierNode.setProperty("identifier", identifier.identifier);
            identifierNode.setProperty("id_type", identifier.idType);
            identifierNode.setProperty("struct_id", identifier.structId);
            identifierNode.setProperty("parent_match", identifier.parentMatch);
            g.addNode(identifierNode);
            addEdge(g, identifierNode, g.findNode("idType", "type", identifier.idType), "hasIdType");
            addEdge(g, identifierNode, g.findNode("structures", "id", identifier.structId), "hasStructure");
        }
        for (Label label : getDataList(dataSource, Label.class)) {
            Node labelNode = createNode(g, "label");
            labelNode.setProperty("id", label.id);
            labelNode.setProperty("category", label.category);
            labelNode.setProperty("title", label.title);
            labelNode.setProperty("effective_date", label.effectiveDate);
            labelNode.setProperty("assigned_entity", label.assignedEntity);
            labelNode.setProperty("pdf_url", label.pdfUrl);
            g.addNode(labelNode);
        }
        for (LincsSignature lincsSignature : getDataList(dataSource, LincsSignature.class)) {
            Node lincsSignatureNode = createNode(g, "lincsSignature");
            lincsSignatureNode.setProperty("id", lincsSignature.id);
            lincsSignatureNode.setProperty("struct_id1", lincsSignature.structId1);
            lincsSignatureNode.setProperty("struct_id2", lincsSignature.structId2);
            lincsSignatureNode.setProperty("is_parent1", lincsSignature.isParent1);
            lincsSignatureNode.setProperty("is_parent2", lincsSignature.isParent2);
            lincsSignatureNode.setProperty("cell_id", lincsSignature.cellId);
            lincsSignatureNode.setProperty("rmsd", lincsSignature.rmsd);
            lincsSignatureNode.setProperty("rmsd_norm", lincsSignature.rmsdNorm);
            lincsSignatureNode.setProperty("pearson", lincsSignature.pearson);
            lincsSignatureNode.setProperty("euclid", lincsSignature.euclid);
            g.addNode(lincsSignatureNode);
        }
        for (ObExclusivityCode obExclusivityCode : getDataList(dataSource, ObExclusivityCode.class)) {
            Node obExclusivityCodeNode = createNode(g, "obExclusivityCode");
            obExclusivityCodeNode.setProperty("code", obExclusivityCode.code);
            obExclusivityCodeNode.setProperty("description", obExclusivityCode.description);
            g.addNode(obExclusivityCodeNode);
        }
        for (ObExclusivity obExclusivity : getDataList(dataSource, ObExclusivity.class)) {
            Node obExclusivityNode = createNode(g, "obExclusivity");
            obExclusivityNode.setProperty("id", obExclusivity.id);
            obExclusivityNode.setProperty("appl_type", obExclusivity.applType);
            obExclusivityNode.setProperty("appl_no", obExclusivity.applNo);
            obExclusivityNode.setProperty("product_no", obExclusivity.productNo);
            obExclusivityNode.setProperty("exclusivity_code", obExclusivity.exclusivityCode);
            obExclusivityNode.setProperty("exclusivity_date", obExclusivity.exclusivityDate);
            g.addNode(obExclusivityNode);
            addEdge(g, obExclusivityNode, g.findNode("obExclusivityCode", "code", obExclusivity.exclusivityCode),
                    "hasObExclusivityCode");
        }
        for (ObPatent obPatent : getDataList(dataSource, ObPatent.class)) {
            Node obPatentNode = createNode(g, "obPatent");
            obPatentNode.setProperty("id", obPatent.id);
            obPatentNode.setProperty("appl_type", obPatent.applType);
            obPatentNode.setProperty("appl_no", obPatent.applNo);
            obPatentNode.setProperty("product_no", obPatent.productNo);
            obPatentNode.setProperty("patent_no", obPatent.parentNo);
            obPatentNode.setProperty("patent_expire_date", obPatent.patentExpireDate);
            obPatentNode.setProperty("drug_substance_flag", obPatent.drugSubstanceFlag);
            obPatentNode.setProperty("drug_product_flag", obPatent.drugProductFlag);
            obPatentNode.setProperty("patent_use_code", obPatent.patentUseCode);
            obPatentNode.setProperty("delist_flag", obPatent.delistFlag);
            g.addNode(obPatentNode);
        }
        for (ObPatentUseCode obPatentUseCode : getDataList(dataSource, ObPatentUseCode.class)) {
            Node obPatentUseCodeNode = createNode(g, "obPatentUseCode");
            obPatentUseCodeNode.setProperty("code", obPatentUseCode.code);
            obPatentUseCodeNode.setProperty("description", obPatentUseCode.description);
        }
        for (ObProduct obProduct : getDataList(dataSource, ObProduct.class)) {
            Node obProductNode = createNode(g, "obProduct");
            obProductNode.setProperty("id", obProduct.id);
            obProductNode.setProperty("ingredient", obProduct.ingredient);
            obProductNode.setProperty("trade_name", obProduct.tradeName);
            obProductNode.setProperty("applicant", obProduct.applicant);
            obProductNode.setProperty("strength", obProduct.strength);
            obProductNode.setProperty("appl_type", obProduct.applType);
            obProductNode.setProperty("te_code", obProduct.teCode);
            obProductNode.setProperty("approval_date", obProduct.approvalDate);
            obProductNode.setProperty("rld", obProduct.rld);
            obProductNode.setProperty("type", obProduct.type);
            obProductNode.setProperty("applicant_full_name", obProduct.applicantFullName);
            obProductNode.setProperty("dose_form", obProduct.doseForm);
            obProductNode.setProperty("route", obProduct.route);
            obProductNode.setProperty("product_no", obProduct.productNo);
            g.addNode(obProductNode);
        }
        for (OmopRelationship omopRelationship : getDataList(dataSource, OmopRelationship.class)) {
            Node omopRelationshipNode = createNode(g, "omopRelationship");
            omopRelationshipNode.setProperty("id", omopRelationship.id);
            omopRelationshipNode.setProperty("struct_id", omopRelationship.structId);
            omopRelationshipNode.setProperty("concept_id", omopRelationship.conceptId);
            omopRelationshipNode.setProperty("relationship_name", omopRelationship.relationshipName);
            omopRelationshipNode.setProperty("concept_name", omopRelationship.conceptName);
            omopRelationshipNode.setProperty("umls_cui", omopRelationship.umlsCui);
            omopRelationshipNode.setProperty("snomed_full_name", omopRelationship.snomedFullName);
            omopRelationshipNode.setProperty("cui_semantic_type", omopRelationship.cuiSemanticType);
            omopRelationshipNode.setProperty("snomed_conceptid", omopRelationship.snomedConceptId);
            g.addNode(omopRelationshipNode);
            addEdge(g, omopRelationshipNode, g.findNode("structures", "id", omopRelationship.structId), "hasStructure");
        }
        for (Parentmol parentmol : getDataList(dataSource, Parentmol.class)) {
            Node parentmolNode = createNode(g, "parentmol");
            parentmolNode.setProperty("cd_id", parentmol.cdId);
            parentmolNode.setProperty("name", parentmol.name);
            parentmolNode.setProperty("cas_reg_no", parentmol.casRegNo);
            parentmolNode.setProperty("inchi", parentmol.inchi);
            parentmolNode.setProperty("no_stereo_inchi", parentmol.nostereoInchi);
            parentmolNode.setProperty("molfile", parentmol.molfile);
            parentmolNode.setProperty("molimg", parentmol.molimg);
            parentmolNode.setProperty("smiles", parentmol.smiles);
            parentmolNode.setProperty("inchikey", parentmol.inchiKey);
            g.addNode(parentmolNode);
        }
        for (Pdb pdb : getDataList(dataSource, Pdb.class)) {
            Node pdbNode = createNode(g, "pdb");
            pdbNode.setProperty("id", pdb.id);
            pdbNode.setProperty("struct_id", pdb.structId);
            pdbNode.setProperty("pdb", pdb.pdb);
            pdbNode.setProperty("chain_id", pdb.chainId);
            pdbNode.setProperty("accession", pdb.accession);
            pdbNode.setProperty("title", pdb.title);
            pdbNode.setProperty("pubmed_id", pdb.pubmedId);
            pdbNode.setProperty("exp_method", pdb.expMethod);
            pdbNode.setProperty("deposition_date", pdb.depositionDate);
            pdbNode.setProperty("ligand_id", pdb.ligandId);
            g.addNode(pdbNode);
            addEdge(g, pdbNode, g.findNode("structures", "id", pdb.structId), "hasStructure");
        }
        for (PharmaClass pharmaClass : getDataList(dataSource, PharmaClass.class)) {
            Node pharmaClassNode = createNode(g, "pharmaClass");
            pharmaClassNode.setProperty("id", pharmaClass.id);
            pharmaClassNode.setProperty("struct_id", pharmaClass.structId);
            pharmaClassNode.setProperty("type", pharmaClass.type);
            pharmaClassNode.setProperty("name", pharmaClass.name);
            pharmaClassNode.setProperty("class_code", pharmaClass.classCode);
            pharmaClassNode.setProperty("source", pharmaClass.source);
            g.addNode(pharmaClassNode);
            addEdge(g, pharmaClassNode, g.findNode("structures", "id", pharmaClass.structId), "hasStructure");
        }
        for (Pka pka : getDataList(dataSource, Pka.class)) {
            Node pkaNode = createNode(g, "pka");
            pkaNode.setProperty("id", pka.id);
            pkaNode.setProperty("struct_id", pka.structId);
            pkaNode.setProperty("pka_level", pka.pkaLevel);
            pkaNode.setProperty("value", pka.value);
            pkaNode.setProperty("pka_type", pka.pkaType);
            addEdge(g, pkaNode, g.findNode("structures", "id", pka.structId), "hasStructure");
            g.addNode(pkaNode);
        }
        for (Prd2Label prd2Label : getDataList(dataSource, Prd2Label.class)) {
            Node prd2LabelNode = createNode(g, "prd2Label");
            prd2LabelNode.setProperty("ndc_product_code", prd2Label.ndcProductCode);
            prd2LabelNode.setProperty("label_id", prd2Label.labelId);
            prd2LabelNode.setProperty("id", prd2Label.id);
            addEdge(g, prd2LabelNode, g.findNode("label", "id", prd2Label.labelId), "hasLabel");
            addEdge(g, prd2LabelNode, g.findNode("product", "ndc_product_code", prd2Label.ndcProductCode),
                    "hasProduct");
        }
        for (ProteinType proteinType : getDataList(dataSource, ProteinType.class)) {
            Node proteinTypeNode = createNode(g, "proteinType");
            proteinTypeNode.setProperty("id", proteinType.id);
            proteinTypeNode.setProperty("type", proteinType.type);
            g.addNode(proteinTypeNode);
        }
        for (Section section : getDataList(dataSource, Section.class)) {
            Node sectionNode = createNode(g, "section");
            sectionNode.setProperty("id", section.id);
            sectionNode.setProperty("text", section.text);
            sectionNode.setProperty("label_id", section.labelId);
            sectionNode.setProperty("code", section.code);
            sectionNode.setProperty("title", section.title);
            g.addNode(sectionNode);
            addEdge(g, sectionNode, g.findNode("label", "id", section.labelId), "hasLabel");
        }
        for (Struct2Atc struct2Atc : getDataList(dataSource, Struct2Atc.class)) {
            Node struct2AtcNode = createNode(g, "struct2Atc");
            struct2AtcNode.setProperty("struct_id", struct2Atc.structId);
            struct2AtcNode.setProperty("atc_code", struct2Atc.atcCode);
            struct2AtcNode.setProperty("id", struct2Atc.id);
            addEdge(g, struct2AtcNode, g.findNode("structures", "id", struct2Atc.structId), "hasStructure");
            addEdge(g, struct2AtcNode, g.findNode("atc", "code", struct2Atc.atcCode), "hasAtcCode");
        }

        for (Struct2DrgClass struct2DrgClass : getDataList(dataSource, Struct2DrgClass.class)) {
            Node struct2DrgClassNode = createNode(g, "struct2DrgClass");
            struct2DrgClassNode.setProperty("id", struct2DrgClass.id);
            struct2DrgClassNode.setProperty("struct_id", struct2DrgClass.structId);
            struct2DrgClassNode.setProperty("druc_class_id", struct2DrgClass.drugClassId);
            addEdge(g, struct2DrgClassNode, g.findNode("structures", "id", struct2DrgClass.structId), "hasStructure");
            addEdge(g, struct2DrgClassNode, g.findNode("drugClass", "id", struct2DrgClass.drugClassId), "hasDrugClass");
        }
        for (Struct2ObProd struct2ObProd : getDataList(dataSource, Struct2ObProd.class)) {
            Node struct2ObProdNode = createNode(g, "struct2ObProd");
            struct2ObProdNode.setProperty("struct_id", struct2ObProd.structId);
            struct2ObProdNode.setProperty("prod_id", struct2ObProd.prodId);
            struct2ObProdNode.setProperty("strength", struct2ObProd.strength);
            addEdge(g, struct2ObProdNode, g.findNode("structures", "id", struct2ObProd.structId), "hasStructure");
            addEdge(g, struct2ObProdNode, g.findNode("obProduct", "id", struct2ObProd.prodId), "hasObProduct");
        }
        for (Struct2Parent struct2Parent : getDataList(dataSource, Struct2Parent.class)) {
            Node struct2ParentNode = createNode(g, "struct2Parent");
            struct2ParentNode.setProperty("struct_id", struct2Parent.structId);
            struct2ParentNode.setProperty("parent_id", struct2Parent.parentId);
            addEdge(g, struct2ParentNode, g.findNode("structures", "id", struct2Parent.structId), "hasStructure");
            addEdge(g, struct2ParentNode, g.findNode("parentmol", "cd_id", struct2Parent.parentId), "hasParentmol");
        }
        for (StructTypeDef structTypeDef : getDataList(dataSource, StructTypeDef.class)) {
            Node structTypeDefNode = createNode(g, "structTypeDef");
            structTypeDefNode.setProperty("id", structTypeDef.id);
            structTypeDefNode.setProperty("type", structTypeDef.type);
            structTypeDefNode.setProperty("description", structTypeDef.description);
            g.addNode(structTypeDefNode);
        }
        for (StructureType structureType : getDataList(dataSource, StructureType.class)) {
            Node structureTypeNode = createNode(g, "structureType");
            structureTypeNode.setProperty("id", structureType.id);
            structureTypeNode.setProperty("struct_id", structureType.structId);
            structureTypeNode.setProperty("type", structureType.type);
            g.addNode(structureTypeNode);
            addEdge(g, structureTypeNode, g.findNode("structures", "id", structureType.structId), "hasStructure");
            addEdge(g, structureTypeNode, g.findNode("structTypeDef", "type", structureType.type), "hasStructTypeDef");
        }
        for (Synonyms synonyms : getDataList(dataSource, Synonyms.class)) {
            Node synonymsNode = createNode(g, "synonyms");
            synonymsNode.setProperty("syn_id", synonyms.synId);
            synonymsNode.setProperty("id", synonyms.id);
            synonymsNode.setProperty("name", synonyms.name);
            synonymsNode.setProperty("preferred_name", synonyms.preferredName);
            synonymsNode.setProperty("parent_id", synonyms.parentId);
            synonymsNode.setProperty("lname", synonyms.lname);
            g.addNode(synonymsNode);
            addEdge(g, synonymsNode, g.findNode("parentmol", "cd_id", synonyms.parentId), "hasParentmol");
            addEdge(g, synonymsNode, g.findNode("structures", "id", synonyms.id), "hasStructure");
        }
        for (TargetComponent targetComponent : getDataList(dataSource, TargetComponent.class)) {
            Node targetComponentNode = createNode(g, "targetComponent");
            targetComponentNode.setProperty("id", targetComponent.id);
            targetComponentNode.setProperty("accession", targetComponent.accession);
            targetComponentNode.setProperty("swissprot", targetComponent.swissprot);
            targetComponentNode.setProperty("organism", targetComponent.organism);
            targetComponentNode.setProperty("name", targetComponent.name);
            targetComponentNode.setProperty("gene", targetComponent.gene);
            targetComponentNode.setProperty("geneid", targetComponent.geneId);
            targetComponentNode.setProperty("tdl", targetComponent.tdl);
            g.addNode(targetComponentNode);
        }
        for (TargetGo targetGo : getDataList(dataSource, TargetGo.class)) {
            Node targetGoNode = createNode(g, "targetGo");
            targetGoNode.setProperty("id", targetGo.id);
            targetGoNode.setProperty("term", targetGo.term);
            targetGoNode.setProperty("type", targetGo.type);
            g.addNode(targetGoNode);
        }
        for (TargetKeyword targetKeyword : getDataList(dataSource, TargetKeyword.class)) {
            Node targetKeywordNode = createNode(g, "targetKeyword");
            targetKeywordNode.setProperty("id", targetKeyword.id);
            targetKeywordNode.setProperty("descr", targetKeyword.descr);
            targetKeywordNode.setProperty("category", targetKeyword.category);
            targetKeywordNode.setProperty("keyword", targetKeyword.keyword);
            g.addNode(targetKeywordNode);
        }
        for (Td2Tc td2Tc : getDataList(dataSource, Td2Tc.class)) {
            Node td2TcNode = createNode(g, "td2Td");
            td2TcNode.setProperty("target_id", td2Tc.targetId);
            td2TcNode.setProperty("component_id", td2Tc.componentId);
            addEdge(g, td2TcNode, g.findNode("targetDictionary", "id", td2Tc.targetId), "hasTargetDictionary");
        }
        for (Tdgo2Tc tdgo2Tc : getDataList(dataSource, Tdgo2Tc.class)) {
            Node tdgo2TcNode = createNode(g, "tdGo2Tc");
            tdgo2TcNode.setProperty("id", tdgo2Tc.id);
            tdgo2TcNode.setProperty("go_id", tdgo2Tc.goId);
            tdgo2TcNode.setProperty("component_id", tdgo2Tc.componentId);
            addEdge(g, tdgo2TcNode, g.findNode("targetGo", "id", tdgo2Tc.goId), "hasTargetGo");
            addEdge(g, tdgo2TcNode, g.findNode("targetComponent", "id", tdgo2Tc.componentId), "hasTargetComponent");
        }
        for (Tdkey2Tc tdkey2Tc : getDataList(dataSource, Tdkey2Tc.class)) {
            Node tdkey2TcNode = createNode(g, "tdkey2Tc");
            tdkey2TcNode.setProperty("id", tdkey2Tc.id);
            tdkey2TcNode.setProperty("tdkey_id", tdkey2Tc.tdKeyId);
            tdkey2TcNode.setProperty("component_id", tdkey2Tc.componentId);
            addEdge(g, tdkey2TcNode, g.findNode("targetKeyword", "id", tdkey2Tc.tdKeyId), "hasTargetKeyword");
            addEdge(g, tdkey2TcNode, g.findNode("targetComponent", "id", tdkey2Tc.componentId), "hasTargetComponent");
        }
        return g;
    }

    private void addEdge(Graph g, Node nodeFrom, Node nodeTo, String label) {
        if (nodeFrom != null && nodeTo != null) {
            Edge e = new Edge(nodeFrom, nodeTo, label);
            g.addEdge(e);
        }
    }
}
