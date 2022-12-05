package de.unibi.agbi.biodwh2.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

class ConfigurationTest {
    @Test
    void testSerialization() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Configuration configuration = new Configuration();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValueAsString(configuration);
    }
}