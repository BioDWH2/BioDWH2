package de.unibi.agbi.biodwh2.pharmgkb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.pharmgkb.PharmGKBDataSource;
import de.unibi.agbi.biodwh2.pharmgkb.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PharmGKBGraphExporter extends GraphExporter<PharmGKBDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PharmGKBGraphExporter.class);
    private static final String QUOTED_ARRAY_DELIMITER = "\",\"";
    private static final String ESCAPED_DOUBLE_QUOTES = "\"";
    private static final String ID_PROPERTY = "id";
    private static final String BOOLEAN_VALUE_TRUE = "yes";
    private static final String GENE_LABEL = "Gene";
    private static final String HAPLOTYPE_LABEL = "Haplotype";
    private static final String HAPLOTYPE_SET_LABEL = "HaplotypeSet";
    private static final String VARIANT_LABEL = "Variant";
    private static final String CHEMICAL_LABEL = "Chemical";
    private static final String PHENOTYPE_LABEL = "Phenotype";

    private Map<String, Long> accessionNodeIdMap = new HashMap<>();
    private Map<Integer, Long> variantAnnotationIdNodeIdMap = new HashMap<>();

    public PharmGKBGraphExporter(final PharmGKBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys(ID_PROPERTY, "symbol", "name");
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(7);
        threadPool.submit(() -> executeParallelWithLatch(latch, () -> addGenes(graph, dataSource.genes)));
        threadPool.submit(() -> executeParallelWithLatch(latch, () -> addChemicals(graph, dataSource.chemicals)));
        threadPool.submit(() -> executeParallelWithLatch(latch, () -> addDrugs(graph, dataSource.drugs)));
        threadPool.submit(() -> executeParallelWithLatch(latch, () -> addPhenotypes(graph, dataSource.phenotyps)));
        threadPool.submit(() -> executeParallelWithLatch(latch, () -> addDrugLabels(graph, dataSource.drugLabels)));
        threadPool.submit(
                () -> executeParallelWithLatch(latch, () -> addStudyParameters(graph, dataSource.studyParameters)));
        threadPool.submit(() -> executeParallelWithLatch(latch, () -> addPathways(graph, dataSource.pathways)));
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new ExporterException("Failed to export entities concurrently", e);
        }
        threadPool.shutdown();
        addVariants(graph, dataSource.variants);
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

    private void executeParallelWithLatch(final CountDownLatch latch, final Runnable callback) {
        callback.run();
        latch.countDown();
    }

    private void addGenes(final Graph graph, final List<Gene> genes) {
        LOGGER.info("Add Genes...");
        for (Gene gene : genes) {
            Node node = createNodeFromModel(graph, gene);
            accessionNodeIdMap.put(gene.pharmgkbAccessionId, node.getId());
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
        if (arrayString == null)
            return new String[0];
        List<String> result = new ArrayList<>();
        for (String value : StringUtils.splitByWholeSeparator(arrayString, QUOTED_ARRAY_DELIMITER))
            result.add(StringUtils.strip(value, ESCAPED_DOUBLE_QUOTES));
        return result.toArray(new String[0]);
    }

    private boolean parseBoolean(String value) {
        return BOOLEAN_VALUE_TRUE.equalsIgnoreCase(value);
    }

    private String[] parseStringArray(String arrayString) {
        return arrayString != null ? arrayString.split("(?<=[^3]),(?=[^ ])") : new String[0];
    }

    private void addChemicals(final Graph graph, final List<Chemical> chemicals) {
        LOGGER.info("Add Chemicals...");
        for (Chemical chemical : chemicals) {
            Node node = createNodeFromModel(graph, chemical);
            accessionNodeIdMap.put(chemical.pharmgkbAccessionId, node.getId());
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
            if (chemical.dosingGuidelineSources != null)
                node.setProperty("dosing_guideline_sources", parseQuotedStringArray(chemical.dosingGuidelineSources));
            graph.update(node);
        }
    }

    private void addDrugs(final Graph graph, final List<Drug> drugs) {
        LOGGER.info("Add Drugs...");
        for (Drug drug : drugs) {
            Node node = createNodeFromModel(graph, drug);
            accessionNodeIdMap.put(drug.pharmgkbAccessionId, node.getId());
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
            if (drug.dosingGuidelineSources != null)
                node.setProperty("dosing_guideline_sources", parseQuotedStringArray(drug.dosingGuidelineSources));
            graph.update(node);
        }
    }

    private void addPhenotypes(final Graph graph, final List<Phenotype> phenotypes) {
        LOGGER.info("Add Phenotypes...");
        for (Phenotype phenotype : phenotypes) {
            Node node = createNodeFromModel(graph, phenotype);
            accessionNodeIdMap.put(phenotype.pharmgkbAccessionId, node.getId());
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
        LOGGER.info("Add Variants...");
        for (Variant variant : variants) {
            Node node = createNodeFromModel(graph, variant);
            accessionNodeIdMap.put(variant.variantId, node.getId());
            if (variant.synonyms != null)
                node.setProperty("synonyms", parseQuotedStringArray(variant.synonyms));
            graph.update(node);
            if (variant.geneIds != null)
                for (String geneId : (String[]) node.getProperty("gene_ids"))
                    graph.addEdge(accessionNodeIdMap.get(geneId), node, "HAS_VARIANT");
        }
    }

    private void addPathways(final Graph graph, final HashMap<String, List<Pathway>> pathways) {
        LOGGER.info("Add Pathways...");
        for (String keyName : pathways.keySet())
            addPathway(graph, keyName, pathways.get(keyName));
    }

    private void addPathway(final Graph graph, final String keyName, final List<Pathway> pathways) {
        final String[] parts = StringUtils.split(keyName, "-");
        final String pathwayId = parts[0];
        Node node = graph.addNode("Pathway", ID_PROPERTY, pathwayId, "name", parts[1].replace("_", " "));
        accessionNodeIdMap.put(pathwayId, node.getId());
        for (int i = 0; i < pathways.size(); i++) {
            Pathway pathway = pathways.get(i);
            Node stepNode = createNodeFromModel(graph, pathway);
            stepNode.setProperty("step", i);
            graph.update(stepNode);
            graph.addEdge(node, stepNode, "HAS_PATHWAY_STEP", "step", i);
        }
    }

    private void addDrugLabels(final Graph graph, final List<DrugLabel> drugLabels) {
        LOGGER.info("Add DrugLabels...");
        for (DrugLabel drugLabel : drugLabels)
            createNodeFromModel(graph, drugLabel);
    }

    private void addStudyParameters(final Graph graph, final List<StudyParameters> studyParameters) {
        LOGGER.info("Add StudyParameters...");
        for (StudyParameters studyParameter : studyParameters)
            createNodeFromModel(graph, studyParameter);
    }

    private void addOccurrences(final Graph graph, final List<Occurrence> occurrences) {
        LOGGER.info("Add Occurrences...");
        for (Occurrence occurrence : occurrences) {
            Node node = createNodeFromModel(graph, occurrence);
            switch (occurrence.objectType) {
                case HAPLOTYPE_LABEL:
                    long haplotypeNodeId = addHaplotypeIfNotExists(graph, occurrence.objectId, occurrence.objectName);
                    graph.addEdge(haplotypeNodeId, node, "HAS_OCCURRENCE");
                    break;
                case HAPLOTYPE_SET_LABEL:
                    long haplotypeSetNodeId = addHaplotypeSetIfNotExists(graph, occurrence.objectId,
                                                                         occurrence.objectName);
                    graph.addEdge(haplotypeSetNodeId, node, "HAS_OCCURRENCE");
                    break;
                case VARIANT_LABEL:
                    long variantNodeId = addVariantIfNotExists(graph, occurrence.objectId, occurrence.objectName);
                    graph.addEdge(variantNodeId, node, "HAS_OCCURRENCE");
                    break;
                default:
                    long objectNodeId = accessionNodeIdMap.get(occurrence.objectId);
                    graph.addEdge(objectNodeId, node, "HAS_OCCURRENCE");
                    break;
            }
        }
    }

    private long addVariantIfNotExists(final Graph graph, final String variantId, String variantName) {
        if (accessionNodeIdMap.containsKey(variantId))
            return accessionNodeIdMap.get(variantId);
        if (variantId.startsWith("rs")) {
            Node node = graph.findNode(VARIANT_LABEL, "name", variantId);
            if (node != null)
                return node.getId();
            variantName = variantName != null ? variantName : variantId;
        }
        Node node;
        if (variantName != null)
            node = graph.addNode(VARIANT_LABEL, ID_PROPERTY, variantId, "name", variantName);
        else
            node = graph.addNode(VARIANT_LABEL, ID_PROPERTY, variantId);
        accessionNodeIdMap.put(variantId, node.getId());
        return node.getId();
    }

    private long addHaplotypeIfNotExists(final Graph graph, final String haplotypeId, final String haplotypeName) {
        if (accessionNodeIdMap.containsKey(haplotypeId))
            return accessionNodeIdMap.get(haplotypeId);
        Node node;
        if (haplotypeName != null)
            node = graph.addNode(HAPLOTYPE_LABEL, ID_PROPERTY, haplotypeId, "name", haplotypeName);
        else
            node = graph.addNode(HAPLOTYPE_LABEL, ID_PROPERTY, haplotypeId);
        accessionNodeIdMap.put(haplotypeId, node.getId());
        return node.getId();
    }

    private long addHaplotypeSetIfNotExists(final Graph graph, final String haplotypeId, final String haplotypeName) {
        if (accessionNodeIdMap.containsKey(haplotypeId))
            return accessionNodeIdMap.get(haplotypeId);
        Node node;
        if (haplotypeName != null)
            node = graph.addNode(HAPLOTYPE_SET_LABEL, ID_PROPERTY, haplotypeId, "name", haplotypeName);
        else
            node = graph.addNode(HAPLOTYPE_SET_LABEL, ID_PROPERTY, haplotypeId);
        accessionNodeIdMap.put(haplotypeId, node.getId());
        return node.getId();
    }

    private void addDrugLabelsByGene(final Graph graph, final List<DrugLabelsByGene> drugLabelsByGenes) {
        LOGGER.info("Add DrugLabelsByGene...");
        for (DrugLabelsByGene drugLabelsByGene : drugLabelsByGenes) {
            for (String labelId : drugLabelsByGene.labelIds.split(";")) {
                Node labelNode = graph.findNode("DrugLabel", ID_PROPERTY, labelId);
                Node geneNode = graph.findNode(GENE_LABEL, ID_PROPERTY, drugLabelsByGene.geneId);
                graph.addEdge(labelNode, geneNode, "ASSOCIATED_WITH");
            }
        }
    }

    private void addClinicalAnnotations(final Graph graph, final List<ClinicalAnnotation> annotations) {
        LOGGER.info("Add ClinicalAnnotations...");
        for (ClinicalAnnotation annotation : annotations)
            createNodeFromModel(graph, annotation);
    }

    private void addAutomatedAnnotations(final Graph graph, final List<AutomatedAnnotation> annotations) {
        LOGGER.info("Add AutomatedAnnotations...");
        for (AutomatedAnnotation annotation : annotations) {
            Node node = createNodeFromModel(graph, annotation);
            if (annotation.geneIds != null)
                for (String geneId : StringUtils.split(annotation.geneIds, ","))
                    graph.addEdge(node, accessionNodeIdMap.get(geneId), "ASSOCIATED_WITH");
            if (annotation.chemicalId != null) {
                long chemicalNodeId = addChemicalIfNotExists(graph, annotation.chemicalId, annotation.chemicalName);
                graph.addEdge(node, chemicalNodeId, "ASSOCIATED_WITH");
            }
            if (annotation.variationId != null) {
                long targetNodeId;
                if (annotation.variationType.equals(HAPLOTYPE_LABEL))
                    targetNodeId = addHaplotypeIfNotExists(graph, annotation.variationId, annotation.variationName);
                else
                    targetNodeId = addVariantIfNotExists(graph, annotation.variationId, annotation.variationName);
                graph.addEdge(node, targetNodeId, "ASSOCIATED_WITH");
            }
        }
    }

    private long addChemicalIfNotExists(final Graph graph, final String chemicalId, final String chemicalName) {
        if (accessionNodeIdMap.containsKey(chemicalId))
            return accessionNodeIdMap.get(chemicalId);
        Node node;
        if (chemicalName != null)
            node = graph.addNode(CHEMICAL_LABEL, ID_PROPERTY, chemicalId, "name", chemicalName);
        else
            node = graph.addNode(CHEMICAL_LABEL, ID_PROPERTY, chemicalId);
        accessionNodeIdMap.put(chemicalId, node.getId());
        return node.getId();
    }

    private void addClinicalVariants(final Graph graph, final List<ClinicalVariant> clinicalVariants) {
        LOGGER.info("Add ClinicalVariants...");
        for (ClinicalVariant clinicalVariant : clinicalVariants) {
            Node node = createNodeFromModel(graph, clinicalVariant);
            if (clinicalVariant.gene != null)
                for (String geneId : StringUtils.split(clinicalVariant.gene, ",")) {
                    Node geneNode = graph.findNode(GENE_LABEL, "symbol", geneId);
                    graph.addEdge(node, geneNode, "ASSOCIATED_WITH");
                }
            for (String chemical : parseStringArray(clinicalVariant.chemicals)) {
                long chemicalNodeId = addChemicalIfNotExists(graph, chemical, null);
                graph.addEdge(node, chemicalNodeId, "ASSOCIATED_WITH");
            }
            for (String phenotype : parseStringArray(clinicalVariant.phenotypes)) {
                Node phenotypeNode = graph.findNode(PHENOTYPE_LABEL, "name", phenotype);
                graph.addEdge(node, phenotypeNode, "ASSOCIATED_WITH");
            }
            for (String variant : parseStringArray(clinicalVariant.variant)) {
                long targetNodeId = variant.contains("rs") ? addVariantIfNotExists(graph, variant, null) :
                                    addHaplotypeIfNotExists(graph, variant, null);
                graph.addEdge(node, targetNodeId, "ASSOCIATED_WITH");
            }
        }
    }

    private void addVariantDrugAnnotations(final Graph graph, final List<VariantDrugAnnotation> annotations) {
        LOGGER.info("Add VariantDrugAnnotations...");
        for (VariantDrugAnnotation annotation : annotations) {
            Node node = createNodeFromModel(graph, annotation);
            variantAnnotationIdNodeIdMap.put(annotation.annotationId, node.getId());
            if (annotation.phenotypeCategory != null)
                node.setProperty("phenotype_categories", parseQuotedStringArray(annotation.phenotypeCategory));
            graph.update(node);
            if (annotation.gene != null) {
                for (String gene : parseQuotedStringArray(annotation.gene)) {
                    long geneNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(gene));
                    graph.addEdge(node, geneNodeId, "ASSOCIATED_WITH");
                }
            }
            if (annotation.chemical != null) {
                for (String chemical : parseQuotedStringArray(annotation.chemical)) {
                    long chemicalNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(chemical));
                    graph.addEdge(node, chemicalNodeId, "ASSOCIATED_WITH");
                }
            }
            if (annotation.studyParameters != null) {
                for (String studyParametersId : parseQuotedStringArray(annotation.studyParameters)) {
                    Node studyParametersNode = graph.findNode("StudyParameters", ID_PROPERTY, studyParametersId);
                    graph.addEdge(node, studyParametersNode, "WITH_PARAMETERS");
                }
            }
            for (String variant : parseStringArray(annotation.variant)) {
                variant = variant.trim();
                long targetNodeId = variant.contains("rs") ? addVariantIfNotExists(graph, variant, null) :
                                    addHaplotypeIfNotExists(graph, variant, null);
                graph.addEdge(node, targetNodeId, "ASSOCIATED_WITH");
            }
        }
    }

    private static String getIdFromNameIdPair(String pair) {
        String[] parts = StringUtils.split(pair, "(");
        return parts[parts.length - 1].replace(")", "").trim();
    }

    private void addVariantFunctionalAnalysisAnnotations(final Graph graph,
                                                         final List<VariantFunctionalAnalysisAnnotation> annotations) {
        LOGGER.info("Add VariantFunctionalAnalysisAnnotations...");
        for (VariantFunctionalAnalysisAnnotation annotation : annotations) {
            Node node = createNodeFromModel(graph, annotation);
            variantAnnotationIdNodeIdMap.put(annotation.annotationId, node.getId());
            if (annotation.phenotypeCategory != null)
                node.setProperty("phenotype_categories", parseQuotedStringArray(annotation.phenotypeCategory));
            graph.update(node);
            if (annotation.gene != null) {
                for (String gene : parseQuotedStringArray(annotation.gene)) {
                    long geneNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(gene));
                    graph.addEdge(node, geneNodeId, "ASSOCIATED_WITH");
                }
            }
            if (annotation.chemical != null) {
                for (String chemical : parseQuotedStringArray(annotation.chemical)) {
                    long chemicalNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(chemical));
                    graph.addEdge(node, chemicalNodeId, "ASSOCIATED_WITH");
                }
            }
            if (annotation.studyParameters != null) {
                for (String studyParametersId : parseQuotedStringArray(annotation.studyParameters)) {
                    Node studyParametersNode = graph.findNode("StudyParameters", ID_PROPERTY, studyParametersId);
                    graph.addEdge(node, studyParametersNode, "WITH_PARAMETERS");
                }
            }
            for (String variant : parseStringArray(annotation.variant)) {
                variant = variant.trim();
                long targetNodeId = variant.contains("rs") ? addVariantIfNotExists(graph, variant, null) :
                                    addHaplotypeIfNotExists(graph, variant, null);
                graph.addEdge(node, targetNodeId, "ASSOCIATED_WITH");
            }
        }
    }

    private void addVariantPhenotypeAnnotations(final Graph graph, final List<VariantPhenotypeAnnotation> annotations) {
        LOGGER.info("Add VariantPhenotypeAnnotations...");
        for (VariantPhenotypeAnnotation annotation : annotations) {
            Node node = createNodeFromModel(graph, annotation);
            variantAnnotationIdNodeIdMap.put(annotation.annotationId, node.getId());
            if (annotation.phenotypeCategory != null)
                node.setProperty("phenotype_categories", parseQuotedStringArray(annotation.phenotypeCategory));
            graph.update(node);
            if (annotation.gene != null) {
                for (String gene : parseQuotedStringArray(annotation.gene)) {
                    long geneNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(gene));
                    graph.addEdge(node, geneNodeId, "ASSOCIATED_WITH");
                }
            }
            if (annotation.chemical != null) {
                for (String chemical : parseQuotedStringArray(annotation.chemical)) {
                    long chemicalNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(chemical));
                    graph.addEdge(node, chemicalNodeId, "ASSOCIATED_WITH");
                }
            }
            if (annotation.studyParameters != null) {
                for (String studyParametersId : parseQuotedStringArray(annotation.studyParameters)) {
                    Node studyParametersNode = graph.findNode("StudyParameters", ID_PROPERTY, studyParametersId);
                    graph.addEdge(node, studyParametersNode, "WITH_PARAMETERS");
                }
            }
            for (String variant : parseStringArray(annotation.variant)) {
                variant = variant.trim();
                long targetNodeId = variant.contains("rs") ? addVariantIfNotExists(graph, variant, null) :
                                    addHaplotypeIfNotExists(graph, variant, null);
                graph.addEdge(node, targetNodeId, "ASSOCIATED_WITH");
            }
        }
    }

    private void addClinicalAnnotationMetadata(final Graph graph,
                                               final List<ClinicalAnnotationMetadata> clinicalAnnotationMetadata) {
        LOGGER.info("Add ClinicalAnnotationMetadata...");
        for (ClinicalAnnotationMetadata metadata : clinicalAnnotationMetadata) {
            Node node = createNodeFromModel(graph, metadata);
            if (metadata.gene != null) {
                for (String gene : StringUtils.split(metadata.gene, ";")) {
                    long geneNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(gene));
                    graph.addEdge(node, geneNodeId, "ASSOCIATED_WITH");
                }
            }
            if (metadata.relatedChemicals != null) {
                for (String chemical : StringUtils.split(metadata.relatedChemicals, ";")) {
                    long chemicalNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(chemical));
                    graph.addEdge(node, chemicalNodeId, "ASSOCIATED_WITH");
                }
            }
            if (metadata.relatedDiseases != null) {
                for (String disease : StringUtils.split(metadata.relatedDiseases, ";")) {
                    long phenotypeNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(disease));
                    graph.addEdge(node, phenotypeNodeId, "ASSOCIATED_WITH");
                }
            }
            if (metadata.genotypePhenotypesId != null) {
                for (String genotypePhenotypeId : StringUtils.split(metadata.genotypePhenotypesId, ";")) {
                    Node annotationNode = graph.findNode("ClinicalAnnotation", ID_PROPERTY, genotypePhenotypeId);
                    graph.addEdge(node, annotationNode, "ASSOCIATED_WITH");
                }
            }
            if (metadata.variantAnnotationsId != null) {
                String[] ids = StringUtils.split(metadata.variantAnnotationsId, ";");
                String[] texts = StringUtils.split(metadata.variantAnnotation, ";");
                for (int i = 0; i < ids.length; i++) {
                    long annotationNodeId = variantAnnotationIdNodeIdMap.get(Integer.parseInt(ids[i]));
                    graph.addEdge(node, annotationNodeId, "ASSOCIATED_WITH", "annotation", texts[i]);
                }
            }
        }
    }
}
