package de.unibi.agbi.biodwh2.pharmgkb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.pharmgkb.PharmGKBDataSource;
import de.unibi.agbi.biodwh2.pharmgkb.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PharmGKBGraphExporter extends GraphExporter<PharmGKBDataSource> {
    private static final Logger logger = LoggerFactory.getLogger(PharmGKBGraphExporter.class);

    private HashMap<String, Node> idNodeMap = new HashMap<>();
    private HashMap<String, Node> geneSymbolNodeMap = new HashMap<>();
    private HashMap<String, Node> phenotypeNameNodeMap = new HashMap<>();
    private HashMap<String, Node> variantNameNodeMap = new HashMap<>();
    private HashMap<String, Node> chemicalNameNodeMap = new HashMap<>();
    private HashMap<String, Node> genotype_phenotype_id = new HashMap<>();
    private HashMap<String, Node> studyParametersIdNodeMap = new HashMap<>();
    private HashMap<String, Node> variant_annotation_id = new HashMap<>();

    @Override
    protected boolean exportGraph(final Workspace workspace, final PharmGKBDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        for (String keyName : dataSource.pathways.keySet())
            addPathway(graph, keyName, dataSource.pathways.get(keyName));
        addGenes(graph, dataSource.genes);
        addChemicals(graph, dataSource.chemicals);
        addDrugs(graph, dataSource.drugs);
        addPhenotypes(graph, dataSource.phenotyps);
        addVariants(graph, dataSource.variants);
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

    private void addGenes(final Graph graph, final List<Gene> genes) throws ExporterException {
        for (Gene gene : genes) {
            Node node = createNode(graph, "Gene");
            idNodeMap.put(gene.pharmgkbAccessionId, node);
            String id = "PharmGKB:" + gene.pharmgkbAccessionId;
            Set<String> ids = new HashSet<>();
            ids.add(id);
            if (gene.ncbiGeneId != null)
                ids.add("NCBI Gene:" + gene.ncbiGeneId);
            if (gene.hgncId != null)
                ids.add("HGNC:" + gene.hgncId);
            if (gene.ensembleId != null)
                ids.add("Ensembl:" + gene.ensembleId);
            if (gene.crossReference != null)
                Collections.addAll(ids, parseQuotedStringArray(gene.crossReference));
            Set<String> names = new HashSet<>();
            names.add(gene.name);
            if (gene.alternateNames != null)
                Collections.addAll(names, parseQuotedStringArray(gene.alternateNames));
            node.setProperty("_id", id);
            node.setProperty("ids", ids.toArray(new String[0]));
            node.setProperty("name", gene.name);
            node.setProperty("names", names.toArray(new String[0]));
            node.setProperty("symbol", gene.symbol);
            node.setProperty("alternate_symbols", parseQuotedStringArray(gene.alternateSymbols));
            node.setProperty("is_vip", gene.isVip);
            node.setProperty("has_variant_annotation", gene.hasVariantAnnotation);
            node.setProperty("has_cpic_dosing_guideline", gene.hasCpicDosingGuideline);
            node.setProperty("chromosome", gene.chromosome);
            node.setProperty("chromosomal_start_grch37_p13", gene.chromosomalStartGrch37P13);
            node.setProperty("chromosomal_stop_grch37_p13", gene.chromosomalStopGrch37P13);
            node.setProperty("chromosomal_start_grch38_p7", gene.chromosomalStartGrch38P7);
            node.setProperty("chromosomal_stop_grch38_p7", gene.chromosomalStopGrch38P7);
            geneSymbolNodeMap.put(gene.symbol, node);
        }
    }

    private String[] parseQuotedStringArray(String arrayString) {
        List<String> result = new ArrayList<>();
        if (arrayString != null)
            for (String value : arrayString.split("\",\""))
                result.add(StringUtils.strip(value, "\""));
        return result.toArray(new String[0]);
    }

    private String[] parseStringArray(String arrayString) {
        List<String> result = new ArrayList<>();
        if (arrayString != null)
            result.addAll(Arrays.asList(arrayString.split("(?<=[^3]),(?=[^ ])")));
        return result.toArray(new String[0]);
    }

    private void addChemicals(final Graph graph, final List<Chemical> chemicals) throws ExporterException {
        for (Chemical chemical : chemicals) {
            Node node = createNode(graph, "Chemical");
            idNodeMap.put(chemical.pharmgkbAccessionId, node);
            String id = "PharmGKB:" + chemical.pharmgkbAccessionId;
            List<String> ids = new ArrayList<>();
            ids.add(id);
            Collections.addAll(ids, parseQuotedStringArray(chemical.crossReference));
            for (String externalId : parseQuotedStringArray(chemical.externalVocabulary))
                ids.add(externalId.split("\\(")[0]); // TODO: test
            for (String atcId : parseQuotedStringArray(chemical.atcIdentifiers))
                ids.add("ATC:" + atcId);
            if (chemical.pubChemCompoundIdentifiers != null)
                ids.add("PubChem Compound:" + chemical.pubChemCompoundIdentifiers);
            if (chemical.rxNormIdentifiers != null)
                ids.add("RxNorm:" + chemical.rxNormIdentifiers);
            Set<String> names = new HashSet<>();
            names.add(chemical.name);
            Collections.addAll(names, parseQuotedStringArray(chemical.genericNames));
            chemicalNameNodeMap.put(chemical.name, node);
            node.setProperty("_id", id);
            node.setProperty("ids", normalizeExternalIds(ids));
            node.setProperty("name", chemical.name);
            node.setProperty("names", names.toArray(new String[0]));
            node.setProperty("trade_names", parseQuotedStringArray(chemical.tradeNames));
            node.setProperty("brand_mixtures", parseQuotedStringArray(chemical.brandMixtures));
            node.setProperty("type", chemical.type);
            node.setProperty("smiles", chemical.smiles);
            node.setProperty("inchi", chemical.inchi);
            node.setProperty("dosing_guideline", chemical.dosingGuideline);
            node.setProperty("clinical_annotation_count", chemical.clinicalAnnotationCount);
            node.setProperty("variant_annotation_count", chemical.variantAnnotationCount);
            node.setProperty("pathway_count", chemical.pathwayCount);
            node.setProperty("vip_count", chemical.vipCount);
            node.setProperty("dosing_guideline_sources", parseQuotedStringArray(chemical.dosingGuidelineSources));
            node.setProperty("top_clinical_annotation_level", chemical.topClinicalAnnotationLevel);
            node.setProperty("top_fda_label_testing_level", chemical.topFdaLabelTestingLevel);
            node.setProperty("top_any_drug_label_testing_level", chemical.topAnyDrugLabelTestingLevel);
            node.setProperty("label_has_dosing_info", chemical.labelHasDosingInfo);
            node.setProperty("has_rx_annotation", chemical.hasRxAnnotation);
        }
    }

    private String[] normalizeExternalIds(List<String> ids) {
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            id = id.replace("Therapeutic Targets Database:", "TTD:");
            id = id.replace("KEGG Drug:", "KEGG:");
            id = id.replace("KEGG Compound:", "KEGG:");
            id = id.replace("Chemical Abstracts Service:", "CAS:");
            id = id.replace("ChEBI:CHEBI:", "ChEBI:");
            id = id.replace("Drugs Product Database (DPD):", "DPD:");
            id = id.replace("PubChem Compound:", "PubChem:CID");
            id = id.replace("PubChem Substance:", "PubChem:SID");
            ids.set(i, id);
        }
        return new HashSet<>(ids).toArray(new String[0]);
    }

    private void addDrugs(final Graph graph, final List<Drug> drugs) throws ExporterException {
        for (Drug drug : drugs) {
            Node node = createNode(graph, "Drug");
            idNodeMap.put(drug.pharmgkbAccessionId, node);
            String id = "PharmGKB:" + drug.pharmgkbAccessionId;
            List<String> ids = new ArrayList<>();
            ids.add(id);
            Collections.addAll(ids, parseQuotedStringArray(drug.crossReference));
            for (String externalId : parseQuotedStringArray(drug.externalVocabulary))
                ids.add(externalId.split("\\(")[0]); // TODO: test
            for (String atcId : parseQuotedStringArray(drug.atcIdentifiers))
                ids.add("ATC:" + atcId);
            if (drug.pubChemCompoundIdentifiers != null)
                ids.add("PubChem Compound:" + drug.pubChemCompoundIdentifiers);
            if (drug.rxNormIdentifiers != null)
                ids.add("RxNorm:" + drug.rxNormIdentifiers);
            Set<String> names = new HashSet<>();
            names.add(drug.name);
            Collections.addAll(names, parseQuotedStringArray(drug.genericNames));
            node.setProperty("_id", id);
            node.setProperty("ids", normalizeExternalIds(ids));
            node.setProperty("name", drug.name);
            node.setProperty("names", names.toArray(new String[0]));
            node.setProperty("trade_names", parseQuotedStringArray(drug.tradeNames));
            node.setProperty("brand_mixtures", parseQuotedStringArray(drug.brandMixtures));
            node.setProperty("type", drug.type);
            node.setProperty("smiles", drug.smiles);
            node.setProperty("inchi", drug.inchi);
            node.setProperty("dosing_guideline", drug.dosingGuideline);
            node.setProperty("clinical_annotation_count", drug.clinicalAnnotationCount);
            node.setProperty("variant_annotation_count", drug.variantAnnotationCount);
            node.setProperty("pathway_count", drug.pathwayCount);
            node.setProperty("vip_count", drug.vipCount);
            node.setProperty("dosing_guideline_sources", parseQuotedStringArray(drug.dosingGuidelineSources));
            node.setProperty("top_clinical_annotation_level", drug.topClinicalAnnotationLevel);
            node.setProperty("top_fda_label_testing_level", drug.topFdaLabelTestingLevel);
            node.setProperty("top_any_drug_label_testing_level", drug.topAnyDrugLabelTestingLevel);
            node.setProperty("label_has_dosing_info", drug.labelHasDosingInfo);
            node.setProperty("has_rx_annotation", drug.hasRxAnnotation);
        }
    }

    private void addPhenotypes(final Graph graph, final List<Phenotype> phenotypes) throws ExporterException {
        for (Phenotype phenotype : phenotypes) {
            Node node = createNode(graph, "Phenotype");
            idNodeMap.put(phenotype.pharmgkbAccessionId, node);
            String id = "PharmGKB:" + phenotype.pharmgkbAccessionId;
            List<String> ids = new ArrayList<>();
            ids.add(id);
            Collections.addAll(ids, parseQuotedStringArray(phenotype.crossReference));
            for (String externalId : parseQuotedStringArray(phenotype.externalVocabulary))
                ids.add(externalId.split("\\(")[0]); // TODO: test
            Set<String> names = new HashSet<>();
            names.add(phenotype.name);
            Collections.addAll(names, parseQuotedStringArray(phenotype.alternateNames));
            phenotypeNameNodeMap.put(phenotype.name, node);
            node.setProperty("_id", id);
            node.setProperty("ids", normalizeExternalIds(ids));
            node.setProperty("name", phenotype.name);
            node.setProperty("names", names.toArray(new String[0]));
        }
    }

    private void addVariants(final Graph graph, final List<Variant> variants) throws ExporterException {
        for (Variant variant : variants) {
            Node node = createNode(graph, "Variant");
            idNodeMap.put(variant.variantId, node);
            String id = "PharmGKB:" + variant.variantId;
            List<String> ids = new ArrayList<>();
            ids.add(id);
            Set<String> names = new HashSet<>();
            names.add(variant.variantName);
            Collections.addAll(names, parseQuotedStringArray(variant.synonyms));
            variantNameNodeMap.put(variant.variantName, node);
            node.setProperty("_id", id);
            node.setProperty("ids", normalizeExternalIds(ids));
            node.setProperty("name", variant.variantName);
            node.setProperty("names", names.toArray(new String[0]));
            if (variant.location != null)
                node.setProperty("location", variant.location);
            node.setProperty("variant_annotation_count", variant.variantAnnotationCount);
            node.setProperty("clinical_annotation_count", variant.clinicalAnnotationCount);
            node.setProperty("level_12_clinical_annotation_count", variant.level12ClinicalAnnotationCount);
            node.setProperty("guideline_annotation_count", variant.guidelineAnnotationCount);
            node.setProperty("label_annotation_count", variant.labelAnnotationCount);
            // TODO: variant.gene_symbols
            if (variant.geneIds != null)
                for (String geneId : variant.geneIds.split(","))
                    graph.addEdge(idNodeMap.get(geneId), node, "has_variant");
        }
    }

    private void addDrugLabels(final Graph graph, final List<DrugLabel> drugLabels) throws ExporterException {
        for (DrugLabel drugLabel : drugLabels) {
            Node node = createNode(graph, "DrugLabel");
            idNodeMap.put(drugLabel.pharmgkbAccessionId, node);
            String id = "PharmGKB:" + drugLabel.pharmgkbAccessionId;
            node.setProperty("_id", id);
            node.setProperty("name", drugLabel.name);
            node.setProperty("source", drugLabel.source);
            node.setProperty("biomarker_flag", drugLabel.biomarkerFlag);
            node.setProperty("testing_level", drugLabel.testingLevel);
            node.setProperty("has_dosing_info", drugLabel.hasDosingInfo);
            node.setProperty("has_alternate_drug", drugLabel.hasAlternateDrug);
            node.setProperty("cancer_genome", drugLabel.cancerGenome);
        }
    }

    private void addStudyParameters(final Graph graph,
                                    final List<StudyParameters> studyParameters) throws ExporterException {
        for (StudyParameters studyParameter : studyParameters) {
            Node node = createNode(graph, "StudyParameters");
            idNodeMap.put(studyParameter.studyParametersId, node);
            String id = "PharmGKB Study Parameter:" + studyParameter.studyParametersId;
            studyParametersIdNodeMap.put(studyParameter.studyParametersId, node);
            node.setProperty("_id", id);
            node.setProperty("study_type", studyParameter.studyType);
            node.setProperty("study_cases", studyParameter.studyCases);
            node.setProperty("study_controls", studyParameter.studyControls);
            node.setProperty("characteristics", studyParameter.characteristics);
            node.setProperty("characteristics_type", studyParameter.characteristicsType);
            node.setProperty("frequency_in_cases", studyParameter.frequencyInCases);
            node.setProperty("allele_of_frequency_in_cases", studyParameter.alleleOfFrequencyInCases);
            node.setProperty("frequency_in_controls", studyParameter.frequencyInControls);
            node.setProperty("allele_of_frequency_in_controls", studyParameter.alleleOfFrequencyInControls);
            node.setProperty("p_value_operator", studyParameter.pValueOperator);
            node.setProperty("p_value", studyParameter.pValue);
            node.setProperty("ratio_stat_type", studyParameter.ratioStatType);
            node.setProperty("ratio_stat", studyParameter.ratioStat);
            node.setProperty("confidence_interval_start", studyParameter.confidenceIntervalStart);
            node.setProperty("confidence_interval_stop", studyParameter.confidenceIntervalStop);
            node.setProperty("biogeographical_groups", studyParameter.biogeographicalGroups);
        }
    }

    private void addOccurrences(final Graph graph, final List<Occurrence> occurrences) throws ExporterException {
        for (Occurrence occurrence : occurrences) {
            Node node = createNode(graph, "Occurrence");
            node.setProperty("source_type", occurrence.sourceType);
            node.setProperty("source_id", occurrence.sourceId);
            node.setProperty("source_name", occurrence.sourceName);
            node.setProperty("object_type", occurrence.objectType);
            node.setProperty("object_id", occurrence.objectId);
            node.setProperty("object_name", occurrence.objectName);
            if (idNodeMap.containsKey(occurrence.objectId))
                graph.addEdge(idNodeMap.get(occurrence.objectId), node, "has_occurrence");
            //TODO: else
            //    logger.warn("PharmGKB ID not found (potential new haplotype data): " + occurrence.object_id);
        }
    }

    private void addDrugLabelsByGene(final Graph graph,
                                     final List<DrugLabelsByGene> drugLabelsByGenes) throws ExporterException {
        for (DrugLabelsByGene drugLabelsByGene : drugLabelsByGenes) {
            for (String labelId : drugLabelsByGene.labelIds.split(";")) {
                Edge edge = graph.addEdge(idNodeMap.get(labelId), idNodeMap.get(drugLabelsByGene.geneId),
                                          "associated_with");
                edge.setProperty("gene_symbol", drugLabelsByGene.geneSymbol);
                edge.setProperty("label_names", drugLabelsByGene.labelNames.split(";"));
            }
        }
    }

    private void addClinicalAnnotations(final Graph graph,
                                        final List<ClinicalAnnotation> clinicalAnnotations) throws ExporterException {
        for (ClinicalAnnotation clinicalAnnotation : clinicalAnnotations) {
            Node node = createNode(graph, "ClinicalAnnotation", "Annotation");
            node.setProperty("_id", clinicalAnnotation.genotypePhenotypeId);
            node.setProperty("genotype", clinicalAnnotation.genotype);
            node.setProperty("clinical_phenotype", clinicalAnnotation.clinicalPhenotype);
            genotype_phenotype_id.put(clinicalAnnotation.genotypePhenotypeId, node);
        }
    }

    private void addAutomatedAnnotations(final Graph graph,
                                         final List<AutomatedAnnotation> automatedAnnotations) throws ExporterException {
        for (AutomatedAnnotation automatedAnnotation : automatedAnnotations) {
            Node node = createNode(graph, "AutomatedAnnotation", "Annotation");
            // TODO
            if (automatedAnnotation.gene_ids != null)
                for (String geneId : automatedAnnotation.gene_ids.split(","))
                    graph.addEdge(node, idNodeMap.get(geneId), "has_gene");
            if (automatedAnnotation.chemical_id != null) {
                if (idNodeMap.containsKey(automatedAnnotation.chemical_id))
                    graph.addEdge(node, idNodeMap.get(automatedAnnotation.chemical_id), "has_chemical");
                else
                    logger.warn("Chemical not found: " + automatedAnnotation.chemical_id);
            }
            if (automatedAnnotation.variation_id != null) {
                if (automatedAnnotation.variation_id.contains("rs")) {
                    if (variantNameNodeMap.containsKey(automatedAnnotation.variation_id))
                        graph.addEdge(node, variantNameNodeMap.get(automatedAnnotation.variation_id), "has_variation");
                    else
                        logger.warn("Variant not found: " + automatedAnnotation.variation_id);
                } else {
                    if (idNodeMap.containsKey(automatedAnnotation.variation_id))
                        graph.addEdge(node, idNodeMap.get(automatedAnnotation.variation_id), "has_variation");
                    else
                        logger.warn("Variant not found: " + automatedAnnotation.variation_id);
                }
            }
        }
    }

    private void addClinicalVariants(final Graph graph,
                                     final List<ClinicalVariants> clinicalVariants) throws ExporterException {
        for (ClinicalVariants clinicalVariant : clinicalVariants) {
            Node node = createNode(graph, "ClinicalVariant");
            node.setProperty("type", clinicalVariant.type);
            node.setProperty("level_of_evidence", clinicalVariant.levelOfEvidence);
            if (clinicalVariant.gene != null)
                for (String geneId : clinicalVariant.gene.split(","))
                    graph.addEdge(node, geneSymbolNodeMap.get(geneId), "associated_with");
            for (String chemical : parseStringArray(clinicalVariant.chemicals)) {
                if (chemicalNameNodeMap.containsKey(chemical))
                    graph.addEdge(node, chemicalNameNodeMap.get(chemical), "associated_with");
                else
                    logger.warn("Chemical not found: " + chemical);
            }
            for (String phenotype : parseStringArray(clinicalVariant.phenotypes)) {
                if (phenotypeNameNodeMap.containsKey(phenotype))
                    graph.addEdge(node, phenotypeNameNodeMap.get(phenotype), "associated_with");
                else
                    logger.warn("Phenotype not found: " + phenotype);
            }
            if (clinicalVariant.variant != null) {
                if (clinicalVariant.variant.contains("rs")) {
                    if (variantNameNodeMap.containsKey(clinicalVariant.variant))
                        graph.addEdge(node, variantNameNodeMap.get(clinicalVariant.variant), "associated_with");
                    else
                        logger.warn("Variant name not found: " + clinicalVariant.variant);
                } else {
                    logger.warn("Not an rs number " + clinicalVariant.variant);
                }
            }
        }
    }

    private void addVariantDrugAnnotations(final Graph graph,
                                           final List<VariantDrugAnnotation> variantDrugAnnotations) {
        // TODO
    }

    private void addVariantFunctionalAnalysisAnnotations(final Graph graph,
                                                         final List<VariantFunctionalAnalysisAnnotation> variantFunctionalAnalysisAnnotations) {
        // TODO
        /*
        if (className.equals("VariantDrugAnnotations") || className.equals("VariantPhenotypeAnnotation") || className.equals("VariantFunctionalAnalysisAnnotation")) {
            if (fieldName.equals("gene")) {
                for (String geneId : parseStringArray(value)) {
                    String gid = geneId.split("\\(")[1].replace(")", "");
                    graph.addEdge(node, idNodeMap.get(gid), "has_gene", g);
                }
            } else if (fieldName.equals("study_parameters")) {
                for (String gpId : parseStringArray(value))
                    graph.addEdge(node, studyParametersIdNodeMap.get(gpId), "has_study_parameter", g);
            } else if (fieldName.equals("variant")) {
                if (value != null) {
                    if (value.contains("rs") && variantNameNodeMap.containsKey(value))
                        graph.addEdge(node, variantNameNodeMap.get(value), "has_variation", g);
                    else
                        logger.warn("Chemical not found: " + value);
                }
            } else if (fieldName.equals("annotation_id")) {
                variant_annotation_id.put(value, node);
            } else if (fieldName.equals("chemical")) {
                for (String chemicalId : parseStringArray(value)) {
                    String[] partList = chemicalId.split("\\(");
                    if (partList.length > 2) {
                        String cid = partList[partList.length - 1].replace(")", "");
                        if (idNodeMap.containsKey(cid)) {
                            graph.addEdge(node, idNodeMap.get(cid), "has_chemical", g);
                        } else {
                            logger.warn("Chemical ID not found: " + cid);
                        }
                    } else {
                        String cid = partList[1].replace(")", "").replace("\"", "");
                        if (idNodeMap.containsKey(cid)) {
                            graph.addEdge(node, idNodeMap.get(cid), "has_chemical", g);
                        } else {
                            logger.warn("Chemical ID not found: " + cid);
                        }
                    }
                }
            }
        }
         */
    }

    private void addVariantPhenotypeAnnotations(final Graph graph,
                                                final List<VariantPhenotypeAnnotation> variantPhenotypeAnnotations) {
        // TODO
    }

    private void addClinicalAnnotationMetadata(final Graph graph,
                                               final List<ClinicalAnnotationMetadata> clinicalAnnotationMetadata) {
        // TODO
        /*
        if (fieldName.equals("related_chemicals")) {
            for (String chemicalId : parseStringArray(value)) {
                String[] partList = chemicalId.split("\\(");
                if (partList.length > 2) {
                    String cid = partList[partList.length - 1].replace(")", "");
                    if (idNodeMap.containsKey(cid)) {
                        graph.addEdge(node, idNodeMap.get(cid), "has_chemical", g);
                    } else {
                        logger.warn("Chemical ID not found: " + cid);
                    }
                } else {
                    String cid = partList[1].replace(")", "");
                    if (idNodeMap.containsKey(cid)) {
                        graph.addEdge(node, idNodeMap.get(cid), "has_chemical", g);
                    } else {
                        logger.warn("Chemical ID not found: " + cid);
                    }
                }
            }
        } else if (fieldName.equals("gene")) {
            for (String geneId : parseStringArray(value)) {
                String gid = geneId.split("\\(")[1].replace(")", "");
                graph.addEdge(node, idNodeMap.get(gid), "has_gene", g);
            }
        } else if (fieldName.equals("genotype_phenotypes_id")) {
            for (String gpId : parseStringArray(value))
                graph.addEdge(node, genotype_phenotype_id.get(gpId), "has_genotype_phenotype", g);
        } else if (fieldName.equals("variant_annotations_id")) {
            for (String gpId : parseStringArray(value))
                graph.addEdge(node, variant_annotation_id.get(gpId), "has_genotype_phenotype", g);
        } else if (fieldName.equals("location")) {
            if (value != null && value.contains("rs")) {
                for (String variantName : parseStringArray(value)) {
                    if (variantNameNodeMap.containsKey(variantName)) {
                        graph.addEdge(node, variantNameNodeMap.get(variantName), "has_variation", g);
                    } else {
                        logger.warn("Variant name not found: " + variantName);
                    }
                }
            }
        }
         */
    }

    private void addPathway(Graph graph, String keyName, List<Pathway> pathways) throws ExporterException {
        String pathwayId = keyName.split("-")[0];
        String pathwayName = keyName.split("-")[1].replace("_", " ");
        Node node = createNode(graph, "Pathway");
        node.setProperty("_id", "PharmGKB:" + pathwayId);
        node.setProperty("name", pathwayName);
        idNodeMap.put(pathwayId, node);
        /* TODO
        for (Pathway entry : pathways) {
            Field[] fields = entry.getClass().getDeclaredFields();
            Node node = new Node(id, "Pathway");
            for (Field field : fields) {
                try {
                    String value = (String) field.get(entry);
                    node.setProperty(field.getName(), value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            g.addNode(node);
            id += 1;
        }
        */
    }
}
