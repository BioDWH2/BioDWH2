package de.unibi.agbi.biodwh2.pharmgkb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.pharmgkb.PharmGKBDataSource;
import de.unibi.agbi.biodwh2.pharmgkb.model.*;
import de.unibi.agbi.biodwh2.pharmgkb.model.guideline.Citation;
import de.unibi.agbi.biodwh2.pharmgkb.model.guideline.CrossReference;
import de.unibi.agbi.biodwh2.pharmgkb.model.guideline.GuidelineAnnotation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class PharmGKBGraphExporter extends GraphExporter<PharmGKBDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(PharmGKBGraphExporter.class);
    public static final String QUOTED_ARRAY_DELIMITER = "\", \"";
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
    static final String WEB_PAGE_LABEL = "WebPage";
    private static final String ASSOCIATED_WITH_LABEL = "ASSOCIATED_WITH";
    private static final String HAS_OCCURRENCE_LABEL = "HAS_OCCURRENCE";
    private static final String[] MERGE_HAPLOTYPE_NAME_PREFIXES = new String[]{
            "Taiwan-Hakka", "Gifu-like", "Agrigento-like", "Dallas", "Panama' Sassari", "Cagliari", "Birmingham"
    };

    private final Map<String, Long> accessionNodeIdMap = new HashMap<>();
    private final Map<String, Long> literatureIdNodeIdMap = new HashMap<>();
    private final Map<String, Long> webUrlNodeIdMap = new HashMap<>();
    private final Map<Integer, Long> variantAnnotationIdNodeIdMap = new HashMap<>();

    public PharmGKBGraphExporter(final PharmGKBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 6;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(LITERATURE_LABEL, ID_PROPERTY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(VARIANT_LABEL, ID_PROPERTY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(HAPLOTYPE_LABEL, ID_PROPERTY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(HAPLOTYPE_SET_LABEL, ID_PROPERTY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PATHWAY_LABEL, ID_PROPERTY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(CHEMICAL_LABEL, ID_PROPERTY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PHENOTYPE_LABEL, ID_PROPERTY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("StudyParameters", ID_PROPERTY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("GuidelineAnnotation", ID_PROPERTY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("ClinicalAnnotation", ID_PROPERTY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("DrugLabel", ID_PROPERTY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_PROPERTY, IndexDescription.Type.UNIQUE));
        // TODO: gene symbols appear with duplicates which are data errors!
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, "symbol", IndexDescription.Type.NON_UNIQUE));
        graph.addIndex(IndexDescription.forNode(VARIANT_LABEL, NAME_PROPERTY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(HAPLOTYPE_LABEL, NAME_PROPERTY, IndexDescription.Type.UNIQUE));
        // TODO: chemical names appear with duplicates which are data errors!
        graph.addIndex(IndexDescription.forNode(CHEMICAL_LABEL, NAME_PROPERTY, IndexDescription.Type.NON_UNIQUE));
        // TODO: phenotype names appear with duplicates which are data errors!
        graph.addIndex(IndexDescription.forNode(PHENOTYPE_LABEL, NAME_PROPERTY, IndexDescription.Type.NON_UNIQUE));
        addGuidelineAnnotations(graph, dataSource.guidelineAnnotations);
        addGenes(graph, dataSource.genes);
        addChemicals(graph, dataSource.chemicals);
        addPhenotypes(graph, dataSource.phenotypes);
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
        addClinicalAnnotationEvidences(graph, dataSource.clinicalAnnotationEvidences);
        return true;
    }

    private void addGuidelineAnnotations(final Graph graph, final List<GuidelineAnnotation> guidelineAnnotations) {
        LOGGER.info("Add GuidelineAnnotations...");
        for (final GuidelineAnnotation annotation : guidelineAnnotations) {
            final Node node = graph.addNodeFromModel(annotation.guideline);
            accessionNodeIdMap.put(annotation.guideline.id, node.getId());
            final Map<String, Citation> citations = new HashMap<>();
            if (annotation.citations != null)
                for (final Citation citation : annotation.citations)
                    citations.put(citation.resourceId, citation);
            if (annotation.guideline.literature != null)
                for (final Citation citation : annotation.guideline.literature)
                    citations.put(citation.resourceId, citation);
            for (final Citation citation : citations.values())
                connectGuidelineAnnotationWithCitation(graph, node, citation);
        }
    }

    private void connectGuidelineAnnotationWithCitation(final Graph graph, final Node node, final Citation citation) {
        Integer pmid = null;
        String doi = null;
        String pmcid = null;
        for (final CrossReference reference : citation.crossReferences) {
            if ("DOI".equals(reference.resource))
                doi = reference.resourceId;
            else if ("PubMed".equals(reference.resource))
                pmid = Integer.parseInt(reference.resourceId);
            else if ("PubMed Central".equals(reference.resource))
                pmcid = reference.resourceId;
        }
        final long literatureNode = addLiteratureIfNotExists(graph, citation.resourceId, citation.title, pmid, pmcid,
                                                             doi, citation.year);
        graph.addEdge(node, literatureNode, HAS_OCCURRENCE_LABEL);
    }

    private long addLiteratureIfNotExists(final Graph graph, final String id, final String title, final Integer pmid,
                                          final String pmcid, final String doi, final Integer year) {
        Node node = null;
        if (id != null && literatureIdNodeIdMap.containsKey(id))
            node = graph.getNode(literatureIdNodeIdMap.get(id));
        if (pmid != null && literatureIdNodeIdMap.containsKey("PMID:" + pmid))
            node = graph.getNode(literatureIdNodeIdMap.get("PMID:" + pmid));
        if (pmcid != null && literatureIdNodeIdMap.containsKey(pmcid))
            node = graph.getNode(literatureIdNodeIdMap.get(pmcid));
        if (node != null) {
            boolean changed = false;
            if (id != null && !node.hasProperty(ID_PROPERTY)) {
                node.setProperty(ID_PROPERTY, id);
                changed = true;
            }
            if (title != null && !node.hasProperty("title")) {
                node.setProperty("title", title);
                changed = true;
            }
            if (pmid != null && !node.hasProperty("pmid")) {
                node.setProperty("pmid", pmid);
                changed = true;
            }
            if (pmcid != null && !node.hasProperty("pmcid")) {
                node.setProperty("pmcid", pmcid);
                changed = true;
            }
            if (doi != null && !node.hasProperty("doi")) {
                node.setProperty("doi", doi);
                changed = true;
            }
            if (year != null && !node.hasProperty("year")) {
                node.setProperty("year", year);
                changed = true;
            }
            if (changed)
                graph.update(node);
        } else {
            final NodeBuilder builder = graph.buildNode().withLabel(LITERATURE_LABEL);
            builder.withPropertyIfNotNull(ID_PROPERTY, id);
            builder.withPropertyIfNotNull("title", title);
            builder.withPropertyIfNotNull("pmid", pmid);
            builder.withPropertyIfNotNull("pmcid", pmcid);
            builder.withPropertyIfNotNull("doi", doi);
            builder.withPropertyIfNotNull("year", year);
            node = builder.build();
        }
        if (id != null)
            literatureIdNodeIdMap.put(id, node.getId());
        if (pmid != null)
            literatureIdNodeIdMap.put("PMID:" + pmid, node.getId());
        if (pmcid != null)
            literatureIdNodeIdMap.put(pmcid, node.getId());
        return node.getId();
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
            final String[] externalVocabulary = parseExternalVocabulary(chemical.externalVocabulary);
            final Node node;
            if (externalVocabulary != null)
                node = graph.addNodeFromModel(chemical, "external_vocabulary", externalVocabulary);
            else
                node = graph.addNodeFromModel(chemical);
            accessionNodeIdMap.put(chemical.pharmgkbAccessionId, node.getId());
        }
    }

    private String[] parseExternalVocabulary(String text) {
        if (text == null || StringUtils.isEmpty(text))
            return null;
        List<String> result = new ArrayList<>();
        int start = 0;
        boolean quoted = text.charAt(0) == '"';
        boolean withinQuotes = false;
        int withinBracesCount = 0;
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) == ',' && text.charAt(i + 1) == ' ') {
                if ((!quoted || !withinQuotes) && withinBracesCount == 0) {
                    result.add(text.substring(start, i));
                    start = i + 2;
                    i++;
                }
            } else if (text.charAt(i) == '(') {
                withinBracesCount++;
            } else if (text.charAt(i) == ')') {
                withinBracesCount--;
            } else if (quoted && text.charAt(i) == '"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                withinQuotes = !withinQuotes;
            }
        }
        if (start < text.length()) {
            result.add(text.substring(start));
        }
        if (quoted) {
            result.replaceAll(s -> StringUtils.strip(s, "\""));
        }
        return result.toArray(new String[0]);
    }

    private void addPhenotypes(final Graph graph, final List<Phenotype> phenotypes) {
        LOGGER.info("Add Phenotypes...");
        for (final Phenotype phenotype : phenotypes) {
            final String[] externalVocabulary = parseExternalVocabulary(phenotype.externalVocabulary);
            final Node node;
            if (externalVocabulary != null)
                node = graph.addNodeFromModel(phenotype, "external_vocabulary", externalVocabulary);
            else
                node = graph.addNodeFromModel(phenotype);
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
                    if (fromOccurrence != null) {
                        final Long fromNodeId = accessionNodeIdMap.get(fromOccurrence.objectId);
                        if (fromNodeId != null) {
                            graph.addEdge(stepNode, fromNodeId, "HAS_INPUT");
                        } else {
                            LOGGER.warn("Failed to add pathway input for missing source object '{}' ({}) of type '{}'",
                                        fromOccurrence.objectName, fromOccurrence.objectId, fromOccurrence.objectType);
                        }
                    }
                }
            }
            if (pathway.to != null) {
                for (final String to : StringUtils.split(pathway.to, ',')) {
                    final Occurrence toOccurrence = findOccurrenceFromReactionName(occurrences, to);
                    if (toOccurrence != null) {
                        final Long toNodeId = accessionNodeIdMap.get(toOccurrence.objectId);
                        if (toNodeId != null) {
                            graph.addEdge(stepNode, toNodeId, "HAS_OUTPUT");
                        } else {
                            LOGGER.warn("Failed to add pathway output for missing source object '{}' ({}) of type '{}'",
                                        toOccurrence.objectName, toOccurrence.objectId, toOccurrence.objectType);
                        }
                    }
                }
            }
            if (pathway.controller != null) {
                for (final String controller : StringUtils.split(pathway.controller, ',')) {
                    final Occurrence controllerOccurrence = findOccurrenceFromReactionName(occurrences, controller);
                    if (controllerOccurrence != null) {
                        final Long controllerNodeId = accessionNodeIdMap.get(controllerOccurrence.objectId);
                        if (controllerNodeId != null) {
                            graph.addEdge(stepNode, controllerNodeId, "HAS_CONTROLLER");
                        } else {
                            LOGGER.warn(
                                    "Failed to add pathway controller for missing source object '{}' ({}) of type '{}'",
                                    controllerOccurrence.objectName, controllerOccurrence.objectId,
                                    controllerOccurrence.objectType);
                        }
                    }
                }
            }
            if (pathway.pmids != null) {
                for (final String pmidText : StringUtils.split(pathway.pmids, ',')) {
                    final int pmid = Integer.parseInt(pmidText.trim());
                    final long literatureNode = addLiteratureIfNotExists(graph, null, null, pmid, null, null, null);
                    graph.addEdge(stepNode, literatureNode, HAS_OCCURRENCE_LABEL);
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
            if (LITERATURE_LABEL.equalsIgnoreCase(occurrence.sourceType)) {
                if (occurrence.sourceId.startsWith("PMID:")) {
                    final int pmid = Integer.parseInt(StringUtils.split(occurrence.sourceId, ":", 2)[1]);
                    nodeId = addLiteratureIfNotExists(graph, null, occurrence.sourceName, pmid, null, null, null);
                } else if (occurrence.sourceId.startsWith("PMC")) {
                    nodeId = addLiteratureIfNotExists(graph, null, occurrence.sourceName, null, occurrence.sourceId,
                                                      null, null);
                } else
                    nodeId = addWebPageIfNotExists(graph, occurrence.sourceId, occurrence.sourceName);
            } else if (PATHWAY_LABEL.equalsIgnoreCase(occurrence.sourceType))
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
                case PATHWAY_LABEL:
                    long pathwayNodeId = addPathwayIfNotExists(graph, occurrence.objectId, occurrence.objectName);
                    graph.addEdge(pathwayNodeId, nodeId, HAS_OCCURRENCE_LABEL);
                    break;
                default:
                    Long objectNodeId = accessionNodeIdMap.get(occurrence.objectId);
                    if (objectNodeId != null)
                        graph.addEdge(objectNodeId, nodeId, HAS_OCCURRENCE_LABEL);
                    else if (LOGGER.isWarnEnabled())
                        LOGGER.warn("Failed to add occurrence for missing source object '{}' ({}) of type '{}'",
                                    occurrence.objectName, occurrence.objectId, occurrence.objectType);
                    break;
            }
        }
    }

    private long addWebPageIfNotExists(final Graph graph, final String url, final String title) {
        if (webUrlNodeIdMap.containsKey(url))
            return webUrlNodeIdMap.get(url);
        final Node node;
        if (title != null)
            node = graph.addNode(WEB_PAGE_LABEL, "url", url, NAME_PROPERTY, title);
        else
            node = graph.addNode(WEB_PAGE_LABEL, "url", url);
        webUrlNodeIdMap.put(url, node.getId());
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
            if (annotation.geneIds != null) {
                for (String geneId : StringUtils.split(annotation.geneIds, ',')) {
                    final Long geneNodeId = accessionNodeIdMap.get(geneId);
                    if (geneNodeId != null)
                        graph.addEdge(node, accessionNodeIdMap.get(geneId), ASSOCIATED_WITH_LABEL);
                    else {
                        LOGGER.warn(
                                "Failed to add AutomatedAnnotation gene association as no gene with accession '{}' was found",
                                geneId);
                    }
                }
            }
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
            if (annotation.literatureId != null) {
                final Integer pmid = annotation.pmid != null ? Integer.parseInt(annotation.pmid.trim()) : null;
                final long literatureNode = addLiteratureIfNotExists(graph, annotation.literatureId,
                                                                     annotation.literatureTitle, pmid, null, null,
                                                                     annotation.publicationYear);
                graph.addEdge(node, literatureNode, HAS_OCCURRENCE_LABEL);
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
        LOGGER.info("Add {}...", annotations.get(0).getClass().getSimpleName());
        for (final T annotation : annotations) {
            final Node node = graph.addNodeFromModel(annotation);
            variantAnnotationIdNodeIdMap.put(annotation.annotationId, node.getId());
            if (annotation.gene != null) {
                for (final String gene : parseCommaSpaceStringArray(annotation.gene)) {
                    final Node geneNode = graph.findNode(GENE_LABEL, "symbol", gene);
                    if (geneNode != null)
                        graph.addEdge(node, geneNode, ASSOCIATED_WITH_LABEL);
                    else
                        LOGGER.warn("Failed to link variant annotation {} with gene '{}'", annotation.annotationId,
                                    gene);
                }
            }
            if (annotation.drugs != null) {
                final String[] drugs = annotation.drugs.startsWith("\"") ? parseQuotedStringArray(annotation.drugs) :
                                       parseCommaSpaceStringArray(annotation.drugs);
                for (final String drug : drugs) {
                    Node drugNode = graph.findNode(CHEMICAL_LABEL, NAME_PROPERTY, drug);
                    if (drugNode == null) {
                        drugNode = graph.findNode(CHEMICAL_LABEL, NAME_PROPERTY, annotation.drugs);
                        if (drugNode == null) {
                            LOGGER.warn("Failed to link variant annotation {} with drugs '{}'", annotation.annotationId,
                                        annotation.drugs);
                            break;
                        }
                    }
                    graph.addEdge(node, drugNode, ASSOCIATED_WITH_LABEL);
                }
            }
            if (annotation.pmid != null) {
                final int pmid = Integer.parseInt(annotation.pmid.trim());
                final long literatureNode = addLiteratureIfNotExists(graph, null, null, pmid, null, null, null);
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
            accessionNodeIdMap.put(drugLabel.pharmgkbAccessionId, node.getId());
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

    private void addClinicalAnnotationEvidences(final Graph graph, final List<ClinicalAnnotationEvidence> evidences) {
        LOGGER.info("Add ClinicalAnnotationEvidence...");
        for (final ClinicalAnnotationEvidence evidence : evidences) {
            final Node annotationNode = graph.findNode("ClinicalAnnotation", ID_PROPERTY,
                                                       evidence.clinicalAnnotationId);
            if (annotationNode == null) {
                LOGGER.warn("Failed to find clinical annotation node for id '{}'", evidence.clinicalAnnotationId);
                continue;
            }
            Long evidenceNodeId = accessionNodeIdMap.get(evidence.evidenceId);
            if (evidenceNodeId == null) {
                try {
                    final int evidenceId = Integer.parseInt(evidence.evidenceId);
                    evidenceNodeId = variantAnnotationIdNodeIdMap.get(evidenceId);
                } catch (NumberFormatException ignored) {
                }
            }
            if (evidenceNodeId != null) {
                final EdgeBuilder builder = graph.buildEdge().fromNode(annotationNode).toNode(evidenceNodeId).withLabel(
                        "HAS_EVIDENCE");
                builder.withPropertyIfNotNull("evidence_url", evidence.evidenceUrl);
                builder.withPropertyIfNotNull("pmid", evidence.pmid);
                builder.withPropertyIfNotNull("summary", evidence.summary);
                builder.withPropertyIfNotNull("score", evidence.score);
                builder.build();
            } else if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Failed to add clinical annotation evidence for evidence id '{}' and evidence type '{}'",
                            evidence.evidenceId, evidence.evidenceType);
            }
        }
    }
}