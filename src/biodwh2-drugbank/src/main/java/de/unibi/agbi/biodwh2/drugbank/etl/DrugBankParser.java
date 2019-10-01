package de.unibi.agbi.biodwh2.drugbank.etl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;
import de.unibi.agbi.biodwh2.drugbank.model.Drugbank;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DrugBankParser extends Parser {
    @Override
    public boolean parse(Workspace workspace, DataSource dataSource) throws ParserException {
        String filePath = dataSource.resolveSourceFilePath(workspace, "drugbank_all_full_database.xml.zip");
        File zipFile = new File(filePath);
        if (!zipFile.exists())
            throw new ParserFileNotFoundException("drugbank_all_full_database.xml.zip");
        ZipInputStream zipInputStream = openZipInputStream(zipFile);
        try {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (isZipEntryCoreXml(zipEntry.getName())) {
                    ((DrugBankDataSource) dataSource).drugBankData = parseDrugBankFromZipStream(zipInputStream);
                    return true;
                }
            }
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file 'drugbank_all_full_database.xml.zip'", e);
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
        return name.startsWith("full") && name.endsWith(".xml");
    }

    private Drugbank parseDrugBankFromZipStream(InputStream stream) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(stream, Drugbank.class);
    }
}
