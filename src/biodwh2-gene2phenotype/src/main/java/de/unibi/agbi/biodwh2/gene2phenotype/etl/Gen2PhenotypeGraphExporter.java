package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.gene2phenotype.Gen2PhenotypeDataSource;
import de.unibi.agbi.biodwh2.gene2phenotype.model.GeneDiseasePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Gen2PhenotypeGraphExporter extends GraphExporter<Gen2PhenotypeDataSource> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Gen2PhenotypeGraphExporter.class);

    public Gen2PhenotypeGraphExporter(Gen2PhenotypeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(Workspace workspace, Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys("hgnc_symbol", "disease_name", "id");

        Map<String, Node> phenotypes = new HashMap<>(dataSource.geneDiseasePairs.size()*10);
        Map<String, Node> publications = new HashMap<>(dataSource.geneDiseasePairs.size());
        Map<String, Node> gens = new HashMap<>(dataSource.geneDiseasePairs.size());
        Map<String, Node> diseases = new HashMap<>(dataSource.geneDiseasePairs.size());

        for (GeneDiseasePair gdp : dataSource.geneDiseasePairs) {
            LOGGER.debug("exporting " + gdp.getGeneSymbol() + "-" + gdp.getDiseaseName());

            for (String phenotype : gdp.getPhenotypes()){
                if (!phenotypes.containsKey(phenotype)){
                    Node phenNode = createNode(graph, "Phenotype");
                    phenNode.setProperty("hpo_id", phenotype);
                    phenotypes.put(phenotype, phenNode);
                }
            }

            for (String pubmed_id : gdp.getPmids()){
                if (!publications.containsKey(pubmed_id)){
                    Node pubNode = createNode(graph, "Publication");
                    pubNode.setProperty("pubmed_id", pubmed_id);
                    publications.put(pubmed_id, pubNode);
                }
            }

            Node genNode;
            if (gens.containsKey(gdp.getGeneSymbol())){
                genNode = gens.get(gdp.getGeneSymbol());
            } else {
                genNode = createNode(graph, "Gene");
                genNode.setProperty("hgnc_symbol", gdp.getGeneSymbol());
                genNode.setProperty("hgnc_id", gdp.getHgncId());
                genNode.setProperty("mim", gdp.getGeneMim());
                genNode.setProperty("previous_symbols", gdp.getPrevSymbols());
                graph.update(genNode);
                gens.put(gdp.getGeneSymbol(), genNode);
            }


            Node diseaseNode;
            if (diseases.containsKey(gdp.getDiseaseName())){
                diseaseNode = diseases.get(gdp.getDiseaseName());
            } else {
                diseaseNode = createNode(graph, "Disease");
                diseaseNode.setProperty("disease_name", gdp.getDiseaseName());
                diseaseNode.setProperty("mim", gdp.getDiseaseMim());
                graph.update(diseaseNode);
                diseases.put(gdp.getDiseaseName(), diseaseNode);
            }

            Node genDiseaseNode = createNode(graph, "GeneDiseasePair");
            genDiseaseNode.setProperty("id", gdp.getGeneSymbol() + "-" + gdp.getDiseaseName());
            genDiseaseNode.setProperty("confidence", gdp.getDiseaseConfidence());
            genDiseaseNode.setProperty("allelic_requirement", gdp.getAllelicRequirement());
            genDiseaseNode.setProperty("mutation_consequence", gdp.getMutationConsequence());
            genDiseaseNode.setProperty("panel", gdp.getG2Ppanel());
            genDiseaseNode.setProperty("entry date", gdp.getEntryDate());
            genDiseaseNode.setProperty("organ_specificity_list", gdp.getOrganSpecificityList());
            graph.update(genDiseaseNode);

            graph.addEdge(genNode, genDiseaseNode, "MUTATES");
            graph.addEdge(genDiseaseNode, diseaseNode, "CAUSES");

            for (String phenotype : gdp.getPhenotypes()){
                graph.addEdge(genDiseaseNode, phenotypes.get(phenotype), "SHOWS");
            }
            for (String pubmed_id : gdp.getPmids()){
                graph.addEdge(genDiseaseNode, publications.get(pubmed_id), "PUBLISHED_IN");
            }

        }
        LOGGER.debug("read " + graph.getNumberOfNodes() + " nodes and " + graph.getNumberOfEdges() + " edges");
        return true;
    }
}
