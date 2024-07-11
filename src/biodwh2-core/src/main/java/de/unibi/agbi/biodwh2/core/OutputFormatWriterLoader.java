package de.unibi.agbi.biodwh2.core;

import de.unibi.agbi.biodwh2.core.io.graph.OutputFormatWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class OutputFormatWriterLoader {
    private static final Logger LOGGER = LogManager.getLogger(OutputFormatWriterLoader.class);
    private static OutputFormatWriterLoader instance;

    private final List<OutputFormatWriter> outputFormatWriters = new ArrayList<>();

    private OutputFormatWriterLoader() {
        final var classes = Factory.getInstance().getImplementations(OutputFormatWriter.class);
        for (final Class<OutputFormatWriter> _class : classes) {
            final var writer = tryInstantiateGraphWriter(_class);
            if (writer != null)
                outputFormatWriters.add(writer);
        }
    }

    public static OutputFormatWriterLoader getInstance() {
        if (instance == null)
            instance = new OutputFormatWriterLoader();
        return instance;
    }

    private static OutputFormatWriter tryInstantiateGraphWriter(final Class<OutputFormatWriter> _class) {
        if (Modifier.isAbstract(_class.getModifiers()))
            return null;
        try {
            return _class.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to instantiate output format writer '{}'", _class.getName(), e);
        }
        return null;
    }

    public OutputFormatWriter[] getOutputFormatWriters() {
        return outputFormatWriters.toArray(OutputFormatWriter[]::new);
    }

    public OutputFormatWriter[] getOutputFormatWriters(final String... outputFormatIds) {
        final Set<String> failedIds = new HashSet<>();
        final Map<String, OutputFormatWriter> result = new HashMap<>();
        for (final String id : outputFormatIds)
            result.put(id, null);
        final Set<String> remainingIds = new HashSet<>(result.keySet());
        while (!remainingIds.isEmpty()) {
            for (final String id : remainingIds) {
                final OutputFormatWriter writer = getOutputFormatWriterById(id);
                if (writer != null)
                    result.put(id, writer);
                else
                    failedIds.add(id);
            }
            remainingIds.clear();
            remainingIds.addAll(
                    result.keySet().stream().filter(k -> result.get(k) == null).collect(Collectors.toSet()));
            remainingIds.removeAll(failedIds);
        }
        return result.values().toArray(new OutputFormatWriter[0]);
    }

    public OutputFormatWriter getOutputFormatWriterById(final String id) {
        for (final OutputFormatWriter writer : outputFormatWriters)
            if (writer.getId().equals(id))
                return writer;
        if (LOGGER.isWarnEnabled())
            LOGGER.warn("Failed to retrieve output format writer with id '{}'", id);
        return null;
    }

    public String[] getOutputFormatIds() {
        return outputFormatWriters.stream().map(OutputFormatWriter::getId).toArray(String[]::new);
    }
}
