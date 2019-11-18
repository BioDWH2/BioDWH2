package de.unibi.agbi.biodwh2.core.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.HashMap;
import java.util.Map;

public class KalisNs {
    private static final String Uri = "https://rdf.kalis-amts.de/ns#";
    private Map<String, Map<String, String>> propertyList = new HashMap<>();

    private static Property createHGNCProperty(String property) {
        return ResourceFactory.createProperty(Uri + "hgnc/" + property);
    }

    public static Property locusGroupProperty = createHGNCProperty("locusGroup");
    public static Property locusTypeProperty = createHGNCProperty("locusType");
    public static Property locationProperty = createHGNCProperty("location");
    public static Property locationSortableProperty = createHGNCProperty("locationSortable");
    public static Property aliasSymbolProperty = createHGNCProperty("aliasSymbol");
    public static Property aliasNameProperty = createHGNCProperty("aliasName");
    public static Property prevSymbolProperty = createHGNCProperty("prevSymbol");
    public static Property prevNameProperty = createHGNCProperty("prevName");
    public static Property geneFamilyProperty = createHGNCProperty("geneFamily");
    public static Property geneFamilyIdProperty = createHGNCProperty("geneFamilyId");
    public static Property dateApprovedReservedProperty = createHGNCProperty("dateApprovedReserved");
    public static Property dateSymbolChangedProperty = createHGNCProperty("dateSymbolChanged");
    public static Property dateNameChangedProperty = createHGNCProperty("dateNameChanged");
    public static Property dateModifiedProperty = createHGNCProperty("dateModified");
    public static Property entrezIdProperty = createHGNCProperty("entrezId");
    public static Property ensembleGeneIdProperty = createHGNCProperty("ensembleGeneId");
    public static Property vegaIdProperty = createHGNCProperty("vegaId");
    public static Property ucscIdProperty = createHGNCProperty("iscsId");
    public static Property enaProperty = createHGNCProperty("ena");
    public static Property refsecAccessionProperty = createHGNCProperty("refsecAccession");
    public static Property ccdsIdProperty = createHGNCProperty("ccdsId");
    public static Property uniprotIdsProperty = createHGNCProperty("uniprotId");
    public static Property pubmedIdProperty = createHGNCProperty("pubmedId");
    public static Property mgdIdProperty = createHGNCProperty("mgdId");
    public static Property rgdIdProperty = createHGNCProperty("rgdId");
    public static Property lsdbProperty = createHGNCProperty("lsdb");
    public static Property cosmicProperty = createHGNCProperty("cosmic");
    public static Property omimIdProperty = createHGNCProperty("omimId");
    public static Property mirbaseProperty = createHGNCProperty("mirbase");
    public static Property homeodbProperty = createHGNCProperty("homedb");
    public static Property snornabaseProperty = createHGNCProperty("snornabase");
    public static Property bioparadigmsSlcProperty = createHGNCProperty("bioparadigmsSlc");
    public static Property orphanetProperty = createHGNCProperty("orphanet");
    public static Property pseudogeneProperty = createHGNCProperty("pseudogene");
    public static Property hordeIdProperty = createHGNCProperty("hordeId");
    public static Property meropsProperty = createHGNCProperty("merops");
    public static Property imgtProperty = createHGNCProperty("imgt");
    public static Property iupharProperty = createHGNCProperty("iuphar");
    public static Property kznfGeneCatalogProperty = createHGNCProperty("kznfGeneCatalog");
    public static Property mamitTrnadbProperty = createHGNCProperty("mamitTrnadb");
    public static Property cdProperty = createHGNCProperty("cd");
    public static Property lncrnadbProperty = createHGNCProperty("lncrnadb");
    public static Property enzymeIdProperty = createHGNCProperty("enzymeId");
    public static Property intermediateFilamentDbProperty = createHGNCProperty("intermediateFilamentDb");
    public static Property rnaCentralIdsProperty = createHGNCProperty("rnaCentralIds");
    public static Property lncipediaProperty = createHGNCProperty("lncipedia");
    public static Property gtrnadbProperty = createHGNCProperty("gtrnadb");

    private KalisNs() {
    }

}
