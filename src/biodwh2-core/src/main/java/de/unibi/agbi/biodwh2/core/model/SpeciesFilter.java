package de.unibi.agbi.biodwh2.core.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
}
