package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.model.Version;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Updater {
    public abstract Version getNewestVersion();

    public abstract boolean update();

    protected static Version convertDateTimeToVersion(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HHmmss");
        return Version.parse(dateTime.format(formatter));
    }
}
