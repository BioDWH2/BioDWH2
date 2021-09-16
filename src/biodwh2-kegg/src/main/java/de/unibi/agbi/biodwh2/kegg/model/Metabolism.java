package de.unibi.agbi.biodwh2.kegg.model;

public class Metabolism {
    public final String type;
    public final NameIdsPair target;

    public Metabolism(final String type, final NameIdsPair target) {
        this.type = type;
        this.target = target;
    }
}
