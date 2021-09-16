package de.unibi.agbi.biodwh2.pharmgkb.etl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.pharmgkb.PharmGKBDataSource;
import de.unibi.agbi.biodwh2.pharmgkb.model.*;
import de.unibi.agbi.biodwh2.pharmgkb.model.guideline.GuidelineAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PharmGKBParser extends Parser<PharmGKBDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PharmGKBParser.class);

    public PharmGKBParser(final PharmGKBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        for (final String filePath : PharmGKBUpdater.FILE_NAMES)
            tryParseFile(workspace, dataSource, filePath);
        return true;
    }

    private void tryParseFile(final Workspace workspace, final PharmGKBDataSource dataSource,
                              final String filePath) throws ParserException {
        try {
            parseFile(dataSource, dataSource.resolveSourceFilePath(workspace, filePath));
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file '" + filePath + "'");
        }
    }

    private void parseFile(final PharmGKBDataSource dataSource, final String filePath) throws IOException {
        final ZipFile zipFile = new ZipFile(filePath);
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry zipEntry = entries.nextElement();
            final String zipEntryName = zipEntry.getName();
            final InputStream stream = zipFile.getInputStream(zipEntry);
            if (filePath.contains("pathways") && zipEntryName.startsWith("PA")) {
                final List<Pathway> tmpList = parseTSV(stream, Pathway.class);
                dataSource.pathways.put(zipEntry.getName().split("\\.")[0], tmpList);
            } else if (filePath.contains("dosingGuidelines.json.zip") && zipEntryName.endsWith(".json")) {
                final GuidelineAnnotation annotation = parseGuidelineAnnotation(stream, zipEntryName);
                if (annotation != null)
                    dataSource.guidelineAnnotations.add(annotation);
            } else if (zipEntryName.equals("chemicals.tsv"))
                dataSource.chemicals = parseTSV(stream, Chemical.class);
            else if (zipEntryName.equals("genes.tsv"))
                dataSource.genes = parseTSV(stream, Gene.class);
            else if (zipEntryName.equals("phenotypes.tsv"))
                dataSource.phenotyps = parseTSV(stream, Phenotype.class);
            else if (zipEntryName.equals("variants.tsv"))
                dataSource.variants = parseTSV(stream, Variant.class);
            else if (zipEntryName.equals("clinical_annotations.tsv"))
                dataSource.clinicalAnnotations = parseTSV(stream, ClinicalAnnotation.class);
            else if (zipEntryName.equals("clinical_ann_alleles.tsv"))
                dataSource.clinicalAnnotationAlleles = parseTSV(stream, ClinicalAnnotationAllele.class);
            else if (zipEntryName.equals("clinical_ann_evidence.tsv"))
                dataSource.clinicalAnnotationEvidences = parseTSV(stream, ClinicalAnnotationEvidence.class);
            else if (zipEntryName.equals("clinical_ann_history.tsv"))
                dataSource.clinicalAnnotationHistories = parseTSV(stream, ClinicalAnnotationHistory.class);
            else if (zipEntryName.equals("study_parameters.tsv"))
                dataSource.studyParameters = parseTSV(stream, StudyParameters.class);
            else if (zipEntryName.equals("var_drug_ann.tsv"))
                dataSource.variantDrugAnnotations = parseTSV(stream, VariantDrugAnnotation.class);
            else if (zipEntryName.equals("var_fa_ann.tsv"))
                dataSource.variantFunctionalAnalysisAnnotations = parseTSV(stream,
                                                                           VariantFunctionalAnalysisAnnotation.class);
            else if (zipEntryName.equals("var_pheno_ann.tsv"))
                dataSource.variantPhenotypeAnnotations = parseTSV(stream, VariantPhenotypeAnnotation.class);
            else if (zipEntryName.equals("automated_annotations.tsv"))
                dataSource.automatedAnnotations = parseTSV(stream, AutomatedAnnotation.class);
            else if (zipEntryName.equals("clinicalVariants.tsv"))
                dataSource.clinicalVariants = parseTSV(stream, ClinicalVariant.class);
            else if (zipEntryName.equals("drugLabels.byGene.tsv"))
                dataSource.drugLabelsByGenes = parseTSV(stream, DrugLabelsByGene.class);
            else if (zipEntryName.equals("drugLabels.tsv"))
                dataSource.drugLabels = parseTSV(stream, DrugLabel.class);
            else if (zipEntryName.equals("occurrences.tsv"))
                dataSource.occurrences = parseTSV(stream, Occurrence.class);
        }
    }

    private <T> List<T> parseTSV(final InputStream stream, final Class<T> clazz) {
        final BeanListProcessor<T> processor = new BeanListProcessor<>(clazz);
        final TsvParserSettings parserSettings = new TsvParserSettings();
        parserSettings.setProcessor(processor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setMaxCharsPerColumn(200000);
        final TsvParser parser = new TsvParser(parserSettings);
        parser.parse(stream, StandardCharsets.UTF_8);
        return processor.getBeans();
    }

    private GuidelineAnnotation parseGuidelineAnnotation(final InputStream stream, final String fileName) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(stream, GuidelineAnnotation.class);
        } catch (IOException e) {
            LOGGER.warn("Failed to parse guideline annotation '" + fileName + "'", e);
        }
        return null;
    }
}
