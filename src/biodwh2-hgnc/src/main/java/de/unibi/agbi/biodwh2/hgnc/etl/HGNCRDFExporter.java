package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.vocabulary.*;
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
    public Model exportModel(DataSource dataSource) throws ExporterException {
        Model model = createDefaultModel();
        for (Gene gene : ((HGNCDataSource) dataSource).genes)
            createGeneResource(model, gene);
        return model;
    }

    private static Resource createGeneResource(Model model, Gene gene) {
        Resource r = model.createResource(HGNCDataUri + "gene-symbol-report/#!/hgnc_id/" + gene.hgncId);
        r.addProperty(DCTerms.identifier, gene.hgncId);
        r.addProperty(RDF.type, UniProt.Gene);
        if (gene.symbol != null) {
            r.addProperty(SKOS.prefLabel, gene.symbol);
            r.addProperty(Hgnc.approvedSymbol, gene.symbol);
        }
        if (gene.status != null) {
            r.addProperty(UniProt.Status, gene.status);
            r.addProperty(Hgnc.status, gene.status);
        }
        if (gene.name != null) {
            r.addProperty(UniProt.name, gene.name);
            r.addProperty(BioModels.name, gene.name);
            r.addProperty(Hgnc.approvedName, gene.name);
        }
        if (gene.aliasName != null)
            r.addProperty(Hgnc.nameSynonym, gene.aliasName);
        if (gene.locusGroup != null)
            r.addProperty(KalisNs.locusGroupHGNCProperty, gene.locusGroup);
        if (gene.locusType != null)
            r.addProperty(KalisNs.locusTypeHGNCProperty, gene.locusType);
        if (gene.location != null) {
            r.addProperty(Faldo.location, gene.location);
            r.addProperty(Hgnc.chromosome, gene.location);
        }
        if (gene.locationSortable != null)
            r.addProperty(KalisNs.locationSortableHGNCProperty, gene.locationSortable);
        if (gene.aliasSymbol != null)
            r.addProperty(KalisNs.aliasSymbolHGNCProperty, gene.aliasSymbol);
        if (gene.prevSymbol != null)
            r.addProperty(Hgnc.prevSymbol, gene.prevSymbol);
        if (gene.prevName != null)
            r.addProperty(Hgnc.prevName, gene.prevName);
        if (gene.geneFamily != null) {
            r.addProperty(UniProt.familyMembershipStatement, gene.geneFamily);
            r.addProperty(Hgnc.geneFamilyDiscription, gene.geneFamily);
        }
        if (gene.geneFamilyId != null) {
            r.addProperty(KalisNs.geneFamilyIdHGNCProperty, gene.geneFamilyId);
            r.addProperty(Hgnc.geneFamilyTag, gene.geneFamily);
        }
        if (gene.dateApprovedReserved != null)
            r.addProperty(Hgnc.dateApproved, gene.dateApprovedReserved);
        if (gene.dateSymbolChanged != null)
            r.addProperty(Hgnc.dateSymbolChanged, gene.dateSymbolChanged);
        if (gene.dateNameChanged != null)
            r.addProperty(Hgnc.dateNameChanged, gene.dateNameChanged);
        if (gene.dateModified != null)
            r.addProperty(Hgnc.dateModified, gene.dateModified);
        if (gene.entrezId != null)
            r.addProperty(GeneOntology.hasAlternativeId, gene.entrezId);
        if (gene.ensemblGeneId != null)
            r.addProperty(Hgnc.ensemblId, gene.ensemblGeneId);
        if (gene.vegaId != null)
            r.addProperty(Hgnc.vegaId, gene.vegaId);
        if (gene.ucscId != null)
            r.addProperty(Hgnc.ucscId, gene.ucscId);
        if (gene.ena != null)
            r.addProperty(KalisNs.enaHGNCProperty, gene.ena);
        if (gene.refseqAccession != null)
            r.addProperty(Hgnc.refseqId, gene.refseqAccession);
        if (gene.ccdsId != null)
            r.addProperty(Hgnc.ccdsId, gene.ccdsId);
        if (gene.uniprotIds != null)
            r.addProperty(Hgnc.uniprotId, gene.uniprotIds);
        if (gene.pubmedId != null) {
            r.addProperty(Hgnc.pubmedId, gene.pubmedId);
            r.addProperty(UniProt.journal, gene.pubmedId);
        }
        if (gene.mgdId != null)
            r.addProperty(Hgnc.mgiId, gene.mgdId);
        if (gene.rgdId != null)
            r.addProperty(Hgnc.rgdId, gene.rgdId);
        if (gene.lsdb != null)
            r.addProperty(KalisNs.lsdbHGNCProperty, gene.lsdb);
        if (gene.cosmic != null)
            r.addProperty(KalisNs.cosmicHGNCProperty, gene.cosmic);
        if (gene.omimId != null)
            r.addProperty(Hgnc.omimId, gene.omimId);
        if (gene.mirbase != null)
            r.addProperty(KalisNs.mirbaseHGNCProperty, gene.mirbase);
        if (gene.homeodb != null)
            r.addProperty(KalisNs.homeodbHGNCProperty, gene.homeodb);
        if (gene.snornabase != null)
            r.addProperty(KalisNs.snornabaseHGNCProperty, gene.snornabase);
        if (gene.bioparadigmsSlc != null)
            r.addProperty(KalisNs.bioparadigmsSlcHGNCProperty, gene.bioparadigmsSlc);
        if (gene.orphanet != null)
            r.addProperty(KalisNs.orphanetHGNCProperty, gene.orphanet);
        if (gene.pseudogeneOrg != null)
            r.addProperty(KalisNs.pseudogeneHGNCProperty, gene.pseudogeneOrg);
        if (gene.hordeId != null)
            r.addProperty(KalisNs.hordeIdHGNCProperty, gene.hordeId);
        if (gene.merops != null)
            r.addProperty(KalisNs.meropsHGNCProperty, gene.merops);
        if (gene.imgt != null)
            r.addProperty(KalisNs.imgtHGNCProperty, gene.imgt);
        if (gene.iuphar != null)
            r.addProperty(KalisNs.iupharHGNCProperty, gene.iuphar);
        if (gene.kznfGeneCatalog != null)
            r.addProperty(KalisNs.kznfGeneCatalogHGNCProperty, gene.kznfGeneCatalog);
        if (gene.mamitTrnaDb != null)
            r.addProperty(KalisNs.mamitTrnadbHGNCProperty, gene.mamitTrnaDb);
        if (gene.cd != null)
            r.addProperty(KalisNs.cdHGNCProperty, gene.cd);
        if (gene.lncrnaDb != null)
            r.addProperty(KalisNs.lncrnadbHGNCProperty, gene.lncrnaDb);
        if (gene.enzymeId != null)
            r.addProperty(Hgnc.ecId, gene.enzymeId);
        if (gene.intermediateFilamentDb != null)
            r.addProperty(KalisNs.intermediateFilamentDbHGNCProperty, gene.intermediateFilamentDb);
        if (gene.rnaCentralIds != null)
            r.addProperty(KalisNs.rnaCentralIdsHGNCProperty, gene.rnaCentralIds);
        if (gene.lnciPedia != null)
            r.addProperty(KalisNs.lncipediaHGNCProperty, gene.lnciPedia);
        if (gene.gtrnaDb != null)
            r.addProperty(KalisNs.gtrnadbHGNCProperty, gene.gtrnaDb);
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
