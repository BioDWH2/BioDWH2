package de.unibi.agbi.biodwh2.core.exceptions;

public class ParserFileNotFoundException extends ParserException {
    public ParserFileNotFoundException(String fileName) {
        super("The file '" + fileName + "' is missing");
    }
}
