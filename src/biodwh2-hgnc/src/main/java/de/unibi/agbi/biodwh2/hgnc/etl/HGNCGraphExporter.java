package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;

public class HGNCGraphExporter extends GraphExporter {
    @Override
    protected Graph exportGraph(DataSource dataSource) {
        Graph g = new Graph();
        long id = 0;
        for (Gene gene : ((HGNCDataSource) dataSource).genes) {
            Node geneNode = new Node(id, "HGNC_Gene");
            geneNode.setProperty("hgnc_id", gene.hgncId);
            geneNode.setProperty("symbol", gene.symbol);
            geneNode.setProperty("name", gene.name);
            geneNode.setProperty("locus_group", gene.locusGroup);
            geneNode.setProperty("locus_type", gene.locusType);
            geneNode.setProperty("status", gene.status);
            geneNode.setProperty("location", gene.location);
            geneNode.setProperty("location_sortable", gene.locationSortable);
            geneNode.setProperty("alias_symbol", gene.aliasSymbol);
            geneNode.setProperty("alias_name", gene.aliasName);
            geneNode.setProperty("prev_symbol", gene.prevSymbol);
            geneNode.setProperty("prev_name", gene.prevName);
            geneNode.setProperty("gene_family", gene.geneFamily);
            geneNode.setProperty("gene_family_id", gene.geneFamilyId);
            geneNode.setProperty("date_approved_reserved", gene.dateApprovedReserved);
            geneNode.setProperty("date_symbol_changed", gene.dateSymbolChanged);
            geneNode.setProperty("date_name_changed", gene.dateNameChanged);
            geneNode.setProperty("date_modified", gene.dateModified);
            geneNode.setProperty("entrez_id", gene.entrezId);
            geneNode.setProperty("ensembl_gene_id", gene.ensemblGeneId);
            geneNode.setProperty("vega_id", gene.vegaId);
            geneNode.setProperty("ucsc_id", gene.ucscId);
            geneNode.setProperty("ena", gene.ena);
            geneNode.setProperty("refseq_accession", gene.refseqAccession);
            geneNode.setProperty("ccds_id", gene.ccdsId);
            geneNode.setProperty("uniprot_ids", gene.uniprotIds);
            geneNode.setProperty("pubmed_id", gene.pubmedId);
            geneNode.setProperty("mgd_id", gene.mgdId);
            geneNode.setProperty("rgd_id", gene.rgdId);
            geneNode.setProperty("lsdb", gene.lsdb);
            geneNode.setProperty("cosmic", gene.cosmic);
            geneNode.setProperty("omim_id", gene.omimId);
            geneNode.setProperty("mirbase", gene.mirbase);
            geneNode.setProperty("homeodb", gene.homeodb);
            geneNode.setProperty("snornabase", gene.snornabase);
            geneNode.setProperty("bioparadigms_slc", gene.bioparadigmsSlc);
            geneNode.setProperty("orphanet", gene.orphanet);
            geneNode.setProperty("pseudogene.org", gene.pseudogeneOrg);
            geneNode.setProperty("horde_id", gene.hordeId);
            geneNode.setProperty("merops", gene.merops);
            geneNode.setProperty("imgt", gene.imgt);
            geneNode.setProperty("iuphar", gene.iuphar);
            geneNode.setProperty("kznf_gene_catalog", gene.kznfGeneCatalog);
            geneNode.setProperty("mamit-trnadb", gene.mamitTrnaDb);
            geneNode.setProperty("cd", gene.cd);
            geneNode.setProperty("lncrnadb", gene.lncrnaDb);
            geneNode.setProperty("enzyme_id", gene.enzymeId);
            geneNode.setProperty("intermediate_filament_db", gene.intermediateFilamentDb);
            geneNode.setProperty("rna_central_ids", gene.rnaCentralIds);
            geneNode.setProperty("lncipedia", gene.lnciPedia);
            geneNode.setProperty("gtrnadb", gene.gtrnaDb);
            g.addNode(geneNode);
            id += 1;
        }
        return g;
    }
}
