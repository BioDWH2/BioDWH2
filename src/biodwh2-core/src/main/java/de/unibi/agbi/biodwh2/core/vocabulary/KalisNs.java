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

    public static Property locusGroupHGNCProperty = createHGNCProperty("locusGroup");
    public static Property locusTypeHGNCProperty = createHGNCProperty("locusType");
    public static Property locationHGNCProperty = createHGNCProperty("location");
    public static Property locationSortableHGNCProperty = createHGNCProperty("locationSortable");
    public static Property aliasSymbolHGNCProperty = createHGNCProperty("aliasSymbol");
    public static Property aliasNameHGNCProperty = createHGNCProperty("aliasName");
    public static Property prevSymbolHGNCProperty = createHGNCProperty("prevSymbol");
    public static Property prevNameHGNCProperty = createHGNCProperty("prevName");
    public static Property geneFamilyHGNCProperty = createHGNCProperty("geneFamily");
    public static Property geneFamilyIdHGNCProperty = createHGNCProperty("geneFamilyId");
    public static Property dateApprovedReservedHGNCProperty = createHGNCProperty("dateApprovedReserved");
    public static Property dateSymbolChangedHGNCProperty = createHGNCProperty("dateSymbolChanged");
    public static Property dateNameChangedHGNCProperty = createHGNCProperty("dateNameChanged");
    public static Property dateModifiedHGNCProperty = createHGNCProperty("dateModified");
    public static Property entrezIdHGNCProperty = createHGNCProperty("entrezId");
    public static Property ensembleGeneIdHGNCProperty = createHGNCProperty("ensembleGeneId");
    public static Property vegaIdHGNCProperty = createHGNCProperty("vegaId");
    public static Property ucscIdHGNCProperty = createHGNCProperty("iscsId");
    public static Property enaHGNCProperty = createHGNCProperty("ena");
    public static Property refsecAccessionHGNCProperty = createHGNCProperty("refsecAccession");
    public static Property ccdsIdHGNCProperty = createHGNCProperty("ccdsId");
    public static Property uniprotIdsHGNCProperty = createHGNCProperty("uniprotId");
    public static Property pubmedIdHGNCProperty = createHGNCProperty("pubmedId");
    public static Property mgdIdHGNCProperty = createHGNCProperty("mgdId");
    public static Property rgdIdHGNCProperty = createHGNCProperty("rgdId");
    public static Property lsdbHGNCProperty = createHGNCProperty("lsdb");
    public static Property cosmicHGNCProperty = createHGNCProperty("cosmic");
    public static Property omimIdHGNCProperty = createHGNCProperty("omimId");
    public static Property mirbaseHGNCProperty = createHGNCProperty("mirbase");
    public static Property homeodbHGNCProperty = createHGNCProperty("homedb");
    public static Property snornabaseHGNCProperty = createHGNCProperty("snornabase");
    public static Property bioparadigmsSlcHGNCProperty = createHGNCProperty("bioparadigmsSlc");
    public static Property orphanetHGNCProperty = createHGNCProperty("orphanet");
    public static Property pseudogeneHGNCProperty = createHGNCProperty("pseudogene");
    public static Property hordeIdHGNCProperty = createHGNCProperty("hordeId");
    public static Property meropsHGNCProperty = createHGNCProperty("merops");
    public static Property imgtHGNCProperty = createHGNCProperty("imgt");
    public static Property iupharHGNCProperty = createHGNCProperty("iuphar");
    public static Property kznfGeneCatalogHGNCProperty = createHGNCProperty("kznfGeneCatalog");
    public static Property mamitTrnadbHGNCProperty = createHGNCProperty("mamitTrnadb");
    public static Property cdHGNCProperty = createHGNCProperty("cd");
    public static Property lncrnadbHGNCProperty = createHGNCProperty("lncrnadb");
    public static Property enzymeIdHGNCProperty = createHGNCProperty("enzymeId");
    public static Property intermediateFilamentDbHGNCProperty = createHGNCProperty("intermediateFilamentDb");
    public static Property rnaCentralIdsHGNCProperty = createHGNCProperty("rnaCentralIds");
    public static Property lncipediaHGNCProperty = createHGNCProperty("lncipedia");
    public static Property gtrnadbHGNCProperty = createHGNCProperty("gtrnadb");

    private KalisNs() {
    }

}
