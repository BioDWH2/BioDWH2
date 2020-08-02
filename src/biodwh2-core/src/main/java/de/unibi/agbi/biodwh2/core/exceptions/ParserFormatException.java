package de.unibi.agbi.biodwh2.core.exceptions;

public class ParserFormatException extends ParserException {
    private static final long serialVersionUID = 4976013817813880629L;

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
