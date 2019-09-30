package de.unibi.agbi.biodwh2.ndfrt.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.DataSource;
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

public class NDFRTParser extends Parser {
    @Override
    public boolean parse(Workspace workspace, DataSource dataSource) throws ParserException {
        String filePath = dataSource.resolveSourceFilePath(workspace, "NDFRT_Public_All.zip");
        File coreZipFile = new File(filePath);
        if (!coreZipFile.exists())
            throw new ParserFileNotFoundException("NDFRT_Public_All.zip");
        ZipInputStream zipInputStream = openZipInputStream(coreZipFile);
        try {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (isZipEntryCoreXml(zipEntry.getName())) {
                    ((NDFRTDataSource) dataSource).terminology = parseTerminologyFromZipStream(zipInputStream);
                    return true;
                }
            }
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file 'NDFRT_Public_All.zip'", e);
        }
        return false;
    }

    private static ZipInputStream openZipInputStream(File file) throws ParserFileNotFoundException {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            return new ZipInputStream(bufferedInputStream);
        } catch (FileNotFoundException e) {
            throw new ParserFileNotFoundException(file.getName());
        }
    }

    private static boolean isZipEntryCoreXml(String name) {
        return name.startsWith("NDFRT_Public_") && name.endsWith(".xml");
    }

    private Terminology parseTerminologyFromZipStream(InputStream stream) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(stream, Terminology.class);
    }
}
