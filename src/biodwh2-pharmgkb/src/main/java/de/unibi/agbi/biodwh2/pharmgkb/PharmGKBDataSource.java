package de.unibi.agbi.biodwh2.pharmgkb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.pharmgkb.etl.*;
import de.unibi.agbi.biodwh2.pharmgkb.model.*;

import java.util.HashMap;
import java.util.List;

public class PharmGKBDataSource extends DataSource {
    public List<Gene> genes;
    public List<Chemical> chemicals;
    public List<Drug> drugs;
    public List<Phenotype> phenotyps;
    public List<Variant> variants;
    public List<AutomatedAnnotation> automatedAnnotations;
    public List<ClinicalAnnotation> clinicalAnnotations;
    public List<ClinicalAnnotationMetadata> clinicalAnnotationMetadata;
    public List<ClinicalVariant> clinicalVariants;
    public List<DrugLabel> drugLabels;
    public List<DrugLabelsByGene> drugLabelsByGenes;
    public List<Occurrence> occurrences;
    public List<StudyParameters> studyParameters;
    public List<VariantDrugAnnotation> variantDrugAnnotations;
    public List<VariantFunctionalAnalysisAnnotation> variantFunctionalAnalysisAnnotations;
    public List<VariantPhenotypeAnnotation> variantPhenotypeAnnotations;
    public HashMap<String, List<Pathway>> pathways = new HashMap<>();

    @Override
    public String getId() {
        return "PharmGKB";
    }

    @Override
    public Updater<PharmGKBDataSource> getUpdater() {
        return new PharmGKBUpdater();
    }

    @Override
    public Parser<PharmGKBDataSource> getParser() {
        return new PharmGKBParser();
    }

    @Override
    public RDFExporter<PharmGKBDataSource> getRdfExporter() {
        return new EmptyRDFExporter<>();
    }

    @Override
    public GraphExporter<PharmGKBDataSource> getGraphExporter() {
        return new PharmGKBGraphExporter();
    }
}
