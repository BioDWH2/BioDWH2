package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.model.Version;

public abstract class Updater {
    public abstract Version getNewestVersion();

    public abstract boolean update();
}
