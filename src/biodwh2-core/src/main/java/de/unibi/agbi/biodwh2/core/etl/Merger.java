package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.MergerException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class Merger {
    Collection<Map<String, String>> merged = new ArrayList<>();
    Model model = ModelFactory.createDefaultModel();

    public final boolean merge(Workspace workspace, List<DataSource> dataSources) throws MergerException {
        return true;
    }
}
