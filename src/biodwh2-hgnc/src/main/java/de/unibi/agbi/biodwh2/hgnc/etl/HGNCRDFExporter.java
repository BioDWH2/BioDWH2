package de.unibi.agbi.biodwh2.hgnc.etl;

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

public class HGNCRDFExporter extends RDFExporter<HGNCDataSource> {
    private static final String HGNCDataUri = "https://www.genenames.org/data/";

    @Override
    public Model exportModel(HGNCDataSource dataSource) throws ExporterException {
        Model model = createDefaultModel();
        for (Gene gene : dataSource.genes)
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
        connect(r, BioPortal.STATUS, gene.status);
        connect(r, UniProt.name, gene.name);
        connect(r, BioModels.name, gene.name);
        connect(r, Hgnc.approvedName, gene.name);
        connect(r, BioPortal.APPROVED_NAME, gene.name);
        connect(r, Hgnc.nameSynonym, gene.aliasName);
        connect(r, BioPortal.ALIASES, gene.aliasName);
        connect(r, DCTerms.alternative, gene.aliasName);
        connect(r, BioPortal.LOCUS_GROUP, gene.locusGroup);
        connect(r, BioPortal.LOCUS_TYPE, gene.locusType);
        connect(r, Faldo.location, gene.location);
        connect(r, Hgnc.chromosome, gene.location);
        connect(r, BioPortal.CHROMOSOME, gene.location);
        connect(r, Hgnc.prevSymbol, gene.prevSymbol);
        connect(r, BioPortal.PREVIOUS_SYMBOLS, gene.prevSymbol);
        connect(r, Hgnc.prevName, gene.prevName);
        connect(r, BioPortal.PREVIOUS_NAMES, gene.prevName);
        connect(r, UniProt.familyMembershipStatement, gene.geneFamily);
        connect(r, Hgnc.geneFamilyDiscription, gene.geneFamily);
        connect(r, Hgnc.geneFamilyTag, gene.geneFamilyId);
        connect(r, BioPortal.GENE_FAMILY_TAG, gene.geneFamilyId);
        connect(r, Hgnc.dateApproved, gene.dateApprovedReserved);
        connect(r, BioPortal.DATE_APPROVED, gene.dateApprovedReserved);
        connect(r, Hgnc.dateSymbolChanged, gene.dateSymbolChanged);
        connect(r, BioPortal.DATE_SYMBOL_CHANGED, gene.dateSymbolChanged);
        connect(r, Hgnc.dateNameChanged, gene.dateNameChanged);
        connect(r, BioPortal.DATE_NAME_CHANGED, gene.dateNameChanged);
        connect(r, Hgnc.dateModified, gene.dateModified);
        connect(r, BioPortal.DATE_MODIFIED, gene.dateModified);
        connect(r, DCTerms.modified, gene.dateModified);
        connect(r, BioPortal.ENTREZ_GENE_ID, gene.entrezId);
        connect(r, Hgnc.ensemblId, gene.ensemblGeneId);
        connect(r, BioPortal.ENSEMBL_GENE_ID, gene.ensemblGeneId);
        connect(r, Hgnc.vegaId, gene.vegaId);
        connect(r, BioPortal.VEGA_IDS, gene.vegaId);
        connect(r, Hgnc.ucscId, gene.ucscId);
        connect(r, Hgnc.refseqId, gene.refseqAccession);
        connect(r, BioPortal.REF_SEQ_IDS, gene.refseqAccession);
        connect(r, Hgnc.ccdsId, gene.ccdsId);
        connect(r, BioPortal.CCDS_IDS, gene.ccdsId);
        connect(r, Hgnc.uniprotId, gene.uniprotIds);
        connect(r, Hgnc.pubmedId, gene.pubmedId);
        connect(r, UniProt.journal, gene.pubmedId);
        connect(r, Hgnc.mgiId, gene.mgdId);
        connect(r, BioPortal.MOUSE_GENOME_DATABASE_ID, gene.mgdId);
        connect(r, Hgnc.rgdId, gene.rgdId);
        connect(r, BioPortal.COSMIC_ID, gene.cosmic);
        connect(r, Hgnc.omimId, gene.omimId);
        connect(r, BioPortal.SNO_RNA_BASE_ID, gene.snornabase);
        connect(r, BioPortal.ORPHANET_ID, gene.orphanet);
        connect(r, BioPortal.PSEUDOGENE_ID, gene.pseudogeneOrg);
        connect(r, BioPortal.HORDE_ID, gene.hordeId);
        connect(r, BioPortal.MEROPS_ID, gene.merops);
        connect(r, BioPortal.IMGT_GENEDB_ID, gene.imgt);
        connect(r, BioPortal.IUPHAR_ID, gene.iuphar);
        connect(r, BioPortal.KZNF_GENE_CATALOG_ID, gene.kznfGeneCatalog);
        connect(r, BioPortal.CD_ID, gene.cd);
        connect(r, Hgnc.ecId, gene.enzymeId);
        connect(r, BioPortal.ENZYME_IDS, gene.enzymeId);
        connect(r, BioPortal.INTERMEDIATE_FILAMENT_DB_ID, gene.intermediateFilamentDb);


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
