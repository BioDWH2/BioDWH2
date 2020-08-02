package de.unibi.agbi.biodwh2.core.exceptions;

public class UpdaterMalformedVersionException extends UpdaterException {
    private static final long serialVersionUID = 5468140477376792188L;

    public UpdaterMalformedVersionException() {
    }

    public UpdaterMalformedVersionException(String version) {
        super("The version string '" + version + "' is malformed");
    }

    public UpdaterMalformedVersionException(String version, Throwable cause) {
        super("The version string '" + version + "' is malformed", cause);
    }
}
