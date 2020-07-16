package de.unibi.agbi.biodwh2.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.schema.GraphSchema;
import de.unibi.agbi.biodwh2.server.model.RequestBody;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.*;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BioDWH2Server {
    private static final Logger logger = LoggerFactory.getLogger(BioDWH2Server.class);

    private static GraphSchema schema;
    private static GraphQL graphQL;

    public static void main(String[] args) {
        logger.info("Load database...");
        final Graph graph = new Graph("D:\\databases\\BioDWH2_test\\sources\\merged.db", true);

        logger.info("Setup GraphQL...");
        SchemaParser schemaParser = new SchemaParser();
        File schemaFile = new File("D:\\databases\\BioDWH2_test\\sources\\mapped.graphqls");
        TypeDefinitionRegistry typeRegistry = schemaParser.parse(schemaFile);
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        RuntimeWiring wiring = buildRuntimeWiring(graph);
        GraphQLSchema schema = schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();

        logger.info("Start server...");
        Javalin app = Javalin.create(config -> {
            config.defaultContentType = "application/json";
            config.enableCorsForAllOrigins();
        }).start(8084);
        app.post("/", BioDWH2Server::handleRootPost);
    }

    private static RuntimeWiring buildRuntimeWiring(final Graph graph) {
        return RuntimeWiring.newRuntimeWiring().type("QueryType", typeWiring -> typeWiring
                .defaultDataFetcher(new GraphDataFetcher(graph))).build();
    }

    static class GraphDataFetcher implements DataFetcher {
        private final Graph graph;

        public GraphDataFetcher(final Graph graph) {
            this.graph = graph;
        }

        @Override
        public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
            Map<String, Object> arguments = dataFetchingEnvironment.getArguments();
            final String label = dataFetchingEnvironment.getMergedField().getName();
            List<Node> result = new ArrayList<>();
            graph.findNodes(label, arguments).forEach(result::add);
            return result;
        }
    }

    private static void handleRootPost(Context ctx) throws IOException {
        RequestBody body = ctx.bodyValidator(RequestBody.class).getOrNull();
        body.query = Arrays.stream(body.query.split("\n")).filter(l -> l.length() > 0 && !l.trim().startsWith("#"))
                           .collect(Collectors.joining("\n"));
        ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(body.query).build();
        ExecutionResult executionResult = graphQL.execute(executionInput);
        ObjectMapper objectMapper = new ObjectMapper();
        ctx.result(objectMapper.writeValueAsString(executionResult.toSpecification()));
    }
}
