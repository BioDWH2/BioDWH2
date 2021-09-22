package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import de.unibi.agbi.biodwh2.canadiannutrientfile.CanadianNutrientFileDataSource;
import de.unibi.agbi.biodwh2.canadiannutrientfile.model.*;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CanadianNutrientFileParser extends Parser<CanadianNutrientFileDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CanadianNutrientFileParser.class);

    /**
     * pattern to extract data from csv files with use ',' as delimiter and in the data and use '"' to encapsulate data
     */
    private static final String CSV_PATTERN = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CanadianNutrientFileParser(CanadianNutrientFileDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(Workspace workspace) throws ParserException {
        boolean suc = loadYield(workspace);
        suc = suc & loadRefuse(workspace);
        suc = suc & loadMeasure(workspace);
        suc = suc & loadNutrient(workspace);
        suc = suc & loadFoodGroup(workspace);
        suc = suc & loadFoodSource(workspace);
        suc = suc & loadNutrientSource(workspace);

        suc = suc & loadYieldAmount(workspace);
        suc = suc & loadRefuseAmount(workspace);
        suc = suc & loadConversionFactor(workspace);
        suc = suc & loadNutrientAmount(workspace);

        suc = suc & loadFood(workspace);

        return suc;
    }

    private List<String> loadFile(Workspace workspace,
                                  String name) throws ParserFileNotFoundException, ParserFormatException {
        LOGGER.debug("loading " + name);
        String path = dataSource.resolveSourceFilePath(workspace, name);

        //the CSV-Files are broken, they need to be fix them before parsing them
        //some files have millions of ",," lines
        //sometimes a line is split in two
        //or they are empty or contain only a newline
        List<String> lines = new ArrayList<>(500000);
        try (BufferedReader br = Files.newBufferedReader(new File(path).toPath(), StandardCharsets.ISO_8859_1)) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.startsWith(",") || line.equals("\n") || line.equals(""))
                    continue;
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("File {} not found", path);
            throw new ParserFileNotFoundException(String.format("%s not found", path));
        } catch (IOException e) {
            LOGGER.error("Error parsing {}", path);
            throw new ParserFormatException(String.format("Error parsing %s", path));
        }

        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).startsWith("\"")) {
                String prev = lines.get(i - 1);
                String current = lines.get(i);
                lines.set(i - 1, prev + current);
                lines.remove(i);
            }
        }
        return lines;
    }


    // #######################################################
    // methods to load the different files and prase them to usable Objects

    private boolean loadYield(Workspace workspace) throws ParserFileNotFoundException, ParserFormatException {
        List<String> lines = loadFile(workspace, "YIELD NAME.csv");

        for (String line : lines) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }
            dataSource.yields.put(splitted[0], new Yield(splitted[0], splitted[1], splitted[2]));
        }
        return true;
    }

    private boolean loadRefuse(Workspace workspace) throws ParserFileNotFoundException, ParserFormatException {
        List<String> lines = loadFile(workspace, "REFUSE NAME.csv");

        for (String line : lines) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }
            dataSource.refuses.put(splitted[0], new Refuse(splitted[0], splitted[1], splitted[2]));
        }
        return true;
    }

    private boolean loadMeasure(Workspace workspace) throws ParserFileNotFoundException, ParserFormatException {
        List<String> lines = loadFile(workspace, "MEASURE NAME.csv");

        for (String line : lines) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }
            dataSource.measures.put(splitted[0], new Measure(splitted[0], splitted[1], splitted[2]));
        }
        return true;
    }

    private boolean loadNutrient(Workspace workspace) throws ParserFileNotFoundException, ParserFormatException {
        List<String> lines = loadFile(workspace, "NUTRIENT NAME.csv");

        for (String line : lines) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }
            dataSource.nutrients.put(splitted[0],
                                     new Nutrient(splitted[0], splitted[4], splitted[5], splitted[1], splitted[2],
                                                  splitted[3], splitted[6], splitted[7]));
        }
        return true;
    }

    private boolean loadFoodGroup(Workspace workspace) throws ParserFileNotFoundException, ParserFormatException {
        List<String> lines = loadFile(workspace, "FOOD GROUP.csv");

        for (String line : lines) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }
            dataSource.foodGroups.put(splitted[0], new FoodGroup(splitted[0], splitted[2], splitted[3], splitted[1]));
        }
        return true;
    }

    private boolean loadFoodSource(Workspace workspace) throws ParserFileNotFoundException, ParserFormatException {
        List<String> lines = loadFile(workspace, "FOOD SOURCE.csv");

        for (String line : lines) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }
            dataSource.foodSources.put(splitted[0], new FoodSource(splitted[0], splitted[1], splitted[2], splitted[3]));
        }
        return true;
    }

    private boolean loadNutrientSource(Workspace workspace) throws ParserFileNotFoundException, ParserFormatException {
        List<String> lines = loadFile(workspace, "NUTRIENT SOURCE.csv");

        for (String line : lines) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }
            dataSource.nutrientSources.put(splitted[0],
                                           new NutrientSource(splitted[0], splitted[1], splitted[2], splitted[3]));
        }
        return true;
    }

    private boolean loadYieldAmount(Workspace workspace) throws ParserFileNotFoundException, ParserFormatException {
        List<String> lines = loadFile(workspace, "YIELD AMOUNT.csv");

        for (String line : lines) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }
            dataSource.yieldAmounts.add(new YieldAmount(splitted[0], splitted[1], Double.parseDouble(splitted[2]),
                                                        LocalDateTime.parse(splitted[3] + " 00:00:00", FORMATTER)));
        }
        return true;
    }

    private boolean loadRefuseAmount(Workspace workspace) throws ParserFileNotFoundException, ParserFormatException {
        List<String> lines = loadFile(workspace, "REFUSE AMOUNT.csv");

        for (String line : lines) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }
            dataSource.refuseAmounts.add(new RefuseAmount(splitted[0], splitted[1], Double.parseDouble(splitted[2]),
                                                          LocalDateTime.parse(splitted[3] + " 00:00:00", FORMATTER)));
        }
        return true;
    }

    private boolean loadConversionFactor(
            Workspace workspace) throws ParserFileNotFoundException, ParserFormatException {
        List<String> lines = loadFile(workspace, "CONVERSION FACTOR.csv");

        for (String line : lines) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }
            dataSource.conversionFactors.add(
                    new ConversionFactor(splitted[0], splitted[1], Double.parseDouble(splitted[2]),
                                         LocalDateTime.parse(splitted[3] + " 00:00:00", FORMATTER)));
        }
        return true;
    }

    private boolean loadNutrientAmount(Workspace workspace) throws ParserFileNotFoundException, ParserFormatException {
        List<String> lines = loadFile(workspace, "NUTRIENT AMOUNT.csv");

        for (String line : lines) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }
            if (splitted[3].equals(""))
                splitted[3] = "-1.0";
            if (splitted[4].equals(""))
                splitted[4] = "-1";

            dataSource.nutrientAmounts.add(
                    new NutrientAmount(splitted[0], splitted[1], splitted[5], Double.parseDouble(splitted[2]),
                                       Double.parseDouble(splitted[3]), Integer.parseInt(splitted[4]),
                                       LocalDateTime.parse(splitted[6] + " 00:00:00", FORMATTER)));
        }
        return true;
    }

    private boolean loadFood(Workspace workspace) throws ParserFileNotFoundException, ParserFormatException {
        List<String> lines = loadFile(workspace, "FOOD NAME.csv");

        for (String line : lines) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }

            if (splitted[8].equals(""))
                splitted[8] = "-1";
            if (splitted[7].equals(""))
                splitted[7] = "1970-01-01";

            dataSource.foods.put(splitted[0],
                                 new Food(splitted[0], splitted[1], splitted[2], splitted[3], splitted[4], splitted[5],
                                          Integer.parseInt(splitted[8]),
                                          LocalDateTime.parse(splitted[6] + " 00:00:00", FORMATTER),
                                          LocalDateTime.parse(splitted[7] + " 00:00:00", FORMATTER), splitted[9]));
        }
        return true;
    }

}
