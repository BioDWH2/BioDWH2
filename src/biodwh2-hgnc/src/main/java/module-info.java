module de.unibi.agbi.biodwh2.hgnc {
    requires de.unibi.agbi.biodwh2.core;
    requires com.fasterxml.jackson.annotation;

    exports de.unibi.agbi.biodwh2.hgnc;

    opens de.unibi.agbi.biodwh2.hgnc.model to com.fasterxml.jackson.databind, de.unibi.agbi.biodwh2.core;
}