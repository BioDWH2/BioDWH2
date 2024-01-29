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
                    final String text = buffer.toString();
                    boolean usedCustomInsert = false;
                    if (text.startsWith("INSERT INTO ")) {
                        usedCustomInsert = tryHandleInsert(text, outputPath);
                        if (usedCustomInsert)
                            buffer = new StringBuilder();
                    }
                    if (!usedCustomInsert) {
                        final MySqlParser parser = tryParseChunk(text);
                        if (parser != null) {
                            buffer = new StringBuilder();
                            handleParsedChunk(parser, outputPath);
                        }
                    }
                }
            }
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }

    private static boolean tryHandleInsert(final String chunk, final Path outputPath) throws IOException {
        final int intoKeywordIndex = chunk.indexOf("INTO");
        final int valuesKeywordIndex = chunk.indexOf("VALUES");
        if (intoKeywordIndex == -1 || valuesKeywordIndex == -1)
            return false;
        final String tableName = getTableNameManual(chunk, intoKeywordIndex, valuesKeywordIndex);
        try (final OutputStream stream = Files.newOutputStream(outputPath.resolve(tableName + ".tsv"),
                                                               StandardOpenOption.APPEND);
             final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream))) {
            int currentIndex = valuesKeywordIndex + 6;
            boolean insideRow = false;
            int rowCounter = 0;
            while (currentIndex < chunk.length()) {
                if (rowCounter % 1000 == 0)
                    writer.flush();
                final char c = chunk.charAt(currentIndex);
                currentIndex++;
                if (c == '(') {
                    insideRow = true;
                } else if (c == ')') {
                    writer.write('\n');
                    insideRow = false;
                    rowCounter++;
                } else if (insideRow) {
                    if (c == '"' || c == '\'') {
                        int backslashes;
                        int endIndex = currentIndex - 1;
                        do {
                            backslashes = 0;
                            endIndex = chunk.indexOf(c, endIndex + 1);
                            for (int i = endIndex - 1; i > currentIndex; i--) {
                                if (chunk.charAt(i) == '\\')
                                    backslashes++;
                                else
                                    break;
                            }
                        } while ((backslashes % 2) != 0);
                        String value = chunk.substring(currentIndex, endIndex);
                        int replaceIndex = value.length();
                        do {
                            replaceIndex = value.lastIndexOf('"', replaceIndex - 1);
                            if (replaceIndex != -1) {
                                backslashes = 0;
                                for (int i = replaceIndex - 1; i > 0; i--) {
                                    if (value.charAt(i) == '\\')
                                        backslashes++;
                                    else
                                        break;
                                }
                                if ((backslashes % 2) == 0)
                                    value = value.substring(0, replaceIndex) + '"' + value.substring(replaceIndex);
                                else {
                                    value = value.substring(0, replaceIndex - 1) + '"' + value.substring(replaceIndex);
                                    replaceIndex -= 1;
                                }
                            }
                        } while (replaceIndex != -1);
                        value = removeEscapeForChars(value);
                        writer.write('"' + value + '"');
                        currentIndex = endIndex + 1;
                    } else if (c == ',') {
                        writer.write('\t');
                    } else {
                        if (c == 'N' && chunk.substring(currentIndex - 1, currentIndex + 3).equals("NULL")) {
                            currentIndex += 3;
                            continue;
                        }
                        writer.write(c);
                    }
                }
            }
        }
        return true;
    }

    private static String removeEscapeForChars(String value) {
        int replaceIndex = value.length();
        do {
            replaceIndex = lastIndexOfChars(value, replaceIndex - 1);
            if (replaceIndex != -1) {
                int backslashes = 0;
                for (int i = replaceIndex - 1; i >= 0; i--) {
                    if (value.charAt(i) == '\\')
                        backslashes++;
                    else
                        break;
                }
                if ((backslashes % 2) != 0) {
                    value = value.substring(0, replaceIndex - 1) + value.substring(replaceIndex);
                    replaceIndex -= 1;
                }
            }
        } while (replaceIndex != -1);
        return value;
    }

    private static int lastIndexOfChars(final String value, int fromIndex) {
        for (int i = fromIndex; i >= 0; i--) {
            final char c = value.charAt(i);
            if (c == '%' || c == '_' || c == '\'')
                return i;
        }
        return -1;
    }

    private static String getTableNameManual(final String chunk, final int intoKeywordIndex,
                                             final int valuesKeywordIndex) {
        String tableName = chunk.substring(intoKeywordIndex + 4, valuesKeywordIndex).trim();
        if (tableName.charAt(0) == '`')
            tableName = tableName.substring(1);
        if (tableName.charAt(tableName.length() - 1) != '`') {
            final int partitionKeywordIndex = tableName.indexOf("PARTITION");
            if (partitionKeywordIndex != -1)
                tableName = tableName.substring(0, partitionKeywordIndex).trim();
            else {
                final int braceIndex = tableName.indexOf("(");
                if (braceIndex != -1)
                    tableName = tableName.substring(0, braceIndex).trim();
            }
        }
        if (tableName.charAt(tableName.length() - 1) == '`')
            tableName = tableName.substring(0, tableName.length() - 1);
        return tableName;
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
                    stream = Files.newOutputStream(outputPath.resolve(tableName + ".tsv"), StandardOpenOption.APPEND);
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
            if (i > 0)
                writer.write('\t');
            if (expression.DEFAULT() != null)
                continue;
            final MySqlParser.ConstantContext constantContext = (MySqlParser.ConstantContext) expression.expression()
                                                                                                        .getChild(0)
                                                                                                        .getChild(0)
                                                                                                        .getChild(0);
            final ParseTree valueContext = constantContext.getChild(0);
            if (valueContext instanceof MySqlParser.StringLiteralContext) {
                writer.write('"' + StringUtils.strip(valueContext.getText(), "'") + '"');
            } else if (valueContext instanceof MySqlParser.BooleanLiteralContext) {
                writer.write(valueContext.getText().toLowerCase());
            } else if (valueContext instanceof MySqlParser.DecimalLiteralContext) {
                writer.write(valueContext.getText());
            } else if (valueContext instanceof MySqlParser.HexadecimalLiteralContext) {
                writer.write(valueContext.getText());
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
