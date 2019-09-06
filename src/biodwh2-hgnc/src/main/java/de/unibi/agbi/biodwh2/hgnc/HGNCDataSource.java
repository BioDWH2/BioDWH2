package de.unibi.agbi.biodwh2.hgnc;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;
import de.unibi.agbi.biodwh2.hgnc.model.HGNCDataSourceMetadata;

import java.util.List;

public class HGNCDataSource extends DataSource<HGNCDataSourceMetadata> {
    public List<Gene> genes;
}
