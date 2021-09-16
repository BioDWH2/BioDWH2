package de.unibi.agbi.biodwh2.core.io.gmt;

public final class GeneSet {
    private final String name;
    private final String description;
    private final String[] genes;

    GeneSet(final String name, final String description, final String[] genes) {
        this.name = name;
        this.description = description;
        this.genes = genes;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getGenes() {
        return genes;
    }

    @Override
    public String toString() {
        return name + " [" + description + "] {" + String.join(", ", genes) + '}';
    }
}
