package de.unibi.agbi.biodwh2.pharmgkb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.pharmgkb.PharmGKBDataSource;
import de.unibi.agbi.biodwh2.pharmgkb.model.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class PharmGKBGraphExporter extends GraphExporter<PharmGKBDataSource> {
    private Set<String> existingIds = new HashSet<>();

    @Override
    protected boolean exportGraph(final Workspace workspace, final PharmGKBDataSource dataSource, final Graph graph) {
        graph.setNodeIndexPropertyKeys("id");
        addGenes(graph, dataSource.genes);
        addChemicals(graph, dataSource.chemicals);
        addDrugs(graph, dataSource.drugs);
        addPhenotypes(graph, dataSource.phenotyps);
        addVariants(graph, dataSource.variants);
        for (String keyName : dataSource.pathways.keySet())
            addPathway(graph, keyName, dataSource.pathways.get(keyName));
        addDrugLabels(graph, dataSource.drugLabels);
        addStudyParameters(graph, dataSource.studyParameters);
        addOccurrences(graph, dataSource.occurrences);
        addDrugLabelsByGene(graph, dataSource.drugLabelsByGenes);
        addClinicalAnnotations(graph, dataSource.clinicalAnnotations);
        addAutomatedAnnotations(graph, dataSource.automatedAnnotations);
        addClinicalVariants(graph, dataSource.clinicalVariants);
        addVariantDrugAnnotations(graph, dataSource.variantDrugAnnotations);
        addVariantFunctionalAnalysisAnnotations(graph, dataSource.variantFunctionalAnalysisAnnotations);
        addVariantPhenotypeAnnotations(graph, dataSource.variantPhenotypeAnnotations);
        addClinicalAnnotationMetadata(graph, dataSource.clinicalAnnotationMetadata);
        return true;
    }

    private void addGenes(final Graph graph, final List<Gene> genes) {
        for (Gene gene : genes) {
            existingIds.add(gene.pharmgkbAccessionId);
            Node node = createNodeFromModel(graph, gene);
            if (gene.crossReference != null)
                node.setProperty("cross_references", parseQuotedStringArray(gene.crossReference));
            if (gene.hgncId != null)
                node.setProperty("hgnc_ids", parseQuotedStringArray(gene.hgncId));
            if (gene.ncbiGeneId != null)
                node.setProperty("ncbi_gene_ids", parseQuotedStringArray(gene.ncbiGeneId));
            if (gene.ensembleId != null)
                node.setProperty("ensemble_ids", parseQuotedStringArray(gene.ensembleId));
            if (gene.alternateNames != null)
                node.setProperty("alternate_names", parseQuotedStringArray(gene.alternateNames));
            if (gene.alternateSymbols != null)
                node.setProperty("alternate_symbols", parseQuotedStringArray(gene.alternateSymbols));
            node.setProperty("is_vip", parseBoolean(gene.isVip));
            node.setProperty("has_variant_annotation", parseBoolean(gene.hasVariantAnnotation));
            node.setProperty("has_cpic_dosing_guideline", parseBoolean(gene.hasCpicDosingGuideline));
            graph.update(node);
        }
    }

    private String[] parseQuotedStringArray(String arrayString) {
        List<String> result = new ArrayList<>();
        if (arrayString != null)
            for (String value : arrayString.split("\",\""))
                result.add(StringUtils.strip(value, "\""));
        return result.toArray(new String[0]);
    }

    private boolean parseBoolean(String value) {
        return value.toLowerCase(Locale.US).equals("yes");
    }

    private String[] parseStringArray(String arrayString) {
        List<String> result = new ArrayList<>();
        if (arrayString != null)
            result.addAll(Arrays.asList(arrayString.split("(?<=[^3]),(?=[^ ])")));
        return result.toArray(new String[0]);
    }

    private void addChemicals(final Graph graph, final List<Chemical> chemicals) {
        for (Chemical chemical : chemicals) {
            existingIds.add(chemical.pharmgkbAccessionId);
            Node node = createNodeFromModel(graph, chemical);
            if (chemical.type != null)
                node.setProperty("types", parseQuotedStringArray(chemical.type));
            if (chemical.crossReference != null)
                node.setProperty("cross_references", parseQuotedStringArray(chemical.crossReference));
            if (chemical.externalVocabulary != null)
                node.setProperty("external_vocabulary", parseQuotedStringArray(chemical.externalVocabulary));
            if (chemical.atcIdentifiers != null)
                node.setProperty("atc_identifiers", parseQuotedStringArray(chemical.atcIdentifiers));
            if (chemical.pubChemCompoundIdentifiers != null)
                node.setProperty("pubchem_compound_identifiers",
                                 parseQuotedStringArray(chemical.pubChemCompoundIdentifiers));
            if (chemical.rxNormIdentifiers != null)
                node.setProperty("rxnorm_identifiers", parseQuotedStringArray(chemical.rxNormIdentifiers));
            if (chemical.genericNames != null)
                node.setProperty("generic_names", parseQuotedStringArray(chemical.genericNames));
            if (chemical.tradeNames != null)
                node.setProperty("trade_names", parseQuotedStringArray(chemical.tradeNames));
            if (chemical.brandMixtures != null)
                node.setProperty("brand_mixtures", parseQuotedStringArray(chemical.brandMixtures));
            node.setProperty("dosing_guideline", parseBoolean(chemical.dosingGuideline));
            node.setProperty("clinical_annotation_count", Integer.parseInt(chemical.clinicalAnnotationCount));
            node.setProperty("variant_annotation_count", Integer.parseInt(chemical.variantAnnotationCount));
            node.setProperty("pathway_count", Integer.parseInt(chemical.pathwayCount));
            node.setProperty("vip_count", Integer.parseInt(chemical.vipCount));
            if (chemical.dosingGuidelineSources != null)
                node.setProperty("dosing_guideline_sources", parseQuotedStringArray(chemical.dosingGuidelineSources));
            if (chemical.topClinicalAnnotationLevel != null)
                node.setProperty("top_clinical_annotation_level", chemical.topClinicalAnnotationLevel);
            if (chemical.topFdaLabelTestingLevel != null)
                node.setProperty("top_fda_label_testing_level", chemical.topFdaLabelTestingLevel);
            if (chemical.topAnyDrugLabelTestingLevel != null)
                node.setProperty("top_any_drug_label_testing_level", chemical.topAnyDrugLabelTestingLevel);
            if (chemical.labelHasDosingInfo != null)
                node.setProperty("label_has_dosing_info", chemical.labelHasDosingInfo);
            if (chemical.hasRxAnnotation != null)
                node.setProperty("has_rx_annotation", chemical.hasRxAnnotation);
            graph.update(node);
        }
    }

    private void addDrugs(final Graph graph, final List<Drug> drugs) {
        for (Drug drug : drugs) {
            existingIds.add(drug.pharmgkbAccessionId);
            Node node = createNodeFromModel(graph, drug);
            if (drug.type != null)
                node.setProperty("types", parseQuotedStringArray(drug.type));
            if (drug.crossReference != null)
                node.setProperty("cross_references", parseQuotedStringArray(drug.crossReference));
            if (drug.externalVocabulary != null)
                node.setProperty("external_vocabulary", parseQuotedStringArray(drug.externalVocabulary));
            if (drug.atcIdentifiers != null)
                node.setProperty("atc_identifiers", parseQuotedStringArray(drug.atcIdentifiers));
            if (drug.pubChemCompoundIdentifiers != null)
                node.setProperty("pubchem_compound_identifiers",
                                 parseQuotedStringArray(drug.pubChemCompoundIdentifiers));
            if (drug.rxNormIdentifiers != null)
                node.setProperty("rxnorm_identifiers", parseQuotedStringArray(drug.rxNormIdentifiers));
            if (drug.genericNames != null)
                node.setProperty("generic_names", parseQuotedStringArray(drug.genericNames));
            if (drug.tradeNames != null)
                node.setProperty("trade_names", parseQuotedStringArray(drug.tradeNames));
            if (drug.brandMixtures != null)
                node.setProperty("brand_mixtures", parseQuotedStringArray(drug.brandMixtures));
            node.setProperty("dosing_guideline", parseBoolean(drug.dosingGuideline));
            node.setProperty("clinical_annotation_count", Integer.parseInt(drug.clinicalAnnotationCount));
            node.setProperty("variant_annotation_count", Integer.parseInt(drug.variantAnnotationCount));
            node.setProperty("pathway_count", Integer.parseInt(drug.pathwayCount));
            node.setProperty("vip_count", Integer.parseInt(drug.vipCount));
            if (drug.dosingGuidelineSources != null)
                node.setProperty("dosing_guideline_sources", parseQuotedStringArray(drug.dosingGuidelineSources));
            if (drug.topClinicalAnnotationLevel != null)
                node.setProperty("top_clinical_annotation_level", drug.topClinicalAnnotationLevel);
            if (drug.topFdaLabelTestingLevel != null)
                node.setProperty("top_fda_label_testing_level", drug.topFdaLabelTestingLevel);
            if (drug.topAnyDrugLabelTestingLevel != null)
                node.setProperty("top_any_drug_label_testing_level", drug.topAnyDrugLabelTestingLevel);
            if (drug.labelHasDosingInfo != null)
                node.setProperty("label_has_dosing_info", drug.labelHasDosingInfo);
            if (drug.hasRxAnnotation != null)
                node.setProperty("has_rx_annotation", drug.hasRxAnnotation);
            graph.update(node);
        }
    }

    private void addPhenotypes(final Graph graph, final List<Phenotype> phenotypes) {
        for (Phenotype phenotype : phenotypes) {
            existingIds.add(phenotype.pharmgkbAccessionId);
            Node node = createNodeFromModel(graph, phenotype);
            if (phenotype.crossReference != null)
                node.setProperty("cross_references", parseQuotedStringArray(phenotype.crossReference));
            if (phenotype.externalVocabulary != null)
                node.setProperty("external_vocabulary", parseQuotedStringArray(phenotype.externalVocabulary));
            if (phenotype.alternateNames != null)
                node.setProperty("alternate_names", parseQuotedStringArray(phenotype.alternateNames));
            graph.update(node);
        }
    }

    private void addVariants(final Graph graph, final List<Variant> variants) {
        for (Variant variant : variants) {
            existingIds.add(variant.variantId);
            Node node = createNodeFromModel(graph, variant);
            if (variant.geneIds != null) {
                String[] geneIds = StringUtils.split(variant.geneIds, ",");
                node.setProperty("gene_ids", geneIds);
                for (String geneId : geneIds)
                    graph.addEdge(graph.findNode("Gene", "id", geneId), node, "HAS_VARIANT");
            }
            if (variant.geneSymbols != null)
                node.setProperty("gene_symbols", StringUtils.split(variant.geneSymbols, ","));
            if (variant.synonyms != null)
                node.setProperty("synonyms", parseQuotedStringArray(variant.synonyms));
            node.setProperty("variant_annotation_count", Integer.parseInt(variant.variantAnnotationCount));
            node.setProperty("clinical_annotation_count", Integer.parseInt(variant.clinicalAnnotationCount));
            node.setProperty("level12_clinical_annotation_count",
                             Integer.parseInt(variant.level12ClinicalAnnotationCount));
            node.setProperty("guideline_annotation_count", Integer.parseInt(variant.guidelineAnnotationCount));
            node.setProperty("label_annotation_count", Integer.parseInt(variant.labelAnnotationCount));
            graph.update(node);
        }
    }

    private void addPathway(Graph graph, String keyName, List<Pathway> pathways) {
        String[] parts = StringUtils.split(keyName, "-");
        String pathwayId = parts[0];
        existingIds.add(pathwayId);
        Node node = createNode(graph, "Pathway");
        node.setProperty("id", pathwayId);
        node.setProperty("name", parts[1].replace("_", " "));
        graph.update(node);
        for (int i = 0; i < pathways.size(); i++) {
            Pathway pathway = pathways.get(i);
            Node stepNode = createNodeFromModel(graph, pathway);
            stepNode.setProperty("step", i);
            stepNode.setProperty("pmids", StringUtils.split(pathway.pmids, ","));
            graph.update(stepNode);
            Edge e = graph.addEdge(node, stepNode, "HAS_PATHWAY_STEP");
            e.setProperty("step", i);
            graph.update(e);
        }
    }

    private void addDrugLabels(final Graph graph, final List<DrugLabel> drugLabels) {
        for (DrugLabel drugLabel : drugLabels) {
            Node node = createNodeFromModel(graph, drugLabel);
            if (drugLabel.hasDosingInfo != null)
                node.setProperty("has_dosing_info", drugLabel.hasDosingInfo);
            if (drugLabel.hasAlternateDrug != null)
                node.setProperty("has_alternate_drug", drugLabel.hasAlternateDrug);
            graph.update(node);
        }
    }

    private void addStudyParameters(final Graph graph, final List<StudyParameters> studyParameters) {
        for (StudyParameters studyParameter : studyParameters) {
            Node node = createNodeFromModel(graph, studyParameter);
            if (studyParameter.studyCases != null)
                node.setProperty("study_cases", Integer.parseInt(studyParameter.studyCases));
            if (studyParameter.studyControls != null)
                node.setProperty("study_controls", Integer.parseInt(studyParameter.studyControls));
            graph.update(node);
        }
    }

    private void addOccurrences(final Graph graph, final List<Occurrence> occurrences) {
        for (Occurrence occurrence : occurrences) {
            Node node = createNodeFromModel(graph, occurrence);
            switch (occurrence.objectType) {
                case "Haplotype":
                    Node haplotypeNode = addHaplotypeIfNotExists(graph, occurrence.objectId, occurrence.objectName);
                    graph.addEdge(haplotypeNode, node, "HAS_OCCURRENCE");
                    break;
                case "HaplotypeSet":
                    Node haplotypeSetNode = addHaplotypeSetIfNotExists(graph, occurrence.objectId,
                                                                       occurrence.objectName);
                    graph.addEdge(haplotypeSetNode, node, "HAS_OCCURRENCE");
                    break;
                case "Variant":
                    Node variantNode = addVariantIfNotExists(graph, occurrence.objectId, occurrence.objectName);
                    graph.addEdge(variantNode, node, "HAS_OCCURRENCE");
                    break;
                default:
                    String label = occurrence.objectType.replace("Disease", "Phenotype");
                    Node objectNode = graph.findNode(label, "id", occurrence.objectId);
                    graph.addEdge(objectNode, node, "HAS_OCCURRENCE");
                    break;
            }
        }
    }

    private Node addVariantIfNotExists(final Graph graph, final String variantId, String variantName) {
        if (existingIds.contains(variantId))
            return graph.findNode("Variant", "id", variantId);
        if (variantId.startsWith("rs")) {
            Node node = graph.findNode("Variant", "name", variantId);
            if (node != null)
                return node;
            variantName = variantName != null ? variantName : variantId;
        }
        Node node = createNode(graph, "Variant");
        node.setProperty("id", variantId);
        node.setProperty("name", variantName);
        graph.update(node);
        existingIds.add(variantId);
        return node;
    }

    private Node addHaplotypeIfNotExists(final Graph graph, final String haplotypeId, final String haplotypeName) {
        if (existingIds.contains(haplotypeId))
            return graph.findNode("Haplotype", "id", haplotypeId);
        Node node = createNode(graph, "Haplotype");
        node.setProperty("id", haplotypeId);
        node.setProperty("name", haplotypeName);
        graph.update(node);
        existingIds.add(haplotypeId);
        return node;
    }

    private Node addHaplotypeSetIfNotExists(final Graph graph, final String haplotypeId, final String haplotypeName) {
        if (existingIds.contains(haplotypeId))
            return graph.findNode("HaplotypeSet", "id", haplotypeId);
        Node node = createNode(graph, "HaplotypeSet");
        node.setProperty("id", haplotypeId);
        node.setProperty("name", haplotypeName);
        graph.update(node);
        existingIds.add(haplotypeId);
        return node;
    }

    private void addDrugLabelsByGene(final Graph graph, final List<DrugLabelsByGene> drugLabelsByGenes) {
        for (DrugLabelsByGene drugLabelsByGene : drugLabelsByGenes) {
            for (String labelId : drugLabelsByGene.labelIds.split(";")) {
                Node labelNode = graph.findNode("DrugLabel", "id", labelId);
                Node geneNode = graph.findNode("Gene", "id", drugLabelsByGene.geneId);
                graph.addEdge(labelNode, geneNode, "ASSOCIATED_WITH");
            }
        }
    }

    private void addClinicalAnnotations(final Graph graph, final List<ClinicalAnnotation> annotations) {
        for (ClinicalAnnotation annotation : annotations)
            createNodeFromModel(graph, annotation);
    }

    private void addAutomatedAnnotations(final Graph graph, final List<AutomatedAnnotation> annotations) {
        for (AutomatedAnnotation annotation : annotations) {
            Node node = createNodeFromModel(graph, annotation);
            if (annotation.publicationYear != null)
                node.setProperty("publication_year", Integer.parseInt(annotation.publicationYear));
            graph.update(node);
            if (annotation.geneIds != null)
                for (String geneId : StringUtils.split(annotation.geneIds, ","))
                    graph.addEdge(node, graph.findNode("Gene", "id", geneId), "ASSOCIATED_WITH");
            if (annotation.chemicalId != null) {
                Node chemicalNode = addChemicalIfNotExists(graph, annotation.chemicalId, annotation.chemicalName);
                graph.addEdge(node, chemicalNode, "ASSOCIATED_WITH");
            }
            if (annotation.variationId != null) {
                Node variantNode;
                if (annotation.variationType.equals("Haplotype"))
                    variantNode = addHaplotypeIfNotExists(graph, annotation.variationId, annotation.variationName);
                else
                    variantNode = addVariantIfNotExists(graph, annotation.variationId, annotation.variationName);
                graph.addEdge(node, variantNode, "ASSOCIATED_WITH");
            }
        }
    }

    private Node addChemicalIfNotExists(final Graph graph, final String chemicalId, final String chemicalName) {
        if (existingIds.contains(chemicalId))
            return graph.findNode("Chemical", "id", chemicalId);
        Node node = createNode(graph, "Chemical");
        node.setProperty("id", chemicalId);
        node.setProperty("name", chemicalName);
        graph.update(node);
        existingIds.add(chemicalId);
        return node;
    }

    private void addClinicalVariants(final Graph graph, final List<ClinicalVariant> clinicalVariants) {
        for (ClinicalVariant clinicalVariant : clinicalVariants) {
            Node node = createNodeFromModel(graph, clinicalVariant);
            if (clinicalVariant.gene != null)
                for (String geneId : StringUtils.split(clinicalVariant.gene, ",")) {
                    Node geneNode = graph.findNode("Gene", "symbol", geneId);
                    graph.addEdge(node, geneNode, "ASSOCIATED_WITH");
                }
            for (String chemical : parseStringArray(clinicalVariant.chemicals)) {
                Node chemicalNode = addChemicalIfNotExists(graph, chemical, null);
                graph.addEdge(node, chemicalNode, "ASSOCIATED_WITH");
            }
            for (String phenotype : parseStringArray(clinicalVariant.phenotypes)) {
                Node phenotypeNode = graph.findNode("Phenotype", "name", phenotype);
                graph.addEdge(node, phenotypeNode, "ASSOCIATED_WITH");
            }
            for (String variant : parseStringArray(clinicalVariant.variant)) {
                Node variantNode;
                if (variant.contains("rs"))
                    variantNode = addVariantIfNotExists(graph, variant, null);
                else
                    variantNode = addHaplotypeIfNotExists(graph, variant, null);
                graph.addEdge(node, variantNode, "ASSOCIATED_WITH");
            }
        }
    }

    private void addVariantDrugAnnotations(final Graph graph, final List<VariantDrugAnnotation> annotations) {
        for (VariantDrugAnnotation annotation : annotations) {
            Node node = createNodeFromModel(graph, annotation);
            if (annotation.phenotypeCategory != null)
                node.setProperty("phenotype_categories", parseQuotedStringArray(annotation.phenotypeCategory));
            graph.update(node);
            if (annotation.gene != null) {
                for (String gene : parseQuotedStringArray(annotation.gene)) {
                    String[] parts = StringUtils.split(gene, "(");
                    String geneId = parts[parts.length - 1].replace(")", "").trim();
                    Node geneNode = graph.findNode("Gene", "id", geneId);
                    graph.addEdge(node, geneNode, "ASSOCIATED_WITH");
                }
            }
            if (annotation.chemical != null) {
                for (String chemical : parseQuotedStringArray(annotation.chemical)) {
                    String[] parts = StringUtils.split(chemical, "(");
                    String chemicalId = parts[parts.length - 1].replace(")", "").trim();
                    Node chemicalNode = graph.findNode("Chemical", "id", chemicalId);
                    graph.addEdge(node, chemicalNode, "ASSOCIATED_WITH");
                }
            }
            if (annotation.studyParameters != null) {
                for (String studyParametersId : parseQuotedStringArray(annotation.studyParameters)) {
                    Node studyParametersNode = graph.findNode("StudyParameters", "id", studyParametersId);
                    graph.addEdge(node, studyParametersNode, "WITH_PARAMETERS");
                }
            }
            for (String variant : parseStringArray(annotation.variant)) {
                variant = variant.trim();
                Node variantNode;
                if (variant.contains("rs"))
                    variantNode = addVariantIfNotExists(graph, variant, null);
                else
                    variantNode = addHaplotypeIfNotExists(graph, variant, null);
                graph.addEdge(node, variantNode, "ASSOCIATED_WITH");
            }
        }
    }

    private void addVariantFunctionalAnalysisAnnotations(final Graph graph,
                                                         final List<VariantFunctionalAnalysisAnnotation> annotations) {
        for (VariantFunctionalAnalysisAnnotation annotation : annotations) {
            Node node = createNodeFromModel(graph, annotation);
            if (annotation.phenotypeCategory != null)
                node.setProperty("phenotype_categories", parseQuotedStringArray(annotation.phenotypeCategory));
            graph.update(node);
            if (annotation.gene != null) {
                for (String gene : parseQuotedStringArray(annotation.gene)) {
                    String[] parts = StringUtils.split(gene, "(");
                    String geneId = parts[parts.length - 1].replace(")", "").trim();
                    Node geneNode = graph.findNode("Gene", "id", geneId);
                    graph.addEdge(node, geneNode, "ASSOCIATED_WITH");
                }
            }
            if (annotation.chemical != null) {
                for (String chemical : parseQuotedStringArray(annotation.chemical)) {
                    String[] parts = StringUtils.split(chemical, "(");
                    String chemicalId = parts[parts.length - 1].replace(")", "").trim();
                    Node chemicalNode = graph.findNode("Chemical", "id", chemicalId);
                    graph.addEdge(node, chemicalNode, "ASSOCIATED_WITH");
                }
            }
            if (annotation.studyParameters != null) {
                for (String studyParametersId : parseQuotedStringArray(annotation.studyParameters)) {
                    Node studyParametersNode = graph.findNode("StudyParameters", "id", studyParametersId);
                    graph.addEdge(node, studyParametersNode, "WITH_PARAMETERS");
                }
            }
            for (String variant : parseStringArray(annotation.variant)) {
                variant = variant.trim();
                Node variantNode;
                if (variant.contains("rs"))
                    variantNode = addVariantIfNotExists(graph, variant, null);
                else
                    variantNode = addHaplotypeIfNotExists(graph, variant, null);
                graph.addEdge(node, variantNode, "ASSOCIATED_WITH");
            }
        }
    }

    private void addVariantPhenotypeAnnotations(final Graph graph, final List<VariantPhenotypeAnnotation> annotations) {
        for (VariantPhenotypeAnnotation annotation : annotations) {
            Node node = createNodeFromModel(graph, annotation);
            if (annotation.phenotypeCategory != null)
                node.setProperty("phenotype_categories", parseQuotedStringArray(annotation.phenotypeCategory));
            graph.update(node);
            if (annotation.gene != null) {
                for (String gene : parseQuotedStringArray(annotation.gene)) {
                    String[] parts = StringUtils.split(gene, "(");
                    String geneId = parts[parts.length - 1].replace(")", "").trim();
                    Node geneNode = graph.findNode("Gene", "id", geneId);
                    graph.addEdge(node, geneNode, "ASSOCIATED_WITH");
                }
            }
            if (annotation.chemical != null) {
                for (String chemical : parseQuotedStringArray(annotation.chemical)) {
                    String[] parts = StringUtils.split(chemical, "(");
                    String chemicalId = parts[parts.length - 1].replace(")", "").trim();
                    Node chemicalNode = graph.findNode("Chemical", "id", chemicalId);
                    graph.addEdge(node, chemicalNode, "ASSOCIATED_WITH");
                }
            }
            if (annotation.studyParameters != null) {
                for (String studyParametersId : parseQuotedStringArray(annotation.studyParameters)) {
                    Node studyParametersNode = graph.findNode("StudyParameters", "id", studyParametersId);
                    graph.addEdge(node, studyParametersNode, "WITH_PARAMETERS");
                }
            }
            for (String variant : parseStringArray(annotation.variant)) {
                variant = variant.trim();
                Node variantNode;
                if (variant.contains("rs"))
                    variantNode = addVariantIfNotExists(graph, variant, null);
                else
                    variantNode = addHaplotypeIfNotExists(graph, variant, null);
                graph.addEdge(node, variantNode, "ASSOCIATED_WITH");
            }
        }
    }

    private void addClinicalAnnotationMetadata(final Graph graph,
                                               final List<ClinicalAnnotationMetadata> clinicalAnnotationMetadata) {
        for (ClinicalAnnotationMetadata metadata : clinicalAnnotationMetadata) {
            Node node = createNodeFromModel(graph, metadata);
            if (metadata.evidenceCount != null)
                node.setProperty("evidence_count", Integer.parseInt(metadata.evidenceCount));
            if (metadata.clinicalAnnotationTypes != null)
                node.setProperty("clinical_annotation_types", StringUtils.split(metadata.clinicalAnnotationTypes, ";"));
            if (metadata.pmids != null)
                node.setProperty("pmids", StringUtils.split(metadata.pmids, ";"));
            graph.update(node);
            if (metadata.gene != null) {
                for (String gene : StringUtils.split(metadata.gene, ";")) {
                    String[] parts = StringUtils.split(gene, "(");
                    String geneId = parts[parts.length - 1].replace(")", "").trim();
                    Node geneNode = graph.findNode("Gene", "id", geneId);
                    graph.addEdge(node, geneNode, "ASSOCIATED_WITH");
                }
            }
            if (metadata.relatedChemicals != null) {
                for (String chemical : StringUtils.split(metadata.relatedChemicals, ";")) {
                    String[] parts = StringUtils.split(chemical, "(");
                    String chemicalId = parts[parts.length - 1].replace(")", "").trim();
                    Node chemicalNode = graph.findNode("Chemical", "id", chemicalId);
                    graph.addEdge(node, chemicalNode, "ASSOCIATED_WITH");
                }
            }
            if (metadata.relatedDiseases != null) {
                for (String disease : StringUtils.split(metadata.relatedDiseases, ";")) {
                    String[] parts = StringUtils.split(disease, "(");
                    String diseaseId = parts[parts.length - 1].replace(")", "").trim();
                    Node phenotypeNode = graph.findNode("Phenotype", "id", diseaseId);
                    graph.addEdge(node, phenotypeNode, "ASSOCIATED_WITH");
                }
            }
            if (metadata.genotypePhenotypesId != null) {
                for (String genotypePhenotypeId : StringUtils.split(metadata.genotypePhenotypesId, ";")) {
                    Node annotationNode = graph.findNode("ClinicalAnnotation", "id", genotypePhenotypeId);
                    graph.addEdge(node, annotationNode, "ASSOCIATED_WITH");
                }
            }
            if (metadata.variantAnnotationsId != null) {
                String[] ids = StringUtils.split(metadata.variantAnnotationsId, ";");
                String[] texts = StringUtils.split(metadata.variantAnnotation, ";");
                for (int i = 0; i < ids.length; i++) {
                    Node annotationNode = graph.findNode("VariantDrugAnnotation", "id", ids[i]);
                    if (annotationNode == null)
                        annotationNode = graph.findNode("VariantFunctionalAnalysisAnnotation", "id", ids[i]);
                    if (annotationNode == null)
                        annotationNode = graph.findNode("VariantPhenotypeAnnotation", "id", ids[i]);
                    Edge e = graph.addEdge(node, annotationNode, "ASSOCIATED_WITH");
                    e.setProperty("annotation", texts[i]);
                    graph.update(e);
                }
            }
        }
    }
}
