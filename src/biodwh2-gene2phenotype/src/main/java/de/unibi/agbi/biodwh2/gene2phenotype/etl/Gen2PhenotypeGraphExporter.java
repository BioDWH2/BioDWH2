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

public class Gen2PhenotypeGraphExporter extends GraphExporter<Gen2PhenotypeDataSource> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Gen2PhenotypeGraphExporter.class);

    public Gen2PhenotypeGraphExporter(Gen2PhenotypeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(Workspace workspace, Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys("hgnc_symbol", "disease_name", "id");
        for (GeneDiseasePair gdp : dataSource.geneDiseasePairs) {
            LOGGER.debug("exporting " + gdp.getGeneSymbol() + "-" + gdp.getDiseaseName());

            Node genNode = createNode(graph, "Gene");
            genNode.setProperty("hgnc_symbol", gdp.getGeneSymbol());
            genNode.setProperty("hgnc_id", gdp.getHgncId());
            genNode.setProperty("mim", gdp.getGeneMim());
            genNode.setProperty("previous_symbols", gdp.getPrevSymbols());
            graph.update(genNode);

            Node diseaseNode = createNode(graph, "Disease");
            diseaseNode.setProperty("disease_name", gdp.getDiseaseName());
            diseaseNode.setProperty("mim", gdp.getDiseaseMim());
            graph.update(diseaseNode);

            Node genDiseaseNode = createNode(graph, "GeneDiseasePair");
            genDiseaseNode.setProperty("id", gdp.getGeneSymbol() + "-" + gdp.getDiseaseName());
            genDiseaseNode.setProperty("confidence", gdp.getDiseaseConfidence());
            genDiseaseNode.setProperty("allelic_requirement", gdp.getAllelicRequirement());
            genDiseaseNode.setProperty("mutation_consequence", gdp.getMutationConsequence());
            genDiseaseNode.setProperty("panel", gdp.getG2Ppanel());
            genDiseaseNode.setProperty("entry date", gdp.getEntryDate());
            genDiseaseNode.setProperty("phenotypes", gdp.getPhenotypes());
            genDiseaseNode.setProperty("organ_specificity_list", gdp.getOrganSpecificityList());
            genDiseaseNode.setProperty("pubmed_ids", gdp.getPmids());
            graph.update(genDiseaseNode);

            graph.addEdge(genNode, genDiseaseNode, "MUTATES");
            graph.addEdge(genDiseaseNode, diseaseNode, "CAUSES");
        }
        LOGGER.debug("read " + graph.getNumberOfNodes() + " nodes and " + graph.getNumberOfEdges() + " edges");
        return true;
    }
}
