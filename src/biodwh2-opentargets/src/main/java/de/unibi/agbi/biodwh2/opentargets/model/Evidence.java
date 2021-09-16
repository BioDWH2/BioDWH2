package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Evidence {
    @JsonProperty("id")
    public String id;
    @JsonProperty("datasourceId")
    public String datasourceId;
    @JsonProperty("targetId")
    public String targetId;
    @JsonProperty("alleleOrigins")
    public String[] alleleOrigins;
    @JsonProperty("allelicRequirements")
    public String[] allelicRequirements;
    @JsonProperty("beta")
    public Double beta;
    @JsonProperty("betaConfidenceIntervalLower")
    public Double betaConfidenceIntervalLower;
    @JsonProperty("betaConfidenceIntervalUpper")
    public Double betaConfidenceIntervalUpper;
    @JsonProperty("biologicalModelAllelicComposition")
    public String biologicalModelAllelicComposition;
    @JsonProperty("biologicalModelGeneticBackground")
    public String biologicalModelGeneticBackground;
    @JsonProperty("biologicalModelId")
    public String biologicalModelId;
    @JsonProperty("biosamplesFromSource")
    public String[] biosamplesFromSource;
    @JsonProperty("clinicalPhase")
    public Integer clinicalPhase;
    @JsonProperty("clinicalSignificances")
    public String[] clinicalSignificances;
    @JsonProperty("clinicalStatus")
    public String clinicalStatus;
    @JsonProperty("cohortDescription")
    public String cohortDescription;
    @JsonProperty("cohortId")
    public String cohortId;
    @JsonProperty("cohortPhenotypes")
    public String[] cohortPhenotypes;
    @JsonProperty("cohortShortName")
    public String cohortShortName;
    @JsonProperty("confidence")
    public String confidence;
    @JsonProperty("contrast")
    public String contrast;
    @JsonProperty("datatypeId")
    public String datatypeId;
    @JsonProperty("diseaseCellLines")
    public String[] diseaseCellLines;
    @JsonProperty("diseaseFromSource")
    public String diseaseFromSource;
    @JsonProperty("diseaseFromSourceId")
    public String diseaseFromSourceId;
    @JsonProperty("diseaseFromSourceMappedId")
    public String diseaseFromSourceMappedId;
    @JsonProperty("drugId")
    public String drugId;
    @JsonProperty("literature")
    public String[] literature;
    @JsonProperty("log2FoldChangePercentileRank")
    public Long log2FoldChangePercentileRank;
    @JsonProperty("log2FoldChangeValue")
    public Double log2FoldChangeValue;
    @JsonProperty("oddsRatio")
    public Double oddsRatio;
    @JsonProperty("oddsRatioConfidenceIntervalLower")
    public Double oddsRatioConfidenceIntervalLower;
    @JsonProperty("oddsRatioConfidenceIntervalUpper")
    public Double oddsRatioConfidenceIntervalUpper;
    @JsonProperty("pValueExponent")
    public Long pValueExponent;
    @JsonProperty("pValueMantissa")
    public Double pValueMantissa;
    @JsonProperty("pmcIds")
    public String[] pmcIds;
    @JsonProperty("projectId")
    public String projectId;
    @JsonProperty("publicationFirstAuthor")
    public String publicationFirstAuthor;
    @JsonProperty("publicationYear")
    public Integer publicationYear;
    @JsonProperty("reactionId")
    public String reactionId;
    @JsonProperty("reactionName")
    public String reactionName;
    @JsonProperty("resourceScore")
    public Double resourceScore;
    @JsonProperty("significantDriverMethods")
    public String[] significantDriverMethods;
    @JsonProperty("studyCases")
    public Integer studyCases;
    @JsonProperty("studyId")
    public String studyId;
    @JsonProperty("studyOverview")
    public String studyOverview;
    @JsonProperty("studySampleSize")
    public Integer studySampleSize;
    @JsonProperty("studyStartDate")
    public String studyStartDate;
    @JsonProperty("studyStopReason")
    public String studyStopReason;
    @JsonProperty("targetFromSource")
    public String targetFromSource;
    @JsonProperty("targetFromSourceId")
    public String targetFromSourceId;
    @JsonProperty("targetInModel")
    public String targetInModel;
    @JsonProperty("targetInModelId")
    public String targetInModelId;
    @JsonProperty("targetModulation")
    public String targetModulation;
    @JsonProperty("variantAminoacidDescriptions")
    public String[] variantAminoacidDescriptions;
    @JsonProperty("variantFunctionalConsequenceId")
    public String variantFunctionalConsequenceId;
    @JsonProperty("variantId")
    public String variantId;
    @JsonProperty("variantRsId")
    public String variantRsId;
    @JsonProperty("sourceId")
    public String sourceId;
    @JsonProperty("diseaseId")
    public String diseaseId;
    @JsonProperty("score")
    public Double score;
    @JsonProperty("diseaseModelAssociatedHumanPhenotypes")
    public IdLabelPair[] diseaseModelAssociatedHumanPhenotypes;
    @JsonProperty("diseaseModelAssociatedModelPhenotypes")
    public IdLabelPair[] diseaseModelAssociatedModelPhenotypes;
    @JsonProperty("mutatedSamples")
    public MutatedSample[] mutatedSamples;
    @JsonProperty("pathways")
    public IdNamePair[] pathways;
    @JsonProperty("textMiningSentences")
    public TextMiningSentence[] textMiningSentences;
    @JsonProperty("urls")
    public Url[] urls;

    public static class IdLabelPair {
        @JsonProperty("id")
        public String id;
        @JsonProperty("label")
        public String label;
    }

    public static class MutatedSample {
        @JsonProperty("functionalConsequenceId")
        public String functionalConsequenceId;
        @JsonProperty("numberMutatedSamples")
        public Integer numberMutatedSamples;
        @JsonProperty("numberSamplesTested")
        public Integer numberSamplesTested;
        @JsonProperty("numberSamplesWithMutationType")
        public Integer numberSamplesWithMutationType;
    }

    public static class IdNamePair {
        @JsonProperty("id")
        public String id;
        @JsonProperty("name")
        public String name;
    }

    public static class TextMiningSentence {
        @JsonProperty("section")
        public String section;
        @JsonProperty("text")
        public String text;
        @JsonProperty("dStart")
        public Long dStart;
        @JsonProperty("dEnd")
        public Long dEnd;
        @JsonProperty("tStart")
        public Long tStart;
        @JsonProperty("tEnd")
        public Long tEnd;
    }

}
