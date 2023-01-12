package de.unibi.agbi.biodwh2.expasy;

public class UniRuleTrigger {
    public String dbName;
    public String identifier1;
    public String identifier2;
    public String numHits;
    public String level;

    @Override
    public String toString() {
        return dbName + "; " + identifier1 + "; " + identifier2 + "; " + numHits + "; " + level;
    }
}
