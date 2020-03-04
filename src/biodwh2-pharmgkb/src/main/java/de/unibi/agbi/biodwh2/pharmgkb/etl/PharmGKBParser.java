package de.unibi.agbi.biodwh2.pharmgkb.etl;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.pharmgkb.PharmGKBDataSource;
import de.unibi.agbi.biodwh2.pharmgkb.model.*;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PharmGKBParser extends Parser<PharmGKBDataSource> {
    @Override
    public boolean parse(Workspace workspace, PharmGKBDataSource dataSource) throws ParserException {
        List<String> files = dataSource.listSourceFiles(workspace);
        for (String file : files)
            try {
                parseFile(dataSource, dataSource.resolveSourceFilePath(workspace, file));
            } catch (IOException e) {
                throw new ParserFileNotFoundException("Failed to parse the file '" + file + "'");
            }
        return true;
    }

    private void parseFile(final PharmGKBDataSource dataSource, final String filePath) throws IOException {
        ZipFile zipFile = new ZipFile(filePath);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            String zipEntryName = zipEntry.getName();
            InputStream stream = zipFile.getInputStream(zipEntry);
            if (zipEntryName.contains("Pathway")) {
                List<Pathway> tmpList = parseTSV(stream, Pathway.class);
                dataSource.pathways.put(zipEntry.getName().split("\\.")[0], tmpList);
            } else if (zipEntryName.equals("chemicals.tsv")) {
                dataSource.chemicals = parseTSV(stream, Chemical.class);
            } else if (zipEntryName.equals("drugs.tsv")) {
                dataSource.drugs = parseTSV(stream, Drug.class);
            } else if (zipEntryName.equals("genes.tsv")) {
                dataSource.genes = parseTSV(stream, Gene.class);
            } else if (zipEntryName.equals("phenotypes.tsv")) {
                dataSource.phenotyps = parseTSV(stream, Phenotype.class);
            } else if (zipEntryName.equals("variants.tsv")) {
                dataSource.variants = parseTSV(stream, Variant.class);
            } else if (zipEntryName.equals("clinical_ann.tsv")) {
                dataSource.clinicalAnnotations = parseTSV(stream, ClinicalAnnotation.class);
            } else if (zipEntryName.equals("clinical_ann_metadata.tsv")) {
                dataSource.clinicalAnnotationMetadata = parseTSV(stream, ClinicalAnnotationMetadata.class);
            } else if (zipEntryName.equals("study_parameters.tsv")) {
                dataSource.studyParameters = parseTSV(stream, StudyParameters.class);
            } else if (zipEntryName.equals("var_drug_ann.tsv")) {
                dataSource.variantDrugAnnotations = parseTSV(stream, VariantDrugAnnotation.class);
            } else if (zipEntryName.equals("var_fa_ann.tsv")) {
                dataSource.variantFunctionalAnalysisAnnotations = parseTSV(stream,
                                                                           VariantFunctionalAnalysisAnnotation.class);
            } else if (zipEntryName.equals("var_pheno_ann.tsv")) {
                dataSource.variantPhenotypeAnnotations = parseTSV(stream, VariantPhenotypeAnnotation.class);
            } else if (zipEntryName.equals("automated_annotations.tsv")) {
                dataSource.automatedAnnotations = parseTSV(stream, AutomatedAnnotation.class);
            } else if (zipEntryName.equals("clinicalVariants.tsv")) {
                dataSource.clinicalVariants = parseTSV(stream, ClinicalVariants.class);
            } else if (zipEntryName.equals("drugLabels.byGene.tsv")) {
                dataSource.drugLabelsByGenes = parseTSV(stream, DrugLabelsByGene.class);
            } else if (zipEntryName.equals("drugLabels.tsv")) {
                dataSource.drugLabels = parseTSV(stream, DrugLabel.class);
            } else if (zipEntryName.equals("occurrences.tsv")) {
                dataSource.occurrences = parseTSV(stream, Occurrence.class);
            }
        }
    }

    private <T> List<T> parseTSV(final InputStream stream, final Class<T> clazz) {
        BeanListProcessor<T> processor = new BeanListProcessor<>(clazz);
        TsvParserSettings parserSettings = new TsvParserSettings();
        parserSettings.setProcessor(processor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setMaxCharsPerColumn(200000);
        TsvParser parser = new TsvParser(parserSettings);
        parser.parse(stream, "UTF-8");
        return processor.getBeans();
    }
}
