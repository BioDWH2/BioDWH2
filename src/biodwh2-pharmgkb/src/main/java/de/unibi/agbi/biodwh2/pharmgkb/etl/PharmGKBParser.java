package de.unibi.agbi.biodwh2.pharmgkb.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.pharmgkb.PharmGKBDataSource;
import de.unibi.agbi.biodwh2.pharmgkb.model.*;
import org.apache.commons.lang3.reflect.TypeLiteral;
import sun.security.util.AuthResources;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PharmGKBParser extends Parser {

    HashMap<String, String> filesHashMap = new HashMap<String, String>() {{
        put("chemicals.tsv", "Chemical");
        put("drugs.tsv", "Drug");
        put("genes.tsv", "Gene");
        put("phenotypes.tsv", "Phenotyp");
        put("variants.tsv", "Variant");
        put("clinical_ann.tsv", "ClinicalAnnotation");
        put("clinical_ann_metadata.tsv", "ClinicalAnnotationMetadata");
        put("study_parameters.tsv", "StudyParameters");
        put("var_drug_ann.tsv", "VariantDrugAnnotations");
        put("var_fa_ann.tsv", "VariantFunctionalAnalysisAnnotation");
        put("var_pheno_ann.tsv", "VariantPhenotypeAnnotation.java");
        put("automated_annotations.tsv", "AutomatedAnnotation.java");
        put("clinicalVariants.tsv", "ClinicalVariants.java");
        put("drugLabels.byGene.tsv", "DrugLabelByGene");
        put("drugLabels.tsv", "DrugLabel");
        put("occurrences.tsv", "Occurrence");
        put("pathways-tsv.zip", "Pathway");
    }};

    @Override
    public boolean parse(Workspace workspace, DataSource dataSource) throws ParserException {

        List<String> files = dataSource.listSourceFiles(workspace);

        for (String file : files) {
            String filePath = dataSource.resolveSourceFilePath(workspace, file);
            try {
                ZipFile zipFile = new ZipFile(filePath);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry zipEntry = entries.nextElement();
                    if (filesHashMap.containsKey(zipEntry.getName())) {

                        switch (zipEntry.getName()) {
                            case "chemicals.tsv":
                                ((PharmGKBDataSource) dataSource).chemicals = parseTSV(zipFile, zipEntry,
                                                                                       Chemical.class);
                                break;
                            case "drugs.tsv":
                                ((PharmGKBDataSource) dataSource).drugs = parseTSV(zipFile, zipEntry, Drug.class);
                                break;
                            case "genes.tsv":
                                ((PharmGKBDataSource) dataSource).genes = parseTSV(zipFile, zipEntry, Gene.class);
                                break;
                            case "phenotypes.tsv":
                                ((PharmGKBDataSource) dataSource).phenotyps = parseTSV(zipFile, zipEntry,
                                                                                       Phenotyp.class);
                                break;
                            case "variants.tsv":
                                ((PharmGKBDataSource) dataSource).variants = parseTSV(zipFile, zipEntry, Variant.class);
                                break;
                            case "clinical_ann.tsv":
                                ((PharmGKBDataSource) dataSource).clinicalAnnotations = parseTSV(zipFile, zipEntry,
                                                                                                 ClinicalAnnotation.class);
                                break;
                            case "clinical_ann_metadata.tsv":
                                ((PharmGKBDataSource) dataSource).clinicalAnnotationMetadata = parseTSV(zipFile,
                                                                                                        zipEntry,
                                                                                                        ClinicalAnnotationMetadata.class);
                                break;
                            case "study_parameters.tsv":
                                ((PharmGKBDataSource) dataSource).studyParameters = parseTSV(zipFile, zipEntry,
                                                                                             StudyParameters.class);
                                break;
                            case "var_drug_ann.tsv":
                                ((PharmGKBDataSource) dataSource).variantDrugAnnotations = parseTSV(zipFile, zipEntry,
                                                                                                    VariantDrugAnnotations.class);
                                break;
                            case "var_fa_ann.tsv":
                                ((PharmGKBDataSource) dataSource).variantFunctionalAnalysisAnnotations = parseTSV(
                                        zipFile, zipEntry, VariantFunctionalAnalysisAnnotation.class);
                                break;
                            case "var_pheno_ann.tsv":
                                ((PharmGKBDataSource) dataSource).variantPhenotypeAnnotations = parseTSV(zipFile,
                                                                                                         zipEntry,
                                                                                                         VariantPhenotypeAnnotation.class);
                                break;
                            case "automated_annotations.tsv":
                                ((PharmGKBDataSource) dataSource).automatedAnnotations = parseTSV(zipFile, zipEntry,
                                                                                                  AutomatedAnnotation.class);
                                break;
                            case "clinicalVariants.tsv":
                                ((PharmGKBDataSource) dataSource).clinicalVariants = parseTSV(zipFile, zipEntry,
                                                                                              ClinicalVariants.class);
                                break;
                            case "drugLabels.byGene.tsv":
                                ((PharmGKBDataSource) dataSource).drugLabelsByGenes = parseTSV(zipFile, zipEntry,
                                                                                               DrugLabelsByGene.class);
                                break;
                            case "drugLabels.tsv":
                                ((PharmGKBDataSource) dataSource).drugLabels = parseTSV(zipFile, zipEntry,
                                                                                        DrugLabel.class);
                                break;
                            case "occurrences.tsv":
                                ((PharmGKBDataSource) dataSource).occurrences = parseTSV(zipFile, zipEntry,
                                                                                         Occurrence.class);
                                break;
                            default:
                                System.out.println("Nothing to Parse");

                        }
                    } else if (zipEntry.getName().contains("Pathway")) {
                        List<Pathway> tmpList = new ArrayList<Pathway>();
                        tmpList = parseTSV(zipFile, zipEntry, Pathway.class);
                        ((PharmGKBDataSource) dataSource).pathways.put(zipEntry.getName().split("\\.")[0], tmpList);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new ParserFileNotFoundException("Failed to parse the file '" + file + "'");
            }
        }
        return true;
    }

    private <T> List<T> parseTSV(ZipFile zipFile, ZipEntry zipEntry,
                                 Class<T> clazz) throws IOException, ClassNotFoundException {

        BeanListProcessor<T> processor = new BeanListProcessor<T>(clazz);
        TsvParserSettings parserSettings = new TsvParserSettings();
        parserSettings.setProcessor(processor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setMaxCharsPerColumn(Integer.MAX_VALUE / 4);
        TsvParser parser = new TsvParser(parserSettings);
        InputStream stream = zipFile.getInputStream(zipEntry);
        parser.parse(stream);
        return processor.getBeans();
    }

    private void parseTSV(DataSource dataSource, InputStream inputStream, String file) throws ParserFormatException {
        ObjectReader reader = getTsvReader();
        try {
            MappingIterator<Gene> iterator = reader.readValues(inputStream);
            iterator.next();
            ((PharmGKBDataSource) dataSource).genes = iterator.readAll();
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file '" + file + "'", e);
        }
    }

    private ObjectReader getTsvReader() {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(Gene.class).withColumnSeparator('\t').withArrayElementSeparator("\",\"");
        return csvMapper.readerFor(Gene.class).with(schema);
    }
}

