package de.unibi.agbi.biodwh2.medrt.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.medrt.MEDRTDataSource;
import de.unibi.agbi.biodwh2.medrt.model.Terminology;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MEDRTParser extends Parser<MEDRTDataSource> {
    public MEDRTParser(final MEDRTDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        final String filePath = dataSource.resolveSourceFilePath(workspace, "Core_MEDRT_XML.zip");
        final File coreZipFile = new File(filePath);
        if (!coreZipFile.exists())
            throw new ParserFileNotFoundException("Core_MEDRT_XML.zip");
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
            throw new ParserFormatException("Failed to parse the file 'Core_MEDRT_XML.zip'", e);
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
        return name.startsWith("Core_MEDRT_") && name.endsWith(".xml");
    }

    private Terminology parseTerminologyFromZipStream(final InputStream stream) throws IOException {
        final XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(stream, Terminology.class);
    }
}
