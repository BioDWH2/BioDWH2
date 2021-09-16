package de.unibi.agbi.biodwh2.core.exceptions;

public class ParserFormatException extends ParserException {
    private static final long serialVersionUID = 4976013817813880629L;

    public ParserFormatException() {
        super();
    }

    public ParserFormatException(final String message) {
        super(message);
    }

    public ParserFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ParserFormatException(final Throwable cause) {
        super(cause);
    }

    public ParserFormatException(final String message, final Throwable cause, final boolean enableSuppression,
                                 final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
