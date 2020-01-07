package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.vocabulary.*;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
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

    private static void connect(Resource sub, Property pred, String ob) {
        if (ob != null && pred != null)
            sub.addProperty(pred, ob);
    }

    private static void connect(Resource sub, Property pred, Resource ob) {
        if (ob != null && pred != null)
            sub.addProperty(pred, ob);
    }

    private static Resource createGeneResource(Model model, Gene gene) {
        Resource r = model.createResource(HGNCDataUri + "gene-symbol-report/#!/hgnc_id/" + gene.hgncId);
        connect(r, DCTerms.identifier, gene.hgncId);
        connect(r, RDF.type, UniProt.Gene);
        connect(r, SKOS.prefLabel, gene.symbol);
        connect(r, Hgnc.approvedSymbol, gene.symbol);
        connect(r, UniProt.Status, gene.status);
        connect(r, Hgnc.status, gene.status);
        connect(r, BioPortal.status, gene.status);
        connect(r, UniProt.name, gene.name);
        connect(r, BioModels.name, gene.name);
        connect(r, Hgnc.approvedName, gene.name);
        connect(r, BioPortal.approvedName, gene.name);
        connect(r, Hgnc.nameSynonym, gene.aliasName);
        connect(r, BioPortal.aliases, gene.aliasName);
        connect(r, DCTerms.alternative, gene.aliasName);
        connect(r, BioPortal.locusGroup, gene.locusGroup);
        connect(r, BioPortal.locusType, gene.locusType);
        connect(r, Faldo.location, gene.location);
        connect(r, Hgnc.chromosome, gene.location);
        connect(r, BioPortal.chromosome, gene.location);
        connect(r, Hgnc.prevSymbol, gene.prevSymbol);
        connect(r, BioPortal.previousSymbols, gene.prevSymbol);
        connect(r, Hgnc.prevName, gene.prevName);
        connect(r, BioPortal.previousNames, gene.prevName);
        connect(r, UniProt.familyMembershipStatement, gene.geneFamily);
        connect(r, Hgnc.geneFamilyDiscription, gene.geneFamily);
        connect(r, Hgnc.geneFamilyTag, gene.geneFamilyId);
        connect(r, BioPortal.geneFamilyTag, gene.geneFamilyId);
        connect(r, Hgnc.dateApproved, gene.dateApprovedReserved);
        connect(r, BioPortal.dateApproved, gene.dateApprovedReserved);
        connect(r, Hgnc.dateSymbolChanged, gene.dateSymbolChanged);
        connect(r, BioPortal.dateSymbolChanged, gene.dateSymbolChanged);
        connect(r, Hgnc.dateNameChanged, gene.dateNameChanged);
        connect(r, BioPortal.dateNameChanged, gene.dateNameChanged);
        connect(r, Hgnc.dateModified, gene.dateModified);
        connect(r, BioPortal.dateModified, gene.dateModified);
        connect(r, DCTerms.modified, gene.dateModified);
        connect(r, BioPortal.entrezGeneId, gene.entrezId);
        connect(r, Hgnc.ensemblId, gene.ensemblGeneId);
        connect(r, BioPortal.ensemblGeneId, gene.ensemblGeneId);
        connect(r, Hgnc.vegaId, gene.vegaId);
        connect(r, BioPortal.vegaIDs, gene.vegaId);
        connect(r, Hgnc.ucscId, gene.ucscId);
        connect(r, Hgnc.refseqId, gene.refseqAccession);
        connect(r, BioPortal.refSeqIDs, gene.refseqAccession);
        connect(r, Hgnc.ccdsId, gene.ccdsId);
        connect(r, BioPortal.ccdsIDs, gene.ccdsId);
        connect(r, Hgnc.uniprotId, gene.uniprotIds);
        connect(r, Hgnc.pubmedId, gene.pubmedId);
        connect(r, UniProt.journal, gene.pubmedId);
        connect(r, Hgnc.mgiId, gene.mgdId);
        connect(r, BioPortal.mouseGenomeDatabaseId, gene.mgdId);
        connect(r, Hgnc.rgdId, gene.rgdId);
        connect(r, BioPortal.cosmicId, gene.cosmic);
        connect(r, Hgnc.omimId, gene.omimId);
        connect(r, BioPortal.snoRNABaseId, gene.snornabase);
        connect(r, BioPortal.orphanetId, gene.orphanet);
        connect(r, BioPortal.pseudogeneId, gene.pseudogeneOrg);
        connect(r, BioPortal.hordeId, gene.hordeId);
        connect(r, BioPortal.meropsId, gene.merops);
        connect(r, BioPortal.imgtGenedbId, gene.imgt);
        connect(r, BioPortal.iupharId, gene.iuphar);
        connect(r, BioPortal.kznfGeneCatalogId, gene.kznfGeneCatalog);
        connect(r, BioPortal.cdId, gene.cd);
        connect(r, Hgnc.ecId, gene.enzymeId);
        connect(r, BioPortal.enzymeIDs, gene.enzymeId);
        connect(r, BioPortal.intermediateFilamentDbId, gene.intermediateFilamentDb);


        connect(r, KalisNs.rnaCentralIdsHGNCProperty, gene.rnaCentralIds);
        connect(r, KalisNs.lncipediaHGNCProperty, gene.lnciPedia);
        connect(r, KalisNs.gtrnadbHGNCProperty, gene.gtrnaDb);
        connect(r, KalisNs.lncrnadbHGNCProperty, gene.lncrnaDb);
        connect(r, KalisNs.mamitTrnadbHGNCProperty, gene.mamitTrnaDb);
        connect(r, KalisNs.bioparadigmsSlcHGNCProperty, gene.bioparadigmsSlc);
        connect(r, KalisNs.mirbaseHGNCProperty, gene.mirbase);
        connect(r, KalisNs.homeodbHGNCProperty, gene.homeodb);
        connect(r, KalisNs.enaHGNCProperty, gene.ena);
        connect(r, KalisNs.locationSortableHGNCProperty, gene.locationSortable);
        connect(r, KalisNs.aliasSymbolHGNCProperty, gene.aliasSymbol);
        connect(r, KalisNs.lsdbHGNCProperty, gene.lsdb);


        connect(r, RDFS.seeAlso, HGNCDataUri + "gene-symbol-report/#!/symbol/" + gene.symbol);
        connect(r, RDFS.seeAlso, "https://identifiers.org/hgnc.symbol:" + gene.symbol);
        connect(r, RDFS.seeAlso, "https://identifiers.org/hgnc:" + gene.hgncId);
        return r;
    }

    @Override
    protected void setModelPrefixes(Model model) {
        super.setModelPrefixes(model);
        model.setNsPrefix("upCore", UniProt.Uri);
    }
}
