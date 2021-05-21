package de.unibi.agbi.biodwh2.gene2phenotype.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class GeneDiseasePair {
    private String geneSymbol;
    private String geneMim;

    private String diseaseName;
    private String diseaseMim;

    private DiseaseConfidence diseaseConfidence;

    private List<String> allelicRequirement;
    private String mutationConsequence;

    private List<String> phenotypes;
    private List<String> organSpecificityList;
    private List<String> pmids;
    private G2PPanel g2ppanel;
    private List<String> prevSymbols;
    private int hgncId;
    private LocalDateTime entryDate;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public GeneDiseasePair() {
        new GeneDiseasePair("", "", "", "", null, new ArrayList<String>(5), "", new ArrayList<String>(5),
                            new ArrayList<String>(5), new ArrayList<String>(5), null, new ArrayList<String>(5), -1,
                            "1970-01-01 00:00:00");
    }

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

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    public String getGeneMim() {
        return geneMim;
    }

    public void setGeneMim(String geneMim) {
        this.geneMim = geneMim;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseMim() {
        return diseaseMim;
    }

    public void setDiseaseMim(String diseaseMim) {
        this.diseaseMim = diseaseMim;
    }

    public DiseaseConfidence getDiseaseConfidence() {
        return diseaseConfidence;
    }

    public void setDiseaseConfidence(DiseaseConfidence diseaseConfidence) {
        this.diseaseConfidence = diseaseConfidence;
    }

    public List<String> getAllelicRequirement() {
        return allelicRequirement;
    }

    public void setAllelicRequirement(List<String> allelicRequirement) {
        this.allelicRequirement = allelicRequirement;
    }

    public void addAllelicRequirement(String allelicRequirement) {
        this.allelicRequirement.add(allelicRequirement);
    }

    public void removeAllelicRequirement(String allelicRequirement) {
        this.allelicRequirement.remove(allelicRequirement);
    }

    public void removeAllelicRequirement(int index) {
        this.allelicRequirement.remove(index);
    }

    public String getMutationConsequence() {
        return mutationConsequence;
    }

    public void setMutationConsequence(String mutationConsequence) {
        this.mutationConsequence = mutationConsequence;
    }

    public List<String> getPhenotypes() {
        return phenotypes;
    }

    public void setPhenotypes(List<String> phenotypes) {
        this.phenotypes = phenotypes;
    }

    public void addPhenotypes(String phenotype) {
        this.phenotypes.add(phenotype);
    }

    public void removePhenotypes(String phenotype) {
        this.phenotypes.remove(phenotype);
    }

    public void removePhenotypes(int index) {
        this.phenotypes.remove(index);
    }

    public List<String> getOrganSpecificityList() {
        return organSpecificityList;
    }

    public void setOrganSpecificityList(List<String> organSpecificityList) {
        this.organSpecificityList = organSpecificityList;
    }

    public void addOrganSpecificity(String organSpecificity) {
        this.organSpecificityList.add(organSpecificity);
    }

    public void removeOrganSpecificity(String organSpecificity) {
        this.organSpecificityList.remove(organSpecificity);
    }

    public void removeOrganSpecificity(int index) {
        this.organSpecificityList.remove(index);
    }

    public List<String> getPmids() {
        return pmids;
    }

    public void setPmids(List<String> pmids) {
        this.pmids = pmids;
    }

    public void addPmid(String pmid) {
        this.pmids.add(pmid);
    }

    public void removePmid(String pmid) {
        this.pmids.remove(pmid);
    }

    public void removePmid(int index) {
        this.pmids.remove(index);
    }

    public G2PPanel getG2ppanel() {
        return g2ppanel;
    }

    public void setG2ppanel(G2PPanel g2ppanel) {
        this.g2ppanel = g2ppanel;
    }

    public List<String> getPrevSymbols() {
        return prevSymbols;
    }

    public void setPrevSymbols(List<String> prevSymbols) {
        this.prevSymbols = prevSymbols;
    }

    public void addPrevSymbol(String prevSymbol) {
        this.prevSymbols.add(prevSymbol);
    }

    public void removePrevSymbol(String prevSymbol) {
        this.prevSymbols.remove(prevSymbol);
    }

    public void removePrevSymbol(int index) {
        this.prevSymbols.remove(index);
    }

    public int getHgncId() {
        return hgncId;
    }

    public void setHgncId(int hgncId) {
        this.hgncId = hgncId;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDateTime entryDate) {
        this.entryDate = entryDate;
    }

    public void setEntryDateFromString(String entryDate) {
        this.entryDate = LocalDateTime.parse(entryDate, formatter);
    }

    public void setEntryDateFromString(String entryDate, DateTimeFormatter formatter) {
        this.entryDate = LocalDateTime.parse(entryDate, formatter);
    }
}
