package de.unibi.agbi.biodwh2.gene2phenotype.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GeneDiseasePair {
    private final String geneSymbol;
    private final String geneMim;

    private final String diseaseName;
    private final String diseaseMim;

    private final DiseaseConfidence diseaseConfidence;

    private final List<String> allelicRequirement;
    private final String mutationConsequence;

    private final List<String> phenotypes;
    private final List<String> organSpecificityList;
    private final List<String> pmids;
    private final G2PPanel g2ppanel;
    private final List<String> prevSymbols;
    private final int hgncId;
    private LocalDateTime entryDate;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public GeneDiseasePair(String geneSymbol, String geneMim, String diseaseName, String diseaseMim,
                           DiseaseConfidence diseaseConfidence, List<String> allelicRequirement,
                           String mutationConsequence, List<String> phenotypes, List<String> organSpecificityList,
                           List<String> pmids, G2PPanel g2ppanel, List<String> prevSymbols, int hgncId,
                           String entryDate) {
        this.geneSymbol = geneSymbol;
        this.geneMim = geneMim;
        this.diseaseName = diseaseName;
        this.diseaseMim = diseaseMim;
        this.diseaseConfidence = diseaseConfidence;
        this.allelicRequirement = allelicRequirement;
        this.mutationConsequence = mutationConsequence;
        this.phenotypes = phenotypes;
        this.organSpecificityList = organSpecificityList;
        this.pmids = pmids;
        this.g2ppanel = g2ppanel;
        this.prevSymbols = prevSymbols;
        this.hgncId = hgncId;
        try {
            this.entryDate = LocalDateTime.parse(entryDate, formatter);
        } catch (DateTimeParseException e) {
            this.entryDate = null;
        }

    }

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public String getGeneMim() {
        return geneMim;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public String getDiseaseMim() {
        return diseaseMim;
    }

    public DiseaseConfidence getDiseaseConfidence() {
        return diseaseConfidence;
    }

    public List<String> getAllelicRequirement() {
        return allelicRequirement;
    }

    public String getMutationConsequence() {
        return mutationConsequence;
    }

    public List<String> getPhenotypes() {
        return phenotypes;
    }

    public List<String> getOrganSpecificityList() {
        return organSpecificityList;
    }

    public G2PPanel getG2Ppanel() {
        return g2ppanel;
    }

    public int getHgncId() {
        return hgncId;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public List<String> getPrevSymbols() {
        return prevSymbols;
    }

    public List<String> getPmids() {
        return pmids;
    }
}
