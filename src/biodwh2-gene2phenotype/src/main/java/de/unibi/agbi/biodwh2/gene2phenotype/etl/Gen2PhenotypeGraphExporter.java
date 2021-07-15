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
    private static final String[] FILE_LIST = new String[]{
            "SkinG2P.csv", "EyeG2P.csv", "DDG2P.csv", "CancerG2P.csv"
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(Gen2PhenotypeGraphExporter.class);

    public Gen2PhenotypeGraphExporter(Gen2PhenotypeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(Workspace workspace, Graph graph) throws ExporterException {
        for (GeneDiseasePair gdp : dataSource.geneDiseasePairs){
            LOGGER.debug("exporting " + gdp.getGeneSymbol() + "-" + gdp.getDiseaseName());
            Node genNode = createNode(graph, gdp.getGeneSymbol());
            genNode.setProperty("hgncId", gdp.getHgncId());
            genNode.setProperty("mim", gdp.getGeneMim());
            graph.update(genNode);

            Node diseaseNode = createNode(graph, gdp.getDiseaseName());
            diseaseNode.setProperty("mim", gdp.getDiseaseMim());
            graph.update(diseaseNode);

            Node genDiseaseNode = createNode(graph, gdp.getGeneSymbol() + "-" + gdp.getDiseaseName());
            genDiseaseNode.setProperty("confidence", gdp.getDiseaseConfidence());
            genDiseaseNode.setProperty("allelic_requirement", gdp.getAllelicRequirement());
            genDiseaseNode.setProperty("mutation_consequence", gdp.getMutationConsequence());
            genDiseaseNode.setProperty("panel", gdp.getG2Ppanel());
            genDiseaseNode.setProperty("entry date", gdp.getEntryDate());
            graph.update(genDiseaseNode);


            Node phenotypesNode = createNode(graph, "phenotypes");
            phenotypesNode.setProperty("phenotypes", gdp.getPhenotypes());
            graph.update(phenotypesNode);

            Node organListNode = createNode(graph, "organSpecificityList");
            organListNode.setProperty("organ_specificity_list", gdp.getOrganSpecificityList());
            graph.update(organListNode);

            graph.addEdge(genNode, genDiseaseNode, "CHANGES");
            graph.addEdge(genDiseaseNode, diseaseNode, "CAUSES");
            graph.addEdge(genDiseaseNode, phenotypesNode, "SHOWS");
            graph.addEdge(genDiseaseNode, organListNode, "AFFECTS");
        }
        LOGGER.debug("read " + graph.getNumberOfNodes() + " nodes and " + graph.getNumberOfEdges() + " edges");
        return true;
    }
}
