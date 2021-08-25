package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Target {
    @JsonProperty("id")
    public String id;
    @JsonProperty("approvedName")
    public String approvedName;
    @JsonProperty("approvedSymbol")
    public String approvedSymbol;
    @JsonProperty("bioType")
    public String bioType;
    @JsonProperty("hgncId")
    public String hgncId;
    @JsonProperty("chemicalProbes")
    public ChemicalProbe chemicalProbes;
    @JsonProperty("reactome")
    public String[] reactome;
    @JsonProperty("nameSynonyms")
    public String[] nameSynonyms;
    @JsonProperty("symbolSynonyms")
    public String[] symbolSynonyms;
    @JsonProperty("genomicLocation")
    public GenomicLocation genomicLocation;
    @JsonProperty("proteinAnnotations")
    public ProteinAnnotation proteinAnnotations;
    @JsonProperty("tractability")
    public Tractability tractability;
    @JsonProperty("go")
    public GO[] go;
    @JsonProperty("hallMarks")
    public HallMark hallMarks;
    @JsonProperty("safety")
    public Safety safety;
    @JsonProperty("tep")
    public Tep tep;

    public static class ChemicalProbe {
        @JsonProperty("portalprobes")
        public PortalProbe[] portalProbes;
        @JsonProperty("probeminer")
        public ProbeMiner probeMiner;
    }

    public static class PortalProbe {
        @JsonProperty("chemicalprobe")
        public String chemicalProbe;
        @JsonProperty("gene")
        public String gene;
        @JsonProperty("note")
        public String note;
        @JsonProperty("sourcelinks")
        public SourceLink[] sourceLinks;
    }

    public static class SourceLink {
        @JsonProperty("link")
        public String link;
        @JsonProperty("source")
        public String source;
    }

    public static class ProbeMiner {
        @JsonProperty("link")
        public String link;
    }

    public static class GenomicLocation {
        @JsonProperty("chromosome")
        public String chromosome;
        @JsonProperty("start")
        public Long start;
        @JsonProperty("end")
        public Long end;
        @JsonProperty("strand")
        public Integer strand;
    }

    public static class ProteinAnnotation {
        @JsonProperty("id")
        public String id;
        @JsonProperty("accessions")
        public String[] accessions;
        @JsonProperty("functions")
        public String[] functions;
        @JsonProperty("pathways")
        public String[] pathways;
        @JsonProperty("similarities")
        public String[] similarities;
        @JsonProperty("subunits")
        public String[] subunits;
        @JsonProperty("classes")
        public ProteinAnnotationClass[] classes;
    }

    public static class ProteinAnnotationClass {
        @JsonProperty("l1")
        public Label l1;
        @JsonProperty("l2")
        public Label l2;
        @JsonProperty("l3")
        public Label l3;
        @JsonProperty("l4")
        public Label l4;
        @JsonProperty("l5")
        public Label l5;
        @JsonProperty("l6")
        public Label l6;
    }

    public static class Label {
        @JsonProperty("id")
        public Long id;
        @JsonProperty("label")
        public String label;
    }

    public static class Tractability {
        @JsonProperty("antibody")
        public Antibody antibody;
        @JsonProperty("smallmolecule")
        public SmallMolecule smallMolecule;
        @JsonProperty("other_modalities")
        public Modality otherModalities;
    }

    public static class Antibody {
        @JsonProperty("buckets")
        public Long[] buckets;
        @JsonProperty("categories")
        public AntibodyCategory categories;
        @JsonProperty("top_category")
        public String topCategory;
    }

    public static class AntibodyCategory {
        @JsonProperty("clinical_precedence")
        public Double clinicalPrecedence;
        @JsonProperty("predicted_tractable_high_confidence")
        public Double predictedTractableHighConfidence;
        @JsonProperty("predicted_tractable_med_low_confidence")
        public Double predictedTractableMedLowConfidence;
    }

    public static class SmallMolecule {
        @JsonProperty("buckets")
        public Long[] buckets;
        @JsonProperty("categories")
        public SmallMoleculeCategory categories;
        @JsonProperty("high_quality_compounds")
        public Long highQualityCompounds;
        @JsonProperty("small_molecule_genome_member")
        public Boolean smallMoleculeGenomeMember;
        @JsonProperty("top_category")
        public String topCategory;
    }

    public static class SmallMoleculeCategory {
        @JsonProperty("clinical_precedence")
        public Double clinicalPrecedence;
        @JsonProperty("discovery_precedence")
        public Double discoveryPrecedence;
        @JsonProperty("predicted_tractable")
        public Double predictedTractable;
    }

    public static class Modality {
        @JsonProperty("buckets")
        public Long[] buckets;
        @JsonProperty("categories")
        public ModalityCategory categories;
    }

    public static class ModalityCategory {
        @JsonProperty("clinical_precedence")
        public Double clinicalPrecedence;
    }

    public static class GO {
        @JsonProperty("id")
        public String id;
        @JsonProperty("value")
        public GOValue value;
    }

    public static class GOValue {
        @JsonProperty("evidence")
        public String evidence;
        @JsonProperty("project")
        public String project;
        @JsonProperty("term")
        public String term;
    }

    public static class HallMark {
        @JsonProperty("attributes")
        public HallMarkAttribute[] attributes;
        @JsonProperty("cancer_hallmarks")
        public CancerHallMark[] cancerHallMarks;
        @JsonProperty("function_summary")
        public HallMarkFunctionSummary[] functionSummary;
    }

    public static class HallMarkAttribute {
        @JsonProperty("pmid")
        public Long pmid;
        @JsonProperty("attribute_name")
        public String attributeName;
        @JsonProperty("description")
        public String description;
    }

    public static class CancerHallMark {
        @JsonProperty("pmid")
        public Long pmid;
        @JsonProperty("description")
        public String description;
        @JsonProperty("label")
        public String label;
        @JsonProperty("promote")
        public Boolean promote;
        @JsonProperty("suppress")
        public Boolean suppress;
    }

    public static class HallMarkFunctionSummary {
        @JsonProperty("pmid")
        public Long pmid;
        @JsonProperty("description")
        public String description;
    }

    public static class Safety {
        @JsonProperty("adverse_effects")
        public AdverseEffect[] adverseEffects;
        @JsonProperty("safety_risk_info")
        public SafetyRiskInfo[] safetyRiskInfo;
        @JsonProperty("experimental_toxicity")
        public ExperimentalToxicity[] experimentalToxicity;
    }

    public static class AdverseEffect {
        @JsonProperty("inhibition_effects")
        public InhibitionEffect inhibitionEffects;
        @JsonProperty("unspecified_interaction_effects")
        public String[] unspecifiedInteractionEffects;
        @JsonProperty("organs_systems_affected")
        public Term[] organsSystemsAffected;
        @JsonProperty("activation_effects")
        public ActivationEffect activationEffects;
        @JsonProperty("references")
        public SafetyReference[] references;
    }

    public static class InhibitionEffect {
        @JsonProperty("acute_dosing")
        public Term[] acuteDosing;
        @JsonProperty("chronic_dosing")
        public Term[] chronicDosing;
        @JsonProperty("developmental")
        public Term[] developmental;
        @JsonProperty("general")
        public Term[] general;
    }

    public static class Term {
        @JsonProperty("code")
        public String code;
        @JsonProperty("mapped_term")
        public String mappedTerm;
        @JsonProperty("term_in_paper")
        public String termInPaper;
    }

    public static class ActivationEffect {
        @JsonProperty("acute_dosing")
        public Term[] acuteDosing;
        @JsonProperty("chronic_dosing")
        public Term[] chronicDosing;
        @JsonProperty("general")
        public Term[] general;
    }

    public static class SafetyReference {
        @JsonProperty("pmid")
        public Long pmid;
        @JsonProperty("ref_label")
        public String refLabel;
        @JsonProperty("ref_link")
        public String refLink;
    }

    public static class SafetyRiskInfo {
        @JsonProperty("organs_systems_affected")
        public Term[] organsSystemsAffected;
        @JsonProperty("safety_liability")
        public String safetyLiability;
        @JsonProperty("references")
        public SafetyReference[] references;
    }

    public static class ExperimentalToxicity {
        @JsonProperty("data_source")
        public String dataSource;
        @JsonProperty("data_source_reference_link")
        public String dataSourceReferenceLink;
        @JsonProperty("experiment_details")
        public ExperimentDetails experimentDetails;
    }

    public static class ExperimentDetails {
        @JsonProperty("assay_description")
        public String assayDescription;
        @JsonProperty("assay_format")
        public String assayFormat;
        @JsonProperty("assay_format_type")
        public String assayFormatType;
        @JsonProperty("cell_short_name")
        public String cellShortName;
        @JsonProperty("tissue")
        public String tissue;

    }

    public static class Tep {
        @JsonProperty("uri")
        public String uri;
        @JsonProperty("name")
        public String name;
    }
}
