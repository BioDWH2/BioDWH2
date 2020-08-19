module de.unibi.agbi.biodwh2.server {
    requires de.unibi.agbi.biodwh2.core;
    requires com.fasterxml.jackson.databind;
    requires graphql.java;
    requires io.javalin;
    requires org.slf4j;

    exports de.unibi.agbi.biodwh2.server;
}