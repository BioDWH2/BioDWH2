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
            r.addProperty(BioPortal.status, gene.status);
        }
        if (gene.name != null) {
            r.addProperty(UniProt.name, gene.name);
            r.addProperty(BioModels.name, gene.name);
            r.addProperty(Hgnc.approvedName, gene.name);
            r.addProperty(BioPortal.approvedName, gene.name);
        }
        if (gene.aliasName != null) {
            r.addProperty(Hgnc.nameSynonym, gene.aliasName);
            r.addProperty(BioPortal.aliases, gene.aliasName);
        }
        if (gene.locusGroup != null)
            r.addProperty(BioPortal.locusGroup, gene.locusGroup);
        if (gene.locusType != null)
            r.addProperty(BioPortal.locusType, gene.locusType);
        if (gene.location != null) {
            r.addProperty(Faldo.location, gene.location);
            r.addProperty(Hgnc.chromosome, gene.location);
            r.addProperty(BioPortal.chromosome, gene.location);
        }
        if (gene.locationSortable != null)
            r.addProperty(KalisNs.locationSortableHGNCProperty, gene.locationSortable);
        if (gene.aliasSymbol != null)
            r.addProperty(KalisNs.aliasSymbolHGNCProperty, gene.aliasSymbol);
        if (gene.prevSymbol != null) {
            r.addProperty(Hgnc.prevSymbol, gene.prevSymbol);
            r.addProperty(BioPortal.previousSymbols, gene.symbol);
        }
        if (gene.prevName != null) {
            r.addProperty(Hgnc.prevName, gene.prevName);
            r.addProperty(BioPortal.previousNames, gene.prevName);
        }
        if (gene.geneFamily != null) {
            r.addProperty(UniProt.familyMembershipStatement, gene.geneFamily);
            r.addProperty(Hgnc.geneFamilyDiscription, gene.geneFamily);
        }
        if (gene.geneFamilyId != null) {
            r.addProperty(KalisNs.geneFamilyIdHGNCProperty, gene.geneFamilyId);
            r.addProperty(Hgnc.geneFamilyTag, gene.geneFamily);
            r.addProperty(BioPortal.geneFamilyTag, gene.geneFamily);
        }
        if (gene.dateApprovedReserved != null) {
            r.addProperty(Hgnc.dateApproved, gene.dateApprovedReserved);
            r.addProperty(BioPortal.dateApproved, gene.dateApprovedReserved);
        }
        if (gene.dateSymbolChanged != null) {
            r.addProperty(Hgnc.dateSymbolChanged, gene.dateSymbolChanged);
            r.addProperty(BioPortal.dateSymbolChanged, gene.dateSymbolChanged);
        }
        if (gene.dateNameChanged != null) {
            r.addProperty(Hgnc.dateNameChanged, gene.dateNameChanged);
            r.addProperty(BioPortal.dateNameChanged, gene.dateNameChanged);
        }
        if (gene.dateModified != null) {
            r.addProperty(Hgnc.dateModified, gene.dateModified);
            r.addProperty(BioPortal.dateModified, gene.dateModified);
        }
        if (gene.entrezId != null)
            r.addProperty(BioPortal.entrezGeneId, gene.entrezId);
        if (gene.ensemblGeneId != null) {
            r.addProperty(Hgnc.ensemblId, gene.ensemblGeneId);
            r.addProperty(BioPortal.ensemblGeneId, gene.ensemblGeneId);
        }
        if (gene.vegaId != null) {
            r.addProperty(Hgnc.vegaId, gene.vegaId);
            r.addProperty(BioPortal.vegaIDs, gene.vegaId);
        }
        if (gene.ucscId != null) {
            r.addProperty(Hgnc.ucscId, gene.ucscId);
        }
        if (gene.ena != null)
            r.addProperty(KalisNs.enaHGNCProperty, gene.ena);
        if (gene.refseqAccession != null) {
            r.addProperty(Hgnc.refseqId, gene.refseqAccession);
            r.addProperty(BioPortal.refSeqIDs, gene.refseqAccession);
        }
        if (gene.ccdsId != null) {
            r.addProperty(Hgnc.ccdsId, gene.ccdsId);
            r.addProperty(BioPortal.ccdsIDs, gene.ccdsId);
        }
        if (gene.uniprotIds != null)
            r.addProperty(Hgnc.uniprotId, gene.uniprotIds);
        if (gene.pubmedId != null) {
            r.addProperty(Hgnc.pubmedId, gene.pubmedId);
            r.addProperty(UniProt.journal, gene.pubmedId);
        }
        if (gene.mgdId != null) {
            r.addProperty(Hgnc.mgiId, gene.mgdId);
            r.addProperty(BioPortal.mouseGenomeDatabaseId, gene.mgdId);
        }
        if (gene.rgdId != null) {
            r.addProperty(Hgnc.rgdId, gene.rgdId);
        }
        if (gene.lsdb != null)
            r.addProperty(KalisNs.lsdbHGNCProperty, gene.lsdb);
        if (gene.cosmic != null) {
            r.addProperty(KalisNs.cosmicHGNCProperty, gene.cosmic);
            r.addProperty(BioPortal.cosmicId, gene.cosmic);
        }
        if (gene.omimId != null)
            r.addProperty(Hgnc.omimId, gene.omimId);
        if (gene.mirbase != null)
            r.addProperty(KalisNs.mirbaseHGNCProperty, gene.mirbase);
        if (gene.homeodb != null)
            r.addProperty(KalisNs.homeodbHGNCProperty, gene.homeodb);
        if (gene.snornabase != null) {
            r.addProperty(KalisNs.snornabaseHGNCProperty, gene.snornabase);
            r.addProperty(BioPortal.snoRNABaseId, gene.snornabase);
        }
        if (gene.bioparadigmsSlc != null)
            r.addProperty(KalisNs.bioparadigmsSlcHGNCProperty, gene.bioparadigmsSlc);
        if (gene.orphanet != null)
            r.addProperty(BioPortal.orphanetId, gene.orphanet);
        if (gene.pseudogeneOrg != null)
            r.addProperty(BioPortal.pseudogeneId, gene.pseudogeneOrg);
        if (gene.hordeId != null)
            r.addProperty(BioPortal.hordeId, gene.hordeId);
        if (gene.merops != null)
            r.addProperty(BioPortal.meropsId, gene.merops);
        if (gene.imgt != null)
            r.addProperty(BioPortal.imgtGenedbId, gene.imgt);
        if (gene.iuphar != null)
            r.addProperty(BioPortal.iupharId, gene.iuphar);
        if (gene.kznfGeneCatalog != null)
            r.addProperty(BioPortal.kznfGeneCatalogId, gene.kznfGeneCatalog);
        if (gene.mamitTrnaDb != null)
            r.addProperty(KalisNs.mamitTrnadbHGNCProperty, gene.mamitTrnaDb);
        if (gene.cd != null)
            r.addProperty(BioPortal.cdId, gene.cd);
        if (gene.lncrnaDb != null)
            r.addProperty(KalisNs.lncrnadbHGNCProperty, gene.lncrnaDb);
        if (gene.enzymeId != null) {
            r.addProperty(Hgnc.ecId, gene.enzymeId);
            r.addProperty(BioPortal.enzymeIDs, gene.enzymeId);
        }
        if (gene.intermediateFilamentDb != null)
            r.addProperty(BioPortal.intermediateFilamentDbId, gene.intermediateFilamentDb);
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
