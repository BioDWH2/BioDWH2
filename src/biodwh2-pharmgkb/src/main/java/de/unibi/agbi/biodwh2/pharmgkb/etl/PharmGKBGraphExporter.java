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
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys(ID_PROPERTY, "symbol", "name");
        addGenes(graph, dataSource.genes);
        addChemicals(graph, dataSource.chemicals);
        addPhenotypes(graph, dataSource.phenotyps);
        addStudyParameters(graph, dataSource.studyParameters);
        final Map<String, List<Occurrence>> pathwayOccurrences = collectPathwayOccurrences(dataSource.occurrences);
        addPathways(graph, dataSource.pathways, pathwayOccurrences);
        addVariants(graph, dataSource.variants);
        addOccurrences(graph, dataSource.occurrences);
        addClinicalAnnotations(graph, dataSource.clinicalAnnotations);
        addAutomatedAnnotations(graph, dataSource.automatedAnnotations);
        addClinicalVariants(graph, dataSource.clinicalVariants);
        addVariantDrugAnnotations(graph, dataSource.variantDrugAnnotations);
        addVariantFunctionalAnalysisAnnotations(graph, dataSource.variantFunctionalAnalysisAnnotations);
        addVariantPhenotypeAnnotations(graph, dataSource.variantPhenotypeAnnotations);
        addClinicalAnnotationMetadata(graph, dataSource.clinicalAnnotationMetadata);
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
            if (variant.geneIds != null)
                for (String geneId : node.<String[]>getProperty("gene_ids"))
                    graph.addEdge(accessionNodeIdMap.get(geneId), node, "HAS_VARIANT");
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
            final String[] parts = StringUtils.split(keyName, "-");
            final String pathwayId = parts[0];
            final Node node = graph.addNode(PATHWAY_LABEL, ID_PROPERTY, pathwayId, "name", parts[1].replace("_", " "));
            accessionNodeIdMap.put(pathwayId, node.getId());
        }
        for (final String keyName : pathways.keySet())
            addPathwayReactions(graph, keyName, pathways.get(keyName), pathwayOccurrences);
    }

    private void addPathwayReactions(final Graph graph, final String keyName, final List<Pathway> pathways,
                                     final Map<String, List<Occurrence>> pathwayOccurrences) {
        final String pathwayId = StringUtils.split(keyName, "-")[0];
        final List<Occurrence> occurrences = pathwayOccurrences.getOrDefault(pathwayId, Collections.emptyList());
        final Long pathwayNodeId = accessionNodeIdMap.get(pathwayId);
        for (final Pathway pathway : pathways) {
            final Node stepNode = graph.addNodeFromModel(pathway);
            graph.addEdge(pathwayNodeId, stepNode, "HAS_REACTION");
            if (pathway.from != null) {
                for (final String from : pathway.from.split(",")) {
                    final Occurrence fromOccurrence = findOccurrenceFromReactionName(occurrences, from);
                    if (fromOccurrence != null)
                        graph.addEdge(stepNode, accessionNodeIdMap.get(fromOccurrence.objectId), "HAS_INPUT");
                }
            }
            if (pathway.to != null) {
                for (final String to : pathway.to.split(",")) {
                    final Occurrence toOccurrence = findOccurrenceFromReactionName(occurrences, to);
                    if (toOccurrence != null)
                        graph.addEdge(stepNode, accessionNodeIdMap.get(toOccurrence.objectId), "HAS_OUTPUT");
                }
            }
            if (pathway.controller != null) {
                for (final String controller : pathway.controller.split(",")) {
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

    private void addStudyParameters(final Graph graph, final List<StudyParameters> studyParameters) {
        LOGGER.info("Add StudyParameters...");
        for (final StudyParameters studyParameter : studyParameters)
            graph.addNodeFromModel(studyParameter);
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
            node = graph.addNode(LITERATURE_LABEL, ID_PROPERTY, id, "name", name);
        else
            node = graph.addNode(LITERATURE_LABEL, ID_PROPERTY, id);
        literatureIdNodeIdMap.put(id, node.getId());
        return node.getId();
    }

    private long addPathwayIfNotExists(final Graph graph, final String pathwayId, final String pathwayName) {
        if (accessionNodeIdMap.containsKey(pathwayId))
            return accessionNodeIdMap.get(pathwayId);
        Node node;
        if (pathwayName != null)
            node = graph.addNode(PATHWAY_LABEL, ID_PROPERTY, pathwayId, "name", pathwayName);
        else
            node = graph.addNode(PATHWAY_LABEL, ID_PROPERTY, pathwayId);
        accessionNodeIdMap.put(pathwayId, node.getId());
        return node.getId();
    }

    private long addVariantIfNotExists(final Graph graph, final String variantId, String variantName) {
        if (variantId != null && accessionNodeIdMap.containsKey(variantId))
            return accessionNodeIdMap.get(variantId);
        if (variantName != null) {
            final Node nodeByName = graph.findNode(VARIANT_LABEL, "name", variantName);
            if (nodeByName != null) {
                if (variantId != null) {
                    accessionNodeIdMap.put(variantId, nodeByName.getId());
                    nodeByName.setProperty("id", variantId);
                    graph.update(nodeByName);
                }
                return nodeByName.getId();
            }
        }
        final Node node;
        if (variantId != null && variantName != null) {
            node = graph.addNode(VARIANT_LABEL, ID_PROPERTY, variantId, "name", variantName);
        } else if (variantId != null) {
            node = graph.addNode(VARIANT_LABEL, ID_PROPERTY, variantId);
        } else if (variantName != null) {
            node = graph.addNode(VARIANT_LABEL, "name", variantName);
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
            final Node nodeByName = graph.findNode(HAPLOTYPE_LABEL, "name", haplotypeName);
            if (nodeByName != null) {
                if (haplotypeId != null) {
                    accessionNodeIdMap.put(haplotypeId, nodeByName.getId());
                    nodeByName.setProperty("id", haplotypeId);
                    graph.update(nodeByName);
                }
                return nodeByName.getId();
            }
        }
        final Node node;
        if (haplotypeId != null && haplotypeName != null) {
            node = graph.addNode(HAPLOTYPE_LABEL, ID_PROPERTY, haplotypeId, "name", haplotypeName);
        } else if (haplotypeId != null) {
            node = graph.addNode(HAPLOTYPE_LABEL, ID_PROPERTY, haplotypeId);
        } else if (haplotypeName != null) {
            node = graph.addNode(HAPLOTYPE_LABEL, "name", haplotypeName);
        } else
            throw new ExporterException("Failed to get or add haplotype with both id and name null");
        if (haplotypeId != null)
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

    private void addClinicalAnnotations(final Graph graph, final List<ClinicalAnnotation> annotations) {
        LOGGER.info("Add ClinicalAnnotations...");
        for (final ClinicalAnnotation annotation : annotations)
            graph.addNodeFromModel(annotation);
    }

    private void addAutomatedAnnotations(final Graph graph, final List<AutomatedAnnotation> annotations) {
        LOGGER.info("Add AutomatedAnnotations...");
        for (final AutomatedAnnotation annotation : annotations) {
            final Node node = graph.addNodeFromModel(annotation);
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
            final Node nodeByName = graph.findNode(CHEMICAL_LABEL, "name", chemicalName);
            if (nodeByName != null) {
                if (chemicalId != null) {
                    accessionNodeIdMap.put(chemicalId, nodeByName.getId());
                    nodeByName.setProperty("id", chemicalId);
                    graph.update(nodeByName);
                }
                return nodeByName.getId();
            }
        }
        final Node node;
        if (chemicalId != null && chemicalName != null) {
            node = graph.addNode(CHEMICAL_LABEL, ID_PROPERTY, chemicalId, "name", chemicalName);
        } else if (chemicalId != null) {
            node = graph.addNode(CHEMICAL_LABEL, ID_PROPERTY, chemicalId);
        } else if (chemicalName != null) {
            node = graph.addNode(CHEMICAL_LABEL, "name", chemicalName);
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
                for (final String geneId : StringUtils.split(clinicalVariant.gene, ",")) {
                    final Node geneNode = graph.findNode(GENE_LABEL, "symbol", geneId);
                    graph.addEdge(node, geneNode, ASSOCIATED_WITH_LABEL);
                }
            for (final String chemical : parseStringArray(clinicalVariant.chemicals)) {
                final long chemicalNodeId = addChemicalIfNotExists(graph, null, chemical);
                graph.addEdge(node, chemicalNodeId, ASSOCIATED_WITH_LABEL);
            }
            for (final String phenotype : parseStringArray(clinicalVariant.phenotypes)) {
                final Node phenotypeNode = graph.findNode(PHENOTYPE_LABEL, "name", phenotype);
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

    private void addVariantDrugAnnotations(final Graph graph, final List<VariantDrugAnnotation> annotations) {
        LOGGER.info("Add VariantDrugAnnotations...");
        for (final VariantDrugAnnotation annotation : annotations) {
            final Node node = graph.addNodeFromModel(annotation);
            variantAnnotationIdNodeIdMap.put(annotation.annotationId, node.getId());
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
                    final Integer studyParametersIdInteger = Integer.parseInt(studyParametersId);
                    final Node studyParametersNode = graph.findNode("StudyParameters", ID_PROPERTY,
                                                                    studyParametersIdInteger);
                    graph.addEdge(node, studyParametersNode, "WITH_PARAMETERS");
                }
            }
            if (annotation.pmid != null) {
                final long literatureNode = addLiteratureIfNotExists(graph, "PMID:" + annotation.pmid, null);
                graph.addEdge(node, literatureNode, HAS_OCCURRENCE_LABEL);
            }
            addVariantOrHaplotypeAssociations(graph, annotation.variant, node);
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
            final Node node = graph.addNodeFromModel(annotation);
            variantAnnotationIdNodeIdMap.put(annotation.annotationId, node.getId());
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
                    final Integer studyParametersIdInteger = Integer.parseInt(studyParametersId);
                    final Node studyParametersNode = graph.findNode("StudyParameters", ID_PROPERTY,
                                                                    studyParametersIdInteger);
                    graph.addEdge(node, studyParametersNode, "WITH_PARAMETERS");
                }
            }
            if (annotation.pmid != null) {
                final long literatureNode = addLiteratureIfNotExists(graph, "PMID:" + annotation.pmid, null);
                graph.addEdge(node, literatureNode, HAS_OCCURRENCE_LABEL);
            }
            addVariantOrHaplotypeAssociations(graph, annotation.variant, node);
        }
    }

    private void addVariantPhenotypeAnnotations(final Graph graph, final List<VariantPhenotypeAnnotation> annotations) {
        LOGGER.info("Add VariantPhenotypeAnnotations...");
        for (final VariantPhenotypeAnnotation annotation : annotations) {
            final Node node = graph.addNodeFromModel(annotation);
            variantAnnotationIdNodeIdMap.put(annotation.annotationId, node.getId());
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
                    final Integer studyParametersIdInteger = Integer.parseInt(studyParametersId);
                    final Node studyParametersNode = graph.findNode("StudyParameters", ID_PROPERTY,
                                                                    studyParametersIdInteger);
                    graph.addEdge(node, studyParametersNode, "WITH_PARAMETERS");
                }
            }
            if (annotation.pmid != null) {
                final long literatureNode = addLiteratureIfNotExists(graph, "PMID:" + annotation.pmid, null);
                graph.addEdge(node, literatureNode, HAS_OCCURRENCE_LABEL);
            }
            addVariantOrHaplotypeAssociations(graph, annotation.variant, node);
        }
    }

    private void addClinicalAnnotationMetadata(final Graph graph,
                                               final List<ClinicalAnnotationMetadata> clinicalAnnotationMetadata) {
        LOGGER.info("Add ClinicalAnnotationMetadata...");
        for (final ClinicalAnnotationMetadata metadata : clinicalAnnotationMetadata) {
            final Node node = graph.addNodeFromModel(metadata);
            if (metadata.location != null) {
                for (final String location : parseVariantHaplotypeCommaSpaceStringArray(metadata.location)) {
                    final long targetNodeId = location.contains("rs") ? addVariantIfNotExists(graph, null, location) :
                                              addHaplotypeIfNotExists(graph, null, location);
                    graph.addEdge(node, targetNodeId, "HAS_LOCATION");
                }
            }
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
            if (metadata.pmids != null) {
                for (final String pmid : metadata.pmids.split(";")) {
                    final long literatureNode = addLiteratureIfNotExists(graph, "PMID:" + pmid, null);
                    graph.addEdge(node, literatureNode, HAS_OCCURRENCE_LABEL);
                }
            }
            if (metadata.genotypePhenotypesId != null) {
                for (final String genotypePhenotypeId : StringUtils.split(metadata.genotypePhenotypesId, ";")) {
                    final Integer genotypePhenotypeIdInteger = Integer.parseInt(genotypePhenotypeId);
                    final Node annotationNode = graph.findNode("ClinicalAnnotation", ID_PROPERTY,
                                                               genotypePhenotypeIdInteger);
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
                    if (LOGGER.isWarnEnabled())
                        LOGGER.warn("Skipping invalid variant annotation links for ClinicalAnnotationMetadata '" +
                                    metadata.clinicalAnnotationId + "'.");
                    return;
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
            for (final String labelId : drugLabelsByGene.labelIds.split(";")) {
                final Node labelNode = graph.findNode("DrugLabel", ID_PROPERTY, labelId);
                final Node geneNode = graph.findNode(GENE_LABEL, ID_PROPERTY, drugLabelsByGene.geneId);
                graph.addEdge(labelNode, geneNode, ASSOCIATED_WITH_LABEL);
            }
        }
    }
}