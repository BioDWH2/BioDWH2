package de.unibi.agbi.biodwh2.unii.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.unii.UNIIDataSource;
import de.unibi.agbi.biodwh2.unii.model.UNIIDataEntry;
import de.unibi.agbi.biodwh2.unii.model.UNIIEntry;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UNIIParser extends Parser<UNIIDataSource> {
    public UNIIParser(UNIIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(Workspace workspace) throws ParserException {
        parseNamesFile(workspace, dataSource);
        parseDataFile(workspace, dataSource);
        return true;
    }

    private void parseNamesFile(final Workspace workspace, final UNIIDataSource dataSource) throws ParserException {
        try {
            ZipInputStream zipInputStream = FileUtils.openZip(workspace, dataSource, "UNIIs.zip");
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().startsWith("UNII_Names_") && zipEntry.getName().endsWith(".txt")) {
                    dataSource.uniiEntries = parseZipStream(zipInputStream, UNIIEntry.class);
                    break;
                }
            }
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file 'UNIIs.zip'", e);
        }
    }

    private void parseDataFile(final Workspace workspace, final UNIIDataSource dataSource) throws ParserException {
        try {
            ZipInputStream zipInputStream = FileUtils.openZip(workspace, dataSource, "UNII_Data.zip");
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().startsWith("UNII_Records_") && zipEntry.getName().endsWith(".txt")) {
                    List<UNIIDataEntry> dataEntries = parseZipStream(zipInputStream, UNIIDataEntry.class);
                    dataSource.uniiDataEntries = new HashMap<>();
                    for (UNIIDataEntry entry : dataEntries)
                        dataSource.uniiDataEntries.put(entry.unii, entry);
                    break;
                }
            }
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file 'UNII_Data.zip'", e);
        }
    }

    private <T> List<T> parseZipStream(final ZipInputStream zipInputStream, Class<T> typeClass) throws IOException {
        return FileUtils.openSeparatedValuesFile(zipInputStream, typeClass, '\t', true).readAll();
    }
}
