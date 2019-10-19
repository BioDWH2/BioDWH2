package de.unibi.agbi.biodwh2.hgnc;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.hgnc.etl.HGNCGraphExporter;
import de.unibi.agbi.biodwh2.hgnc.etl.HGNCParser;
import de.unibi.agbi.biodwh2.hgnc.etl.HGNCRDFExporter;
import de.unibi.agbi.biodwh2.hgnc.etl.HGNCUpdater;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;

import java.util.List;

public class HGNCDataSource extends DataSource {
    public List<Gene> genes;

    @Override
    public String getId() {
        return "HGNC";
    }

    @Override
    public Updater getUpdater() {
        return new HGNCUpdater();
    }

    @Override
    public Parser getParser() {
        return new HGNCParser();
    }

    @Override
    public RDFExporter getRdfExporter() {
        return new HGNCRDFExporter();
    }

    @Override
    public GraphExporter getGraphExporter() {
        return new HGNCGraphExporter();
    }
}
