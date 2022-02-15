package de.unibi.agbi.biodwh2.core.io.mixml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;

/**
 * A molecule participating in an interaction.
 * <pre>
 * &lt;complexType name="participant">
 *   &lt;complexContent>
 *     &lt;restriction base="anyType">
 *       &lt;sequence>
 *         &lt;element name="names" type="names" minOccurs="0"/>
 *         &lt;element name="xref" type="xref" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="interactorRef" type="int"/>
 *           &lt;element name="interactor" type="interactor"/>
 *           &lt;element name="interactionRef" type="int"/>
 *           &lt;element name="interactorCandidateList" type="interactorCandidateList"/>
 *         &lt;/choice>
 *         &lt;element name="participantIdentificationMethodList" type="participantIdentificationMethodList" minOccurs="0"/>
 *         &lt;element name="biologicalRole" type="cvType" minOccurs="0"/>
 *         &lt;element name="experimentalRoleList" type="experimentalRoleList" minOccurs="0"/>
 *         &lt;element name="experimentalPreparationList" type="experimentalPreparationList" minOccurs="0"/>
 *         &lt;element name="experimentalInteractorList" type="experimentalInteractorList" minOccurs="0"/>
 *         &lt;element name="featureList" type="featureList" minOccurs="0"/>
 *         &lt;element name="hostOrganismList" type="hostOrganismList" minOccurs="0"/>
 *         &lt;element name="confidenceList" type="confidenceList" minOccurs="0"/>
 *         &lt;element name="parameterList" type="parameterList" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="stoichiometry" type="stoichiometryType"/>
 *           &lt;element name="stoichiometryRange" type="stoichiometryRangeType"/>
 *         &lt;/choice>
 *         &lt;element name="attributeList" type="attributeList" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class Participant {
    public Names names;
    public Xref xref;
    public Integer interactorRef;
    public Interactor interactor;
    public Integer interactionRef;
    public InteractorCandidateList interactorCandidateList;
    @JacksonXmlElementWrapper
    public ArrayList<ParticipantIdentificationMethod> participantIdentificationMethodList;
    public CvType biologicalRole;
    @JacksonXmlElementWrapper
    public ArrayList<ExperimentalRole> experimentalRoleList;
    @JacksonXmlElementWrapper
    public ArrayList<ExperimentalPreparation> experimentalPreparationList;
    @JacksonXmlElementWrapper
    public ArrayList<ExperimentalInteractor> experimentalInteractorList;
    @JacksonXmlElementWrapper
    public ArrayList<Feature> featureList;
    @JacksonXmlElementWrapper
    public ArrayList<HostOrganism> hostOrganismList;
    @JacksonXmlElementWrapper
    public ArrayList<Confidence> confidenceList;
    @JacksonXmlElementWrapper
    public ArrayList<Parameter> parameterList;
    public StoichiometryType stoichiometry;
    public StoichiometryRangeType stoichiometryRange;
    @JacksonXmlElementWrapper
    public ArrayList<Attribute> attributeList;
    @JacksonXmlProperty(isAttribute = true)
    public int id;
}
