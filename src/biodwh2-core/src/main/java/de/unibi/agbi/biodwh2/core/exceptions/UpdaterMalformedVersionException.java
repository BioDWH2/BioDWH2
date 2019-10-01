package de.unibi.agbi.biodwh2.core.exceptions;

public class UpdaterMalformedVersionException extends UpdaterException {
    public UpdaterMalformedVersionException() {
    }

    public UpdaterMalformedVersionException(String version) {
        super("The version string '" + version + "' is malformed");
    }

    public UpdaterMalformedVersionException(String version, Throwable cause) {
        super("The version string '" + version + "' is malformed", cause);
    }
}
