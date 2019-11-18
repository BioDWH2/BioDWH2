package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import de.unibi.agbi.biodwh2.core.vocabulary.KalisNs;
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
    private static final String kalisNs = "https://rdf.kalis-amts.de/ns#";
    private static final String bio2rdf = "http://bio2rdf.org/wormbase_vocabulary:approved-gene-name";

    //TODO: Hilfsfunktionen für Properties. Look up table
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
        r.addProperty(RDF.type, UniProt.Gene);
        r.addProperty(SKOS.prefLabel, gene.symbol);
        if (gene.status != null)
            r.addProperty(UniProt.Status, gene.status);
        if (gene.name != null)
            r.addProperty(UniProt.name, gene.name);
        if (gene.aliasName != null)
            r.addProperty(UniProt.alias, gene.aliasName);
        if (gene.locusGroup != null)
            r.addProperty(KalisNs.locusGroupProperty, gene.locusGroup);
        if (gene.locusType != null)
            r.addProperty(KalisNs.locusTypeProperty, gene.locusType);
        if (gene.location != null)
            r.addProperty(KalisNs.locationProperty, gene.location);
        if (gene.locationSortable != null)
            r.addProperty(KalisNs.locationSortableProperty, gene.locationSortable);
        if (gene.aliasSymbol != null)
            r.addProperty(KalisNs.aliasSymbolProperty, gene.aliasSymbol);
        if (gene.aliasName != null)
            r.addProperty(KalisNs.aliasNameProperty, gene.aliasName);
        if (gene.prevSymbol != null)
            r.addProperty(KalisNs.prevSymbolProperty, gene.prevSymbol);
        if (gene.prevName != null)
            r.addProperty(KalisNs.prevNameProperty, gene.prevName);
        if (gene.geneFamily != null)
            r.addProperty(KalisNs.geneFamilyProperty, gene.geneFamily);
        if (gene.geneFamilyId != null)
            r.addProperty(KalisNs.geneFamilyIdProperty, gene.geneFamilyId);
        if (gene.dateApprovedReserved != null)
            r.addProperty(KalisNs.dateApprovedReservedProperty, gene.dateApprovedReserved);
        if (gene.dateSymbolChanged != null)
            r.addProperty(KalisNs.dateSymbolChangedProperty, gene.dateSymbolChanged);
        if (gene.dateNameChanged != null)
            r.addProperty(KalisNs.dateNameChangedProperty, gene.dateNameChanged);
        if (gene.dateModified != null)
            r.addProperty(KalisNs.dateModifiedProperty, gene.dateModified);
        if (gene.entrezId != null)
            r.addProperty(KalisNs.entrezIdProperty, gene.entrezId);
        if (gene.ensemblGeneId != null)
            r.addProperty(KalisNs.ensembleGeneIdProperty, gene.ensemblGeneId);
        if (gene.vegaId != null)
            r.addProperty(KalisNs.vegaIdProperty, gene.vegaId);
        if (gene.ucscId != null)
            r.addProperty(KalisNs.ucscIdProperty, gene.ucscId);
        if (gene.ena != null)
            r.addProperty(KalisNs.enaProperty, gene.ena);
        if (gene.refseqAccession != null)
            r.addProperty(KalisNs.refsecAccessionProperty, gene.refseqAccession);
        if (gene.ccdsId != null)
            r.addProperty(KalisNs.ccdsIdProperty, gene.ccdsId);
        if (gene.uniprotIds != null)
            r.addProperty(KalisNs.uniprotIdsProperty, gene.uniprotIds);
        if (gene.pubmedId != null)
            r.addProperty(KalisNs.pubmedIdProperty, gene.pubmedId);
        if (gene.mgdId != null)
            r.addProperty(KalisNs.mgdIdProperty, gene.mgdId);
        if (gene.rgdId != null)
            r.addProperty(KalisNs.rgdIdProperty, gene.rgdId);
        if (gene.lsdb != null)
            r.addProperty(KalisNs.lsdbProperty, gene.lsdb);
        if (gene.cosmic != null)
            r.addProperty(KalisNs.cosmicProperty, gene.cosmic);
        if (gene.omimId != null)
            r.addProperty(KalisNs.omimIdProperty, gene.omimId);
        if (gene.mirbase != null)
            r.addProperty(KalisNs.mirbaseProperty, gene.mirbase);
        if (gene.homeodb != null)
            r.addProperty(KalisNs.homeodbProperty, gene.homeodb);
        if (gene.snornabase != null)
            r.addProperty(KalisNs.snornabaseProperty, gene.snornabase);
        if (gene.bioparadigmsSlc != null)
            r.addProperty(KalisNs.bioparadigmsSlcProperty, gene.bioparadigmsSlc);
        if (gene.orphanet != null)
            r.addProperty(KalisNs.orphanetProperty, gene.orphanet);
        if (gene.pseudogeneOrg != null)
            r.addProperty(KalisNs.pseudogeneProperty, gene.pseudogeneOrg);
        if (gene.hordeId != null)
            r.addProperty(KalisNs.hordeIdProperty, gene.hordeId);
        if (gene.merops != null)
            r.addProperty(KalisNs.meropsProperty, gene.merops);
        if (gene.imgt != null)
            r.addProperty(KalisNs.imgtProperty, gene.imgt);
        if (gene.iuphar != null)
            r.addProperty(KalisNs.iupharProperty, gene.iuphar);
        if (gene.kznfGeneCatalog != null)
            r.addProperty(KalisNs.kznfGeneCatalogProperty, gene.kznfGeneCatalog);
        if (gene.mamitTrnaDb != null)
            r.addProperty(KalisNs.mamitTrnadbProperty, gene.mamitTrnaDb);
        if (gene.cd != null)
            r.addProperty(KalisNs.cdProperty, gene.cd);
        if (gene.lncrnaDb != null)
            r.addProperty(KalisNs.lncrnadbProperty, gene.lncrnaDb);
        if (gene.enzymeId != null)
            r.addProperty(KalisNs.enzymeIdProperty, gene.enzymeId);
        if (gene.intermediateFilamentDb != null)
            r.addProperty(KalisNs.intermediateFilamentDbProperty, gene.intermediateFilamentDb);
        if (gene.rnaCentralIds != null)
            r.addProperty(KalisNs.rnaCentralIdsProperty, gene.rnaCentralIds);
        if (gene.lnciPedia != null)
            r.addProperty(KalisNs.lncipediaProperty, gene.lnciPedia);
        if (gene.gtrnaDb != null)
            r.addProperty(KalisNs.gtrnadbProperty, gene.gtrnaDb);
        r.addProperty(RDFS.seeAlso, HGNCDataUri + "gene-symbol-report/#!/symbol/" + gene.symbol);
        r.addProperty(RDFS.seeAlso, "https://identifiers.org/hgnc.symbol:" + gene.symbol);
        r.addProperty(RDFS.seeAlso, "https://identifiers.org/hgnc:" + gene.hgncId);
        return r;
    }


    @Override
    protected void setModelPrefixes(Model model) {
        super.setModelPrefixes(model);
        model.setNsPrefix("upCore", UniProt.Uri);
    }
}
