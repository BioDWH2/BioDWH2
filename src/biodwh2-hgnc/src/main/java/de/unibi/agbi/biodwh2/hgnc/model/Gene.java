package de.unibi.agbi.biodwh2.hgnc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "hgnc_id", "symbol", "name", "locus_group", "locus_type", "status", "location", "location_sortable",
        "alias_symbol", "alias_name", "prev_symbol", "prev_name", "gene_family", "gene_family_id",
        "date_approved_reserved", "date_symbol_changed", "date_name_changed", "date_modified", "entrez_id",
        "ensembl_gene_id", "vega_id", "ucsc_id", "ena", "refseq_accession", "ccds_id", "uniprot_ids", "pubmed_id",
        "mgd_id", "rgd_id", "lsdb", "cosmic", "omim_id", "mirbase", "homeodb", "snornabase", "bioparadigms_slc",
        "orphanet", "pseudogene.org", "horde_id", "merops", "imgt", "iuphar", "kznf_gene_catalog", "mamit-trnadb", "cd",
        "lncrnadb", "enzyme_id", "intermediate_filament_db", "rna_central_ids", "lncipedia", "gtrnadb"
})
public final class Gene {
    @JsonProperty("hgnc_id")
    public String hgncId;
    @JsonProperty("symbol")
    public String symbol;
    @JsonProperty("name")
    public String name;
    @JsonProperty("locus_group")
    public String locusGroup;
    @JsonProperty("locus_type")
    public String locusType;
    @JsonProperty("status")
    public String status;
    @JsonProperty("location")
    public String location;
    @JsonProperty("location_sortable")
    public String locationSortable;
    @JsonProperty("alias_symbol")
    public String aliasSymbol;
    @JsonProperty("alias_name")
    public String aliasName;
    @JsonProperty("prev_symbol")
    public String prevSymbol;
    @JsonProperty("prev_name")
    public String prevName;
    @JsonProperty("gene_family")
    public String geneFamily;
    @JsonProperty("gene_family_id")
    public String geneFamilyId;
    @JsonProperty("date_approved_reserved")
    public String dateApprovedReserved;
    @JsonProperty("date_symbol_changed")
    public String dateSymbolChanged;
    @JsonProperty("date_name_changed")
    public String dateNameChanged;
    @JsonProperty("date_modified")
    public String dateModified;
    @JsonProperty("entrez_id")
    public String entrezId;
    @JsonProperty("ensembl_gene_id")
    public String ensemblGeneId;
    @JsonProperty("vega_id")
    public String vegaId;
    @JsonProperty("ucsc_id")
    public String ucscId;
    @JsonProperty("ena")
    public String ena;
    @JsonProperty("refseq_accession")
    public String refseqAccession;
    @JsonProperty("ccds_id")
    public String ccdsId;
    @JsonProperty("uniprot_ids")
    public String uniprotIds;
    @JsonProperty("pubmed_id")
    public String pubmedId;
    @JsonProperty("mgd_id")
    public String mgdId;
    @JsonProperty("rgd_id")
    public String rgdId;
    @JsonProperty("lsdb")
    public String lsdb;
    @JsonProperty("cosmic")
    public String cosmic;
    @JsonProperty("omim_id")
    public String omimId;
    @JsonProperty("mirbase")
    public String mirbase;
    @JsonProperty("homeodb")
    public String homeodb;
    @JsonProperty("snornabase")
    public String snornabase;
    @JsonProperty("bioparadigms_slc")
    public String bioparadigmsSlc;
    @JsonProperty("orphanet")
    public String orphanet;
    @JsonProperty("pseudogene.org")
    public String pseudogeneOrg;
    @JsonProperty("horde_id")
    public String hordeId;
    @JsonProperty("merops")
    public String merops;
    @JsonProperty("imgt")
    public String imgt;
    @JsonProperty("iuphar")
    public String iuphar;
    @JsonProperty("kznf_gene_catalog")
    public String kznfGeneCatalog;
    @JsonProperty("mamit-trnadb")
    public String mamitTrnaDb;
    @JsonProperty("cd")
    public String cd;
    @JsonProperty("lncrnadb")
    public String lncrnaDb;
    @JsonProperty("enzyme_id")
    public String enzymeId;
    @JsonProperty("intermediate_filament_db")
    public String intermediateFilamentDb;
    @JsonProperty("rna_central_ids")
    public String rnaCentralIds;
    @JsonProperty("lncipedia")
    public String lnciPedia;
    @JsonProperty("gtrnadb")
    public String gtrnaDb;
}
