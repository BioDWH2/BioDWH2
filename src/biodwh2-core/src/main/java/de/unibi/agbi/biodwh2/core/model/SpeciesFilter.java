package de.unibi.agbi.biodwh2.core.model;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;

import java.util.*;

public class SpeciesFilter {
    private final Set<Integer> taxonIds;

    public SpeciesFilter(final Collection<Integer> taxonIds) {
        this.taxonIds = new HashSet<>(taxonIds);
    }

    public SpeciesFilter(final Integer... taxonIds) {
        this.taxonIds = new HashSet<>();
        if (taxonIds != null)
            this.taxonIds.addAll(Arrays.asList(taxonIds));
    }

    public boolean isSpeciesAllowed(final Integer taxonId) {
        return taxonIds == null || taxonIds.isEmpty() || taxonIds.contains(taxonId);
    }

    public static SpeciesFilter fromWorkspaceDataSource(final Workspace workspace, final DataSource dataSource) {
        final List<Integer> speciesFilterIds = new ArrayList<>();
        final var workspaceSpeciesFilter = workspace.getConfiguration().getGlobalProperties().speciesFilter;
        if (workspaceSpeciesFilter != null)
            Collections.addAll(speciesFilterIds, workspaceSpeciesFilter);
        final var dataSourceSpeciesFilter = dataSource.<List<Integer>>getProperty(workspace, "speciesFilter");
        if (dataSourceSpeciesFilter != null)
            speciesFilterIds.addAll(dataSourceSpeciesFilter);
        return new SpeciesFilter(speciesFilterIds);
    }
}
