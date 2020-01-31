package de.unibi.agbi.biodwh2.pharmgkb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.pharmgkb.PharmGKBDataSource;
import de.unibi.agbi.biodwh2.pharmgkb.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class PharmGKBGraphExporter extends GraphExporter {

    private static final Logger logger = LoggerFactory.getLogger(DataSource.class);

    //Gene, Chemical, Drug, Phenotype, DrugLabel, Variants Ids
    public HashMap<String, Node> pharmgkb_ids = new HashMap<String, Node>();

    public HashMap<String, Node> genes_symbol = new HashMap<String, Node>();
    public HashMap<String, Node> phenotypes_name = new HashMap<String, Node>();
    public HashMap<String, Node> variants_name = new HashMap<String, Node>();
    public HashMap<String, Node> chemicals_name = new HashMap<String, Node>();
    public HashMap<String, Node> genotype_phenotype_id = new HashMap<String, Node>();
    public HashMap<String, Node> study_parameters_id = new HashMap<String, Node>();
    public HashMap<String, Node> variant_annotation_id = new HashMap<String, Node>();

    @Override
    protected Graph exportGraph(DataSource dataSource) {
        Graph g = new Graph();
        long id = 0;

        //Primary Data
        id = addNodes(((PharmGKBDataSource) dataSource).genes, g, id, Gene.class);
        id = addNodes(((PharmGKBDataSource) dataSource).chemicals, g, id, Chemical.class);
        id = addNodes(((PharmGKBDataSource) dataSource).drugs, g, id, Drug.class);
        id = addNodes(((PharmGKBDataSource) dataSource).phenotyps, g, id, Phenotyp.class);
        id = addNodes(((PharmGKBDataSource) dataSource).variants, g, id, Variant.class);

        //Annotation Data
        id = addNodes(((PharmGKBDataSource) dataSource).clinicalAnnotations, g, id, ClinicalAnnotation.class);
        id = addNodes(((PharmGKBDataSource) dataSource).studyParameters, g, id, StudyParameters.class);

        id = addNodes(((PharmGKBDataSource) dataSource).drugLabels, g, id, DrugLabel.class);
        id = addNodes(((PharmGKBDataSource) dataSource).drugLabelsByGenes, g, id, DrugLabelsByGene.class);

        id = addNodes(((PharmGKBDataSource) dataSource).automatedAnnotations, g, id, AutomatedAnnotation.class);
        id = addNodes(((PharmGKBDataSource) dataSource).clinicalVariants, g, id, ClinicalVariants.class);

        id = addNodes(((PharmGKBDataSource) dataSource).variantDrugAnnotations, g, id, VariantDrugAnnotations.class);
        id = addNodes(((PharmGKBDataSource) dataSource).variantFunctionalAnalysisAnnotations, g, id,
                      VariantFunctionalAnalysisAnnotation.class);
        id = addNodes(((PharmGKBDataSource) dataSource).variantPhenotypeAnnotations, g, id,
                      VariantPhenotypeAnnotation.class);
        id = addNodes(((PharmGKBDataSource) dataSource).clinicalAnnotationMetadata, g, id,
                      ClinicalAnnotationMetadata.class);

        id = addNodes(((PharmGKBDataSource) dataSource).occurrences, g, id, Occurrence.class);

        for (String keyName : ((PharmGKBDataSource) dataSource).pathways.keySet()) {
            id = addPathwayNode(((PharmGKBDataSource) dataSource).pathways.get(keyName), g, id, Pathway.class,
                                keyName.split("-")[1]);
        }

        return g;
    }

    public <T> long addNodes(List<T> list, Graph g, long id, Class<T> clazz) {

        for (int i = 0; i < list.size(); i++) {

            Field[] fields = list.get(i).getClass().getDeclaredFields();
            String[] className = list.get(i).getClass().toString().split("\\.");

            Node node = new Node(id, "PharmGKB_" + className[className.length - 1]);

            for (Field field : fields) {
                try {
                    T object = list.get(i);
                    String value = (String) field.get(object);

                    if (value != null) {
                        value = replaceInvalidXMLCharacters(value);
                    }

                    node.setProperty(field.getName(), value);

                    if (field.getName().equals("pharmgkb_accession_id")) {
                        pharmgkb_ids.put(value, node);
                    } else if (field.getName().equals("name") && className[className.length - 1].equals("Chemical")) {
                        chemicals_name.put(value, node);
                    } else if (field.getName().equals("study_parameters_id") && className[className.length - 1].equals(
                            "StudyParameters")) {
                        study_parameters_id.put(value, node);
                    } else if (field.getName().equals("genotype_phenotype_id") &&
                               className[className.length - 1].equals("ClinicalAnnotation")) {
                        genotype_phenotype_id.put(value, node);
                    } else if (field.getName().equals("variant_id") && className[className.length - 1].equals(
                            "Variant")) {
                        pharmgkb_ids.put(value, node);
                    } else if (field.getName().equals("variant_name") && className[className.length - 1].equals(
                            "Variant")) {
                        variants_name.put(value, node);
                    } else if (field.getName().equals("gene_ids") && className[className.length - 1].equals(
                            "Variant")) {
                        if (value != null) {
                            for (String geneId : value.split(",")) {
                                addEdgeToGraph(node, pharmgkb_ids.get(geneId), "has_gene", g);
                            }
                        }
                    } else if (field.getName().equals("symbol") && className[className.length - 1].equals("Gene")) {
                        genes_symbol.put(value, node);
                    } else if (field.getName().equals("name") && className[className.length - 1].equals("Phenotyp")) {
                        phenotypes_name.put(value, node);
                    } else if (field.getName().equals("gene_id") && className[className.length - 1].equals(
                            "DrugLabelsByGene")) {
                        addEdgeToGraph(node, pharmgkb_ids.get(value), "has_gene", g);
                    } else if (field.getName().equals("label_ids") && className[className.length - 1].equals(
                            "DrugLabelsByGene")) {
                        for (String labelId : value.split(";")) {
                            addEdgeToGraph(node, pharmgkb_ids.get(labelId), "has_drug_label", g);
                        }
                    } else if (field.getName().equals("genotype_phenotypes_id") &&
                               className[className.length - 1].equals("ClinicalAnnotationMetadata")) {
                        for (String gpId : value.split("\",\"")) {
                            addEdgeToGraph(node, genotype_phenotype_id.get(gpId.replace("\"", "")), "has_drug_label",
                                           g);
                        }
                    } else if (field.getName().equals("gene") && className[className.length - 1].equals(
                            "ClinicalAnnotationMetadata")) {
                        if (value != null) {
                            if (value.contains("\"")) {
                                for (String geneId : value.split("\",\"")) {
                                    String gid = geneId.split("\\(")[1].replace(")", "").replace("\"", "");
                                    addEdgeToGraph(node, pharmgkb_ids.get(gid), "has_gene", g);
                                }
                            } else {
                                String gid = value.split("\\(")[1].replace(")", "");
                                addEdgeToGraph(node, pharmgkb_ids.get(gid), "has_gene", g);
                            }
                        }
                    } else if (field.getName().equals("gene_ids") && className[className.length - 1].equals(
                            "AutomatedAnnotation")) {
                        if (value != null) {
                            for (String geneId : value.split(",")) {
                                addEdgeToGraph(node, pharmgkb_ids.get(geneId), "has_gene", g);
                            }
                        }
                    } else if (field.getName().equals("chemical_id") && className[className.length - 1].equals(
                            "AutomatedAnnotation")) {
                        if (value != null) {
                            if (pharmgkb_ids.containsKey(value)) {
                                addEdgeToGraph(node, pharmgkb_ids.get(value), "has_chemical", g);
                            } else {
                                logger.warn("Chemical not found: " + value);
                            }
                        }
                    } else if (field.getName().equals("variation_id") && className[className.length - 1].equals(
                            "AutomatedAnnotation")) {
                        if (value != null) {
                            if (value.contains("rs")) {
                                if (variants_name.containsKey(value)) {
                                    addEdgeToGraph(node, variants_name.get(value), "has_variation", g);
                                } else {
                                    logger.warn("Chemical not found: " + value);
                                }
                            } else {
                                if (pharmgkb_ids.containsKey(value)) {
                                    addEdgeToGraph(node, pharmgkb_ids.get(value), "has_variation", g);
                                } else {
                                    logger.warn("Chemical not found: " + value);
                                }
                            }

                        }
                    } else if (field.getName().equals("gene") && className[className.length - 1].equals(
                            "ClinicalVariants")) {
                        if (value != null) {
                            for (String geneId : value.split(",")) {
                                addEdgeToGraph(node, genes_symbol.get(geneId), "has_gene", g);
                            }
                        }
                    } else if (field.getName().equals("chemicals") && className[className.length - 1].equals(
                            "ClinicalVariants")) {
                        if (value != null) {
                            for (String chemical : value.split(",")) {
                                if (chemicals_name.containsKey(chemical)) {
                                    addEdgeToGraph(node, chemicals_name.get(chemical), "has_chemical", g);
                                } else {
                                    logger.warn("Chemical not found: " + chemical);
                                }
                            }
                        }
                    } else if (field.getName().equals("variant") && className[className.length - 1].equals(
                            "ClinicalVariants")) {
                        if (value != null) {
                            if (value.contains("rs")) {
                                if (variants_name.containsKey(value)) {
                                    addEdgeToGraph(node, variants_name.get(value), "has_variation", g);
                                } else {
                                    logger.warn("Variant name not found: " + value);
                                }
                            } else {
                                logger.warn("Not an rs number " + value);
                            }
                        }
                    } else if (field.getName().equals("chemical") && (className[className.length - 1].equals(
                            "VariantDrugAnnotations") || className[className.length - 1].equals(
                            "VariantPhenotypeAnnotation") || className[className.length - 1].equals(
                            "VariantFunctionalAnalysisAnnotation"))) {
                        if (value != null) {
                            if (value.contains("\"")) {
                                for (String chemicalId : value.split("\",\"")) {
                                    String[] partList = chemicalId.split("\\(");
                                    if (partList.length > 2) {
                                        String cid = partList[partList.length - 1].replace(")", "");
                                        if (pharmgkb_ids.containsKey(cid)) {
                                            addEdgeToGraph(node, pharmgkb_ids.get(cid), "has_chemical", g);
                                        } else {
                                            logger.warn("Chemical ID not found: " + cid);
                                        }
                                    } else {
                                        String cid = partList[1].replace(")", "").replace("\"", "");
                                        if (pharmgkb_ids.containsKey(cid)) {
                                            addEdgeToGraph(node, pharmgkb_ids.get(cid), "has_chemical", g);
                                        } else {
                                            logger.warn("Chemical ID not found: " + cid);
                                        }
                                    }
                                }
                            } else {
                                String[] partList = value.split("\\(");
                                if (partList.length > 2) {
                                    String gid = partList[partList.length - 1].replace(")", "");
                                    addEdgeToGraph(node, pharmgkb_ids.get(gid), "has_chemical", g);
                                } else {
                                    String gid = partList[1].replace(")", "");
                                    addEdgeToGraph(node, pharmgkb_ids.get(gid), "has_chemical", g);
                                }
                            }
                        }
                    } else if (field.getName().equals("gene") && (className[className.length - 1].equals(
                            "VariantDrugAnnotations") || className[className.length - 1].equals(
                            "VariantPhenotypeAnnotation") || className[className.length - 1].equals(
                            "VariantFunctionalAnalysisAnnotation"))) {
                        if (value != null) {
                            if (value.contains("\"")) {
                                for (String geneId : value.split("\",\"")) {
                                    String gid = geneId.split("\\(")[1].replace(")", "").replace("\"", "");
                                    addEdgeToGraph(node, pharmgkb_ids.get(gid), "has_gene", g);
                                }
                            } else {
                                    String gid = value.split("\\(")[1].replace(")", "");
                                    addEdgeToGraph(node, pharmgkb_ids.get(gid), "has_gene", g);
                            }
                        }
                    } else if (field.getName().equals("study_parameters") && (className[className.length - 1].equals(
                            "VariantDrugAnnotations") || className[className.length - 1].equals(
                            "VariantPhenotypeAnnotation") || className[className.length - 1].equals(
                            "VariantFunctionalAnalysisAnnotation"))) {
                        if (value != null) {
                            for (String gpId : value.split("\",\"")) {
                                addEdgeToGraph(node, study_parameters_id.get(gpId.replace("\"", "")),
                                               "has_study_parameter", g);
                            }
                        }
                    } else if (field.getName().equals("variant") && (className[className.length - 1].equals(
                            "VariantDrugAnnotations") || className[className.length - 1].equals(
                            "VariantPhenotypeAnnotation") || className[className.length - 1].equals(
                            "VariantFunctionalAnalysisAnnotation"))) {
                        if (value != null) {
                            if (value.contains("rs") && variants_name.containsKey(value)) {
                                addEdgeToGraph(node, variants_name.get(value), "has_variation", g);
                            } else {
                                logger.warn("Chemical not found: " + value);
                            }
                        }
                    } else if (field.getName().equals("annotation_id") && (className[className.length - 1].equals(
                            "VariantDrugAnnotations") || className[className.length - 1].equals(
                            "VariantPhenotypeAnnotation") || className[className.length - 1].equals(
                            "VariantFunctionalAnalysisAnnotation"))) {
                        variant_annotation_id.put(value, node);
                    } else if (field.getName().equals("related_chemicals") && className[className.length - 1].equals(
                            "ClinicalAnnotationMetadata")) {
                        if (value != null) {
                            if (value.contains("\"")) {
                                for (String chemicalId : value.split("\",\"")) {
                                    String[] partList = chemicalId.split("\\(");
                                    if (partList.length > 2) {
                                        String cid = partList[partList.length - 1].replace(")", "");
                                        if (pharmgkb_ids.containsKey(cid)) {
                                            addEdgeToGraph(node, pharmgkb_ids.get(cid), "has_chemical", g);
                                        } else {
                                            logger.warn("Chemical ID not found: " + cid);
                                        }
                                    } else {
                                        String cid = partList[1].replace(")", "").replace("\"", "");
                                        if (pharmgkb_ids.containsKey(cid)) {
                                            addEdgeToGraph(node, pharmgkb_ids.get(cid), "has_chemical", g);
                                        } else {
                                            logger.warn("Chemical ID not found: " + cid);
                                        }
                                    }
                                }
                            } else {
                                String[] partList = value.split("\\(");
                                if (partList.length > 2) {
                                    String cid = partList[partList.length - 1].replace(")", "");
                                    addEdgeToGraph(node, pharmgkb_ids.get(cid), "has_chemical", g);
                                } else {
                                    String cid = partList[1].replace(")", "");
                                    addEdgeToGraph(node, pharmgkb_ids.get(cid), "has_chemical", g);
                                }
                            }
                        }
                    } else if (field.getName().equals("gene") && className[className.length - 1].equals(
                            "ClinicalAnnotationMetadata")) {
                        if (value != null) {
                            if (value.contains("\"")) {
                                for (String geneId : value.split("\",\"")) {
                                    String gid = geneId.split("\\(")[1].replace(")", "").replace("\"", "");
                                    addEdgeToGraph(node, pharmgkb_ids.get(gid), "has_gene", g);
                                }
                            } else {
                                String gid = value.split("\\(")[1].replace(")", "");
                                addEdgeToGraph(node, pharmgkb_ids.get(gid), "has_gene", g);
                            }
                        }
                    } else if (field.getName().equals("genotype_phenotypes_id") &&
                               className[className.length - 1].equals("ClinicalAnnotationMetadata")) {
                        if (value != null) {
                            for (String gpId : value.split("\",\"")) {
                                addEdgeToGraph(node, genotype_phenotype_id.get(gpId.replace("\"", "")),
                                               "has_genotype_phenotype", g);
                            }
                        }
                    } else if (field.getName().equals("variant_annotations_id") &&
                               className[className.length - 1].equals("ClinicalAnnotationMetadata")) {
                        if (value != null) {
                            for (String gpId : value.split("\",\"")) {
                                addEdgeToGraph(node, variant_annotation_id.get(gpId.replace("\"", "")),
                                               "has_genotype_phenotype", g);
                            }
                        }
                    } else if (field.getName().equals("location") && className[className.length - 1].equals(
                            "ClinicalAnnotationMetadata")) {
                        if (value != null && value.contains("rs")) {
                            for (String variantName : value.split(",")) {
                                if (variants_name.containsKey(variantName)) {
                                    addEdgeToGraph(node, variants_name.get(variantName), "has_variation", g);
                                } else {
                                    logger.warn("Variant name not found: " + variantName);
                                }
                            }
                        }
                    } else if (field.getName().equals("object_id") && className[className.length - 1].equals(
                            "Occurrence")) {
                        if (pharmgkb_ids.containsKey(value)) {
                            addEdgeToGraph(pharmgkb_ids.get(value), node, "has_occurrence", g);
                        } else {
                            logger.warn("Pharmgkb ID not found: " + value);
                        }
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();

                }
            }
            g.addNode(node);
            id += 1;
        }
        return id;
    }

    public <T> long addPathwayNode(List<T> list, Graph g, long id, Class<T> clazz, String label) {

        for (int i = 0; i < list.size(); i++) {

            Field[] fields = list.get(i).getClass().getDeclaredFields();
            String[] className = list.get(i).getClass().toString().split("\\.");

            Node node = new Node(id, "PharmGKB_" + label);

            for (Field field : fields) {
                try {
                    T object = list.get(i);
                    String value = (String) field.get(object);
                    node.setProperty(field.getName(), value);


                } catch (IllegalAccessException e) {
                    e.printStackTrace();

                }
            }
            g.addNode(node);
            id += 1;
        }
        return id;
    }

    public void addEdgeToGraph(Node startNode, Node endNode, String label, Graph g) {
        Edge edge = new Edge(startNode, endNode, label);
        g.addEdge(edge);
    }

    public static String stripInvalidXMLCharacters(String in) {

        String xml10pattern =
                "[^" + "\u0009\r\n" + "\u0020-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]";
        return in.replaceAll(xml10pattern, "").trim();
    }

    public static String replaceInvalidXMLCharacters(String in) {

        in = in.replace("&", "&amp;");
        in = in.replace("\"", "&quot;");
        in = in.replace("\'", "&apos;");
        in = in.replace("<", "&lt;");
        in = in.replace(">", "&gt;");
        in = in.replaceAll("\\x02", "&#2;");
        in = in.replaceAll("\\x04", "&#4;");

        return in;
    }


}
