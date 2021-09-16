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
    public UNIIParser(final UNIIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        parseNamesFile(workspace, dataSource);
        parseDataFile(workspace, dataSource);
        return true;
    }

    private void parseNamesFile(final Workspace workspace, final UNIIDataSource dataSource) throws ParserException {
        try {
            final ZipInputStream zipInputStream = FileUtils.openZip(workspace, dataSource, UNIIUpdater.UNIIS_FILE_NAME);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().contains("Names") && zipEntry.getName().endsWith(".txt")) {
                    dataSource.uniiEntries = parseZipStream(zipInputStream, UNIIEntry.class);
                    break;
                }
            }
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file '" + UNIIUpdater.UNIIS_FILE_NAME + "'", e);
        }
    }

    private void parseDataFile(final Workspace workspace, final UNIIDataSource dataSource) throws ParserException {
        try {
            final ZipInputStream zipInputStream = FileUtils.openZip(workspace, dataSource,
                                                                    UNIIUpdater.UNII_DATA_FILE_NAME);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().contains("Records") && zipEntry.getName().endsWith(".txt")) {
                    final List<UNIIDataEntry> dataEntries = parseZipStream(zipInputStream, UNIIDataEntry.class);
                    dataSource.uniiDataEntries = new HashMap<>();
                    for (final UNIIDataEntry entry : dataEntries)
                        dataSource.uniiDataEntries.put(entry.unii, entry);
                    break;
                }
            }
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file '" + UNIIUpdater.UNII_DATA_FILE_NAME + "'", e);
        }
    }

    private <T> List<T> parseZipStream(final ZipInputStream zipInputStream,
                                       final Class<T> typeClass) throws IOException {
        return FileUtils.openSeparatedValuesFile(zipInputStream, typeClass, '\t', true).readAll();
    }
}
