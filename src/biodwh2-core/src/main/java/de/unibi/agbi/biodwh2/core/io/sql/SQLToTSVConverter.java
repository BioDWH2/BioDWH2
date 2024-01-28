package de.unibi.agbi.biodwh2.core.io.sql;

import de.unibi.agbi.biodwh2.core.io.FileUtils;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public final class SQLToTSVConverter {
    public static boolean process(final InputStream stream, final Path outputPath) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder buffer = new StringBuilder();
        try {
            while (reader.ready()) {
                final String line = reader.readLine();
                if (line.startsWith("--"))
                    continue;
                buffer.append(line);
                if (line.endsWith(";")) {
                    final MySqlParser parser = tryParseChunk(buffer.toString());
                    if (parser != null) {
                        buffer = new StringBuilder();
                        handleParsedChunk(parser, outputPath);
                    }
                }
            }
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }

    private static MySqlParser tryParseChunk(final String chunk) {
        final MySqlLexer lexer = new MySqlLexer(CharStreams.fromString(chunk));
        lexer.removeErrorListeners();
        final ErrorListener errorListener = new ErrorListener();
        lexer.addErrorListener(errorListener);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        if (errorListener.error)
            return null;
        final MySqlParser parser = new MySqlParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        if (errorListener.error)
            return null;
        return parser;
    }

    private static void handleParsedChunk(final MySqlParser parser, final Path outputPath) throws IOException {
        final MySqlParser.SqlStatementsContext statements = parser.sqlStatements();
        for (final MySqlParser.SqlStatementContext statement : statements.sqlStatement()) {
            final MySqlParser.DdlStatementContext ddlStatement = statement.ddlStatement();
            final MySqlParser.DmlStatementContext dmlStatement = statement.dmlStatement();
            if (ddlStatement != null)
                handleDdlStatement(ddlStatement, outputPath);
            else if (dmlStatement != null)
                handleDmlStatement(dmlStatement, outputPath);
        }
    }

    private static void handleDdlStatement(final MySqlParser.DdlStatementContext ddlStatement, final Path outputPath) {
        final MySqlParser.CreateTableContext createTable = ddlStatement.createTable();
        if (createTable == null)
            return;
        String tableName = null;
        final List<String> columnNames = new ArrayList<>();
        for (ParseTree child : createTable.children) {
            if (child instanceof MySqlParser.TableNameContext) {
                tableName = getTableName((MySqlParser.TableNameContext) child);
            } else if (child instanceof MySqlParser.CreateDefinitionsContext) {
                final MySqlParser.CreateDefinitionsContext createDefinitions = (MySqlParser.CreateDefinitionsContext) child;
                for (MySqlParser.CreateDefinitionContext createDefinition : createDefinitions.createDefinition()) {
                    if (createDefinition instanceof MySqlParser.ColumnDeclarationContext) {
                        final MySqlParser.ColumnDeclarationContext columnDeclaration = (MySqlParser.ColumnDeclarationContext) createDefinition;
                        final MySqlParser.UidContext uid = columnDeclaration.fullColumnName().uid();
                        columnNames.add(StringUtils.strip(uid.STRING_LITERAL().getSymbol().getText(), "`"));
                    }
                }
            }
        }
        if (tableName != null)
            FileUtils.writeTextToUTF8File(outputPath.resolve(tableName + ".tsv"),
                                          String.join("\t", columnNames) + "\n");
    }

    private static String getTableName(final MySqlParser.TableNameContext tableNameContext) {
        final MySqlParser.UidContext uid = tableNameContext.fullId().uid().get(
                tableNameContext.fullId().uid().size() - 1);
        return StringUtils.strip(uid.STRING_LITERAL().getSymbol().getText(), "`");
    }

    private static void handleDmlStatement(final MySqlParser.DmlStatementContext dmlStatement,
                                           final Path outputPath) throws IOException {
        final MySqlParser.InsertStatementContext insert = dmlStatement.insertStatement();
        if (insert != null) {
            OutputStream stream = null;
            BufferedWriter writer = null;
            for (final ParseTree child : insert.children) {
                if (child instanceof MySqlParser.TableNameContext) {
                    final String tableName = getTableName((MySqlParser.TableNameContext) child);
                    stream = Files.newOutputStream(outputPath.resolve(tableName + ".tsv").toFile().toPath(),
                                                   StandardOpenOption.APPEND);
                    writer = new BufferedWriter(new OutputStreamWriter(stream));
                } else if (child instanceof MySqlParser.InsertStatementValueContext) {
                    final MySqlParser.InsertStatementValueContext insertValue = (MySqlParser.InsertStatementValueContext) child;
                    for (final ParseTree insertChild : insertValue.children)
                        if (insertChild instanceof MySqlParser.ExpressionsWithDefaultsContext)
                            writeTableRow((MySqlParser.ExpressionsWithDefaultsContext) insertChild, writer);
                }
            }
            if (stream != null) {
                writer.flush();
                stream.close();
            }
        }
    }

    private static void writeTableRow(final MySqlParser.ExpressionsWithDefaultsContext expressions,
                                      final BufferedWriter writer) throws IOException {
        final List<MySqlParser.ExpressionOrDefaultContext> values = expressions.expressionOrDefault();
        for (int i = 0; i < values.size(); i++) {
            MySqlParser.ExpressionOrDefaultContext expression = values.get(i);
            if (expression.DEFAULT() != null) {
                System.out.println("DEFAULT value: " + expression.DEFAULT().getSymbol().getText());
                continue;
            }
            final MySqlParser.ConstantContext constantContext = (MySqlParser.ConstantContext) expression.expression()
                                                                                                        .getChild(0)
                                                                                                        .getChild(0)
                                                                                                        .getChild(0);
            if (i > 0)
                writer.write('\t');
            if (constantContext.stringLiteral() != null) {
                writer.write('"' + StringUtils.strip(constantContext.stringLiteral().getText(), "'") + '"');
            } else if (constantContext.booleanLiteral() != null) {
                writer.write(constantContext.booleanLiteral().getText().toLowerCase());
            } else if (constantContext.decimalLiteral() != null) {
                writer.write(constantContext.decimalLiteral().getText());
            } else if (constantContext.hexadecimalLiteral() != null) {
                writer.write(constantContext.hexadecimalLiteral().getText());
            }
        }
        writer.write('\n');
    }

    private static class ErrorListener extends BaseErrorListener {
        public boolean error = false;

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                                String msg, RecognitionException e) {
            error = true;
        }
    }
}
