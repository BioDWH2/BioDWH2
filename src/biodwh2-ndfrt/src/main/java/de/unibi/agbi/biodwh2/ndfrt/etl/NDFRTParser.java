package de.unibi.agbi.biodwh2.ndfrt.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.ndfrt.NDFRTDataSource;
import de.unibi.agbi.biodwh2.ndfrt.model.Terminology;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class NDFRTParser extends Parser<NDFRTDataSource> {
    public NDFRTParser(final NDFRTDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        final String filePath = dataSource.resolveSourceFilePath(workspace, NDFRTUpdater.FILE_NAME);
        final File coreZipFile = new File(filePath);
        if (!coreZipFile.exists())
            throw new ParserFileNotFoundException(NDFRTUpdater.FILE_NAME);
        final ZipInputStream zipInputStream = openZipInputStream(coreZipFile);
        try {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (isZipEntryCoreXml(zipEntry.getName())) {
                    dataSource.terminology = parseTerminologyFromZipStream(zipInputStream);
                    return true;
                }
            }
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file '" + NDFRTUpdater.FILE_NAME + "'", e);
        }
        return false;
    }

    private static ZipInputStream openZipInputStream(final File file) throws ParserFileNotFoundException {
        try {
            final FileInputStream inputStream = new FileInputStream(file);
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            return new ZipInputStream(bufferedInputStream);
        } catch (FileNotFoundException e) {
            throw new ParserFileNotFoundException(file.getName());
        }
    }

    private static boolean isZipEntryCoreXml(final String name) {
        return name.startsWith("NDFRT_Public_") && name.endsWith(".xml");
    }

    private Terminology parseTerminologyFromZipStream(final InputStream stream) throws IOException {
        final XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(stream, Terminology.class);
    }
}
