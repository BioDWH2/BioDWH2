module de.unibi.agbi.biodwh2.main {
    requires de.unibi.agbi.biodwh2.core;
    requires de.unibi.agbi.biodwh2.dgidb;
    requires de.unibi.agbi.biodwh2.disgenet;
    requires de.unibi.agbi.biodwh2.drugbank;
    requires de.unibi.agbi.biodwh2.drugcentral;
    requires de.unibi.agbi.biodwh2.geneontology;
    requires de.unibi.agbi.biodwh2.hgnc;
    requires de.unibi.agbi.biodwh2.hpo;
    requires de.unibi.agbi.biodwh2.kegg;
    requires de.unibi.agbi.biodwh2.medrt;
    requires de.unibi.agbi.biodwh2.mondo;
    requires de.unibi.agbi.biodwh2.ncbi;
    requires de.unibi.agbi.biodwh2.ndfrt;
    requires de.unibi.agbi.biodwh2.pharmgkb;
    requires de.unibi.agbi.biodwh2.sider;
    requires de.unibi.agbi.biodwh2.unii;

    requires org.apache.commons.lang3;
    requires slf4j.api;
    requires info.picocli;

    exports de.unibi.agbi.biodwh2;
}