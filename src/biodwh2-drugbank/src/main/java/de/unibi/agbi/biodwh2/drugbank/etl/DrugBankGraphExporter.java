package de.unibi.agbi.biodwh2.drugbank.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;
import de.unibi.agbi.biodwh2.drugbank.model.*;
import de.unibi.agbi.biodwh2.drugbank.model.DrugStructure;
import de.unibi.agbi.biodwh2.drugbank.model.MetaboliteStructure;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class DrugBankGraphExporter extends GraphExporter<DrugBankDataSource> {
    private class DrugInteractionTriple {
        String drugBankIdSource;
        String drugBankIdTarget;
        String description;
    }

    private class PathwayDrugTriple {
        String smpdIdSource;
        String drugBankIdTarget;
        String description;
    }

    public DrugBankGraphExporter(final DrugBankDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph g) {
        g.setNodeIndexPropertyKeys("id");
        exportDrugStructures(g, dataSource.drugStructures);
        exportMetaboliteStructures(g, dataSource.metaboliteStructures);
        Hashtable<String, Node> drug_lookUp = new Hashtable<>();
        Hashtable<String, Node> pathway_lookUp = new Hashtable<>();
        Hashtable<String, Node> organism_lookUp = new Hashtable<>();
        Hashtable<Object, Node> enzyme_lookUp = new Hashtable<>();
        Hashtable<Object, Node> carriers_lookUp = new Hashtable<>();
        Hashtable<Object, Node> transporters_lookUp = new Hashtable<>();
        Hashtable<Object, Node> referenceList_lookUp = new Hashtable<>();
        Hashtable<Object, Node> reactionElement_lookUp = new Hashtable<>();
        Hashtable<String, Node> polypeptide_lookUp = new Hashtable<>();
        ArrayList<DrugInteractionTriple> drugInteractionCache = new ArrayList<>();
        ArrayList<PathwayDrugTriple> pathwayDrugCache = new ArrayList<>();
        ArrayList<Drug> drugs = ((DrugBankDataSource) dataSource).drugBankData.drugs;
        for (int drugIndex = drugs.size() - 1; drugIndex >= 0; drugIndex--) {
            Drug drug = drugs.get(drugIndex);
            Node drugNode = createNode("Drug");
            drug_lookUp.put(drug.drugbankIds.get(0).value, drugNode);
            for (int i = 0; i < drug.drugbankIds.size(); i++) {
                if (drug.drugbankIds.get(i).primary == true) {
                    drugNode.setProperty("drugbank_id", drug.drugbankIds.get(i).value);
                }
            }
            drugNode.setProperty("name", drug.name);
            drugNode.setProperty("description", drug.description);
            drugNode.setProperty("group", drug.groups.toString());
            //Chemical Property
            Node chemicalPropertyNode = createNode("Chemical Property");
            chemicalPropertyNode.setProperty("cas-number", drug.casNumber);
            chemicalPropertyNode.setProperty("unii", drug.unii);
            chemicalPropertyNode.setProperty("average-mass", drug.averageMass);
            chemicalPropertyNode.setProperty("monoisotopic-mass", drug.monoisotopicMass);
            chemicalPropertyNode.setProperty("state", drug.state);
            chemicalPropertyNode.setProperty("synthesis-reference", drug.synthesisReference);
            g.addNode(chemicalPropertyNode);
            g.addEdge(new Edge(drugNode, chemicalPropertyNode, "HAS_CHEMICAL_PROPERTY"));
            //Pharmacology
            Node pharmacologyNode = createNode("Pharmacology");
            pharmacologyNode.setProperty("mechanism-of-action", drug.mechanismOfAction);
            pharmacologyNode.setProperty("toxicity", drug.toxicity);
            pharmacologyNode.setProperty("metabolism", drug.metabolism);
            pharmacologyNode.setProperty("absorption", drug.absorption);
            pharmacologyNode.setProperty("indication", drug.indication);
            pharmacologyNode.setProperty("pharmacodynamics", drug.pharmacodynamics);
            pharmacologyNode.setProperty("half-life", drug.halfLife);
            pharmacologyNode.setProperty("protein-binding", drug.proteinBinding);
            pharmacologyNode.setProperty("route-of-elimination", drug.routeOfElimination);
            pharmacologyNode.setProperty("volume-of-distribution", drug.volumeOfDistribution);
            pharmacologyNode.setProperty("clearance", drug.clearance);
            g.addNode(pharmacologyNode);
            g.addEdge(new Edge(drugNode, pharmacologyNode, "HAS_PHARMACOLOGY"));

            if (drug.atcCodes != null) {
                ArrayList<String> atcCodes = new ArrayList<>();
                for (int j = 0; j < drug.atcCodes.size(); j++) {
                    atcCodes.add(drug.atcCodes.get(j).code);
                }
                drugNode.setProperty("atc-code", atcCodes.toArray(new String[atcCodes.size()]));
            }

            drugNode.setProperty("ahfs-code", drug.ahfsCodes);
            drugNode.setProperty("pdb-entries", drug.pdbEntries);
            drugNode.setProperty("fda-label", drug.fdaLabel);
            drugNode.setProperty("msds", drug.msds);
            if (drug.externalIdentifiers != null) {
                for (int j = 0; j < drug.externalIdentifiers.size(); j++) {
                    Node externalIdentifierNode = createNode("External Identifier");
                    externalIdentifierNode.setProperty("resource", drug.externalIdentifiers.get(j).resource);
                    externalIdentifierNode.setProperty("identifier", drug.externalIdentifiers.get(j).identifier);
                    g.addNode(externalIdentifierNode);
                    g.addEdge(new Edge(drugNode, externalIdentifierNode, "HAS_EXTERNAL_IDENTIFIER"));
                }
            }
            g.addNode(drugNode);

            //ReferenceList -> Article, Attachement, Link, Textbook
            if (drug.generalReferences != null) {
                createReferenceListNode(g, referenceList_lookUp, drugNode, drug.generalReferences);
            }

            //Classification
            if(drug.classification != null){
                Node classificationNode = createNode("Classification");
                classificationNode.setProperty("description", drug.classification.description);
                classificationNode.setProperty("direct-parent", drug.classification.directParent);
                classificationNode.setProperty("kingdom", drug.classification.kingdom);
                classificationNode.setProperty("superclass", drug.classification.superclass);
                classificationNode.setProperty("class", drug.classification.class_);
                classificationNode.setProperty("subclass", drug.classification.subclass);
                if(drug.classification.alternativeParents != null) {
                    String alternativeParents = String.join("; ", drug.classification.alternativeParents);
                    classificationNode.setProperty("alternative-parents", alternativeParents);
                }
                if(drug.classification.substituents != null) {
                    String substituents = String.join("; ", drug.classification.substituents);
                    classificationNode.setProperty("substituents", substituents);
                }
                g.addNode(classificationNode);
                g.addEdge(new Edge(drugNode, classificationNode, "CLASSIFIED_AS"));
            }

            //External Links
            if (drug.externalLinks != null) {
                for (int i = 0; i < drug.externalLinks.size(); i++) {
                    Node externalLinkNode = createNode("External Link");
                    externalLinkNode.setProperty("external-links", drug.externalLinks.get(i).url);
                    externalLinkNode.setProperty("external-links-resource", drug.externalLinks.get(i).resource.toString());
                    g.addNode(externalLinkNode);
                    g.addEdge(new Edge(drugNode, externalLinkNode, "HAS_EXTERNAL_LINKS"));
                }
            }

            //Salts
            if (drug.salts != null) {
                for (int i = 0; i < drug.salts.size(); i++) {
                    Node saltNode = createNode("Salt");
                    saltNode.setProperty("drugbank_id", drug.salts.get(i).drugbankId.value);
                    saltNode.setProperty("name", drug.salts.get(i).name);
                    saltNode.setProperty("unii", drug.salts.get(i).unii);
                    saltNode.setProperty("cas-number", drug.salts.get(i).casNumber);
                    saltNode.setProperty("inchikey", drug.salts.get(i).inchikey);
                    saltNode.setProperty("average-mass", drug.salts.get(i).averageMass);
                    saltNode.setProperty("monoisotopic-mass", drug.salts.get(i).monoisotopicMass);
                    g.addNode(saltNode);
                    g.addEdge(new Edge(drugNode, saltNode, "HAS_SALT"));
                }
            }

            //Synonyms
            if (drug.synonyms != null) {
                for (int i = 0; i < drug.synonyms.size(); i++) {
                    Node synonymNode = createNode("Synonym");
                    synonymNode.setProperty("synonym", drug.synonyms.get(i).value);
                    synonymNode.setProperty("language", drug.synonyms.get(i).language);
                    synonymNode.setProperty("coder", drug.synonyms.get(i).coder);
                    g.addNode(synonymNode);
                    g.addEdge(new Edge(drugNode, synonymNode, "HAS_SYNONYM"));
                }
            }

            //International Brands
            if (drug.internationalBrands != null) {
                for (int i = 0; i < drug.internationalBrands.size(); i++) {
                    Node internationalBrandsNode = createNode("International Brand");
                    internationalBrandsNode.setProperty("name", drug.internationalBrands.get(i).name);
                    internationalBrandsNode.setProperty("company", drug.internationalBrands.get(i).company);
                    g.addNode(internationalBrandsNode);
                    g.addEdge(new Edge(drugNode, internationalBrandsNode, "HAS_BRAND"));
                }
            }

            //Mixtures
            if (drug.mixtures != null) {
                for (int i = 0; i < drug.mixtures.size(); i++) {
                    Node mixtureNode = createNode("Mixture");
                    mixtureNode.setProperty("name", drug.mixtures.get(i).name);
                    mixtureNode.setProperty("ingredients", drug.mixtures.get(i).ingredients);
                    g.addNode(mixtureNode);
                    g.addEdge(new Edge(drugNode, mixtureNode, "IS_IN_MIXTURE"));
                }
            }

            //Pharmaeconomics -> Manufacturer, Packager, Product, Prices
            Node pharmacoeconomicsNode = createNode("Pharmacoeconomics");
            g.addNode(pharmacoeconomicsNode);
            g.addEdge(new Edge(drugNode, pharmacoeconomicsNode, "HAS_PHARMACOECONOMICS"));

            //Product
            if (drug.products != null) {
                for (int i = 0; i < drug.products.size(); i++) {
                    Node productNode = createNode("Product");
                    productNode.setProperty("name", drug.products.get(i).name);
                    productNode.setProperty("labeller", drug.products.get(i).labeller);
                    productNode.setProperty("ndc-id", drug.products.get(i).ndcId);
                    productNode.setProperty("ndc-product-code", drug.products.get(i).ndcProductCode);
                    productNode.setProperty("dpd-id", drug.products.get(i).dpdId);
                    productNode.setProperty("ema-product-code", drug.products.get(i).emaProductCode);
                    productNode.setProperty("ema-ma-number", drug.products.get(i).emaMaNumber);
                    productNode.setProperty("started-marketing-on", drug.products.get(i).startedMarketingOn);
                    productNode.setProperty("ended-marketing-on", drug.products.get(i).endedMarketingOn);
                    productNode.setProperty("dosage-form", drug.products.get(i).dosageForm);
                    productNode.setProperty("strength", drug.products.get(i).strength);
                    productNode.setProperty("route", drug.products.get(i).route);
                    productNode.setProperty("fda-application-number", drug.products.get(i).fdaApplicationNumber);
                    productNode.setProperty("generic", drug.products.get(i).generic);
                    productNode.setProperty("over-the-counter", drug.products.get(i).overTheCounter);
                    productNode.setProperty("approved", drug.products.get(i).approved);
                    productNode.setProperty("country", drug.products.get(i).country);
                    productNode.setProperty("source", drug.products.get(i).source);
                    g.addNode(productNode);
                    g.addEdge(new Edge(pharmacoeconomicsNode, productNode, "IS_PRODUCT"));
                }
            }

            //Packagers
            if (drug.packagers != null) {
                for (int i = 0; i < drug.packagers.size(); i++) {
                    Node packagersNode = createNode("Packager");
                    packagersNode.setProperty("name", drug.packagers.get(i).name);
                    packagersNode.setProperty("url", drug.packagers.get(i).url);
                    g.addNode(packagersNode);
                    g.addEdge(new Edge(pharmacoeconomicsNode, packagersNode, "HAS_PACKAGER"));
                }
            }

            //Manufacturers
            if (drug.manufacturers != null) {
                for (int i = 0; i < drug.manufacturers.size(); i++) {
                    Node manufacturersNode = createNode("Manufacturer");
                    manufacturersNode.setProperty("name", drug.manufacturers.get(i).value);
                    manufacturersNode.setProperty("url", drug.manufacturers.get(i).url);
                    g.addNode(manufacturersNode);
                    g.addEdge(new Edge(pharmacoeconomicsNode, manufacturersNode, "HAS_MANUFACTURER"));
                }
            }

            //Prices
            if (drug.prices != null) {
                for (int i = 0; i < drug.prices.size(); i++) {
                    Node pricesNode = createNode("Price");
                    pricesNode.setProperty("description", drug.prices.get(i).description);
                    pricesNode.setProperty("cost", drug.prices.get(i).cost.value);
                    pricesNode.setProperty("currency", drug.prices.get(i).cost.currency);
                    pricesNode.setProperty("unit", drug.prices.get(i).unit);
                    g.addNode(pricesNode);
                    g.addEdge(new Edge(pharmacoeconomicsNode, pricesNode, "COSTS"));
                }
            }

            //Dosages
            if (drug.dosages != null) {
                for (int i = 0; i < drug.dosages.size(); i++) {
                    Node dosageNode = createNode("Dosage");
                    dosageNode.setProperty("form", drug.dosages.get(i).form);
                    dosageNode.setProperty("route", drug.dosages.get(i).route);
                    dosageNode.setProperty("strength", drug.dosages.get(i).strength);
                    g.addNode(dosageNode);
                    g.addEdge(new Edge(pharmacoeconomicsNode, dosageNode, "HAS_DOSAGE"));
                }
            }

            //Patents
            if (drug.patents != null) {
                for (int i = 0; i < drug.patents.size(); i++) {
                    Node patentNode = createNode("Patent");
                    patentNode.setProperty("number", drug.patents.get(i).number);
                    patentNode.setProperty("country", drug.patents.get(i).country);
                    patentNode.setProperty("approved", drug.patents.get(i).approved);
                    patentNode.setProperty("expires", drug.patents.get(i).expires);
                    patentNode.setProperty("pediatric-extension", drug.patents.get(i).pediatricExtension);
                    g.addNode(patentNode);
                    g.addEdge(new Edge(pharmacoeconomicsNode, patentNode, "IS_PATENTED"));
                }
            }

            //Categories
            if (drug.categories != null) {
                for (int i = 0; i < drug.categories.size(); i++) {
                    Node categoryNode = createNode("Category");
                    categoryNode.setProperty("category", drug.categories.get(i).category);
                    categoryNode.setProperty("mesh-id", drug.categories.get(i).meshId);
                    g.addNode(categoryNode);
                    g.addEdge(new Edge(drugNode, categoryNode, "CATEGORIZED_IN"));
                }
            }

            //calculated Properties
            if (drug.calculatedProperties != null) {
                for (int i = 0; i < drug.calculatedProperties.size(); i++) {
                    Node calculatedPropertyNode = createNode("Calculated Property");
                    calculatedPropertyNode.setProperty("value", drug.calculatedProperties.get(i).value);
                    calculatedPropertyNode.setProperty("kind", drug.calculatedProperties.get(i).kind.toString());
                    calculatedPropertyNode.setProperty("source", drug.calculatedProperties.get(i).source.toString());
                    g.addNode(calculatedPropertyNode);
                    g.addEdge(new Edge(drugNode, calculatedPropertyNode, "HAS_CALCULATED_PROPERTY"));
                }
            }

            //experimental Properties
            if (drug.experimentalProperties != null) {
                for (int i = 0; i < drug.experimentalProperties.size(); i++) {
                    Node experimentalPropertyNode = createNode("Experimental Property");
                    experimentalPropertyNode.setProperty("value", drug.experimentalProperties.get(i).value);
                    experimentalPropertyNode.setProperty("kind", drug.experimentalProperties.get(i).kind.toString());
                    experimentalPropertyNode.setProperty("source", drug.experimentalProperties.get(i).source);
                    g.addNode(experimentalPropertyNode);
                    g.addEdge(new Edge(drugNode, experimentalPropertyNode, "HAS_EXPERIMENTAL_PROPERTY"));
                }
            }

            //Drug Interactions
            if (drug.drugInteractions != null) {
                for (int i = 0; i < drug.drugInteractions.size(); i++) {
                    DrugInteractionTriple triple = new DrugInteractionTriple();
                    triple.drugBankIdSource = drug.drugbankIds.get(0).value;
                    triple.drugBankIdTarget = drug.drugInteractions.get(i).drugbankId.value;
                    triple.description = drug.drugInteractions.get(i).description;
                    drugInteractionCache.add(triple);
                }
            }

            //affected Organisms
            if (drug.affectedOrganisms != null) {
                String organismNames = String.join("; ", drug.affectedOrganisms);
                Node affectedOrganismNode = createNode("Affected Organism");
                affectedOrganismNode.setProperty("names", organismNames);
                g.addNode(affectedOrganismNode);
                g.addEdge(new Edge(drugNode, affectedOrganismNode, "AFFECTS_ORG"));
            }

            //Pathways->Pathway-Enzymes
            if (drug.pathways != null) {
                for (int i = 0; i < drug.pathways.size(); i++) {
                    PathwayDrugTriple triple = new PathwayDrugTriple();
                    triple.smpdIdSource = drug.pathways.get(i).smpdbId;
                    triple.drugBankIdTarget = drugs.get(i).drugbankIds.get(0).value;
                    triple.description = drug.pathways.get(i).name;
                    pathwayDrugCache.add(triple);

                    if (pathway_lookUp.containsKey(drug.pathways.get(i).smpdbId)) {
                        Pathway drugPathway = drug.pathways.get(i);
                        g.addEdge(new Edge(drugNode, pathway_lookUp.get(drugPathway.smpdbId), "IS_IN_PATHWAY"));

                    } else {
                        Node pathwaysNode = createNode("Pathway");
                        pathway_lookUp.put(drug.pathways.get(i).smpdbId, pathwaysNode);
                        pathwaysNode.setProperty("name", drug.pathways.get(i).name);
                        pathwaysNode.setProperty("smpdbId", drug.pathways.get(i).smpdbId);
                        pathwaysNode.setProperty("category", drug.pathways.get(i).category);
                        g.addNode(pathwaysNode);
                        g.addEdge(new Edge(drugNode, pathwaysNode, "IS_IN_PATHWAY"));
                        if (drug.pathways.get(i).enzymes != null) {
                            String enzymes = String.join("; ", drug.pathways.get(i).enzymes);
                            pathwaysNode.setProperty("enzymes", enzymes);
                        }
                    }
                }
            }

            //Food Interactions
            if (drug.foodInteractions != null) {
                for (int i = 0; i < drug.foodInteractions.size(); i++) {
                    String foodInteractions = String.join("; ", drug.foodInteractions.get(i));
                    Node foodInteractionsNode = createNode("Food Interaction");
                    foodInteractionsNode.setProperty("foodInteraction", foodInteractions);
                    g.addNode(foodInteractionsNode);
                    g.addEdge(new Edge(drugNode, foodInteractionsNode, "HAS_FOOD_INTERACTION"));
                }
            }

            //Targets->Polypeptide->Synonyms, ExternalIdentifiers, Pfams, GO-Classifiers, Organism
            if (drug.targets != null) {
                for (int i = 0; i < drug.targets.size(); i++) {
                    Target drugtarget = drug.targets.get(i);
                    Node targetNode = createNode("Target");
                    targetNode.setProperty("position", drugtarget.position);
                    targetNode.setProperty("id", drugtarget.id);
                    targetNode.setProperty("name", drugtarget.name);
                    targetNode.setProperty("organism", drugtarget.organism);
                    targetNode.setProperty("knownAction", drugtarget.knownAction.value);
                    if (drugtarget.actions != null) {
                        String actions = String.join("; ", drugtarget.actions);
                        targetNode.setProperty("actions", actions);
                    }

                    createReferenceListNode(g, referenceList_lookUp, targetNode, drugtarget.references);
                    if(drugtarget.polypeptide != null) {
                        if (polypeptide_lookUp.containsKey(drugtarget.polypeptide.id)) {
                            g.addEdge(new Edge(drugNode, polypeptide_lookUp.get(drugtarget.polypeptide.id),
                                               "IS_POLYPEPTIDE"));
                        } else {
                            createPolypeptideNode(g, organism_lookUp, polypeptide_lookUp, targetNode, drugtarget.polypeptide);
                        }
                    }
                    g.addNode(targetNode);
                    g.addEdge(new Edge(drugNode, targetNode, "TARGETS"));
                }
            }
            //Interactant -> Carrier, Enzyme, Target, Transporter
            //Enzyme
            if (drug.enzymes != null) {
                for (int i = 0; i < drug.enzymes.size(); i++) {
                    Enzyme drugEnzyme = drug.enzymes.get(i);
                    if (enzyme_lookUp.containsKey(drugEnzyme.id)) {
                        g.addEdge(new Edge(drugNode, enzyme_lookUp.get(drugEnzyme.id), "IS_ENZYME"));
                    } else {
                        Node enzymeNode = createNode("Enzyme");
                        enzymeNode.setProperty("position", drugEnzyme.position);
                        enzymeNode.setProperty("inhibitionStrength", drugEnzyme.inhibitionStrength);
                        enzymeNode.setProperty("inductionStrength", drugEnzyme.inductionStrength);
                        enzymeNode.setProperty("id", drugEnzyme.id);
                        enzymeNode.setProperty("name", drugEnzyme.name);
                        enzymeNode.setProperty("organism", drugEnzyme.organism);
                        if (drugEnzyme.actions != null) {
                            String actions = String.join("; ", drugEnzyme.actions);
                            enzymeNode.setProperty("actions", actions);
                        }
                        createReferenceListNode(g, referenceList_lookUp, enzymeNode, drugEnzyme.references);
                        if(drugEnzyme.polypeptide != null) {
                            if (polypeptide_lookUp.containsKey(drugEnzyme.polypeptide.id)) {
                                g.addEdge(new Edge(drugNode, polypeptide_lookUp.get(drugEnzyme.polypeptide.id),
                                                   "IS_POLYPEPTIDE"));
                            } else {
                                createPolypeptideNode(g, organism_lookUp, polypeptide_lookUp, enzymeNode, drugEnzyme.polypeptide);
                            }
                        }
                        g.addNode(enzymeNode);
                        g.addEdge(new Edge(drugNode, enzymeNode, "TARGETS"));
                    }
                }
            }
            //Carrier
            if (drug.carriers != null) {
                for (int i = 0; i < drug.carriers.size(); i++) {
                    Carrier drugCarrier = drug.carriers.get(i);
                    if (carriers_lookUp.containsKey(drugCarrier.id)) {
                        g.addEdge(new Edge(drugNode, carriers_lookUp.get(drugCarrier.id), "IS_CARRIER"));
                    } else {
                        Node carriersNode = createNode("Carrier");
                        carriersNode.setProperty("position", drugCarrier.position);
                        carriersNode.setProperty("id", drugCarrier.id);
                        carriersNode.setProperty("name", drugCarrier.name);
                        carriersNode.setProperty("organism", drugCarrier.organism);
                        if (drugCarrier.actions != null) {
                            String actions = String.join("; ", drugCarrier.actions);
                            carriersNode.setProperty("actions", actions);
                        }
                        createReferenceListNode(g, referenceList_lookUp, carriersNode, drugCarrier.references);
                        if(drugCarrier.polypeptide != null) {
                            if (polypeptide_lookUp.containsKey(drugCarrier.polypeptide.id)) {
                                g.addEdge(new Edge(drugNode, polypeptide_lookUp.get(drugCarrier.polypeptide.id),
                                                   "IS_POLYPEPTIDE"));
                            } else {
                                createPolypeptideNode(g, organism_lookUp, polypeptide_lookUp, carriersNode, drugCarrier.polypeptide);
                            }
                        }
                        g.addNode(carriersNode);
                        g.addEdge(new Edge(drugNode, carriersNode, "TARGETS"));
                    }
                }
            }
            //Transporter
            if (drug.transporters != null) {
                for (int i = 0; i < drug.transporters.size(); i++) {
                    Transporter drugTransporter = drug.transporters.get(i);
                    if (transporters_lookUp.containsKey(drugTransporter.id)) {
                        g.addEdge(new Edge(drugNode, transporters_lookUp.get(drugTransporter.id), "IS_TRANSPORTER"));
                    } else {
                        Node transportersNode = createNode("Transporter");
                        transportersNode.setProperty("position", drugTransporter.position);
                        transportersNode.setProperty("id", drugTransporter.id);
                        transportersNode.setProperty("name", drugTransporter.name);
                        transportersNode.setProperty("organism", drugTransporter.organism);
                        if (drugTransporter.actions != null) {
                            String actions = String.join("; ", drugTransporter.actions);
                            transportersNode.setProperty("actions", actions);
                        }
                        createReferenceListNode(g, referenceList_lookUp, transportersNode, drugTransporter.references);
                        if(drugTransporter.polypeptide != null) {
                            if (polypeptide_lookUp.containsKey(drugTransporter.polypeptide.id)) {
                                g.addEdge(new Edge(drugNode, polypeptide_lookUp.get(drugTransporter.polypeptide.id),
                                                   "IS_POLYPEPTIDE"));
                            } else {
                                createPolypeptideNode(g, organism_lookUp, polypeptide_lookUp, transportersNode, drugTransporter.polypeptide);
                            }
                        }
                        g.addNode(transportersNode);
                        g.addEdge(new Edge(drugNode, transportersNode, "TARGETS"));
                    }
                }
            }
            //Sequences
            if (drug.sequences != null) {
                for (int i = 0; i < drug.sequences.size(); i++) {
                    Node sequencesNode = createNode("Sequence");
                    sequencesNode.setProperty("value", drug.sequences.get(i).value);
                    sequencesNode.setProperty("format", drug.sequences.get(i).format);
                    g.addNode(sequencesNode);
                    g.addEdge(new Edge(drugNode, sequencesNode, "HAS_SEQUENCE"));
                }
            }
            //Reactions: Drug -> Reaction -> Reaction-Enzyme -> Metabolite (dummy)
            if (drug.reactions != null) {
                for (int i = 0; i < drug.reactions.size(); i++) {
                    Node reactionsNode = createNode("Reaction");
                    reactionsNode.setProperty("sequence", drug.reactions.get(i).sequence);
                    ReactionElement leftElement = drug.reactions.get(i).leftElement;
                    if (reactionElement_lookUp.containsKey(leftElement.drugbankId)) {
                        Edge leftElementEdge = new Edge(drugNode, reactionsNode, "SUBSTRATE_IN");
                        g.addEdge(leftElementEdge);
                    } else {
                        Edge leftElementEdge = new Edge(drugNode, reactionsNode, "SUBSTRATE_IN");
                        g.addEdge(leftElementEdge);
                        leftElementEdge.setProperty("name", drug.reactions.get(i).leftElement.name);
                    }

                    ReactionElement rightElement = drug.reactions.get(i).rightElement;
                    if (!reactionElement_lookUp.containsKey(rightElement.drugbankId)) {
                        Node metaboliteNodeDummy = createNode("Metabolite Dummy");
                        metaboliteNodeDummy.setProperty("I'm a dummy", rightElement.name);
                        g.addNode(metaboliteNodeDummy);
                        Edge rightElementEdge = new Edge(reactionsNode, metaboliteNodeDummy, "HAS_PRODUCT");
                        g.addEdge(rightElementEdge);
                        rightElementEdge.setProperty("name", drug.reactions.get(i).rightElement.name);

                    } else {
                        g.addEdge(new Edge(reactionsNode, reactionElement_lookUp.get(rightElement.drugbankId), "RIGHT_ELEMENT"));
                        Node metaboliteNodeDummy = createNode("Metabolite Dummy");
                        Edge rightElementEdge = new Edge(reactionsNode, metaboliteNodeDummy, "HAS_PRODUCT");
                        rightElementEdge.setProperty("name", drug.reactions.get(i).rightElement.name);
                        g.addEdge(rightElementEdge);

                    }

                    if (drug.reactions.get(i).enzymes != null) {
                        for (int j = 0; j < drug.reactions.get(i).enzymes.size(); j++) {
                            Node reactionEnzymeNode = createNode("Reaction Enzyme");
                            reactionEnzymeNode.setProperty("drugbankId", drug.reactions.get(i).enzymes.get(j).drugbankId);
                            reactionEnzymeNode.setProperty("name", drug.reactions.get(i).enzymes.get(j).name);
                            reactionEnzymeNode.setProperty("uniProtId", drug.reactions.get(i).enzymes.get(j).uniprotId);
                            g.addNode(reactionEnzymeNode);
                            g.addEdge(new Edge(reactionEnzymeNode, reactionsNode, "IS_INFERRED_TO"));
                        }
                    }
                    g.addNode(reactionsNode);
                }
            }

            //SNP-Effects
            if (drug.snpEffects != null) {
                for (int i = 0; i < drug.snpEffects.size(); i++) {
                    Node snpEffectsNode = createNode("SNP Effect");
                    snpEffectsNode.setProperty("proteinName", drug.snpEffects.get(i).proteinName);
                    snpEffectsNode.setProperty("geneSymbol", drug.snpEffects.get(i).geneSymbol);
                    snpEffectsNode.setProperty("uniprotId", drug.snpEffects.get(i).uniprotId);
                    snpEffectsNode.setProperty("rsId", drug.snpEffects.get(i).rsId);
                    snpEffectsNode.setProperty("allele", drug.snpEffects.get(i).allele);
                    snpEffectsNode.setProperty("definingChange", drug.snpEffects.get(i).definingChange);
                    snpEffectsNode.setProperty("description", drug.snpEffects.get(i).description);
                    snpEffectsNode.setProperty("pubmedId", drug.snpEffects.get(i).pubmedId);
                    g.addNode(snpEffectsNode);
                    g.addEdge(new Edge(drugNode, snpEffectsNode, "HAS_SNP_EFFECT"));
                }
            }
            //SNP-Adverse-Drug-Reactions
            if (drug.snpAdverseDrugReactions != null) {
                for (int i = 0; i < drug.snpAdverseDrugReactions.size(); i++) {
                    Node snpAdverseDrugReactionsNode = createNode("SNP Adverse Drug Reaction");
                    snpAdverseDrugReactionsNode.setProperty("proteinName",
                                                            drug.snpAdverseDrugReactions.get(i).proteinName);
                    snpAdverseDrugReactionsNode.setProperty("geneSymbol",
                                                            drug.snpAdverseDrugReactions.get(i).geneSymbol);
                    snpAdverseDrugReactionsNode.setProperty("uniprotId", drug.snpAdverseDrugReactions.get(i).uniprotId);
                    snpAdverseDrugReactionsNode.setProperty("rsId", drug.snpAdverseDrugReactions.get(i).rsId);
                    snpAdverseDrugReactionsNode.setProperty("allele", drug.snpAdverseDrugReactions.get(i).allele);
                    snpAdverseDrugReactionsNode.setProperty("adverseReaction",
                                                            drug.snpAdverseDrugReactions.get(i).adverseReaction);
                    snpAdverseDrugReactionsNode.setProperty("description",
                                                            drug.snpAdverseDrugReactions.get(i).description);
                    snpAdverseDrugReactionsNode.setProperty("pubmedId", drug.snpAdverseDrugReactions.get(i).pubmedId);
                    g.addNode(snpAdverseDrugReactionsNode);
                    g.addEdge(new Edge(drugNode, snpAdverseDrugReactionsNode, "HAS_SNP_ADVERSE_DRUG_REACTION"));
                }
            }

            drugs.remove(drugIndex);
            //System.gc();
            //if (drugIndex < 12000) {
            //    break;
            //}
        }
        for (DrugInteractionTriple triple : drugInteractionCache) {
            if (drug_lookUp.containsKey(triple.drugBankIdTarget)) {
                Node source = drug_lookUp.get(triple.drugBankIdSource);
                Node target = drug_lookUp.get(triple.drugBankIdTarget);
                Edge drugInteractionEdge = new Edge(source, target, "INTERACTS_WITH_DRUG");
                g.addEdge(drugInteractionEdge);
                drugInteractionEdge.setProperty("description", triple.description);
            }
            else{
                //Log-Message if key missing!
            }

        }

        for (PathwayDrugTriple triple : pathwayDrugCache) {
            if (pathway_lookUp.containsKey(triple.drugBankIdTarget)) {
                Node source = pathway_lookUp.get(triple.smpdIdSource);
                Node target = drug_lookUp.get(triple.drugBankIdTarget);
                Edge pathwayDrugEdge = new Edge(source, target, "IS_PATHWAYDRUG");
                g.addEdge(pathwayDrugEdge);
                pathwayDrugEdge.setProperty("name", triple.description);
            }
            else{
                //Log-Message if key missing!
            }

        }

        return true;
    }
    //Polypeptide
    private void createPolypeptideNode(Graph g, Hashtable<String, Node> organism_lookUp, Hashtable<String, Node> polypeptide_lookUp, Node parentNode, Polypeptide polypeptide){
        if(polypeptide != null){
            Node polypeptideNode = createNode("Polypeptide");
            polypeptide_lookUp.put(polypeptide.id, polypeptideNode);
            polypeptideNode.setProperty("id", polypeptide.id);
            polypeptideNode.setProperty("name", polypeptide.name);
            polypeptideNode.setProperty("source", polypeptide.source);
            polypeptideNode.setProperty("generalFunction", polypeptide.generalFunction);
            polypeptideNode.setProperty("specificFunction",
                                        polypeptide.specificFunction);
            polypeptideNode.setProperty("geneName", polypeptide.geneName);
            polypeptideNode.setProperty("locus", polypeptide.locus);
            polypeptideNode.setProperty("cellularLocation",
                                        polypeptide.cellularLocation);
            polypeptideNode.setProperty("transmembraneRegions",
                                        polypeptide.transmembraneRegions);
            polypeptideNode.setProperty("signalRegions", polypeptide.signalRegions);
            polypeptideNode.setProperty("theoreticalPi", polypeptide.theoreticalPi);
            polypeptideNode.setProperty("molecularWeight", polypeptide.molecularWeight);
            polypeptideNode.setProperty("chromosomeLocation",
                                        polypeptide.chromosomeLocation);
            polypeptideNode.setProperty("aminoAcidSequence",
                                        polypeptide.aminoAcidSequence);
            polypeptideNode.setProperty("geneSequence", polypeptide.geneSequence);
            if (polypeptide.synonyms != null) {
                String synonyms = String.join("; ", polypeptide.synonyms);
                polypeptideNode.setProperty("aminoAcidSequence", synonyms);
            }
            g.addNode(polypeptideNode);
            g.addEdge(new Edge(parentNode, polypeptideNode, "IS_POLYPEPTIDE"));
            if (polypeptide.externalIdentifiers != null) {
                for (int j = 0; j < polypeptide.externalIdentifiers.size(); j++) {
                    Node polypeptideExternalIdentifierNode = createNode("Polypeptide External Identifier");
                    polypeptideExternalIdentifierNode.setProperty("resource", polypeptide.externalIdentifiers.get(j).resource);
                    polypeptideExternalIdentifierNode.setProperty("identifier", polypeptide.externalIdentifiers.get(j).identifier);
                    g.addNode(polypeptideExternalIdentifierNode);
                    g.addEdge(new Edge(polypeptideNode, polypeptideExternalIdentifierNode,
                                       "HAS_POLYPEPTIDE_EXTERNAL_IDENTIFIER"));
                }
            }
            if (polypeptide.pfams != null) {
                for (int j = 0; j < polypeptide.pfams.size(); j++) {
                    Node pfamsNode = createNode("Pfam");
                    pfamsNode.setProperty("identifier", polypeptide.pfams
                            .get(j).identifier);
                    pfamsNode.setProperty("name", polypeptide.pfams.get(j).name);
                    g.addNode(pfamsNode);
                    g.addEdge(new Edge(polypeptideNode, pfamsNode, "HAS_PFAM"));
                }
            }
            if (polypeptide.goClassifiers != null) {
                for (int j = 0; j < polypeptide.goClassifiers.size(); j++) {
                    Node goClassifiersNode = createNode("GO Classifiers");
                    goClassifiersNode.setProperty("category", polypeptide.goClassifiers
                            .get(j).category);
                    goClassifiersNode.setProperty("description", polypeptide.goClassifiers.get(j).description);
                    g.addNode(goClassifiersNode);
                    g.addEdge(new Edge(polypeptideNode, goClassifiersNode, "HAS_GO_CLASSIFIER"));
                }
            }
            if (polypeptide.organism != null) {
                Organism polypeptidesOrganism = polypeptide.organism;
                if (organism_lookUp.containsKey(polypeptidesOrganism.ncbiTaxonomyId)) {
                    g.addEdge(new Edge(polypeptideNode, organism_lookUp.get(polypeptidesOrganism.ncbiTaxonomyId), "ORGANISM"));
                } else {
                    Node organismNode = createNode("Organism");
                    organismNode.setProperty("ncbiTaxonomyId", polypeptide.organism.ncbiTaxonomyId);
                    organismNode.setProperty("value", polypeptide.organism.value);
                    g.addNode(organismNode);
                    g.addEdge(new Edge(polypeptideNode, organismNode, "HAS_ORGANISM"));
                    organism_lookUp.put(polypeptidesOrganism.ncbiTaxonomyId, organismNode);
                }
            }
        }
    }

    //References
    private void createReferenceListNode(Graph g, Hashtable<Object, Node> referenceList_lookUp, Node parent, ReferenceList references ) {
        Node referenceListNode = null;
        if ((references.textbooks != null && references.textbooks.size() > 0) ||
            (references.articles != null && references.articles.size() > 0) ||
            (references.attachments != null && references.attachments.size() > 0) ||
            (references.links != null && references.links.size() > 0)) {
            referenceListNode = createNode("Reference");
            g.addNode(referenceListNode);
            g.addEdge(new Edge(parent, referenceListNode, "HAS_REFERENCE"));
        }
        if (references.textbooks != null) {
            for (int j = 0; j < references.textbooks.size(); j++) {
                Textbook rf = references.textbooks.get(j);
                referenceListNode.setProperty("textbook", references.textbooks.get(j).refId);
                if (referenceList_lookUp.containsKey(rf.refId)) {
                    g.addEdge(new Edge(referenceListNode, referenceList_lookUp.get(rf.refId), "HAS_TEXTBOOK"));
                } else {
                    Node textbookNode = createNode("Textbook");
                    textbookNode.setProperty("citation", rf.citation);
                    textbookNode.setProperty("isbn", rf.isbn);
                    textbookNode.setProperty("refId", rf.refId);
                    g.addNode(textbookNode);
                    g.addEdge(new Edge(referenceListNode, textbookNode, "HAS_TEXTBOOK"));
                }
            }
        }
        if (references.articles != null) {
            for (int j = 0; j < references.articles.size(); j++) {
                Article a = references.articles.get(j);
                referenceListNode.setProperty("article", a.refId);
                if (referenceList_lookUp.containsKey(a.refId)) {
                    g.addEdge(new Edge(referenceListNode, referenceList_lookUp.get(a.refId), "HAS_ARTICLE"));
                } else {
                    Node articleNode = createNode("Article");
                    articleNode.setProperty("citation", a.citation);
                    articleNode.setProperty("pubmedId", a.pubmedId);
                    articleNode.setProperty("refId", a.refId);
                    g.addNode(articleNode);
                    g.addEdge(new Edge(referenceListNode, articleNode, "HAS_ARTICLE"));
                }
            }
        }
        if (references.links != null) {
            for (int j = 0; j < references.links.size(); j++) {
                Link l = references.links.get(j);
                if (referenceList_lookUp.containsKey(l.refId)) {
                    g.addEdge(new Edge(referenceListNode, referenceList_lookUp.get(l.refId), "HAS_LINK"));
                } else {
                    Node linksNode = createNode("Link");
                    linksNode.setProperty("title", l.title);
                    linksNode.setProperty("url", l.url);
                    linksNode.setProperty("refId", l.refId);
                    g.addNode(linksNode);
                    g.addEdge(new Edge(referenceListNode, linksNode, "HAS_LINK"));
                }
            }
        }
        if (references.attachments != null) {
            for (int j = 0; j < references.attachments.size(); j++) {
                Attachment at = references.attachments.get(j);
                if (referenceList_lookUp.containsKey(at.refId)) {
                    g.addEdge(new Edge(referenceListNode, referenceList_lookUp.get(at.refId), "HAS_ATTACHMENT"));
                } else {
                    Node attachmentNode = createNode("Attachment");
                    attachmentNode.setProperty("title", at.title);
                    attachmentNode.setProperty("url", at.url);
                    attachmentNode.setProperty("refId", at.refId);
                    g.addNode(attachmentNode);
                    g.addEdge(new Edge(referenceListNode, attachmentNode, "HAS_ATTACHEMENT"));
                }
            }
        }
    }

    private void exportDrugStructures(final Graph graph, final List<DrugStructure> drugStructures) {
        for (DrugStructure drug : drugStructures)
            exportDrugStructure(graph, drug);
    }

    private void exportDrugStructure(final Graph graph, final DrugStructure drug) {
        createNodeFromModel(graph, drug);
    }

    private void exportMetaboliteStructures(final Graph graph, final List<MetaboliteStructure> metabolites) {
        for (MetaboliteStructure metabolite : metabolites)
            exportMetaboliteStructure(graph, metabolite);
    }

    private void exportMetaboliteStructure(final Graph graph, final MetaboliteStructure metabolite) {

    }
}