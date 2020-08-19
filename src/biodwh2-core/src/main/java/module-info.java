module de.unibi.agbi.biodwh2.core {
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.csv;
    requires slf4j.api;
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;
    requires commons.net;
    requires nitrite;
    requires java.management;
    requires java.xml;
    requires org.jsoup;

    exports de.unibi.agbi.biodwh2.core;
    exports de.unibi.agbi.biodwh2.core.etl;
    exports de.unibi.agbi.biodwh2.core.exceptions;
    exports de.unibi.agbi.biodwh2.core.io;
    exports de.unibi.agbi.biodwh2.core.io.obo;
    exports de.unibi.agbi.biodwh2.core.io.sdf;
    exports de.unibi.agbi.biodwh2.core.mapping;
    exports de.unibi.agbi.biodwh2.core.model;
    exports de.unibi.agbi.biodwh2.core.model.graph;
    exports de.unibi.agbi.biodwh2.core.schema;
    exports de.unibi.agbi.biodwh2.core.net;

    opens de.unibi.agbi.biodwh2.core.model to com.fasterxml.jackson.databind;
    opens de.unibi.agbi.biodwh2.core.model.graph to nitrite;
}