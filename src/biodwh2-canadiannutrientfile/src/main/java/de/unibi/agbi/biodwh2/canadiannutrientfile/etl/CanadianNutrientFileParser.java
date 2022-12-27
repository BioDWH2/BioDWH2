package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.canadiannutrientfile.CanadianNutrientFileDataSource;
import de.unibi.agbi.biodwh2.canadiannutrientfile.model.*;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CanadianNutrientFileParser extends Parser<CanadianNutrientFileDataSource> {
    public CanadianNutrientFileParser(final CanadianNutrientFileDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        dataSource.measures = readAllZipCsvFile(workspace, "MEASURE NAME.csv", Measure.class, ",,\r", "");
        dataSource.conversionFactors = readAllZipCsvFile(workspace, "CONVERSION FACTOR.csv", ConversionFactor.class,
                                                         null, null);
        dataSource.foodGroups = readAllZipCsvFile(workspace, "FOOD GROUP.csv", FoodGroup.class, null, null);
        dataSource.foods = readAllZipCsvFile(workspace, "FOOD NAME.csv", Food.class, null, null);
        dataSource.foodSources = readAllZipCsvFile(workspace, "FOOD SOURCE.csv", FoodSource.class, ",,\r", "");
        dataSource.nutrients = readAllZipCsvFile(workspace, "NUTRIENT NAME.csv", Nutrient.class, null, null);
        dataSource.nutrientSources = readAllZipCsvFile(workspace, "NUTRIENT SOURCE.csv", NutrientSource.class, null,
                                                       null);
        dataSource.nutrientAmounts = readAllZipCsvFile(workspace, "NUTRIENT AMOUNT.csv", NutrientAmount.class, null,
                                                       null);
        dataSource.yields = readAllZipCsvFile(workspace, "YIELD NAME.csv", Yield.class, null, null).stream().filter(
                y -> y.id != null).collect(Collectors.toList());
        dataSource.yieldAmounts = readAllZipCsvFile(workspace, "YIELD AMOUNT.csv", YieldAmount.class, ",,,,\r", "");
        dataSource.refuses = readAllZipCsvFile(workspace, "REFUSE NAME.csv", Refuse.class, null, null).stream().filter(
                y -> y.id != null).collect(Collectors.toList());
        dataSource.refuseAmounts = readAllZipCsvFile(workspace, "REFUSE AMOUNT.csv", RefuseAmount.class, null, null);
        return true;
    }

    private <T> List<T> readAllZipCsvFile(final Workspace workspace, final String fileName, final Class<T> type,
                                          final String search, final String replace) throws ParserFormatException {
        try (final MappingIterator<T> iterator = parseZipCsvFile(workspace, fileName, type, search, replace)) {
            return iterator.readAll();
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse file '" + fileName + "'", e);
        }
    }

    private <T> MappingIterator<T> parseZipCsvFile(final Workspace workspace, final String fileName,
                                                   final Class<T> type, final String search,
                                                   final String replace) throws ParserFormatException {
        try {
            final ZipInputStream zipInputStream = FileUtils.openZip(workspace, dataSource,
                                                                    CanadianNutrientFileUpdater.FILE_NAME);
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null)
                if (fileName.equals(zipEntry.getName())) {
                    final InputStream fileStream = search != null ? new ReplacerInputStream(zipInputStream, search,
                                                                                            replace) : zipInputStream;
                    return FileUtils.openSeparatedValuesFile(fileStream, StandardCharsets.ISO_8859_1, type, ',', true);
                }
        } catch (IOException e) {
            throw new ParserFormatException(
                    "Failed to parse the file '" + fileName + "' in '" + CanadianNutrientFileUpdater.FILE_NAME + "'",
                    e);
        }
        throw new ParserFormatException(
                "Failed to parse the file '" + fileName + "' missing in '" + CanadianNutrientFileUpdater.FILE_NAME +
                "'");
    }
}
