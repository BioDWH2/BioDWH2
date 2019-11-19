package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public abstract class RDFExporter {
    public final boolean export(Workspace workspace, DataSource dataSource) throws ExporterException {
        Model model = exportModel(dataSource);
        setModelPrefixes(model);
        try {
            FileOutputStream outputStream = new FileOutputStream(
                    dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.RDFTurtle));
            RDFDataMgr.write(outputStream, model, RDFFormat.TURTLE_PRETTY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected abstract Model exportModel(DataSource dataSource) throws ExporterException;

    protected void setModelPrefixes(Model model) {
        model.setNsPrefix("skos", SKOS.getURI());
        model.setNsPrefix("dcterms", DCTerms.getURI());
        model.setNsPrefix("rdfs", RDFS.getURI());
        model.setNsPrefix("rdf", RDF.getURI());
    }

    protected final Model createDefaultModel() {
        return ModelFactory.createDefaultModel();
    }
}
