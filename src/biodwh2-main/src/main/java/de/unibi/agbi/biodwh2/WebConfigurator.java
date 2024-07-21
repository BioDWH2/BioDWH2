package de.unibi.agbi.biodwh2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.*;
import de.unibi.agbi.biodwh2.core.exceptions.WorkspaceException;
import de.unibi.agbi.biodwh2.core.model.DataSourceFileType;
import de.unibi.agbi.biodwh2.core.net.UrlUtils;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.Context;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

public class WebConfigurator {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final StringWriter writer = new StringWriter();
    private final int port;
    private final String[] dataSourceIds;
    private final Map<String, Map<String, DataSourcePropertyType>> dataSourcePropertyTypes;
    private final List<Map<String, Object>> dataSourceDescriptions;
    private final String[] outputFormatIds;
    private Thread runThread;

    public WebConfigurator(Integer webConfiguratorPort) {
        port = webConfiguratorPort != null ? webConfiguratorPort : 7070;
        final var dataSources = DataSourceLoader.getInstance().getDataSources();
        dataSourceIds = Arrays.stream(dataSources).filter(this::filterDataSource).map(DataSource::getId).toArray(
                String[]::new);
        dataSourcePropertyTypes = Arrays.stream(dataSources).filter(this::filterDataSource).collect(
                Collectors.toMap(DataSource::getId, DataSource::getAvailableProperties));
        dataSourceDescriptions = Arrays.stream(dataSources).filter(this::filterDataSource).sorted(
                Comparator.comparing(a -> a.getId().toLowerCase())).map(this::getDataSourceDescription).collect(
                Collectors.toList());
        outputFormatIds = OutputFormatWriterLoader.getInstance().getOutputFormatIds();
    }

    private boolean filterDataSource(final DataSource dataSource) {
        return !dataSource.getId().startsWith("Mock") && !dataSource.getId().equals("NCBI");
    }

    private Map<String, Object> getDataSourceDescription(final DataSource dataSource) {
        var map = new HashMap<String, Object>();
        map.put("id", dataSource.getId());
        map.put("fullName", dataSource.getFullName());
        map.put("description", dataSource.getDescription());
        map.put("license", dataSource.getLicense());
        map.put("licenseUrl", dataSource.getLicenseUrl());
        map.put("website", dataSource.getWebsite());
        map.put("developmentState", dataSource.getDevelopmentState().toString());
        map.put("isOntology", dataSource instanceof OntologyDataSource);
        final Map<String, String> availableProperties = new HashMap<>();
        for (final var entry : dataSource.getAvailableProperties().entrySet())
            availableProperties.put(entry.getKey(), entry.getValue().name());
        map.put("availableProperties", availableProperties);
        return map;
    }

    public void run() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration logConfig = ctx.getConfiguration();
        final var layout = PatternLayout.newBuilder().withPattern(
                "%d{yyyy-MM-dd HH:mm:ss} [%-5p] %c:%L - %m%n%throwable").withConfiguration(logConfig).build();
        final var appender = WriterAppender.createAppender(layout, null, writer, "BioDWH2-WebConfig-Writer", false,
                                                           true);
        appender.start();
        logConfig.addAppender(appender);
        for (final LoggerConfig loggerConfig : logConfig.getLoggers().values()) {
            loggerConfig.addAppender(appender, null, null);
        }
        logConfig.getRootLogger().addAppender(appender, null, null);

        final var app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.bundledPlugins.enableCors(cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost));
            config.router.apiBuilder(() -> {
                ApiBuilder.get("/connect", this::onConnect);
                ApiBuilder.post("/workspace/sync", this::onWorkspaceSync);
                ApiBuilder.post("/workspace/add", this::onWorkspaceAdd);
                ApiBuilder.post("/workspace/save", this::onWorkspaceSave);
                ApiBuilder.post("/workspace/run", this::onWorkspaceRun);
                ApiBuilder.get("/log", this::onGetLog);
            });
        });
        app.start(port);
        clearLog();
        UrlUtils.openInBrowser("https://biodwh2.github.io/config/?endpoint=http://localhost:" + port);
    }

    private void clearLog() {
        writer.getBuffer().setLength(0);
    }

    private void onConnect(final Context ctx) {
        var map = new HashMap<String, Object>();
        map.put("dataSources", dataSourceDescriptions.toArray());
        map.put("outputFormatIds", outputFormatIds);
        ctx.json(map);
    }

    private void onGetLog(final Context ctx) {
        var map = new HashMap<String, Object>();
        map.put("running", runThread != null && runThread.isAlive());
        map.put("log", writer.toString());
        ctx.json(map);
    }

    private void onWorkspaceSync(final Context ctx) throws JsonProcessingException {
        final var body = objectMapper.readTree(ctx.body());
        final var workspacesNode = body.get("workspaces");
        final List<String> workspaces = new ArrayList<>();
        for (final var workspacePath : workspacesNode)
            workspaces.add(workspacePath.asText());
        var map = new HashMap<String, Object>();
        map.put("workspaces",
                workspaces.stream().map(w -> new AbstractMap.SimpleEntry<>(w, new BaseWorkspace(w).exists()))
                          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        final var activeWorkspacePath = body.get("activeWorkspace");
        if (activeWorkspacePath != null && StringUtils.isNotEmpty(activeWorkspacePath.asText())) {
            final var activeWorkspace = new BaseWorkspace(activeWorkspacePath.asText());
            if (activeWorkspace.exists()) {
                final var status = new HashMap<String, Object>();
                try {
                    final var configuration = activeWorkspace.loadConfiguration();
                    status.put("outputFormatIds", configuration.getOutputFormatIds());
                    status.put("skipMetaGraphGeneration", configuration.shouldSkipMetaGraphGeneration());
                    status.put("globalProperties", configuration.getGlobalProperties());
                    final List<String> activeDataSources = Arrays.asList(configuration.getDataSourceIds());
                    final var dataSources = new HashMap<String, Map<String, Object>>();
                    for (final String dataSourceId : dataSourceIds) {
                        final var dataSource = DataSourceLoader.getInstance().getDataSourceById(dataSourceId);
                        final var dataSourceStatus = new HashMap<String, Object>();
                        dataSourceStatus.put("id", dataSourceId);
                        dataSourceStatus.put("active", activeDataSources.contains(dataSourceId));
                        dataSourceStatus.put("properties", configuration.getDataSourceProperties(dataSourceId));
                        final var metadataFilePath = dataSource.getFilePath(activeWorkspace,
                                                                            DataSourceFileType.METADATA);
                        if (metadataFilePath.toFile().exists()) {
                            final var metadata = dataSource.loadMetadata(metadataFilePath);
                            if (metadata != null) {
                                dataSourceStatus.put("updateSuccessful", metadata.updateSuccessful);
                                dataSourceStatus.put("parseSuccessful", metadata.parseSuccessful);
                                dataSourceStatus.put("exportSuccessful", metadata.exportSuccessful);
                            }
                        }
                        dataSources.put(dataSourceId, dataSourceStatus);
                    }
                    status.put("dataSources", dataSources);
                } catch (IOException ignored) {
                }
                map.put("activeWorkspace", status);
            } else {
                map.put("activeWorkspace", null);
            }
        }
        ctx.json(map);
    }

    private void onWorkspaceAdd(final Context ctx) throws JsonProcessingException {
        final var body = objectMapper.readTree(ctx.body());
        final var workspaceNode = body.get("workspace");
        if (workspaceNode != null) {
            final var workspace = workspaceNode.asText();
            if (StringUtils.isNotEmpty(workspace)) {
                try {
                    new Workspace(workspace);
                    ctx.json(new HashMap<>());
                    return;
                } catch (WorkspaceException ignored) {
                }
            }
        }
        ctx.json(new HashMap<>()).status(500);
    }

    private void onWorkspaceRun(final Context ctx) throws JsonProcessingException {
        if (runThread != null && runThread.isAlive()) {
            ctx.json(new HashMap<String, Object>()).status(500);
        }
        clearLog();
        final var body = objectMapper.readTree(ctx.body());
        final var workspaceNode = body.get("workspace");
        if (workspaceNode == null) {
            ctx.json(new HashMap<String, Object>()).status(500);
            return;
        }
        final var workspacePath = workspaceNode.asText();
        if (StringUtils.isEmpty(workspacePath) || !new BaseWorkspace(workspacePath).exists()) {
            ctx.json(new HashMap<String, Object>()).status(500);
            return;
        }
        final var skipUpdate = body.get("skipUpdate").asBoolean(false);
        try {
            runThread = new Thread(() -> {
                final var workspace = new Workspace(workspacePath);
                workspace.processDataSources(skipUpdate);
            });
            runThread.start();
            ctx.json(new HashMap<>());
        } catch (Exception ignored) {
            ctx.json(new HashMap<String, Object>()).status(500);
        }
    }

    private void onWorkspaceSave(final Context ctx) throws JsonProcessingException {
        final var body = objectMapper.readTree(ctx.body());
        final var workspaceNode = body.get("workspace");
        final var stateNode = body.get("state");
        if (workspaceNode == null || StringUtils.isEmpty(workspaceNode.asText()) || stateNode == null) {
            ctx.json(new HashMap<String, Object>()).status(500);
            return;
        }
        final var activeWorkspace = new BaseWorkspace(workspaceNode.asText());
        if (!activeWorkspace.exists()) {
            ctx.json(new HashMap<String, Object>()).status(500);
            return;
        }
        try {
            final var configuration = activeWorkspace.loadConfiguration();
            // skip meta graph generation
            final var skipMetaGraphGenerationNode = stateNode.get("skipMetaGraphGeneration");
            if (skipMetaGraphGenerationNode != null && skipMetaGraphGenerationNode.isBoolean())
                configuration.setSkipMetaGraphGeneration(skipMetaGraphGenerationNode.asBoolean());
            // global species filter
            final var globalPropertiesNode = stateNode.get("globalProperties");
            if (globalPropertiesNode != null) {
                final var fields = globalPropertiesNode.fields();
                final var globalProperties = configuration.getGlobalProperties();
                while (fields.hasNext()) {
                    final var field = fields.next();
                    if ("speciesFilter".equals(field.getKey())) {
                        final var value = getIntArrayFromJson(field.getValue());
                        if (value != null)
                            globalProperties.speciesFilter = value;
                    }
                    // no other properties exist atm.
                }
            }
            // output format IDs
            final var outputFormatIdsNode = stateNode.get("outputFormatIds");
            if (outputFormatIdsNode != null && outputFormatIdsNode.isArray()) {
                for (final String outputFormatId : configuration.getOutputFormatIds())
                    configuration.removeOutputFormat(outputFormatId);
                for (final var outputFormatIdNode : outputFormatIdsNode)
                    configuration.addOutputFormat(outputFormatIdNode.asText());
            }
            // data sources
            final var dataSourcesNode = stateNode.get("dataSources");
            if (dataSourcesNode != null && dataSourcesNode.isObject()) {
                for (final var dataSourceNode : dataSourcesNode) {
                    final var id = dataSourceNode.get("id").asText();
                    if (dataSourceNode.get("active").asBoolean(false)) {
                        configuration.addDataSource(id);
                    } else {
                        configuration.removeDataSource(id);
                    }
                    final var propertiesNode = dataSourceNode.get("properties");
                    if (propertiesNode != null) {
                        final var properties = configuration.getDataSourceProperties(id);
                        final var fields = propertiesNode.fields();
                        while (fields.hasNext()) {
                            final var field = fields.next();
                            switch (dataSourcePropertyTypes.get(id).get(field.getKey())) {
                                case STRING:
                                    properties.put(field.getKey(), field.getValue().asText());
                                    break;
                                case INTEGER:
                                    properties.put(field.getKey(), field.getValue().asInt());
                                    break;
                                case DECIMAL:
                                    properties.put(field.getKey(), field.getValue().asDouble());
                                    break;
                                case BOOLEAN:
                                    properties.put(field.getKey(), field.getValue().asBoolean());
                                    break;
                                case INTEGER_LIST:
                                    properties.put(field.getKey(), getIntArrayFromJson(field.getValue()));
                                    break;
                                case STRING_LIST:
                                    properties.put(field.getKey(), getStringArrayFromJson(field.getValue()));
                                    break;
                            }
                        }
                    }
                }
            }
            activeWorkspace.saveConfiguration(configuration);
            ctx.json(new HashMap<String, Object>());
        } catch (IOException ignored) {
            ctx.json(new HashMap<String, Object>()).status(500);
        }
    }

    private Integer[] getIntArrayFromJson(final JsonNode node) {
        if (node == null || !node.isArray())
            return null;
        final List<Integer> values = new ArrayList<>();
        for (final var element : node) {
            if (element.isInt())
                values.add(element.asInt());
            else
                values.add(Integer.parseInt(element.asText()));
        }
        return values.toArray(Integer[]::new);
    }

    private String[] getStringArrayFromJson(final JsonNode node) {
        if (node == null || !node.isArray())
            return null;
        final List<String> values = new ArrayList<>();
        for (final var element : node)
            if (element.isNumber())
                values.add(element.asText());
        return values.toArray(String[]::new);
    }
}
