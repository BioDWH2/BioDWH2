package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import de.unibi.agbi.biodwh2.core.vocabulary.UniProt;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

public class HGNCRDFExporter extends RDFExporter {
    private static final String HGNCDataUri = "https://www.genenames.org/data/";

    @Override
    public Model exportModel(DataSource dataSource) {
        Model model = createDefaultModel();
        for (Gene gene : ((HGNCDataSource) dataSource).genes)
            createGeneResource(model, gene);
        return model;
    }

    private static Resource createGeneResource(Model model, Gene gene) {
        Resource r = model.createResource(HGNCDataUri + "gene-symbol-report/#!/hgnc_id/" + gene.hgncId);
        r.addProperty(DCTerms.identifier, gene.hgncId);
        r.addProperty(RDFS.seeAlso, HGNCDataUri + "gene-symbol-report/#!/symbol/" + gene.symbol);
        r.addProperty(RDFS.seeAlso, "https://identifiers.org/hgnc:" + gene.hgncId);
        r.addProperty(RDFS.seeAlso, "https://identifiers.org/hgnc.symbol:" + gene.symbol);
        r.addProperty(RDF.type, UniProt.Gene);
        r.addProperty(SKOS.prefLabel, gene.symbol);
        return r;
    }

    @Override
    protected void setModelPrefixes(Model model) {
        super.setModelPrefixes(model);
        model.setNsPrefix("upCore", UniProt.Uri);
    }
}
