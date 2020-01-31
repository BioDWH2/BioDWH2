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
    public List<Phenotyp> phenotyps;
    public List<Variant> variants;
    public List<AutomatedAnnotation> automatedAnnotations;
    public List<ClinicalAnnotation> clinicalAnnotations;
    public List<ClinicalAnnotationMetadata> clinicalAnnotationMetadata;
    public List<ClinicalVariants> clinicalVariants;
    public List<DrugLabel> drugLabels;
    public List<DrugLabelsByGene> drugLabelsByGenes;
    public List<Occurrence> occurrences;
    public List<StudyParameters> studyParameters;
    public List<VariantDrugAnnotations> variantDrugAnnotations;
    public List<VariantFunctionalAnalysisAnnotation> variantFunctionalAnalysisAnnotations;
    public List<VariantPhenotypeAnnotation> variantPhenotypeAnnotations;
    public HashMap<String, List<Pathway>> pathways = new HashMap<String, List<Pathway>>();

    @Override
    public String getId() { return "PharmGKB"; }

    @Override
    public Updater getUpdater() {
        return new PharmGKBUpdater();
    }

    @Override
    public Parser getParser() {
        return new PharmGKBParser();
    }

    @Override
    public RDFExporter getRdfExporter() { return new PharmGKBRDFExporter(); }

    @Override
    public GraphExporter getGraphExporter() {
        return new PharmGKBGraphExporter();
    }

    @Override
    public Merger getMerger() {
        return new PharmGKBMerger();
    }
}
