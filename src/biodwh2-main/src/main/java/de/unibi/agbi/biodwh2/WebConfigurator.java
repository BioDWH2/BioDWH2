package de.unibi.agbi.biodwh2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.BaseWorkspace;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourceLoader;
import de.unibi.agbi.biodwh2.core.OntologyDataSource;
import de.unibi.agbi.biodwh2.core.net.UrlUtils;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.Context;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WebConfigurator {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void run() {
        final var app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.bundledPlugins.enableCors(cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost));
            config.router.apiBuilder(() -> {
                ApiBuilder.get("/connect", this::onConnect);
                ApiBuilder.post("/sync", this::onSync);
                ApiBuilder.post("/workspace/add", this::onWorkspaceAdd);
            });
        });
        app.start(7070);
        UrlUtils.openInBrowser("http://localhost:8089/?endpoint=http://localhost:7070");
    }

    private void onConnect(final Context ctx) {
        final var dataSources = DataSourceLoader.getInstance().getDataSources();
        var map = new HashMap<String, Object>();
        map.put("dataSources", Arrays.stream(dataSources).filter(d -> !d.getId().startsWith("Mock")).sorted(
                Comparator.comparing(a -> a.getId().toLowerCase())).map(this::getDataSourceDescription).toArray());
        ctx.json(map);
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
        return map;
    }

    private void onSync(final Context ctx) throws JsonProcessingException {
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
                final var status = new HashMap<>();
                try {
                    final var configuration = activeWorkspace.loadConfiguration();
                    status.put("skipGraphMLExport", configuration.shouldSkipGraphMLExport());
                    status.put("skipMetaGraphGeneration", configuration.shouldSkipMetaGraphGeneration());
                    status.put("globalProperties", configuration.getGlobalProperties());
                } catch (IOException ignored) {
                }
                map.put("activeWorkspace", status);
            } else {
                map.put("activeWorkspace", null);
            }
        }
        ctx.json(map);
    }

    private void onWorkspaceAdd(final Context ctx) {
        // TODO
    }
}
