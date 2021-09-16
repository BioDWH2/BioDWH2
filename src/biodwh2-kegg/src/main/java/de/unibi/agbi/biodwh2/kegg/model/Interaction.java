package de.unibi.agbi.biodwh2.kegg.model;

public class Interaction {
    public final String type;
    public final NameIdsPair target;

    public Interaction(final String type, final NameIdsPair target) {
        this.type = type;
        this.target = target;
    }
}
