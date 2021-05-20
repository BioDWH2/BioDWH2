package de.unibi.agbi.biodwh2.uniprot.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.uniprot.UniProtDataSource;
import de.unibi.agbi.biodwh2.uniprot.model.UniProt;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class UniProtParser extends Parser<UniProtDataSource> {
    public UniProtParser(final UniProtDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        final String filePath = dataSource.resolveSourceFilePath(workspace, "uniprot_sprot_human.xml.gz");
        final File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ParserFileNotFoundException("uniprot_sprot_human.xml.gz");
        try {
            final GZIPInputStream inputStream = openZipInputStream(zipFile);
            final UniProt uniProt = parseUniProtFromZipStream(inputStream);
            return true;
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file 'uniprot_sprot_human.xml.gz'", e);
        }
    }

    private static GZIPInputStream openZipInputStream(final File file) throws IOException {
        final FileInputStream inputStream = new FileInputStream(file);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        return new GZIPInputStream(bufferedInputStream);
    }

    private UniProt parseUniProtFromZipStream(final InputStream stream) throws IOException {
        final XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(stream, UniProt.class);
    }
}
