package de.unibi.agbi.biodwh2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

final class CmdConfigPropertyParser {
    public static boolean parseBoolean(final String value) {
        if (value == null || StringUtils.isEmpty(value))
            return false;
        return "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "y".equalsIgnoreCase(value) ||
               "1".equalsIgnoreCase(value);
    }

    public static Integer parseInteger(final String value) {
        if (NumberUtils.isDigits(value))
            return Integer.parseInt(value);
        return null;
    }

    public static Double parseDecimal(final String value) {
        if (NumberUtils.isCreatable(value))
            return Double.parseDouble(value);
        return null;
    }

    public static String[] parseStringList(final String value) {
        final var objectMapper = new ObjectMapper();
        try {
            final var json = objectMapper.readTree(value);
            if (json.isArray()) {
                final List<String> values = new ArrayList<>();
                for (final var node : json)
                    values.add(node.asText());
                return values.toArray(String[]::new);
            }
        } catch (JsonProcessingException ignored) {
        }
        return null;
    }

    public static Integer[] parseIntegerList(final String value) {
        final var objectMapper = new ObjectMapper();
        try {
            final var json = objectMapper.readTree(value);
            if (json.isArray()) {
                final List<Integer> values = new ArrayList<>();
                for (final var node : json)
                    if (node.isInt())
                        values.add(node.asInt());
                return values.toArray(Integer[]::new);
            }
        } catch (JsonProcessingException ignored) {
        }
        return null;
    }

    public static Object parse(final String value, DataSourcePropertyType propertyType) {
        switch (propertyType) {
            case STRING:
                return value;
            case INTEGER:
                return parseInteger(value);
            case DECIMAL:
                return parseDecimal(value);
            case BOOLEAN:
                return parseBoolean(value);
            case INTEGER_LIST:
                return parseIntegerList(value);
            case STRING_LIST:
                return parseStringList(value);
        }
        return null;
    }
}
