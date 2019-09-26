package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public abstract class RDFExporter {
    public boolean export(Workspace workspace, DataSource dataSource) {
        Model model = exportModel(dataSource);
        if (model != null) {
            try {
                FileOutputStream outputStream = new FileOutputStream(
                        dataSource.getIntermediateGraphFilePath(workspace));
                RDFDataMgr.write(outputStream, model, RDFFormat.TURTLE_PRETTY);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    protected abstract Model exportModel(DataSource dataSource);

    protected final Model createDefaultModel() {
        return ModelFactory.createDefaultModel();
    }
}
