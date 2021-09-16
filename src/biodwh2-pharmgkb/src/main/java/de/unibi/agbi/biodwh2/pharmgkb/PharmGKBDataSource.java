package de.unibi.agbi.biodwh2.pharmgkb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.pharmgkb.etl.*;
import de.unibi.agbi.biodwh2.pharmgkb.model.*;
import de.unibi.agbi.biodwh2.pharmgkb.model.guideline.GuidelineAnnotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PharmGKBDataSource extends DataSource {
    public List<Gene> genes;
    public List<Chemical> chemicals;
    public List<Phenotype> phenotyps;
    public List<Variant> variants;
    public List<AutomatedAnnotation> automatedAnnotations;
    public List<ClinicalAnnotation> clinicalAnnotations;
    public List<ClinicalAnnotationAllele> clinicalAnnotationAlleles;
    public List<ClinicalAnnotationEvidence> clinicalAnnotationEvidences;
    public List<ClinicalAnnotationHistory> clinicalAnnotationHistories;
    public List<ClinicalVariant> clinicalVariants;
    public List<DrugLabel> drugLabels;
    public List<DrugLabelsByGene> drugLabelsByGenes;
    public List<Occurrence> occurrences;
    public List<StudyParameters> studyParameters;
    public List<VariantDrugAnnotation> variantDrugAnnotations;
    public List<VariantFunctionalAnalysisAnnotation> variantFunctionalAnalysisAnnotations;
    public List<VariantPhenotypeAnnotation> variantPhenotypeAnnotations;
    public HashMap<String, List<Pathway>> pathways = new HashMap<>();
    public List<GuidelineAnnotation> guidelineAnnotations = new ArrayList<>();

    @Override
    public String getId() {
        return "PharmGKB";
    }

    @Override
    public String getFullName() {
        return "Pharmacogenomics Knowledgebase (PharmGKB)";
    }

    @Override
    public String getDescription() {
        return "PharmGKB provides information about how human genetic variation affects response to medications.";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    public Updater<PharmGKBDataSource> getUpdater() {
        return new PharmGKBUpdater(this);
    }

    @Override
    public Parser<PharmGKBDataSource> getParser() {
        return new PharmGKBParser(this);
    }

    @Override
    public GraphExporter<PharmGKBDataSource> getGraphExporter() {
        return new PharmGKBGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new PharmGKBMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        genes = null;
        chemicals = null;
        phenotyps = null;
        variants = null;
        automatedAnnotations = null;
        clinicalAnnotations = null;
        clinicalAnnotationAlleles = null;
        clinicalAnnotationEvidences = null;
        clinicalAnnotationHistories = null;
        clinicalVariants = null;
        drugLabels = null;
        drugLabelsByGenes = null;
        occurrences = null;
        studyParameters = null;
        variantDrugAnnotations = null;
        variantFunctionalAnalysisAnnotations = null;
        variantPhenotypeAnnotations = null;
        pathways = null;
        guidelineAnnotations = null;
    }
}
