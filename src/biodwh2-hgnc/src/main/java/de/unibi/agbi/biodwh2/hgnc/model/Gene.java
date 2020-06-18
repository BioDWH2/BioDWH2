package de.unibi.agbi.biodwh2.hgnc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
@JsonPropertyOrder(value = {
        "hgnc_id", "symbol", "name", "locus_group", "locus_type", "status", "location", "location_sortable",
        "alias_symbol", "alias_name", "prev_symbol", "prev_name", "gene_family", "gene_family_id",
        "date_approved_reserved", "date_symbol_changed", "date_name_changed", "date_modified", "entrez_id",
        "ensembl_gene_id", "vega_id", "ucsc_id", "ena", "refseq_accession", "ccds_id", "uniprot_ids", "pubmed_id",
        "mgd_id", "rgd_id", "lsdb", "cosmic", "omim_id", "mirbase", "homeodb", "snornabase", "bioparadigms_slc",
        "orphanet", "pseudogene.org", "horde_id", "merops", "imgt", "iuphar", "kznf_gene_catalog", "mamit-trnadb", "cd",
        "lncrnadb", "enzyme_id", "intermediate_filament_db", "rna_central_ids", "lncipedia", "gtrnadb", "agr"
})
@NodeLabels({"Gene"})
public final class Gene {
    @JsonProperty("hgnc_id")
    @GraphProperty("hgnc_id")
    public String hgncId;
    @JsonProperty("symbol")
    @GraphProperty("symbol")
    public String symbol;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("locus_group")
    @GraphProperty("locus_group")
    public String locusGroup;
    @JsonProperty("locus_type")
    @GraphProperty("locus_type")
    public String locusType;
    @JsonProperty("status")
    @GraphProperty("status")
    public String status;
    @JsonProperty("location")
    @GraphProperty("location")
    public String location;
    @JsonProperty("location_sortable")
    @GraphProperty("location_sortable")
    public String locationSortable;
    @JsonProperty("alias_symbol")
    public String aliasSymbol;
    @JsonProperty("alias_name")
    public String aliasName;
    @JsonProperty("prev_symbol")
    @GraphProperty("prev_symbol")
    public String prevSymbol;
    @JsonProperty("prev_name")
    @GraphProperty("prev_name")
    public String prevName;
    @JsonProperty("gene_family")
    @GraphProperty("gene_family")
    public String geneFamily;
    @JsonProperty("gene_family_id")
    @GraphProperty("gene_family_id")
    public String geneFamilyId;
    @JsonProperty("date_approved_reserved")
    @GraphProperty("date_approved_reserved")
    public String dateApprovedReserved;
    @JsonProperty("date_symbol_changed")
    @GraphProperty("date_symbol_changed")
    public String dateSymbolChanged;
    @JsonProperty("date_name_changed")
    @GraphProperty("date_name_changed")
    public String dateNameChanged;
    @JsonProperty("date_modified")
    @GraphProperty("date_modified")
    public String dateModified;
    @JsonProperty("entrez_id")
    @GraphProperty("entrez_id")
    public String entrezId;
    @JsonProperty("ensembl_gene_id")
    @GraphProperty("ensembl_gene_id")
    public String ensemblGeneId;
    @JsonProperty("vega_id")
    @GraphProperty("vega_id")
    public String vegaId;
    @JsonProperty("ucsc_id")
    @GraphProperty("ucsc_id")
    public String ucscId;
    @JsonProperty("ena")
    @GraphProperty("ena")
    public String ena;
    @JsonProperty("refseq_accession")
    @GraphProperty("refseq_accession")
    public String refseqAccession;
    @JsonProperty("ccds_id")
    @GraphProperty("ccds_id")
    public String ccdsId;
    @JsonProperty("uniprot_ids")
    @GraphProperty("uniprot_ids")
    public String uniprotIds;
    @JsonProperty("pubmed_id")
    @GraphProperty("pubmed_id")
    public String pubmedId;
    @JsonProperty("mgd_id")
    @GraphProperty("mgd_id")
    public String mgdId;
    @JsonProperty("rgd_id")
    @GraphProperty("rgd_id")
    public String rgdId;
    @JsonProperty("lsdb")
    @GraphProperty("lsdb")
    public String lsdb;
    @JsonProperty("cosmic")
    @GraphProperty("cosmic")
    public String cosmic;
    @JsonProperty("omim_id")
    @GraphProperty("omim_id")
    public String omimId;
    @JsonProperty("mirbase")
    @GraphProperty("mirbase")
    public String mirbase;
    @JsonProperty("homeodb")
    @GraphProperty("homeodb")
    public String homeodb;
    @JsonProperty("snornabase")
    @GraphProperty("snornabase")
    public String snornabase;
    @JsonProperty("bioparadigms_slc")
    @GraphProperty("bioparadigms_slc")
    public String bioparadigmsSlc;
    @JsonProperty("orphanet")
    @GraphProperty("orphanet")
    public String orphanet;
    @JsonProperty("pseudogene.org")
    @GraphProperty("pseudogene_org")
    public String pseudogeneOrg;
    @JsonProperty("horde_id")
    @GraphProperty("horde_id")
    public String hordeId;
    @JsonProperty("merops")
    @GraphProperty("merops")
    public String merops;
    @JsonProperty("imgt")
    @GraphProperty("imgt")
    public String imgt;
    @JsonProperty("iuphar")
    @GraphProperty("iuphar")
    public String iuphar;
    @JsonProperty("kznf_gene_catalog")
    @GraphProperty("kznf_gene_catalog")
    public String kznfGeneCatalog;
    @JsonProperty("mamit-trnadb")
    @GraphProperty("mamit_trnadb")
    public String mamitTrnaDb;
    @JsonProperty("cd")
    @GraphProperty("cd")
    public String cd;
    @JsonProperty("lncrnadb")
    @GraphProperty("lncrnadb")
    public String lncrnaDb;
    @JsonProperty("enzyme_id")
    @GraphProperty("enzyme_id")
    public String enzymeId;
    @JsonProperty("intermediate_filament_db")
    @GraphProperty("intermediate_filament_db")
    public String intermediateFilamentDb;
    @JsonProperty("rna_central_ids")
    @GraphProperty("rna_central_ids")
    public String rnaCentralIds;
    @JsonProperty("lncipedia")
    @GraphProperty("lncipedia")
    public String lnciPedia;
    @JsonProperty("gtrnadb")
    @GraphProperty("gtrnadb")
    public String gtrnaDb;
    @JsonProperty("agr")
    @GraphProperty("agr")
    public String agr;
}
