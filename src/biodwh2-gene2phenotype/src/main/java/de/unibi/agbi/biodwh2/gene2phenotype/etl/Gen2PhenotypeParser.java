package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.gene2phenotype.Gen2PhenotypeDataSource;
import de.unibi.agbi.biodwh2.gene2phenotype.model.DiseaseConfidence;
import de.unibi.agbi.biodwh2.gene2phenotype.model.G2PPanel;
import de.unibi.agbi.biodwh2.gene2phenotype.model.GeneDiseasePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;

public class Gen2PhenotypeParser extends Parser<Gen2PhenotypeDataSource> {
    private static final String[] FILE_LIST = new String[]{
            "SkinG2P.csv", "EyeG2P.csv", "DDG2P.csv", "CancerG2P.csv"
    };

    private static final String CSV_PATTERN = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    private static final Logger LOGGER = LoggerFactory.getLogger(Gen2PhenotypeParser.class);

    public Gen2PhenotypeParser(final Gen2PhenotypeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(Workspace workspace) throws ParserException {
        for (String file : FILE_LIST) {
            LOGGER.debug(String.format("Parsing %s", file));
            parseFile(dataSource.resolveSourceFilePath(workspace, file), dataSource);
        }
        return true;
    }

    private void parseFile(String path, Gen2PhenotypeDataSource dataSource) throws ParserException {
        List<String> lines;

        try {
            lines = Files.readAllLines(new File(path).toPath());
        } catch (NoSuchFileException e) {
            LOGGER.error("File {} not found", path);
            throw new ParserFileNotFoundException(String.format("%s not found", path));
        } catch (IOException e) {
            LOGGER.error("Error parsing {}", path);
            throw new ParserFormatException(String.format("Error parsing %s", path));
        }

        LOGGER.debug(String.format("Read %d lines from %s", lines.size(), path));

        int j = 0;
        for (String line : lines.subList(1, lines.size())) {
            String[] splitted = line.split(CSV_PATTERN, -1);
            for (int i = 0; i < splitted.length; i++) {
                splitted[i] = splitted[i].replace("\"", "");
            }

            DiseaseConfidence dc = null;
            switch (splitted[4]) {
                case "both RD and IF":
                    dc = DiseaseConfidence.DD_IF;
                    break;
                case "confirmed":
                    dc = DiseaseConfidence.CONFIRMED;
                    break;
                case "possible":
                    dc = DiseaseConfidence.POSSIBLE;
                    break;
                case "probable":
                    dc = DiseaseConfidence.PROBABLE;
                    break;
                default:
                    LOGGER.warn(String.format("%s, %s error while parsing confidence", splitted[0], splitted[2]));
                    dc = null;
            }

            List<String> allelicRequirement = Arrays.asList(splitted[5].split(","));
            List<String> phenotypes = Arrays.asList(splitted[7].split(";"));
            List<String> organList = Arrays.asList(splitted[8].split(";"));
            List<String> pmids = Arrays.asList(splitted[9].split(";"));

            G2PPanel g2pp = null;
            switch (splitted[10]) {
                case "Cancer":
                    g2pp = G2PPanel.CANCER;
                    break;
                case "Cardiac":
                    g2pp = G2PPanel.CARDIAC;
                    break;
                case "DD":
                    g2pp = G2PPanel.DD;
                    break;
                case "Ear":
                    g2pp = G2PPanel.EAR;
                    break;
                case "Eye":
                    g2pp = G2PPanel.EYE;
                    break;
                case "Skin":
                    g2pp = G2PPanel.SKIN;
                    break;
                default:
                    LOGGER.warn(String.format("%s, %s error while parsing Panel", splitted[0], splitted[2]));
                    g2pp = null;

            }

            List<String> prevSymbols = Arrays.asList(splitted[11].split(";"));

            int hgncId = -1;
            if (splitted[12].matches("\\d+"))
                hgncId = Integer.parseInt(splitted[12]);
            else {
                LOGGER.warn(
                        String.format("%s, %s hgnc ID is not an integer (%s)", splitted[0], splitted[2], splitted[12]));
            }

            dataSource.geneDiseasePairs.add(
                    new GeneDiseasePair(splitted[0], splitted[1], splitted[2], splitted[3], dc, allelicRequirement,
                                        splitted[6], phenotypes, organList, pmids, g2pp, prevSymbols, hgncId,
                                        splitted[13]));
        }
        System.out.println();
    }
}
