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

public class PharmGKBGraphExporter extends GraphExporter<PharmGKBDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PharmGKBGraphExporter.class);
    public static final String QUOTED_ARRAY_DELIMITER = "\",\"";
    private static final String ESCAPED_DOUBLE_QUOTES = "\"";
    private static final String ID_PROPERTY = "id";
    private static final String GENE_LABEL = "Gene";
    private static final String HAPLOTYPE_LABEL = "Haplotype";
    private static final String HAPLOTYPE_SET_LABEL = "HaplotypeSet";
    private static final String VARIANT_LABEL = "Variant";
    private static final String CHEMICAL_LABEL = "Chemical";
    private static final String PHENOTYPE_LABEL = "Phenotype";
    private static final String ASSOCIATED_WITH_LABEL = "ASSOCIATED_WITH";
    private static final String HAS_OCCURRENCE_LABEL = "HAS_OCCURRENCE";

    private final Map<String, Long> accessionNodeIdMap = new HashMap<>();
    private final Map<Integer, Long> variantAnnotationIdNodeIdMap = new HashMap<>();

    public PharmGKBGraphExporter(final PharmGKBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys(ID_PROPERTY, "symbol", "name");
        addGenes(graph, dataSource.genes);
        addChemicals(graph, dataSource.chemicals);
        addDrugs(graph, dataSource.drugs);
        addPhenotypes(graph, dataSource.phenotyps);
        addDrugLabels(graph, dataSource.drugLabels);
        addStudyParameters(graph, dataSource.studyParameters);
        addPathways(graph, dataSource.pathways);
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

    private void addGenes(final Graph graph, final List<Gene> genes) {
        LOGGER.info("Add Genes...");
        for (final Gene gene : genes) {
            final Node node = createNodeFromModel(graph, gene);
            accessionNodeIdMap.put(gene.pharmgkbAccessionId, node.getIdValue());
        }
    }

    private void addChemicals(final Graph graph, final List<Chemical> chemicals) {
        LOGGER.info("Add Chemicals...");
        for (final Chemical chemical : chemicals) {
            final Node node = createNodeFromModel(graph, chemical);
            accessionNodeIdMap.put(chemical.pharmgkbAccessionId, node.getIdValue());
        }
    }

    private void addDrugs(final Graph graph, final List<Drug> drugs) {
        LOGGER.info("Add Drugs...");
        for (final Drug drug : drugs) {
            final Node node = createNodeFromModel(graph, drug);
            accessionNodeIdMap.put(drug.pharmgkbAccessionId, node.getIdValue());
        }
    }

    private void addPhenotypes(final Graph graph, final List<Phenotype> phenotypes) {
        LOGGER.info("Add Phenotypes...");
        for (final Phenotype phenotype : phenotypes) {
            final Node node = createNodeFromModel(graph, phenotype);
            accessionNodeIdMap.put(phenotype.pharmgkbAccessionId, node.getIdValue());
        }
    }

    private void addVariants(final Graph graph, final List<Variant> variants) {
        LOGGER.info("Add Variants...");
        for (final Variant variant : variants) {
            final Node node = createNodeFromModel(graph, variant);
            accessionNodeIdMap.put(variant.variantId, node.getIdValue());
            if (variant.geneIds != null)
                for (String geneId : node.<String[]>getProperty("gene_ids"))
                    graph.addEdge(accessionNodeIdMap.get(geneId), node, "HAS_VARIANT");
        }
    }

    private void addPathways(final Graph graph, final HashMap<String, List<Pathway>> pathways) {
        LOGGER.info("Add Pathways...");
        for (final String keyName : pathways.keySet())
            addPathway(graph, keyName, pathways.get(keyName));
    }

    private void addPathway(final Graph graph, final String keyName, final List<Pathway> pathways) {
        final String[] parts = StringUtils.split(keyName, "-");
        final String pathwayId = parts[0];
        final Node node = graph.addNode("Pathway", ID_PROPERTY, pathwayId, "name", parts[1].replace("_", " "));
        accessionNodeIdMap.put(pathwayId, node.getIdValue());
        for (int i = 0; i < pathways.size(); i++) {
            final Pathway pathway = pathways.get(i);
            final Node stepNode = createNodeFromModel(graph, pathway);
            stepNode.setProperty("step", i);
            graph.update(stepNode);
            graph.addEdge(node, stepNode, "HAS_PATHWAY_STEP", "step", i);
        }
    }

    private void addDrugLabels(final Graph graph, final List<DrugLabel> drugLabels) {
        LOGGER.info("Add DrugLabels...");
        for (final DrugLabel drugLabel : drugLabels)
            createNodeFromModel(graph, drugLabel);
    }

    private void addStudyParameters(final Graph graph, final List<StudyParameters> studyParameters) {
        LOGGER.info("Add StudyParameters...");
        for (final StudyParameters studyParameter : studyParameters)
            createNodeFromModel(graph, studyParameter);
    }

    private void addOccurrences(final Graph graph, final List<Occurrence> occurrences) {
        LOGGER.info("Add Occurrences...");
        for (final Occurrence occurrence : occurrences) {
            final Node node = createNodeFromModel(graph, occurrence);
            switch (occurrence.objectType) {
                case HAPLOTYPE_LABEL:
                    long haplotypeNodeId = addHaplotypeIfNotExists(graph, occurrence.objectId, occurrence.objectName);
                    graph.addEdge(haplotypeNodeId, node, HAS_OCCURRENCE_LABEL);
                    break;
                case HAPLOTYPE_SET_LABEL:
                    long haplotypeSetNodeId = addHaplotypeSetIfNotExists(graph, occurrence.objectId,
                                                                         occurrence.objectName);
                    graph.addEdge(haplotypeSetNodeId, node, HAS_OCCURRENCE_LABEL);
                    break;
                case VARIANT_LABEL:
                    long variantNodeId = addVariantIfNotExists(graph, occurrence.objectId, occurrence.objectName);
                    graph.addEdge(variantNodeId, node, HAS_OCCURRENCE_LABEL);
                    break;
                default:
                    long objectNodeId = accessionNodeIdMap.get(occurrence.objectId);
                    graph.addEdge(objectNodeId, node, HAS_OCCURRENCE_LABEL);
                    break;
            }
        }
    }

    private long addVariantIfNotExists(final Graph graph, final String variantId, String variantName) {
        if (accessionNodeIdMap.containsKey(variantId))
            return accessionNodeIdMap.get(variantId);
        if (variantId.startsWith("rs")) {
            final Node node = graph.findNode(VARIANT_LABEL, "name", variantId);
            if (node != null)
                return node.getIdValue();
            variantName = variantName != null ? variantName : variantId;
        }
        Node node;
        if (variantName != null)
            node = graph.addNode(VARIANT_LABEL, ID_PROPERTY, variantId, "name", variantName);
        else
            node = graph.addNode(VARIANT_LABEL, ID_PROPERTY, variantId);
        accessionNodeIdMap.put(variantId, node.getIdValue());
        return node.getIdValue();
    }

    private long addHaplotypeIfNotExists(final Graph graph, final String haplotypeId, final String haplotypeName) {
        if (accessionNodeIdMap.containsKey(haplotypeId))
            return accessionNodeIdMap.get(haplotypeId);
        Node node;
        if (haplotypeName != null)
            node = graph.addNode(HAPLOTYPE_LABEL, ID_PROPERTY, haplotypeId, "name", haplotypeName);
        else
            node = graph.addNode(HAPLOTYPE_LABEL, ID_PROPERTY, haplotypeId);
        accessionNodeIdMap.put(haplotypeId, node.getIdValue());
        return node.getIdValue();
    }

    private long addHaplotypeSetIfNotExists(final Graph graph, final String haplotypeId, final String haplotypeName) {
        if (accessionNodeIdMap.containsKey(haplotypeId))
            return accessionNodeIdMap.get(haplotypeId);
        Node node;
        if (haplotypeName != null)
            node = graph.addNode(HAPLOTYPE_SET_LABEL, ID_PROPERTY, haplotypeId, "name", haplotypeName);
        else
            node = graph.addNode(HAPLOTYPE_SET_LABEL, ID_PROPERTY, haplotypeId);
        accessionNodeIdMap.put(haplotypeId, node.getIdValue());
        return node.getIdValue();
    }

    private void addDrugLabelsByGene(final Graph graph, final List<DrugLabelsByGene> drugLabelsByGenes) {
        LOGGER.info("Add DrugLabelsByGene...");
        for (final DrugLabelsByGene drugLabelsByGene : drugLabelsByGenes) {
            for (final String labelId : drugLabelsByGene.labelIds.split(";")) {
                final Node labelNode = graph.findNode("DrugLabel", ID_PROPERTY, labelId);
                final Node geneNode = graph.findNode(GENE_LABEL, ID_PROPERTY, drugLabelsByGene.geneId);
                graph.addEdge(labelNode, geneNode, ASSOCIATED_WITH_LABEL);
            }
        }
    }

    private void addClinicalAnnotations(final Graph graph, final List<ClinicalAnnotation> annotations) {
        LOGGER.info("Add ClinicalAnnotations...");
        for (final ClinicalAnnotation annotation : annotations)
            createNodeFromModel(graph, annotation);
    }

    private void addAutomatedAnnotations(final Graph graph, final List<AutomatedAnnotation> annotations) {
        LOGGER.info("Add AutomatedAnnotations...");
        for (final AutomatedAnnotation annotation : annotations) {
            final Node node = createNodeFromModel(graph, annotation);
            if (annotation.geneIds != null)
                for (String geneId : StringUtils.split(annotation.geneIds, ","))
                    graph.addEdge(node, accessionNodeIdMap.get(geneId), ASSOCIATED_WITH_LABEL);
            if (annotation.chemicalId != null) {
                long chemicalNodeId = addChemicalIfNotExists(graph, annotation.chemicalId, annotation.chemicalName);
                graph.addEdge(node, chemicalNodeId, ASSOCIATED_WITH_LABEL);
            }
            if (annotation.variationId != null) {
                long targetNodeId;
                if (annotation.variationType.equals(HAPLOTYPE_LABEL))
                    targetNodeId = addHaplotypeIfNotExists(graph, annotation.variationId, annotation.variationName);
                else
                    targetNodeId = addVariantIfNotExists(graph, annotation.variationId, annotation.variationName);
                graph.addEdge(node, targetNodeId, ASSOCIATED_WITH_LABEL);
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
        accessionNodeIdMap.put(chemicalId, node.getIdValue());
        return node.getIdValue();
    }

    private void addClinicalVariants(final Graph graph, final List<ClinicalVariant> clinicalVariants) {
        LOGGER.info("Add ClinicalVariants...");
        for (final ClinicalVariant clinicalVariant : clinicalVariants) {
            final Node node = createNodeFromModel(graph, clinicalVariant);
            if (clinicalVariant.gene != null)
                for (final String geneId : StringUtils.split(clinicalVariant.gene, ",")) {
                    final Node geneNode = graph.findNode(GENE_LABEL, "symbol", geneId);
                    graph.addEdge(node, geneNode, ASSOCIATED_WITH_LABEL);
                }
            for (final String chemical : parseStringArray(clinicalVariant.chemicals)) {
                final long chemicalNodeId = addChemicalIfNotExists(graph, chemical, null);
                graph.addEdge(node, chemicalNodeId, ASSOCIATED_WITH_LABEL);
            }
            for (final String phenotype : parseStringArray(clinicalVariant.phenotypes)) {
                final Node phenotypeNode = graph.findNode(PHENOTYPE_LABEL, "name", phenotype);
                graph.addEdge(node, phenotypeNode, ASSOCIATED_WITH_LABEL);
            }
            for (final String variant : parseStringArray(clinicalVariant.variant)) {
                final long targetNodeId = variant.contains("rs") ? addVariantIfNotExists(graph, variant, null) :
                                          addHaplotypeIfNotExists(graph, variant, null);
                graph.addEdge(node, targetNodeId, ASSOCIATED_WITH_LABEL);
            }
        }
    }

    private String[] parseStringArray(final String arrayString) {
        return arrayString != null ? arrayString.split("(?<=[^3]),(?=[^ ])") : new String[0];
    }

    private void addVariantDrugAnnotations(final Graph graph, final List<VariantDrugAnnotation> annotations) {
        LOGGER.info("Add VariantDrugAnnotations...");
        for (final VariantDrugAnnotation annotation : annotations) {
            final Node node = createNodeFromModel(graph, annotation);
            variantAnnotationIdNodeIdMap.put(annotation.annotationId, node.getIdValue());
            if (annotation.gene != null) {
                for (final String gene : parseQuotedStringArray(annotation.gene)) {
                    final long geneNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(gene));
                    graph.addEdge(node, geneNodeId, ASSOCIATED_WITH_LABEL);
                }
            }
            if (annotation.chemical != null) {
                for (final String chemical : parseQuotedStringArray(annotation.chemical)) {
                    final long chemicalNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(chemical));
                    graph.addEdge(node, chemicalNodeId, ASSOCIATED_WITH_LABEL);
                }
            }
            if (annotation.studyParameters != null) {
                for (final String studyParametersId : parseQuotedStringArray(annotation.studyParameters)) {
                    final Node studyParametersNode = graph.findNode("StudyParameters", ID_PROPERTY, studyParametersId);
                    graph.addEdge(node, studyParametersNode, "WITH_PARAMETERS");
                }
            }
            for (String variant : parseStringArray(annotation.variant)) {
                variant = variant.trim();
                final long targetNodeId = variant.contains("rs") ? addVariantIfNotExists(graph, variant, null) :
                                          addHaplotypeIfNotExists(graph, variant, null);
                graph.addEdge(node, targetNodeId, ASSOCIATED_WITH_LABEL);
            }
        }
    }

    private String[] parseQuotedStringArray(final String arrayString) {
        if (arrayString == null)
            return new String[0];
        final List<String> result = new ArrayList<>();
        for (final String value : StringUtils.splitByWholeSeparator(arrayString, QUOTED_ARRAY_DELIMITER))
            result.add(StringUtils.strip(value, ESCAPED_DOUBLE_QUOTES));
        return result.toArray(new String[0]);
    }

    private static String getIdFromNameIdPair(final String pair) {
        final String[] parts = StringUtils.split(pair, "(");
        return parts[parts.length - 1].replace(")", "").trim();
    }

    private void addVariantFunctionalAnalysisAnnotations(final Graph graph,
                                                         final List<VariantFunctionalAnalysisAnnotation> annotations) {
        LOGGER.info("Add VariantFunctionalAnalysisAnnotations...");
        for (final VariantFunctionalAnalysisAnnotation annotation : annotations) {
            final Node node = createNodeFromModel(graph, annotation);
            variantAnnotationIdNodeIdMap.put(annotation.annotationId, node.getIdValue());
            if (annotation.gene != null) {
                for (final String gene : parseQuotedStringArray(annotation.gene)) {
                    final long geneNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(gene));
                    graph.addEdge(node, geneNodeId, ASSOCIATED_WITH_LABEL);
                }
            }
            if (annotation.chemical != null) {
                for (final String chemical : parseQuotedStringArray(annotation.chemical)) {
                    final long chemicalNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(chemical));
                    graph.addEdge(node, chemicalNodeId, ASSOCIATED_WITH_LABEL);
                }
            }
            if (annotation.studyParameters != null) {
                for (final String studyParametersId : parseQuotedStringArray(annotation.studyParameters)) {
                    final Node studyParametersNode = graph.findNode("StudyParameters", ID_PROPERTY, studyParametersId);
                    graph.addEdge(node, studyParametersNode, "WITH_PARAMETERS");
                }
            }
            for (String variant : parseStringArray(annotation.variant)) {
                variant = variant.trim();
                final long targetNodeId = variant.contains("rs") ? addVariantIfNotExists(graph, variant, null) :
                                          addHaplotypeIfNotExists(graph, variant, null);
                graph.addEdge(node, targetNodeId, ASSOCIATED_WITH_LABEL);
            }
        }
    }

    private void addVariantPhenotypeAnnotations(final Graph graph, final List<VariantPhenotypeAnnotation> annotations) {
        LOGGER.info("Add VariantPhenotypeAnnotations...");
        for (final VariantPhenotypeAnnotation annotation : annotations) {
            final Node node = createNodeFromModel(graph, annotation);
            variantAnnotationIdNodeIdMap.put(annotation.annotationId, node.getIdValue());
            if (annotation.gene != null) {
                for (final String gene : parseQuotedStringArray(annotation.gene)) {
                    final long geneNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(gene));
                    graph.addEdge(node, geneNodeId, ASSOCIATED_WITH_LABEL);
                }
            }
            if (annotation.chemical != null) {
                for (final String chemical : parseQuotedStringArray(annotation.chemical)) {
                    final long chemicalNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(chemical));
                    graph.addEdge(node, chemicalNodeId, ASSOCIATED_WITH_LABEL);
                }
            }
            if (annotation.studyParameters != null) {
                for (final String studyParametersId : parseQuotedStringArray(annotation.studyParameters)) {
                    final Node studyParametersNode = graph.findNode("StudyParameters", ID_PROPERTY, studyParametersId);
                    graph.addEdge(node, studyParametersNode, "WITH_PARAMETERS");
                }
            }
            for (String variant : parseStringArray(annotation.variant)) {
                variant = variant.trim();
                final long targetNodeId = variant.contains("rs") ? addVariantIfNotExists(graph, variant, null) :
                                          addHaplotypeIfNotExists(graph, variant, null);
                graph.addEdge(node, targetNodeId, ASSOCIATED_WITH_LABEL);
            }
        }
    }

    private void addClinicalAnnotationMetadata(final Graph graph,
                                               final List<ClinicalAnnotationMetadata> clinicalAnnotationMetadata) {
        LOGGER.info("Add ClinicalAnnotationMetadata...");
        for (final ClinicalAnnotationMetadata metadata : clinicalAnnotationMetadata) {
            final Node node = createNodeFromModel(graph, metadata);
            if (metadata.gene != null) {
                for (final String gene : StringUtils.split(metadata.gene, ";")) {
                    final Long geneNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(gene));
                    if (geneNodeId != null)
                        graph.addEdge(node, geneNodeId, ASSOCIATED_WITH_LABEL);
                    else if (LOGGER.isWarnEnabled())
                        LOGGER.warn("Could not find gene '" + gene + "' from clinical annotation metadata");
                }
            }
            if (metadata.relatedChemicals != null) {
                for (final String chemical : StringUtils.split(metadata.relatedChemicals, ";")) {
                    final Long chemicalNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(chemical));
                    if (chemicalNodeId != null)
                        graph.addEdge(node, chemicalNodeId, ASSOCIATED_WITH_LABEL);
                    else if (LOGGER.isWarnEnabled())
                        LOGGER.warn("Could not find chemical '" + chemical + "' from clinical annotation metadata");
                }
            }
            if (metadata.relatedDiseases != null) {
                for (final String disease : StringUtils.split(metadata.relatedDiseases, ";")) {
                    final Long phenotypeNodeId = accessionNodeIdMap.get(getIdFromNameIdPair(disease));
                    if (phenotypeNodeId != null)
                        graph.addEdge(node, phenotypeNodeId, ASSOCIATED_WITH_LABEL);
                    else if (LOGGER.isWarnEnabled())
                        LOGGER.warn("Could not find disease '" + disease + "' from clinical annotation metadata");
                }
            }
            if (metadata.genotypePhenotypesId != null) {
                for (final String genotypePhenotypeId : StringUtils.split(metadata.genotypePhenotypesId, ";")) {
                    final Node annotationNode = graph.findNode("ClinicalAnnotation", ID_PROPERTY, genotypePhenotypeId);
                    if (annotationNode != null)
                        graph.addEdge(node, annotationNode, ASSOCIATED_WITH_LABEL);
                    else if (LOGGER.isWarnEnabled())
                        LOGGER.warn("Could not find ClinicalAnnotation '" + genotypePhenotypeId +
                                    "' from clinical annotation metadata");
                }
            }
            if (metadata.variantAnnotationsId != null) {
                final String[] ids = StringUtils.split(metadata.variantAnnotationsId, ";");
                String[] texts = StringUtils.split(metadata.variantAnnotation, ";");
                if (ids.length != texts.length) {
                    // hack fix as long \t chars are included in the variant annotation column
                    texts = StringUtils.split(metadata.variantAnnotation + "\t" + metadata.relatedChemicals, ";");
                }
                for (int i = 0; i < ids.length; i++) {
                    final Long annotationNodeId = variantAnnotationIdNodeIdMap.get(Integer.parseInt(ids[i]));
                    if (annotationNodeId != null)
                        graph.addEdge(node, annotationNodeId, ASSOCIATED_WITH_LABEL, "annotation", texts[i]);
                    else if (LOGGER.isWarnEnabled())
                        LOGGER.warn(
                                "Could not find VariantAnnotation '" + ids[i] + "' from clinical annotation metadata");
                }
            }
        }
    }
}