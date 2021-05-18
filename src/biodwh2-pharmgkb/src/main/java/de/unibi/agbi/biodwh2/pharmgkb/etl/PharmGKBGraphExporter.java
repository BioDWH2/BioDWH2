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
import java.util.stream.Collectors;

public class PharmGKBGraphExporter extends GraphExporter<PharmGKBDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PharmGKBGraphExporter.class);
    public static final String QUOTED_ARRAY_DELIMITER = "\",\"";
    private static final String ESCAPED_DOUBLE_QUOTES = "\"";
    private static final String ID_PROPERTY = "id";
    private static final String NAME_PROPERTY = "name";
    static final String GENE_LABEL = "Gene";
    static final String HAPLOTYPE_LABEL = "Haplotype";
    static final String HAPLOTYPE_SET_LABEL = "HaplotypeSet";
    static final String VARIANT_LABEL = "Variant";
    static final String PATHWAY_LABEL = "Pathway";
    static final String CHEMICAL_LABEL = "Chemical";
    static final String PHENOTYPE_LABEL = "Phenotype";
    static final String LITERATURE_LABEL = "Literature";
    private static final String ASSOCIATED_WITH_LABEL = "ASSOCIATED_WITH";
    private static final String HAS_OCCURRENCE_LABEL = "HAS_OCCURRENCE";
    private static final String[] MERGE_HAPLOTYPE_NAME_PREFIXES = new String[]{
            "Taiwan-Hakka", "Gifu-like", "Agrigento-like", "Dallas", "Panama' Sassari", "Cagliari", "Birmingham"
    };

    private final Map<String, Long> accessionNodeIdMap = new HashMap<>();
    private final Map<String, Long> literatureIdNodeIdMap = new HashMap<>();
    private final Map<Integer, Long> variantAnnotationIdNodeIdMap = new HashMap<>();

    public PharmGKBGraphExporter(final PharmGKBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys(ID_PROPERTY, "symbol", NAME_PROPERTY);
        addGenes(graph, dataSource.genes);
        addChemicals(graph, dataSource.chemicals);
        addPhenotypes(graph, dataSource.phenotyps);
        final Map<String, List<Occurrence>> pathwayOccurrences = collectPathwayOccurrences(dataSource.occurrences);
        addPathways(graph, dataSource.pathways, pathwayOccurrences);
        addVariants(graph, dataSource.variants);
        addOccurrences(graph, dataSource.occurrences);
        addClinicalAnnotations(graph, dataSource.clinicalAnnotations);
        addClinicalAnnotationAlleles(graph, dataSource.clinicalAnnotationAlleles);
        addAutomatedAnnotations(graph, dataSource.automatedAnnotations);
        addClinicalVariants(graph, dataSource.clinicalVariants);
        addVariantAnnotations(graph, dataSource.variantDrugAnnotations);
        addVariantAnnotations(graph, dataSource.variantFunctionalAnalysisAnnotations);
        addVariantAnnotations(graph, dataSource.variantPhenotypeAnnotations);
        addStudyParameters(graph, dataSource.studyParameters);
        addDrugLabels(graph, dataSource.drugLabels);
        addDrugLabelsByGene(graph, dataSource.drugLabelsByGenes);
        return true;
    }

    private void addGenes(final Graph graph, final List<Gene> genes) {
        LOGGER.info("Add Genes...");
        for (final Gene gene : genes) {
            final Node node = graph.addNodeFromModel(gene);
            accessionNodeIdMap.put(gene.pharmgkbAccessionId, node.getId());
        }
    }

    private void addChemicals(final Graph graph, final List<Chemical> chemicals) {
        LOGGER.info("Add Chemicals...");
        for (final Chemical chemical : chemicals) {
            final Node node = graph.addNodeFromModel(chemical);
            accessionNodeIdMap.put(chemical.pharmgkbAccessionId, node.getId());
        }
    }

    private void addPhenotypes(final Graph graph, final List<Phenotype> phenotypes) {
        LOGGER.info("Add Phenotypes...");
        for (final Phenotype phenotype : phenotypes) {
            final Node node = graph.addNodeFromModel(phenotype);
            accessionNodeIdMap.put(phenotype.pharmgkbAccessionId, node.getId());
        }
    }

    private void addVariants(final Graph graph, final List<Variant> variants) {
        LOGGER.info("Add Variants...");
        for (final Variant variant : variants) {
            final Node node = graph.addNodeFromModel(variant);
            accessionNodeIdMap.put(variant.variantId, node.getId());
            if (variant.geneIds != null) {
                final String[] geneIds = node.getProperty("gene_ids");
                if (geneIds != null)
                    for (final String geneId : geneIds)
                        graph.addEdge(accessionNodeIdMap.get(geneId), node, "HAS_VARIANT");
            }
        }
    }

    private Map<String, List<Occurrence>> collectPathwayOccurrences(final List<Occurrence> occurrences) {
        final Map<String, List<Occurrence>> pathwayOccurrences = new HashMap<>();
        for (final Occurrence occurrence : occurrences) {
            if (!pathwayOccurrences.containsKey(occurrence.sourceId))
                pathwayOccurrences.put(occurrence.sourceId, new ArrayList<>());
            pathwayOccurrences.get(occurrence.sourceId).add(occurrence);
        }
        return pathwayOccurrences;
    }

    private void addPathways(final Graph graph, final HashMap<String, List<Pathway>> pathways,
                             final Map<String, List<Occurrence>> pathwayOccurrences) {
        LOGGER.info("Add Pathways...");
        for (final String keyName : pathways.keySet()) {
            final String[] parts = StringUtils.split(keyName, '-');
            final String pathwayId = parts[0];
            final Node node = graph.addNode(PATHWAY_LABEL, ID_PROPERTY, pathwayId, NAME_PROPERTY,
                                            parts[1].replace('_', ' '));
            accessionNodeIdMap.put(pathwayId, node.getId());
        }
        for (final String keyName : pathways.keySet())
            addPathwayReactions(graph, keyName, pathways.get(keyName), pathwayOccurrences);
    }

    private void addPathwayReactions(final Graph graph, final String keyName, final List<Pathway> pathways,
                                     final Map<String, List<Occurrence>> pathwayOccurrences) {
        final String pathwayId = StringUtils.split(keyName, '-')[0];
        final List<Occurrence> occurrences = pathwayOccurrences.getOrDefault(pathwayId, Collections.emptyList());
        final Long pathwayNodeId = accessionNodeIdMap.get(pathwayId);
        for (final Pathway pathway : pathways) {
            final Node stepNode = graph.addNodeFromModel(pathway);
            graph.addEdge(pathwayNodeId, stepNode, "HAS_REACTION");
            if (pathway.from != null) {
                for (final String from : StringUtils.split(pathway.from, ',')) {
                    final Occurrence fromOccurrence = findOccurrenceFromReactionName(occurrences, from);
                    if (fromOccurrence != null)
                        graph.addEdge(stepNode, accessionNodeIdMap.get(fromOccurrence.objectId), "HAS_INPUT");
                }
            }
            if (pathway.to != null) {
                for (final String to : StringUtils.split(pathway.to, ',')) {
                    final Occurrence toOccurrence = findOccurrenceFromReactionName(occurrences, to);
                    if (toOccurrence != null)
                        graph.addEdge(stepNode, accessionNodeIdMap.get(toOccurrence.objectId), "HAS_OUTPUT");
                }
            }
            if (pathway.controller != null) {
                for (final String controller : StringUtils.split(pathway.controller, ',')) {
                    final Occurrence controllerOccurrence = findOccurrenceFromReactionName(occurrences, controller);
                    if (controllerOccurrence != null)
                        graph.addEdge(stepNode, accessionNodeIdMap.get(controllerOccurrence.objectId),
                                      "HAS_CONTROLLER");
                }
            }
        }
    }

    private Occurrence findOccurrenceFromReactionName(final List<Occurrence> occurrences, final String name) {
        for (final Occurrence occurrence : occurrences)
            if (occurrence.objectName.equals(name))
                return occurrence;
        return null;
    }

    private void addOccurrences(final Graph graph, final List<Occurrence> occurrences) {
        LOGGER.info("Add Occurrences...");
        for (final Occurrence occurrence : occurrences) {
            final long nodeId;
            if (LITERATURE_LABEL.equalsIgnoreCase(occurrence.sourceType))
                nodeId = addLiteratureIfNotExists(graph, occurrence.sourceId, occurrence.sourceName);
            else if (PATHWAY_LABEL.equalsIgnoreCase(occurrence.sourceType))
                nodeId = addPathwayIfNotExists(graph, occurrence.sourceId, occurrence.sourceName);
            else
                throw new ExporterException("Unknown occurrence source type found '" + occurrence.sourceType + "'");
            switch (occurrence.objectType) {
                case HAPLOTYPE_LABEL:
                    long haplotypeNodeId = addHaplotypeIfNotExists(graph, occurrence.objectId, occurrence.objectName);
                    graph.addEdge(haplotypeNodeId, nodeId, HAS_OCCURRENCE_LABEL);
                    break;
                case HAPLOTYPE_SET_LABEL:
                    long haplotypeSetNodeId = addHaplotypeSetIfNotExists(graph, occurrence.objectId,
                                                                         occurrence.objectName);
                    graph.addEdge(haplotypeSetNodeId, nodeId, HAS_OCCURRENCE_LABEL);
                    break;
                case VARIANT_LABEL:
                    long variantNodeId = addVariantIfNotExists(graph, occurrence.objectId, occurrence.objectName);
                    graph.addEdge(variantNodeId, nodeId, HAS_OCCURRENCE_LABEL);
                    break;
                default:
                    Long objectNodeId = accessionNodeIdMap.get(occurrence.objectId);
                    graph.addEdge(objectNodeId, nodeId, HAS_OCCURRENCE_LABEL);
                    break;
            }
        }
    }

    private long addLiteratureIfNotExists(final Graph graph, final String id, final String name) {
        if (literatureIdNodeIdMap.containsKey(id))
            return literatureIdNodeIdMap.get(id);
        final Node node;
        if (name != null)
            node = graph.addNode(LITERATURE_LABEL, ID_PROPERTY, id, NAME_PROPERTY, name);
        else
            node = graph.addNode(LITERATURE_LABEL, ID_PROPERTY, id);
        literatureIdNodeIdMap.put(id, node.getId());
        return node.getId();
    }

    private long addPathwayIfNotExists(final Graph graph, final String pathwayId, final String pathwayName) {
        if (accessionNodeIdMap.containsKey(pathwayId))
            return accessionNodeIdMap.get(pathwayId);
        final Node node;
        if (pathwayName != null)
            node = graph.addNode(PATHWAY_LABEL, ID_PROPERTY, pathwayId, NAME_PROPERTY, pathwayName);
        else
            node = graph.addNode(PATHWAY_LABEL, ID_PROPERTY, pathwayId);
        accessionNodeIdMap.put(pathwayId, node.getId());
        return node.getId();
    }

    private long addVariantIfNotExists(final Graph graph, final String variantId, String variantName) {
        if (variantId != null && accessionNodeIdMap.containsKey(variantId))
            return accessionNodeIdMap.get(variantId);
        if (variantName != null) {
            final Node nodeByName = graph.findNode(VARIANT_LABEL, NAME_PROPERTY, variantName);
            if (nodeByName != null) {
                if (variantId != null) {
                    accessionNodeIdMap.put(variantId, nodeByName.getId());
                    nodeByName.setProperty(ID_PROPERTY, variantId);
                    graph.update(nodeByName);
                }
                return nodeByName.getId();
            }
        }
        final Node node;
        if (variantId != null && variantName != null) {
            node = graph.addNode(VARIANT_LABEL, ID_PROPERTY, variantId, NAME_PROPERTY, variantName);
        } else if (variantId != null) {
            node = graph.addNode(VARIANT_LABEL, ID_PROPERTY, variantId);
        } else if (variantName != null) {
            node = graph.addNode(VARIANT_LABEL, NAME_PROPERTY, variantName);
        } else
            throw new ExporterException("Failed to get or add variant with both id and name null");
        if (variantId != null)
            accessionNodeIdMap.put(variantId, node.getId());
        return node.getId();
    }

    private long addHaplotypeIfNotExists(final Graph graph, final String haplotypeId, final String haplotypeName) {
        if (haplotypeId != null && accessionNodeIdMap.containsKey(haplotypeId))
            return accessionNodeIdMap.get(haplotypeId);
        if (haplotypeName != null) {
            final Node nodeByName = graph.findNode(HAPLOTYPE_LABEL, NAME_PROPERTY, haplotypeName);
            if (nodeByName != null) {
                if (haplotypeId != null) {
                    accessionNodeIdMap.put(haplotypeId, nodeByName.getId());
                    nodeByName.setProperty(ID_PROPERTY, haplotypeId);
                    graph.update(nodeByName);
                }
                return nodeByName.getId();
            }
        }
        final Node node;
        if (haplotypeId != null && haplotypeName != null) {
            node = graph.addNode(HAPLOTYPE_LABEL, ID_PROPERTY, haplotypeId, NAME_PROPERTY, haplotypeName);
        } else if (haplotypeId != null) {
            node = graph.addNode(HAPLOTYPE_LABEL, ID_PROPERTY, haplotypeId);
        } else if (haplotypeName != null) {
            node = graph.addNode(HAPLOTYPE_LABEL, NAME_PROPERTY, haplotypeName);
        } else
            throw new ExporterException("Failed to get or add haplotype with both id and name null");
        if (haplotypeId != null)
            accessionNodeIdMap.put(haplotypeId, node.getId());
        return node.getId();
    }

    private long addHaplotypeSetIfNotExists(final Graph graph, final String haplotypeId, final String haplotypeName) {
        if (accessionNodeIdMap.containsKey(haplotypeId))
            return accessionNodeIdMap.get(haplotypeId);
        final Node node;
        if (haplotypeName != null)
            node = graph.addNode(HAPLOTYPE_SET_LABEL, ID_PROPERTY, haplotypeId, NAME_PROPERTY, haplotypeName);
        else
            node = graph.addNode(HAPLOTYPE_SET_LABEL, ID_PROPERTY, haplotypeId);
        accessionNodeIdMap.put(haplotypeId, node.getId());
        return node.getId();
    }

    private void addClinicalAnnotations(final Graph graph, final List<ClinicalAnnotation> annotations) {
        LOGGER.info("Add ClinicalAnnotations...");
        for (final ClinicalAnnotation annotation : annotations) {
            final Node node = graph.addNodeFromModel(annotation);
            final String historyTsvText = dataSource.clinicalAnnotationHistories.stream().filter(
                    h -> h.clinicalAnnotationId.equals(annotation.clinicalAnnotationId)).map(
                    h -> h.date + '\t' + h.type + '\t' + h.comment).collect(Collectors.joining("\n"));
            if (StringUtils.isNotEmpty(historyTsvText)) {
                node.setProperty("history", historyTsvText);
                graph.update(node);
            }
            addVariantOrHaplotypeAssociations(graph, annotation.variantHaplotypes, node);
            if (annotation.gene != null)
                for (final String gene : StringUtils.split(annotation.gene, ';'))
                    graph.addEdge(node, graph.findNode(GENE_LABEL, "symbol", gene), ASSOCIATED_WITH_LABEL);
            if (annotation.drugs != null)
                for (final String drug : StringUtils.split(annotation.drugs, ';'))
                    graph.addEdge(node, graph.findNode(CHEMICAL_LABEL, NAME_PROPERTY, drug), ASSOCIATED_WITH_LABEL);
            if (annotation.phenotypes != null)
                for (final String phenotype : StringUtils.split(annotation.phenotypes, ';'))
                    graph.addEdge(node, graph.findNode(PHENOTYPE_LABEL, NAME_PROPERTY, phenotype),
                                  ASSOCIATED_WITH_LABEL);
        }
    }

    private void addClinicalAnnotationAlleles(final Graph graph, final List<ClinicalAnnotationAllele> alleles) {
        LOGGER.info("Add ClinicalAnnotationAlleles...");
        for (final ClinicalAnnotationAllele allele : alleles) {
            final Node node = graph.addNodeFromModel(allele);
            final Node annotationNode = graph.findNode("ClinicalAnnotation", ID_PROPERTY, allele.clinicalAnnotationId);
            graph.addEdge(annotationNode, node, "HAS_ALLELE");
        }
    }

    private void addAutomatedAnnotations(final Graph graph, final List<AutomatedAnnotation> annotations) {
        LOGGER.info("Add AutomatedAnnotations...");
        for (final AutomatedAnnotation annotation : annotations) {
            final Node node = graph.addNodeFromModel(annotation);
            if (annotation.geneIds != null)
                for (String geneId : StringUtils.split(annotation.geneIds, ','))
                    graph.addEdge(node, accessionNodeIdMap.get(geneId), ASSOCIATED_WITH_LABEL);
            if (annotation.chemicalId != null) {
                long chemicalNodeId = addChemicalIfNotExists(graph, annotation.chemicalId, annotation.chemicalName);
                graph.addEdge(node, chemicalNodeId, ASSOCIATED_WITH_LABEL);
            }
            if (annotation.variationId != null) {
                long targetNodeId;
                if (annotation.variationType.equals(HAPLOTYPE_LABEL))
                    targetNodeId = addHaplotypeIfNotExists(graph, annotation.variationId, annotation.variationName);
                else {
                    final String variationId = annotation.variationId.startsWith("rs") ? null : annotation.variationId;
                    targetNodeId = addVariantIfNotExists(graph, variationId, annotation.variationName);
                }
                graph.addEdge(node, targetNodeId, ASSOCIATED_WITH_LABEL);
            }
        }
    }

    private long addChemicalIfNotExists(final Graph graph, final String chemicalId, final String chemicalName) {
        if (chemicalId != null && accessionNodeIdMap.containsKey(chemicalId))
            return accessionNodeIdMap.get(chemicalId);
        if (chemicalName != null) {
            final Node nodeByName = graph.findNode(CHEMICAL_LABEL, NAME_PROPERTY, chemicalName);
            if (nodeByName != null) {
                if (chemicalId != null) {
                    accessionNodeIdMap.put(chemicalId, nodeByName.getId());
                    nodeByName.setProperty(ID_PROPERTY, chemicalId);
                    graph.update(nodeByName);
                }
                return nodeByName.getId();
            }
        }
        final Node node;
        if (chemicalId != null && chemicalName != null) {
            node = graph.addNode(CHEMICAL_LABEL, ID_PROPERTY, chemicalId, NAME_PROPERTY, chemicalName);
        } else if (chemicalId != null) {
            node = graph.addNode(CHEMICAL_LABEL, ID_PROPERTY, chemicalId);
        } else if (chemicalName != null) {
            node = graph.addNode(CHEMICAL_LABEL, NAME_PROPERTY, chemicalName);
        } else
            throw new ExporterException("Failed to get or add chemical with both id and name null");
        if (chemicalId != null)
            accessionNodeIdMap.put(chemicalId, node.getId());
        return node.getId();
    }

    private void addClinicalVariants(final Graph graph, final List<ClinicalVariant> clinicalVariants) {
        LOGGER.info("Add ClinicalVariants...");
        for (final ClinicalVariant clinicalVariant : clinicalVariants) {
            final Node node = graph.addNodeFromModel(clinicalVariant);
            if (clinicalVariant.gene != null)
                for (final String geneId : StringUtils.split(clinicalVariant.gene, ',')) {
                    final Node geneNode = graph.findNode(GENE_LABEL, "symbol", geneId);
                    graph.addEdge(node, geneNode, ASSOCIATED_WITH_LABEL);
                }
            for (final String chemical : parseStringArray(clinicalVariant.chemicals)) {
                final long chemicalNodeId = addChemicalIfNotExists(graph, null, chemical);
                graph.addEdge(node, chemicalNodeId, ASSOCIATED_WITH_LABEL);
            }
            for (final String phenotype : parseStringArray(clinicalVariant.phenotypes)) {
                final Node phenotypeNode = graph.findNode(PHENOTYPE_LABEL, NAME_PROPERTY, phenotype);
                graph.addEdge(node, phenotypeNode, ASSOCIATED_WITH_LABEL);
            }
            addVariantOrHaplotypeAssociations(graph, clinicalVariant.variant, node);
        }
    }

    private String[] parseStringArray(final String arrayString) {
        return arrayString != null ? arrayString.split("(?<=[^3]),(?=[^ ])") : new String[0];
    }

    private void addVariantOrHaplotypeAssociations(final Graph graph, final String variantsOrHaplotypes,
                                                   final Node node) {
        for (final String variant : parseVariantHaplotypeCommaSpaceStringArray(variantsOrHaplotypes)) {
            final long targetNodeId;
            if (variant.contains("rs"))
                targetNodeId = addVariantIfNotExists(graph, null, variant);
            else
                targetNodeId = addHaplotypeIfNotExists(graph, null, variant);
            graph.addEdge(node, targetNodeId, ASSOCIATED_WITH_LABEL);
        }
    }

    private String[] parseVariantHaplotypeCommaSpaceStringArray(final String arrayString) {
        final List<String> names = new ArrayList<>(Arrays.asList(parseCommaSpaceStringArray(arrayString)));
        for (int i = names.size() - 1; i >= 1; i--) {
            final String name = names.get(i);
            for (final String prefix : MERGE_HAPLOTYPE_NAME_PREFIXES)
                if (name.startsWith(prefix)) {
                    names.set(i - 1, names.get(i - 1) + ", " + name);
                    names.remove(i);
                    break;
                }
        }
        return names.toArray(new String[0]);
    }

    private String[] parseCommaSpaceStringArray(final String arrayString) {
        return arrayString != null ? StringUtils.splitByWholeSeparator(arrayString, ", ") : new String[0];
    }

    private <T extends VariantAnnotation> void addVariantAnnotations(final Graph graph, final List<T> annotations) {
        LOGGER.info("Add " + annotations.get(0).getClass().getSimpleName() + "...");
        for (final T annotation : annotations) {
            final Node node = graph.addNodeFromModel(annotation);
            variantAnnotationIdNodeIdMap.put(annotation.annotationId, node.getId());
            if (annotation.gene != null) {
                for (final String gene : parseQuotedStringArray(annotation.gene)) {
                    final Node geneNode = graph.findNode(GENE_LABEL, "symbol", gene);
                    graph.addEdge(node, geneNode, ASSOCIATED_WITH_LABEL);
                }
            }
            if (annotation.drugs != null) {
                for (final String drug : parseQuotedStringArray(annotation.drugs)) {
                    final Node drugNode = graph.findNode(CHEMICAL_LABEL, NAME_PROPERTY, drug);
                    graph.addEdge(node, drugNode, ASSOCIATED_WITH_LABEL);
                }
            }
            if (annotation.pmid != null) {
                final long literatureNode = addLiteratureIfNotExists(graph, "PMID:" + annotation.pmid, null);
                graph.addEdge(node, literatureNode, HAS_OCCURRENCE_LABEL);
            }
            addVariantOrHaplotypeAssociations(graph, annotation.variantHaplotypes, node);
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

    private void addStudyParameters(final Graph graph, final List<StudyParameters> studyParameters) {
        LOGGER.info("Add StudyParameters...");
        for (final StudyParameters studyParameter : studyParameters) {
            final Node studyParameterNode = graph.addNodeFromModel(studyParameter);
            final Long variantAnnotationNodeId = variantAnnotationIdNodeIdMap.get(studyParameter.variantAnnotationId);
            graph.addEdge(variantAnnotationNodeId, studyParameterNode, "WITH_PARAMETERS");
        }
    }

    private void addDrugLabels(final Graph graph, final List<DrugLabel> drugLabels) {
        LOGGER.info("Add DrugLabels...");
        for (final DrugLabel drugLabel : drugLabels) {
            final Node node = graph.addNodeFromModel(drugLabel);
            if (drugLabel.variantsHaplotypes != null) {
                for (final String variant : StringUtils.splitByWholeSeparator(drugLabel.variantsHaplotypes, "; ")) {
                    final long targetNodeId = variant.contains("rs") ? addVariantIfNotExists(graph, null, variant) :
                                              addHaplotypeIfNotExists(graph, null, variant);
                    graph.addEdge(node, targetNodeId, ASSOCIATED_WITH_LABEL);
                }
            }
            if (drugLabel.chemicals != null) {
                for (final String chemical : StringUtils.splitByWholeSeparator(drugLabel.chemicals, "; ")) {
                    final long targetNodeId = addChemicalIfNotExists(graph, null, chemical);
                    graph.addEdge(node, targetNodeId, ASSOCIATED_WITH_LABEL);
                }
            }
        }
    }

    private void addDrugLabelsByGene(final Graph graph, final List<DrugLabelsByGene> drugLabelsByGenes) {
        LOGGER.info("Add DrugLabelsByGene...");
        for (final DrugLabelsByGene drugLabelsByGene : drugLabelsByGenes) {
            for (final String labelId : StringUtils.split(drugLabelsByGene.labelIds, ';')) {
                final Node labelNode = graph.findNode("DrugLabel", ID_PROPERTY, labelId);
                final Node geneNode = graph.findNode(GENE_LABEL, ID_PROPERTY, drugLabelsByGene.geneId);
                graph.addEdge(labelNode, geneNode, ASSOCIATED_WITH_LABEL);
            }
        }
    }
}