package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public class Hgnc {
    public static final String Uri = "http://bio2rdf.org/hgnc.symbol_vocabulary:";

    public static final Property uniprotId = ResourceFactory.createProperty(Uri + "x-uniprot");
    public static final Property omimId = ResourceFactory.createProperty(Uri + "x-omim");
    public static final Property ncbiGeneId = ResourceFactory.createProperty(Uri + "x-ncbigene");
    public static final Property vegaId = ResourceFactory.createProperty(Uri + "x-vega");
    public static final Property ccdsId = ResourceFactory.createProperty(Uri + "x-ccds");
    public static final Property refId = ResourceFactory.createProperty(Uri + "xref");
    public static final Property status = ResourceFactory.createProperty(Uri + "status");
    public static final Property pubmedId = ResourceFactory.createProperty(Uri + "x-pubmed");
    public static final Property hasApprovedSymbol = ResourceFactory.createProperty(Uri + "has-approved-symbol");
    public static final Property mgiId = ResourceFactory.createProperty(Uri + "x-mgi");
    public static final Property ensemblId = ResourceFactory.createProperty(Uri + "x-ensembl");
    public static final Property refseqId = ResourceFactory.createProperty(Uri + "x-refseq");
    public static final Property identifiersOrgId = ResourceFactory.createProperty(Uri + "x-identifiers.org");
    public static final Property ucscId = ResourceFactory.createProperty(Uri + "x-ucsc");
    public static final Property rgdId = ResourceFactory.createProperty(Uri + "x-rgd");
    public static final Property accession = ResourceFactory.createProperty(Uri + "accession");
    public static final Property nameSynonym = ResourceFactory.createProperty(Uri + "name-synonym");
    public static final Property locusSpecificXref = ResourceFactory.createProperty(Uri + "locus-specific-xref");
    public static final Property geneFamilyTag = ResourceFactory.createProperty(Uri + "gene-family-tag");
    public static final Property prevSymbol = ResourceFactory.createProperty(Uri + "previous-symbol");
    public static final Property prevName = ResourceFactory.createProperty(Uri + "previous-name");
    public static final Property dateNameChanged = ResourceFactory.createProperty(Uri + "date-name-changed");
    public static final Property geneFamilyDiscription = ResourceFactory.createProperty(
            Uri + "gene-family-description");
    public static final Property approvedSymbol = ResourceFactory.createProperty(Uri + "approved-symbol");
    public static final Property ecId = ResourceFactory.createProperty(Uri + "x-ec");
    public static final Property dateApproved = ResourceFactory.createProperty(Uri + "date-approved");
    public static final Property dateModified = ResourceFactory.createProperty(Uri + "date-modified");
    public static final Property approvedName = ResourceFactory.createProperty(Uri + "approved-name");
    public static final Property chromosome = ResourceFactory.createProperty(Uri + "chromosome");
    public static final Property dateSymbolChanged = ResourceFactory.createProperty(Uri + "date-symbol-changed");
}
