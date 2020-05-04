package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.MergerException;

import java.util.List;

public abstract class Merger {
    public abstract boolean merge(final Workspace workspace, final List<DataSource> dataSources,
                                  final String outputFilePath) throws MergerException;
}
