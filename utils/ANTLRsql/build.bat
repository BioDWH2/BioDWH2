@echo off

java -jar antlr-4.13.1-complete.jar -package de.unibi.agbi.biodwh2.core.io.sql -no-listener -no-visitor -o ../../src/biodwh2-core/src/main/java/de/unibi/agbi/biodwh2/core/io/sql -Dlanguage=Java MySqlLexer.g4 MySqlParser.g4

del %CD%\..\..\src\biodwh2-core\src\main\java\de\unibi\agbi\biodwh2\core\io\sql\MySqlParser.tokens
del %CD%\..\..\src\biodwh2-core\src\main\java\de\unibi\agbi\biodwh2\core\io\sql\MySqlParser.interp
del %CD%\..\..\src\biodwh2-core\src\main\java\de\unibi\agbi\biodwh2\core\io\sql\MySqlLexer.tokens
del %CD%\..\..\src\biodwh2-core\src\main\java\de\unibi\agbi\biodwh2\core\io\sql\MySqlLexer.interp
