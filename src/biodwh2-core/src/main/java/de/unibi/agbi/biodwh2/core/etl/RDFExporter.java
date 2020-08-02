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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class RDFExporter<D extends DataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDFExporter.class);

    public final boolean export(final Workspace workspace, final D dataSource) throws ExporterException {
        final Model model = exportModel(dataSource);
        setModelPrefixes(model);
        try (final OutputStream outputStream = openRdfFile(workspace, dataSource)) {
            RDFDataMgr.write(outputStream, model, RDFFormat.TURTLE_PRETTY);
        } catch (IOException e) {
            LOGGER.error("Failed to export RDF file", e);
            return false;
        }
        return true;
    }

    protected abstract Model exportModel(D dataSource) throws ExporterException;

    protected void setModelPrefixes(Model model) {
        model.setNsPrefix("skos", SKOS.getURI());
        model.setNsPrefix("dcterms", DCTerms.getURI());
        model.setNsPrefix("rdfs", RDFS.getURI());
        model.setNsPrefix("rdf", RDF.getURI());
    }

    private OutputStream openRdfFile(final Workspace workspace, final D dataSource) throws IOException {
        return Files.newOutputStream(
                Paths.get(dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.RDF_TURTLE)));
    }

    protected final Model createDefaultModel() {
        return ModelFactory.createDefaultModel();
    }
}
