package de.unibi.agbi.biodwh2.core.exceptions;

public class ParserFileNotFoundException extends ParserException {
    private static final long serialVersionUID = 3487711390680530016L;

    public ParserFileNotFoundException(String fileName) {
        super("The file '" + fileName + "' is missing");
    }
}
