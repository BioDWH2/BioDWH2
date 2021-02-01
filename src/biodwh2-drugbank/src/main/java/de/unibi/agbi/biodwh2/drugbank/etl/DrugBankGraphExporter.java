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

import java.util.*;

public class DrugBankGraphExporter extends GraphExporter<DrugBankDataSource> {
    private static class DrugInteractionTriple {
        String drugBankIdSource;
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

    public DrugBankGraphExporter(final DrugBankDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph g) {
        g.setNodeIndexPropertyKeys("id", "drugbank_id");
        Map<String, Node> drug_lookUp = new HashMap<>();
        Map<String, Long> metabolite_lookUp = new HashMap<>();
        Map<String, Node> pathway_lookUp = new HashMap<>();
        Map<String, Node> organism_lookUp = new HashMap<>();
        Map<Object, Node> enzyme_lookUp = new HashMap<>();
        Map<Object, Node> carriers_lookUp = new HashMap<>();
        Map<Object, Node> transporters_lookUp = new HashMap<>();
        Map<Object, Node> referenceList_lookUp = new HashMap<>();
        Map<String, Node> polypeptide_lookUp = new HashMap<>();
        ArrayList<DrugInteractionTriple> drugInteractionCache = new ArrayList<>();
        ArrayList<PathwayDrugTriple> pathwayDrugCache = new ArrayList<>();
        ArrayList<MetaboliteTriple> metaboliteCache = new ArrayList<>();
        exportMetaboliteStructures(g, dataSource.metaboliteStructures, metabolite_lookUp);
        ArrayList<Drug> drugs = dataSource.drugBankData.drugs;
        int counter = 1;
        for (int drugIndex = drugs.size() - 1; drugIndex >= 0; drugIndex--) {
            if (counter % 250 == 0)
                System.out.println("Exporting drug " + counter + " of " + drugs.size());
            counter++;
            /*if (counter > 4000) {
                break;
            }*/
            Drug drug = drugs.get(drugIndex);
            Node drugNode = g.addNode("Drug", "name", drug.name, "description", drug.description, "group",
                                      drug.groups.toString());
            drug_lookUp.put(drug.drugbankIds.get(0).value, drugNode);
            for (int i = 0; i < drug.drugbankIds.size(); i++) {
                if (drug.drugbankIds.get(i).primary) {
                    setPropertyIfNotNull(drugNode, "drugbank_id", drug.drugbankIds.get(i).value);
                }
            }
            //Chemical Property
            Node chemicalPropertyNode = g.addNode("Chemical_Property", "cas_number", drug.casNumber, "unii", drug.unii,
                                                  "average_mass", drug.averageMass);
            setPropertyIfNotNull(chemicalPropertyNode, "monoisotopic_mass", drug.monoisotopicMass);
            if (drug.state != null) {
                setPropertyIfNotNull(chemicalPropertyNode, "state", drug.state.toString());
            }
            setPropertyIfNotNull(chemicalPropertyNode, "synthesis_reference", drug.synthesisReference);
            g.update(chemicalPropertyNode);
            g.addEdge(drugNode, chemicalPropertyNode, "HAS_CHEMICAL_PROPERTY");
            //Pharmacology
            Node pharmacologyNode = g.addNode("Pharmacology", "mechanism_of_action", drug.mechanismOfAction, "toxicity",
                                              drug.toxicity, "metabolism", drug.metabolism);
            setPropertyIfNotNull(pharmacologyNode, "absorption", drug.absorption);
            setPropertyIfNotNull(pharmacologyNode, "indication", drug.indication);
            setPropertyIfNotNull(pharmacologyNode, "pharmacodynamics", drug.pharmacodynamics);
            setPropertyIfNotNull(pharmacologyNode, "half_life", drug.halfLife);
            setPropertyIfNotNull(pharmacologyNode, "protein_binding", drug.proteinBinding);
            setPropertyIfNotNull(pharmacologyNode, "route_of_elimination", drug.routeOfElimination);
            setPropertyIfNotNull(pharmacologyNode, "volume_of_distribution", drug.volumeOfDistribution);
            setPropertyIfNotNull(pharmacologyNode, "clearance", drug.clearance);
            g.update(pharmacologyNode);
            g.addEdge(drugNode, pharmacologyNode, "HAS_PHARMACOLOGY");
            if (drug.atcCodes != null) {
                ArrayList<String> atcCodes = new ArrayList<>();
                for (int j = 0; j < drug.atcCodes.size(); j++) {
                    atcCodes.add(drug.atcCodes.get(j).code);
                }
                setPropertyIfNotNull(drugNode, "atc_code", atcCodes.toArray(new String[atcCodes.size()]));
            }
            setPropertyIfNotNull(drugNode, "ahfs_code", drug.ahfsCodes);
            setPropertyIfNotNull(drugNode, "pdb_entries", drug.pdbEntries);
            setPropertyIfNotNull(drugNode, "fda_label", drug.fdaLabel);
            setPropertyIfNotNull(drugNode, "msds", drug.msds);
            if (drug.externalIdentifiers != null) {
                for (int j = 0; j < drug.externalIdentifiers.size(); j++) {
                    Node externalIdentifierNode = g.addNode("External_Identifier", "resource",
                                                            drug.externalIdentifiers.get(j).resource.value,
                                                            "identifier", drug.externalIdentifiers.get(j).identifier);
                    g.addEdge(drugNode, externalIdentifierNode, "HAS_EXTERNAL_IDENTIFIER");
                }
            }
            g.update(drugNode);
            //ReferenceList -> Article, Attachement, Link, Textbook
            if (drug.generalReferences != null) {
                createReferenceListNode(g, referenceList_lookUp, drugNode, drug.generalReferences);
            }
            //Classification
            if (drug.classification != null) {
                Node classificationNode = g.addNode("Classification", "description", drug.classification.description,
                                                    "direct_parent", drug.classification.directParent, "kingdom",
                                                    drug.classification.kingdom);
                setPropertyIfNotNull(classificationNode, "superclass", drug.classification.superclass);
                setPropertyIfNotNull(classificationNode, "class", drug.classification.class_);
                setPropertyIfNotNull(classificationNode, "subclass", drug.classification.subclass);
                if (drug.classification.alternativeParents != null) {
                    String alternativeParents = String.join("; ", drug.classification.alternativeParents);
                    setPropertyIfNotNull(classificationNode, "alternative_parents", alternativeParents);
                }
                if (drug.classification.substituents != null) {
                    String substituents = String.join("; ", drug.classification.substituents);
                    setPropertyIfNotNull(classificationNode, "substituents", substituents);
                }
                g.update(classificationNode);
                g.addEdge(drugNode, classificationNode, "CLASSIFIED_AS");
            }
            //External Links
            if (drug.externalLinks != null) {
                for (int i = 0; i < drug.externalLinks.size(); i++) {
                    Node externalLinkNode = g.addNode("External_Link", "external_links", drug.externalLinks.get(i).url,
                                                      "external_links_resource",
                                                      drug.externalLinks.get(i).resource.toString());
                    g.addEdge(drugNode, externalLinkNode, "HAS_EXTERNAL_LINKS");
                }
            }
            //Salts
            if (drug.salts != null) {
                for (int i = 0; i < drug.salts.size(); i++) {
                    Node saltNode = g.addNode("Salt", "drugbank_id", drug.salts.get(i).drugbankId.value, "name",
                                              drug.salts.get(i).name, "unii", drug.salts.get(i).unii);
                    setPropertyIfNotNull(saltNode, "cas_number", drug.salts.get(i).casNumber);
                    setPropertyIfNotNull(saltNode, "inchikey", drug.salts.get(i).inchikey);
                    setPropertyIfNotNull(saltNode, "average_mass", drug.salts.get(i).averageMass);
                    setPropertyIfNotNull(saltNode, "monoisotopic_mass", drug.salts.get(i).monoisotopicMass);
                    g.update(saltNode);
                    g.addEdge(drugNode, saltNode, "HAS_SALT");
                }
            }
            //Synonyms
            if (drug.synonyms != null) {
                for (int i = 0; i < drug.synonyms.size(); i++) {
                    Node synonymNode = g.addNode("Synonym", "synonym", drug.synonyms.get(i).value, "language",
                                                 drug.synonyms.get(i).language, "coder", drug.synonyms.get(i).coder);
                    g.addEdge(drugNode, synonymNode, "HAS_SYNONYM");
                }
            }
            //International Brands
            if (drug.internationalBrands != null) {
                for (int i = 0; i < drug.internationalBrands.size(); i++) {
                    Node internationalBrandsNode = g.addNode("International_Brand", "name",
                                                             drug.internationalBrands.get(i).name, "company",
                                                             drug.internationalBrands.get(i).company);
                    g.addEdge(drugNode, internationalBrandsNode, "HAS_BRAND");
                }
            }
            //Mixtures
            if (drug.mixtures != null) {
                for (int i = 0; i < drug.mixtures.size(); i++) {
                    Node mixtureNode = g.addNode("Mixture", "name", drug.mixtures.get(i).name, "ingredients",
                                                 drug.mixtures.get(i).ingredients);
                    g.addEdge(drugNode, mixtureNode, "IS_IN_MIXTURE");
                }
            }
            //Pharmaeconomics -> Manufacturer, Packager, Product, Prices
            Node pharmacoeconomicsNode = g.addNode("Pharmacoeconomics");
            g.addEdge(drugNode, pharmacoeconomicsNode, "HAS_PHARMACOECONOMICS");
            //Product
            if (drug.products != null) {
                for (int i = 0; i < drug.products.size(); i++) {
                    Node productNode = g.addNode("Product", "name", drug.products.get(i).name, "labeller",
                                                 drug.products.get(i).labeller, "ndc_id", drug.products.get(i).ndcId);
                    setPropertyIfNotNull(productNode, "ndc_product_code", drug.products.get(i).ndcProductCode);
                    setPropertyIfNotNull(productNode, "dpd_id", drug.products.get(i).dpdId);
                    setPropertyIfNotNull(productNode, "ema_product_code", drug.products.get(i).emaProductCode);
                    setPropertyIfNotNull(productNode, "ema_ma_number", drug.products.get(i).emaMaNumber);
                    setPropertyIfNotNull(productNode, "started_marketing_on", drug.products.get(i).startedMarketingOn);
                    setPropertyIfNotNull(productNode, "ended_marketing_on", drug.products.get(i).endedMarketingOn);
                    setPropertyIfNotNull(productNode, "dosage_form", drug.products.get(i).dosageForm);
                    setPropertyIfNotNull(productNode, "strength", drug.products.get(i).strength);
                    setPropertyIfNotNull(productNode, "route", drug.products.get(i).route);
                    setPropertyIfNotNull(productNode, "fda_application_number",
                                         drug.products.get(i).fdaApplicationNumber);
                    setPropertyIfNotNull(productNode, "generic", drug.products.get(i).generic);
                    setPropertyIfNotNull(productNode, "over_the_counter", drug.products.get(i).overTheCounter);
                    setPropertyIfNotNull(productNode, "approved", drug.products.get(i).approved);
                    setPropertyIfNotNull(productNode, "country", drug.products.get(i).country.value);
                    setPropertyIfNotNull(productNode, "source", drug.products.get(i).source.value);
                    g.update(productNode);
                    g.addEdge(pharmacoeconomicsNode, productNode, "IS_PRODUCT");
                }
            }
            //Packagers
            if (drug.packagers != null) {
                for (int i = 0; i < drug.packagers.size(); i++) {
                    Node packagersNode = g.addNode("Packager", "name", drug.packagers.get(i).name, "url",
                                                   drug.packagers.get(i).url);
                    g.addEdge(pharmacoeconomicsNode, packagersNode, "HAS_PACKAGER");
                }
            }
            //Manufacturers
            if (drug.manufacturers != null) {
                for (int i = 0; i < drug.manufacturers.size(); i++) {
                    Node manufacturersNode = g.addNode("Manufacturer", "name", drug.manufacturers.get(i).value, "url",
                                                       drug.manufacturers.get(i).url);
                    g.addEdge(pharmacoeconomicsNode, manufacturersNode, "HAS_MANUFACTURER");
                }
            }
            //Prices
            if (drug.prices != null) {
                for (int i = 0; i < drug.prices.size(); i++) {
                    Node pricesNode = g.addNode("Price", "description", drug.prices.get(i).description, "cost",
                                                drug.prices.get(i).cost.value, "currency",
                                                drug.prices.get(i).cost.currency);
                    setPropertyIfNotNull(pricesNode, "unit", drug.prices.get(i).unit);
                    g.update(pricesNode);
                    g.addEdge(pharmacoeconomicsNode, pricesNode, "COSTS");
                }
            }
            //Dosages
            if (drug.dosages != null) {
                for (int i = 0; i < drug.dosages.size(); i++) {
                    Node dosageNode = g.addNode("Dosage", "form", drug.dosages.get(i).form, "route",
                                                drug.dosages.get(i).route, "strength", drug.dosages.get(i).strength);
                    g.addEdge(pharmacoeconomicsNode, dosageNode, "HAS_DOSAGE");
                }
            }
            //Patents
            if (drug.patents != null) {
                for (int i = 0; i < drug.patents.size(); i++) {
                    Node patentNode = g.addNode("Patent", "number", drug.patents.get(i).number, "country",
                                                drug.patents.get(i).country, "approved", drug.patents.get(i).approved);
                    setPropertyIfNotNull(patentNode, "expires", drug.patents.get(i).expires);
                    setPropertyIfNotNull(patentNode, "pediatric_extension", drug.patents.get(i).pediatricExtension);
                    g.update(patentNode);
                    g.addEdge(pharmacoeconomicsNode, patentNode, "IS_PATENTED");
                }
            }
            //Categories
            if (drug.categories != null) {
                for (int i = 0; i < drug.categories.size(); i++) {
                    Node categoryNode = g.addNode("Category", "category", drug.categories.get(i).category, "mesh_id",
                                                  drug.categories.get(i).meshId);
                    g.addEdge(drugNode, categoryNode, "CATEGORIZED_IN");
                }
            }
            //calculated Properties
            if (drug.calculatedProperties != null) {
                for (int i = 0; i < drug.calculatedProperties.size(); i++) {
                    Node calculatedPropertyNode = g.addNode("Calculated_Property", "value",
                                                            drug.calculatedProperties.get(i).value, "kind",
                                                            drug.calculatedProperties.get(i).kind.toString(), "source",
                                                            drug.calculatedProperties.get(i).source.toString());
                    g.addEdge(drugNode, calculatedPropertyNode, "HAS_CALCULATED_PROPERTY");
                }
            }
            //experimental Properties
            if (drug.experimentalProperties != null) {
                for (int i = 0; i < drug.experimentalProperties.size(); i++) {
                    Node experimentalPropertyNode = g.addNode("Experimental_Property", "value",
                                                              drug.experimentalProperties.get(i).value, "kind",
                                                              drug.experimentalProperties.get(i).kind.toString(),
                                                              "source", drug.experimentalProperties.get(i).source);
                    g.addEdge(drugNode, experimentalPropertyNode, "HAS_EXPERIMENTAL_PROPERTY");
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
                Node affectedOrganismNode = g.addNode("Affected_Organism", "names", organismNames);
                g.addEdge(drugNode, affectedOrganismNode, "AFFECTS_ORG");
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
                        g.addEdge(drugNode, pathway_lookUp.get(drugPathway.smpdbId), "IS_IN_PATHWAY");

                    } else {
                        Node pathwaysNode = g.addNode("Pathway", "name", drug.pathways.get(i).name, "smpdbId",
                                                      drug.pathways.get(i).smpdbId, "category",
                                                      drug.pathways.get(i).category);
                        pathway_lookUp.put(drug.pathways.get(i).smpdbId, pathwaysNode);
                        g.addEdge(drugNode, pathwaysNode, "IS_IN_PATHWAY");
                        if (drug.pathways.get(i).enzymes != null) {
                            String enzymes = String.join("; ", drug.pathways.get(i).enzymes);
                            setPropertyIfNotNull(pathwaysNode, "enzymes", enzymes);
                        }
                    }
                }
            }
            //Food Interactions
            if (drug.foodInteractions != null) {
                for (int i = 0; i < drug.foodInteractions.size(); i++) {
                    String foodInteractions = String.join("; ", drug.foodInteractions.get(i));
                    Node foodInteractionsNode = g.addNode("Food_Interaction", "food_interaction", foodInteractions);
                    g.addEdge(drugNode, foodInteractionsNode, "HAS_FOOD_INTERACTION");
                }
            }
            //Sequences
            if (drug.sequences != null) {
                for (int i = 0; i < drug.sequences.size(); i++) {
                    Node sequencesNode = g.addNode("Sequence", "value", drug.sequences.get(i).value, "format",
                                                   drug.sequences.get(i).format);
                    g.addEdge(drugNode, sequencesNode, "HAS_SEQUENCE");
                }
            }
            //Targets->Polypeptide->Synonyms, ExternalIdentifiers, Pfams, GO-Classifiers, Organism
            if (drug.targets != null) {
                for (int i = 0; i < drug.targets.size(); i++) {
                    Target drugtarget = drug.targets.get(i);
                    Node targetNode = g.addNode("Target", "position", drugtarget.position, "id", drugtarget.id, "name",
                                                drugtarget.name);
                    setPropertyIfNotNull(targetNode, "organism", drugtarget.organism);
                    setPropertyIfNotNull(targetNode, "known_action", drugtarget.knownAction.value);
                    if (drugtarget.actions != null) {
                        String actions = String.join("; ", drugtarget.actions);
                        setPropertyIfNotNull(targetNode, "actions", actions);
                    }

                    createReferenceListNode(g, referenceList_lookUp, targetNode, drugtarget.references);
                    if (drugtarget.polypeptide != null) {
                        if (polypeptide_lookUp.containsKey(drugtarget.polypeptide.id)) {
                            g.addEdge(drugNode, polypeptide_lookUp.get(drugtarget.polypeptide.id), "IS_POLYPEPTIDE");
                        } else {
                            createPolypeptideNode(g, organism_lookUp, polypeptide_lookUp, targetNode,
                                                  drugtarget.polypeptide);
                        }
                    }
                    g.update(targetNode);
                    g.addEdge(drugNode, targetNode, "TARGETS");
                }
            }
            //Interactant -> Carrier, Enzyme, Target, Transporter
            //Enzyme
            if (drug.enzymes != null) {
                for (int i = 0; i < drug.enzymes.size(); i++) {
                    Enzyme drugEnzyme = drug.enzymes.get(i);
                    if (enzyme_lookUp.containsKey(drugEnzyme.id)) {
                        g.addEdge(drugNode, enzyme_lookUp.get(drugEnzyme.id), "IS_ENZYME");
                    } else {
                        Node enzymeNode = g.addNode("Enzyme", "id", drugEnzyme.id, "name", drugEnzyme.name, "organism",
                                                    drugEnzyme.organism);
                        setPropertyIfNotNull(enzymeNode, "position", drugEnzyme.position);
                        setPropertyIfNotNull(enzymeNode, "inhibition_strength", drugEnzyme.inhibitionStrength);
                        setPropertyIfNotNull(enzymeNode, "induction_strength", drugEnzyme.inductionStrength);
                        if (drugEnzyme.actions != null) {
                            String actions = String.join("; ", drugEnzyme.actions);
                            setPropertyIfNotNull(enzymeNode, "actions", actions);
                        }
                        createReferenceListNode(g, referenceList_lookUp, enzymeNode, drugEnzyme.references);
                        if (drugEnzyme.polypeptide != null) {
                            if (polypeptide_lookUp.containsKey(drugEnzyme.polypeptide.id)) {
                                g.addEdge(drugNode, polypeptide_lookUp.get(drugEnzyme.polypeptide.id),
                                          "IS_POLYPEPTIDE");
                            } else {
                                createPolypeptideNode(g, organism_lookUp, polypeptide_lookUp, enzymeNode,
                                                      drugEnzyme.polypeptide);
                            }
                        }
                        g.update(enzymeNode);
                        g.addEdge(drugNode, enzymeNode, "TARGETS");
                    }
                }
            }
            //Carrier
            if (drug.carriers != null) {
                for (int i = 0; i < drug.carriers.size(); i++) {
                    Carrier drugCarrier = drug.carriers.get(i);
                    if (carriers_lookUp.containsKey(drugCarrier.id)) {
                        g.addEdge(drugNode, carriers_lookUp.get(drugCarrier.id), "IS_CARRIER");
                    } else {
                        Node carriersNode = g.addNode("Carrier", "id", drugCarrier.id, "name", drugCarrier.name,
                                                      "position", drugCarrier.position);
                        setPropertyIfNotNull(carriersNode, "organism", drugCarrier.organism);
                        if (drugCarrier.actions != null) {
                            String actions = String.join("; ", drugCarrier.actions);
                            setPropertyIfNotNull(carriersNode, "actions", actions);
                        }
                        createReferenceListNode(g, referenceList_lookUp, carriersNode, drugCarrier.references);
                        if (drugCarrier.polypeptide != null) {
                            if (polypeptide_lookUp.containsKey(drugCarrier.polypeptide.id)) {
                                g.addEdge(drugNode, polypeptide_lookUp.get(drugCarrier.polypeptide.id),
                                          "IS_POLYPEPTIDE");
                            } else {
                                createPolypeptideNode(g, organism_lookUp, polypeptide_lookUp, carriersNode,
                                                      drugCarrier.polypeptide);
                            }
                        }
                        g.update(carriersNode);
                        g.addEdge(drugNode, carriersNode, "TARGETS");
                    }
                }
            }
            //Transporter
            if (drug.transporters != null) {
                for (int i = 0; i < drug.transporters.size(); i++) {
                    Transporter drugTransporter = drug.transporters.get(i);
                    if (transporters_lookUp.containsKey(drugTransporter.id)) {
                        g.addEdge(drugNode, transporters_lookUp.get(drugTransporter.id), "IS_TRANSPORTER");
                    } else {
                        Node transportersNode = g.addNode("Transporter", "id", drugTransporter.id, "name",
                                                          drugTransporter.name, "organism", drugTransporter.organism);
                        setPropertyIfNotNull(transportersNode, "position", drugTransporter.position);
                        if (drugTransporter.actions != null) {
                            String actions = String.join("; ", drugTransporter.actions);
                            setPropertyIfNotNull(transportersNode, "actions", actions);
                        }
                        createReferenceListNode(g, referenceList_lookUp, transportersNode, drugTransporter.references);
                        if (drugTransporter.polypeptide != null) {
                            if (polypeptide_lookUp.containsKey(drugTransporter.polypeptide.id)) {
                                g.addEdge(drugNode, polypeptide_lookUp.get(drugTransporter.polypeptide.id),
                                          "IS_POLYPEPTIDE");
                            } else {
                                createPolypeptideNode(g, organism_lookUp, polypeptide_lookUp, transportersNode,
                                                      drugTransporter.polypeptide);
                            }
                        }
                        g.update(transportersNode);
                        g.addEdge(drugNode, transportersNode, "TARGETS");
                    }
                }
            }
            //Reactions: Drug -> Reaction -> Reaction-Enzyme -> Metabolite
            if (drug.reactions != null) {
                for (int i = 0; i < drug.reactions.size(); i++) {
                    Node reactionsNode = g.addNode("Reaction");
                    setPropertyIfNotNull(reactionsNode, "sequence", drug.reactions.get(i).sequence);
                    MetaboliteTriple triple = new MetaboliteTriple();
                    triple.reactionsId = reactionsNode.getId();
                    triple.rightElementId = drug.reactions.get(i).rightElement.drugbankId;
                    triple.leftElementId = drug.reactions.get(i).leftElement.drugbankId;
                    metaboliteCache.add(triple);

                    if (drug.reactions.get(i).enzymes != null) {
                        for (int j = 0; j < drug.reactions.get(i).enzymes.size(); j++) {
                            Node reactionEnzymeNode = g.addNode("Reaction_Enzyme");
                            setPropertyIfNotNull(reactionEnzymeNode, "drugbank_id", drug.reactions.get(i).enzymes
                                    .get(j).drugbankId);
                            setPropertyIfNotNull(reactionEnzymeNode, "name", drug.reactions.get(i).enzymes.get(j).name);
                            setPropertyIfNotNull(reactionEnzymeNode, "uniprot_id", drug.reactions.get(i).enzymes
                                    .get(j).uniprotId);
                            g.update(reactionEnzymeNode);
                            g.addEdge(reactionEnzymeNode, reactionsNode, "IS_INFERRED_TO");
                        }
                    }
                    g.update(reactionsNode);
                }
            }
            //SNP-Effects
            if (drug.snpEffects != null) {
                for (int i = 0; i < drug.snpEffects.size(); i++) {
                    Node snpEffectsNode = g.addNode("SNP_Effect", "protein_name", drug.snpEffects.get(i).proteinName,
                                                    "gene_symbol", drug.snpEffects.get(i).geneSymbol, "uniprot_id",
                                                    drug.snpEffects.get(i).uniprotId);
                    setPropertyIfNotNull(snpEffectsNode, "rs_id", drug.snpEffects.get(i).rsId);
                    setPropertyIfNotNull(snpEffectsNode, "allele", drug.snpEffects.get(i).allele);
                    setPropertyIfNotNull(snpEffectsNode, "defining_change", drug.snpEffects.get(i).definingChange);
                    setPropertyIfNotNull(snpEffectsNode, "description", drug.snpEffects.get(i).description);
                    setPropertyIfNotNull(snpEffectsNode, "pubmed_id", drug.snpEffects.get(i).pubmedId);
                    g.update(snpEffectsNode);
                    g.addEdge(drugNode, snpEffectsNode, "HAS_SNP_EFFECT");
                }
            }
            //SNP-Adverse-Drug-Reactions
            if (drug.snpAdverseDrugReactions != null) {
                for (int i = 0; i < drug.snpAdverseDrugReactions.size(); i++) {
                    Node snpAdverseDrugReactionsNode = g.addNode("SNP_Adverse_Drug_Reaction", "protein_name",
                                                                 drug.snpAdverseDrugReactions.get(i).proteinName,
                                                                 "gene_symbol",
                                                                 drug.snpAdverseDrugReactions.get(i).geneSymbol,
                                                                 "uniprot_id",
                                                                 drug.snpAdverseDrugReactions.get(i).uniprotId);
                    setPropertyIfNotNull(snpAdverseDrugReactionsNode, "rs_Id",
                                         drug.snpAdverseDrugReactions.get(i).rsId);
                    setPropertyIfNotNull(snpAdverseDrugReactionsNode, "allele",
                                         drug.snpAdverseDrugReactions.get(i).allele);
                    setPropertyIfNotNull(snpAdverseDrugReactionsNode, "adverse_reaction",
                                         drug.snpAdverseDrugReactions.get(i).adverseReaction);
                    setPropertyIfNotNull(snpAdverseDrugReactionsNode, "description",
                                         drug.snpAdverseDrugReactions.get(i).description);
                    setPropertyIfNotNull(snpAdverseDrugReactionsNode, "pubmed_id",
                                         drug.snpAdverseDrugReactions.get(i).pubmedId);
                    g.update(snpAdverseDrugReactionsNode);
                    g.addEdge(drugNode, snpAdverseDrugReactionsNode, "HAS_SNP_ADVERSE_DRUG_REACTION");
                }
            }

            drugs.remove(drugIndex);
        }
        for (DrugInteractionTriple triple : drugInteractionCache) {
            if (drug_lookUp.containsKey(triple.drugBankIdTarget)) {
                Node source = drug_lookUp.get(triple.drugBankIdSource);
                Node target = drug_lookUp.get(triple.drugBankIdTarget);
                Edge drugInteractionEdge = g.addEdge(source, target, "INTERACTS_WITH_DRUG");
                drugInteractionEdge.setProperty("description", triple.description);
                g.update(drugInteractionEdge);
            } else {
                //Log-Message if key missing!
                //System.out.println("DrugInteraction-Key is missing! ID: " + triple.drugBankIdTarget);
            }

        }

        for (PathwayDrugTriple triple : pathwayDrugCache) {
            if (pathway_lookUp.containsKey(triple.drugBankIdTarget)) {
                Node source = pathway_lookUp.get(triple.smpdIdSource);
                Node target = drug_lookUp.get(triple.drugBankIdTarget);
                Edge pathwayDrugEdge = g.addEdge(source, target, "IS_PATHWAYDRUG");
                pathwayDrugEdge.setProperty("name", triple.description);
                g.update(pathwayDrugEdge);
            } else {
                //Log-Message if key missing!
                //System.out.println("Pathway-Key is missing! ID: " + triple.drugBankIdTarget);
            }
        }

        for (MetaboliteTriple triple : metaboliteCache) {
            if (metabolite_lookUp.containsKey(triple.leftElementId)) {
                Long source = metabolite_lookUp.get(triple.leftElementId);
                g.addEdge(source, triple.reactionsId, "SUBSTRATE_IN");
            } else if (drug_lookUp.containsKey(triple.leftElementId)) {
                Node source = drug_lookUp.get(triple.leftElementId);
                g.addEdge(source, triple.reactionsId, "SUBSTRATE_IN");
            } else {
                if (triple.leftElementId.startsWith("DBMET")) {
                    Node metaboliteNode = g.addNode("Metabolite", "drugbank_id", triple.leftElementId);
                    metabolite_lookUp.put(triple.leftElementId, metaboliteNode.getId());
                }
            }

            if (drug_lookUp.containsKey(triple.rightElementId)) {
                Node target = drug_lookUp.get(triple.rightElementId);
                g.addEdge(triple.reactionsId, target, "METABOLIZED_TO");
            } else if (metabolite_lookUp.containsKey(triple.rightElementId)) {
                Long target = metabolite_lookUp.get(triple.rightElementId);
                g.addEdge(triple.reactionsId, target, "METABOLIZED_TO");
            } else {
                if (triple.rightElementId.startsWith("DBMET")) {
                    Node metaboliteNode = g.addNode("Metabolite", "drugbank_id", triple.rightElementId);
                    metabolite_lookUp.put(triple.rightElementId, metaboliteNode.getId());

                }
            }
        }

        return true;
    }

    //Polypeptide
    private void createPolypeptideNode(Graph g, Map<String, Node> organism_lookUp, Map<String, Node> polypeptide_lookUp,
                                       Node parentNode, Polypeptide polypeptide) {
        if (polypeptide != null) {
            Node polypeptideNode = g.addNode("Polypeptide", "id", polypeptide.id, "name", polypeptide.name, "source",
                                             polypeptide.source);
            polypeptide_lookUp.put(polypeptide.id, polypeptideNode);
            setPropertyIfNotNull(polypeptideNode, "general_function", polypeptide.generalFunction);
            setPropertyIfNotNull(polypeptideNode, "specific_function", polypeptide.specificFunction);
            setPropertyIfNotNull(polypeptideNode, "gene_name", polypeptide.geneName);
            setPropertyIfNotNull(polypeptideNode, "locus", polypeptide.locus);
            setPropertyIfNotNull(polypeptideNode, "cellular_location", polypeptide.cellularLocation);
            setPropertyIfNotNull(polypeptideNode, "transmembrane_regions", polypeptide.transmembraneRegions);
            setPropertyIfNotNull(polypeptideNode, "signal_regions", polypeptide.signalRegions);
            setPropertyIfNotNull(polypeptideNode, "theoretical_pi", polypeptide.theoreticalPi);
            setPropertyIfNotNull(polypeptideNode, "molecular_weight", polypeptide.molecularWeight);
            setPropertyIfNotNull(polypeptideNode, "chromosome_location", polypeptide.chromosomeLocation);
            setPropertyIfNotNull(polypeptideNode, "aminoacid_sequence", polypeptide.aminoAcidSequence.value);
            setPropertyIfNotNull(polypeptideNode, "aminoacid_sequence_format", polypeptide.aminoAcidSequence.format);
            setPropertyIfNotNull(polypeptideNode, "gene_sequence", polypeptide.geneSequence.value);
            setPropertyIfNotNull(polypeptideNode, "gene_sequence_format", polypeptide.geneSequence.format);
            if (polypeptide.synonyms != null) {
                String synonyms = String.join("; ", polypeptide.synonyms);
                setPropertyIfNotNull(polypeptideNode, "synonyms", synonyms);
            }
            g.update(polypeptideNode);
            g.addEdge(parentNode, polypeptideNode, "IS_POLYPEPTIDE");
            if (polypeptide.externalIdentifiers != null) {
                for (int j = 0; j < polypeptide.externalIdentifiers.size(); j++) {
                    Node polypeptideExternalIdentifierNode = g.addNode("Polypeptide_External_Identifier", "resource",
                                                                       polypeptide.externalIdentifiers
                                                                               .get(j).resource.value, "identifier",
                                                                       polypeptide.externalIdentifiers
                                                                               .get(j).identifier);
                    g.addEdge(polypeptideNode, polypeptideExternalIdentifierNode,
                              "HAS_POLYPEPTIDE_EXTERNAL_IDENTIFIER");
                }
            }
            if (polypeptide.pfams != null) {
                for (int j = 0; j < polypeptide.pfams.size(); j++) {
                    Node pfamsNode = g.addNode("Pfam", "identifier", polypeptide.pfams.get(j).identifier, "name",
                                               polypeptide.pfams.get(j).name);
                    g.addEdge(polypeptideNode, pfamsNode, "HAS_PFAM");
                }
            }
            if (polypeptide.goClassifiers != null) {
                for (int j = 0; j < polypeptide.goClassifiers.size(); j++) {
                    Node goClassifiersNode = g.addNode("GO Classifier", "category",
                                                       polypeptide.goClassifiers.get(j).category, "description",
                                                       polypeptide.goClassifiers.get(j).description);
                    g.addEdge(polypeptideNode, goClassifiersNode, "HAS_GO_CLASSIFIER");
                }
            }
            if (polypeptide.organism != null) {
                Organism polypeptidesOrganism = polypeptide.organism;
                if (organism_lookUp.containsKey(polypeptidesOrganism.ncbiTaxonomyId)) {
                    g.addEdge(polypeptideNode, organism_lookUp.get(polypeptidesOrganism.ncbiTaxonomyId),
                              "HAS_ORGANISM");
                } else {
                    Node organismNode = g.addNode("Organism", "ncbi_taxonomy_id", polypeptide.organism.ncbiTaxonomyId,
                                                  "value", polypeptide.organism.value);
                    g.addEdge(polypeptideNode, organismNode, "HAS_ORGANISM");
                    organism_lookUp.put(polypeptidesOrganism.ncbiTaxonomyId, organismNode);
                }
            }
        }
    }

    //References
    private void createReferenceListNode(Graph g, Map<Object, Node> referenceList_lookUp, Node parent,
                                         ReferenceList references) {
        Node referenceListNode = null;
        if ((references.textbooks != null && references.textbooks.size() > 0) ||
            (references.articles != null && references.articles.size() > 0) ||
            (references.attachments != null && references.attachments.size() > 0) ||
            (references.links != null && references.links.size() > 0)) {
            referenceListNode = g.addNode("Reference");
            g.addEdge(parent, referenceListNode, "HAS_REFERENCE");
        }
        if (references.textbooks != null) {
            for (int j = 0; j < references.textbooks.size(); j++) {
                Textbook rf = references.textbooks.get(j);
                referenceListNode.setProperty("textbook", references.textbooks.get(j).refId);
                if (referenceList_lookUp.containsKey(rf.refId)) {
                    g.addEdge(referenceListNode, referenceList_lookUp.get(rf.refId), "HAS_TEXTBOOK");
                } else {
                    Node textbookNode = g.addNode("Textbook", "citation", rf.citation, "isbn", rf.isbn, "ref_id",
                                                  rf.refId);
                    g.addEdge(referenceListNode, textbookNode, "HAS_TEXTBOOK");
                }
            }
        }
        if (references.articles != null) {
            for (int j = 0; j < references.articles.size(); j++) {
                Article a = references.articles.get(j);
                referenceListNode.setProperty("article", a.refId);
                if (referenceList_lookUp.containsKey(a.refId)) {
                    g.addEdge(referenceListNode, referenceList_lookUp.get(a.refId), "HAS_ARTICLE");
                } else {
                    Node articleNode = g.addNode("Article", "citation", a.citation, "pubmed_id", a.pubmedId, "ref_id",
                                                 a.refId);
                    g.addEdge(referenceListNode, articleNode, "HAS_ARTICLE");
                }
            }
        }
        if (references.links != null) {
            for (int j = 0; j < references.links.size(); j++) {
                Link l = references.links.get(j);
                if (referenceList_lookUp.containsKey(l.refId)) {
                    g.addEdge(referenceListNode, referenceList_lookUp.get(l.refId), "HAS_LINK");
                } else {
                    Node linksNode = g.addNode("Link", "title", l.title, "url", l.url, "ref_id", l.refId);
                    g.addEdge(referenceListNode, linksNode, "HAS_LINK");
                }
            }
        }
        if (references.attachments != null) {
            for (int j = 0; j < references.attachments.size(); j++) {
                Attachment at = references.attachments.get(j);
                if (referenceList_lookUp.containsKey(at.refId)) {
                    g.addEdge(referenceListNode, referenceList_lookUp.get(at.refId), "HAS_ATTACHMENT");
                } else {
                    Node attachmentNode = g.addNode("Attachment", "title", at.title, "url", at.url, "ref_id", at.refId);
                    g.addEdge(referenceListNode, attachmentNode, "HAS_ATTACHEMENT");
                }
            }
        }
    }

    public void setPropertyIfNotNull(Node node, String propertyKey, Object value) {
        if (value != null) {
            node.setProperty(propertyKey, value);
        }
    }

    private void exportDrugStructures(final Graph graph, final List<DrugStructure> drugStructures) {
        for (DrugStructure drug : drugStructures)
            exportDrugStructure(graph, drug);
    }

    private void exportDrugStructure(final Graph graph, final DrugStructure drug) {
        createNodeFromModel(graph, drug);
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
