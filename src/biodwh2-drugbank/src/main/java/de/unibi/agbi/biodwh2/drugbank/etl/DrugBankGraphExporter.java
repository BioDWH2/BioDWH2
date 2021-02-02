package de.unibi.agbi.biodwh2.drugbank.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;
import de.unibi.agbi.biodwh2.drugbank.model.*;
import de.unibi.agbi.biodwh2.drugbank.model.MetaboliteStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DrugBankGraphExporter extends GraphExporter<DrugBankDataSource> {
    private static class DrugInteractionTriple {
        Long drugNodeId;
        String drugBankIdTarget;
        String description;
    }

    private static class PathwayDrugTriple {
        String smpdIdSource;
        String drugBankIdTarget;
        String description;
    }

    private static class MetaboliteTriple {
        Long reactionsId;
        String rightElementId;
        String leftElementId;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DrugBankGraphExporter.class);
    private static final String DRUGBANK_ID = "drugbank_id";
    private static final String DRUG_INTERACTION_LABEL = "INTERACTS_WITH_DRUG";
    private static final String ORGANISM_LABEL = "Organism";

    public DrugBankGraphExporter(final DrugBankDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph g) {
        g.setNodeIndexPropertyKeys("id", DRUGBANK_ID);
        final Map<String, Long> drugLookUp = new HashMap<>();
        final Map<String, Long> metaboliteLookUp = new HashMap<>();
        final Map<String, Long> pathwayLookUp = new HashMap<>();
        final Map<String, Long> referenceLookUp = new HashMap<>();
        final ArrayList<DrugInteractionTriple> drugInteractionCache = new ArrayList<>();
        final ArrayList<PathwayDrugTriple> pathwayDrugCache = new ArrayList<>();
        final ArrayList<MetaboliteTriple> metaboliteCache = new ArrayList<>();
        exportMetaboliteStructures(g, dataSource.metaboliteStructures, metaboliteLookUp);
        int counter = 1;
        final int totalDrugs = dataSource.drugBankData.drugs.size();
        for (int drugIndex = dataSource.drugBankData.drugs.size() - 1; drugIndex >= 0; drugIndex--) {
            if (counter % 250 == 0 && LOGGER.isInfoEnabled())
                LOGGER.info("Exporting drug " + counter + " of " + totalDrugs);
            counter++;
            Drug drug = dataSource.drugBankData.drugs.get(drugIndex);
            final Node drugNode = createDrugNode(g, drug, drugLookUp);
            addDrugSalts(g, drug, drugNode);
            addDrugExternalIdentifiers(g, drug, drugNode);
            addDrugExternalLinks(g, drug, drugNode);
            createReferenceListNode(g, referenceLookUp, drugNode, drug.generalReferences);
            addDrugSynonyms(g, drug, drugNode);
            addDrugBrands(g, drug, drugNode);
            addDrugMixtures(g, drug, drugNode);
            addDrugSnpEffects(g, drug, drugNode);
            addDrugSnpAdverseDrugReactions(g, drug, drugNode);
            addDrugFoodInteractions(g, drug, drugNode);
            addDrugSequences(g, drug, drugNode);
            addDrugExperimentalProperties(g, drug, drugNode);
            addDrugCalculatedProperties(g, drug, drugNode);
            addDrugAffectedOrganisms(g, drug, drugNode);
            addDrugInteractants(g, drug, drugNode, referenceLookUp);
            addPharmacology(g, drug, drugNode);
            if (drug.classification != null) {
                Node classificationNode = g.addNode("Classification", "description", drug.classification.description,
                                                    "direct_parent", drug.classification.directParent, "kingdom",
                                                    drug.classification.kingdom);
                setPropertyIfNotNull(classificationNode, "superclass", drug.classification.superclass);
                setPropertyIfNotNull(classificationNode, "class", drug.classification.class_);
                setPropertyIfNotNull(classificationNode, "subclass", drug.classification.subclass);
                if (drug.classification.alternativeParents != null)
                    setPropertyIfNotNull(classificationNode, "alternative_parents",
                                         drug.classification.alternativeParents);
                if (drug.classification.substituents != null)
                    setPropertyIfNotNull(classificationNode, "substituents", drug.classification.substituents);
                g.update(classificationNode);
                g.addEdge(drugNode, classificationNode, "CLASSIFIED_AS");
            }
            //Pharmaeconomics -> Manufacturer, Packager, Product, Prices
            Node pharmacoeconomicsNode = g.addNode("Pharmacoeconomics");
            g.addEdge(drugNode, pharmacoeconomicsNode, "HAS_PHARMACOECONOMICS");
            if (drug.products != null) {
                for (final Product product : drug.products) {
                    final NodeBuilder productBuilder = g.buildNode().withLabel("Product");
                    productBuilder.withPropertyIfNotNull("name", product.name);
                    productBuilder.withPropertyIfNotNull("labeller", product.labeller);
                    productBuilder.withPropertyIfNotNull("ndc_id", product.ndcId);
                    productBuilder.withPropertyIfNotNull("ndc_product_code", product.ndcProductCode);
                    productBuilder.withPropertyIfNotNull("dpd_id", product.dpdId);
                    productBuilder.withPropertyIfNotNull("ema_product_code", product.emaProductCode);
                    productBuilder.withPropertyIfNotNull("ema_ma_number", product.emaMaNumber);
                    productBuilder.withPropertyIfNotNull("started_marketing_on", product.startedMarketingOn);
                    productBuilder.withPropertyIfNotNull("ended_marketing_on", product.endedMarketingOn);
                    productBuilder.withPropertyIfNotNull("dosage_form", product.dosageForm);
                    productBuilder.withPropertyIfNotNull("strength", product.strength);
                    productBuilder.withPropertyIfNotNull("route", product.route);
                    productBuilder.withPropertyIfNotNull("fda_application_number", product.fdaApplicationNumber);
                    productBuilder.withPropertyIfNotNull("generic", product.generic);
                    productBuilder.withPropertyIfNotNull("over_the_counter", product.overTheCounter);
                    productBuilder.withPropertyIfNotNull("approved", product.approved);
                    productBuilder.withPropertyIfNotNull("country", product.country.value);
                    productBuilder.withPropertyIfNotNull("source", product.source.value);
                    g.addEdge(pharmacoeconomicsNode, productBuilder.build(), "IS_PRODUCT");
                }
            }
            if (drug.packagers != null) {
                for (final Packager packager : drug.packagers) {
                    final Node node = g.addNode("Packager", "name", packager.name, "url", packager.url);
                    g.addEdge(pharmacoeconomicsNode, node, "HAS_PACKAGER");
                }
            }
            if (drug.manufacturers != null) {
                for (final Manufacturer manufacturer : drug.manufacturers) {
                    final Node node = g.addNode("Manufacturer", "name", manufacturer.value, "url", manufacturer.url);
                    g.addEdge(pharmacoeconomicsNode, node, "HAS_MANUFACTURER");
                }
            }
            if (drug.prices != null) {
                for (final Price price : drug.prices) {
                    final Node node = g.addNode("Price", "description", price.description, "cost", price.cost.value,
                                                "currency", price.cost.currency);
                    setPropertyIfNotNull(node, "unit", price.unit);
                    g.update(node);
                    g.addEdge(pharmacoeconomicsNode, node, "COSTS");
                }
            }
            if (drug.dosages != null) {
                for (final Dosage dosage : drug.dosages) {
                    final Node node = g.addNode("Dosage", "form", dosage.form, "route", dosage.route, "strength",
                                                dosage.strength);
                    g.addEdge(pharmacoeconomicsNode, node, "HAS_DOSAGE");
                }
            }
            if (drug.patents != null) {
                for (final Patent patent : drug.patents) {
                    final Node node = createNodeFromModel(g, patent);
                    g.addEdge(pharmacoeconomicsNode, node, "IS_PATENTED");
                }
            }
            if (drug.categories != null) {
                for (final Category category : drug.categories) {
                    final Node node = g.addNode("Category", "category", category.category, "mesh_id", category.meshId);
                    g.addEdge(drugNode, node, "CATEGORIZED_IN");
                }
            }
            if (drug.drugInteractions != null) {
                for (final DrugInteraction interaction : drug.drugInteractions) {
                    if (drugLookUp.containsKey(interaction.drugbankId.value)) {
                        final Long target = drugLookUp.get(interaction.drugbankId.value);
                        g.addEdge(drugNode, target, DRUG_INTERACTION_LABEL, "description", interaction.description);
                    } else {
                        final DrugInteractionTriple triple = new DrugInteractionTriple();
                        triple.drugNodeId = drugNode.getId();
                        triple.drugBankIdTarget = interaction.drugbankId.value;
                        triple.description = interaction.description;
                        drugInteractionCache.add(triple);
                    }
                }
            }
            //Pathways->Pathway-Enzymes
            if (drug.pathways != null) {
                for (final Pathway pathway : drug.pathways) {
                    PathwayDrugTriple triple = new PathwayDrugTriple();
                    triple.smpdIdSource = pathway.smpdbId;
                    // TODO
                    System.out.println(
                            drug.drugbankIds.stream().map(d -> d.value).collect(Collectors.joining(";")) + " -> " +
                            pathway.drugs.stream().map(d -> d.drugbankId.value).collect(Collectors.joining()));
                    //triple.drugBankIdTarget = drugs.get(i).drugbankIds.get(0).value;
                    triple.description = pathway.name;
                    pathwayDrugCache.add(triple);
                    if (!pathwayLookUp.containsKey(pathway.smpdbId)) {
                        final Node pathwaysNode = g.addNode("Pathway", "name", pathway.name, "smpdbId", pathway.smpdbId,
                                                            "category", pathway.category);
                        setPropertyIfNotNull(pathwaysNode, "enzymes", pathway.enzymes);
                        g.update(pathwaysNode);
                        pathwayLookUp.put(pathway.smpdbId, pathwaysNode.getId());
                    }
                    g.addEdge(drugNode, pathwayLookUp.get(pathway.smpdbId), "IS_IN_PATHWAY");
                }
            }
            //Reactions: Drug -> Reaction -> Reaction-Enzyme -> Metabolite
            if (drug.reactions != null) {
                for (final Reaction reaction : drug.reactions) {
                    final Node reactionsNode;
                    if (reaction.sequence != null)
                        reactionsNode = g.addNode("Reaction", "sequence", reaction.sequence);
                    else
                        reactionsNode = g.addNode("Reaction");
                    MetaboliteTriple triple = new MetaboliteTriple();
                    triple.reactionsId = reactionsNode.getId();
                    triple.rightElementId = reaction.rightElement.drugbankId;
                    triple.leftElementId = reaction.leftElement.drugbankId;
                    metaboliteCache.add(triple);
                    if (reaction.enzymes != null) {
                        for (final ReactionEnzyme enzyme : reaction.enzymes) {
                            Node reactionEnzymeNode = g.addNode("ReactionEnzyme");
                            setPropertyIfNotNull(reactionEnzymeNode, DRUGBANK_ID, enzyme.drugbankId);
                            setPropertyIfNotNull(reactionEnzymeNode, "name", enzyme.name);
                            setPropertyIfNotNull(reactionEnzymeNode, "uniprot_id", enzyme.uniprotId);
                            g.update(reactionEnzymeNode);
                            g.addEdge(reactionEnzymeNode, reactionsNode, "IS_INFERRED_TO");
                        }
                    }
                }
            }
            dataSource.drugBankData.drugs.remove(drugIndex);
        }
        for (final DrugInteractionTriple triple : drugInteractionCache) {
            if (drugLookUp.containsKey(triple.drugBankIdTarget)) {
                Long target = drugLookUp.get(triple.drugBankIdTarget);
                g.addEdge(triple.drugNodeId, target, DRUG_INTERACTION_LABEL, "description", triple.description);
            } else {
                //Log-Message if key missing!
                //System.out.println("DrugInteraction-Key is missing! ID: " + triple.drugBankIdTarget);
            }
        }
        for (PathwayDrugTriple triple : pathwayDrugCache) {
            if (pathwayLookUp.containsKey(triple.drugBankIdTarget)) {
                Long sourceId = pathwayLookUp.get(triple.smpdIdSource);
                Long target = drugLookUp.get(triple.drugBankIdTarget);
                Edge pathwayDrugEdge = g.addEdge(sourceId, target, "IS_PATHWAYDRUG");
                pathwayDrugEdge.setProperty("name", triple.description);
                g.update(pathwayDrugEdge);
            } else {
                //Log-Message if key missing!
                //System.out.println("Pathway-Key is missing! ID: " + triple.drugBankIdTarget);
            }
        }
        for (MetaboliteTriple triple : metaboliteCache) {
            if (metaboliteLookUp.containsKey(triple.leftElementId)) {
                Long source = metaboliteLookUp.get(triple.leftElementId);
                g.addEdge(source, triple.reactionsId, "SUBSTRATE_IN");
            } else if (drugLookUp.containsKey(triple.leftElementId)) {
                Long source = drugLookUp.get(triple.leftElementId);
                g.addEdge(source, triple.reactionsId, "SUBSTRATE_IN");
            } else {
                if (triple.leftElementId.startsWith("DBMET")) {
                    Node metaboliteNode = g.addNode("Metabolite", DRUGBANK_ID, triple.leftElementId);
                    metaboliteLookUp.put(triple.leftElementId, metaboliteNode.getId());
                }
            }
            if (drugLookUp.containsKey(triple.rightElementId)) {
                Long target = drugLookUp.get(triple.rightElementId);
                g.addEdge(triple.reactionsId, target, "METABOLIZED_TO");
            } else if (metaboliteLookUp.containsKey(triple.rightElementId)) {
                Long target = metaboliteLookUp.get(triple.rightElementId);
                g.addEdge(triple.reactionsId, target, "METABOLIZED_TO");
            } else {
                if (triple.rightElementId.startsWith("DBMET")) {
                    Node metaboliteNode = g.addNode("Metabolite", DRUGBANK_ID, triple.rightElementId);
                    metaboliteLookUp.put(triple.rightElementId, metaboliteNode.getId());
                }
            }
        }
        return true;
    }

    private Node createDrugNode(final Graph graph, final Drug drug, final Map<String, Long> drugLookUp) {
        final NodeBuilder drugBuilder = graph.buildNode().withLabel("Drug");
        drugBuilder.withProperty(DRUGBANK_ID, getPrimaryOrFirstDrugBankId(drug).value);
        drugBuilder.withProperty("name", drug.name);
        drugBuilder.withProperty("description", drug.description);
        drugBuilder.withProperty("group", drug.groups.stream().map(Group::toValue).toArray(String[]::new));
        drugBuilder.withPropertyIfNotNull("ahfs_codes", drug.ahfsCodes);
        drugBuilder.withPropertyIfNotNull("pdb_entries", drug.pdbEntries);
        drugBuilder.withPropertyIfNotNull("fda_labels", drug.fdaLabel);
        drugBuilder.withPropertyIfNotNull("msds", drug.msds);
        if (drug.atcCodes != null) {
            final List<String> atcCodes = new ArrayList<>();
            for (final AtcCode code : drug.atcCodes)
                atcCodes.add(code.code);
            drugBuilder.withProperty("atc_code", atcCodes.toArray(new String[0]));
        }
        drugBuilder.withPropertyIfNotNull("cas_number", drug.casNumber);
        drugBuilder.withPropertyIfNotNull("unii", drug.unii);
        drugBuilder.withPropertyIfNotNull("average_mass", drug.averageMass);
        drugBuilder.withPropertyIfNotNull("monoisotopic_mass", drug.monoisotopicMass);
        if (drug.state != null)
            drugBuilder.withPropertyIfNotNull("state", drug.state.value);
        drugBuilder.withPropertyIfNotNull("synthesis_reference", drug.synthesisReference);
        final Node drugNode = drugBuilder.build();
        for (final DrugbankDrugSaltId id : drug.drugbankIds)
            drugLookUp.put(id.value, drugNode.getId());
        return drugNode;
    }

    private DrugbankDrugSaltId getPrimaryOrFirstDrugBankId(final Drug drug) {
        for (final DrugbankDrugSaltId id : drug.drugbankIds)
            if (id.primary)
                return id;
        return drug.drugbankIds.size() > 0 ? drug.drugbankIds.get(0) : null;
    }

    private void addDrugSalts(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.salts != null)
            for (final Salt salt : drug.salts)
                addDrugSalt(graph, salt, drugNode);
    }

    private void addDrugSalt(final Graph graph, final Salt salt, final Node drugNode) {
        final NodeBuilder builder = graph.buildNode().withLabel("Salt");
        builder.withProperty(DRUGBANK_ID, salt.drugbankId.value);
        builder.withPropertyIfNotNull("name", salt.name);
        builder.withPropertyIfNotNull("unii", salt.unii);
        builder.withPropertyIfNotNull("cas_number", salt.casNumber);
        builder.withPropertyIfNotNull("inchi_key", salt.inchikey);
        builder.withPropertyIfNotNull("average_mass", salt.averageMass);
        builder.withPropertyIfNotNull("monoisotopic_mass", salt.monoisotopicMass);
        final Node saltNode = builder.build();
        graph.addEdge(drugNode, saltNode, "HAS_SALT");
    }

    private void addDrugExternalIdentifiers(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.externalIdentifiers != null)
            for (final ExternalIdentifier identifier : drug.externalIdentifiers) {
                final Node node = graph.addNode("ExternalIdentifier", "resource", identifier.resource.value,
                                                "identifier", identifier.identifier);
                graph.addEdge(drugNode, node, "HAS_EXTERNAL_IDENTIFIER");
            }
    }

    private void addDrugExternalLinks(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.externalLinks != null)
            for (final ExternalLink link : drug.externalLinks) {
                final Node node = graph.addNode("ExternalLink", "url", link.url, "resource", link.resource.value);
                graph.addEdge(drugNode, node, "HAS_EXTERNAL_LINK");
            }
    }

    private void addDrugSynonyms(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.synonyms != null)
            for (final Synonym synonym : drug.synonyms) {
                final Node node = createNodeFromModel(graph, synonym);
                graph.addEdge(drugNode, node, "HAS_SYNONYM");
            }
    }

    private void addDrugBrands(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.internationalBrands != null)
            for (final InternationalBrand brand : drug.internationalBrands) {
                final Node node = createNodeFromModel(graph, brand);
                graph.addEdge(drugNode, node, "HAS_BRAND");
            }
    }

    private void addDrugMixtures(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.mixtures != null)
            for (final Mixture mixture : drug.mixtures) {
                final Node node = createNodeFromModel(graph, mixture);
                graph.addEdge(drugNode, node, "IS_IN_MIXTURE");
            }
    }

    private void addDrugSnpEffects(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.snpEffects != null)
            for (final SnpEffect snpEffect : drug.snpEffects) {
                final Node node = createNodeFromModel(graph, snpEffect);
                graph.addEdge(drugNode, node, "HAS_SNP_EFFECT");
            }
    }

    private void addDrugSnpAdverseDrugReactions(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.snpAdverseDrugReactions != null)
            for (final SnpAdverseDrugReaction snpAdverseDrugReaction : drug.snpAdverseDrugReactions) {
                final Node node = createNodeFromModel(graph, snpAdverseDrugReaction);
                graph.addEdge(drugNode, node, "HAS_SNP_ADVERSE_DRUG_REACTION");
            }
    }

    private void addDrugFoodInteractions(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.foodInteractions != null)
            for (final String interaction : drug.foodInteractions) {
                final Node node = graph.addNode("FoodInteraction", "description", interaction);
                graph.addEdge(drugNode, node, "HAS_FOOD_INTERACTION");
            }
    }

    private void addDrugSequences(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.sequences != null)
            for (final Sequence sequence : drug.sequences) {
                final Node node = graph.addNode("Sequence", "value", sequence.value, "format", sequence.format);
                graph.addEdge(drugNode, node, "HAS_SEQUENCE");
            }
    }

    private void addDrugExperimentalProperties(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.experimentalProperties != null)
            for (final ExperimentalProperty property : drug.experimentalProperties) {
                final Node node = graph.addNode("ExperimentalProperty", "value", property.value, "kind",
                                                property.kind.value, "source", property.source);
                graph.addEdge(drugNode, node, "HAS_EXPERIMENTAL_PROPERTY");
            }
    }

    private void addDrugCalculatedProperties(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.calculatedProperties != null)
            for (final CalculatedProperty property : drug.calculatedProperties) {
                final Node node = graph.addNode("CalculatedProperty", "value", property.value, "kind",
                                                property.kind.value, "source", property.source.value);
                graph.addEdge(drugNode, node, "HAS_CALCULATED_PROPERTY");
            }
    }

    private void addDrugAffectedOrganisms(final Graph graph, final Drug drug, final Node drugNode) {
        if (drug.affectedOrganisms != null)
            for (final String organism : drug.affectedOrganisms) {
                final Long affectedOrganismNodeId = updateOrCreateOrganism(graph, organism, null);
                graph.addEdge(drugNode, affectedOrganismNodeId, "AFFECTS");
            }
    }

    private Long updateOrCreateOrganism(final Graph graph, final String name, final String ncbiTaxonomyId) {
        Node foundNode = null;
        if (ncbiTaxonomyId != null)
            foundNode = graph.findNode(ORGANISM_LABEL, "id", ncbiTaxonomyId);
        if (foundNode == null && name != null)
            foundNode = graph.findNode(ORGANISM_LABEL, "name", name);
        if (foundNode == null) {
            if (name != null && ncbiTaxonomyId != null)
                foundNode = graph.addNode(ORGANISM_LABEL, "id", ncbiTaxonomyId, "name", name);
            else if (name != null)
                foundNode = graph.addNode(ORGANISM_LABEL, "name", name);
            else
                foundNode = graph.addNode(ORGANISM_LABEL, "id", ncbiTaxonomyId);
        } else {
            boolean changed = false;
            if (foundNode.getProperty("id") == null && ncbiTaxonomyId != null) {
                foundNode.setProperty("id", ncbiTaxonomyId);
                changed = true;
            }
            if (foundNode.getProperty("name") == null && name != null) {
                foundNode.setProperty("name", name);
                changed = true;
            }
            if (changed)
                graph.update(foundNode);
        }
        return foundNode.getId();
    }

    private void addDrugInteractants(final Graph graph, final Drug drug, final Node drugNode,
                                     final Map<String, Long> referenceLookUp) {
        if (drug.targets != null)
            for (final Target target : drug.targets) {
                final Node node = getOrCreateInteractantNode(graph, target, "Target");
                connectInteractant(graph, drugNode, target, node, referenceLookUp);
            }
        if (drug.enzymes != null)
            for (final Enzyme enzyme : drug.enzymes) {
                final Node node = getOrCreateInteractantNode(graph, enzyme, "Enzyme");
                connectInteractant(graph, drugNode, enzyme, node, referenceLookUp);
            }
        if (drug.carriers != null)
            for (final Carrier carrier : drug.carriers) {
                final Node node = getOrCreateInteractantNode(graph, carrier, "Carrier");
                connectInteractant(graph, drugNode, carrier, node, referenceLookUp);
            }
        if (drug.transporters != null)
            for (final Transporter transporter : drug.transporters) {
                final Node node = getOrCreateInteractantNode(graph, transporter, "Transporter");
                connectInteractant(graph, drugNode, transporter, node, referenceLookUp);
            }
    }

    private Node getOrCreateInteractantNode(final Graph graph, final Interactant interactant, final String label) {
        Node node = graph.findNode(label, "id", interactant.id);
        if (node == null) {
            if (interactant.position != null) {
                node = graph.addNode(label, "id", interactant.id, "name", interactant.name, "position",
                                     interactant.position);
            } else {
                node = graph.addNode(label, "id", interactant.id, "name", interactant.name);
            }
            if (interactant.polypeptide != null) {
                Node polypeptideNode = graph.findNode("Polypeptide", "id", interactant.polypeptide.id);
                if (polypeptideNode == null)
                    polypeptideNode = createPolypeptideNode(graph, interactant.polypeptide);
                graph.addEdge(node, polypeptideNode, "IS_POLYPEPTIDE");
            }
            if (interactant.organism != null) {
                final Long organismNodeId = updateOrCreateOrganism(graph, interactant.organism, null);
                graph.addEdge(node, organismNodeId, "HAS_ORGANISM");
            }
        }
        return node;
    }

    private Node createPolypeptideNode(final Graph graph, final Polypeptide polypeptide) {
        final NodeBuilder builder = graph.buildNode().withLabel("Polypeptide");
        builder.withPropertyIfNotNull("id", polypeptide.id);
        builder.withPropertyIfNotNull("name", polypeptide.name);
        builder.withPropertyIfNotNull("source", polypeptide.source);
        builder.withPropertyIfNotNull("general_function", polypeptide.generalFunction);
        builder.withPropertyIfNotNull("specific_function", polypeptide.specificFunction);
        builder.withPropertyIfNotNull("gene_name", polypeptide.geneName);
        builder.withPropertyIfNotNull("locus", polypeptide.locus);
        builder.withPropertyIfNotNull("cellular_location", polypeptide.cellularLocation);
        builder.withPropertyIfNotNull("transmembrane_regions", polypeptide.transmembraneRegions);
        builder.withPropertyIfNotNull("signal_regions", polypeptide.signalRegions);
        builder.withPropertyIfNotNull("theoretical_pi", polypeptide.theoreticalPi);
        builder.withPropertyIfNotNull("molecular_weight", polypeptide.molecularWeight);
        builder.withPropertyIfNotNull("chromosome_location", polypeptide.chromosomeLocation);
        builder.withPropertyIfNotNull("aminoacid_sequence", polypeptide.aminoAcidSequence.value);
        builder.withPropertyIfNotNull("aminoacid_sequence_format", polypeptide.aminoAcidSequence.format);
        builder.withPropertyIfNotNull("gene_sequence", polypeptide.geneSequence.value);
        builder.withPropertyIfNotNull("gene_sequence_format", polypeptide.geneSequence.format);
        builder.withPropertyIfNotNull("synonyms", polypeptide.synonyms);
        final Node polypeptideNode = builder.build();
        if (polypeptide.externalIdentifiers != null) {
            for (final PolypeptideExternalIdentifier identifier : polypeptide.externalIdentifiers) {
                final Node node = graph.addNode("PolypeptideExternalIdentifier", "resource", identifier.resource.value,
                                                "identifier", identifier.identifier);
                graph.addEdge(polypeptideNode, node, "HAS_POLYPEPTIDE_EXTERNAL_IDENTIFIER");
            }
        }
        if (polypeptide.pfams != null) {
            for (final Pfam pfam : polypeptide.pfams) {
                final Node node = graph.addNode("Pfam", "identifier", pfam.identifier, "name", pfam.name);
                graph.addEdge(polypeptideNode, node, "HAS_PFAM");
            }
        }
        if (polypeptide.goClassifiers != null) {
            for (final GoClassifier classifier : polypeptide.goClassifiers) {
                final Node node = graph.addNode("GOClassifier", "category", classifier.category, "description",
                                                classifier.description);
                graph.addEdge(polypeptideNode, node, "HAS_GO_CLASSIFIER");
            }
        }
        if (polypeptide.organism != null) {
            final Long organismNodeId = updateOrCreateOrganism(graph, polypeptide.organism.value,
                                                               polypeptide.organism.ncbiTaxonomyId);
            graph.addEdge(polypeptideNode, organismNodeId, "HAS_ORGANISM");
        }
        return polypeptideNode;
    }

    private void connectInteractant(final Graph graph, final Node drugNode, final Interactant interactant,
                                    final Node interactantNode, final Map<String, Long> referenceLookUp) {
        final NodeBuilder builder = graph.buildNode().withLabel("TargetMetadata");
        builder.withPropertyIfNotNull("known_action", interactant.knownAction.value);
        builder.withPropertyIfNotNull("actions", interactant.actions);
        if (interactant instanceof Enzyme) {
            builder.withPropertyIfNotNull("inhibition_strength", ((Enzyme) interactant).inhibitionStrength);
            builder.withPropertyIfNotNull("induction_strength", ((Enzyme) interactant).inductionStrength);
        }
        final Node metadataNode = builder.build();
        createReferenceListNode(graph, referenceLookUp, metadataNode, interactant.references);
        graph.addEdge(drugNode, metadataNode, "TARGETS");
        graph.addEdge(metadataNode, interactantNode, "HAS_TARGET");
    }

    private void addPharmacology(final Graph graph, final Drug drug, final Node drugNode) {
        final NodeBuilder builder = graph.buildNode().withLabel("Pharmacology");
        builder.withPropertyIfNotNull("mechanism_of_action", drug.mechanismOfAction);
        builder.withPropertyIfNotNull("toxicity", drug.toxicity);
        builder.withPropertyIfNotNull("metabolism", drug.metabolism);
        builder.withPropertyIfNotNull("absorption", drug.absorption);
        builder.withPropertyIfNotNull("indication", drug.indication);
        builder.withPropertyIfNotNull("pharmacodynamics", drug.pharmacodynamics);
        builder.withPropertyIfNotNull("half_life", drug.halfLife);
        builder.withPropertyIfNotNull("protein_binding", drug.proteinBinding);
        builder.withPropertyIfNotNull("route_of_elimination", drug.routeOfElimination);
        builder.withPropertyIfNotNull("volume_of_distribution", drug.volumeOfDistribution);
        builder.withPropertyIfNotNull("clearance", drug.clearance);
        graph.addEdge(drugNode, builder.build(), "HAS_PHARMACOLOGY");
    }

    private void createReferenceListNode(final Graph g, final Map<String, Long> referenceLookUp, final Node parent,
                                         final ReferenceList references) {
        if (references == null)
            return;
        if (isListNullOrEmpty(references.textbooks) && isListNullOrEmpty(references.articles) && isListNullOrEmpty(
                references.attachments) && isListNullOrEmpty(references.links))
            return;
        final Node referenceListNode = g.addNode("ReferenceList");
        g.addEdge(parent, referenceListNode, "HAS_REFERENCES");
        if (isListNotEmpty(references.textbooks)) {
            for (final Textbook reference : references.textbooks) {
                if (!referenceLookUp.containsKey(reference.refId)) {
                    final Node node = createNodeFromModel(g, reference);
                    referenceLookUp.put(reference.refId, node.getId());
                }
                g.addEdge(referenceListNode, referenceLookUp.get(reference.refId), "HAS_TEXTBOOK");
            }
        }
        if (isListNotEmpty(references.articles)) {
            for (final Article reference : references.articles) {
                if (!referenceLookUp.containsKey(reference.refId)) {
                    final Node node = createNodeFromModel(g, reference);
                    referenceLookUp.put(reference.refId, node.getId());
                }
                g.addEdge(referenceListNode, referenceLookUp.get(reference.refId), "HAS_ARTICLE");
            }
        }
        if (isListNotEmpty(references.links)) {
            for (final Link reference : references.links) {
                if (!referenceLookUp.containsKey(reference.refId)) {
                    final Node node = createNodeFromModel(g, reference);
                    referenceLookUp.put(reference.refId, node.getId());
                }
                g.addEdge(referenceListNode, referenceLookUp.get(reference.refId), "HAS_LINK");
            }
        }
        if (isListNotEmpty(references.attachments)) {
            for (final Attachment reference : references.attachments) {
                if (!referenceLookUp.containsKey(reference.refId)) {
                    final Node node = createNodeFromModel(g, reference);
                    referenceLookUp.put(reference.refId, node.getId());
                }
                g.addEdge(referenceListNode, referenceLookUp.get(reference.refId), "HAS_ATTACHMENT");
            }
        }
    }

    private boolean isListNullOrEmpty(final List<?> list) {
        return list == null || list.size() == 0;
    }

    private boolean isListNotEmpty(final List<?> list) {
        return list != null && list.size() > 0;
    }

    public void setPropertyIfNotNull(final Node node, final String propertyKey, final Object value) {
        if (value != null)
            node.setProperty(propertyKey, value);
    }

    private void exportMetaboliteStructures(final Graph graph, final List<MetaboliteStructure> metabolites,
                                            final Map<String, Long> metaboliteLookUp) {
        for (MetaboliteStructure metabolite : metabolites)
            exportMetaboliteStructure(graph, metabolite, metaboliteLookUp);
    }

    private void exportMetaboliteStructure(final Graph graph, final MetaboliteStructure metabolite,
                                           final Map<String, Long> metaboliteLookUp) {
        final Node metaboliteNode = createNodeFromModel(graph, metabolite);
        metaboliteLookUp.put(metabolite.drugbankId, metaboliteNode.getId());
    }
}
