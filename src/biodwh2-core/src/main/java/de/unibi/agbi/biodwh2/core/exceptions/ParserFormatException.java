package de.unibi.agbi.biodwh2.core.exceptions;

public class ParserFormatException extends ParserException {
    public ParserFormatException() {
    }

    public ParserFormatException(String message) {
        super(message);
    }

    public ParserFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserFormatException(Throwable cause) {
        super(cause);
    }

    public ParserFormatException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
